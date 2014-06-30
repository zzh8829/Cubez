#version 110

attribute vec3 A_POSITION;
attribute vec3 A_NORMAL;
attribute vec2 A_UV;

uniform mat4 U_MODEL_VIEW_PROJECTION;

varying vec3 V_NORMAL;
varying vec3 V_POS;
varying vec2 V_UV;

void main()
{
	gl_Position = U_MODEL_VIEW_PROJECTION * vec4(A_POSITION,1);

	V_NORMAL = A_NORMAL;
	V_POS = A_POSITION;
	V_UV = A_UV;
}