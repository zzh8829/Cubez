package ca.zihao.utility;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Zihao on 6/1/2014.
 */
public class GLUtil {

	public static Matrix4f lookAt(Vector3f eye, Vector3f center, Vector3f up) {
		Matrix4f mat = new Matrix4f();
		Vector3f f = Vector3f.sub(center, eye, null).normalise(null);
		Vector3f s = Vector3f.cross(f, up, null).normalise(null);
		Vector3f u = Vector3f.cross(s, f, null);
		mat.m00 = s.getX();
		mat.m10 = s.getY();
		mat.m20 = s.getZ();
		mat.m01 = u.getX();
		mat.m11 = u.getY();
		mat.m21 = u.getZ();
		mat.m02 = -f.getX();
		mat.m12 = -f.getY();
		mat.m22 = -f.getZ();
		mat.m30 = -Vector3f.dot(s, eye);
		mat.m31 = -Vector3f.dot(u, eye);
		mat.m32 = Vector3f.dot(f, eye);
		return mat;
	}

	public static Matrix4f perspective(float fov, float aspect, float near, float far) {
		float top = (float) Math.tan(Math.toRadians(fov)) * near;
		float bottom = -top;
		float left = aspect * bottom;
		float right = aspect * top;
		return frustum(left, right, bottom, top, near, far);
	}

	public static Matrix4f frustum(float left, float right, float bottom, float top, float near, float far) {
		Matrix4f mat = (Matrix4f) new Matrix4f().setZero();
		mat.m00 = (2 * near) / (right - left);
		mat.m11 = (2 * near) / (top - bottom);
		mat.m20 = (right + left) / (right - left);
		mat.m21 = (top + bottom) / (top - bottom);
		mat.m22 = -(far + near) / (far - near);
		mat.m23 = -1;
		mat.m32 = -(2 * far * near) / (far - near);
		return mat;
	}
}
