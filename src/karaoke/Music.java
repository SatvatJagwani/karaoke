package karaoke;

import java.util.List;
import java.util.Map;

import karaoke.sound.SequencePlayer;

/**
 * Represents a music object.
 * Immutable.
 */
public interface Music {

    // Music = Rest(duration: Double)
    //  + Note(duration: Double, pitch: Pitch, instrument: Instrument)
    //  + Lyrics(lyricLine: String, voice: String)
    //  + Concat(first: Music, second: Music)
    //  + Together(top: Music, bottom: Music)
    
    /**
     * @return total duration of this piece in beats
     */
    double duration();

    /**
     * Play this piece and add events to player for modifying the map while playing the music.
     * @param player player to play on
     * @param atBeat when to play
     * @param voiceToLyricsMap player modifies this whenever a lyric is to be printed.
     */
    void play(SequencePlayer player, double atBeat, Map<String, List<String>> voiceToLyricsMap);
}
