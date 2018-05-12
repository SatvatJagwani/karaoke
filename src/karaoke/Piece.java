package karaoke;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import edu.mit.eecs.parserlib.UnableToParseException;
import karaoke.parser.PieceParser;
import karaoke.sound.MidiSequencePlayer;
import karaoke.sound.SequencePlayer;

/**
 * An object having all information in an abc file, i.e., header and music.
 * Immutable.
 */
public class Piece {
    
    private final String composer;
    private final int index;
    private final double defaultNoteDuration;
    private final String meter;
    private final int beatsPerMinute;
    private final String title;
    private final Set<String> voices;
    private final String key;
    private final Music music;
    
    // Abstraction Function
    //    AF(composer, index, defaultNoteDuration, meter, beatsPerMinute,
    //       title, meter, beatsPerMinute, title, voices, key, music) =   a piece of music, title,  by composer
    //                                                                    with track index, notes with
    //                                                                    defaultNoteDuration, meter, and
    //                                                                    beatsPerMinute sang by voices in key
    //                                                                    with accompanying music
    // Rep Invariant
    //    - index >= 0
    //    - composer cannot have a newline character
    //    - defaultNoteDuration > 0
    //    - meter must be in form "number / number"
    //    - beatsPerMinute > 0
    //    - title cannot have a newline character
    //    - each voice in voices cannot have a newline character
    //    - voices.size() >= 1
    //    - key must be [A-G](# | b)?m?
    //    - music must not be null
    // Safety from rep exposure
    //    - every field is private, final, and immmutable type except for the set
    //    - for the set, we use defensive copying in the constructor and the observer
    // Thread safety argument:
    //    - Immutable references to immutable objects. No beneficent mutation.
    //    - voices does not need to be threadsafe because we only create one in each instance of Piece
    
    /**
     * Get a piece from a file.
     * @param filename name of the abc file to parse and store
     * @return a piece object with all information of abc file.
     * @throws IOException if no abc file is found at path filename
     * @throws UnableToParseException if the abc file is not in right format.
     */
    public static Piece parseFromFile(String filename) throws IOException, UnableToParseException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String fileContents = "";
        String input;
        while((input=br.readLine())!=null) {
            fileContents += input + "\n";
        }
        br.close();
        return PieceParser.parse(fileContents);
    }
    
    /**
     * Creates a new piece with the given parameters
     * @param composer composer of the piece
     * @param index index number 
     * @param defaultNoteDuration duration for default note length
     * @param meter meter of the piece 
     * @param beatsPerMinute tempo 
     * @param title title of the piece 
     * @param voices the set of voices within the piece 
     * @param key the default key signature of the piece 
     * @param music the Music data type for the piece 
     */
    public Piece(String composer, int index, double defaultNoteDuration, 
            String meter, int beatsPerMinute, String title, Set<String> voices,
            String key, Music music) {
        this.composer = composer;
        this.index = index;
        this.defaultNoteDuration = defaultNoteDuration;
        this.meter = meter;
        this.beatsPerMinute = beatsPerMinute;
        this.title = title;
        this.voices = new HashSet<>();
        for (String v: voices) {
            this.voices.add(v);
        }
        this.key = key;
        this.music = music;
        checkRep();
    }
    
    /**
     * Checks the rep invariant
     */
    private void checkRep() {
        // index >= 0
        assert index >= 0;
        // composer cannot have a newline character
        assert !(composer.contains("\n") | composer.contains("\r"));
        // defaultNoteDuration > 0
        assert defaultNoteDuration > 0;
        // meter must be in form "number / number"
        assert meter.matches("[0-9]+/[0-9]+");
        // beatsPerMinute > 0
        assert beatsPerMinute > 0;
        // title cannot have a newline character
        assert !(title.contains("\n") | title.contains("\r"));
        // each voice in voices cannot have a newline character
        for (String voice: voices) {
            assert !(voice.contains("\n") | voice.contains("\r"));
        }
        // voices.size() >= 1
        assert voices.size() >= 1;
        // key must be [A-G](# | b)?m?
        assert key.matches("[A-G](#|b)?m?");
        // music must not be null
        assert music != null;
    }
    
    /**
     * Get the name of the composer of this piece.
     * @return the name as String
     */
    public String getNameOfComposer() {
        checkRep();
        return composer;
    }
    
    /**
     * Get the index number of this piece.
     * @return the number as int.
     */
    public int getIndex() {
        checkRep();
        return index;
    }
    
    /**
     * Get the default duration of each note.
     * @return duration as a fraction.
     */
    public double getNoteDuration() {
        checkRep();
        return defaultNoteDuration;
    }
    
    /**
     * Get the meter of the piece.
     * @return the meter as string in the format "x/y"
     */
    public String getMeter() {
        checkRep();
        return meter;
    }
    
    /**
     * Get the number of beats played per minute. A beat is the 
     * default note length 
     * @return frequency as int.
     */
    public int beatsPerMinute() {
        checkRep();
        return beatsPerMinute;
    }
    
    /**
     * Get title of this piece.
     * @return the title as string.
     */
    public String getTitle() {
        checkRep();
        return title;
    }
    
    /**
     * Get all the voices in this piece.
     * @return set of all the names of voices.
     */
    public Set<String> getVoices() {
        Set<String> result = new HashSet<>();
        for (String v: voices) {
            result.add(v);
        }
        checkRep();
        return result;
    }
    
    /**
     * Get the key of this piece.
     * @return key as string.
     */
    public String getKey() {
        checkRep();
        return key;
    }
    
    /**
     * Get the music of this piece.
     * @return the music object storing the score.
     */
    public Music getMusic() {
        checkRep();
        return music;
    }
    
    /**
     * Get the beats per minute of the piece
     * @return the beats per minute
     */
    public int getBeatsPerMinute() {
        checkRep();
        return beatsPerMinute;
    }
    
    /**
     * Creates an empty sequence player with same beats per minute as specified in the piece.
     * @return the sequence player.
     * @throws InvalidMidiDataException if MIDI device unavailable
     * @throws MidiUnavailableException if MIDI play fails
     */
    public SequencePlayer createPlayer() throws MidiUnavailableException, InvalidMidiDataException {
        final int ticksPerBeat = 64;
        SequencePlayer sp = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);
        return sp;
    }

    /**
     * Get a string representation of the piece that shows its header fields and its music.
     * @return [composer, index, defaultNoteDuration, meter, beatsPerMinute, title, voices, key, music]
     *         where all these fields (except voices) are string representations of what we get from the observers.
     *         voices is printed as [voice1, voice2, ... voicen] where voice1, voice2, ... are string representations
     *         of the voices present and are in alphabetical order.
     */
    @Override
    public String toString() {
        SortedSet<String> sortedVoices = new TreeSet<>(getVoices());  
        checkRep();
        return "[" + this.getNameOfComposer() + ", "
                   + this.getIndex() + ", "
                   + this.getNoteDuration() + ", "
                   + this.getMeter() + ", "
                   + this.getBeatsPerMinute() + ", "
                   + this.getTitle() + ", "
                   + sortedVoices.toString() + ", "
                   + this.getKey() + ", "
                   + this.getMusic().toString()
                   + "]";
    }
    
    /**
     * Check whether this represents composer, index, defaultNoteDuration, meter, beatsPerMinute, title,
     *                               voices, key, music and structure as that
     * @param that the other object
     * @return true iff both are piece objects with all fields same and the music represented has same structure and sounds the same.
     */
    @Override
    public boolean equals(Object that) {
        return that instanceof Piece && this.sameValue((Piece) that);
    }
    
    /**
     * Compares this and that
     * @param that the Piece to compare this against
     * @return true if this and that have the same field values
     */
    private boolean sameValue(Piece that) {
        return this.composer.equals(that.composer)
            && this.index == that.index
            && this.defaultNoteDuration == that.defaultNoteDuration
            && this.meter == that.meter
            && this.beatsPerMinute == that.beatsPerMinute
            && this.title.equals(that.title)
            && this.voices.equals(that.voices)
            && this.key.equals(that.key)
            && this.music.equals(that.music);
    }
    
    /**
     * Get a hashCode of this consistent with the definition of equals.
     * @return an integer such that this.equals(that) -> this.hashCode()==that.hashCode()
     */
    @Override
    public int hashCode() {
        return this.index 
             + Double.hashCode(this.defaultNoteDuration)
             + this.meter.hashCode()
             + this.beatsPerMinute
             + this.title.hashCode()
             + this.voices.hashCode()
             + this.key.hashCode()
             + this.music.hashCode();
    }
}
