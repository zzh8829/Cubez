package ca.zihao.cubez;

import ca.zihao.utility.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.Rectangle;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.TrueTypeFont;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Zihao on 5/22/2014.
 */

public class WindowMain {

	Cube cube;

	int width, height;
	boolean running;

	Matrix4f modelMatrix;
	Matrix4f viewMatrix;
	Matrix4f projectionMatrix;
	Matrix4f MVPMatrix;
	Matrix4f inverseMVPMatrix;

	Vector3f eyeVec, centerVec, upVec;

	CubeletRenderer renderer;

	boolean isDragging = false;

	int rotation;

	int up;

	TrueTypeFont font;

	Rectangle resetRect;
	Rectangle scrambleRect;
	Rectangle optionsRect;
	Rectangle instructionRect;

	ZButton resetButton;
	ZButton scrambleButton;
	ZButton instructionButton;
	ZButton instructionCloseButton;

	ZLabel timeLabel;
	ZLabel moveLabel;

	ZImageLabel instructionLabel;

	boolean guiOn;

	public WindowMain(String[] args) {
		cube = new Cube();
		width = 1000;
		height = 600;
	}

	public void run() {
		initDisplay();
		initOpenGL();
		resizeOpenGL();
		initGUI();
		mainLoop();
	}

	void initDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle("Le Cubez");
			Display.setIcon(IconLoader.loadIcon(ResLoader.load("images/Rubik-Cube-32.png")));
			Display.setResizable(false);
			Display.setVSyncEnabled(true);
			Display.create();
		} catch(LWJGLException e) {
			e.printStackTrace();
		}
	}

	void initOpenGL() {
		glClearColor(31 / 255.0f, 37 / 255.0f, 63 / 255.0f, 0.0f);

		Matrix4f projection = new Matrix4f();

		eyeVec = new Vector3f(0, 3, 7);
		centerVec = new Vector3f(0, 0, 0);
		upVec = new Vector3f(0, 1, 0);

		renderer = new CubeletRenderer();
		renderer.init();

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);

		//glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, 0.5f);

		glDisable(GL_CULL_FACE);

		modelMatrix = (Matrix4f) new Matrix4f().setIdentity();
		viewMatrix = GLUtil.lookAt(eyeVec, centerVec, upVec);
		projectionMatrix = GLUtil.perspective(30, width / (float) height, 1, 1000);

		modelMatrix.rotate((float) Math.toRadians(-45), new Vector3f(0, 1, 0));
		//modelMatrix.translate(new Vector3f(-1,0,0));

		Matrix4f vp = Matrix4f.mul(projectionMatrix, viewMatrix, null);
		MVPMatrix = Matrix4f.mul(vp, modelMatrix, null);
		inverseMVPMatrix = Matrix4f.invert(MVPMatrix, null);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, height, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
	}

	void resizeOpenGL() {
		glViewport(0, 0, width, height);

	}

	void initGUI() {

		guiOn = false;

		font = new TrueTypeFont(new Font("Arial", Font.BOLD, 24), true);

		scrambleRect = new Rectangle(width - 200, 140, 200, 70);
		resetRect = new Rectangle(width - 200, 260, 200, 70);
		instructionRect = new Rectangle(width-200,380,200,70);

		optionsRect = new Rectangle(0, 380, 200, 70);

		resetButton = new ZButton("Reset");
		resetButton.setRect(resetRect);

		scrambleButton = new ZButton("Scramble");
		scrambleButton.setRect(scrambleRect);

		instructionButton = new ZButton("Instructions");
		instructionButton.setRect(instructionRect);

		//optionsButton = new ZButton("Options");
		//optionsButton.setRect(optionsRect);


		timeLabel = new ZLabel("Time: 0.0 s");
		timeLabel.setRect(new Rectangle(0, 140, 200, 70));

		moveLabel = new ZLabel("Moves: 0");
		moveLabel.setRect(new Rectangle(0, 260, 200, 70));

		instructionLabel = new ZImageLabel(ResLoader.loadAsStream("images/instructions.png"));
		instructionLabel.setRect(new Rectangle(242,43,1000,1000));

		instructionCloseButton = new ZButton("Close");
		instructionCloseButton.yshift = 10;
		instructionCloseButton.xshift = 60;
		instructionCloseButton.setRect(new Rectangle(400,480,200,50));

		resetButton.addCallback(new Callback() {
			@Override
			public void run() {
				cube.reset();
			}
		});

		scrambleButton.addCallback(new Callback() {
			@Override
			public void run() {
				cube.scramble();
/*
				if(!cube.turning) {
					cube.scramble();
				}
				else {
					System.out.println("[Warning] Cube not ready");
				}
*/
			}
		});

		instructionButton.addCallback(new Callback() {
			@Override
			public void run() {
				guiOn = true;
			}
		});

		instructionCloseButton.addCallback(new Callback() {
			@Override
			public void run() {
				guiOn = false;
			}
		});
	}

	long lastTime;

	long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastTime);
		lastTime = time;
		return delta;
	}

	void mainLoop() {

		lastTime = getTime();

		running = true;
		while(running) {
			int delta = getDelta();
			if(Display.isCloseRequested()) {
				running = false;
			}
			if(Display.wasResized()) {
				width = Display.getWidth();
				height = Display.getHeight();
				resizeOpenGL();
			}
			input();
			render(delta);
			Display.update();
		}
		Display.destroy();
	}

	Cubeface hit;
	int dragX, dragY;

	public void input() {
		while(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				char ch = Keyboard.getEventCharacter();
				switch(ch) {
					default:
						if(!cube.turning) {
							cube.turn(ch);
						} else {
							System.out.println("[Warning] Cube is not ready");
						}
				}
				int kc = Keyboard.getEventKey();
				switch(kc) {
					case Keyboard.KEY_ESCAPE:
						running = false;
						break;
					case Keyboard.KEY_SPACE:
						if(!cube.turning)
							cube.scramble();
						break;
					case Keyboard.KEY_RETURN:
						if(!cube.turning)
							cube.reset();
						break;
					default:
						break;
				}
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_1)) {
			rotation = -1;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_2)) {
			rotation = 1;
		} else {
			rotation = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_3)) {
			up = 1;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_4)) {
			up = -1;
		} else {
			up = 0;
		}

		int mx = Mouse.getX();
		int my = Mouse.getY();

		cube.hover = rayCast(mx, my);
		if(cube.hover == null) {
			cube.hover = new Cubeface(-1, -1, null, null, null, null);
		}

		if(isDragging) {
			int dx = mx - dragX;
			int dy = my - dragY;
			if(hit != null && hit.face == Cube.TOP) {
				float tx = dx, ty = dy;
				dx = (int) Math.round(tx * Math.cos(Math.toRadians(45)) - ty * Math.sin(Math.toRadians(45)));
				dy = (int) Math.round(tx * Math.sin(Math.toRadians(45)) + ty * Math.cos(Math.toRadians(45)));
			}
			if(Math.abs(dx) > 10 || Math.abs(dy) > 10) {
				if(Math.abs(dx) > Math.abs(dy)) {
					if(dx > 0) {
						if(hit != null) {
							if((hit.face == Cube.FRONT && (hit.cubelet == 0 || hit.cubelet == 1 || hit.cubelet == 2)) ||
									hit.face == Cube.RIGHT && (hit.cubelet == 2 || hit.cubelet == 5 || hit.cubelet == 8)) {
								cube.turn('u');
							} else if((hit.face == Cube.FRONT && (hit.cubelet == 9 || hit.cubelet == 10 || hit.cubelet == 11)) ||
									hit.face == Cube.RIGHT && (hit.cubelet == 11 || hit.cubelet == 14 || hit.cubelet == 17)) {
								cube.turn('E');
							} else if((hit.face == Cube.FRONT && (hit.cubelet == 18 || hit.cubelet == 19 || hit.cubelet == 20)) ||
									hit.face == Cube.RIGHT && (hit.cubelet == 20 || hit.cubelet == 23 || hit.cubelet == 26)) {
								cube.turn('D');
							} else if(hit.face == Cube.TOP && (hit.cubelet == 0 || hit.cubelet == 1 || hit.cubelet == 2)) {
								cube.turn('F');
							} else if(hit.face == Cube.TOP && (hit.cubelet == 3 || hit.cubelet == 4 || hit.cubelet == 5)) {
								cube.turn('S');
							} else if(hit.face == Cube.TOP && (hit.cubelet == 6 || hit.cubelet == 7 || hit.cubelet == 8)) {
								cube.turn('b');
							}
						} else {
							cube.turn('y');
						}
					} else {
						if(hit != null) {
							if((hit.face == Cube.FRONT && (hit.cubelet == 0 || hit.cubelet == 1 || hit.cubelet == 2)) ||
									hit.face == Cube.RIGHT && (hit.cubelet == 2 || hit.cubelet == 5 || hit.cubelet == 8)) {
								cube.turn('U');
							} else if((hit.face == Cube.FRONT && (hit.cubelet == 9 || hit.cubelet == 10 || hit.cubelet == 11)) ||
									hit.face == Cube.RIGHT && (hit.cubelet == 11 || hit.cubelet == 14 || hit.cubelet == 17)) {
								cube.turn('e');
							} else if((hit.face == Cube.FRONT && (hit.cubelet == 18 || hit.cubelet == 19 || hit.cubelet == 20)) ||
									hit.face == Cube.RIGHT && (hit.cubelet == 20 || hit.cubelet == 23 || hit.cubelet == 26)) {
								cube.turn('d');
							} else if(hit.face == Cube.TOP && (hit.cubelet == 0 || hit.cubelet == 1 || hit.cubelet == 2)) {
								cube.turn('f');
							} else if(hit.face == Cube.TOP && (hit.cubelet == 3 || hit.cubelet == 4 || hit.cubelet == 5)) {
								cube.turn('s');
							} else if(hit.face == Cube.TOP && (hit.cubelet == 6 || hit.cubelet == 7 || hit.cubelet == 8)) {
								cube.turn('B');
							}
						} else {
							cube.turn('Y');
						}
					}
				} else {
					if(dy > 0) {
						if(hit != null) {
							if((hit.face == Cube.FRONT && (hit.cubelet == 0 || hit.cubelet == 9 || hit.cubelet == 18)) ||
									hit.face == Cube.TOP && (hit.cubelet == 0 || hit.cubelet == 3 || hit.cubelet == 6)) {
								cube.turn('L');
							} else if((hit.face == Cube.FRONT && (hit.cubelet == 1 || hit.cubelet == 10 || hit.cubelet == 19)) ||
									hit.face == Cube.TOP && (hit.cubelet == 1 || hit.cubelet == 4 || hit.cubelet == 7)) {
								cube.turn('m');
							} else if((hit.face == Cube.FRONT && (hit.cubelet == 2 || hit.cubelet == 11 || hit.cubelet == 20)) ||
									hit.face == Cube.TOP && (hit.cubelet == 2 || hit.cubelet == 5 || hit.cubelet == 8)) {
								cube.turn('r');
							} else if(hit.face == Cube.RIGHT && (hit.cubelet == 2 || hit.cubelet == 11 || hit.cubelet == 20)) {
								cube.turn('f');
							} else if(hit.face == Cube.RIGHT && (hit.cubelet == 5 || hit.cubelet == 14 || hit.cubelet == 23)) {
								cube.turn('s');
							} else if(hit.face == Cube.RIGHT && (hit.cubelet == 8 || hit.cubelet == 17 || hit.cubelet == 26)) {
								cube.turn('B');
							}
						} else {
							if(dragX < width / 2) {
								cube.turn('x');
							} else {
								cube.turn('z');
							}
						}
					} else {
						if(hit != null) {
							if((hit.face == Cube.FRONT && (hit.cubelet == 0 || hit.cubelet == 9 || hit.cubelet == 18)) ||
									hit.face == Cube.TOP && (hit.cubelet == 0 || hit.cubelet == 3 || hit.cubelet == 6)) {
								cube.turn('l');
							} else if((hit.face == Cube.FRONT && (hit.cubelet == 1 || hit.cubelet == 10 || hit.cubelet == 19)) ||
									hit.face == Cube.TOP && (hit.cubelet == 1 || hit.cubelet == 4 || hit.cubelet == 7)) {
								cube.turn('M');
							} else if((hit.face == Cube.FRONT && (hit.cubelet == 2 || hit.cubelet == 11 || hit.cubelet == 20)) ||
									hit.face == Cube.TOP && (hit.cubelet == 2 || hit.cubelet == 5 || hit.cubelet == 8)) {
								cube.turn('R');
							} else if(hit.face == Cube.RIGHT && (hit.cubelet == 2 || hit.cubelet == 11 || hit.cubelet == 20)) {
								cube.turn('F');
							} else if(hit.face == Cube.RIGHT && (hit.cubelet == 5 || hit.cubelet == 14 || hit.cubelet == 23)) {
								cube.turn('S');
							} else if(hit.face == Cube.RIGHT && (hit.cubelet == 8 || hit.cubelet == 17 || hit.cubelet == 26)) {
								cube.turn('b');
							}
						} else {
							if(dragX < width / 2) {
								cube.turn('X');
							} else {
								cube.turn('Z');
							}
						}
					}
				}
				isDragging = false;
			}
		}

		boolean noDrag =
				scrambleButton.update(mx, height - my, Mouse.isButtonDown(0)) ||
						resetButton.update(mx, height - my, Mouse.isButtonDown(0)) ||
						instructionButton.update(mx, height - my, Mouse.isButtonDown(0)) ||
						timeLabel.rect.contains(mx, height-my) ||
						moveLabel.rect.contains(mx, height-my);

		if(guiOn) {
			noDrag = noDrag ||
					instructionCloseButton.update(mx, height- my, Mouse.isButtonDown(0));
		}

		String cubetime = new SimpleDateFormat("mm:ss.SSS").format(new Date(cube.watch.time())).substring(0,8);

		if(cube.started && cube.solved()) {
			Sys.alert("Congratulations",String.format("You solved the cube in %s with %d moves",cubetime,cube.moveCount));
			cube.reset();
		}

		if(cube.moveCount == 1 && !cube.started) {
			cube.started = true;
			cube.watch.start();
		}

		//timeLabel.text = String.format("Time: %.1f",cube.watch.time()/1000.0);
		timeLabel.text = "Time: " + cubetime;
		moveLabel.text = String.format("Moves: %d",cube.moveCount);

		while(Mouse.next()) {
			if(Mouse.getEventButtonState()) {
				if(Mouse.getEventButton() == 0) {
					if(!cube.turning) {
						dragX = mx;
						dragY = my;
						hit = rayCast(mx, my);
						if(!noDrag) {
							isDragging = true;
						}
						if(hit != null)
							System.out.println("[Info] Hit C: " + hit.cubelet + " F: " + Cube.f2s(hit.face) + " D: " + hit.distance);
					}
				}
			} else {
				if(Mouse.getEventButton() == 0) {
					isDragging = false;
				}
			}
		}
	}

	Vector3f rayBeg, rayEnd, rayDir;
	boolean ray;

	public Cubeface rayCast(float x, float y) {
		x = 2 * x / width - 1;
		y = 2 * y / height - 1;

		Vector4f vec1 = Matrix4f.transform(inverseMVPMatrix, new Vector4f(x, y, -1, 1), null);
		Vector4f vec2 = Matrix4f.transform(inverseMVPMatrix, new Vector4f(x, y, 1, 1), null);

		rayBeg = new Vector3f(vec1.x / vec1.w, vec1.y / vec1.w, vec1.z / vec1.w);
		rayEnd = new Vector3f(vec2.x / vec2.w, vec2.y / vec2.w, vec2.z / vec2.w);
		rayDir = Vector3f.sub(rayEnd, rayBeg, null).normalise(null);

		ray = true;

		Ray ray = new Ray(rayBeg, rayDir);
		return cube.intersect(ray);
	}

	public void render(int delta) {
		modelMatrix.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0));
		modelMatrix.rotate((float) Math.toRadians(up), new Vector3f(0, 0, 1));
		viewMatrix = GLUtil.lookAt(eyeVec, centerVec, upVec);
		projectionMatrix = GLUtil.perspective(30, width / (float) height, 1, 1000);
		Matrix4f vp = Matrix4f.mul(projectionMatrix, viewMatrix, null);
		MVPMatrix = Matrix4f.mul(vp, modelMatrix, null);
		inverseMVPMatrix = Matrix4f.invert(MVPMatrix, null);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		//optionsButton.render(font);
		scrambleButton.render(font);
		resetButton.render(font);
		instructionButton.render(font);

		timeLabel.render(font);
		moveLabel.render(font);

		cube.update(delta);
		renderer.render(MVPMatrix, cube);

		if(guiOn) {
			instructionLabel.render(font);
			instructionCloseButton.render(font);
		}
	}
}
