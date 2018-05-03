package karaoke;

import java.util.List;
import java.util.Map;

import karaoke.sound.SequencePlayer;


public class Concat implements Music {

    @Override
    public double duration() {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void play(SequencePlayer player, double atBeat, Map<String, List<String>> voiceToLyricsMap) {
        throw new RuntimeException("Unimplemented");
    }

}
