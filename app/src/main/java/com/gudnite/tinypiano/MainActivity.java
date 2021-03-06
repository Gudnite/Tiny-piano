package com.gudnite.tinypiano;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.billthefarmer.mididriver.MidiDriver;

public class MainActivity extends AppCompatActivity implements MidiDriver.OnMidiStartListener, View.OnTouchListener {

    private MidiDriver midiDriver;
    private byte[] event;
    private int[] config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout whitenotes = findViewById(R.id.blackKeys);
        System.out.println("hello1");
        LinearLayout blacknotes = findViewById(R.id.whiteKeys);
        System.out.println("hello2");
        int wnotes = whitenotes.getChildCount();
        for (int i=0; i<wnotes; i++){
            whitenotes.getChildAt(i).setOnTouchListener(this);
        }
        System.out.println("hello3");
        int bnotes = blacknotes.getChildCount();
        for (int j=0; j<bnotes; j++){
            blacknotes.getChildAt(j).setOnTouchListener(this);
        }
        System.out.println("hello4");

        // Instantiate the driver.
        midiDriver = new MidiDriver();
        // Set the listener.
        midiDriver.setOnMidiStartListener(this);

        Button quitbtn = (Button) findViewById(R.id.quitBtn);
        quitbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
                System.exit(0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();

        // Get the configuration.
        config = midiDriver.config();

        // Print out the details.
       /* Log.d(this.getClass().getName(), "maxVoices: " + config[0]);
        Log.d(this.getClass().getName(), "numChannels: " + config[1]);
        Log.d(this.getClass().getName(), "sampleRate: " + config[2]);
        Log.d(this.getClass().getName(), "mixBufferSize: " + config[3]);*/
    }


    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }

    private void playNote(int offset) {

        // Construct a note ON message for the middle C at maximum velocity on channel 1:
        event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = (byte) (0x3C+offset);  // 0x3C = middle C
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        // Internally this just calls write() and can be considered obsoleted:
        //midiDriver.queueEvent(event);

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);
    }

    private void stopNote(int offset) {

        // Construct a note OFF message for the middle C at minimum velocity on channel 1:
        event = new byte[3];
        event[0] = (byte) (0x80 | 0x00);  // 0x80 = note Off, 0x00 = channel 1
        event[1] = (byte) (0x3C+offset);  // 0x3C = middle C
        event[2] = (byte) 0x00;  // 0x00 = the minimum velocity (0)

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);

    }

    @Override
    public void onMidiStart() {
        //Log.d(this.getClass().getName(), "onMidiStart()");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        //Log.d(this.getClass().getName(), "Motion event: " + event);
        int note = Integer.parseInt(v.getTag().toString());

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(this.getClass().getName(), "MotionEvent.ACTION_DOWN");
                playNote(note);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(this.getClass().getName(), "MotionEvent.ACTION_UP");
                stopNote(note);
            }
        return false;
    }
}