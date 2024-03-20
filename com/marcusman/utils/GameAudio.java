package com.marcusman.utils;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class GameAudio {
	public static synchronized void play(final File file, int playerX, int playerY, int sourceX, int sourceY, int distance, float volume) {
		double dist = Math.sqrt(Math.pow(playerX - sourceX, 2) + Math.pow(playerY - sourceY, 2));
		float vol = 1.0f - (float) (dist/distance);
		
		float pan = (float) (sourceX - playerX) / distance;
		
		new Thread(() -> {
			try {
				Clip clip = AudioSystem.getClip();
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
				clip.open(inputStream);
				
				FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				volumeControl.setValue(volume * (float) Math.log10(vol));
				FloatControl panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
				panControl.setValue(pan);
				
				clip.start();
			} catch(Exception e) {
				System.err.println("Play sound error: " + e.getMessage());
			}
		}).start();
	}
	
	public static synchronized void play(final File file, int playerX, int playerY, int sourceX, int sourceY) {
		play(file, playerX, playerY, sourceX, sourceY, 1000, 20f);
	}
}
