package com.devmello.music.player;

import com.devmello.music.MusicPlugin;
import com.devmello.music.player.player.MP3Player;
import com.devmello.music.util.Song;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Player {

	public static MP3Player player;
	//public static Song currentSong;
	public static boolean isPlaying = false;
	//public static List<Song> currentSongList = new CopyOnWriteArrayList<Song>();

	public static float vol;
	public static boolean paused;
	public static boolean playerStopped = true;
	public static boolean playerPlaying;


	public static void resume() {
		if (player != null) {
            //setVolume(Music.volumeControl*50);
			player.play();
			isPlaying = true;
			paused = false;
		}
	}



    public static void play(Song song){
        song.play();
    }



	public static void play(String url) {
		stop();

        URL u;
        try {
            u = new URL(url);
            player = new MP3Player(u);
        } catch (MalformedURLException e1) {
        }
        player.setRepeat(false);
        new Thread() {
            @Override
            public void run() {
                try {
        player.play();
        isPlaying = true;
        paused = false;
    } catch (Exception e) {
    }
			}
		}.start();
	}


	public static void playFile(File f) {
		stop();
					player = new MP3Player(f);
					player.setRepeat(false);
					new Thread() {
						@Override
						public void run() {
							try {
					player.play();
					isPlaying = true;
					paused = false;
				} catch (Exception e) {
				}
			}
		}.start();
	}

	public static void pause() {
		if (player != null) {
			player.pause();
			isPlaying = false;
			paused = true;

			//System.out.println(Player.player.getPosition());
		}
	}

	public static boolean isPlaying(){
		return playerPlaying;
	}

	public static boolean isStopped(){
		return playerStopped;
	}

	public static void stop() {
		if (player != null) {
			player.stop();
			player = null;
			isPlaying = false;
			paused = false;
		}
	}

	public static void setVolume(int volume) {
		if (player != null) {
			player.setVolume(volume);
		}
	}

}
