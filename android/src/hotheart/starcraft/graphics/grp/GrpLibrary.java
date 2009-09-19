package hotheart.starcraft.graphics.grp;

import hotheart.starcraft.configure.FilePaths;
import hotheart.starcraft.files.GrpFile;
import hotheart.starcraft.utils.FileSystemUtils;

import java.io.ByteArrayInputStream;

public class GrpLibrary {

	static byte[] images;

	public final static void init(byte[] data) {
		images = data;
	}

	public final static String getFileName(int id) {
		ByteArrayInputStream is = new ByteArrayInputStream(images);

		is.skip(id * 2);

		int offset = (is.read() & 0xFF) + ((is.read() & 0xFF) << 8);

		is.skip(offset - id * 2 - 2);

		StringBuilder sb = new StringBuilder();
		int ch = is.read() & 0xFF;

		while (ch != 0) {
			sb.append((char) ch);
			ch = is.read() & 0xFF;
		}

		return sb.toString();
	}

	public final static String getAbsoluteFileName(int id) {
		return FilePaths.UNITS_FOLDER + getFileName(id).replace('\\', '/');
	}

	private final static byte[] readWholeImage(String fileName) {
		return FileSystemUtils.readAllBytes(fileName);
	}

//	public final static LoFile getLoData(int id) {
//		if (resources.containsKey((Integer) id))
//			return (LoFile) resources.get((Integer) id);
//		else {
//			LoFile res = new LoFile(readWholeImage(id));
//			resources.put((Integer) id, res);
//			return res;
//		}
//	}

	public final static GrpFile getGraphics(int id) {
		return new GrpFile(readWholeImage(getAbsoluteFileName(id)));
	}
	
	public final static GrpFile getGraphics(String path) {
		return new GrpFile(readWholeImage(path));
	}

}
