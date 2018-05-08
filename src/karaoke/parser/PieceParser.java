package karaoke.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.Parser;
import edu.mit.eecs.parserlib.UnableToParseException;
import karaoke.Music;
import karaoke.Piece;

/**
 * Parser for our Karaoke project pieces of music written in standard abc notation
 * Inspiration for this code framework comes from instructor-written ps3, ExpressionParser.java
 */
public class PieceParser {
    /**
     * Main method. Parses and then reprints an example piece.
     * 
     * @param args command line arguments, not used
     * @throws UnableToParseException if example expression can't be parsed
     */
    public static void main(final String[] args) throws UnableToParseException {
        throw new RuntimeException("Unimplemented");
    }
    
    // the nonterminals of the grammar
    private static enum PieceGrammar {
        ABC_TUNE,
        ABC_HEADER,
        FIELD_NUMBER,
        FIELD_TITLE,
        OTHER_FIELDS,
        FIELD_COMPOSER,
        FIELD_DEFAULT_LENGTH,
        FIELD_METER,
        FIELD_TEMPO,
        FIELD_VOICE,
        FIELD_KEY,
        KEY,
        KEYNOTE,
        KEY_ACCIDENTAL,
        MODE_MINOR,
        METER,
        METER_FRACTION,
        TEMPO,
        ABC_BODY,
        ABC_LINE,
        ELEMENT,
        NOTE_ELEMENT,
        NOTE,
        PITCH,
        OCTAVE,
        NOTE_LENGTH,
        NOTE_LENGTH_STRICT,
        ACCIDENTAL,
        BASENOTE,
        REST_ELEMENT,
        TUPLET_ELEMENT,
        TUPLET_SPEC,
        CHORD,
        BARLINE,
        NTH_REPEAT,
        MIDDLE_OF_BODY_FIELD,
        LYRIC,
        LYRICAL_ELEMENT,
        LYRIC_TEXT,
        COMMENT,
        COMMENT_TEXT,
        END_OF_LINE,
        DIGIT,
        NEWLINE,
        SPACE_OR_TAB, 
        TEXT, 
        BACKSLASH_HYPHEN
    }
    
    private static Parser<PieceGrammar> parser = makeParser();
    
    /**
     * Compile the grammar into a parser.
     * 
     * @return parser for the grammar
     * @throws RuntimeException if grammar file can't be read or has syntax errors
     */
    private static Parser<PieceGrammar> makeParser() {
        try {
            // read the grammar as a file, relative to the project root.
            final File grammarFile = new File("src/karaoke/parser/Abc.g");
            return Parser.compile(grammarFile, PieceGrammar.ABC_TUNE);
        } catch (IOException e) {
            throw new RuntimeException("can't read the grammar file", e);
        } catch (UnableToParseException e) {
            throw new RuntimeException("the grammar has a syntax error", e);
        }
    }
    
    /**
     * Parse a string into an abc piece.
     * @param string string to parse
     * @return Music parsed from the string
     * @throws UnableToParseException if the string doesn't match the Abc grammar
     */
    public static Piece parse(final String string) throws UnableToParseException {
        // parse the example into a parse tree
        final ParseTree<PieceGrammar> parseTree = parser.parse(string);
        // make an AST from the parse tree
        final Piece piece = makeAbstractSyntaxTree(parseTree);
        return piece;
    }
    
    /**
     * Convert a parse tree into an abstract syntax tree.
     * 
     * @param parseTree constructed according to the grammar in Abc.g
     * @return abstract syntax tree corresponding to parseTree
     */
    private static Piece makeAbstractSyntaxTree(final ParseTree<PieceGrammar> parseTree) {
        if(parseTree.name().equals(PieceGrammar.ABC_TUNE)) {
            // Split into header and body 
            final ParseTree<PieceGrammar> headerTree = parseTree.children().get(0);
            final ParseTree<PieceGrammar> bodyTree = parseTree.children().get(1);

            // Initialize some values 
            String composer = "Unknown";
            int index = 0;
            final double invalidDuration = -1;
            double defaultNoteDuration = invalidDuration;
            String meter = "4/4";
            String tempo = "";
            final int defaultBeatsPerMinute = 100;
            int beatsPerMinute = defaultBeatsPerMinute;
            String title = "";
            Set<String> voices = new HashSet<>();
            String key = "C";
            
            // Start parsing the header 
            final List<ParseTree<PieceGrammar>> headerFields = headerTree.children();
            for(int i = 0; i < headerFields.size(); i++) {
                ParseTree<PieceGrammar> givenField = headerFields.get(i);
                List<ParseTree<PieceGrammar>> givenFieldChildren = givenField.children();
                switch(givenField.name()) {
                case FIELD_NUMBER:
                    String digits = "";
                    for(int j = 0; j < givenFieldChildren.size() - 1; j++) {
                        digits += givenFieldChildren.get(j).text();
                    }
                    index = Integer.parseInt(digits);
                    break;
                case FIELD_TITLE:
                    title = givenFieldChildren.get(0).text();
                    break;
                case OTHER_FIELDS:
                    final ParseTree<PieceGrammar> otherField = givenField.children().get(0);
                    switch(otherField.name()) {
                    case FIELD_COMPOSER: 
                        composer = otherField.children().get(0).text();
                        break;
                    case FIELD_DEFAULT_LENGTH:
                        String noteLength = otherField.children().get(0).text();
                        defaultNoteDuration = fractionToDouble(noteLength);
                        break;
                    case FIELD_METER:
                        meter = otherField.children().get(0).text();
                        break;
                    case FIELD_TEMPO:
                        tempo = otherField.children().get(0).text();
                        break;
                    case FIELD_VOICE:
                        String voice = otherField.children().get(0).text();
                        voices.add(voice);
                        break;
                    case COMMENT:
                        break;
                    default:
                        throw new AssertionError("Should never get here.");
                    }
                    break;
                case FIELD_KEY:
                    key = givenFieldChildren.get(0).text();
                    break;
                case COMMENT:
                    break;
                default:
                    throw new AssertionError("Should never get here.");
                }
            }
            
            // Handle special cases
            if(meter.equals("C")) {
                meter = "4/4";
            } 
            else if(meter.equals("C|")) {
                meter = "2/2";
            }
            
            // Handle cases where things were missing from header 
            if(defaultNoteDuration==invalidDuration) {
                double meterDouble = fractionToDouble(meter);
                final double meterCutoff = 0.75;
                final double smallNoteDuration = 1.0/16.0;
                final double largeNoteDuration = 1.0/8.0; 
                if(meterDouble < meterCutoff) {
                    defaultNoteDuration = smallNoteDuration;
                }
                else {
                    defaultNoteDuration = largeNoteDuration;
                }
            }
            
            if(!tempo.equals("")) {
                tempo = tempo.replaceAll("//s", "");
                String[] tempoArr = tempo.split("=");
                double beatLength = fractionToDouble(tempoArr[0]);
                int numBeats = Integer.parseInt(tempoArr[1]);
                double defaultNotesInOneBeat = beatLength/defaultNoteDuration;
                beatsPerMinute = (int) Math.round(defaultNotesInOneBeat * numBeats);
            }
            
            if(voices.isEmpty()) {
                voices.add("voice1");
            }
            
            // Parse the body to get the Music object 
            Music music = parseBody(bodyTree, voices, defaultNoteDuration, key);
            
            return new Piece(composer, index, defaultNoteDuration, meter, beatsPerMinute, title, voices, key, music);
        } 
        else {
            throw new RuntimeException("Should never get here");
        }
    }
    
    /**
     * Converts a fraction represented as a string into a double
     * @param fraction represented as a string, may include white-space
     * @return the fraction simplified as a double 
     */
    private static double fractionToDouble(String fraction) {
        fraction = fraction.replace("//s", "");
        String[] fractionArr = fraction.split("/"); 
        double numerator = Double.parseDouble(fractionArr[0]);
        double denominator = Double.parseDouble(fractionArr[1]);
        return numerator/denominator;
    }
    
    /**
     * Parses the body of the grammar into a Music object 
     * @param bodyTree created from parsing Abc.g
     * @param voices the set of voices in the body
     * @param defaultNoteLength the default length for a note 
     * @param key the key signature for the piece of music
     * @return the Music object corresponding to the bodyTree
     */
    private static Music parseBody(final ParseTree<PieceGrammar> bodyTree, 
            Set<String>voices, double defaultNoteLength, String key) {
        throw new RuntimeException("Unimplemented");
    }
    
}
