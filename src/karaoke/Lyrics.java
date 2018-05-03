package karaoke;

import java.util.List;
import java.util.Map;

import karaoke.sound.SequencePlayer;

/**
 * Represents the syllable being sung by a specific voice. 
 * Immutable and thread-safe. 
 */
public class Lyrics implements Music {

    /**
     * A line of lyrics sung by voice that highlights the specific syllable surrounded by 
     *      two asterisks in the given lyricLine
     * @param lyricLine a line of lyrics with one syllable surrounded by asterisks
     * @param voice the voice of the singer for the lyricsLine 
     */
    public Lyrics(String lyricLine, String voice) {
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
