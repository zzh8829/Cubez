package ca.zihao.cubez;

import ca.zihao.utility.ResLoader;
import ca.zihao.utility.ShaderLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * Created by Zihao on 6/7/2014.
 */
public class CubeletRenderer {

	static float[] vertices = new float[]{
			-0.5f, -0.5f, 0.5f,
			0.5f, -0.5f, 0.5f,
			0.5f, 0.5f, 0.5f,
			-0.5f, 0.5f, 0.5f
	};

	static float[] normals = new float[]{
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f
	};

	static float[] uvs = new float[]{
			0.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 0.0f
	};

	static byte[] indices = new byte[]{
			0, 1, 2,
			2, 0, 3
	};

	static int[][] faceColors = new int[][]{
			{232, 112, 0, 255}, //Up
			{255, 255, 255, 255}, //Front
			{0, 157, 84, 255}, //Left
			{220, 66, 47, 255}, //Down
			{245, 180, 0, 255}, //Back
			{61, 129, 246, 255}, //Right
			{0, 0, 0, 255} // None
	};

	int verticesVBO, normalsVBO, uvsVBO, indicesVBO;
	int shader;

	public CubeletRenderer() {

	}

	void init() {
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
		FloatBuffer normalsBuffer = BufferUtils.createFloatBuffer(normals.length);
		FloatBuffer uvsBuffer = BufferUtils.createFloatBuffer(uvs.length);
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indices.length);
		verticesBuffer.put(vertices);
		normalsBuffer.put(normals);
		uvsBuffer.put(uvs);
		indicesBuffer.put(indices);
		verticesBuffer.flip();
		normalsBuffer.flip();
		uvsBuffer.flip();
		indicesBuffer.flip();
		verticesVBO = glGenBuffers();
		normalsVBO = glGenBuffers();
		uvsVBO = glGenBuffers();
		indicesVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, verticesVBO);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, normalsVBO);
		glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, uvsVBO);
		glBufferData(GL_ARRAY_BUFFER, uvsBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVBO);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

		shader = ShaderLoader.loadShader(
				ResLoader.loadAsStream("shaders/cubelet.vert"),
				ResLoader.loadAsStream("shaders/cubelet.frag"));

	}

	void render(Matrix4f mvp, Cube cube) {

		glUseProgram(shader);

		glUniform1i(glGetUniformLocation(shader, "U_CUBELET_HIT"), cube.hover.cubelet);
		glUniform1i(glGetUniformLocation(shader, "U_FACE_HIT"), cube.hover.faceId);

		for(int i = 0; i != 27; i++) {
			glUniform1i(glGetUniformLocation(shader, "U_CUBELET"), i);
			renderCubelet(cube.cubelets[i], Matrix4f.mul(mvp, cube.transformation, null));
		}

		glUseProgram(0);
	}

	void renderCubelet(Cubelet cubelet, Matrix4f cubeMatrix) {
		for(int face = 0; face != 6; face++) {
			glUniform1i(glGetUniformLocation(shader, "U_FACE"), face);
			renderFace(face, cubelet.faces[face], Matrix4f.mul(cubeMatrix, cubelet.transformation, null));
		}
	}

	void renderFace(int face, int lface, Matrix4f cubeletMatrix) {
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
				return;
		}
		Matrix4f faceMatrix = Matrix4f.mul(cubeletMatrix, localMatrix, null);

		int lmvp = glGetUniformLocation(shader, "U_MODEL_VIEW_PROJECTION");
		int lcol = glGetUniformLocation(shader, "U_COLOR");
		int lpos = glGetAttribLocation(shader, "A_POSITION");
		int lnormal = glGetAttribLocation(shader, "A_NORMAL");
		int luv = glGetAttribLocation(shader, "A_UV");

		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
		faceMatrix.store(matBuffer);
		matBuffer.flip();
		glUniformMatrix4(lmvp, false, matBuffer);

		glUniform4f(lcol, faceColors[lface][0], faceColors[lface][1], faceColors[lface][2], faceColors[lface][3]);

		glBindBuffer(GL_ARRAY_BUFFER, verticesVBO);
		glVertexAttribPointer(lpos, 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, normalsVBO);
		glVertexAttribPointer(lnormal, 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, uvsVBO);
		glVertexAttribPointer(luv, 2, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		glEnableVertexAttribArray(lpos);
		glEnableVertexAttribArray(lnormal);
		glEnableVertexAttribArray(luv);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVBO);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_BYTE, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

		glDisableVertexAttribArray(lpos);
		glDisableVertexAttribArray(lnormal);
		glDisableVertexAttribArray(luv);
	}
}
