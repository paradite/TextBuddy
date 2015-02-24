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
 * created and is not empty.
 * 
 * In this case, we do not show the warning to the user that the file may have
 * been created by other programs. We still proceed to read and write the file,
 * assuming that the text file follows the file format we use for TextBuddy.
 * 
 * Assumption 2: The text file specified by the user may not have been created.
 * 
 * In this case, we create an empty file with the specified name for the user.
 * 
 * @author A0093910H
 *
 */
public class TextBuddy {

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
    private static final String ERROR_INVALID_LINE_NUMBER = "Invalid line number";

    private static final String WARNING_FILE_EXISTS = "Warning: The file already exists, proceed with caution.";

    private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %1$s is ready for use";
    private static final String MESSAGE_WAIT_FOR_COMMAND = "command: ";
    private static final String MESSAGE_INVALID_FILENAME = "Filename is invalid";
    private static final String MESSAGE_EMPTY_FILENAME = "Filename cannot be empty";
    private static final String MESSAGE_MULTIPLE_FILENAMES = "Please provide only one filename";
    private static final String MESSAGE_NO_FILENAME = "Please provide a filename, eg. mytextfile.txt";

    private static final String MESSAGE_WRITE_SUCCESS = "added to %1$s: \"%2$s\"";
    private static final String MESSAGE_DISPLAY_CONTENT = "%1$d. %2$s";
    private static final String MESSAGE_CLEAR_SUCCESS = "all content deleted from %1$s";
    private static final String MESSAGE_DELETE_SUCCESS = "deleted from %1$s: \"%2$s\"";
    private static final String MESSAGE_EMPTY_FILE = "%1$s is empty";

    private static Scanner scanner = new Scanner(System.in);
    private static File file;
    public static String fileName;
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
            processFilename(fileName);
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
     * Method to take user inputs and respond
     */
    private static void readCommands() {
        while (scanner.hasNextLine()) {
            displayMessageInline(MESSAGE_WAIT_FOR_COMMAND);
            String command = scanner.nextLine();
            String result = executeCommand(command);
            displayMessageNewLine(result);
        }
    }

    /**
     * Method to execute the command that the user provides
     * 
     * @param command
     * @return The result of the execution as String
     */
    public static String executeCommand(String command) {
        if (command.trim().isEmpty()) {
            return ERROR_INVALID_COMMAND;
        }
        String commandType = getCommandType(command);
        switch (commandType) {
            case COMMAND_ADD :
                return addText(command);
            case COMMAND_CLEAR :
                return clearContent(command);
            case COMMAND_DELETE :
                return deleteOperation(command);
            case COMMAND_DISPLAY :
                return displayContent(command);
            case COMMAND_EXIT :
                return exitProgram(command);
            default :
                return ERROR_INVALID_COMMAND;
        }
    }

    /**
     * Method for exiting the program
     * 
     * @param command
     * @return signal as String if the argument is not invalid
     */
    private static String exitProgram(String command) {
        // Verify that the command does not have extra parameters
        String[] parameters = getParameters(command);
        if (parameters.length != 1 || !parameters[0].isEmpty()) {
            return ERROR_INVALID_COMMAND;
        }
        System.exit(0);
        return command;
    }

    /**
     * Method for handling delete operation
     * 
     * @param command
     * @return signal for the operation as String
     */
    private static String deleteOperation(String command) {
        String[] parameters = getParameters(command);
        if (parameters.length != 1) {
            return ERROR_INVALID_COMMAND;
        }
        int lineNumberToDelete = Integer.parseInt(parameters[0]);
        return deleteLine(lineNumberToDelete);
    }

    /**
     * Method to delete the specified line of text
     * 
     * @param lineNumberToDelete
     * @return signal for the operation as String
     */
    private static String deleteLine(int lineNumberToDelete) {
        // Read the content, skipping the line, clear the content, then save the
        // new content
        try {
            String deletedLineContent = getLineAt(lineNumberToDelete);
            String newContent = readAndSkipLine(lineNumberToDelete);
            isFileRecreated();
            writeNewContent(newContent);

            return String.format(MESSAGE_DELETE_SUCCESS, file,
                    deletedLineContent);
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_READING_WRITING;
        }
    }

    /**
     * Method to write new content into the file after deleting operation
     * 
     * @param newContent
     * @throws IOException
     */
    private static void writeNewContent(String newContent) throws IOException {
        setUpWriter();
        fileWriter.write(newContent);
        writeEndOfFileIndicator();
        closeWriter();
    }

    /**
     * Method to get the content at a specific line as String
     * 
     * @param lineNumberToDelete
     * @return the content of the line as String
     * @throws IOException
     */
    private static String getLineAt(int lineNumberToDelete) throws IOException {
        setUpReader();
        int lineNumber = 1;
        String lineString = fileReader.readLine();
        while (lineString != null) {
            if (lineNumber == lineNumberToDelete) {
                closeReader();
                return lineString;
            }
            lineString = fileReader.readLine();
            lineNumber++;
        }
        closeReader();
        return ERROR_INVALID_LINE_NUMBER;
    }

    /**
     * Method to read the text file line by line, skipping the line to be
     * deleted and save the content as a String
     * 
     * @param lineNumberToDelete
     * @param newContentBuilder
     * @return the content of the text file without the specified line as String
     * @throws IOException
     */
    private static String readAndSkipLine(int lineNumberToDelete)
            throws IOException {
        StringBuilder newContentBuilder = new StringBuilder();
        setUpReader();
        int lineNumber = 1;
        String lineString = fileReader.readLine();
        while (lineString != null) {
            // Skip the line to be deleted
            if (lineNumber != lineNumberToDelete) {
                // Add new line before all lines except first one
                if (lineNumber > 1) {
                    newContentBuilder.append(System
                            .getProperty("line.separator"));
                }
                newContentBuilder.append(lineString);
            }
            lineString = fileReader.readLine();
            lineNumber++;
        }
        closeReader();
        return newContentBuilder.toString();
    }

    /**
     * Method to clear the content of the file by recreating it
     * 
     * @param command
     * @return Signal for the result of clearing operation as String
     */
    private static String clearContent(String command) {
        // Verify that the command does not have extra parameters
        String[] parameters = getParameters(command);
        if (parameters.length != 1 || !parameters[0].isEmpty()) {
            return ERROR_INVALID_COMMAND;
        }
        // Delete and recreate the file to clear the content
        boolean success = isFileRecreated();
        if (success) {
            return String.format(MESSAGE_CLEAR_SUCCESS, fileName);
        } else {
            return ERROR_READING_WRITING;
        }

    }

    /**
     * Method to recreate the file
     * 
     * @return boolean signal for success or failure
     */
    private static boolean isFileRecreated() {
        try {
            file.delete();
            file.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Method to display the content of the text file
     * 
     * @param command
     * @return the formatted content as String if argument is valid, otherwise
     *         return the error signal as String
     */
    public static String displayContent(String command) {
        String[] parameters = getParameters(command);
        // Make sure there are no extra parameters for display
        if (parameters.length != 1 || !parameters[0].isEmpty()) {
            return ERROR_INVALID_COMMAND;
        }
        return formatContent();
    }

    /**
     * Method to get the formatted content of the file
     * 
     * @return the formatted content as String if the file is not empty,
     *         otherwise, return the signal for empty file as String
     */
    private static String formatContent() {
        String contentToDisplay;
        String lineContent;
        try {
            setUpReader();
            lineContent = fileReader.readLine();
            // Handle the empty content
            if (lineContent == null) {
                return String.format(MESSAGE_EMPTY_FILE, fileName);
            }
            contentToDisplay = processContent(lineContent);
            closeReader();
            return contentToDisplay;
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_READING_WRITING;
        }
    }

    /**
     * Method to read the file line by line and format the content as required
     * 
     * @param lineContent
     * @return formatted content as String
     * @throws IOException
     */
    private static String processContent(String lineContent) throws IOException {
        StringBuilder contentToDisplayBuilder = new StringBuilder();
        int lineNumber = 1;
        while (lineContent != null) {
            // Format each line according to the format given with line number
            lineContent = String.format(MESSAGE_DISPLAY_CONTENT, lineNumber,
                    lineContent);
            // Add new line before all lines except first one
            if (lineNumber > 1) {
                contentToDisplayBuilder.append(System.lineSeparator());
            }
            contentToDisplayBuilder.append(lineContent);
            lineNumber++;
            lineContent = fileReader.readLine();
        }
        return contentToDisplayBuilder.toString();
    }

    /**
     * Method to add a line of text to the file
     * 
     * @param command
     * @return Signal for the adding operation as String
     */
    private static String addText(String command) {
        String textToAdd = removeFirstWord(command).trim();
        try {
            setUpWriter();
            fileWriter.write(textToAdd);
            writeEndOfFileIndicator();
            closeWriter();
            return String.format(MESSAGE_WRITE_SUCCESS, fileName, textToAdd);
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_READING_WRITING;
        }
    }

    /**
     * Method to write the end of file indicator for the text file
     * 
     * @throws IOException
     */
    private static void writeEndOfFileIndicator() throws IOException {
        // Use an new line marker as end of file indicator
        fileWriter.newLine();
    }

    /**
     * Method to get the type of the command
     * 
     * @param command
     * @return type of command as String
     */
    private static String getCommandType(String command) {
        return getFirstWord(command);
    }

    /**
     * Method to remove the first word of a String, without affecting the rest
     * of the String
     * 
     * @param s
     * @return the String with first word removed
     */
    public static String removeFirstWord(String s) {
        return s.replaceFirst(getFirstWord(s), "").trim();
    }

    /**
     * Method to get the first word of a String
     * 
     * @param s
     * @return the first word of the String
     */
    public static String getFirstWord(String s) {
        String firstWord = s.trim().split("\\s+")[0];
        return firstWord;
    }

    /**
     * Method to get the parameters in a command
     * 
     * @param command
     * @return parameters as String array
     */
    public static String[] getParameters(String command) {
        String parameterString = removeFirstWord(command.trim());
        String[] parameters = parameterString.split("\\s+");
        return parameters;
    }

    /**
     * Method to process the filename provided by user. Create one if it does
     * not already exist.
     * 
     * @param filename
     * @return File object to interact with
     */
    public static File processFilename(String filename) {
        file = new File(filename);
        if (file.exists() && !file.isDirectory()) {
            // Do not warn the user if the file is not empty
            try {
                setUpReader();
                String lineContent = fileReader.readLine();
                // if (lineContent != null) {
                // displayMessageNewLine(WARNING_FILE_EXISTS);
                // }
                closeReader();
            } catch (IOException e) {
                displayMessageNewLine(ERROR_READING_WRITING);
                e.printStackTrace();
                return null;
            }

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
     * Method to check if the initial argument supplied by the user is valid
     * 
     * @param args
     * @return valid or invalid as boolean
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
     * Method to display a message with a new line to the user
     * 
     * @param message
     */
    private static void displayMessageNewLine(String message) {
        System.out.println(message);
    }

    /**
     * Method to display an inline message without new line to the user
     * 
     * @param message
     */
    private static void displayMessageInline(String message) {
        System.out.print(message);
    }

    /**
     * Method to set up the BufferedReader for reading the text file
     */
    private static void setUpReader() {
        try {
            fileReader = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            displayMessageNewLine(ERROR_READING_WRITING);
            e.printStackTrace();
        }
    }

    /**
     * Method to close BufferedReader
     */
    private static void closeReader() {
        try {
            fileReader.close();
        } catch (IOException e) {
            displayMessageNewLine(ERROR_READING_WRITING);
            e.printStackTrace();
        }
    }

    /**
     * Method to set up the BufferedWriter for writing into the text file
     */
    private static void setUpWriter() {
        try {
            fileWriter = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            displayMessageNewLine(ERROR_READING_WRITING);
            e.printStackTrace();
        }
    }

    /**
     * Method to close BufferedWriter
     */
    private static void closeWriter() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            displayMessageNewLine(ERROR_READING_WRITING);
            e.printStackTrace();
        }
    }

    public static void DeleteFile() {
        file.delete();
    }
}
