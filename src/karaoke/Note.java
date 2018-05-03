package karaoke;

import java.util.List;
import java.util.Map;
import karaoke.sound.*;

/**
 * Represents a note played by an instrument for a specific duration of time 
 * Immutable and thread-safe. 
 */
public class Note implements Music {

    /**
     * Make a Note played by instrument for duration beats.
     * @param duration duration in beats, must be >= 0
     * @param pitch pitch to play
     * @param instrument instrument to use
     */
    public Note(double duration, Pitch pitch, Instrument instrument) {
        throw new RuntimeException("Unimplemented");
    }
    
    @Override
    public double duration() {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void play(SequencePlayer player, double atBeat, Map<String, List<String>> voiceToLyricsMap) {
        throw new RuntimeException("Unimplemented");
    }

    @Override 
    public int hashCode() {
        throw new RuntimeException("Unimplemented");
    }
    
    @Override 
    public boolean equals(Object that) {
        throw new RuntimeException("Unimplemented");
    }
    
    @Override
    public String toString() {
        throw new RuntimeException("Unimplemented");
    }
    
}
