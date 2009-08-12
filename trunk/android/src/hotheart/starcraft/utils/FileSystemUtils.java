package hotheart.starcraft.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileSystemUtils {

	public static byte[] readAllBytes(String fileName) {
		try {

			FileInputStream is = new FileInputStream(fileName);
			byte[] data = new byte[is.available()];
			is.read(data);
			is.close();

			return data;

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
