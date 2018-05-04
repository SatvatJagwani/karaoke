package karaoke;


import java.util.Set;

import karaoke.sound.SequencePlayer;

/**
 * An object having all information in an abc file, i.e., header and music.
 * Immutable.
 */
public class Piece {
    
    /**
     * Get a piece from a file.
     * @param filename name of the abc file to parse and store
     * @return a piece object with all information of abc file.
     */
    public static Piece parseFromFile(String filename) {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Get the name of the composer of this piece.
     * @return the name as String
     */
    public String nameOfComposer() {
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
     * Get the number of beats played per minute.
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
     * Get an empty sequence player with same beats per minute as specified in the piece.
     * @return the sequence player.
     */
    public SequencePlayer getPlayer() {
        throw new RuntimeException("Unimplemented");
    }
}
