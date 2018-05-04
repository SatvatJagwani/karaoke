package karaoke;

import java.util.List;
import java.util.Map;
import karaoke.sound.*;

/**
 * Represents two pieces of music being played sequentially 
 * Immutable and thread-safe. 
 */
public class Concat implements Music {

    /**
     * Make a Music sequence that plays first followed by second.
     * @param first music to play first
     * @param second music to play second
     */
    public Concat(Music first, Music second) {
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
