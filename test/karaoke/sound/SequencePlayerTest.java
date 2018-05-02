package karaoke.sound;

import static org.junit.Assert.fail;

import org.junit.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import karaoke.sound.Instrument;
import karaoke.sound.MidiSequencePlayer;
import karaoke.sound.Pitch;
import karaoke.sound.SequencePlayer;

/**
 * Test cases for Warmup
 * @category no_didit
 */
public class SequencePlayerTest {

    @Test
    public void testPiece1() throws InvalidMidiDataException, MidiUnavailableException {

        Instrument piano = Instrument.PIANO;
        
        // create a new player
        final int beatsPerMinute = 140; // a beat is a quarter note, so this is 120 quarter notes per minute
        final int ticksPerBeat = 64; // allows up to 1/64-beat notes to be played with fidelity
        SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);

        double startBeat = 0;
        double numBeats = 1;
        
        player.addNote(piano, new Pitch('C'), startBeat, numBeats);
        startBeat+=numBeats;
        player.addNote(piano, new Pitch('C'), startBeat, numBeats);
        startBeat+=numBeats;
        player.addNote(piano, new Pitch('C'), startBeat, numBeats*0.75);
        startBeat+=numBeats*0.75;
        player.addNote(piano, new Pitch('D'), startBeat, numBeats*0.25);
        startBeat+=numBeats*0.25;
        player.addNote(piano, new Pitch('E'), startBeat, numBeats);
        startBeat+=numBeats;
        player.addNote(piano, new Pitch('E'), startBeat, numBeats*0.75);
        startBeat+=numBeats*0.75;
        player.addNote(piano, new Pitch('D'), startBeat, numBeats*0.25);
        startBeat+=numBeats*0.25;
        player.addNote(piano, new Pitch('E'), startBeat, numBeats*0.75);
        startBeat+=numBeats*0.75;
        player.addNote(piano, new Pitch('F'), startBeat, numBeats*0.25);
        startBeat+=numBeats*0.25;
        player.addNote(piano, new Pitch('G'), startBeat, numBeats*2);
        startBeat+=numBeats*2;
        player.addNote(piano, new Pitch('C').transpose(Pitch.OCTAVE), startBeat, numBeats/3);
        startBeat+=numBeats/3;
        player.addNote(piano, new Pitch('C').transpose(Pitch.OCTAVE), startBeat, numBeats/3);
        startBeat+=numBeats/3;
        player.addNote(piano, new Pitch('C').transpose(Pitch.OCTAVE), startBeat, numBeats/3);
        startBeat+=numBeats/3;
        player.addNote(piano, new Pitch('G'), startBeat, numBeats/3);
        startBeat+=numBeats/3;
        player.addNote(piano, new Pitch('G'), startBeat, numBeats/3);
        startBeat+=numBeats/3;
        player.addNote(piano, new Pitch('G'), startBeat, numBeats/3);
        startBeat+=numBeats/3;
        player.addNote(piano, new Pitch('E'), startBeat, numBeats/3);
        startBeat+=numBeats/3;
        player.addNote(piano, new Pitch('E'), startBeat, numBeats/3);
        startBeat+=numBeats/3;
        player.addNote(piano, new Pitch('E'), startBeat, numBeats/3);
        startBeat+=numBeats/3;
        player.addNote(piano, new Pitch('C'), startBeat, numBeats/3);
        startBeat+=numBeats/3;
        player.addNote(piano, new Pitch('C'), startBeat, numBeats/3);
        startBeat+=numBeats/3;
        player.addNote(piano, new Pitch('C'), startBeat, numBeats/3);
        startBeat+=numBeats/3;
        player.addNote(piano, new Pitch('G'), startBeat, numBeats*0.75);
        startBeat+=numBeats*0.75;
        player.addNote(piano, new Pitch('F'), startBeat, numBeats*0.25);
        startBeat+=numBeats*0.25;
        player.addNote(piano, new Pitch('E'), startBeat, numBeats*0.75);
        startBeat+=numBeats*0.75;
        player.addNote(piano, new Pitch('D'), startBeat, numBeats*0.25);
        startBeat+=numBeats*0.25;
        player.addNote(piano, new Pitch('C'), startBeat, numBeats*2);
        startBeat+=numBeats*2;
        
        // add a listener at the end of the piece to tell main thread when it's done
        Object lock = new Object();
        player.addEvent(startBeat, (Double beat) -> {
            synchronized (lock) {
                lock.notify();
            }
        });
        
        // print the configured player
        System.out.println(player);

        // play!
        player.play();
        
        // wait until player is done
        // (not strictly needed here, but useful for JUnit tests)
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                return;
            }
        }
        System.out.println("done playing");
    }
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
}
