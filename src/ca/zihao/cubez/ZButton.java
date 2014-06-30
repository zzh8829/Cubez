package ca.zihao.cubez;

import org.lwjgl.util.Rectangle;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Zihao on 6/16/2014.
 */
public class ZButton {

	Rectangle rect;
	String text;
	String state;
	ArrayList<Callback> callbackArrayList;

	int xshift;
	int yshift;

	public ZButton(String text) {
		yshift = 20;
		xshift = 20;
		state = "up";
		rect = new Rectangle();
		this.text = text;
		callbackArrayList = new ArrayList<Callback>();
	}

	public void addCallback(Callback cb) {
		callbackArrayList.add(cb);
	}

	public ZButton setRect(Rectangle rect) {
		this.rect = rect;
		return this;
	}

	public ZButton setPosition(int x, int y) {
		rect.setLocation(x, y);
		return this;
	}

	public ZButton setSize(int w, int h) {
		rect.setSize(w, h);
		return this;
	}

	public boolean update(int mx, int my, boolean down) {
		if(rect.contains(mx, my)) {
			if(down) {
				state = "down";
			} else {
				if(state == "down") {
					for(Callback c : callbackArrayList) {
						c.run();
					}
				}
				state = "hover";
			}
			return true;
		} else {
			state = "up";
			return false;
		}
	}

	public void render(TrueTypeFont font) {
		glDisable(GL_ALPHA_TEST);
		if(state == "up")
			glColor3f(57 / 255.0f, 65 / 255.0f, 101 / 255.0f);
		else if(state == "down")
			glColor3f(72 / 255.0f, 57 / 255.0f, 101 / 255.0f);
		else if(state == "hover")
			glColor3f(80 / 255.0f, 88 / 255.0f, 124 / 255.0f);

		glRecti(rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());
		glEnable(GL_ALPHA_TEST);

		font.drawString(rect.getX() + xshift, rect.getY() + yshift, text, Color.white);
	}
}
