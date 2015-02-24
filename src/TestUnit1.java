
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestUnit1 {
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
