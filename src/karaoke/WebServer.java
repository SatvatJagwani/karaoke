package karaoke;
import java.util.List;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Represents a WebServer that streams each set of lyrics for a voice to
 * a different web-site. 
 */
public class WebServer {
    
    /**
     * Initialize an HttpServer at the given serverPort
     * @param serverPort port for the HttpServer 
     */
    public WebServer(int serverPort) {
        throw new RuntimeException("Unimplemented");
    }

    /**
     * @return the port for the HttpServer 
     */
    public int port() {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Starts running a server that handles multiple voices concurrently. 
     * For each voice in the voiceToLyrics map, the address streaming 
     * the lyrics for that voice is http://localhost:serverPort/textStream/voice 
     * @param voiceToLyricsMap a map that maps a voice to a list of lyrics, whenever 
     *        a lyric is appended to the list of lyrics, the web server prints 
     *        out the new line at the address for that voice 
     */
    public void run(Map<String, List<String>> voiceToLyricsMap) {
        throw new RuntimeException("Unimplemented");        
    }
    
    /**
     * Handles the text stream for a specific exchange and voice 
     * @param exchange request/reply object
     * @param voice the voice to be handles by this exchange 
     * @param voiceToLyricsMap a map that maps a voice to a list of lyrics, whenever 
     *        a lyric is appended to the list of lyrics, the web server prints 
     *        out the new line at the address for the specified voice 
     */
    private static void textStream(HttpExchange exchange, String voice, Map<String, List<String>> voiceToLyricsMap) {
        throw new RuntimeException("Unimplemented");        
    }
    
}
