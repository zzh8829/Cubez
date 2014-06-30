package ca.zihao.cubez;

import org.lwjgl.util.Rectangle;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.InputStream;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Zihao on 6/16/2014.
 */
public class ZImageLabel {
	Rectangle rect;
	int texture;

	Texture tex;

	public ZImageLabel(InputStream image) {
		try {
			tex = TextureLoader.getTexture("PNG", image);
		} catch(Exception e) {
			e.printStackTrace();
		}
		//texture = ImageLoader.loadPNG(image);
		rect = new Rectangle();
	}

	public ZImageLabel setRect(Rectangle rect) {
		this.rect = rect;
		return this;
	}

	public ZImageLabel setPosition(int x, int y) {
		rect.setLocation(x, y);
		return this;
	}

	public ZImageLabel setSize(int w, int h) {
		rect.setSize(w, h);
		return this;
	}

	public void render(TrueTypeFont font) {
		//glDisable(GL_ALPHA_TEST);
		//glColor3f(57 / 255.0f, 65 / 255.0f, 101 / 255.0f);

		glEnable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);

		tex.bind();

		glBegin(GL_QUADS);
			glTexCoord2f(0,0);
			glVertex2f(rect.getX(),rect.getY());
			glTexCoord2f(1,0);
			glVertex2f(rect.getX()+rect.getWidth(),rect.getY());
			glTexCoord2f(1,1);
			glVertex2f(rect.getX()+rect.getWidth(),rect.getY()+rect.getHeight());
			glTexCoord2f(0,1);
			glVertex2f(rect.getX(),rect.getY()+rect.getHeight());
		glEnd();

		glDisable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
	}
}
