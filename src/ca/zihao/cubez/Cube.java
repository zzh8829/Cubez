package ca.zihao.cubez;

/**
 * Created by Zihao on 5/25/2014.
 */

import ca.zihao.utility.Ray;
import ca.zihao.utility.StopWatch;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Collections;

public class Cube {

	static final int FRONT = 1;
	static final int BACK = 4;
	static final int TOP = 0;
	static final int BOTTOM = 3;
	static final int LEFT = 2;
	static final int RIGHT = 5;

	static final String validMoves = "lrudfbmesxyzLRUDFBMESXYZ";
	static final String rotateMoves = "xyzXYZ";

	/*
	    0
      2 1 5 4
        3
     */

	Cubelet[] cubelets;
	Cubelet[] cubeletsOrderless;

    /*      6  7  8
          3  4  5
        0  1  2

            15 16 17
          12 13 14
        9 10 11

            24 25 26
          21 22 23
        18 19 20
    */

	static int[] top = new int[]{6, 7, 8, 3, 4, 5, 0, 1, 2};
	static int[] bottom = new int[]{18, 19, 20, 21, 22, 23, 24, 25, 26};
	static int[] left = new int[]{6, 3, 0, 15, 12, 9, 24, 21, 18};
	static int[] right = new int[]{2, 5, 8, 11, 14, 17, 20, 23, 26};
	static int[] front = new int[]{0, 1, 2, 9, 10, 11, 18, 19, 20};
	static int[] back = new int[]{8, 7, 6, 17, 16, 15, 26, 25, 24};
	static int[] all = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
	static int[] middle = new int[]{1, 4, 7, 10, 13, 16, 19, 22, 25};
	static int[] equator = new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17};
	static int[] standing = new int[]{3, 4, 5, 12, 13, 14, 21, 22, 23};

	static int turningSpeed = 300; // ms per turn;

	boolean turning; // is turning
	float turningAngle = 0; // degrees per ms;
	float turnedAngle = 0; // turned degrees
	char turningMove; // current move
	ArrayList<Cubelet> turningCubelets; // cublets to turn
	Vector3f turningVector; // axis
	float turningDirection; // multiplier

	Matrix4f transformation;

	Cubeface hover;

	StopWatch watch;
	int moveCount;
	boolean started;

	public Cube() {
		watch = new StopWatch();
		watch.setTimelimit(3599999);
		reset();
	}

	public void reset() {

		transformation = (Matrix4f) new Matrix4f().setIdentity();

		cubelets = new Cubelet[27];
		for(int i = 0; i != 27; i++) {
			cubelets[i] = new Cubelet();
		}
		for(int y = 0; y != 3; y++) {
			for(int i = 0; i != 9; i++) {
				cubelets[y * 9 + i].y = 1 - y;
			}
		}
		for(int x = 0; x != 3; x++) {
			for(int i = 0; i != 9; i++) {
				cubelets[x + i * 3].x = x - 1;
			}
		}
		for(int z = 0; z != 3; z++) {
			for(int i = 0; i != 3; i++) {
				for(int j = 0; j != 3; j++)
					cubelets[z * 3 + i * 9 + j].z = 1 - z;
			}
		}

		cubeletsOrderless = new Cubelet[27];
		for(int i = 0; i != 27; i++) {
			cubeletsOrderless[i] = cubelets[i];
			cubeletsOrderless[i].transformation.translate(new Vector3f(
					cubeletsOrderless[i].x,
					cubeletsOrderless[i].y,
					cubeletsOrderless[i].z));
		}

		for(Cubelet c : getCubulets(front)) {
			c.faces[FRONT] = FRONT;
		}
		for(Cubelet c : getCubulets(back)) {
			c.faces[BACK] = BACK;
		}
		for(Cubelet c : getCubulets(left)) {
			c.faces[LEFT] = LEFT;
		}
		for(Cubelet c : getCubulets(right)) {
			c.faces[RIGHT] = RIGHT;
		}
		for(Cubelet c : getCubulets(top)) {
			c.faces[TOP] = TOP;
		}
		for(Cubelet c : getCubulets(bottom)) {
			c.faces[BOTTOM] = BOTTOM;
		}

		for(Cubelet c : getCubulets(all)) {
			c.turnedFaces[FRONT] = FRONT;
		}
		for(Cubelet c : getCubulets(all)) {
			c.turnedFaces[BACK] = BACK;
		}
		for(Cubelet c : getCubulets(all)) {
			c.turnedFaces[LEFT] = LEFT;
		}
		for(Cubelet c : getCubulets(all)) {
			c.turnedFaces[RIGHT] = RIGHT;
		}
		for(Cubelet c : getCubulets(all)) {
			c.turnedFaces[TOP] = TOP;
		}
		for(Cubelet c : getCubulets(all)) {
			c.turnedFaces[BOTTOM] = BOTTOM;
		}

		moveCount = 0;
		watch.reset();
		started = false;
	}

	public void scramble() {
		for(int i=0;i!=50;i++) {
			twist(validMoves.charAt((int)(Math.random()*validMoves.length())));
		}
		moveCount = 0;
		watch.reset();
		started = false;
	}

	public void twist(String moves, int times) {
		for(int t = 0; t != times; t++) {
			twist(moves);
		}
	}

	public void twist(String moves) {
		for(int i = 0; i != moves.length(); i++) {
			twist(moves.charAt(i));
		}
	}

	public void twist(char move, int times) {
		for(int t = 0; t != times; t++) {
			twist(move);
		}
	}

	// Instant twist
	public void twist(char move) {
		if(validMoves.indexOf(move) == -1) {
			System.err.println("[Warning] Unknown Move: " + move);
			return;
		}
		if(rotateMoves.indexOf(move) == -1 ){
			moveCount ++;
		}
		turning = true;
		turningMove = move;
		System.out.println("[Info] Twisting: " + move);
		switch(move) {
			case 'L':
				turningCubelets = getCubulets(left);
				turningDirection = -1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'l':
				turningCubelets = getCubulets(left);
				turningDirection = 1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'R':
				turningCubelets = getCubulets(right);
				turningDirection = 1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'r':
				turningCubelets = getCubulets(right);
				turningDirection = -1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'U':
				turningCubelets = getCubulets(top);
				turningDirection = -1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'u':
				turningCubelets = getCubulets(top);
				turningDirection = 1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'D':
				turningCubelets = getCubulets(bottom);
				turningDirection = 1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'd':
				turningCubelets = getCubulets(bottom);
				turningDirection = -1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'F':
				turningCubelets = getCubulets(front);
				turningDirection = -1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 'f':
				turningCubelets = getCubulets(front);
				turningDirection = 1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 'B':
				turningCubelets = getCubulets(back);
				turningDirection = 1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 'b':
				turningCubelets = getCubulets(back);
				turningDirection = -1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 'M':
				turningCubelets = getCubulets(middle);
				turningDirection = 1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'm':
				turningCubelets = getCubulets(middle);
				turningDirection = -1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'E':
				turningCubelets = getCubulets(equator);
				turningDirection = 1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'e':
				turningCubelets = getCubulets(equator);
				turningDirection = -1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'S':
				turningCubelets = getCubulets(standing);
				turningDirection = -1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 's':
				turningCubelets = getCubulets(standing);
				turningDirection = 1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 'X':
				turningCubelets = getCubulets(all);
				turningDirection = 1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'x':
				turningCubelets = getCubulets(all);
				turningDirection = -1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'Y':
				turningCubelets = getCubulets(all);
				turningDirection = -1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'y':
				turningCubelets = getCubulets(all);
				turningDirection = 1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'Z':
				turningCubelets = getCubulets(all);
				turningDirection = -1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 'z':
				turningCubelets = getCubulets(all);
				turningDirection = 1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			default:
				break;
		}
		for(Cubelet c : turningCubelets) {
			c.transformation = Matrix4f.mul(
					new Matrix4f().rotate((float) Math.toRadians(90) * turningDirection, turningVector),
					c.transformation, null);
		}
		finalizeTurn();
	}

	// Uppercase clockwise Lowercase counter-clockwise
	// Turn by hand
	public void turn(char move) {
		if(validMoves.indexOf(move) == -1) {
			System.err.println("[Warning] Unknown Move: " + move);
			return;
		}
		if(rotateMoves.indexOf(move) == -1 ){
			moveCount ++;
		}
		turning = true;
		turningMove = move;
		turningAngle = 90.0f / turningSpeed; // angle per ms;
		turnedAngle = 0;

		System.out.println("[Info] Turning: " + move);

		switch(move) {
			case 'L':
				turningCubelets = getCubulets(left);
				turningDirection = -1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'l':
				turningCubelets = getCubulets(left);
				turningDirection = 1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'R':
				turningCubelets = getCubulets(right);
				turningDirection = 1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'r':
				turningCubelets = getCubulets(right);
				turningDirection = -1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'U':
				turningCubelets = getCubulets(top);
				turningDirection = -1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'u':
				turningCubelets = getCubulets(top);
				turningDirection = 1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'D':
				turningCubelets = getCubulets(bottom);
				turningDirection = 1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'd':
				turningCubelets = getCubulets(bottom);
				turningDirection = -1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'F':
				turningCubelets = getCubulets(front);
				turningDirection = -1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 'f':
				turningCubelets = getCubulets(front);
				turningDirection = 1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 'B':
				turningCubelets = getCubulets(back);
				turningDirection = 1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 'b':
				turningCubelets = getCubulets(back);
				turningDirection = -1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 'M':
				turningCubelets = getCubulets(middle);
				turningDirection = 1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'm':
				turningCubelets = getCubulets(middle);
				turningDirection = -1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'E':
				turningCubelets = getCubulets(equator);
				turningDirection = 1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'e':
				turningCubelets = getCubulets(equator);
				turningDirection = -1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'S':
				turningCubelets = getCubulets(standing);
				turningDirection = -1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 's':
				turningCubelets = getCubulets(standing);
				turningDirection = 1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 'X':
				turningCubelets = getCubulets(all);
				turningDirection = 1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'x':
				turningCubelets = getCubulets(all);
				turningDirection = -1;
				turningVector = new Vector3f(1, 0, 0);
				break;
			case 'Y':
				turningCubelets = getCubulets(all);
				turningDirection = -1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'y':
				turningCubelets = getCubulets(all);
				turningDirection = 1;
				turningVector = new Vector3f(0, 1, 0);
				break;
			case 'Z':
				turningCubelets = getCubulets(all);
				turningDirection = -1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			case 'z':
				turningCubelets = getCubulets(all);
				turningDirection = 1;
				turningVector = new Vector3f(0, 0, 1);
				break;
			default:
				break;
		}
	}

	public ArrayList<Cubelet> getCubulets(int[] group) {
		ArrayList<Cubelet> ac = new ArrayList<Cubelet>();
		for(int i : group) {
			ac.add(cubelets[i]);
		}
		return ac;
	}

	public void reorder() {
		for(Cubelet c : cubeletsOrderless) {
			//System.out.println(c.transformation.toString());
			int x = Math.round(c.transformation.m30);
			int y = Math.round(c.transformation.m31);
			int z = Math.round(c.transformation.m32);
			cubelets[(1 - y) * 9 + (1 - z) * 3 + (x + 1)] = c;
		}
	}

	public Cubeface intersect(Ray ray) {
		ArrayList<Cubeface> ac = new ArrayList<Cubeface>();
		for(int i = 0; i != cubelets.length; i++) {
			ArrayList<Cubeface> acc = cubelets[i].intersect(ray);
			for(Cubeface cf : acc) {
				cf.cubelet = i;
				ac.add(cf);
			}
		}
		Collections.sort(ac);
		if(ac.size() == 0) {
			return null;
		}
		return ac.get(0);
	}

	public void finalizeTurn() {

		switch(turningMove) {
			case 'L':
				for(Cubelet c : getCubulets(left)) {
					c.rotateFaceX(true);
				}
				break;
			case 'l':
				for(Cubelet c : getCubulets(left)) {
					c.rotateFaceX(false);
				}
				break;
			case 'R':
				for(Cubelet c : getCubulets(right)) {
					c.rotateFaceX(false);
				}
				break;
			case 'r':
				for(Cubelet c : getCubulets(right)) {
					c.rotateFaceX(true);
				}
				break;
			case 'U':
				for(Cubelet c : getCubulets(top)) {
					c.rotateFaceY(true);
				}
				break;
			case 'u':
				for(Cubelet c : getCubulets(top)) {
					c.rotateFaceY(false);
				}
				break;
			case 'D':
				for(Cubelet c : getCubulets(bottom)) {
					c.rotateFaceY(false);
				}
				break;
			case 'd':
				for(Cubelet c : getCubulets(bottom)) {
					c.rotateFaceY(true);
				}
				break;
			case 'F':
				for(Cubelet c : getCubulets(front)) {
					c.rotateFaceZ(true);
				}
				break;
			case 'f':
				for(Cubelet c : getCubulets(front)) {
					c.rotateFaceZ(false);
				}
				break;
			case 'B':
				for(Cubelet c : getCubulets(back)) {
					c.rotateFaceZ(false);
				}
				break;
			case 'b':
				for(Cubelet c : getCubulets(back)) {
					c.rotateFaceZ(true);
				}
				break;
			case 'M':
				for(Cubelet c : getCubulets(middle)) {
					c.rotateFaceX(false);
				}
				break;
			case 'm':
				for(Cubelet c : getCubulets(middle)) {
					c.rotateFaceX(true);
				}
				break;
			case 'E':
				for(Cubelet c : getCubulets(equator)) {
					c.rotateFaceY(false);
				}
				break;
			case 'e':
				for(Cubelet c : getCubulets(equator)) {
					c.rotateFaceY(true);
				}
				break;
			case 'S':
				for(Cubelet c : getCubulets(standing)) {
					c.rotateFaceZ(true);
				}
				break;
			case 's':
				for(Cubelet c : getCubulets(standing)) {
					c.rotateFaceZ(false);
				}
				break;
			case 'X':
				for(Cubelet c : getCubulets(all)) {
					c.rotateFaceX(false);
				}
				break;
			case 'x':
				for(Cubelet c : getCubulets(all)) {
					c.rotateFaceX(true);
				}
				break;
			case 'Y':
				for(Cubelet c : getCubulets(all)) {
					c.rotateFaceY(true);
				}
				break;
			case 'y':
				for(Cubelet c : getCubulets(all)) {
					c.rotateFaceY(false);
				}
				break;
			case 'Z':
				for(Cubelet c : getCubulets(all)) {
					c.rotateFaceZ(true);
				}
				break;
			case 'z':
				for(Cubelet c : getCubulets(all)) {
					c.rotateFaceZ(false);
				}
				break;
			default:
				break;
		}

		reorder();
		turning = false;
	}

	public void update(int delta) {
		if(turning) {
			if(turnedAngle >= 90) {
				finalizeTurn();
				return;
			}
			float toTurn = delta * turningAngle;
			if(turnedAngle + toTurn > 90) {
				toTurn = 90 - turnedAngle;
			}
			for(Cubelet c : turningCubelets) {
				c.transformation = Matrix4f.mul(
						new Matrix4f().rotate((float) Math.toRadians(toTurn) * turningDirection, turningVector),
						c.transformation, null);
			}
			turnedAngle += toTurn;
		}
	}

	public static String f2s(int f) {
		return new String[]{"top", "front", "left", "bot", "back", "right"}[f];
	}

	public boolean solved() {
		if(turning) {
			return false;
		}
		for(Cubelet c : getCubulets(front)) {
			if(c.turnedFaces[FRONT] != cubelets[0].turnedFaces[FRONT]) {
				return false;
			}
		}
		for(Cubelet c : getCubulets(back)) {
			if(c.turnedFaces[BACK] != cubelets[26].turnedFaces[BACK]) {
				return false;
			}
		}
		for(Cubelet c : getCubulets(left)) {
			if(c.turnedFaces[LEFT] != cubelets[0].turnedFaces[LEFT]) {
				return false;
			}
		}
		for(Cubelet c : getCubulets(right)) {
			if(c.turnedFaces[RIGHT] != cubelets[26].turnedFaces[RIGHT]) {
				return false;
			}
		}
		for(Cubelet c : getCubulets(top)) {
			if(c.turnedFaces[TOP] != cubelets[0].turnedFaces[TOP]) {
				return false;
			}		}
		for(Cubelet c : getCubulets(bottom)) {
			if(c.turnedFaces[BOTTOM] != cubelets[26].turnedFaces[BOTTOM]) {
				return false;
			}
		}
		return true;
	}

}
