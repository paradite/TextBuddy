import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main Class for CS2103T CE1: TextBuddy
 * 
 * Assumption 1: The text file specified by the user could have already been
 * created.
 * 
 * In this case, we show an warning to the user that the file may have been
 * created by other programs and the user should take caution when editing them.
 * We still proceed to read and write the file, assuming that the text file
 * follows the file format we use for TextBuddy.
 * 
 * Assumption 2: The text file specified by the user may not have been created.
 * 
 * In this case, we create an empty file with the specified name for the user.
 * 
 * @author paradite
 *
 */
public class TextBuddy {
	private static Scanner scanner = new Scanner(System.in);

	/**
	 * String constants
	 */
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_ADD = "add";

	private static final String ERROR_CREATING_FILE = "Error in creating the file.";
	private static final String ERROR_READING_WRITING = "Error in reading or writing the file.";
	private static final String ERROR_INVALID_COMMAND = "Invalid command";

	private static final String WARNING_FILE_EXISTS = "Warning: The file already exists and it not be created by TextBuddy. Please edit with caution.";

	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %1$s is ready for use";
	private static final String MESSAGE_WAIT_FOR_COMMAND = "command: ";
	private static final String MESSAGE_INVALID_FILENAME = "Filename is invalid";
	private static final String MESSAGE_EMPTY_FILENAME = "Filename cannot be empty";
	private static final String MESSAGE_MULTIPLE_FILENAMES = "Please provide only one filename";
	private static final String MESSAGE_NO_FILENAME = "Please provide a filename, eg. mytextfile.txt";

	private static final String MESSAGE_WRITE_SUCCESS = "added to %1$s: \"%2$s\"";
	private static final String MESSAGE_DISPLAY_CONTENT = "%1$d. %2$s";
	private static final String MESSAGE_CLEAR_SUCCESS = "all content deleted from %1$s";


	private static File file;
	private static String fileName;
	private static BufferedReader fileReader;
	private static BufferedWriter fileWriter;

	/**
	 * Main method
	 * 
	 * @param args
	 */

	public static void main(String[] args) {
		if (isValidArgument(args)) {
			fileName = args[0];
			if (fileName.isEmpty()) {
				displayMessageNewLine(MESSAGE_INVALID_FILENAME);
				System.exit(-1);
			}
			file = processFilename(fileName);
			if (file == null) {
				displayMessageNewLine(MESSAGE_INVALID_FILENAME);
			} else {
				displayMessageNewLine(String.format(MESSAGE_WELCOME, fileName));
				readCommands();
			}
		} else {
			System.exit(-1);
		}
	}

	/**
	 * Main method to take user inputs and respond
	 */
	private static void readCommands() {
		while (true) {
			displayMessageInline(MESSAGE_WAIT_FOR_COMMAND);
			String command = scanner.nextLine();
			String result = executeCommand(command);
			displayMessageNewLine(result);
		}
	}

	private static String executeCommand(String command) {
		if (command.trim().isEmpty()) {
			return ERROR_INVALID_COMMAND;
		}
		String commandType = getCommandType(command);
		switch (commandType) {
		case COMMAND_ADD:
			return addText(command);
		case COMMAND_CLEAR:
			return clearContent(command);
		case COMMAND_DELETE:
			return deleteLine(command);
		case COMMAND_DISPLAY:
			return displayContent(command);
		case COMMAND_EXIT:
			exitProgram();
		default:
			return ERROR_INVALID_COMMAND;
		}
	}

	private static void exitProgram() {
		System.exit(0);
	}

	private static String deleteLine(String command) {
		String[] parameters = getParameters(command);
		if (parameters.length != 1) {
			return ERROR_INVALID_COMMAND;
		}
		return null;
	}

	private static String clearContent(String command) {
		// Delete and recreate the file to clear the content
		try {
			file.delete();
			file.createNewFile();
			return MESSAGE_CLEAR_SUCCESS;
		} catch (IOException e) {
			e.printStackTrace();
			return ERROR_READING_WRITING;
		}
	}

	private static String displayContent(String command) {
		String[] parameters = getParameters(command);
		// Make sure there are no extra parameters for display
		if (parameters.length != 1 || !parameters[0].isEmpty()) {
			return ERROR_INVALID_COMMAND;
		}
		return formatContent();
	}

	private static String formatContent() {
		String contentToDisplay;
		String lineContent;
		try {
			setUpReader();
			lineContent = fileReader.readLine();
			contentToDisplay = processContent(lineContent);
			closeReader();
			return contentToDisplay;
		} catch (IOException e) {
			e.printStackTrace();
			return ERROR_READING_WRITING;
		}
	}

	private static String processContent(String lineContent) throws IOException {
		StringBuilder contentToDisplayBuilder = new StringBuilder();
		int lineNumber = 1;
		while (lineContent != null) {
			//Format each line according to the format given with line number
			lineContent = String.format(MESSAGE_DISPLAY_CONTENT, lineNumber, lineContent);
			// Add new line before all lines except first one
			if (lineNumber > 1) {
				contentToDisplayBuilder.append(System.getProperty("line.separator"));
			}
			contentToDisplayBuilder.append(lineContent);
			lineNumber++;
			lineContent = fileReader.readLine();
		}
		return contentToDisplayBuilder.toString();
	}

	private static String addText(String command) {
		String textToAdd = removeFirstWord(command).trim();
		try {
			setUpWriter();
			fileWriter.write(textToAdd);
			// Use an new line marker as end of file indicator
			fileWriter.newLine();
			closeWriter();
			return String.format(MESSAGE_WRITE_SUCCESS, fileName, textToAdd);
		} catch (IOException e) {
			e.printStackTrace();
			return ERROR_READING_WRITING;
		}
	}

	private static String getCommandType(String command) {
		return getFirstWord(command);
	}

	private static String removeFirstWord(String s) {
		return s.replace(getFirstWord(s), "").trim();
	}

	private static String getFirstWord(String s) {
		String firstWord = s.trim().split("\\s+")[0];
		return firstWord;
	}

	private static String[] getParameters(String command) {
		String parameterString = removeFirstWord(command.trim());
		String[] parameters = parameterString.split("\\s+");
		return parameters;
	}

	/**
	 * Process the filename provided by user
	 * 
	 * @param filename
	 * @return File object to interact with
	 */
	private static File processFilename(String filename) {
		File file = new File(filename);
		if (file.exists() && !file.isDirectory()) {
			displayMessageNewLine(WARNING_FILE_EXISTS);
			return file;
		} else if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				displayMessageNewLine(ERROR_CREATING_FILE);
				e.printStackTrace();
			}
			return file;
		}
		return null;
	}

	/**
	 * Check if the argument supplied by the user is valid
	 * 
	 * @param args
	 * @return
	 */
	private static boolean isValidArgument(String[] args) {
		if (args.length == 0) {
			displayMessageNewLine(MESSAGE_EMPTY_FILENAME);
			return false;
		} else if (args.length > 1) {
			displayMessageNewLine(MESSAGE_MULTIPLE_FILENAMES);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Display a message with a new line to the user
	 * 
	 * @param message
	 */
	private static void displayMessageNewLine(String message) {
		System.out.println(message);

	}

	/**
	 * Display an inline message without new line to the user
	 * 
	 * @param message
	 */
	private static void displayMessageInline(String message) {
		System.out.print(message);

	}

	private static void setUpReader() {
		try {
			fileReader = new BufferedReader(new FileReader(file));
		} catch (IOException e) {
			displayMessageNewLine(ERROR_READING_WRITING);
			e.printStackTrace();
		}
	}

	private static void closeReader() {
		try {
			fileReader.close();
		} catch (IOException e) {
			displayMessageNewLine(ERROR_READING_WRITING);
			e.printStackTrace();
		}
	}

	private static void setUpWriter() {
		try {
			fileWriter = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e) {
			displayMessageNewLine(ERROR_READING_WRITING);
			e.printStackTrace();
		}
	}

	private static void closeWriter() {
		try {
			fileWriter.close();
		} catch (IOException e) {
			displayMessageNewLine(ERROR_READING_WRITING);
			e.printStackTrace();
		}
	}
}
