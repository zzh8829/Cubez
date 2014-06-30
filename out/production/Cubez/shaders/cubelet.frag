#version 110

uniform vec4 U_COLOR;
uniform sampler2D U_TEX;
uniform int U_CUBELET_HIT;
uniform int U_FACE_HIT;
uniform int U_CUBELET;
uniform int U_FACE;

varying vec3 V_NORMAL;
varying vec3 V_POS;
varying vec2 V_UV;

void main()
{
	vec4 color = U_COLOR/255.0;

	float border = 0.05;
	float border_rad = 0.07;
	float border_tot = border + border_rad;

	float cx = 0.5 - abs(0.5 - V_UV.x);
	float cy = 0.5 - abs(0.5 - V_UV.y);
	if(cx < border || cy < border ||
		(
			(cx < border_tot && cy < border_tot) &&
			((cx-border_tot)*(cx-border_tot) + (cy-border_tot)*(cy-border_tot) > border_rad*border_rad)
		))
	{
		if(U_CUBELET_HIT == U_CUBELET && U_FACE_HIT == U_FACE) {
    		color = vec4(0,0,0,1);
    	} else {
			color = vec4(0,0,0,1);
		}
	}
	if(cx < 0.001 || cy < 0.001)
	{
		color = vec4(0.2,0.2,0.2,1);
	}

	if(U_CUBELET_HIT == U_CUBELET && U_FACE_HIT == U_FACE) {
        color /= 1.2;
    }

	gl_FragColor =  vec4(color.xyz,1);
}