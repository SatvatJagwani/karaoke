package karaoke;

import java.util.List;
import java.util.Map;
import karaoke.sound.*;

/**
 * Represents a music object.
 * Immutable.
 */
public interface Music {

    // Music = Rest(duration: Double)
    //  + Note(duration: Double, pitch: Pitch, instrument: Instrument)
    //  + Lyrics(lyricLine: String, voice: String)
    //  + Concat(first: Music, second: Music)
    //  + Together(m1: Music, m2: Music)
    
    /**
     * Make a rest that lasts for duration beats.
     * @param duration duration in beats, must be >= 0
     * @return a Music object representing a rest 
     */
    public static Music rest(double duration) {
        return new Rest(duration);
    }
    
    /**
     * Make a note played by instrument for duration beats.
     * @param duration duration in beats, must be >= 0
     * @param pitch pitch to play
     * @param instrument instrument to use
     * @return a Music object representing a note 
     */
    public static Music note(double duration, Pitch pitch, Instrument instrument) {
        return new Note(duration, pitch, instrument);
    }
    
    /**
     * Make a line of lyrics sung by voice that highlights the specific syllable surrounded by 
     *      two asterisks in the given lyricLine
     * @param lyricLine a line of lyrics with one syllable surrounded by asterisks
     * @param voice the voice of the singer for the lyricsLine 
     * @return a Music object representing a line of lyrics as described above 
     */
    public static Music lyrics(String lyricLine, String voice) {
        return new Lyrics(lyricLine, voice);
    }
    
    /**
     * Make a Music sequence that plays first followed by second.
     * @param first music to play first
     * @param second music to play second
     * @return a Music object where first is followed by second 
     */
    public static Music concat(Music first, Music second) {
        return new Concat(first, second);
    }
    
    /**
     * Make a Music sequence that plays m1 and m2 in parallel and has a 
     *      duration given by m1.duration(). Note that the time it takes to play
     *      m1 and m2 in parallel may be longer than the duration of this sequence. 
     * @param m1 piece of music to play in parallel with m2
     * @param m2 piece of music to play in parallel with m1
     * @return a Music object where m1 and m2 are played in parallel 
     */
    public static Music together(Music m1, Music m2) {
        return new Together(m1, m2);
    }
    
    /**
     * @return total duration of this piece in beats
     */
    double duration();

    /**
     * Play this piece and add events to player for modifying the map while playing the music.
     * Whenever an event is added to the player to modify the map, the modification of the map
     * occurs inside a synchronized block. At the end of the mutation in the synchronized block, 
     * the event then calls notifyAll().  
     * @param player player to play on
     * @param atBeat when to play
     * @param voiceToLyricsMap player modifies this whenever a lyric is to be printed.
     */
    void play(SequencePlayer player, double atBeat, Map<String, List<String>> voiceToLyricsMap);
    
    /**
     * Get a string representation of the music that shows its overall structure.
     * @return (duration, note) if this is a single note 'note' with duration 'duration'
     *         (duration) if this is a rest with duration 'duration'
     *         (voice: lyrics) if this represents lyrics 'lyrics' sung by voice 'voice'
     *         (left && right) if this is a concatenation of two parts: where right is played immediately after left
     *         (m1 || m2) if this is a combination of m1 and m2 played together, where duration is decided by m1.
     */
    @Override
    public String toString();
    
    /**
     * Check whether this represents same music and same structure as that
     * @param that the other object
     * @return true iff both are music objects with same notes, rests and lyrics arranged in the same structure.
     *         Note that both may sound the same but not be equal because of different structure.
     */
    @Override
    public boolean equals(Object that);
    
    /**
     * Get a hashCode of this consistent with the definition of equals.
     * @return an integer such that this.equals(that) -> this.hashCode()==that.hashCode()
     */
    @Override
    public int hashCode();
}
