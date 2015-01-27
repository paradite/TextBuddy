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
	private static final String MESSAGE_INVALID_FILENAME = "Filename is invalid";
	private static final String MESSAGE_EMPTY_FILENAME = "Filename cannot be empty";
	private static final String MESSAGE_MULTIPLE_FILENAMES = "Please provide only one filename";
	private static final String MESSAGE_NO_FILENAME = "Please provide a filename, eg. mytextfile.txt";

	/**
	 * Main method
	 * 
	 * @param args
	 */

	public static void main(String[] args) {
		if (isValidArgument(args)) {

		}
	}

	/**
	 * Check if the argument supplied by the user is valid
	 * 
	 * @param args
	 * @return
	 */
	private static boolean isValidArgument(String[] args) {
		if (args.length == 0) {
			displayMessage(MESSAGE_EMPTY_FILENAME);
			return false;
		} else if (args.length > 1) {
			displayMessage(MESSAGE_MULTIPLE_FILENAMES);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Display a message to the user
	 * 
	 * @param message
	 */
	private static void displayMessage(String message) {
		System.out.println(message);

	}
}
