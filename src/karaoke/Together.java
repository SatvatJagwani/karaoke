package karaoke;

import java.util.List;
import java.util.Map;
import karaoke.sound.SequencePlayer;

/**
 * Represents two pieces of music being played together at the same time
 * Immutable and thread-safe. 
 */
public class Together implements Music {
    
    /**
     * Make a Music sequence that plays m1 and m2 in parallel and has a 
     *      duration given by m1.duration(). Note that the time it takes to play
     *      m1 and m2 in parallel may be longer than the duration of this sequence. 
     * @param m1 piece of music to play in parallel with m2
     * @param m2 piece of music to play in parallel with m1
     */
    public Together(Music m1, Music m2) {
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
