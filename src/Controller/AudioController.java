package Controller;

import processing.core.PApplet;

import ddf.minim.Minim;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.AudioBuffer;
import ddf.minim.analysis.FFT;

public class AudioController {
  private int frameSize = 512;
  private int sampleRate = 44100;

  private float[] bands;
  private float[] smoothedBands;

  private Minim minim;
  private AudioInput audioInput;
  private AudioPlayer audioPlayer;
  private AudioBuffer audioBuffer;
  private FFT fft;

  private float amplitude = 0;
  private float smothedAmplitude = 0;

  private float log2(float f) {
    return PApplet.log(f) / PApplet.log(2f);
  }

  public void startMinim() {
    minim = new Minim(this);

    fft = new FFT(frameSize, sampleRate);

    bands = new float[(int) log2(frameSize)];
    smoothedBands = new float[bands.length];
  }

  public void calculateFFT() throws AudioException {
    fft.window(FFT.HAMMING);
    if (audioBuffer != null) {
      fft.forward(audioBuffer);
    } else {
      throw new AudioException("You must call start listening or loadAudio before calling fft");
    }
  }

  protected void calculateFrequencyBands() {
    for (int i = 0; i < bands.length; i++) {
      int start = (int) PApplet.pow(2, i) - 1;
      int w = (int) PApplet.pow(2, i);
      int end = start + w;

      float average = 0;

      for (int j = start; j < end; j++) {
        average += fft.getBand(j) * (j + 1);
      }

      average /= (float) w;
      bands[i] = average * 5.0f;
      smoothedBands[i] = PApplet.lerp(smoothedBands[i], bands[i], 0.05f);
    }
  }

  public void loadAudio(String fileName) {
    audioPlayer = minim.loadFile(fileName, frameSize);
    audioBuffer = audioPlayer.left;
  }

  public float[] getBands() {
    return bands;
  }

  public float[] getSmoothedBands() {
    return smoothedBands;
  }

  public AudioInput getAudioInput() {
    return audioInput;
  }


  public AudioBuffer getAudioBuffer() {
    return audioBuffer;
  }

  public float getAmplitude() {
    return amplitude;
  }

  public float getSmoothedAmplitude() {
    return smothedAmplitude;
  }

  public AudioPlayer getAudioPlayer() {
    return audioPlayer;
  }
}