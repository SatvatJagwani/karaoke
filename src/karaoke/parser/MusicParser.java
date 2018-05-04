package karaoke.parser;

import java.io.File;
import java.io.IOException;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.Parser;
import edu.mit.eecs.parserlib.UnableToParseException;
import karaoke.Music;

/**
 * Parser for our Karaoke Music project
 * Inspiration for this code framework comes from instructor-written ps3, ExpressionParser.java
 */
public class MusicParser {
    /**
     * Main method. Parses and then reprints an example music.
     * 
     * @param args command line arguments, not used
     * @throws UnableToParseException if example expression can't be parsed
     */
    public static void main(final String[] args) throws UnableToParseException {
        throw new RuntimeException("Unimplemented");
    }
    
 // the nonterminals of the grammar
    private static enum MusicGrammar {
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
        SPACE_OR_TAB
    }
    
    private static Parser<MusicGrammar> parser = makeParser();
    
    /**
     * Compile the grammar into a parser.
     * 
     * @return parser for the grammar
     * @throws RuntimeException if grammar file can't be read or has syntax errors
     */
    private static Parser<MusicGrammar> makeParser() {
        try {
            // read the grammar as a file, relative to the project root.
            final File grammarFile = new File("src/karaoke/Abc.g");
            return Parser.compile(grammarFile, MusicGrammar.ABC_TUNE);
        } catch (IOException e) {
            throw new RuntimeException("can't read the grammar file", e);
        } catch (UnableToParseException e) {
            throw new RuntimeException("the grammar has a syntax error", e);
        }
    }
    
    /**
     * Parse a string into an abc music.
     * @param string string to parse
     * @return Music parsed from the string
     * @throws UnableToParseException if the string doesn't match the Abc grammar
     */
    public static Music parse(final String string) throws UnableToParseException {
        // parse the example into a parse tree
        final ParseTree<MusicGrammar> parseTree = parser.parse(string);
        // make an AST from the parse tree
        final Music music = makeAbstractSyntaxTree(parseTree);
        return music;
    }
    
    /**
     * Convert a parse tree into an abstract syntax tree.
     * 
     * @param parseTree constructed according to the grammar in Abc.g
     * @return abstract syntax tree corresponding to parseTree
     */
    private static Music makeAbstractSyntaxTree(final ParseTree<MusicGrammar> parseTree) {
        throw new RuntimeException("Unimplemented");
    }
    
}
