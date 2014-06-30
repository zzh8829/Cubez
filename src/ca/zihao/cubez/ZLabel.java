package ca.zihao.cubez;

import org.lwjgl.util.Rectangle;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Zihao on 6/16/2014.
 */
public class ZLabel {
	Rectangle rect;
	String text;

	public ZLabel(String text) {
		rect = new Rectangle();
		this.text = text;
	}

	public ZLabel setRect(Rectangle rect) {
		this.rect = rect;
		return this;
	}

	public ZLabel setPosition(int x, int y) {
		rect.setLocation(x, y);
		return this;
	}

	public ZLabel setSize(int w, int h) {
		rect.setSize(w, h);
		return this;
	}

	public void render(TrueTypeFont font) {
		glDisable(GL_ALPHA_TEST);
		glColor3f(57 / 255.0f, 65 / 255.0f, 101 / 255.0f);

		glRecti(rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());
		glEnable(GL_ALPHA_TEST);

		font.drawString(rect.getX() + 20, rect.getY() + 20, text, Color.white);
	}
}
