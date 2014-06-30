package ca.zihao.cubez;

import ca.zihao.utility.Ray;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Zihao on 6/13/2014.
 */
public class Cubeface implements Comparable<Cubeface> {
	int cubelet;
	int face;
	int faceId;
	float distance;

	Vector3f tl, tr, bl, br;

	public Cubeface(int face, int faceId, Vector3f tl, Vector3f tr, Vector3f bl, Vector3f br) {
		this.cubelet = -1;
		this.face = face;
		this.faceId = faceId;
		this.tl = tl;
		this.tr = tr;
		this.bl = bl;
		this.br = br;
	}

	public float intersect(Ray ray) {
		float d1 = ray.intersect(tl, tr, bl);
		float d2 = ray.intersect(bl, br, tr);
		if(d1 == -1 && d2 == -1) {
			distance = -1;
		} else {
			distance = Math.max(d1, d2);
		}
		return distance;
	}

	@Override
	public int compareTo(Cubeface o) {
		float td = distance;
		float od = o.distance;
		if(td == -1) td = 10000000;
		if(od == -1) od = 10000000;
		if(td == od) {
			return 0;
		} else if(td > od) {
			return 1;
		} else {
			return -1;
		}
	}
}
