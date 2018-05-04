package karaoke.parser;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import edu.mit.eecs.parserlib.UnableToParseException;
import karaoke.Music;
import karaoke.Piece;
import karaoke.sound.Instrument;
import karaoke.sound.Pitch;

public class PieceParserTest {

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Testing Strategy for PieceParser.parse()
    //
    // header:      
    //      includes all fields, missing some non-required fields 
    // key signature:
    //      major, minor, sharp, flat
    // notes:
    //      separated by |, |], [|, ||
    //      octaves are normal, raised 1, raised > 1, lowered 1, lowered < 1
    //      lengths are whole numbers, fractions, missing numerator, missing denominator
    // accidental:
    //      includes sharp, flat, natural, double sharp, double flat
    //      used with C major key signature, other key signature 
    // rests:
    //      lengths are whole numbers, fractions, missing numerator, missing denominator
    // chords:
    //      includes 2 notes, > 2 notes
    //      notes are normal, have accidental, have multiplicative factors
    // tuplets:
    //      is a duplet, triplet, quadruplet
    //      includes notes of different lengths, chords of different lengths
    // repeats:
    //      repeat starts at beginning of song, major section, begin repeat bar
    //      ending is normal, has alternate endings 
    // multiple voices:
    //      number of voices is 1, > 1
    // lyrics:
    //      number of syllables is fewer, same, more than number of notes
    //      contains syllable held for more than one note, skipped notes, multiple words
    //          under one note, multiple syllables under one note 
    
    
    /*
     * Tests for PieceParser.parse() 
     */
   
    // Covers the following:
    //
    // header:      
    //      includes all fields, missing some non-required fields 
    @Test
    public void testPieceParserHeader() throws UnableToParseException {
        // Simple header
        String header = "X: 1" + "\n";
        header += "T: simple song" + "\n";
        header += "K: C" + "\n";
        String body = "C" + "\n";
        
        // Parse the simple header 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct voices and music 
        Set<String> correctVoices = new HashSet<>(); 
        correctVoices.add("voice1");
        Music correctMusic = Music.concat(Music.rest(0), Music.note(1, Pitch.MIDDLE_C, Instrument.PIANO));
        
        assertEquals("expected correct index", 1, piece.getIndex());
        assertEquals("expected correct title", "simple song", piece.getTitle());
        assertEquals("expected correct composer", "Unkown", piece.getNameOfComposer());
        assertEquals("expected correct key signature", "C", piece.getKey());
        assertEquals("expected correct meter", "4/4", piece.getMeter());
        assertEquals("expected correct default note length", "1/8", piece.getNoteDuration());
        assertEquals("expected correct tempo", 100, piece.beatsPerMinute());
        assertEquals("expected correct voices", correctVoices, piece.getVoices());
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // More complicated header 
        header = "X: 2" + "\n";
        header += "T: harder song" + "\n";
        header += "C: Matt" + "\n";
        header += "M: 7/8" + "\n";
        header += "L: 1/16" + "\n";
        header += "Q: 1/8=100" + "\n"; // beats per minute is 200
        header += "V: upper" + "\n";
        header += "K: F#m" + "\n";
        body = "V: upper" + "\n";
        body += "C" + "\n";
        
        // Parse the complicated header 
        piece = PieceParser.parse(header + body);
        
        // Create the correct voices and music 
        correctVoices = new HashSet<>(); 
        correctVoices.add("upper");
        correctMusic = Music.concat(Music.rest(0), Music.note(1, Pitch.MIDDLE_C, Instrument.PIANO));
        
        assertEquals("expected correct index", 2, piece.getIndex());
        assertEquals("expected correct title", "harder song", piece.getTitle());
        assertEquals("expected correct composer", "Matt", piece.getNameOfComposer());
        assertEquals("expected correct key signature", "F#m", piece.getKey());
        assertEquals("expected correct meter", "7/8", piece.getMeter());
        assertEquals("expected correct default note length", "1/16", piece.getNoteDuration());
        assertEquals("expected correct tempo", 200, piece.beatsPerMinute());
        assertEquals("expected correct voices", correctVoices, piece.getVoices());
        assertEquals("expected correct music", correctMusic, piece.getMusic());
    }
    
    // Covers the following:
    //
    // key signature:
    //      major, minor, sharp, flat
    public void testPieceParserKeySignature() throws UnableToParseException {
        // G major
        String header = "X: 1" + "\n";
        header += "T: simple song" + "\n";
        header += "K: G" + "\n";
        header += "M: 2/4" + "\n";
        header += "L: 1/4" + "\n";
        header += "Q: 1/4=100" + "\n";
        String body = "f A" + "\n";
        
        // Parse the string 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                Music.note(1, new Pitch('F').transpose(Pitch.OCTAVE).transpose(1), Instrument.PIANO));
        correctMusic = Music.concat(correctMusic, 
                Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // F minor
        header = "X: 1" + "\n";
        header += "T: simple song" + "\n";
        header += "K: Fm" + "\n";
        header += "M: 2/4" + "\n";
        header += "L: 1/4" + "\n";
        header += "Q: 1/4=100" + "\n";
        body = "A B" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                Music.note(1, new Pitch('A').transpose(-1), Instrument.PIANO));
        correctMusic = Music.concat(correctMusic, 
                Music.note(1, new Pitch('B').transpose(-1), Instrument.PIANO));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // A sharp minor
        header = "X: 1" + "\n";
        header += "T: simple song" + "\n";
        header += "K: A#m" + "\n";
        header += "M: 2/4" + "\n";
        header += "L: 1/4" + "\n";
        header += "Q: 1/4=100" + "\n";
        body = "A e | F F" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                Music.note(1, new Pitch('A').transpose(1), Instrument.PIANO));
        correctMusic = Music.concat(correctMusic, 
                Music.note(1, new Pitch('E').transpose(Pitch.OCTAVE).transpose(1), Instrument.PIANO));
        correctMusic = Music.concat(correctMusic, 
                Music.note(1, new Pitch('F'), Instrument.PIANO));
        correctMusic = Music.concat(correctMusic, 
                Music.note(1, new Pitch('F'), Instrument.PIANO));
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // E flat minor
        header = "X: 1" + "\n";
        header += "T: simple song" + "\n";
        header += "K: Ebm" + "\n";
        header += "M: 2/4" + "\n";
        header += "L: 1/4" + "\n";
        header += "Q: 1/4=100" + "\n";
        body = "F A" + "\n";
        
        // Parse the string  
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                Music.note(1, new Pitch('F'), Instrument.PIANO));
        correctMusic = Music.concat(correctMusic, 
                Music.note(1, new Pitch('A').transpose(-1), Instrument.PIANO));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
    }
    
    // Covers the following:
    //
    // notes:
    //      separated by |, |], [|, ||
    //      octaves are normal, raised 1, raised > 1, lowered 1, lowered < 1
    //      lengths are whole numbers, fractions, missing numerator, missing denominator
    public void testPieceParserNotes() throws UnableToParseException {
        // Test different sections 
        String header = "X: 1" + "\n";
        header += "T: simple song" + "\n";
        header += "K: C" + "\n";
        header += "M: 2/4" + "\n";
        header += "L: 1/4" + "\n";
        header += "Q: 1/4=100" + "\n";
        String body = "[| A B | C D || E F | G A |]" + "\n";
        
        // Parse the string 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                Music.note(1, new Pitch('A'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, 
                Music.note(1, new Pitch('B'), Instrument.PIANO));
        
        Music secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, 
                Music.note(1, new Pitch('C'), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, 
                Music.note(1, new Pitch('D'), Instrument.PIANO));
        
        Music thirdMeasure = Music.rest(0);
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.note(1, new Pitch('E'), Instrument.PIANO));
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.note(1, new Pitch('F'), Instrument.PIANO));
        
        Music fourthMeasure = Music.rest(0);
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.note(1, new Pitch('G'), Instrument.PIANO));
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        Music correctMusic = Music.concat(
                Music.concat(firstMeasure, secondMeasure), 
                Music.concat(thirdMeasure, fourthMeasure));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // Test octaves and lengths
        header = "X: 1" + "\n";
        header += "T: simple song" + "\n";
        header += "K: C" + "\n";
        header += "M: 2/4" + "\n";
        header += "L: 1/4" + "\n";
        header += "Q: 1/4=100" + "\n";
        body = "A,,2 | B,/ C3/ | d'/4 e''7/4 " + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                Music.note(2, new Pitch('A').transpose(-2*Pitch.OCTAVE), Instrument.PIANO));
        
        secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, 
                Music.note(.5, new Pitch('B').transpose(-1*Pitch.OCTAVE), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, 
                Music.note(1.5, new Pitch('C'), Instrument.PIANO));
        
        thirdMeasure = Music.rest(0);
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.note(.25, new Pitch('D').transpose(2*Pitch.OCTAVE), Instrument.PIANO));
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.note(1.75, new Pitch('E').transpose(3*Pitch.OCTAVE), Instrument.PIANO));
        
        correctMusic = Music.concat(
                Music.concat(firstMeasure, secondMeasure), 
                thirdMeasure);
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
    }
    
    // TODO: Other Tests

}
