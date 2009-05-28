package hotheart.starcraft.graphics.grp;

import hotheart.starcraft.configure.FilePaths;
import hotheart.starcraft.files.LoFile;
import hotheart.starcraft.utils.FileSystemUtils;

import java.io.ByteArrayInputStream;
import java.util.TreeMap;

public class GrpRenderFactory {
	private static TreeMap<Integer, Object> resources = new TreeMap<Integer, Object>();

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

	private final static byte[] readWholeImage(int id) {
		return FileSystemUtils.readAllBytes(getAbsoluteFileName(id));
	}

	public final static LoFile getLoData(int id) {
		if (resources.containsKey((Integer) id))
			return (LoFile) resources.get((Integer) id);
		else {
			LoFile res = new LoFile(readWholeImage(id));
			resources.put((Integer) id, res);
			return res;
		}
	}

	public final static AbstractGrpRender getGraphics(int id) {
		if (resources.containsKey((Integer) id))
			return (AbstractGrpRender) resources.get((Integer) id);
		else {
			AbstractGrpRender res = new ArrayGrpImage(readWholeImage(id), id);
			resources.put((Integer) id, res);
			return res;
		}
	}

}
