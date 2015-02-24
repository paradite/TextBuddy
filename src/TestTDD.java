
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestTDD {

    String displayCommand = "display";
    String sortCommand = "sort";
    String searchCommand = "search %1$s";

    @Before
    public void setUp() {
        TextBuddy.fileName = "test.txt";
        TextBuddy.processFilename("test.txt");

    }

    @After
    public void tearDown() {
        TextBuddy.DeleteFile();
    }

    @Test
    public void testSearchContent() {
        String command = String.format(searchCommand, "");
        // test searching empty string
        assertEquals("Search keyword cannot be empty",
                TextBuddy.executeCommand(command));

        command = String.format(searchCommand, "word");
        // test empty file
        assertEquals("No search result in test.txt",
                TextBuddy.executeCommand(command));


    }

    @Test
    public void testSortContent() {
        // test empty file
        assertEquals("test.txt is empty", TextBuddy.executeCommand(sortCommand));
        assertEquals("test.txt is empty", TextBuddy.executeCommand(displayCommand));

        // test 1 line of file
        String c = "add fox is cool";
        TextBuddy.executeCommand(c);
        TextBuddy.executeCommand(sortCommand);
        assertEquals("1. fox is cool", TextBuddy.executeCommand(displayCommand));

        // test 2 lines of file
        String c2 = "add fox is awesome";
        TextBuddy.executeCommand(c2);
        assertEquals("1. fox is cool\n2. fox is awesome",
                TextBuddy.executeCommand(displayCommand));

        TextBuddy.executeCommand(sortCommand);
        assertEquals("1. fox is awesome\n2. fox is cool",
                TextBuddy.executeCommand(displayCommand));

        // test empty line, should be sorted to the top
        String c3 = "add";
        TextBuddy.executeCommand(c3);
        assertEquals("1. fox is awesome\n2. fox is cool\n3. ",
                TextBuddy.executeCommand(displayCommand));

        TextBuddy.executeCommand(sortCommand);
        assertEquals("1. \n2. fox is awesome\n3. fox is cool",
                TextBuddy.executeCommand(displayCommand));
    }

    @Test
    public void testDisplayContent() {

        // test empty file
        assertEquals("test.txt is empty", TextBuddy.executeCommand(displayCommand));

        // test 1 line of file
        String c = "add fox is cool";
        TextBuddy.executeCommand(c);
        assertEquals("1. fox is cool", TextBuddy.executeCommand(displayCommand));

        // test 2 lines of file
        String c2 = "add fox is awesome";
        TextBuddy.executeCommand(c2);
        assertEquals("1. fox is cool\n2. fox is awesome",
                TextBuddy.executeCommand(displayCommand));

    }

    // Empty string
    String s1 = "";

    // Normal string
    String s = "first second third";

    // String with dash and underline, should be ignored
    String s2 = "first second-word third_word";

    // String with numbers
    String s3 = "1st secondword third 4";

    // String with trailing spaces
    String s4 = "  1st.word second.word third 4   ";

    // String with duplicated elements
    String s5 = "  1st.word 1st.word third 4 4  ";

    @Test
    public void testGetFirstWord() {
        assertEquals("", TextBuddy.getFirstWord(s1));
        assertEquals("first", TextBuddy.getFirstWord(s));
        assertEquals("first", TextBuddy.getFirstWord(s2));
        assertEquals("1st", TextBuddy.getFirstWord(s3));
        assertEquals("1st.word", TextBuddy.getFirstWord(s4));
    }

    @Test
    public void testGetParameters() {
        assertArrayEquals(new String[] { "" },
                TextBuddy.getParameters(s1));
        assertArrayEquals(new String[] { "second", "third" },
                TextBuddy.getParameters(s));
        assertArrayEquals(new String[] { "second-word", "third_word" },
                TextBuddy.getParameters(s2));
        assertArrayEquals(new String[] { "secondword", "third", "4" },
                TextBuddy.getParameters(s3));
        assertArrayEquals(new String[] { "second.word", "third", "4" },
                TextBuddy.getParameters(s4));
    }

    @Test
    public void testRemoveFirstWord() {
        assertEquals("", TextBuddy.removeFirstWord(s1));
        assertEquals("second third", TextBuddy.removeFirstWord(s));
        assertEquals("second-word third_word", TextBuddy.removeFirstWord(s2));
        assertEquals("secondword third 4", TextBuddy.removeFirstWord(s3));
        assertEquals("second.word third 4", TextBuddy.removeFirstWord(s4));
        assertEquals("1st.word third 4 4", TextBuddy.removeFirstWord(s5));
    }
}
