package karaoke;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import karaoke.sound.Instrument;
import karaoke.sound.Pitch;

public class MusicTest {
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Testing Strategy for Music
    //
    // toString():
    //      music includes rest, note, lyrics, concat, together
    //      music is just single music object, combination of music objects 
    // duration():
    //      returns     0, 1, > 1
    // play():
    //      tested in separate file with manual test cases 
    // equals():
    //      music includes rest, note, lyrics, concat, together
    //      tests for structural equality
    //      test for equal and not equal
    // hashCode();
    //      music includes rest, note, lyrics, concat, together
    //
    // Cover each part at least once 
    
    // Covers the following:
    //
    // toString():
    //      music includes rest, note, lyrics
    //      music is just single music object
    // duration():
    //      returns     0, 1
    @Test
    public void testMusicSingleton() {
        // Rest of length 0
        Music singleRest = Music.rest(0);
        String singleRestString = "(0.0)";
        assertEquals("Expected correct String", singleRestString, singleRest.toString());
        assertEquals("Expected correct duration", 0, singleRest.duration(), 0);
        
        // Note of length 1 
        Music singleNote = Music.note(1, Pitch.MIDDLE_C, Instrument.PIANO);
        String singleNoteString = "(1.0, " + Pitch.MIDDLE_C.toString() + ")";
        assertEquals("Expected correct String", singleNoteString, singleNote.toString());
        assertEquals("Expected correct duration", 1, singleNote.duration(), 0);
        
        // Lyrics, by convention lyrics have a duration of 0  
        Music singleLyrics = Music.lyrics("*A*-maz-ing grace", "voice1");
        String singleLyricsString = "(voice1: *A*-maz-ing grace)";
        assertEquals("Expected correct String", singleLyricsString, singleLyrics.toString());
        assertEquals("Expected correct duration", 0, singleLyrics.duration(), 0);
    }
    
    // Covers the following:
    //
    // toString():
    //      music includes rest, note, concat, together 
    //      music is combination of music objects 
    // duration():
    //      returns     1, > 1
    @Test
    public void testMusicCombinationsNoLyrics() {
        // Test concat, symbol is &&, duration is sum 
        Music singleConcat = Music.rest(2);
        singleConcat = Music.concat(singleConcat, Music.note(1, Pitch.MIDDLE_C, Instrument.PIANO));
        String singleConcatString = "((2.0) && (1.0, " + Pitch.MIDDLE_C.toString() + "))";
        assertEquals("Expected correct String", singleConcatString, singleConcat.toString());
        assertEquals("Expected correct duration", 3, singleConcat.duration(), 0);
        
        // Test together, symbol is ||, duration is first parameter
        Pitch firstPitch = new Pitch('A');
        Pitch secondPitch = new Pitch('B');
        Music singleTogether = Music.note(1, firstPitch , Instrument.PIANO);
        singleTogether = Music.together(singleTogether, Music.note(3, secondPitch, Instrument.PIANO));
        String singleTogetherString = "((1.0, " + firstPitch.toString() + ") || (3.0, " + secondPitch.toString() + "))";
        assertEquals("Expected correct String", singleTogetherString, singleTogether.toString());
        assertEquals("Expected correct duration", 1, singleTogether.duration(), 0);
        
        // Test together and concat 
        Music togetherAndConcat = Music.concat(singleConcat, singleTogether);
        String togetherAndConcatString = "(" + singleConcatString + " && " + singleTogetherString + ")";
        assertEquals("Expected correct String", togetherAndConcatString, togetherAndConcat.toString());
        assertEquals("Expected correct duration", 4, togetherAndConcat.duration(), 0);
    }

    // Covers the following:
    //
    // toString():
    //      music includes rest, note, lyrics, concat, together 
    //      music is combination of music objects 
    // duration():
    //      returns     > 1
    @Test
    public void testMusicCombinationsWithLyrics() {
        // Concat two notes that are joined together with lyrics 
        Pitch firstPitch = new Pitch('A');
        Pitch secondPitch = new Pitch('B');
        Music firstSyllable = Music.note(1, firstPitch, Instrument.PIANO);
        firstSyllable = Music.together(firstSyllable, Music.lyrics("*test*-ing", "voice1"));
        String firstSyllableString = "((1.0, " + firstPitch.toString() + ") || (voice1: *test*-ing))";
        assertEquals("Expected correct String", firstSyllableString, firstSyllable.toString());
        assertEquals("Expected correct duration", 1, firstSyllable.duration(), 0);
        
        Music secondSyllable = Music.note(2, secondPitch, Instrument.PIANO);
        secondSyllable = Music.together(secondSyllable, Music.lyrics("test-*ing*", "voice1"));
        String secondSyllableString = "((2.0, " + secondPitch.toString() + ") || (voice1: test-*ing*))"; 
        assertEquals("Expected correct String", secondSyllableString, secondSyllable.toString());
        assertEquals("Expected correct duration", 2, secondSyllable.duration(), 0);
        
        Music concatSyllables = Music.concat(firstSyllable, secondSyllable);
        String concatSyllablesString = "(" + firstSyllableString + " && " + secondSyllableString + ")";
        assertEquals("Expected correct String", concatSyllablesString, concatSyllables.toString());
        assertEquals("Expected correct duration", 3, concatSyllables.duration(), 0);
        
        // Together two notes with lyrics, each with a different voice
        Music firstVoice = Music.note(4, firstPitch, Instrument.PIANO);
        firstVoice = Music.together(firstVoice, Music.lyrics("*test*-ing", "voice1"));
        String firstVoiceString = "((4.0, " + firstPitch.toString() + ") || (voice1: *test*-ing))";
        assertEquals("Expected correct String", firstVoiceString, firstVoice.toString());
        assertEquals("Expected correct duration", 4, firstVoice.duration(), 0);
        
        Music secondVoice = Music.note(1.4, secondPitch, Instrument.PIANO);
        secondVoice = Music.together(secondVoice, Music.lyrics("*A*-maz-ing", "voice2"));
        String secondVoiceString = "((1.4, " + secondPitch.toString() + ") || (voice2: *A*-maz-ing))";
        assertEquals("Expected correct String", secondVoiceString, secondVoice.toString());
        assertEquals("Expected correct duration", 1.4, secondVoice.duration(), 0);
        
        Music togetherVoices = Music.together(firstVoice, secondVoice);
        String togetherVoicesString = "(" + firstVoiceString + " || " + secondVoiceString + ")";
        assertEquals("Expected correct String", togetherVoicesString, togetherVoices.toString());
        assertEquals("Expected correct duration", 4, togetherVoices.duration(), 0);
    }
    
    // Covers the following:
    //
    // equals():
    //      music includes rest, note, lyrics, concat, together
    //      tests for structural equality
    //      tests for equal and not equal
    @Test
    public void testMusicEquals() {
        // Tests for rest
        Music rest1 = Music.rest(1);
        Music rest2 = Music.rest(1);
        Music rest3 = Music.rest(2);
        assertTrue(rest1.equals(rest2));
        assertFalse(rest1.equals(rest3));
        
        // Tests for note
        Music note1 = Music.note(1, new Pitch('A'), Instrument.PIANO);
        Music note2 = Music.note(1, new Pitch('A'), Instrument.PIANO);
        Music note3 = Music.note(1, new Pitch('B'), Instrument.PIANO);
        Music note4 = Music.note(2, new Pitch('A'), Instrument.PIANO);
        Music note5 = Music.note(1, new Pitch('A'), Instrument.ACCORDION);
        assertTrue(note1.equals(note2));
        assertFalse(note1.equals(note3));
        assertFalse(note1.equals(note4));
        assertFalse(note1.equals(note5));
        
        // Tests for lyrics
        Music lyrics1 = Music.lyrics("*hello*", "voice1");
        Music lyrics2 = Music.lyrics("*hello*", "voice1");
        Music lyrics3 = Music.lyrics("*hello*", "voice2");
        Music lyrics4 = Music.lyrics("*goodbye*", "voice1");
        assertTrue(lyrics1.equals(lyrics2));
        assertFalse(lyrics1.equals(lyrics3));
        assertFalse(lyrics1.equals(lyrics4));
        
        // Tests for concat
        Music concat1 = Music.concat(note1, note3);
        Music concat2 = Music.concat(note1, note3);
        Music concat3 = Music.concat(note1, note4);
        Music concat4 = Music.concat(note5, note3);
        assertTrue(concat1.equals(concat2));
        assertFalse(concat1.equals(concat3));
        assertFalse(concat1.equals(concat4));
        
        // Tests for together
        Music together1 = Music.together(note1, note3);
        Music together2 = Music.together(note1, note3);
        Music together3 = Music.together(note1, note4);
        Music together4 = Music.together(note5, note3);
        assertTrue(together1.equals(together2));
        assertFalse(together1.equals(together3));
        assertFalse(together1.equals(together4));
        
        // Tests for structural equality
        Music concatConcats1 = Music.concat(Music.concat(note1, note2), Music.concat(note3, note4));
        Music concatConcats2 = Music.concat(Music.concat(Music.concat(note1, note2), note3), note4);
        assertFalse(concatConcats1.equals(concatConcats2));
        
        Music togetherTogethers1 = Music.together(Music.together(note1, note2), Music.together(note3, note4));
        Music togetherTogethers2 = Music.together(Music.together(Music.together(note1, note2), note3), note4);
        assertFalse(togetherTogethers1.equals(togetherTogethers2));
    }
    
    // Covers the following:
    //
    // hashCode():
    //      music includes rest, note, lyrics, concat, together
    @Test
    public void testMusicHashCode() {
        // Tests rest
        Music rest1 = Music.rest(1);
        Music rest2 = Music.rest(1);
        Music rest3 = Music.rest(2);
        assertTrue(rest1.hashCode()==rest1.hashCode());
        assertTrue(rest1.hashCode()==rest2.hashCode());
        assertFalse(rest1.hashCode()==rest3.hashCode());
        
        // Tests note
        Music note1 = Music.note(1, new Pitch('A'), Instrument.PIANO);
        Music note2 = Music.note(1, new Pitch('A'), Instrument.PIANO);
        Music note3 = Music.note(1, new Pitch('B'), Instrument.PIANO);
        Music note4 = Music.note(2, new Pitch('A'), Instrument.PIANO);
        Music note5 = Music.note(1, new Pitch('A'), Instrument.ACCORDION);
        assertTrue(note1.hashCode()==note1.hashCode());
        assertTrue(note1.hashCode()==note2.hashCode());
        assertFalse(note1.hashCode()==note3.hashCode());
        assertFalse(note1.hashCode()==note4.hashCode());
        assertFalse(note1.hashCode()==note5.hashCode());
        
        // Tests lyrics
        Music lyrics1 = Music.lyrics("*hello*", "voice1");
        Music lyrics2 = Music.lyrics("*hello*", "voice1");
        Music lyrics3 = Music.lyrics("*hello*", "voice2");
        Music lyrics4 = Music.lyrics("*goodbye*", "voice1");
        assertTrue(lyrics1.hashCode()==lyrics1.hashCode());
        assertTrue(lyrics1.hashCode()==lyrics2.hashCode());
        assertFalse(lyrics1.hashCode()==lyrics3.hashCode());
        assertFalse(lyrics1.hashCode()==lyrics4.hashCode());
        
        // Tests concat
        Music concat1 = Music.concat(note1, note3);
        Music concat2 = Music.concat(note1, note3);
        Music concat3 = Music.concat(note1, note4);
        Music concat4 = Music.concat(note5, note3);
        assertTrue(concat1.hashCode()==concat1.hashCode());
        assertTrue(concat1.hashCode()==concat2.hashCode());
        assertFalse(concat1.hashCode()==concat3.hashCode());
        assertFalse(concat1.hashCode()==concat4.hashCode());
        
        // Tests together
        Music together1 = Music.together(note1, note3);
        Music together2 = Music.together(note1, note3);
        Music together3 = Music.together(note1, note4);
        Music together4 = Music.together(note5, note3);
        assertTrue(together1.hashCode()==together1.hashCode());
        assertTrue(together1.hashCode()==together2.hashCode());
        assertFalse(together1.hashCode()==together3.hashCode());
        assertFalse(together1.hashCode()==together4.hashCode());
    }
    
}
