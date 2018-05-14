package karaoke;

import java.util.List;
import java.util.Map;
import karaoke.sound.SequencePlayer;

/**
 * Represents a pause in a piece of music 
 * Immutable.
 */
public class Rest implements Music {
    
    private final double duration;
    
    // Abstraction Function
    //    AF(duration) = a pause lasting duration beats
    // Rep invariant
    //    - duration >= 0
    // Safety from rep exposure
    //    - all fields are private, final, and immutable types
    
    /**
     * Make a Rest that lasts for duration beats.
     * @param duration duration in beats, must be >= 0
     */
    public Rest(double duration) {
        this.duration = duration;
        checkRep();
    }
    
    private void checkRep() {
        assert duration >= 0;
    }
    
    @Override
    public double duration() {
        checkRep();
        return duration;
    }

    @Override
    public void play(SequencePlayer player, double atBeat, Map<String, List<String>> voiceToLyricsMap) {
        checkRep();
        return;
    }

    @Override 
    public int hashCode() {
        checkRep();
        return Double.hashCode(duration);
    }
    
    @Override 
    public boolean equals(Object that) {
        checkRep();
        return that instanceof Rest && this.sameValue((Rest) that);
    }
    
    /**
     * Checks if this and that are the same rest
     * @param that the rest to compare against
     * @return true if this and that rest for the same duration
     */
    private boolean sameValue(Rest that) {
        return this.duration == that.duration;
    }
    
    @Override
    public String toString() {
        checkRep();
        return "(" + duration + ")";
    }
    
}
