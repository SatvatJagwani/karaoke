package karaoke.sound;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.junit.Test;

import edu.mit.eecs.parserlib.UnableToParseException;
import karaoke.Main;
import karaoke.Music;
import karaoke.Piece;
import karaoke.WebServer;

/**
 * A class for testing correct sound from Music, Piece, and WebServer ADTs.
 * @category no_didit
 */
public class AllADTsTest {
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Testing strategy:
    //
    // Music.play() and SequencePlayer.play()
    //      music created with music constructors, music created with parser 
    // voiceToLyricsMap
    //      streamed to web server, not streamed to web server
    //      number of voices is 1, >1 
    //
    // Cover each part at least once 
    
    // Covers the following:
    //
    // Music.play() and SequencePlayer.play()
    //      music created with music constructors, music created with parser 
    // voiceToLyricsMap
    //      not streamed to web server
    //      number of voices is 1
    @Test
    public void testMusic() throws MidiUnavailableException, InvalidMidiDataException {
        Music music = Music.rest(0);
        music = Music.concat(music, Music.together(
                Music.note(1, new Pitch('C'), Instrument.PIANO), 
                Music.lyrics("*C*-D-E-F-G-A-B-c", "voice1")));
        music = Music.concat(music, Music.together(
                Music.note(1, new Pitch('D'), Instrument.PIANO), 
                Music.lyrics("C-*D*-E-F-G-A-B-c", "voice1")));
        music = Music.concat(music, Music.together(
                Music.note(1, new Pitch('E'), Instrument.PIANO), 
                Music.lyrics("C-D-*E*-F-G-A-B-c", "voice1")));
        music = Music.concat(music, Music.together(
                Music.note(1, new Pitch('F'), Instrument.PIANO), 
                Music.lyrics("C-D-E-*F*-G-A-B-c", "voice1")));
        music = Music.concat(music, Music.together(
                Music.note(1, new Pitch('G'), Instrument.PIANO), 
                Music.lyrics("C-D-E-F-*G*-A-B-c", "voice1")));
        music = Music.concat(music, Music.together(
                Music.note(1, new Pitch('A'), Instrument.PIANO), 
                Music.lyrics("C-D-E-F-G-*A*-B-c", "voice1")));
        music = Music.concat(music, Music.together(
                Music.note(1, new Pitch('B'), Instrument.PIANO), 
                Music.lyrics("C-D-E-F-G-A-*B*-c", "voice1")));
        music = Music.concat(music, Music.together(
                Music.note(1, new Pitch('C').transpose(Pitch.OCTAVE), Instrument.PIANO), 
                Music.lyrics("C-D-E-F-G-A-B-*c*", "voice1")));

        Map<String, List<String>> voiceToLyricsMap = new HashMap<>(); 
        voiceToLyricsMap.put("voice1", new ArrayList<String>());
        
        // create a new player
        final int beatsPerMinute = 100; // a beat is a quarter note, so this is 100 quarter notes per minute
        final int ticksPerBeat = 64; // allows up to 1/64-beat notes to be played with fidelity
        final double warmup = 0.125;
        SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);
        music.play(player, warmup, voiceToLyricsMap);
        
        // Add a listener at the end of the piece to tell main thread when it's done
        Object lock = new Object();
        player.addEvent(music.duration() + warmup, (Double beat) -> {
            synchronized (lock) {
                lock.notify();
            }
        });
        
        // Play the music with the sequence player 
        player.play();
        
        // Wait until player is done
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                return;
            }
        }
                        
        Map<String, List<String>> correctVoiceToLyricsMap = new HashMap<>(); 
        correctVoiceToLyricsMap.put("voice1", new ArrayList<String>());  
        correctVoiceToLyricsMap.get("voice1").add("*C*-D-E-F-G-A-B-c");
        correctVoiceToLyricsMap.get("voice1").add("C-*D*-E-F-G-A-B-c");
        correctVoiceToLyricsMap.get("voice1").add("C-D-*E*-F-G-A-B-c");
        correctVoiceToLyricsMap.get("voice1").add("C-D-E-*F*-G-A-B-c");
        correctVoiceToLyricsMap.get("voice1").add("C-D-E-F-*G*-A-B-c");
        correctVoiceToLyricsMap.get("voice1").add("C-D-E-F-G-*A*-B-c");
        correctVoiceToLyricsMap.get("voice1").add("C-D-E-F-G-A-*B*-c");
        correctVoiceToLyricsMap.get("voice1").add("C-D-E-F-G-A-B-*c*");
        assertEquals("Expected correct map", correctVoiceToLyricsMap, voiceToLyricsMap);
    }
    
    // Covers the following:
    //
    // Music.play() and SequencePlayer.play()
    //      music created with parser 
    // voiceToLyricsMap
    //      not streamed to web server
    //      number of voices is 1
    @Test
    public void testPiece() throws UnableToParseException, IOException, MidiUnavailableException, InvalidMidiDataException {
        Piece piece = Piece.parseFromFile("sample-abc/little_night_music.abc");
        Map<String, List<String>> voiceToLyricsMap = new HashMap<>();
        for (String voice : piece.getVoices()) {
            voiceToLyricsMap.put(voice, new ArrayList<String>());
        }
        SequencePlayer player = piece.createPlayer();
        final double warmup = 0.125;
        Music music = piece.getMusic();
        music.play(player, warmup, voiceToLyricsMap);
        
        // Add a listener at the end of the piece to tell main thread when it's done
        Object lock = new Object();
        player.addEvent(music.duration() + warmup, (Double beat) -> {
            synchronized (lock) {
                lock.notify();
            }
        });
        
        // Play the music with the sequence player 
        player.play();
        
        // Wait until player is done
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                return;
            }
        }
        
        Map<String, List<String>> correctVoiceToLyricsMap = new HashMap<>(); 
        correctVoiceToLyricsMap.put("voice1", new ArrayList<String>());  
        for (int i = 0; i < 17; i++) {
            correctVoiceToLyricsMap.get("voice1").add("*no lyrics*");
        }
        assertEquals("Expected correct map", correctVoiceToLyricsMap, voiceToLyricsMap);
    }
    
    // Covers the following:
    //
    // Music.play() and SequencePlayer.play()
    //      music created with music constructors
    // voiceToLyricsMap
    //      streamed to web server
    //      number of voices is >1 
    @Test
    public void testWebServerWithMusic() throws IOException, MidiUnavailableException, InvalidMidiDataException {
        // Create Music with two voices 
        Music firstVoice = Music.rest(.5);
        firstVoice = Music.concat(firstVoice, Music.together(
                Music.note(1, new Pitch('C'), Instrument.PIANO), 
                Music.lyrics("*C*-D-E-F-G-A-B-c", "voice1")));
        firstVoice = Music.concat(firstVoice, Music.together(
                Music.note(1, new Pitch('D'), Instrument.PIANO), 
                Music.lyrics("C-*D*-E-F-G-A-B-c", "voice1")));
        firstVoice = Music.concat(firstVoice, Music.together(
                Music.note(1, new Pitch('E'), Instrument.PIANO), 
                Music.lyrics("C-D-*E*-F-G-A-B-c", "voice1")));
        firstVoice = Music.concat(firstVoice, Music.together(
                Music.note(1, new Pitch('F'), Instrument.PIANO), 
                Music.lyrics("C-D-E-*F*-G-A-B-c", "voice1")));
        firstVoice = Music.concat(firstVoice, Music.together(
                Music.note(1, new Pitch('G'), Instrument.PIANO), 
                Music.lyrics("C-D-E-F-*G*-A-B-c", "voice1")));
        firstVoice = Music.concat(firstVoice, Music.together(
                Music.note(1, new Pitch('A'), Instrument.PIANO), 
                Music.lyrics("C-D-E-F-G-*A*-B-c", "voice1")));
        firstVoice = Music.concat(firstVoice, Music.together(
                Music.note(1, new Pitch('B'), Instrument.PIANO), 
                Music.lyrics("C-D-E-F-G-A-*B*-c", "voice1")));
        firstVoice = Music.concat(firstVoice, Music.together(
                Music.note(1, new Pitch('C').transpose(Pitch.OCTAVE), Instrument.PIANO), 
                Music.lyrics("C-D-E-F-G-A-B-*c*", "voice1")));
        
        Music secondVoice = Music.rest(0);
        secondVoice = Music.concat(secondVoice, Music.together(
                Music.note(1, new Pitch('C').transpose(Pitch.OCTAVE), Instrument.PIANO), 
                Music.lyrics("*c*-B-A-G-F-E-D-C", "voice2")));
        secondVoice = Music.concat(secondVoice, Music.together(
                Music.note(1, new Pitch('B'), Instrument.PIANO), 
                Music.lyrics("c-*B*-A-G-F-E-D-C", "voice2")));
        secondVoice = Music.concat(secondVoice, Music.together(
                Music.note(1, new Pitch('A'), Instrument.PIANO), 
                Music.lyrics("c-B-*A*-G-F-E-D-C", "voice2")));
        secondVoice = Music.concat(secondVoice, Music.together(
                Music.note(1, new Pitch('G'), Instrument.PIANO), 
                Music.lyrics("c-B-A-*G*-F-E-D-C", "voice2")));
        secondVoice = Music.concat(secondVoice, Music.together(
                Music.note(1, new Pitch('F'), Instrument.PIANO), 
                Music.lyrics("c-B-A-G-*F*-E-D-C", "voice2")));
        secondVoice = Music.concat(secondVoice, Music.together(
                Music.note(1, new Pitch('E'), Instrument.PIANO), 
                Music.lyrics("c-B-A-G-F-*E*-D-C", "voice2")));
        secondVoice = Music.concat(secondVoice, Music.together(
                Music.note(1, new Pitch('D'), Instrument.PIANO), 
                Music.lyrics("c-B-A-G-F-E-*D*-C", "voice2")));
        secondVoice = Music.concat(secondVoice, Music.together(
                Music.note(1, new Pitch('C'), Instrument.PIANO), 
                Music.lyrics("c-B-A-G-F-E-D-*C*", "voice2")));
        
        Music music = Music.together(firstVoice, secondVoice);
        
        // Create a voiceToLyricsMap 
        Map<String, List<String>> voiceToLyricsMap = new HashMap<>();
        voiceToLyricsMap.put("voice1", new ArrayList<String>());
        voiceToLyricsMap.put("voice2", new ArrayList<String>());
        
        // Start the web-server
        final int serverPort = 5000;
        WebServer server = new WebServer(serverPort);
        server.start(voiceToLyricsMap);
        
        // create a new player
        final int beatsPerMinute = 100; // a beat is a quarter note, so this is 100 quarter notes per minute
        final int ticksPerBeat = 64; // allows up to 1/64-beat notes to be played with fidelity
        final double warmup = 0.125;
        SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);
        music.play(player, warmup, voiceToLyricsMap);
        
        /*
         * Manually: navigate to the printed out URL's, open each URL on more than one tab
         */
        System.out.println("Open each URL on more than one tab");
        synchronized(voiceToLyricsMap) {
            for (String voice : voiceToLyricsMap.keySet()) {
                System.out.println("For voice " + voice + ", go to http://localhost:" + serverPort + "/textStream/" + voice);
            }
        }
        
        Main.promptEnterKey();
        
        // Add a listener at the end of the piece to tell main thread when it's done
        Object lock = new Object();
        player.addEvent(music.duration() + warmup, (Double beat) -> {
            synchronized (lock) {
                lock.notify();
            }
        });
        
        // Play the music with the sequence player 
        player.play();
        
        // Wait until player is done
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                return;
            }
        }
        
        // Then close the server
        server.stop();
    }
    
    // Covers the following:
    //
    // Music.play() and SequencePlayer.play()
    //      music created with parser 
    // voiceToLyricsMap
    //      streamed to web server
    //      number of voices is >1 
    @Test
    public void testWebServerWithParser() throws IOException, UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        // Parse the file, create a voiceToLyricsMap and a sequence player 
        Piece piece = Piece.parseFromFile("sample-abc/piece4.abc");
        Map<String, List<String>> voiceToLyricsMap = new HashMap<>();
        for (String voice : piece.getVoices()) {
            voiceToLyricsMap.put(voice, new ArrayList<String>());
        }
        SequencePlayer player = piece.createPlayer();
        
        // Start the web-server
        final int serverPort = 5000;
        WebServer server = new WebServer(serverPort);
        server.start(voiceToLyricsMap);
        
        /*
         * Manually: navigate to the printed out URL's, open each URL on more than one tab
         */
        System.out.println("Open each URL on more than one tab");
        for (String voice : piece.getVoices()) {
            System.out.println("For voice " + voice + ", go to http://localhost:" + serverPort + "/textStream/" + voice);
        }
        
        Main.promptEnterKey();
        
        // Initialize the sequence player 
        final double warmup = 0.125;
        Music music = piece.getMusic();
        music.play(player, warmup, voiceToLyricsMap);
        
        // Add a listener at the end of the piece to tell main thread when it's done
        Object lock = new Object();
        player.addEvent(music.duration() + warmup, (Double beat) -> {
            synchronized (lock) {
                lock.notify();
            }
        });
        
        // Play the music with the sequence player 
        player.play();
        
        // Wait until player is done
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                return;
            }
        }
        
        // Then close the server
        server.stop();
    }

}
