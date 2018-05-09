package karaoke;

import java.util.List;
import java.util.Map;
import karaoke.sound.*;

/**
 * Represents a note played by an instrument for a specific duration of time 
 * Immutable and thread-safe. 
 */
public class Note implements Music {
    
    private final double duration;
    private final Pitch pitch;
    private final Instrument instrument;
    
    // Abstraction Function
    //    AF(duration, pitch, instrument) = a musical note with with Pitch pitch and duration
    //                                      number of beats that would be played on Instrument
    //                                      instrument
    // Rep invariant
    //    - duration >= 0
    // Safety from rep exposure
    //    - all fields are private, final, and immutable types

    /**
     * Make a Note played by instrument for duration beats.
     * @param duration duration in beats, must be >= 0
     * @param pitch pitch to play
     * @param instrument instrument to use
     */
    public Note(double duration, Pitch pitch, Instrument instrument) {
        this.duration = duration;
        this.pitch = pitch;
        this.instrument = instrument;
        checkRep();
    }
    
    /**
     * Checks the rep invariant
     */
    private void checkRep() {
        assert pitch != null;
        assert instrument != null;
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
        player.addNote(instrument, pitch, atBeat, duration);
        checkRep();
    }

    @Override 
    public int hashCode() {
        checkRep();
        return Double.hashCode(duration) + pitch.hashCode() + instrument.hashCode();
    }
    
    @Override 
    public boolean equals(Object that) {
        return that instanceof Note && this.sameValue((Note) that);
    }
    
    /**
     * Checks if this and that are the same note
     * @param that the note to compare against
     * @return true if they have the same field values
     */
    private boolean sameValue(Note that) {
        return this.duration == that.duration
            && this.pitch.equals(that.pitch)
            && this.instrument.equals(that.instrument);
    }
    
    @Override
    public String toString() {
        checkRep();
        return "(" + duration + ", " + pitch.toString() + ")";
    }
    
}
