package karaoke;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

public class WebServerTest {
    
    // serverPort numbers between the tests are different because tests were randomly failing;
    // we suspect this is due to concurrency issues when running the tests and the port was
    // still occupied by another test

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Helper method that gets a reader from a request
    public BufferedReader getURLReader(WebServer server, int voiceIndex) throws IOException {
        final URL valid = new URL("http://localhost:" + server.port() + "/textStream/voice_" + voiceIndex);
        final InputStream input = valid.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
        return reader;
    }
    
    // Testing Strategy for WebServer 
    // 
    // port() and stop():
    //      tested together with tests for start
    // start():
    //      voiceToLyricsMap contains 1 voice, > 1 voice
    //      voiceToLyricsMap mutates once, mutates more than once 
    //      there are 1, > 1 clients reading the same URL
    // printing lyrics real-time with music:
    //      tested in separate file with manual test cases 
    //
    // Cover each part at least once 
    
    // Example test - shows the flow for interacting with the web server
    @Test
    public void testWebServerExample() throws IOException {
        // Initialize the web-server and the voiceToLyricsMap
        final int serverPort = 5000;
        WebServer server = new WebServer(serverPort);
        
        String voice = "voice1";
        SortedMap<String, List<String>> voiceToLyricsMap = new TreeMap<>();
        voiceToLyricsMap.put(voice, new ArrayList<String>());
        
        // Start the web-server 
        server.start(voiceToLyricsMap);
        
        // Get a reader for a URL 
        BufferedReader voiceReader = getURLReader(server, 0);
        voiceReader.readLine(); // Get rid of the first 2k spaces.
        
        // Try to put a new lyric in the map, make sure to use a synchronized block
        String lyricLine = "*A*-maz-ing grace";
        synchronized(voiceToLyricsMap) {
            voiceToLyricsMap.get(voice).add(lyricLine);
            voiceToLyricsMap.notifyAll();
        }
        
        // Make sure the web-server printed out the lyric line
        String responseLine = voiceReader.readLine();
        assertEquals("Expected correct lyrics", lyricLine, responseLine);
        
        // Stop the web-server
        server.stop();
    }
    
    // Covers the following:
    //
    // start():
    //      voiceToLyricsMap contains 1 voice
    //      voiceToLyricsMap mutates once
    //      there are 1 clients reading the same URL
    @Test 
    public void testWebServerSingleVoiceOneLyric() throws IOException {
        // Create a map with one voice 
        String singleVoice = "voice1";
        SortedMap<String, List<String>> voiceToLyricsMap = new TreeMap<>();
        voiceToLyricsMap.put(singleVoice, new ArrayList<>());
        
        // Start the server and get a reader for responses 
        final int serverPort = 5001;
        WebServer server = new WebServer(serverPort);
        server.start(voiceToLyricsMap);
        BufferedReader singleVoiceReader = getURLReader(server, 0);
        singleVoiceReader.readLine(); // Get rid of the first 2k spaces.
        
        // Add one line of lyrics to the map
        String firstLine = "*test*-ing";
        synchronized(voiceToLyricsMap) {
            voiceToLyricsMap.get(singleVoice).add(firstLine);
            voiceToLyricsMap.notifyAll();
        }
        assertEquals("Expected correct response", firstLine, singleVoiceReader.readLine());
        
        server.stop();
    }
    
    // Covers the following:
    //
    //      voiceToLyricsMap contains 1 voice
    //      voiceToLyricsMap mutates more than once 
    //      there are > 1 clients reading the same URL
    @Test 
    public void testWebServerSingleVoiceMultipleLyrics() throws IOException {
        // Create a map with one voice 
        String singleVoice = "voice1";
        SortedMap<String, List<String>> voiceToLyricsMap = new TreeMap<>();
        voiceToLyricsMap.put(singleVoice, new ArrayList<>());
        
        // Start the server and get a reader for responses 
        final int serverPort = 5002;
        WebServer server = new WebServer(serverPort);
        server.start(voiceToLyricsMap);
        BufferedReader singleVoiceReader1 = getURLReader(server, 0);
        BufferedReader singleVoiceReader2 = getURLReader(server, 0);
        singleVoiceReader1.readLine(); // Get rid of the first 2k spaces.
        singleVoiceReader2.readLine(); // Get rid of the first 2k spaces.
        
        // Add first line of lyrics to the map
        String firstLine = "*A*-maz-ing grace how sweet";
        synchronized(voiceToLyricsMap) {
            voiceToLyricsMap.get(singleVoice).add(firstLine);
            voiceToLyricsMap.notifyAll();
        }
        assertEquals("Expected correct response", firstLine, singleVoiceReader1.readLine());
        assertEquals("Expected correct response", firstLine, singleVoiceReader2.readLine());
        
        // Add second line of lyrics
        String secondLine = "A-*maz*-ing grace how sweet";
        synchronized(voiceToLyricsMap) {
            voiceToLyricsMap.get(singleVoice).add(secondLine);
            voiceToLyricsMap.notifyAll();
        }
        assertEquals("Expected correct response", secondLine, singleVoiceReader1.readLine());
        assertEquals("Expected correct response", secondLine, singleVoiceReader2.readLine());
        
        // Add third line of lyrics
        String thirdLine = "A-maz-*ing* grace how sweet";
        synchronized(voiceToLyricsMap) {
            voiceToLyricsMap.get(singleVoice).add(thirdLine);
            voiceToLyricsMap.notifyAll();
        }
        assertEquals("Expected correct response", thirdLine, singleVoiceReader1.readLine());
        assertEquals("Expected correct response", thirdLine, singleVoiceReader2.readLine());
        
        // Add fourth line of lyrics
        String fourthLine = "A-maz-ing *grace* how sweet";
        synchronized(voiceToLyricsMap) {
            voiceToLyricsMap.get(singleVoice).add(fourthLine);
            voiceToLyricsMap.notifyAll();
        }
        assertEquals("Expected correct response", fourthLine, singleVoiceReader1.readLine());
        assertEquals("Expected correct response", fourthLine, singleVoiceReader2.readLine());
        
        server.stop();
    }
    
    // Covers the following:
    //
    //      voiceToLyricsMap contains > 1 voice
    //      voiceToLyricsMap mutates more than once 
    //      there are > 1 clients reading the same URL
    @Test
    public void testWebServerMultipleVoices() throws IOException {
        // Create a map with two voice 
        String firstVoice = "voice1";
        String secondVoice = "voice2";
        SortedMap<String, List<String>> voiceToLyricsMap = new TreeMap<>();
        voiceToLyricsMap.put(firstVoice, new ArrayList<>());
        voiceToLyricsMap.put(secondVoice, new ArrayList<>());
        
        // Start the server and get a reader for responses 
        final int serverPort = 5003;
        WebServer server = new WebServer(serverPort);
        server.start(voiceToLyricsMap);
        BufferedReader firstVoiceReader1 = getURLReader(server, 0);
        BufferedReader firstVoiceReader2 = getURLReader(server, 0);
        BufferedReader secondVoiceReader1 = getURLReader(server, 1);
        BufferedReader secondVoiceReader2 = getURLReader(server, 1);
        firstVoiceReader1.readLine(); // Get rid of the first 2k spaces.
        firstVoiceReader2.readLine(); // Get rid of the first 2k spaces.
        secondVoiceReader1.readLine(); // Get rid of the first 2k spaces.
        secondVoiceReader2.readLine(); // Get rid of the first 2k spaces.
        
        // Add first line for the first voice 
        String firstLineFirstVoice = "*A*-maz-ing grace how sweet";
        synchronized(voiceToLyricsMap) {
            voiceToLyricsMap.get(firstVoice).add(firstLineFirstVoice);
            voiceToLyricsMap.notifyAll();
        }
        assertEquals("Expected correct response", firstLineFirstVoice, firstVoiceReader1.readLine());
        assertEquals("Expected correct response", firstLineFirstVoice, firstVoiceReader2.readLine());
        
        // Add first line for the second voice 
        String firstLineSecondVoice = "*test*-ing";
        synchronized(voiceToLyricsMap) {
            voiceToLyricsMap.get(secondVoice).add(firstLineSecondVoice);
            voiceToLyricsMap.notifyAll();
        }
        assertEquals("Expected correct response", firstLineSecondVoice, secondVoiceReader1.readLine());
        assertEquals("Expected correct response", firstLineSecondVoice, secondVoiceReader2.readLine());
        
        // Add second line for the first voice
        String secondLineFirstVoice = "A-*maz*-ing grace how sweet";
        synchronized(voiceToLyricsMap) {
            voiceToLyricsMap.get(firstVoice).add(secondLineFirstVoice);
            voiceToLyricsMap.notifyAll();
        }
        assertEquals("Expected correct response", secondLineFirstVoice, firstVoiceReader1.readLine());
        assertEquals("Expected correct response", secondLineFirstVoice, firstVoiceReader2.readLine());
        
        // Add second line for the second voice 
        String secondLineSecondVoice = "test-*ing*";
        synchronized(voiceToLyricsMap) {
            voiceToLyricsMap.get(secondVoice).add(secondLineSecondVoice);
            voiceToLyricsMap.notifyAll();
        }
        assertEquals("Expected correct response", secondLineSecondVoice, secondVoiceReader1.readLine());
        assertEquals("Expected correct response", secondLineSecondVoice, secondVoiceReader2.readLine());
        
        server.stop();
    }
    
}
