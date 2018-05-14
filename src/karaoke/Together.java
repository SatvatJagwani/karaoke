package karaoke;

import java.util.List;
import java.util.Map;
import karaoke.sound.SequencePlayer;

/**
 * Represents two pieces of music being played together at the same time
 * Immutable.
 */
public class Together implements Music {
    
    private final Music m1;
    private final Music m2;
    
    // Abstraction Function
    //    AF(m1, m2) = a music made of m1 and m2 at the same time and played for the duration
    //                 of m1
    // Rep invariant
    //    - true
    // Safety from rep exposure
    //    - all fields are private, final, and immutable types
    
    /**
     * Make a Music sequence that plays m1 and m2 in parallel and has a 
     *      duration given by m1.duration(). Note that the time it takes to play
     *      m1 and m2 in parallel may be longer than the duration of this sequence. 
     * @param m1 piece of music to play in parallel with m2
     * @param m2 piece of music to play in parallel with m1
     */
    public Together(Music m1, Music m2) {
        this.m1 = m1;
        this.m2 = m2;
        checkRep();
    }
    
    private void checkRep() {
        assert m1 != null;
        assert m2 != null;
    }
    
    @Override
    public double duration() {
        checkRep();
        return m1.duration();
    }

    @Override
    public void play(SequencePlayer player, double atBeat, Map<String, List<String>> voiceToLyricsMap) {
        checkRep();
        m1.play(player, atBeat, voiceToLyricsMap);
        m2.play(player, atBeat, voiceToLyricsMap);
        checkRep();
    }

    @Override 
    public int hashCode() {
        checkRep();
        return m1.hashCode() + m2.hashCode();
    }
    
    @Override 
    public boolean equals(Object that) {
        checkRep();
        return that instanceof Together && this.sameValue((Together) that);
    }
    
    private boolean sameValue(Together that) {
        return this.m1.equals(that.m1) && this.m2.equals(that.m2);
    }
    
    @Override
    public String toString() {
        checkRep();
        return "(" + m1.toString() + " || " + m2.toString() + ")";
    }
    
}
