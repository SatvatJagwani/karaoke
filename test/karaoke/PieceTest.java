package karaoke;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import edu.mit.eecs.parserlib.UnableToParseException;
import karaoke.sound.Instrument;
import karaoke.sound.Pitch;

/**
 * Test cases for Piece 
 */
public class PieceTest {
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Testing Strategy:
    // 
    // parseFromFile():
    //     filename: valid filename, file not present, file not in right format 
    // Piece():
    //     composer: unknown, known
    //     index: >=0
    //     defaultNoteDuration: >0
    //     meter: valid format
    //     beatsPerMinute: >0
    //     title: valid
    //     voices.size(): 1, >1
    //     key: correct
    //     music no. of notes: 0, >0
    //           no. of rests: 0, >0
    //           no. of chords: 0, >0
    //           no. of lyrics: 0, >0
    //           no. of voices: 1, >1
    // toString():
    //     music no. of notes: 0, >0
    //           no. of rests: 0, >0
    //           no. of chords: 0, >0
    //           no. of lyrics: 0, >0
    //           no. of voices: 1, >1
    // equals():
    //     type of that: Piece, not Piece
    //     music no. of notes: 0, >0
    //           no. of rests: 0, >0
    //           no. of chords: 0, >0
    //           no. of lyrics: 0, >0
    //           no. of voices: 1, >1
    //     music of this and that: same structure and sound, different structure same sound, different sound
    //     header fields of this and that: same, different
    // hashCode():
    //     music no. of notes: 0, >0
    //           no. of rests: 0, >0
    //           no. of chords: 0, >0
    //           no. of lyrics: 0, >0
    //           no. of voices: 1, >1

    /*
     * Get music togethered with *no lyrics* sung by Voice 1
     */
    private static Music defaultBeginning(Music music) {
        return Music.together(music, Music.lyrics("*no lyrics*", "voice1"));
    }
    
    // covers
    // parseFromFile():
    //     valid filename
    // Piece():
    //     composer: unknown
    //     index: >=0
    //     defaultNoteDuration: >0
    //     meter: valid format
    //     beatsPerMinute: >0
    //     title: valid
    //     voices.size(): 1
    //     key: correct
    //     music no. of notes: >0
    //           no. of nonempty rests: 0
    //           no. of chords: 0
    //           no. of lyrics: 0
    //           no. of voices: 1
    // toString():
    //     music no. of notes: >0
    //           no. of nonempty rests: 0
    //           no. of chords: 0
    //           no. of lyrics: 0
    //           no. of voices: 1
    // equals():
    //     type of that: Piece
    //     music no. of notes: >0
    //           no. of nonempty rests: 0
    //           no. of chords: 0
    //           no. of lyrics: 0
    //           no. of voices: 1
    //     music of this and that: same structure and sound
    //     header fields of this and that: same
    // hashCode():
    //     music no. of notes: >0
    //           no. of nonempty rests: 0
    //           no. of chords: 0
    //           no. of lyrics: 0
    //           no. of voices: 1
    @Test
    public void testSimplePiece() throws IOException, UnableToParseException {
        Instrument instrument = Instrument.PIANO;
        Music correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, defaultBeginning(Music.note(2, Pitch.MIDDLE_C.transpose(-Pitch.OCTAVE), instrument)));
        correctMusic = Music.concat(correctMusic, Music.note(2, Pitch.MIDDLE_C, instrument));
        correctMusic = Music.concat(correctMusic, Music.note(1, Pitch.MIDDLE_C.transpose(2*Pitch.OCTAVE), instrument));
        correctMusic = Music.concat(correctMusic, Music.note(1, Pitch.MIDDLE_C.transpose(3*Pitch.OCTAVE), instrument));
        Piece givenPiece = new Piece("Unknown", 1, 0.125, "4/4", 100, "sample 1", Collections.singleton("voice1"), "C", correctMusic);
        assertEquals("Unknown", givenPiece.getNameOfComposer());
        assertEquals(1, givenPiece.getIndex());
        assertEquals(0.125, givenPiece.getNoteDuration(), 0.0000001);
        assertEquals("4/4", givenPiece.getMeter());
        assertEquals(100, givenPiece.beatsPerMinute());
        assertEquals("sample 1", givenPiece.getTitle());
        assertEquals(Collections.singleton("voice1"), givenPiece.getVoices());
        assertEquals("C", givenPiece.getKey());
        assertEquals(correctMusic, givenPiece.getMusic());
        Piece parsedPiece = Piece.parseFromFile("sample-abc/sample1.abc");
        assertEquals(givenPiece, parsedPiece);
        assertEquals(givenPiece.hashCode(), parsedPiece.hashCode());
        String note1 = "(2.0, " + Pitch.MIDDLE_C.transpose(-Pitch.OCTAVE).toString() + ")";
        String note1WithNoLyrics = "(" + note1 + " || (voice1: *no lyrics*))";
        String note2 = "(2.0, " + Pitch.MIDDLE_C.toString() + ")";
        String note3 = "(1.0, " + Pitch.MIDDLE_C.transpose(2*Pitch.OCTAVE).toString() + ")";
        String note4 = "(1.0, " + Pitch.MIDDLE_C.transpose(3*Pitch.OCTAVE).toString() + ")";
        assertEquals(givenPiece.toString(), "[Unknown, 1, 0.125, 4/4, 100, sample 1, [voice1], C, (((((0.0) && " + note1WithNoLyrics + ") && " + note2 + ") && " + note3 + ") && " + note4 + ")]");
    }
    // covers
    // Piece():
    //     composer: unknown
    //     index: >=0
    //     defaultNoteDuration: >0
    //     meter: valid format
    //     beatsPerMinute: >0
    //     title: valid
    //     voices.size(): 1
    //     key: correct
    //     music no. of notes: 0
    //           no. of nonempty rests: 0
    //           no. of chords: 0
    //           no. of lyrics: 0
    //           no. of voices: 1
    // toString():
    //     music no. of notes: 0
    //           no. of nonempty rests: 0
    //           no. of chords: 0
    //           no. of lyrics: 0
    //           no. of voices: 1
    // equals():
    //     type of that: Piece
    //     music no. of notes: 0
    //           no. of nonempty rests: 0
    //           no. of chords: 0
    //           no. of lyrics: 0
    //           no. of voices: 1
    //     music of this and that: same structure and sound
    //     header fields of this and that: same
    // hashCode():
    //     music no. of notes: 0
    //           no. of nonempty rests: 0
    //           no. of chords: 0
    //           no. of lyrics: 0
    //           no. of voices: 1
    @Test
    public void testSilentPiece() {
        Music correctMusic = Music.rest(0);
        Piece givenPiece = new Piece("Unknown", 3, 0.25, "2/2", 100, "Silence", Collections.singleton("Voice 1"), "Am", correctMusic);
        assertEquals("Unknown", givenPiece.getNameOfComposer());
        assertEquals(3, givenPiece.getIndex());
        assertEquals(0.25, givenPiece.getNoteDuration(), 0.0000001);
        assertEquals("2/2", givenPiece.getMeter());
        assertEquals(100, givenPiece.beatsPerMinute());
        assertEquals("Silence", givenPiece.getTitle());
        assertEquals(Collections.singleton("Voice 1"), givenPiece.getVoices());
        assertEquals("Am", givenPiece.getKey());
        assertEquals(correctMusic, givenPiece.getMusic());
        assertEquals("[Unknown, 3, 0.25, 2/2, 100, Silence, [Voice 1], Am, (0.0)]", givenPiece.toString());
        Piece newPiece = new Piece("Unknown", 3, 0.25, "2/2", 100, "Silence", Collections.singleton("Voice 1"), "Am", Music.rest(0));
        assertEquals(newPiece, givenPiece);
        assertEquals(newPiece.hashCode(), givenPiece.hashCode());
    }
    
    // covers
    // Piece():
    //     composer: known
    //     index: >=0
    //     defaultNoteDuration: >0
    //     meter: valid format
    //     beatsPerMinute: >0
    //     title: valid
    //     voices.size(): 1
    //     key: correct
    //     music no. of notes: >0
    //           no. of nonempty rests: >0
    //           no. of chords: 0
    //           no. of lyrics: >0
    //           no. of voices: 1
    // toString():
    //     music no. of notes: >0
    //           no. of nonempty rests: >0
    //           no. of chords: 0
    //           no. of lyrics: >0
    //           no. of voices: 1
    // equals():
    //     type of that: Piece
    //     music no. of notes: >0
    //           no. of nonempty rests: >0
    //           no. of chords: 0
    //           no. of lyrics: >0
    //           no. of voices: 1
    //     music of this and that: same structure and sound
    //     header fields of this and that: same
    // hashCode():
    //     music no. of notes: >0
    //           no. of nonempty rests: >0
    //           no. of chords: 0
    //           no. of lyrics: >0
    //           no. of voices: 1
    @Test
    public void testPieceWithLyrics() {
        Instrument instrument = Instrument.PIANO;
        Music correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, Music.rest(4));
        correctMusic = Music.concat(correctMusic, Music.together(Music.note(2, new Pitch('D'), instrument), Music.lyrics("*A*-maz-ing grace! How sweet the sound That saved a wretch like me.", "Voice 1")));
        correctMusic = Music.concat(correctMusic, Music.together(Music.note(4, new Pitch('G'), instrument), Music.lyrics("A-*maz*-ing grace! How sweet the sound That saved a wretch like me.", "Voice 1")));
        Music concatNotes = Music.concat(Music.note(1, new Pitch('B'), instrument),Music.note(1, new Pitch('G'), instrument));
        correctMusic = Music.concat(correctMusic, Music.together(concatNotes, Music.lyrics("A-maz-*ing* grace! How sweet the sound That saved a wretch like me.", "Voice 1")));
        Piece givenPiece = new Piece("John Newton", 1, 0.125, "3/4", 100, "Piece No.3", Collections.singleton("Voice 1"), "C", correctMusic);
        assertEquals("John Newton", givenPiece.getNameOfComposer());
        assertEquals(1, givenPiece.getIndex());
        assertEquals(0.125, givenPiece.getNoteDuration(), 0.0000001);
        assertEquals("3/4", givenPiece.getMeter());
        assertEquals(100, givenPiece.beatsPerMinute());
        assertEquals("Piece No.3", givenPiece.getTitle());
        assertEquals(Collections.singleton("Voice 1"), givenPiece.getVoices());
        assertEquals("C", givenPiece.getKey());
        assertEquals(correctMusic, givenPiece.getMusic());
        String note1 = "(2.0, " + new Pitch('D').toString() + ")";
        String lyrics1 = "(Voice 1: *A*-maz-ing grace! How sweet the sound That saved a wretch like me.)";
        String noteWithLyrics1 = "(" + note1 + " || " + lyrics1 + ")";
        String note2 = "(4.0, " + new Pitch('G').toString() + ")";
        String lyrics2 = "(Voice 1: A-*maz*-ing grace! How sweet the sound That saved a wretch like me.)";
        String noteWithLyrics2 = "(" + note2 + " || " + lyrics2 + ")";
        String note3 = "(1.0, " + new Pitch('B').toString() + ")";
        String note4 = "(1.0, " + new Pitch('G').toString() + ")";
        String lyrics34 = "(Voice 1: A-maz-*ing* grace! How sweet the sound That saved a wretch like me.)";
        String noteWithLyrics3 = "((" + note3 + " && " + note4 + ") || " + lyrics34 + ")";
        assertEquals("[John Newton, 1, 0.125, 3/4, 100, Piece No.3, [Voice 1], C, (((((0.0) && (4.0)) && " + noteWithLyrics1 + ") && " + noteWithLyrics2 + ") && " + noteWithLyrics3 + ")]", givenPiece.toString());
        Music nextMusic = Music.rest(0);
        nextMusic = Music.concat(nextMusic, Music.rest(4));
        nextMusic = Music.concat(nextMusic, Music.together(Music.note(2, new Pitch('D'), instrument), Music.lyrics("*A*-maz-ing grace! How sweet the sound That saved a wretch like me.", "Voice 1")));
        nextMusic = Music.concat(nextMusic, Music.together(Music.note(4, new Pitch('G'), instrument), Music.lyrics("A-*maz*-ing grace! How sweet the sound That saved a wretch like me.", "Voice 1")));
        Music nextConcatNotes = Music.concat(Music.note(1, new Pitch('B'), instrument),Music.note(1, new Pitch('G'), instrument));
        nextMusic = Music.concat(nextMusic, Music.together(nextConcatNotes, Music.lyrics("A-maz-*ing* grace! How sweet the sound That saved a wretch like me.", "Voice 1")));
        Piece nextPiece = new Piece("John Newton", 1, 0.125, "3/4", 100, "Piece No.3", Collections.singleton("Voice 1"), "C", nextMusic);
        assertEquals(givenPiece, nextPiece);
        assertEquals(givenPiece.hashCode(), nextPiece.hashCode());
    }
    
    // covers
    // parseFromFile():
    //     valid filename
    // Piece():
    //     composer: unknown
    //     index: >=0
    //     defaultNoteDuration: >0
    //     meter: valid format
    //     beatsPerMinute: >0
    //     title: valid
    //     voices.size(): 1
    //     key: correct
    //     music no. of notes: >0
    //           no. of nonempty rests: 0
    //           no. of chords: >0
    //           no. of lyrics: 0
    //           no. of voices: 1
    // toString():
    //     music no. of notes: >0
    //           no. of nonempty rests: 0
    //           no. of chords: >0
    //           no. of lyrics: 0
    //           no. of voices: 1
    // equals():
    //     type of that: Piece
    //     music no. of notes: >0
    //           no. of nonempty rests: 0
    //           no. of chords: >0
    //           no. of lyrics: 0
    //           no. of voices: 1
    //     music of this and that: same structure and sound
    //     header fields of this and that: same
    // hashCode():
    //     music no. of notes: >0
    //           no. of nonempty rests: 0
    //           no. of chords: >0
    //           no. of lyrics: 0
    //           no. of voices: 1
    @Test
    public void testPieceWithChords() throws IOException, UnableToParseException {
        Instrument instrument = Instrument.PIANO;
        Music correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, defaultBeginning(Music.together(Music.note(1, new Pitch('E'), instrument), Music.note(1, new Pitch('C'), instrument))));
        Piece givenPiece = new Piece("Unknown", 8, 0.125, "4/4", 100, "Chord", Collections.singleton("voice1"), "C", correctMusic);
        assertEquals("Unknown", givenPiece.getNameOfComposer());
        assertEquals(8, givenPiece.getIndex());
        assertEquals(0.125, givenPiece.getNoteDuration(), 0.0000001);
        assertEquals("4/4", givenPiece.getMeter());
        assertEquals(100, givenPiece.beatsPerMinute());
        assertEquals("Chord", givenPiece.getTitle());
        assertEquals(Collections.singleton("voice1"), givenPiece.getVoices());
        assertEquals("C", givenPiece.getKey());
        assertEquals(correctMusic, givenPiece.getMusic());
        Piece parsedPiece = Piece.parseFromFile("sample-abc/sample2.abc");
        assertEquals(givenPiece, parsedPiece);
        assertEquals(givenPiece.hashCode(), parsedPiece.hashCode());
        String note1 = "(1.0, " + new Pitch('E').toString() + ")";
        String note2 = "(1.0, " + new Pitch('C').toString() + ")";
        String chordWithNoLyrics = "((" + note1 + " || " + note2 + ") || (voice1: *no lyrics*))";
        assertEquals(givenPiece.toString(), "[Unknown, 8, 0.125, 4/4, 100, Chord, [voice1], C, ((0.0) && " + chordWithNoLyrics + ")]");
    }
    
    // covers
    // parseFromFile():
    //     valid filename
    // Piece():
    //     composer: unknown
    //     index: >=0
    //     defaultNoteDuration: >0
    //     meter: valid format
    //     beatsPerMinute: >0
    //     title: valid
    //     voices.size(): >1
    //     key: correct
    //     music no. of notes: >0
    //           no. of nonempty rests: 0
    //           no. of chords: 0
    //           no. of lyrics: 0
    //           no. of voices: >1
    // toString():
    //     music no. of notes: >0
    //           no. of nonempty rests: 0
    //           no. of chords: 0
    //           no. of lyrics: 0
    //           no. of voices: >1
    // equals():
    //     type of that: Piece
    //     music no. of notes: >0
    //           no. of nonempty rests: 0
    //           no. of chords: 0
    //           no. of lyrics: 0
    //           no. of voices: >1
    //     music of this and that: same structure and sound
    //     header fields of this and that: same
    // hashCode():
    //     music no. of notes: >0
    //           no. of nonempty rests: 0
    //           no. of chords: 0
    //           no. of lyrics: 0
    //           no. of voices: >1
    @Test
    public void testPieceWithMultipleVoices() throws IOException, UnableToParseException {
        Instrument instrument = Instrument.PIANO;
        Music restMusic = Music.rest(0);
        Music firstVoice = Music.concat(restMusic, Music.together(Music.note(1, new Pitch('C'), instrument), Music.lyrics("*no lyrics*", "1")));
        Music secondVoice = Music.concat(restMusic, Music.together(Music.note(1, new Pitch('E').transpose(-1), instrument), Music.lyrics("*no lyrics*", "2")));
        Music thirdVoice = Music.concat(restMusic, Music.together(Music.note(1, new Pitch('G'), instrument), Music.lyrics("*no lyrics*", "3")));
        Music correctMusic = Music.together(Music.together(firstVoice, secondVoice), thirdVoice);
        Set<String> voices = new HashSet<>();
        voices.add("1");
        voices.add("2");
        voices.add("3");
        Piece givenPiece = new Piece("Unknown", 1, 0.125, "4/4", 100, "voices", voices, "Cm", correctMusic);
        assertEquals("Unknown", givenPiece.getNameOfComposer());
        assertEquals(1, givenPiece.getIndex());
        assertEquals(0.125, givenPiece.getNoteDuration(), 0.0000001);
        assertEquals("4/4", givenPiece.getMeter());
        assertEquals(100, givenPiece.beatsPerMinute());
        assertEquals("voices", givenPiece.getTitle());
        assertEquals(voices, givenPiece.getVoices());
        assertEquals("Cm", givenPiece.getKey());
        assertEquals(correctMusic, givenPiece.getMusic());
        Piece parsedPiece = Piece.parseFromFile("sample-abc/sample3.abc");
        assertEquals(givenPiece, parsedPiece);
        assertEquals(givenPiece.hashCode(), parsedPiece.hashCode());
        String note1 = "(1.0, " + new Pitch('C').toString() + ")";
        String note1WithNoLyrics = "(" + note1 + " || (1: *no lyrics*))";
        String note2 = "(1.0, " + new Pitch('E').transpose(-1).toString() + ")";
        String note2WithNoLyrics = "(" + note2 + " || (2: *no lyrics*))";
        String note3 = "(1.0, " + new Pitch('G').toString() + ")";
        String note3WithNoLyrics = "(" + note3 + " || (3: *no lyrics*))";
        assertEquals(givenPiece.toString(), "[Unknown, 1, 0.125, 4/4, 100, voices, [1, 2, 3], Cm, ((((0.0) && " + note1WithNoLyrics + ") || ((0.0) && " + note2WithNoLyrics + ")) || ((0.0) && " + note3WithNoLyrics + "))]");
    }
    
    // covers
    // parseFromFile():
    //     filename: file not present
    @Test(expected=IOException.class)
    public void testFileNotFound() throws IOException, UnableToParseException {
        Piece.parseFromFile("sample-abc/non-existent.abc");
    }
    
    // covers
    // parseFromFile():
    //     filename: file not in right format
    @Test(expected=UnableToParseException.class)
    public void testFileNotValidMusic() throws IOException, UnableToParseException {
        Piece.parseFromFile("src/karaoke/Piece.java");
    }

    // covers
    // equals():
    //     type of that: not Piece
    @Test
    public void testEqualsNonPiece() {
        Instrument instrument = Instrument.PIANO;
        Music correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, Music.note(2, Pitch.MIDDLE_C.transpose(-Pitch.OCTAVE), instrument));
        correctMusic = Music.concat(correctMusic, Music.note(2, Pitch.MIDDLE_C, instrument));
        Piece givenPiece = new Piece("Unknown", 1, 0.125, "4/4", 100, "sample 1", Collections.singleton("Voice 1"), "C", correctMusic);
        String note1 = "(2.0, " + Pitch.MIDDLE_C.transpose(-Pitch.OCTAVE).toString() + ")";
        String note2 = "(2.0, " + Pitch.MIDDLE_C.toString() + ")";
        assertFalse(givenPiece.equals("[Unknown, 1, 0.125, 4/4, 100, sample 1, [Voice 1], C, (((0.0) && " + note1 + ") && " + note2 + ")]"));
    }
    
    // covers
    // equals():
    //     type of that: Piece
    //     header fields of this and that: different
    @Test
    public void testEqualsSameMusicDifferentHeaders() {
        Instrument instrument = Instrument.PIANO;
        Music correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, Music.note(1, new Pitch('E'), instrument));
        correctMusic = Music.together(correctMusic, Music.note(1, new Pitch('C'), instrument));
        Piece piece1 = new Piece("Unknown", 8, 0.125, "4/4", 100, "Chord", Collections.singleton("Voice 1"), "C", correctMusic);
        Piece piece2 = new Piece("Known", 8, 0.125, "4/4", 100, "Chord", Collections.singleton("Voice 1"), "C", correctMusic);
        assertFalse(piece1.equals(piece2));
    }
    
    // covers
    // equals():
    //     type of that: Piece
    //     music of this and that: different structure same sound
    @Test
    public void testEqualsDifferentStructureSameSound() {
        Instrument instrument = Instrument.PIANO;
        Music rest = Music.rest(0);
        Music note = Music.concat(rest, Music.note(1, new Pitch('E'), instrument));
        Music music1 = Music.together(note, Music.note(1, new Pitch('C'), instrument));
        Music chord = Music.together(Music.note(1, new Pitch('E'), instrument), Music.note(1, new Pitch('C'), instrument));
        Music music2 = Music.concat(rest, chord);
        Piece piece1 = new Piece("Unknown", 8, 0.125, "4/4", 100, "Chord", Collections.singleton("Voice 1"), "C", music1);
        Piece piece2 = new Piece("Unknown", 8, 0.125, "4/4", 100, "Chord", Collections.singleton("Voice 1"), "C", music2);
        assertFalse(piece1.equals(piece2));
    }
    
    // covers
    // equals():
    //     type of that: Piece
    //     music of this and that: different sound
    @Test
    public void testEqualsDifferentSound() {
        Instrument instrument = Instrument.PIANO;
        Music music1 = Music.concat(Music.rest(0), Music.note(1, new Pitch('C'), instrument));
        Music music2 = Music.concat(Music.rest(1), Music.note(2, new Pitch('C'), instrument));
        Piece piece1 = new Piece("Unknown", 1, 0.125, "4/4", 100, "Single Note", Collections.singleton("Voice 1"), "C", music1);
        Piece piece2 = new Piece("Unknown", 1, 0.125, "4/4", 100, "Single Note", Collections.singleton("Voice 1"), "C", music2);
        assertFalse(piece1.equals(piece2));
    }
    
}
