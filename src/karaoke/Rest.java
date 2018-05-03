package karaoke;

import java.util.List;
import java.util.Map;

import karaoke.sound.SequencePlayer;

/**
 * Represents a pause in a piece of music 
 * Immutable and thread-safe. 
 */
public class Rest implements Music {
    
    /**
     * Make a Rest that lasts for duration beats.
     * @param duration duration in beats, must be >= 0
     */
    public Rest(double duration) {
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
