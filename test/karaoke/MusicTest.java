package karaoke;

import static org.junit.Assert.assertEquals;

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
    // TODO tests for equals() and hashCode()
    // Cover each part at least once 

    /*
     * Tests for Music.toString() and Music.duration()
     */
    
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
    
}
