package karaoke;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Represents a WebServer that streams each set of lyrics for a voice to
 * a different web-site. 
 */
public class WebServer {
    
    private final HttpServer server;
    private final int port;
    
    // Abstraction function:TODO
    // Representation invariant:TODO
    // Safety from rep exposure:TODO
    // Thread safety argument:
    //     Not a thread safe datatype by itself.
    //     We only use one web server.
    //     However, its use of multiple threads is safe.
    //     Server acquires the lock for voiceToLyricsMap whenever checking for mutation.
    //     Thread safety by synchronization (through locking).
    
    private void checkRep() {
        assert server!=null;
        assert port>=1 && port<=65535;
    }
    
    /**
     * Initialize an HttpServer at the given serverPort
     * @param serverPort port for the HttpServer 
     */
    public WebServer(int serverPort) throws IOException {
        // make a web server
        server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        port = serverPort;
        checkRep();
    }

    /**
     * @return the port for the HttpServer 
     */
    public int port() {
        checkRep();
        return port;
    }
    
    /**
     * Starts running a server that handles multiple voices concurrently. 
     * For each voice in the voiceToLyrics map, the address streaming 
     * the lyrics for that voice is http://localhost:serverPort/textStream/voice 
     * @param voiceToLyricsMap a map that maps a voice to a list of lyrics, whenever 
     *        a lyric is appended to the list of lyrics, the web server prints 
     *        out the new line at the address for that voice 
     */
    public void start(Map<String, List<String>> voiceToLyricsMap) {
        // handle concurrent requests with multiple threads
        server.setExecutor(Executors.newCachedThreadPool());     
        for(String voice : voiceToLyricsMap.keySet()) {
            server.createContext("/textStream/" + voice, (exchange)->
            {
                try {
                    textStream(exchange, voice, voiceToLyricsMap);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            });
        }
        server.start();
    }
    
    /**
     * Stop this server. Once stopped, this server cannot be restarted.
     */
    public void stop() {
        server.stop(0);        
    }
    
    /**
     * Handles the text stream for a specific exchange and voice 
     * @param exchange request/reply object
     * @param voice the voice to be handles by this exchange 
     * @param voiceToLyricsMap a map that maps a voice to a list of lyrics, whenever 
     *        a lyric is appended to the list of lyrics, the web server prints 
     *        out the new line at the address for the specified voice 
     */
    private static void textStream(HttpExchange exchange, String voice, Map<String, List<String>> voiceToLyricsMap) throws IOException, InterruptedException {

        // plain text response
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");

        // must call sendResponseHeaders() before calling getResponseBody()
        final int successCode = 200;
        final int lengthNotKnownYet = 0;
        exchange.sendResponseHeaders(successCode, lengthNotKnownYet);

        // get output stream to write to web browser
        final boolean autoflushOnPrintln = true;
        PrintWriter out = new PrintWriter(
                              new OutputStreamWriter(
                                  exchange.getResponseBody(), 
                                  StandardCharsets.UTF_8), 
                              autoflushOnPrintln);
        
        try {
            // IMPORTANT: some web browsers don't start displaying a page until at least 2K bytes
            // have been received.  So we'll send a line containing 2K spaces first.
            final int enoughBytesToStartStreaming = 2048;
            for (int i = 0; i < enoughBytesToStartStreaming; ++i) {
                out.print(' ');
            }
            out.println(); // also flushes
            int counter = 0;
            while(true) {
                synchronized(voiceToLyricsMap) {
                    while(voiceToLyricsMap.get(voice).size()==counter) {
                        voiceToLyricsMap.notifyAll();
                        voiceToLyricsMap.wait();
                    }
                    counter = voiceToLyricsMap.get(voice).size();
                    out.println(voiceToLyricsMap.get(voice).get(counter-1));
                    voiceToLyricsMap.notifyAll();
                }
            }
            
        } finally {
            exchange.close();
        }
    }
    
}
