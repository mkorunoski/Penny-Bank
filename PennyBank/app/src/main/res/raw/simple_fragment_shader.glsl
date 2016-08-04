precision mediump float; 
      	 								
varying vec2 f_TexCoords;

uniform sampler2D diffuse;
uniform vec4 u_Color;

void main() {
    vec4 sampledColor = texture2D(diffuse, f_TexCoords);

    if (sampledColor.a < 0.1)
        discard;

    sampledColor.rgb *= smoothstep(0.1, 1.0, sampledColor.a);
    gl_FragColor = u_Color * sampledColor;
}