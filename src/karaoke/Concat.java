package karaoke;

import java.util.List;
import java.util.Map;
import karaoke.sound.*;

/**
 * Represents two pieces of music being played sequentially 
 * Immutable and thread-safe. 
 */
public class Concat implements Music {
    
    private final Music first;
    private final Music second;

    // Abstraction Function
    //    AF(first, second) = a music that is first immediately followed by second
    // Rep invariant
    //    - true
    // Safety from rep exposure
    //    - all fields are private, final, and immutable types
    
    /**
     * Make a Music sequence that plays first followed by second.
     * @param first music to play first
     * @param second music to play second
     */
    public Concat(Music first, Music second) {
        this.first = first;
        this.second = second;
        checkRep();
    }
    
    private void checkRep() {
        assert first != null;
        assert second != null;
    }
    
    @Override
    public double duration() {
        checkRep();
        return first.duration() + second.duration();
    }

    @Override
    public void play(SequencePlayer player, double atBeat, Map<String, List<String>> voiceToLyricsMap) {
        checkRep();
        first.play(player, atBeat, voiceToLyricsMap);
        second.play(player, atBeat + first.duration(), voiceToLyricsMap);
        checkRep();
    }

    @Override 
    public int hashCode() {
        checkRep();
        return first.hashCode() + second.hashCode();
    }
    
    @Override 
    public boolean equals(Object that) {
        checkRep();
        return that instanceof Concat && this.sameValue((Concat) that);
    }
    
    /**
     * Checks if this and that are the same concat
     * @param that the concat to compare against
     * @return true if this and that's first musics are the same 
     *                 this and that's second musics are the same
     */
    private boolean sameValue(Concat that) {
        return this.first.equals(that.first) && this.second.equals(that.second);
    }
    
    @Override
    public String toString() {
        checkRep();
        return "(" + first.toString() + " && " + second.toString() + ")";
    }
    
}
