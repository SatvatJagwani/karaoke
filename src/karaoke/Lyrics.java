package karaoke;

import java.util.List;
import java.util.Map;
import karaoke.sound.SequencePlayer;

/**
 * Represents the syllable being sung by a specific voice. 
 * Immutable.
 */
public class Lyrics implements Music {

    private final String voice, lyricLine;
    
    // Abstraction function:
    //     AF(voice, lyricLine) = a line of lyrics lyricLine with the syllable in between asterisks in lyricLine
    //                            sung by voice voice
    // Representation invariant:
    //     - voice has no newline, underscore, tilde, nor bar
    //     - lyricLine is non-empty.
    //     - lyricLine has exactly two asterisks enclosing at least one character.
    //     - asterisks with enclosed string are surrounded by either spaces or hyphens or a combination of these two.
    // Safety from rep exposure:
    //     - all fields are private, final and immutable.
    
    private void checkRep() {
        assert voice!=null;
        assert lyricLine!=null;
        
        // voice has no newline.
        assert !voice.contains("\n");
        assert !voice.contains("\r");
        
        // lyricLine is non empty
        assert !lyricLine.isEmpty();
        int firstAsteriskPosition = -1;
        int secondAsteriskPosition = -1;
        String disallowedCharacters = "\n\r_~|";
        for(int i=0; i<lyricLine.length(); i++) {
            assert (!disallowedCharacters.contains("" + lyricLine.charAt(i)));
            if(lyricLine.charAt(i)=='*') {
                if(firstAsteriskPosition == -1)
                    firstAsteriskPosition = i;
                else if(secondAsteriskPosition == -1)
                    secondAsteriskPosition = i;
                else
                    assert false; // not more than two asterisks
            }
        }
        assert firstAsteriskPosition != -1 && secondAsteriskPosition != -1; // at least two asterisks
        assert secondAsteriskPosition-firstAsteriskPosition >= 2; // Both asterisks should enclose at least one character
        
        if (firstAsteriskPosition != 0) {
            char beforeFirstAsterisk = lyricLine.charAt(firstAsteriskPosition-1);
            assert beforeFirstAsterisk=='-' || beforeFirstAsterisk==' '; // character before first asterisk must be either hyphen or space
        }
        if (secondAsteriskPosition != lyricLine.length()-1) {
            char afterSecondAsterisk = lyricLine.charAt(secondAsteriskPosition+1);
            assert afterSecondAsterisk=='-' || afterSecondAsterisk==' '; // character after second asterisk must be either hyphen or space
        }
    }
    
    /**
     * A line of lyrics sung by voice that highlights the specific syllable surrounded by 
     *      two asterisks in the given lyricLine
     * @param lyricLine a line of lyrics with one syllable surrounded by asterisks
     * @param voice the voice of the singer for the lyricsLine 
     */
    public Lyrics(String lyricLine, String voice) {
        this.voice = voice;
        this.lyricLine = lyricLine;
        checkRep();
    }
    
    @Override
    public double duration() {
        return 0;
    }

    @Override
    public void play(SequencePlayer player, double atBeat, Map<String, List<String>> voiceToLyricsMap) {
        player.addEvent(atBeat, (Double beat) -> {
            synchronized (voiceToLyricsMap) {
                // Mutate the voiceToLyricsMap for the given voice, then wake up all waiting threads 
                voiceToLyricsMap.get(voice).add(lyricLine);
                voiceToLyricsMap.notifyAll();
            }
        });
    }

    @Override 
    public int hashCode() {
        return lyricLine.hashCode() + voice.hashCode();
    }
    
    @Override 
    public boolean equals(Object that) {
        return that instanceof Lyrics && this.sameValue((Lyrics)that);
    }
    
    @Override
    public String toString() {
        return "(" + voice + ": " + lyricLine + ")";
    }
    
    private boolean sameValue(Lyrics other) {
        return this.lyricLine.equals(other.lyricLine) && this.voice.equals(other.voice);
    }
}
