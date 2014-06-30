package ca.zihao.utility;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Zihao on 6/13/2014.
 */
public class Ray {
	Vector3f org;
	Vector3f dir;

	public Ray(Vector3f org, Vector3f dir) {
		this.org = org;
		this.dir = dir;
	}

	public float intersect(Vector3f v1, Vector3f v2, Vector3f v3) {
		Vector3f e1 = Vector3f.sub(v2, v1, null);
		Vector3f e2 = Vector3f.sub(v3, v1, null);

		Vector3f dce = Vector3f.cross(dir, e2, null);

		float deter = Vector3f.dot(dce, e1); // triple product e1 . (e2 x d)

		if(deter > -0.000001f && deter < 0.000001f) {
			return -1;
		}

		float inv = 1 / deter;

		Vector3f u = Vector3f.sub(org, v1, null);

		float tu = Vector3f.dot(dce, u) * inv;

		if(tu < 0 || tu > 1) {
			return -1;
		}

		Vector3f v = Vector3f.cross(u, e1, null);

		float tv = Vector3f.dot(dir, v) * inv;

		if(tv < 0 || tu + tv > 1) {
			return -1;
		}

		float dis = Vector3f.dot(v, e2) * inv;

		if(dis < 0) {
			return -1;
		}
		return dis;
	}
}
