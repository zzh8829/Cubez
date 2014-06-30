package ca.zihao.utility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

/**
 * Created by Zihao on 5/22/2014.
 */

public class IconLoader {
	public static ByteBuffer[] loadIcon(URL url) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(url);
		} catch(IOException e) {
			e.printStackTrace();
		}
		return loadIconImpl(image);
	}

	public static ByteBuffer[] loadIcon(File file) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(file);
		} catch(IOException e) {
			e.printStackTrace();
		}
		return loadIconImpl(image);
	}

	static ByteBuffer[] loadIconImpl(BufferedImage image) {
		ByteBuffer[] buffers = null;
		String OS = System.getProperty("os.name").toUpperCase();
		if(OS.contains("WIN")) {
			buffers = new ByteBuffer[2];
			buffers[0] = convertImage(image, 16);
			buffers[1] = convertImage(image, 32);
		} else if(OS.contains("MAC")) {
			buffers = new ByteBuffer[1];
			buffers[0] = convertImage(image, 128);
		} else {
			buffers = new ByteBuffer[1];
			buffers[0] = convertImage(image, 32);
		}
		return buffers;
	}

	private static ByteBuffer convertImage(BufferedImage image, int dimension) {
		Image newImage = image.getScaledInstance(dimension, dimension, Image.SCALE_DEFAULT);
		image = new BufferedImage(newImage.getWidth(null), newImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		g.drawImage(newImage, 0, 0, null);
		g.dispose();
		byte[] buffer = new byte[image.getWidth() * image.getHeight() * 4];
		int counter = 0;
		for(int i = 0; i < image.getHeight(); i++) {
			for(int j = 0; j < image.getWidth(); j++) {
				int colorSpace = image.getRGB(j, i);
				buffer[counter + 0] = (byte) ((colorSpace << 8) >> 24);
				buffer[counter + 1] = (byte) ((colorSpace << 16) >> 24);
				buffer[counter + 2] = (byte) ((colorSpace << 24) >> 24);
				buffer[counter + 3] = (byte) (colorSpace >> 24);
				counter += 4;
			}
		}
		return ByteBuffer.wrap(buffer);
	}
}