package hotheart.starcraft.sounds;

import hotheart.starcraft.configure.BuildParameters;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;

public class StarcraftSoundPool {
	private static byte[] sfx;
	private static byte[] sfx_dat;
	public static final void init(byte[] sfxTbl, byte[] sfxDat)
	{
		sfx = sfxTbl;
		sfx_dat = sfxDat;
	}
	
	public static final void playSound(int id)
	{
		if (!BuildParameters.PLAY_SOUNDS)
			return;
		
		int rawId = getTblId(id);
		String fileName = "/sdcard/starcraft/sound/"
				+ getFileName(rawId).replace('\\', '/');
		MediaPlayer mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(fileName);
			mMediaPlayer.prepare();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private final static int getTblId(int ind)
	{
		int offset = ind*4;
		
		return (sfx_dat[offset]&0xFF) + ((sfx_dat[offset + 1]&0xFF)<<8);
	}
	public final static String getFileName(int id)
	{
		ByteArrayInputStream is = new ByteArrayInputStream(sfx);
		
		is.skip(id*2);
			
		int offset = (is.read()&0xFF) + ((is.read()&0xFF)<<8);
			
		is.skip(offset - id*2 - 2);
		StringBuilder sb = new StringBuilder();
		int ch = is.read();
			
		while(ch != 0)
		{
			sb.append((char)ch);
			ch = is.read();
		}
		
		return sb.toString();
	}
}