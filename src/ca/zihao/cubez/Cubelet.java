package ca.zihao.cubez;

import ca.zihao.utility.Ray;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;

/**
 * Created by Zihao on 5/25/2014.
 */
public class Cubelet {

	static final int XAXIS = 0;
	static final int YAXIS = 1;
	static final int ZAXIS = 2;


	Matrix4f transformation;
	Matrix4f worldTransformation;

	/*
	    0
      2 1 5 4
        3
     */

	int[] faces;
	int[] turnedFaces;
	int x, y, z;

	public Cubelet() {
		faces = new int[]{6, 6, 6, 6, 6, 6};
		turnedFaces = new int[6];
		transformation = (Matrix4f) new Matrix4f().setIdentity();
	}

	void rotatePosX(boolean clockwise) {
		int tx = x;
		int ty = y;
		int tz = z;
		double rad = Math.toRadians(clockwise ? -90 : 90);
		x = tx;
		y = (int) Math.round(ty * Math.cos(rad) - tz * Math.sin(rad));
		z = (int) Math.round(ty * Math.sin(rad) + tz * Math.cos(rad));
	}

	void rotatePosY(boolean clockwise) {
		int tx = x;
		int ty = y;
		int tz = z;
		double rad = Math.toRadians(clockwise ? -90 : 90);
		x = (int) Math.round(tx * Math.cos(rad) + tz * Math.sin(rad));
		y = ty;
		z = (int) Math.round(-tx * Math.sin(rad) + tz * Math.cos(rad));
	}

	void rotatePosZ(boolean clockwise) {
		int tx = x;
		int ty = y;
		int tz = z;
		double rad = Math.toRadians(clockwise ? -90 : 90);
		x = (int) Math.round(tx * Math.cos(rad) - ty * Math.sin(rad));
		y = (int) Math.round(tx * Math.sin(rad) + ty * Math.cos(rad));
		z = tz;
	}

	int[] getIndexMap() {
		int[] map = new int[6];
		for(int i = 0; i != 6; i++) {
			map[turnedFaces[i]] = i;
		}
		return map;
	}

	void rotateFaceX(boolean clockwise) {
		int[] indexMap = getIndexMap();

		int f = turnedFaces[indexMap[Cube.FRONT]];
		int b = turnedFaces[indexMap[Cube.BACK]];
		int top = turnedFaces[indexMap[Cube.TOP]];
		int bot = turnedFaces[indexMap[Cube.BOTTOM]];
		if(clockwise) {
			turnedFaces[indexMap[Cube.FRONT]] = top;
			turnedFaces[indexMap[Cube.TOP]] = b;
			turnedFaces[indexMap[Cube.BACK]] = bot;
			turnedFaces[indexMap[Cube.BOTTOM]] = f;
		} else {
			turnedFaces[indexMap[Cube.FRONT]] = bot;
			turnedFaces[indexMap[Cube.TOP]] = f;
			turnedFaces[indexMap[Cube.BACK]] = top;
			turnedFaces[indexMap[Cube.BOTTOM]] = b;
		}
	}

	void rotateFaceY(boolean clockwise) {
		int[] indexMap = getIndexMap();

		int f = turnedFaces[indexMap[Cube.FRONT]];
		int b = turnedFaces[indexMap[Cube.BACK]];
		int l = turnedFaces[indexMap[Cube.LEFT]];
		int r = turnedFaces[indexMap[Cube.RIGHT]];
		if(clockwise) {
			turnedFaces[indexMap[Cube.FRONT]] = l;
			turnedFaces[indexMap[Cube.BACK]] = r;
			turnedFaces[indexMap[Cube.LEFT]] = b;
			turnedFaces[indexMap[Cube.RIGHT]] = f;
		} else {
			turnedFaces[indexMap[Cube.FRONT]] = r;
			turnedFaces[indexMap[Cube.BACK]] = l;
			turnedFaces[indexMap[Cube.LEFT]] = f;
			turnedFaces[indexMap[Cube.RIGHT]] = b;
		}
	}

	void rotateFaceZ(boolean clockwise) {
		int[] indexMap = getIndexMap();

		int t = turnedFaces[indexMap[Cube.TOP]];
		int b = turnedFaces[indexMap[Cube.BOTTOM]];
		int l = turnedFaces[indexMap[Cube.LEFT]];
		int r = turnedFaces[indexMap[Cube.RIGHT]];
		if(clockwise) {
			turnedFaces[indexMap[Cube.TOP]] = r;
			turnedFaces[indexMap[Cube.LEFT]] = t;
			turnedFaces[indexMap[Cube.BOTTOM]] = l;
			turnedFaces[indexMap[Cube.RIGHT]] = b;
		} else {
			turnedFaces[indexMap[Cube.TOP]] = l;
			turnedFaces[indexMap[Cube.LEFT]] = b;
			turnedFaces[indexMap[Cube.BOTTOM]] = r;
			turnedFaces[indexMap[Cube.RIGHT]] = t;
		}
	}

	void print() {
		System.out.printf("F: %d B: %d L: %d R: %d T: %d B: %d\n",
				faces[Cube.FRONT], faces[Cube.BACK], faces[Cube.LEFT],
				faces[Cube.RIGHT], faces[Cube.TOP], faces[Cube.BOTTOM]);
	}

	ArrayList<Cubeface> getCubefaces() {
		ArrayList<Cubeface> ac = new ArrayList<Cubeface>();
		for(int i = 0; i != 6; i++)
			ac.add(getOneCubeface(i));
		return ac;
	}

	Cubeface getOneCubeface(int face) {
		Matrix4f localMatrix = (Matrix4f) new Matrix4f().setIdentity();
		switch(face) {
			case Cube.TOP:
				localMatrix.rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));
				break;
			case Cube.FRONT:
				break;
			case Cube.LEFT:
				localMatrix.rotate((float) Math.toRadians(-90), new Vector3f(0, 1, 0));
				break;
			case Cube.BOTTOM:
				localMatrix.rotate((float) Math.toRadians(90), new Vector3f(1, 0, 0));
				break;
			case Cube.BACK:
				localMatrix.rotate((float) Math.toRadians(180), new Vector3f(0, 1, 0));
				break;
			case Cube.RIGHT:
				localMatrix.rotate((float) Math.toRadians(90), new Vector3f(0, 1, 0));
				break;
			default:
				return null;
		}
		Matrix4f faceMatrix = Matrix4f.mul(transformation, localMatrix, null);
		Vector4f bl = Matrix4f.transform(faceMatrix, new Vector4f(-0.5f, -0.5f, 0.5f, 1f), null);
		Vector4f br = Matrix4f.transform(faceMatrix, new Vector4f(0.5f, -0.5f, 0.5f, 1f), null);
		Vector4f tr = Matrix4f.transform(faceMatrix, new Vector4f(0.5f, 0.5f, 0.5f, 1f), null);
		Vector4f tl = Matrix4f.transform(faceMatrix, new Vector4f(-0.5f, 0.5f, 0.5f, 1f), null);

		Cubeface cf = new Cubeface(turnedFaces[face], face,
				new Vector3f(tl.x / tl.w, tl.y / tl.w, tl.z / tl.w),
				new Vector3f(tr.x / tr.w, tr.y / tr.w, tr.z / tr.w),
				new Vector3f(bl.x / bl.w, bl.y / bl.w, bl.z / bl.w),
				new Vector3f(br.x / br.w, br.y / br.w, br.z / br.w)
		);

		return cf;
	}

	ArrayList<Cubeface> intersect(Ray ray) {
		ArrayList<Cubeface> ac = new ArrayList<Cubeface>();
		for(Cubeface cf : getCubefaces()) {
			float dis = cf.intersect(ray);
			if(dis != -1) {
				ac.add(cf);
			}
		}
		return ac;
	}
}
