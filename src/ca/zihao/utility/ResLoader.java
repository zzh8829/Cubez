package ca.zihao.utility;

/**
 * Created by Zihao on 5/29/2014.
 */

import java.io.InputStream;
import java.net.URL;

public class ResLoader {
	public static URL load(String path) {
		return ResLoader.class.getClassLoader().getResource(path);
	}

	public static InputStream loadAsStream(String path) {
		return ResLoader.class.getClassLoader().getResourceAsStream(path);
	}
}
