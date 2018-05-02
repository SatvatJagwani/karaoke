package karaoke.sound;

import org.junit.Test;

import java.util.function.Consumer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

/**
 * Test cases for Warm-up
 * @category no_didit
 */
public class SequencePlayerTest {

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testPiece1() throws InvalidMidiDataException, MidiUnavailableException {

        Instrument piano = Instrument.PIANO;
        
        // create a new player
        final int beatsPerMinute = 140; // a beat is a quarter note, so this is 140 quarter notes per minute
        final int ticksPerBeat = 64; // allows up to 1/64-beat notes to be played with fidelity
        SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);

        double startBeat = 0;
        double numBeats = 1;
        
        // First measure 
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
        
        // Second measure 
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
        
        // Third measure 
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
        
        // Fourth measure 
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
    
    @Test
    public void testPiece2() throws InvalidMidiDataException, MidiUnavailableException {

        Instrument piano = Instrument.PIANO;
        
        // create a new player
        final int beatsPerMinute = 200; // a beat is a quarter note, so this is 200 quarter notes per minute
        final int ticksPerBeat = 64; // allows up to 1/64-beat notes to be played with fidelity
        SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);

        double startBeat = 0;
        double numBeats = 1;
        
        // First measure 
        player.addNote(piano, new Pitch('F').transpose(1), startBeat, numBeats*0.5);
        player.addNote(piano, new Pitch('E').transpose(Pitch.OCTAVE), startBeat, numBeats*0.5);
        startBeat+=numBeats*0.5;
        player.addNote(piano, new Pitch('F').transpose(1), startBeat, numBeats*0.5);
        player.addNote(piano, new Pitch('E').transpose(Pitch.OCTAVE), startBeat, numBeats*0.5);
        startBeat+=numBeats*0.5;
        startBeat+=numBeats*0.5; // Rest
        player.addNote(piano, new Pitch('F').transpose(1), startBeat, numBeats*0.5);
        player.addNote(piano, new Pitch('E').transpose(Pitch.OCTAVE), startBeat, numBeats*0.5);
        startBeat+=numBeats*0.5;
        startBeat+=numBeats*0.5; // Rest
        player.addNote(piano, new Pitch('F').transpose(1), startBeat, numBeats*0.5);
        player.addNote(piano, new Pitch('C').transpose(Pitch.OCTAVE), startBeat, numBeats*0.5);
        startBeat+=numBeats*0.5;
        player.addNote(piano, new Pitch('F').transpose(1), startBeat, numBeats);
        player.addNote(piano, new Pitch('E').transpose(Pitch.OCTAVE), startBeat, numBeats);
        startBeat+=numBeats;
        
        // Second measure 
        player.addNote(piano, new Pitch('G'), startBeat, numBeats);
        player.addNote(piano, new Pitch('B'), startBeat, numBeats);
        player.addNote(piano, new Pitch('G').transpose(Pitch.OCTAVE), startBeat, numBeats);
        startBeat+=numBeats;
        startBeat+=numBeats; //Rest 
        player.addNote(piano, new Pitch('G'), startBeat, numBeats);
        startBeat+=numBeats;
        startBeat+=numBeats; //Rest 
        
        // Third measure 
        player.addNote(piano, new Pitch('C').transpose(Pitch.OCTAVE), startBeat, numBeats*1.5);
        startBeat+=numBeats*1.5;
        player.addNote(piano, new Pitch('G'), startBeat, numBeats*0.5);
        startBeat+=numBeats*0.5;
        startBeat+=numBeats; //Rest 
        player.addNote(piano, new Pitch('E'), startBeat, numBeats);
        startBeat+=numBeats;
        
        // Fourth measure
        player.addNote(piano, new Pitch('E'), startBeat, numBeats*0.5);
        startBeat+=numBeats*0.5;
        player.addNote(piano, new Pitch('A'), startBeat, numBeats);
        startBeat+=numBeats;
        player.addNote(piano, new Pitch('B'), startBeat, numBeats);
        startBeat+=numBeats;
        player.addNote(piano, new Pitch('B').transpose(-1), startBeat, numBeats*0.5);
        startBeat+=numBeats*0.5;
        player.addNote(piano, new Pitch('A'), startBeat, numBeats);
        startBeat+=numBeats;
        
        // Fifth measure 
        player.addNote(piano, new Pitch('G'), startBeat, numBeats*(2.0/3.0));
        startBeat+=numBeats*(2.0/3.0);
        player.addNote(piano, new Pitch('E').transpose(Pitch.OCTAVE), startBeat, numBeats*(2.0/3.0));
        startBeat+=numBeats*(2.0/3.0);
        player.addNote(piano, new Pitch('G').transpose(Pitch.OCTAVE), startBeat, numBeats*(2.0/3.0));
        startBeat+=numBeats*(2.0/3.0);
        player.addNote(piano, new Pitch('A').transpose(Pitch.OCTAVE), startBeat, numBeats);
        startBeat+=numBeats;
        player.addNote(piano, new Pitch('F').transpose(Pitch.OCTAVE), startBeat, numBeats*0.5);
        startBeat+=numBeats*0.5;
        player.addNote(piano, new Pitch('G').transpose(Pitch.OCTAVE), startBeat, numBeats*0.5);
        startBeat+=numBeats*0.5;
        
        // Sixth measure 
        startBeat+=numBeats*0.5; // Rest
        player.addNote(piano, new Pitch('E').transpose(Pitch.OCTAVE), startBeat, numBeats);
        startBeat+=numBeats;
        player.addNote(piano, new Pitch('C').transpose(Pitch.OCTAVE), startBeat, numBeats*0.5);
        startBeat+=numBeats*0.5;
        player.addNote(piano, new Pitch('D').transpose(Pitch.OCTAVE), startBeat, numBeats*0.5);
        startBeat+=numBeats*0.5;
        player.addNote(piano, new Pitch('B'), startBeat, numBeats*(3.0/4.0));
        startBeat+=numBeats*(3.0/4.0);
        startBeat+=numBeats*(3.0/4.0); // Rest
        
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
    
    
    @Test
    public void testPiece3() throws InvalidMidiDataException, MidiUnavailableException {

        Instrument piano = Instrument.PIANO;
        
        // create a new player
        final int beatsPerMinute = 50; // a beat is a quarter note, 50 quarter notes per minute = 100 eighth notes per minute
        final int ticksPerBeat = 64; // allows up to 1/64-beat notes to be played with fidelity
        SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);

        double startBeat = 0;
        double numBeats = 0.5;
        
        // First measure 
        startBeat+=numBeats*4; // Rest 
        player.addNote(piano, new Pitch('D'), startBeat, numBeats*2);
        player.addEvent(startBeat, new Consumer<Double>() {
            public void accept(Double atBeat) {
                System.out.println("*A*-maz-ing grace! How sweet the sound That save a wretch like me.");
            }
        });
        startBeat+=numBeats*2;
        
        // Second measure 
        player.addNote(piano, new Pitch('G'), startBeat, numBeats*4);
        player.addEvent(startBeat, new Consumer<Double>() {
            public void accept(Double atBeat) {
                System.out.println("A-*maz*-ing grace! How sweet the sound That save a wretch like me.");
            }
        });
        startBeat+=numBeats*4;  
        player.addNote(piano, new Pitch('B'), startBeat, numBeats);
        player.addEvent(startBeat, new Consumer<Double>() {
            public void accept(Double atBeat) {
                System.out.println("A-maz-*ing* grace! How sweet the sound That save a wretch like me.");
            }
        });
        startBeat+=numBeats; 
        player.addNote(piano, new Pitch('G'), startBeat, numBeats);
        startBeat+=numBeats; 
        
        // Third measure 
        // TODO 
        
        // Fourth measure
        // TODO
        
        // Fifth measure
        // TODO
        
        // Sixth measure
        // TODO
        
        // Seventh measure
        // TODO
        
        // Eighth measure 
        // TODO 
        
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
    
}
