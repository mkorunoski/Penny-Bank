attribute vec2 a_Position;
attribute vec2 a_TexCoords;

uniform mat4 u_Model;
uniform mat4 u_View;
uniform mat4 u_Projection;

varying vec2 f_TexCoords;

void main() {
    f_TexCoords = a_TexCoords;
    mat4 MVP = u_Projection * u_View * u_Model;
    gl_Position = MVP * vec4(a_Position, 0.0, 1.0);
}
