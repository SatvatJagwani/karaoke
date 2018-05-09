package karaoke;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import edu.mit.eecs.parserlib.UnableToParseException;
import karaoke.parser.PieceParser;
import karaoke.sound.SequencePlayer;

/**
 * An object having all information in an abc file, i.e., header and music.
 * Immutable.
 */
public class Piece {
    
    // Thread safety argument:
    //     Immutable references to immutable objects. No beneficent mutation.
    
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
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Get the name of the composer of this piece.
     * @return the name as String
     */
    public String getNameOfComposer() {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Get the index number of this piece.
     * @return the number as int.
     */
    public int getIndex() {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Get the default duration of each note.
     * @return duration as a fraction.
     */
    public double getNoteDuration() {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Get the meter of the piece.
     * @return the meter as string in the format "x/y"
     */
    public String getMeter() {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Get the number of beats played per minute. A beat is the 
     * default note length 
     * @return frequency as int.
     */
    public int beatsPerMinute() {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Get title of this piece.
     * @return the title as string.
     */
    public String getTitle() {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Get all the voices in this piece.
     * @return set of all the names of voices.
     */
    public Set<String> getVoices() {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Get the key of this piece.
     * @return key as string.
     */
    public String getKey() {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Get the music of this piece.
     * @return the music object storing the score.
     */
    public Music getMusic() {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Creates an empty sequence player with same beats per minute as specified in the piece.
     * @return the sequence player.
     */
    public SequencePlayer createPlayer() {
        throw new RuntimeException("Unimplemented");
    }

    /**
     * Get a string representation of the piece that shows its header fields and its music.
     * @return [composer, index, defaultNoteDuration, meter, beatsPerMinute, title, voices, key, music]
     *         where all these fields are string representations of what we get from the observers.
     */
    @Override
    public String toString() {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Check whether this represents composer, index, defaultNoteDuration, meter, beatsPerMinute, title,
     *                               voices, key, music and structure as that
     * @param that the other object
     * @return true iff both are piece objects with all fields same and the music represented has same structure and sounds the same.
     */
    @Override
    public boolean equals(Object that) {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Get a hashCode of this consistent with the definition of equals.
     * @return an integer such that this.equals(that) -> this.hashCode()==that.hashCode()
     */
    @Override
    public int hashCode() {
        throw new RuntimeException("Unimplemented");
    }
}
