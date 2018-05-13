package karaoke.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.Parser;
import edu.mit.eecs.parserlib.UnableToParseException;
import karaoke.Music;
import karaoke.Piece;
import karaoke.sound.Instrument;
import karaoke.sound.Pitch;

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
        String header = "";
        String body = "";
        header = "X:1" + "\n";
        header += "T:simple song" + "\n";
        header += "M:3/4" + "\n";
        header += "L:1/4" + "\n";
        header += "Q:1/4=100" + "\n";
        header += "K:C" + "\n";
        body = "A B C | A B C" + "\n";
        body += "w:test | test of day" + "\n";
        
        // Parse the string 
        PieceParser.parse(header + body);
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
        INDEX,
        WORD,
        SPACE,
        SEPARATOR,
        CHUNK,
        MULTIPLE_SYLLABLES,
        MULTIPLE_WORDS, 
        UNDERSCORES, 
        HYPHENS, 
        TILDE, 
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
                switch(givenField.name()) {
                case FIELD_NUMBER:
                    index = Integer.parseInt(givenField.children().get(0).text());
                    break;
                case FIELD_TITLE:
                    title = givenField.children().get(0).text();
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
                    key = givenField.children().get(0).text();
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
        // Split the string on "/" then divide numerator by denominator 
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
        // Put the Music for each voice into a list a pairs of form (voice, Music)
        List<SimpleImmutableEntry<String, Music>> voicesMusic = new ArrayList<>();
        for (String voice : voices) {
            List<ParseTree<PieceGrammar>> voiceBody = extractVoiceBody(bodyTree, voice, voices);
            Music voiceMusic = getMusicForVoice(voiceBody, voice, defaultNoteLength, key);
            voicesMusic.add(new SimpleImmutableEntry<>(voice, voiceMusic));
        }
        
        // Sort the list by duration (largest first), then by voice names (alphabetically) 
        voicesMusic.sort((pair1, pair2) -> {
            String voice1 = pair1.getKey();
            String voice2 = pair2.getKey();
            double duration1 = pair1.getValue().duration();
            double duration2 = pair2.getValue().duration();
            if (duration1 > duration2) {
                // Larger durations go to the front of the list 
                return -1;
            } 
            else if (duration1 < duration2) {
                return 1;
            }
            else {
                return voice1.compareTo(voice2);
            }
        });
        
        // Together all the Music objects in the list 
        Music bodyMusic = voicesMusic.get(0).getValue();
        for (int i = 1; i < voicesMusic.size(); i++) {
            bodyMusic = Music.together(bodyMusic, voicesMusic.get(i).getValue());
        }
        
        return bodyMusic;
    }
    
    /**
     * Extracts the lines in the bodyTree corresponding to the given voice
     * @param bodyTree the parse tree for the body of the abc file
     * @param voice the voice whose lines are getting extracted
     * @return a list of the lines corresponding to a given voice in the order in which they 
     *         appear in the abc file 
     */
    private static List<ParseTree<PieceGrammar>> extractVoiceBody(ParseTree<PieceGrammar> bodyTree, String voice, 
            Set<String> voices) {
        List<ParseTree<PieceGrammar>> abcLines = bodyTree.children();

        // Populate the voice body 
        List<ParseTree<PieceGrammar>> voiceBody = new ArrayList<>();
        boolean shouldAddLine = false; 
        for (ParseTree<PieceGrammar> abcLine : abcLines) {
            if (abcLine.children().get(0).name() != PieceGrammar.COMMENT) {
                if (voices.size() == 1) {
                    // Only one voice, so add everything except comments and middle of body fields 
                    if (abcLine.children().get(0).name() != PieceGrammar.MIDDLE_OF_BODY_FIELD) {
                        voiceBody.add(abcLine);
                    }
                } else {
                    // More than once voice
                    if (abcLine.children().get(0).name() == PieceGrammar.MIDDLE_OF_BODY_FIELD) {
                        // If the line has a voice field, get the voice 
                        ParseTree<PieceGrammar> middleOfBodyField = abcLine.children().get(0);
                        ParseTree<PieceGrammar> fieldVoice = middleOfBodyField.children().get(0);
                        String voiceIdentifier = fieldVoice.children().get(0).text();

                        // If the voice matches the given voice, then we should add the next lines
                        if (voiceIdentifier.equals(voice)) {
                            shouldAddLine = true;
                        } else {
                            shouldAddLine = false;
                        }
                    } else {
                        // Add lines that are not voice fields or comments, if preceded by the correct voice  
                        if (shouldAddLine) {
                            voiceBody.add(abcLine);
                        }
                    }
                }
            }
        }
        
        return voiceBody;
    }

    /**
     * Gets the Music for a single voice whose lines are given in voiceBody
     * @param voiceBody a list of lines from an abc file to convert into a Music object
     * @param voice the name of the voice whose lines are in voiceBody
     * @param defaultNoteLength the default note length of the piece 
     * @param key the key signature of the piece 
     * @return the Music object for the given voice 
     */
    private static Music getMusicForVoice(List<ParseTree<PieceGrammar>> voiceBody, String voice, double defaultNoteLength,
            String key) {
        List<SimpleImmutableEntry<String,Music>> fullVoiceBody = new ArrayList<>();
        
        // Fill in fullVoiceBody line by line, keep accidentals outside the scope of the for loop
        Map<String, Pitch> accidentals = new HashMap<>();
        for (ParseTree<PieceGrammar> abcLine: voiceBody) {
            List<SimpleImmutableEntry<String, Music>> linePart = parseBodyLine(abcLine, voice, key, accidentals);
            fullVoiceBody.addAll(linePart);
        }
        
        // Compress the voiceMusic (takes care of measures, major sections, and repeats)
        Music voiceMusic = compress(fullVoiceBody);
        return voiceMusic;
    }
    
    /**
     * Parses a single abc line of the abc grammar body for a given voice
     * @param abcLine the line of the abc file to parse
     * @param voice the voice of the line
     * @param key the key signature of the piece
     * @param accidentals the list the accidentals found in this measure that is mutated by this method
     * @return a list of pairs where the first element in pair indicates the structure of the song
     *         (either the type of bar/repeat or the music) and the second element is the Music
     *         corresponding to the first element where that is a rest of duration zero if the first
     *         element is a bar/repeat
     */
    private static List<SimpleImmutableEntry<String, Music>> parseBodyLine(ParseTree<PieceGrammar> abcLine, 
            String voice, String key, Map<String, Pitch> accidentals) {
        // First, parse the elements in the body, store them in parsedBodyLine
        List<SimpleImmutableEntry<String, Music>> parsedBodyLine = new ArrayList<>();
        boolean lineContainsLyrics = false;
        
        for (ParseTree<PieceGrammar> element: abcLine.children()) {
            if (element.name()==PieceGrammar.ELEMENT) {
                ParseTree<PieceGrammar> subelement = element.children().get(0);
                switch(subelement.name()) {
                case NOTE_ELEMENT:
                    // Note stored as ("music", Note), Note could be a single note or a chord 
                    ParseTree<PieceGrammar> noteElement = subelement;
                    Music noteElementMusic = parseNoteElement(noteElement, key, accidentals, 1.0);
                    SimpleImmutableEntry<String, Music> noteElementPair
                                = new SimpleImmutableEntry<>("music", noteElementMusic);
                    parsedBodyLine.add(noteElementPair);
                    break;
                case REST_ELEMENT:
                    // Rest stored as ("rest", Rest)
                    double duration;
                    ParseTree<PieceGrammar> noteLength = subelement.children().get(0);
                    if (noteLength.text().equals("")) {
                        duration = 1.0;
                    }
                    else {
                        duration = parseNoteLength(noteLength);
                    }
                    Music rest = Music.rest(duration);
                    SimpleImmutableEntry<String, Music> restPair
                                = new SimpleImmutableEntry<>("rest", rest);
                    parsedBodyLine.add(restPair);
                    break;
                case TUPLET_ELEMENT:
                    // Tuplet is the same as 2, 3, or 4 consecutive NOTE_ELEMENT
                    // So treat it the same way, just change the multiplier in the duration 
                    ParseTree<PieceGrammar> tupletSpec = subelement.children().get(0);
                    String tupletType = tupletSpec.children().get(0).text();
                    double multiplier;
                    final double dupletMultiplier = 3.0/2.0;
                    final double tripletMultiplier = 2.0/3.0;
                    final double quadrupletMultiplier = 3.0/4.0;
                    switch(tupletType) {
                    case "2":
                        multiplier = dupletMultiplier;
                        break;
                    case "3":
                        multiplier = tripletMultiplier;
                        break;
                    case "4":
                        multiplier = quadrupletMultiplier;
                        break;
                    default:
                        throw new AssertionError("Should never get here");
                    }
                    
                    // Add each note in the tuplet as ("music", Note), Note could be a single note or a chord 
                    for (int i=1; i<subelement.children().size(); ++i) {
                        ParseTree<PieceGrammar> tupletNoteElement = subelement.children().get(i);
                        Music tupletMusic = parseNoteElement(tupletNoteElement, key, accidentals, multiplier);
                        SimpleImmutableEntry<String, Music> tupletPair
                                    = new SimpleImmutableEntry<>("music", tupletMusic);
                        parsedBodyLine.add(tupletPair);
                    }
                    break;
                case BARLINE:
                    // Add a barline as (b, Rest(0)), where b is in {"|", "||", "[|", "|]", ":|", "|:"} 
                    SimpleImmutableEntry<String, Music> barline 
                                = new SimpleImmutableEntry<>(subelement.text(), Music.rest(0));
                    parsedBodyLine.add(barline);
                    accidentals = new HashMap<String, Pitch>();
                    break;
                case NTH_REPEAT:
                    // Add nth_repeat as (r, Rest(0)), where r is in {"[1", "[2"}
                    SimpleImmutableEntry<String, Music> repeat 
                                = new SimpleImmutableEntry<>(subelement.text(), Music.rest(0));
                    parsedBodyLine.add(repeat);
                    break;
                case SPACE_OR_TAB:
                    // Don't do anything if we see space or tab 
                    break;
                default:
                    throw new AssertionError("Should never get here");
                }
            } else if (element.name()==PieceGrammar.LYRIC) {
                // There is a lyric in the grammar 
                lineContainsLyrics = true;
            }
        }
        
        // If there was a lyric in the grammar, parse it in a helper method 
        List<SimpleImmutableEntry<String, Integer>> parsedLyric = new ArrayList<>();
        if (lineContainsLyrics) {
            ParseTree<PieceGrammar> lyric = abcLine.children().get(abcLine.children().size() - 2);
            parsedLyric = parseLyric(lyric); 
        } 
        
        // Add the parsedLyric to the parsedBodyLine
        List<SimpleImmutableEntry<String, Music>> combinedMusicAndLyrics = addLyricToBodyLine(parsedBodyLine, parsedLyric, voice);
        
        return combinedMusicAndLyrics;
    }
    
    /**
     * Combines the parsedLyricLine with the parsedBodyLine
     * @param parsedBodyLine a list of pairs where the first element in pair indicates the structure of the song
     *         (either the type of bar/repeat or the music) and the second element is the Music
     *         corresponding to the first element where that is a rest of duration zero if the first
     *         element is a bar/repeat. Here none of the Music elements have lyrics attached to them. 
     * @param parsedLyric the lyrics to be added to the parsedBodyLine, the result
     *        from the parseLyric method
     * @param voice the voice of the lyrics to be sung
     * @return a list of pairs of the same form as parsedBodyLine that has lyrics 
     *         combined with the correct music objects using the Music.together method
     */
    private static List<SimpleImmutableEntry<String, Music>> addLyricToBodyLine(
            List<SimpleImmutableEntry<String, Music>> parsedBodyLine,
            List<SimpleImmutableEntry<String, Integer>> parsedLyric, String voice) {

        // First, convert the parsedLyric into another list where each element in the new list
        // is of the form (lyricLine, Boolean) and if the Boolean is true, then we will add 
        // the pair to one of the Music elements in parsedBodyLine. Allows duplicated lyricLines whenever
        // their length is greater than 1, but only the first on has a true Boolean. 
        List<SimpleImmutableEntry<String, Boolean>> parsedLyricBoolean = new ArrayList<>();
        for (SimpleImmutableEntry<String, Integer> lyricPair : parsedLyric) {
            if (lyricPair.getValue() == 0) {
                // Bars are always false
                SimpleImmutableEntry<String, Boolean> barPair = new SimpleImmutableEntry<>("|", false);
                parsedLyricBoolean.add(barPair);
            }
            else {
                for (int i=0; i < lyricPair.getValue(); i++) {
                    // Lyrics are only true for the first occurrence of the lyric
                    SimpleImmutableEntry<String, Boolean> lyricPairBoolean = 
                                new SimpleImmutableEntry<>(lyricPair.getKey(), i == 0);
                    parsedLyricBoolean.add(lyricPairBoolean);
                }
            }
        }
        
        // Combine parsedLyricBoolean with parsedBodyLine 
        int index = 0;
        boolean waitingForMusicBar = false;
        boolean needToAddNoLyrics = true;
        List<SimpleImmutableEntry<String, Music>> combinedMusicAndLyrics = new ArrayList<>();
        
        for (SimpleImmutableEntry<String, Music> musicPair : parsedBodyLine) {
            String label = musicPair.getKey();
            
            if (index < parsedLyricBoolean.size()) { 
                // Check to see if the lyrics are on a music bar 
                if (parsedLyricBoolean.get(index).getKey().equals("|") &&
                    !waitingForMusicBar) {
                    waitingForMusicBar = true;
                    needToAddNoLyrics = true;
                }
            }
            
            if (label.equals("music")) {
                // We are looking at a music object in the parsedBodyLine 
                Music music = musicPair.getValue();
                SimpleImmutableEntry<String, Music> noLyrics = new SimpleImmutableEntry<>(label, 
                        Music.together(music, Music.lyrics("*no lyrics*", voice)));

                if (index == parsedLyricBoolean.size()) {
                    // We have looked through all the lyrics, so we add one noLyrics at the end
                    combinedMusicAndLyrics.add(noLyrics);
                    // Skip the rest of the loop because there are no more lyrics to look at 
                    index++;
                    continue;
                } else if (index > parsedLyricBoolean.size()) {
                    // We have looked through all the lyrics and we don't add any more noLyrics 
                    combinedMusicAndLyrics.add(musicPair);
                    // Skip the rest of the loop because there are no more lyrics to look at 
                    index++;
                    continue;
                }
                
                if (!waitingForMusicBar) {
                    // If the lyrics are not stuck on a music bar in lyrics
                    SimpleImmutableEntry<String, Boolean> lyricPairBoolean = parsedLyricBoolean.get(index);
                    String lyricLine = lyricPairBoolean.getKey();
                    if (lyricPairBoolean.getValue()) {
                        // Combine the music and lyric line if the boolean for the lyric is true 
                        SimpleImmutableEntry<String, Music> musicWithLyrics = new SimpleImmutableEntry<>(
                                label, Music.together(music, Music.lyrics(lyricLine, voice)));
                        combinedMusicAndLyrics.add(musicWithLyrics);
                    } 
                    else {
                        // Otherwise, just use the original music
                        combinedMusicAndLyrics.add(musicPair);
                    }
                    // Increment the index whenever we are not stuck on a music bar in lyrics
                    index++;
                }
                else {
                    if (needToAddNoLyrics) {
                        // Stuck on the music bar in lyrics and need to add noLyrics once
                        combinedMusicAndLyrics.add(noLyrics);
                        needToAddNoLyrics = false;
                    } 
                    else {
                        // Stuck on the music bar in lyrics and don't need to add noLyrics
                        combinedMusicAndLyrics.add(musicPair);
                    }
                }
            }
            else {
                // We are looking at a rest, bar, or repeat
                combinedMusicAndLyrics.add(musicPair);

                if (waitingForMusicBar && !label.equals("rest")) {
                    // Stuck on a music bar in lyrics and found a music bar in the body
                    waitingForMusicBar = false;
                    needToAddNoLyrics = false;
                    // Increment the index so we are not stuck on a music bar in lyrics anymore
                    index++;
                }
            }
        }
        
        return combinedMusicAndLyrics;
    }

    /**
     * Converts a grammar representation of a note length into a double
     * @param noteLength the abc grammar representation of a note length
     * @return a double referring to the same note length
     */
    private static double parseNoteLength(ParseTree<PieceGrammar> noteLength) {
        String noteLengthString = noteLength.text();
        String fullFraction;
        if (!noteLengthString.contains("/")) {
            // The noteLength is just a number
            fullFraction = noteLengthString + "/1";
        }
        else if (noteLengthString.length() == 1) {
            // The noteLength is just /
            fullFraction = "1/2";
        }
        else if (noteLengthString.charAt(0)=='/') {
            // The noteLength starts with / 
            fullFraction = "1" + noteLengthString;
        }
        else if (noteLengthString.charAt(noteLengthString.length()-1) == '/') {
            // The noteLength ends with /
            fullFraction = noteLengthString + "2";
        }
        else {
            // The noteLength is a full fraction 
            fullFraction = noteLengthString;
        }
        return fractionToDouble(fullFraction);
    }
    
    /**
     * Converts the grammar representation of a note element into a Music object
     * @param noteElement the abc grammar representation of a note element
     * @param key the key signature of the piece 
     * @param accidentals the accidentals in the measure where the note element is, if the given 
     *        noteElement contains an accidental, this method will mutate accidentals 
     * @return the Music object corresponding to noteElement 
     */
    private static Music parseNoteElement(ParseTree<PieceGrammar> noteElement, 
            String key, Map<String, Pitch> accidentals, double multiplier) {
        ParseTree<PieceGrammar> noteOrChord = noteElement.children().get(0);
        switch(noteOrChord.name()) {
        case NOTE:
            // Just return the parsed note
            ParseTree<PieceGrammar> note = noteOrChord;
            return parseNote(note, key, accidentals, multiplier);
        case CHORD:
            // Together all of the notes in the chord, parsing each note on its own
            ParseTree<PieceGrammar> chord = noteOrChord;
            Music chordMusic = parseNote(chord.children().get(0), key, accidentals, multiplier);
            for (int i=1; i<chord.children().size(); ++i) {
                chordMusic = Music.together(chordMusic, parseNote(chord.children().get(i), key, accidentals, multiplier));
            }
            return chordMusic;
        default:
            throw new AssertionError("Should never get here");
        }
    }
    
    /**
     * Converts the grammar representation of a note into a Music object
     * @param note the abc grammar representation of a note element
     * @param key the key signature of the piece 
     * @param accidentals the accidentals in the measure where the note is, if the given 
     *        note contains an accidental, this method will mutate accidentals 
     * @return the Music object corresponding to note 
     */
    private static Music parseNote(ParseTree<PieceGrammar> note, String key, 
                                   Map<String, Pitch> accidentals, double multiplier) {
        // Parse the duration 
        double duration;
        ParseTree<PieceGrammar> noteLength = note.children().get(1);
        if (noteLength.text().equals("")) {
            // No noteLength was given, so default length is 1 (include multiplier)
            duration = 1.0 * multiplier;
        }
        else {
            // Parse the given noteLength (include multiplier) 
            duration = parseNoteLength(noteLength) * multiplier;
        }
        
        // Parse the pitch
        Music finalNote;
        ParseTree<PieceGrammar> pitch = note.children().get(0);
        if (pitch.children().get(0).name() == PieceGrammar.ACCIDENTAL) {
            // The note is an accidental, so ignore the given key signature 
            String accidental = pitch.children().get(0).text();
            String baseNote = pitch.children().get(1).text();
            String baseNoteUpper = baseNote.toUpperCase();
            Map<String, Pitch> keyMapC = getKeySignatureMap("C");
            
            // Get the octaves if they were given 
            String octave = "";
            final int maxPitchSize = 3;
            if (pitch.children().size() == maxPitchSize) {
                octave = pitch.children().get(2).text();
            }
            
            // Get the pitch of the accidental, ignoring the octaves 
            Pitch accidentalPitch;
            switch(accidental) {
            case "^":
                accidentalPitch = keyMapC.get(baseNoteUpper).transpose(1);
                break;
            case "^^":
                accidentalPitch = keyMapC.get(baseNoteUpper).transpose(2);
                break;
            case "=":
                accidentalPitch = keyMapC.get(baseNoteUpper);
                break;
            case "_":
                accidentalPitch = keyMapC.get(baseNoteUpper).transpose(-1);
                break;
            case "__":
                accidentalPitch = keyMapC.get(baseNoteUpper).transpose(-1*2);
                break;
            default:
                throw new AssertionError("Should never get here");
            }
            
            // Modify based on octaves
            for (int i=0; i<octave.length(); ++i) {
                char suboctave = octave.charAt(i);
                if (suboctave == '\'') {
                    accidentalPitch = accidentalPitch.transpose(Pitch.OCTAVE);
                }
                else if (suboctave == ',') {
                    accidentalPitch = accidentalPitch.transpose(-Pitch.OCTAVE);
                }
            }
            
            // If the note was lower-case, raise it another octave 
            if (!baseNote.equals(baseNoteUpper)) {
                accidentalPitch = accidentalPitch.transpose(Pitch.OCTAVE);
            }
            
            // Create the final note and add the accidental to the accidentals map 
            finalNote = Music.note(duration, accidentalPitch, Instrument.PIANO);
            accidentals.put(baseNote+octave, accidentalPitch);
        }
        else {
            // The note is not marked as an accidental, so use given key-signature 
            String baseNote = pitch.children().get(0).text();
            String baseNoteUpper = baseNote.toUpperCase();
            Map<String, Pitch> keyMap = getKeySignatureMap(key);
            
            // Get the octaves if they were given 
            String octave = "";
            if (pitch.children().size() == 2) {
                octave = pitch.children().get(1).text();
            }
            
            // Get the pitch of the note 
            Pitch normalNotePitch;
            normalNotePitch = keyMap.get(baseNoteUpper);
            
            // Modify based on octaves
            for (int i=0; i<octave.length(); ++i) {
                char suboctave = octave.charAt(i);
                if (suboctave == '\'') {
                    normalNotePitch = normalNotePitch.transpose(Pitch.OCTAVE);
                }
                else if (suboctave == ',') {
                    normalNotePitch = normalNotePitch.transpose(-Pitch.OCTAVE);
                }
            }
            
            // If the note was lower-case, raise it another octave 
            if (!baseNote.equals(baseNoteUpper)) {
                normalNotePitch = normalNotePitch.transpose(Pitch.OCTAVE);
            }
            
            // If the note is in the accidentals map, get the correct pitch from that map 
            if (accidentals.keySet().contains(baseNote+octave)) {
                normalNotePitch = accidentals.get(baseNote+octave);
            }
            
            // Create the final note 
            finalNote = Music.note(duration, normalNotePitch, Instrument.PIANO);
        }
        
        return finalNote;
    }

    /**
     * Converts the grammar representation of a lyric into a list of pairs
     * @param lyric the grammar representation of a lyric
     * @return a list of pairs such that the first element in each pair contains either the whole
     *         lyric line with a syllable surrounded by asterisks, a single asterisk, or a single bar. 
     *         And the second element in each pair contains the number of notes the first element should be
     *         sung for (for a bar this will be 0). 
     */
    private static List<SimpleImmutableEntry<String, Integer>> parseLyric(ParseTree<PieceGrammar> lyric) {
        // First pass, create a list where each element is a pair of the form (syllable, numberOfNotes)
        // Here, syllable can also be a bar or a hyphen 
        List<SimpleImmutableEntry<String, Integer>> lyricList = new ArrayList<>();
        
        for (ParseTree<PieceGrammar> lyricElement : lyric.children()) {
            if (lyricElement.text().equals("*")) {
                // A single asterisk is stored as (*no lyrics*, 1) 
                SimpleImmutableEntry<String, Integer> skip = new SimpleImmutableEntry<>("*no lyrics*", 1);
                lyricList.add(skip);
            }
            else if (lyricElement.text().equals("|")) {
                // A bar is stored as (|, 0)
                SimpleImmutableEntry<String, Integer> bar = new SimpleImmutableEntry<>("|", 0);
                lyricList.add(bar);
            } 
            else if (lyricElement.children().get(0).name() == PieceGrammar.WORD) {
                // Dealing with a word
                ParseTree<PieceGrammar> word = lyricElement.children().get(0);
                
                for (ParseTree<PieceGrammar> subword : word.children()) {
                    switch(subword.name()) {
                    case CHUNK:
                        // Each chunk gets parsed into a syllable, then added as (chunk, 1)
                        ParseTree<PieceGrammar> chunk = subword;
                        String chunkLiteral = parseChunk(chunk);
                        SimpleImmutableEntry<String, Integer> chunkPair = 
                                new SimpleImmutableEntry<>(chunkLiteral, 1);
                        lyricList.add(chunkPair);
                        break;
                    case SEPARATOR:
                        ParseTree<PieceGrammar> separator = subword;
                        if (separator.children().get(0).name() == PieceGrammar.SPACE) {
                            // Any number of spaces is the same as a single asterisk 
                            SimpleImmutableEntry<String, Integer> skip = new SimpleImmutableEntry<>("*no lyrics*", 1);
                            lyricList.add(skip);
                        } 
                        
                        ParseTree<PieceGrammar> hyphens;
                        ParseTree<PieceGrammar> underscores;
                        if (separator.children().size() == 2) {
                            // Does not contain spaces
                            hyphens = separator.children().get(0);
                            underscores = separator.children().get(1);
                        } else {
                            // Contains spaces 
                            hyphens = separator.children().get(1);
                            underscores = separator.children().get(2);
                        }
                         
                        // Always add exactly one hyphen for a separator, stored as (-, 0)
                        SimpleImmutableEntry<String, Integer> skip = new SimpleImmutableEntry<>("-", 0);
                        lyricList.add(skip);
                        
                        for (int i=1; i<hyphens.text().length(); ++i) {
                            // Every extra hyphen beyond the first one acts like an asterisk 
                            SimpleImmutableEntry<String, Integer> hyphenSkip = new SimpleImmutableEntry<>("*no lyrics*", 1);
                            lyricList.add(hyphenSkip);
                        }
                        
                        // Modify the second to last pair so that its length increases by the number of underscores
                        // because the underscore after a hyphen applies to the syllable before the hyphen 
                        int numUnderscoresAfterHyphen = underscores.text().length();
                        SimpleImmutableEntry<String, Integer> secondToLast = lyricList.get(lyricList.size() - 2);
                        SimpleImmutableEntry<String, Integer> newSecondToLast = 
                                new SimpleImmutableEntry<>(secondToLast.getKey(), 
                                        secondToLast.getValue() + numUnderscoresAfterHyphen);
                        lyricList.set(lyricList.size() - 2, newSecondToLast);
                        break;
                    case UNDERSCORES:
                        // Modify the last pair in the list so that its length increases by the number of underscores
                        int numUnderscores = subword.text().length();
                        SimpleImmutableEntry<String, Integer> lastEntry = lyricList.get(lyricList.size() - 1);
                        SimpleImmutableEntry<String, Integer> newLastEntry = 
                                new SimpleImmutableEntry<>(lastEntry.getKey(), lastEntry.getValue() + numUnderscores);
                        lyricList.set(lyricList.size() - 1, newLastEntry);
                        break;
                    default:
                        throw new AssertionError("Should never get here");
                    }
                }
            }
        }
        
        // Second pass, convert the pairs of form (syllable, numberOfNotes) into pairs of the form 
        // (lyricLine, numberOfNotes) where lyricLine is the whole line with syllable surrounded by asterisks 
        List<SimpleImmutableEntry<String, Integer>> secondLyricList = new ArrayList<>();

        for (int i=0; i<lyricList.size(); ++i) {
            SimpleImmutableEntry<String, Integer> pair = lyricList.get(i);
            if (pair.getKey().equals("|") || pair.getKey().equals("*no lyrics*")) {
                // Always add bars and no lyrics 
                secondLyricList.add(pair);
            } 
            else if (!pair.getKey().equals("-")) {
                // Never add hyphens, hyphens are used to reconstruct the lyricLine 
                String fullLyricLine = "";
                
                // Iterate through the list again to create the fullLyricLine 
                for (int j=0; j<lyricList.size(); ++j) {
                    SimpleImmutableEntry<String, Integer> secondPair = lyricList.get(j);
                    if (i==j) {
                        // Add asterisks around the current syllable  
                        fullLyricLine += "*" + pair.getKey() + "*";
                    }
                    else {
                        if (secondPair.getKey().equals("-")) {
                            // In the second iteration, add all the hyphens in 
                            fullLyricLine += "-";
                        }
                        else if (!(secondPair.getKey().equals("|") ||
                                secondPair.getKey().equals("*no lyrics*"))) {
                            // Also add all the other syllables (these do not include bars and no lyrics) 
                            fullLyricLine += secondPair.getKey();
                        }
                    }
                    
                    if ((j != lyricList.size() - 1) && 
                         !(secondPair.getKey().equals("|") ||
                           secondPair.getKey().equals("*no lyrics*") ||
                           secondPair.getKey().equals("-"))) {
                        // In the second iteration, if we are not at the end of the list and we are an actual syllable
                        if (!(lyricList.get(j+1).getKey().equals("-"))){
                            // Then add a space after the syllable if there is no hyphen after it 
                            fullLyricLine += " ";
                        }
                    }
                }
                secondLyricList.add(new SimpleImmutableEntry<String, Integer> (fullLyricLine, pair.getValue()));
            }
        }
        
        return secondLyricList;
    }
    
    /**
     * Converts a chunk representation in the abc grammar into a string 
     * @param chunk the chunk in the abc grammar
     * @return the string representation of the chunk
     */
    private static String parseChunk(ParseTree<PieceGrammar> chunk) {
        // Initialize a chunk string and fill it in appropriately 
        String chunkString = ""; 
        ParseTree<PieceGrammar> mutlipleSyllablesOrWords = chunk.children().get(0);
        
        switch(mutlipleSyllablesOrWords.name()) {
        case MULTIPLE_SYLLABLES:
            for (ParseTree<PieceGrammar> lyricTextOrBackslashHyphen : mutlipleSyllablesOrWords.children()) {
                // Combine multiple syllables with a hyphen between them (corresponds to \\-)
                if (lyricTextOrBackslashHyphen.name() == PieceGrammar.LYRIC_TEXT) {
                    chunkString += lyricTextOrBackslashHyphen.text();
                } else {
                    chunkString += "-";
                }
            }
            break;
        case MULTIPLE_WORDS:
            for (ParseTree<PieceGrammar> lyricTextOrTilde : mutlipleSyllablesOrWords.children()) {
                // Combine multiple words with spaces between them (corresponds to ~)
                if (lyricTextOrTilde.name() == PieceGrammar.LYRIC_TEXT) {
                    chunkString += lyricTextOrTilde.text();
                } else {
                    chunkString += " ";
                }
            }
            break;
        default:
            throw new AssertionError("Should never get here");
        }
        return chunkString;
    }

    /**
     * Converts a list of pairs into a final Music object
     * @param voiceMusic a list of pairs of a particular voice for a the entire piece
     * @return the Music object represented by voiceMusic, if voiceMusic does not contain
     *         any "music" or "rest" pairs then this method returns a rest of length 0
     */
    private static Music compress(List<SimpleImmutableEntry<String,Music>> voiceMusic) {
        // TODO implement this method
        // TODO Satvat can you add comments to this? 
        Music requiredMusic = Music.rest(0);
        Music givenSection = Music.rest(0);
        Music givenMeasure = Music.rest(0);
        Music repeatSection = Music.rest(0);
        boolean repeatedSectionStart = false;
        for(SimpleImmutableEntry<String,Music> typeAndMusic : voiceMusic) {
            switch(typeAndMusic.getKey()) {
            case "music":
            case "rest":
                Music addition = typeAndMusic.getValue();
                givenMeasure = Music.concat(givenMeasure, addition);
                break;
            case "[|":
                break;
            case "|":
                givenSection = addMeasure(givenSection, givenMeasure);
                givenMeasure = Music.rest(0);
                break;
            case "||":
            case "|]":
                givenSection = addMeasure(givenSection, givenMeasure);
                givenMeasure = Music.rest(0);
                if(repeatedSectionStart) {
                    repeatSection = addMeasure(repeatSection, givenSection);
                    givenSection = Music.rest(0);
                }
                else {
                    requiredMusic = addMeasure(requiredMusic, givenSection);
                    givenSection = Music.rest(0);
                }
                break;
            case "|:":
                repeatedSectionStart = true;
                givenSection = addMeasure(givenSection, givenMeasure);
                givenMeasure = Music.rest(0);
                requiredMusic = addMeasure(requiredMusic, givenSection);
                givenSection = Music.rest(0);
                break;
            case ":|":
                givenSection = addMeasure(givenSection, givenMeasure);
                givenMeasure = Music.rest(0);
                if(repeatedSectionStart) {
                    repeatSection = addMeasure(repeatSection, givenSection);
                    givenSection = Music.rest(0);
                    requiredMusic = addMeasure(requiredMusic, Music.concat(repeatSection, repeatSection));
                    repeatSection = Music.rest(0);
                }
                else {
                    requiredMusic = addMeasure(requiredMusic, Music.concat(givenSection, givenSection));
                    givenSection = Music.rest(0);
                }
                repeatedSectionStart = false;
                break;
            default:
                throw new AssertionError("Should never get here");
            }
        }
        if(!givenMeasure.equals(Music.rest(0))) {
            givenSection = addMeasure(givenSection, givenMeasure);
            givenMeasure = Music.rest(0);
        }
        if(!givenSection.equals(Music.rest(0))) {
            repeatSection = addMeasure(repeatSection, givenSection);
            givenSection = Music.rest(0);
        }
        if(!repeatSection.equals(Music.rest(0))) {
            requiredMusic = addMeasure(requiredMusic, repeatSection);
            repeatSection = Music.rest(0);
        }

        return requiredMusic;
    }
    
    /**
     * TODO 
     * @param requiredMusic
     * @param givenMeasure
     * @return
     */
    private static Music addMeasure(Music requiredMusic, Music givenMeasure) {
        if(requiredMusic.equals(Music.rest(0))) {
            return givenMeasure;
        }
        else {
            return Music.concat(requiredMusic, givenMeasure);
        }
    }
    
    /**
     * Create a map for a specific key mapping Strings to the correct Pitch
     * @param key the string representation of the key signature
     * @return a map that maps strings "C" "D" "E" "F" "G" "A" "B" to the 
     *         the correct Pitch in the key signature, where "C" is middle C
     */
    private static Map<String, Pitch> getKeySignatureMap(String key){
        // Map for C major and A minor 
        Map<String, Pitch> keyMap = new HashMap<>();
        keyMap.put("C", new Pitch('C'));
        keyMap.put("D", new Pitch('D'));
        keyMap.put("E", new Pitch('E'));
        keyMap.put("F", new Pitch('F'));
        keyMap.put("G", new Pitch('G'));
        keyMap.put("A", new Pitch('A'));
        keyMap.put("B", new Pitch('B'));

        // Add sharps 
        if (key.equals("G") || key.equals("Em")) {
            keyMap.put("F", new Pitch('F').transpose(1));
        } else if (key.equals("D") || key.equals("Bm")) {
            keyMap.put("F", new Pitch('F').transpose(1));
            keyMap.put("C", new Pitch('C').transpose(1));
        } else if (key.equals("A") || key.equals("F#m")) {
            keyMap.put("F", new Pitch('F').transpose(1));
            keyMap.put("C", new Pitch('C').transpose(1));
            keyMap.put("G", new Pitch('G').transpose(1));
        } else if (key.equals("E") || key.equals("C#m")) {
            keyMap.put("F", new Pitch('F').transpose(1));
            keyMap.put("C", new Pitch('C').transpose(1));
            keyMap.put("G", new Pitch('G').transpose(1));
            keyMap.put("D", new Pitch('D').transpose(1));
        } else if (key.equals("B") || key.equals("G#m")) {
            keyMap.put("F", new Pitch('F').transpose(1));
            keyMap.put("C", new Pitch('C').transpose(1));
            keyMap.put("G", new Pitch('G').transpose(1));
            keyMap.put("D", new Pitch('D').transpose(1));
            keyMap.put("A", new Pitch('A').transpose(1));
        } else if (key.equals("F#") || key.equals("D#m")) {
            keyMap.put("F", new Pitch('F').transpose(1));
            keyMap.put("C", new Pitch('C').transpose(1));
            keyMap.put("G", new Pitch('G').transpose(1));
            keyMap.put("D", new Pitch('D').transpose(1));
            keyMap.put("A", new Pitch('A').transpose(1));
            keyMap.put("E", new Pitch('E').transpose(1));
        } else if (key.equals("C#") || key.equals("A#m")) {
            keyMap.put("F", new Pitch('F').transpose(1));
            keyMap.put("C", new Pitch('C').transpose(1));
            keyMap.put("G", new Pitch('G').transpose(1));
            keyMap.put("D", new Pitch('D').transpose(1));
            keyMap.put("A", new Pitch('A').transpose(1));
            keyMap.put("E", new Pitch('E').transpose(1));
            keyMap.put("B", new Pitch('B').transpose(1));
        } 
        
        // Add flats 
        else if (key.equals("F") || key.equals("Dm")) {
            keyMap.put("B", new Pitch('B').transpose(-1));
        } else if (key.equals("Bb") || key.equals("Gm")) {
            keyMap.put("B", new Pitch('B').transpose(-1));
            keyMap.put("E", new Pitch('E').transpose(-1));
        } else if (key.equals("Eb") || key.equals("Cm")) {
            keyMap.put("B", new Pitch('B').transpose(-1));
            keyMap.put("E", new Pitch('E').transpose(-1));
            keyMap.put("A", new Pitch('A').transpose(-1));
        } else if (key.equals("Ab") || key.equals("Fm")) {
            keyMap.put("B", new Pitch('B').transpose(-1));
            keyMap.put("E", new Pitch('E').transpose(-1));
            keyMap.put("A", new Pitch('A').transpose(-1));
            keyMap.put("D", new Pitch('D').transpose(-1));
        } else if (key.equals("Db") || key.equals("Bbm")) {
            keyMap.put("B", new Pitch('B').transpose(-1));
            keyMap.put("E", new Pitch('E').transpose(-1));
            keyMap.put("A", new Pitch('A').transpose(-1));
            keyMap.put("D", new Pitch('D').transpose(-1));
            keyMap.put("G", new Pitch('G').transpose(-1));
        } else if (key.equals("Gb") || key.equals("Ebm")) {
            keyMap.put("B", new Pitch('B').transpose(-1));
            keyMap.put("E", new Pitch('E').transpose(-1));
            keyMap.put("A", new Pitch('A').transpose(-1));
            keyMap.put("D", new Pitch('D').transpose(-1));
            keyMap.put("G", new Pitch('G').transpose(-1));
            keyMap.put("C", new Pitch('C').transpose(-1));
        } else if (key.equals("Cb") || key.equals("Abm")) {
            keyMap.put("B", new Pitch('B').transpose(-1));
            keyMap.put("E", new Pitch('E').transpose(-1));
            keyMap.put("A", new Pitch('A').transpose(-1));
            keyMap.put("D", new Pitch('D').transpose(-1));
            keyMap.put("G", new Pitch('G').transpose(-1));
            keyMap.put("C", new Pitch('C').transpose(-1));
            keyMap.put("F", new Pitch('F').transpose(-1));
        }
        
        return keyMap;
    }
    
}
