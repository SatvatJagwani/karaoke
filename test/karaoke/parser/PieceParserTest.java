package karaoke.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
    // comments:
    //      comments on a new line, comments at the end of a line
    //      comments in the header, comments in the body
    // checked exceptions thrown:
    //      none, UnableToParseException
    //
    // Covers each part at least once 
    
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
        String header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "K:C" + "\n";
        String body = "C" + "\n";
        
        // Parse the simple header 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct voices and music 
        Set<String> correctVoices = new HashSet<>(); 
        correctVoices.add("voice1");
        Music correctMusic = Music.concat(Music.rest(0), Music.together(Music.note(1, Pitch.MIDDLE_C, Instrument.PIANO), Music.lyrics("*no lyrics*", "voice1")));
        
        assertEquals("expected correct index", 1, piece.getIndex());
        assertEquals("expected correct title", "simple song", piece.getTitle());
        assertEquals("expected correct composer", "Unknown", piece.getNameOfComposer());
        assertEquals("expected correct key signature", "C", piece.getKey());
        assertEquals("expected correct meter", "4/4", piece.getMeter());
        assertEquals("expected correct default note length", 1.0/8.0, piece.getNoteDuration(), 0);
        assertEquals("expected correct tempo", 100, piece.beatsPerMinute());
        assertEquals("expected correct voices", correctVoices, piece.getVoices());
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // More complicated header 
        header = "X:2" + "\n";
        header += "T:harder song" + "\n";
        header += "C:Matt" + "\n";
        header += "M:7/8" + "\n";
        header += "L:1/16" + "\n";
        header += "Q:1/8=100" + "\n"; // beats per minute is 200
        header += "V:upper" + "\n";
        header += "K:Bbm" + "\n";
        body = "V:upper" + "\n";
        body += "C" + "\n";
        
        // Parse the complicated header 
        piece = PieceParser.parse(header + body);
        
        // Create the correct voices and music 
        correctVoices = new HashSet<>(); 
        correctVoices.add("upper");
        correctMusic = Music.concat(Music.rest(0), Music.together(Music.note(1, Pitch.MIDDLE_C, Instrument.PIANO), Music.lyrics("*no lyrics*", "upper")));
        
        assertEquals("expected correct index", 2, piece.getIndex());
        assertEquals("expected correct title", "harder song", piece.getTitle());
        assertEquals("expected correct composer", "Matt", piece.getNameOfComposer());
        assertEquals("expected correct key signature", "Bbm", piece.getKey());
        assertEquals("expected correct meter", "7/8", piece.getMeter());
        assertEquals("expected correct default note length", 1.0/16.0, piece.getNoteDuration(), 0);
        assertEquals("expected correct tempo", 200, piece.beatsPerMinute());
        assertEquals("expected correct voices", correctVoices, piece.getVoices());
        assertEquals("expected correct music", correctMusic, piece.getMusic());
    }
    
    // Covers the following:
    //
    // key signature:
    //      major, minor, sharp, flat
    @Test
    public void testPieceParserKeySignature() throws UnableToParseException {
        // G major
        String header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:G" + "\n";
        String body = "f A" + "\n";
        
        // Parse the string 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                Music.together(Music.note(1, new Pitch('F').transpose(Pitch.OCTAVE).transpose(1), Instrument.PIANO),
                               Music.lyrics("*no lyrics*", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // F minor
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:Fm" + "\n";
        body = "A B" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                Music.together(Music.note(1, new Pitch('A').transpose(-1), Instrument.PIANO),
                               Music.lyrics("*no lyrics*", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                Music.note(1, new Pitch('B').transpose(-1), Instrument.PIANO));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // A sharp minor
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:A#m" + "\n";
        body = "A e | F F" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                Music.together(Music.note(1, new Pitch('A').transpose(1), Instrument.PIANO),
                               Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, 
                Music.note(1, new Pitch('E').transpose(Pitch.OCTAVE).transpose(1), Instrument.PIANO));

        Music secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, 
                Music.note(1, new Pitch('F').transpose(1), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, 
                Music.note(1, new Pitch('F').transpose(1), Instrument.PIANO));
        
        correctMusic = Music.concat(firstMeasure, secondMeasure);
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // E flat minor
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:Ebm" + "\n";
        body = "F A" + "\n";
        
        // Parse the string  
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                Music.together(Music.note(1, new Pitch('F'), Instrument.PIANO),
                               Music.lyrics("*no lyrics*", "voice1")));
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
    @Test
    public void testPieceParserNotes() throws UnableToParseException {
        // Test different sections 
        String header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        String body = "[| A B | C D || E F | G A |]" + "\n";
        
        // Parse the string 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                               Music.lyrics("*no lyrics*", "voice1")));
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
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "A,,2 | B,/ C3/ | d'/4 e''7/4 " + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                Music.together(Music.note(2, new Pitch('A').transpose(-2*Pitch.OCTAVE), Instrument.PIANO),
                               Music.lyrics("*no lyrics*", "voice1")));
        
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
    
    // Covers the following:
    //
    // accidentals:
    //      includes sharp, flat, natural, double sharp, double flat
    //      used with C major key signature, other key signature 
    @Test
    public void testPieceParserAccidental() throws UnableToParseException {
        // used with C major key
        String header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        String body = "[| ^A B | ^^C D || =E F | _G A || __G A | __G A |]" + "\n";
        
        // Parse the string 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                Music.together(Music.note(1, new Pitch('A').transpose(1), Instrument.PIANO),
                               Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, 
                Music.note(1, new Pitch('B'), Instrument.PIANO));
        
        Music secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, 
                Music.note(1, new Pitch('C').transpose(2), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, 
                Music.note(1, new Pitch('D'), Instrument.PIANO));
        
        Music thirdMeasure = Music.rest(0);
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.note(1, new Pitch('E'), Instrument.PIANO));
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.note(1, new Pitch('F'), Instrument.PIANO));
        
        Music fourthMeasure = Music.rest(0);
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.note(1, new Pitch('G').transpose(-1), Instrument.PIANO));
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        Music fifthMeasure = Music.rest(0);
        fifthMeasure = Music.concat(fifthMeasure, 
                Music.note(1, new Pitch('G').transpose(-2), Instrument.PIANO));
        fifthMeasure = Music.concat(fifthMeasure, 
                Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        Music sixthMeasure = Music.rest(0);
        sixthMeasure = Music.concat(sixthMeasure, 
                Music.note(1, new Pitch('G').transpose(-2), Instrument.PIANO));
        sixthMeasure = Music.concat(sixthMeasure, 
                Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        Music correctMusic = Music.concat(
                Music.concat(firstMeasure, secondMeasure), 
                Music.concat(thirdMeasure, fourthMeasure));
        
        correctMusic = Music.concat(correctMusic,
                Music.concat(fifthMeasure, sixthMeasure));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // used with other key signature (G major)
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:G" + "\n";
        body = "[| ^f B | ^^f D || =f F | _f A || __f A | f A |]" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                Music.together(Music.note(1, new Pitch('F').transpose(Pitch.OCTAVE).transpose(1), Instrument.PIANO),
                               Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, 
                Music.note(1, new Pitch('B'), Instrument.PIANO));
        
        secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, 
                Music.note(1, new Pitch('F').transpose(Pitch.OCTAVE).transpose(2), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, 
                Music.note(1, new Pitch('D'), Instrument.PIANO));
        
        thirdMeasure = Music.rest(0);
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.note(1, new Pitch('F').transpose(Pitch.OCTAVE), Instrument.PIANO));
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.note(1, new Pitch('F').transpose(1), Instrument.PIANO));
        
        fourthMeasure = Music.rest(0);
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.note(1, new Pitch('F').transpose(Pitch.OCTAVE).transpose(-1), Instrument.PIANO));
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        fifthMeasure = Music.rest(0);
        fifthMeasure = Music.concat(fifthMeasure, 
                Music.note(1, new Pitch('F').transpose(Pitch.OCTAVE).transpose(-2), Instrument.PIANO));
        fifthMeasure = Music.concat(fifthMeasure, 
                Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        sixthMeasure = Music.rest(0);
        sixthMeasure = Music.concat(sixthMeasure, 
                Music.note(1, new Pitch('F').transpose(Pitch.OCTAVE).transpose(1), Instrument.PIANO));
        sixthMeasure = Music.concat(sixthMeasure, 
                Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        correctMusic = Music.concat(
                Music.concat(firstMeasure, secondMeasure), 
                Music.concat(thirdMeasure, fourthMeasure));
        
        correctMusic = Music.concat(correctMusic,
                Music.concat(fifthMeasure, sixthMeasure));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // used with other key signature (F major / D minor)
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:F" + "\n";
        body = "[| ^B B | ^^B D || =B F | _B A || __B A | B A |]" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                Music.together(Music.note(1, new Pitch('B').transpose(1), Instrument.PIANO),
                               Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, 
                Music.note(1, new Pitch('B').transpose(1), Instrument.PIANO));
        
        secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, 
                Music.note(1, new Pitch('B').transpose(2), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, 
                Music.note(1, new Pitch('D'), Instrument.PIANO));
        
        thirdMeasure = Music.rest(0);
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.note(1, new Pitch('B'), Instrument.PIANO));
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.note(1, new Pitch('F'), Instrument.PIANO));
        
        fourthMeasure = Music.rest(0);
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.note(1, new Pitch('B').transpose(-1), Instrument.PIANO));
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        fifthMeasure = Music.rest(0);
        fifthMeasure = Music.concat(fifthMeasure, 
                Music.note(1, new Pitch('B').transpose(-2), Instrument.PIANO));
        fifthMeasure = Music.concat(fifthMeasure, 
                Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        sixthMeasure = Music.rest(0);
        sixthMeasure = Music.concat(sixthMeasure, 
                Music.note(1, new Pitch('B').transpose(-1), Instrument.PIANO));
        sixthMeasure = Music.concat(sixthMeasure, 
                Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        correctMusic = Music.concat(
                Music.concat(firstMeasure, secondMeasure), 
                Music.concat(thirdMeasure, fourthMeasure));
        
        correctMusic = Music.concat(correctMusic,
                Music.concat(fifthMeasure, sixthMeasure));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
    }

    // Covers the following:
    //
    // rests:
    //      lengths are whole numbers, fractions, missing numerator, missing denominator
    @Test 
    public void testPieceParserRests() throws UnableToParseException {
        // Tests whole numbers and fractions, unsimplified and simplified, and missing numerators and denominators
        String header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        String body = "[| A z | C z1/2 z/ || E z2/4 z3/6 | G z/2 z1/ | z2 |]" + "\n";
        
        // Parse the string 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                               Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, 
                Music.rest(1));
        
        Music secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, 
                Music.note(1, new Pitch('C'), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, 
                Music.rest(0.5));
        secondMeasure = Music.concat(secondMeasure, 
                Music.rest(0.5));
        
        Music thirdMeasure = Music.rest(0);
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.note(1, new Pitch('E'), Instrument.PIANO));
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.rest(0.5));
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.rest(0.5));
        
        Music fourthMeasure = Music.rest(0);
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.note(1, new Pitch('G'), Instrument.PIANO));
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.rest(0.5));
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.rest(0.5));
        
        Music fifthMeasure = Music.rest(0);
        fifthMeasure = Music.concat(fifthMeasure, 
                Music.rest(2));
        
        Music correctMusic = Music.concat(
                Music.concat(firstMeasure, secondMeasure), 
                Music.concat(Music.concat(thirdMeasure, fourthMeasure), fifthMeasure));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
    }
    
    // Covers the following:
    //
    // chords:
    //      includes 2 notes, > 2 notes
    //      notes are normal, have accidental, have multiplicative factors
    @Test
    public void testPieceParserChords() throws UnableToParseException { 
        String header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        String body = "[| [AB] [ABC] | [^AB] [A_B] || [__AB] [A^^B] |[AB/2] [A/2B]A/2 |]" + "\n";
        
        // Parse the string 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                Music.together(Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                                              Music.note(1, new Pitch('B'), Instrument.PIANO)),
                               Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, 
                Music.together(Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                                              Music.note(1, new Pitch('B'), Instrument.PIANO)),
                               Music.note(1, new Pitch('C'), Instrument.PIANO)));
        
        Music secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, 
                Music.together(Music.note(1, new Pitch('A').transpose(1), Instrument.PIANO),
                               Music.note(1, new Pitch('B'), Instrument.PIANO)));
        secondMeasure = Music.concat(secondMeasure, 
                Music.together(Music.note(1, new Pitch('A').transpose(1), Instrument.PIANO),
                               Music.note(1, new Pitch('B').transpose(-1), Instrument.PIANO)));
        
        Music thirdMeasure = Music.rest(0);
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.together(Music.note(1, new Pitch('A').transpose(-2), Instrument.PIANO),
                               Music.note(1, new Pitch('B'), Instrument.PIANO)));
        thirdMeasure = Music.concat(thirdMeasure, 
                Music.together(Music.note(1, new Pitch('A').transpose(-2), Instrument.PIANO),
                               Music.note(1, new Pitch('B').transpose(2), Instrument.PIANO)));
        
        Music fourthMeasure = Music.rest(0);
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                               Music.note(0.5, new Pitch('B'), Instrument.PIANO)));
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.together(Music.note(0.5, new Pitch('A'), Instrument.PIANO),
                                              Music.note(1, new Pitch('B'), Instrument.PIANO)));
        fourthMeasure = Music.concat(fourthMeasure, 
                Music.note(0.5, new Pitch('A'), Instrument.PIANO));
        
        Music correctMusic = Music.concat(
                Music.concat(firstMeasure, secondMeasure), 
                Music.concat(thirdMeasure, fourthMeasure));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
    }
    
    // Covers the following:
    //
    // tuplets:
    //      is a duplet, triplet, quadruplet
    //      includes notes of different lengths, chords of different lengths
    @Test
    public void testPieceParserTuplets() throws UnableToParseException { 
        // Tests duplet
        String header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:4/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        String body = "[| (2AB A | (2[AB]C A |]" + "\n";
        
        // Parse the string 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure,
                Music.together(Music.note(1.5, new Pitch('A'), Instrument.PIANO),
                        Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, Music.note(1.5, new Pitch('B'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        Music secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, 
                Music.together(Music.note(1.5, new Pitch('A'), Instrument.PIANO),
                                            Music.note(1.5, new Pitch('B'), Instrument.PIANO)));
        secondMeasure = Music.concat(secondMeasure, Music.note(1.5, new Pitch('C'), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        Music correctMusic = Music.concat(firstMeasure, secondMeasure);
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // Tests triplet
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "[| (3A/2B/2C/2 A | (3[A/2B/2C/2]C/2D/2 A |]" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure,
                Music.together(
                        Music.note(1.0/3, new Pitch('A'), Instrument.PIANO),
                        Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, Music.note(1.0/3, new Pitch('B'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(1.0/3, new Pitch('C'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, 
                Music.together(Music.together(Music.note(1.0/3, new Pitch('A'), Instrument.PIANO), 
                                              Music.note(1.0/3, new Pitch('B'), Instrument.PIANO)), 
                                              Music.note(1.0/3, new Pitch('C'), Instrument.PIANO)));
        secondMeasure = Music.concat(secondMeasure, Music.note(1.0/3, new Pitch('C'), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, Music.note(1.0/3, new Pitch('D'), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        correctMusic = Music.concat(firstMeasure, secondMeasure);
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // Tests quadruplet
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:4/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "[| (4ABCD A | (4[AB]CDE A |]" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                Music.together(
                        Music.note(0.75, new Pitch('A'), Instrument.PIANO),
                        Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, Music.note(0.75, new Pitch('B'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(0.75, new Pitch('C'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(0.75, new Pitch('D'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, 
                Music.together(Music.note(0.75, new Pitch('A'), Instrument.PIANO),
                                                         Music.note(0.75, new Pitch('B'), Instrument.PIANO)));
        secondMeasure = Music.concat(secondMeasure, Music.note(0.75, new Pitch('C'), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, Music.note(0.75, new Pitch('D'), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, Music.note(0.75, new Pitch('E'), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        correctMusic = Music.concat(firstMeasure, secondMeasure);
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());     
    }
    
    // Covers the following:
    //
    // repeats:
    //      repeat starts at beginning of song, major section, begin repeat bar
    //      ending is normal, has alternate endings 
    @Test
    public void testPieceParserRepeats() throws UnableToParseException { 
        // Tests repeating from the beginning
        String header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:4/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        String body = "A B C D | D C B A :|" + "\n";
        
        // Parse the string 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(
                       firstMeasure, 
                       Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO), 
                               Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('B'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('C'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('D'), Instrument.PIANO));
        
        Music secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, Music.note(1, new Pitch('D'), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, Music.note(1, new Pitch('C'), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, Music.note(1, new Pitch('B'), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        Music correctMusic = Music.concat(Music.concat(firstMeasure, secondMeasure),
                                    Music.concat(firstMeasure, secondMeasure));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // Tests repeating from a major section
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "[| A B | C D || D C | B A :|" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                       Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                       Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('B'), Instrument.PIANO));
        
        secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, Music.note(1, new Pitch('C'), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, Music.note(1, new Pitch('D'), Instrument.PIANO));
        
        Music thirdMeasure = Music.rest(0);
        thirdMeasure = Music.concat(thirdMeasure, Music.note(1, new Pitch('D'), Instrument.PIANO));
        thirdMeasure = Music.concat(thirdMeasure, Music.note(1, new Pitch('C'), Instrument.PIANO));
        
        Music fourthMeasure = Music.rest(0);
        fourthMeasure = Music.concat(fourthMeasure, Music.note(1, new Pitch('B'), Instrument.PIANO));
        fourthMeasure = Music.concat(fourthMeasure, Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        Music firstMajorSection = Music.concat(firstMeasure, secondMeasure);
        Music secondMajorSection = Music.concat(thirdMeasure, fourthMeasure);
        correctMusic = Music.concat(firstMajorSection,
                                    Music.concat(secondMajorSection, secondMajorSection));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // Tests repeating from a begin repeat bar
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:2/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "A B |: C D :| E F" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                       Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                       Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('B'), Instrument.PIANO));
        
        secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, Music.note(1, new Pitch('C'), Instrument.PIANO));
        secondMeasure = Music.concat(secondMeasure, Music.note(1, new Pitch('D'), Instrument.PIANO));
        
        thirdMeasure = Music.rest(0);
        thirdMeasure = Music.concat(thirdMeasure, Music.note(1, new Pitch('E'), Instrument.PIANO));
        thirdMeasure = Music.concat(thirdMeasure, Music.note(1, new Pitch('F'), Instrument.PIANO));
        
        Music repeatMeasure = Music.concat(secondMeasure, secondMeasure);
        correctMusic = Music.concat(Music.concat(firstMeasure, repeatMeasure), thirdMeasure);
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // Tests alternate ending
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:4/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "|: C D E F |[1 G A B C | G A B B :|[2 F E D C |" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                       Music.together(Music.note(1, new Pitch('C'), Instrument.PIANO),
                       Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('D'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('E'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('F'), Instrument.PIANO));
        
        Music firstPass = Music.rest(0);
        firstPass = Music.concat(firstPass, Music.note(1, new Pitch('G'), Instrument.PIANO));
        firstPass = Music.concat(firstPass, Music.note(1, new Pitch('A'), Instrument.PIANO));
        firstPass = Music.concat(firstPass, Music.note(1, new Pitch('B'), Instrument.PIANO));
        firstPass = Music.concat(firstPass, Music.note(1, new Pitch('C'), Instrument.PIANO)); 
        firstPass = Music.concat(firstPass, Music.note(1, new Pitch('G'), Instrument.PIANO));
        firstPass = Music.concat(firstPass, Music.note(1, new Pitch('A'), Instrument.PIANO));
        firstPass = Music.concat(firstPass, Music.note(1, new Pitch('B'), Instrument.PIANO));
        firstPass = Music.concat(firstPass, Music.note(1, new Pitch('B'), Instrument.PIANO));
        
        Music secondPass = Music.rest(0);
        secondPass = Music.concat(secondPass, Music.note(1, new Pitch('F'), Instrument.PIANO));
        secondPass = Music.concat(secondPass, Music.note(1, new Pitch('E'), Instrument.PIANO));
        secondPass = Music.concat(secondPass, Music.note(1, new Pitch('D'), Instrument.PIANO));
        secondPass = Music.concat(secondPass, Music.note(1, new Pitch('C'), Instrument.PIANO));
        
        Music firstTimeThrough = Music.concat(firstMeasure, firstPass);
        Music secondTimeThrough = Music.concat(firstMeasure, secondPass);
        correctMusic = Music.concat(firstTimeThrough, secondTimeThrough);
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
    }
    
    // Covers the following:
    //
    // multiple voices:
    //      number of voices is 1, > 1
    @Test
    public void testPieceParserMultipleVoices() throws UnableToParseException { 
        // Tests 1 voice
        String header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:4/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "V:voice1" + "\n";
        header += "K:C" + "\n";
        String body = "V:voice1" + "\n";
        body += "A B C D" + "\n";
        
        // Parse the string 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(
                       firstMeasure, 
                       Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO), 
                               Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('B'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('C'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('D'), Instrument.PIANO));
        
        assertEquals("expected correct music", firstMeasure, piece.getMusic());
        
        // Tests >1 voices
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:4/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "V:voice1" + "\n";
        header += "V:voice2" + "\n";
        header += "V:voice3" + "\n";
        header += "K:C" + "\n";
        body = "V:voice1" + "\n";
        body += "A B C D" + "\n";
        body += "V:voice2" + "\n";
        body += "E E E E" + "\n";
        body += "V:voice3" + "\n";
        body += "F A F A" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music firstVoice = Music.rest(0);
        firstVoice = Music.concat(
                    firstVoice, 
                    Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO), 
                            Music.lyrics("*no lyrics*", "voice1")));
        firstVoice = Music.concat(firstVoice, Music.note(1, new Pitch('B'), Instrument.PIANO));
        firstVoice = Music.concat(firstVoice, Music.note(1, new Pitch('C'), Instrument.PIANO));
        firstVoice = Music.concat(firstVoice, Music.note(1, new Pitch('D'), Instrument.PIANO));
        
        Music secondVoice = Music.rest(0);
        secondVoice = Music.concat(
                    secondVoice, 
                    Music.together(Music.note(1, new Pitch('E'), Instrument.PIANO), 
                            Music.lyrics("*no lyrics*", "voice2")));
        secondVoice = Music.concat(secondVoice, Music.note(1, new Pitch('E'), Instrument.PIANO));
        secondVoice = Music.concat(secondVoice, Music.note(1, new Pitch('E'), Instrument.PIANO));
        secondVoice = Music.concat(secondVoice, Music.note(1, new Pitch('E'), Instrument.PIANO));
        
        Music thirdVoice = Music.rest(0);
        thirdVoice = Music.concat(
                thirdVoice, 
                Music.together(Music.note(1, new Pitch('F'), Instrument.PIANO), 
                        Music.lyrics("*no lyrics*", "voice3")));
        thirdVoice = Music.concat(thirdVoice, Music.note(1, new Pitch('A'), Instrument.PIANO));
        thirdVoice = Music.concat(thirdVoice, Music.note(1, new Pitch('F'), Instrument.PIANO));
        thirdVoice = Music.concat(thirdVoice, Music.note(1, new Pitch('A'), Instrument.PIANO));
        
        Music correctMusic = Music.together(firstVoice, secondVoice);
        correctMusic = Music.together(correctMusic, thirdVoice);
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
    }
    
    // Covers the following:
    //
    // lyrics:
    //      number of syllables is fewer, same, more than number of notes
    //      contains syllable held for more than one note, skipped notes, multiple words
    //          under one note, multiple syllables under one note 
    //      advancing to the next bar
    @Test
    public void testPieceParserLyrics() throws UnableToParseException {         
        // Tests number of syllables is same as number of notes
        String header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:3/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        String body = "A B C " + "\n";
        body += "w:syll-a-ble" + "\n";
        
        // Parse the string 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                                    Music.lyrics("*syll*-a-ble", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('B'), Instrument.PIANO),
                                    Music.lyrics("syll-*a*-ble", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('C'), Instrument.PIANO),
                                    Music.lyrics("syll-a-*ble*", "voice1")));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // Tests number of syllables is fewer than number of notes
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:4/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "A B C D" + "\n";
        body += "w:syll-a-ble" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                                    Music.lyrics("*syll*-a-ble", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('B'), Instrument.PIANO),
                                    Music.lyrics("syll-*a*-ble", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('C'), Instrument.PIANO),
                                    Music.lyrics("syll-a-*ble*", "voice1")));
        correctMusic = Music.concat(correctMusic,
                                    Music.together(Music.note(1, new Pitch('D'), Instrument.PIANO),
                                    Music.lyrics("*no lyrics*", "voice1")));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // Tests number of syllables is greater than number of notes
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:4/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "A B C D" + "\n";
        body += "w:syll-a-ble too many" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                                    Music.lyrics("*syll*-a-ble too many", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('B'), Instrument.PIANO),
                                    Music.lyrics("syll-*a*-ble too many", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('C'), Instrument.PIANO),
                                    Music.lyrics("syll-a-*ble* too many", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('D'), Instrument.PIANO),
                                    Music.lyrics("syll-a-ble *too* many", "voice1")));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // Tests syllable held for more than one note
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:4/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "A B C D" + "\n";
        body += "w:syll-a-_ble" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                                    Music.lyrics("*syll*-a-ble", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('B'), Instrument.PIANO),
                                    Music.lyrics("syll-*a*-ble", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.note(1, new Pitch('C'), Instrument.PIANO));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('D'), Instrument.PIANO),
                                    Music.lyrics("syll-a-*ble*", "voice1")));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // Tests skipped noted
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:3/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "A B C" + "\n";
        body += "w:two * words" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                                    Music.lyrics("*two* words", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('B'), Instrument.PIANO),
                                    Music.lyrics("*no lyrics*", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('C'), Instrument.PIANO),
                                    Music.lyrics("two *words*", "voice1")));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // Tests for multiple words under one note
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:3/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "A B C" + "\n";
        body += "w:the test lyrics~of~the~day" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                                    Music.lyrics("*the* test lyrics of the day", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('B'), Instrument.PIANO),
                                    Music.lyrics("the *test* lyrics of the day", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('C'), Instrument.PIANO),
                                    Music.lyrics("the test *lyrics of the day*", "voice1")));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // Tests for multiple syllables under one note
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:3/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "A B C" + "\n";
        body += "w:test of syll\\-a\\-ble" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        correctMusic = Music.rest(0);
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                                    Music.lyrics("*test* of syll-a-ble", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('B'), Instrument.PIANO),
                                    Music.lyrics("test *of* syll-a-ble", "voice1")));
        correctMusic = Music.concat(correctMusic, 
                                    Music.together(Music.note(1, new Pitch('C'), Instrument.PIANO),
                                    Music.lyrics("test of *syll-a-ble*", "voice1")));
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
        // Tests for advancing to the next bar
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:3/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "A B C | A B C" + "\n";
        body += "w:test | test of day" + "\n";
        
        // Parse the string 
        piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(firstMeasure, 
                                    Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                                    Music.lyrics("*test* test of day", "voice1")));
        firstMeasure = Music.concat(firstMeasure, 
                                    Music.together(Music.note(1, new Pitch('B'), Instrument.PIANO),
                                    Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, 
                                    Music.note(1, new Pitch('C'), Instrument.PIANO));
        Music secondMeasure = Music.rest(0);
        secondMeasure = Music.concat(secondMeasure, 
                                    Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO),
                                    Music.lyrics("test *test* of day", "voice1")));
        secondMeasure = Music.concat(secondMeasure, 
                                    Music.together(Music.note(1, new Pitch('B'), Instrument.PIANO),
                                    Music.lyrics("test test *of* day", "voice1")));
        secondMeasure = Music.concat(secondMeasure, 
                                    Music.together(Music.note(1, new Pitch('C'), Instrument.PIANO),
                                    Music.lyrics("test test of *day*", "voice1")));
        correctMusic = Music.concat(firstMeasure, secondMeasure);
        
        assertEquals("expected correct music", correctMusic, piece.getMusic());
        
    }
    
    // Covers the following:
    //
    // comments:
    //      comments on a new line, comments at the end of a line
    //      comments in the header, comments in the body
    @Test
    public void testPieceParserComments() throws UnableToParseException {
        String header = "X:1" + "\n";
        header += "% comment1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:4/4" + "%comment2" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "V:voice1" + "\n";
        header += "K:C" + "\n";
        String body = "V:voice1" + "%comment3" + "\n";
        body += "%comment4" + "\n";
        body += "A B C D" + "\n";
        
        // Parse the string 
        Piece piece = PieceParser.parse(header + body);
        
        // Create the correct music 
        Music firstMeasure = Music.rest(0);
        firstMeasure = Music.concat(
                        firstMeasure, 
                        Music.together(Music.note(1, new Pitch('A'), Instrument.PIANO), 
                                Music.lyrics("*no lyrics*", "voice1")));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('B'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('C'), Instrument.PIANO));
        firstMeasure = Music.concat(firstMeasure, Music.note(1, new Pitch('D'), Instrument.PIANO));
        
        assertEquals("expected correct music", firstMeasure, piece.getMusic());
    }
    
    // checked exceptions thrown:
    //      none, UnableToParseException
    @Test
    public void testCheckedExceptions() {
        // A string the parser can parse 
        String header = "X:1" + "\n";
        header += "% comment1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:4/4" + "%comment2" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "V:voice1" + "\n";
        header += "K:C" + "\n";
        String body = "V:voice1" + "%comment3" + "\n";
        body += "%comment4" + "\n";
        body += "A B C D" + "\n";
        
        // Parse the valid string 
        try {
            PieceParser.parse(header + body);
        } catch (UnableToParseException e) {
            // no exceptions should be thrown 
            fail();
        }
        
        // Parse an invalid string 
        try {
            PieceParser.parse("Piece.java");
            // Fail the test if it does not throw an exception 
            fail();
        } catch (UnableToParseException e) {
            // Should throw an UnableToParseException
            assert true;
        }
    }
    
}
