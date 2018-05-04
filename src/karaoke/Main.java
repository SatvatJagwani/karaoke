package karaoke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
     */
    public static void main(String[] args) {
        String path = args[0];
        Piece pieceOfMusic = Piece.parseFromFile(path);

        System.out.println("Title:" + pieceOfMusic.getTitle());
        System.out.println("Composer: " + pieceOfMusic.getNameOfComposer());
        
        final int serverPort = 4567;
        WebServer server = new WebServer(serverPort);
        
        Map<String, List<String>> voiceToLyricsMap = new HashMap<>();
        for (String voice : pieceOfMusic.getVoices()) {
            voiceToLyricsMap.put(voice, new ArrayList<String>());
        }
        
        server.run(voiceToLyricsMap);
        
        for (String voice : pieceOfMusic.getVoices()) {
            System.out.println("For voice " + voice + ", go to http://localhost:" + serverPort + "/textStream/" + voice);
        }
        
        promptEnterKey();
        
        final double warmup = 0.125;
        SequencePlayer player = pieceOfMusic.createPlayer();
        pieceOfMusic.getMusic().play(player, warmup, voiceToLyricsMap);
        
        player.play();
    }
    
    /**
     * Prompts the client to press the enter key, we got this code from 
     * https://stackoverflow.com/questions/26184409/java-console-prompt-for-enter-input-before-moving-on
     */
    public static void promptEnterKey(){
        System.out.println("Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
     }
}
