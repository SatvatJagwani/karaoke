package karaoke.sound;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.junit.Test;

import edu.mit.eecs.parserlib.UnableToParseException;
import karaoke.Music;
import karaoke.Piece;

/**
 * A class for testing correct sound from Music, Piece, and WebServer ADTs.
 * @category no_didit
 */
public class AllADTsTest {

    @Test
    public void testMusic() throws MidiUnavailableException, InvalidMidiDataException {
        Music music = null; //TODO
        Map<String, List<String>> voiceToLyricsMap = new HashMap<>(); //TODO
        Instrument piano = Instrument.PIANO;
        
        // create a new player
        final int beatsPerMinute = 100; // a beat is a quarter note, so this is 100 quarter notes per minute
        final int ticksPerBeat = 64; // allows up to 1/64-beat notes to be played with fidelity
        SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);
        music.play(player, 0, voiceToLyricsMap);
        player.play();
    }
    
    @Test
    public void testPiece() throws UnableToParseException, IOException, MidiUnavailableException, InvalidMidiDataException {
        Piece piece = Piece.parseFromFile("sample-abc/little_night_music.abc");
        Map<String, List<String>> voiceToLyricsMap = new HashMap<>();
        for (String voice : piece.getVoices()) {
            voiceToLyricsMap.put(voice, new ArrayList<String>());
        }
        SequencePlayer player = piece.createPlayer();
        piece.getMusic().play(player, 0, voiceToLyricsMap);
        player.play();
    }
    
    @Test
    public void testWebServer() {
        fail("Not yet implemented");
    }

}
