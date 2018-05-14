package karaoke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.net.InetAddress;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import edu.mit.eecs.parserlib.UnableToParseException;
import karaoke.sound.SequencePlayer;

/**
 * Main entry point of your application.
 */
public class Main {

    /**
     * Allows the user to play a piece of music and stream lyrics to a web server
     * The title and composer are printed out along with instructions on how to 
     * stream the lyrics with a web browser. Instructions on how to start music play-back
     * are also printed out. 
     * @param args contains a path to a valid abc file 
     * @throws IOException if no abc file is found at path filename
     * @throws UnableToParseException if the abc file is not in right format.
     * @throws InvalidMidiDataException if MIDI device unavailable
     * @throws MidiUnavailableException if MIDI play fails
     */
    public static void main(String[] args) throws IOException, UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        // Thread safety argument: 
        //      The main thread only creates multiple threads through the web-server 
        //      The only data type modified on multiple threads is the voiceToLyricsMap
        //      Every time the voiceToLyricsMap is read or modified, it is done inside a 
        //          synchronized block 
        
        // Get the filename and parse the file into a Piece 
        String path = args[0];
        Piece piece = Piece.parseFromFile(path);

        // Print out the title and composer
        System.out.println("Title:" + piece.getTitle());
        System.out.println("Composer: " + piece.getNameOfComposer());
        
        // Create voiceToLyricsMap and SequencePlayer
        SortedMap<String, List<String>> voiceToLyricsMap = new TreeMap<>();
        for (String voice : piece.getVoices()) {
            voiceToLyricsMap.put(voice, new ArrayList<String>());
        }
        SequencePlayer player = piece.createPlayer();
        
        // Start the web-server
        final int serverPort = 5002;
        WebServer server = new WebServer(serverPort);
        server.start(voiceToLyricsMap);
        
        // Print out URL's for each voice 
        String myIP = InetAddress.getLocalHost().getHostAddress();
        synchronized(voiceToLyricsMap) {
            int index = 0;
            for (String voice : voiceToLyricsMap.keySet()) {
                System.out.println("For voice " + voice + 
                        ", go to http://" + myIP + ":" + serverPort + "/textStream/voice_" + index);
                index++;
            }
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
        
        // Then close the server and terminate the program
        server.stop();
        System.exit(1);
    }
    
    /**
     * Prompts the client to press the enter key, we got this code from 
     * https://stackoverflow.com/questions/26184409/java-console-prompt-for-enter-input-before-moving-on
     */
    public static void promptEnterKey(){
        System.out.println("Press \"ENTER\" to begin playing the piece of music");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
     }
}
