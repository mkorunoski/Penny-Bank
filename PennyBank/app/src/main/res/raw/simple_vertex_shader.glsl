attribute vec2 a_Position;
attribute vec2 a_TexCoords;

uniform mat4 u_Matrix;

varying vec2 f_TexCoords;

void main()
{
    f_TexCoords = a_TexCoords;
    gl_Position = u_Matrix * vec4(a_Position, 0, 1);
}
