package ca.zihao.cubez;

/**
 * Created by Zihao on 6/1/2014.
 */

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;

public class ConsoleMain {
	// Entry point for the application

	// Setup variables
	private final String WINDOW_TITLE = "The Quad: glDrawElements";
	private final int width = 1000;
	private final int height = 600;
	// Quad variable

	public ConsoleMain(String args[]) {

	}

	public void run() {
		// Initialize OpenGL (Display)
		this.setupOpenGL();

		this.setupQuad();

		while(!Display.isCloseRequested()) {
			// Do a single loop (logic/render)
			this.loopCycle();

			// Force a maximum FPS of about 60
			Display.sync(60);
			// Let the CPU synchronize with the GPU if GPU is tagging behind
			Display.update();
		}

		// Destroy OpenGL (Display)
		this.destroyOpenGL();
	}

	public void setupOpenGL() {
		// Setup an OpenGL context with API version 3.2
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle(WINDOW_TITLE);
			Display.create();
		} catch(LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		glClearColor(31 / 255.0f, 37 / 255.0f, 63 / 255.0f, 0);

		glViewport(0, 0, width, height);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, height, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);

	}

	public void setupQuad() {

	}

	public void loopCycle() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glColor3f(57 / 255.0f, 65 / 255.0f, 101 / 255.0f);

		//drawRect(scrambleRect);

		//glColor4f(0.5f,0.5f,1.0f);

		glRecti(100, 100, 200, 200);

	}

	public void destroyOpenGL() {

		Display.destroy();
	}
}
