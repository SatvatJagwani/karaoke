package karaoke;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import karaoke.sound.Instrument;
import karaoke.sound.Pitch;

public class PieceTest {
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Testing Strategy:
    // 
    // parseFromFile():
    //     valid filename
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
    @Test
    public void testSimplePiece() {
        Instrument instrument = Instrument.PIANO;
        Music correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, Music.note(2, Pitch.MIDDLE_C.transpose(-Pitch.OCTAVE), instrument));
        correctMusic = Music.concat(correctMusic, Music.note(2, Pitch.MIDDLE_C, instrument));
        correctMusic = Music.concat(correctMusic, Music.note(2, Pitch.MIDDLE_C.transpose(2*Pitch.OCTAVE), instrument));
        correctMusic = Music.concat(correctMusic, Music.note(2, Pitch.MIDDLE_C.transpose(3*Pitch.OCTAVE), instrument));
        Piece givenPiece = new Piece("Unknown", 1, 0.125, "4/4", 100, "sample 1", Collections.singleton("Voice 1"), "C", correctMusic);
        assertEquals("Unknown", givenPiece.getNameOfComposer());
        assertEquals(1, givenPiece.getIndex());
        assertEquals(0.125, givenPiece.getNoteDuration(), 0.0000001);
        assertEquals("4/4", givenPiece.getMeter());
        assertEquals(100, givenPiece.beatsPerMinute());
        assertEquals("sample 1", givenPiece.getTitle());
        assertEquals(Collections.singleton("Voice 1"), givenPiece.getVoices());
        assertEquals("C", givenPiece.getKey());
        assertEquals(correctMusic, givenPiece.getMusic());
        assertEquals(givenPiece, Piece.parseFromFile("sample-abc/sample1.abc"));
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
    @Test
    public void testPieceWithChords() {
        Instrument instrument = Instrument.PIANO;
        Music correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, Music.note(1, new Pitch('E'), instrument));
        correctMusic = Music.together(correctMusic, Music.note(1, new Pitch('C'), instrument));
        Piece givenPiece = new Piece("Unknown", 8, 0.125, "4/4", 100, "Chord", Collections.singleton("Voice 1"), "C", correctMusic);
        assertEquals("Unknown", givenPiece.getNameOfComposer());
        assertEquals(8, givenPiece.getIndex());
        assertEquals(0.125, givenPiece.getNoteDuration(), 0.0000001);
        assertEquals("4/4", givenPiece.getMeter());
        assertEquals(100, givenPiece.beatsPerMinute());
        assertEquals("Chord", givenPiece.getTitle());
        assertEquals(Collections.singleton("Voice 1"), givenPiece.getVoices());
        assertEquals("C", givenPiece.getKey());
        assertEquals(correctMusic, givenPiece.getMusic());
        assertEquals(givenPiece, Piece.parseFromFile("sample-abc/sample2.abc"));
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
    @Test
    public void testPieceWithMultipleVoices() {
        Instrument instrument = Instrument.PIANO;
        Music correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, Music.note(1, new Pitch('C'), instrument));
        correctMusic = Music.together(correctMusic, Music.note(1, new Pitch('E'), instrument));
        correctMusic = Music.together(correctMusic, Music.note(1, new Pitch('G'), instrument));
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
        assertEquals(givenPiece, Piece.parseFromFile("sample-abc/sample3.abc"));
    }
    
}
