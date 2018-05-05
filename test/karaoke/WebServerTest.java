package karaoke;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class WebServerTest {

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Helper method that gets a reader from a request
    public BufferedReader getURLReader(WebServer server, String voice) throws IOException {
        final URL valid = new URL("http://localhost:" + server.port() + "/textStream/" + voice);
        final InputStream input = valid.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
        return reader;
    }
    
    // TODO: Testing Strategy
    
    
    // Example test 
    @Test
    public void testWebServerExample() throws IOException {
        // Initialize the web-server and the voiceToLyricsMap
        final int serverPort = 4567;
        WebServer server = new WebServer(serverPort);
        
        String voice = "voice1";
        Map<String, List<String>> voiceToLyricsMap = new HashMap<>();
        voiceToLyricsMap.put("voice", new ArrayList<String>());
        
        // Start the web-server 
        server.start(voiceToLyricsMap);
        
        // Get a reader for a URL 
        BufferedReader voiceReader = getURLReader(server, voice);
        
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
    
    // TODO: Other Tests
    
}
