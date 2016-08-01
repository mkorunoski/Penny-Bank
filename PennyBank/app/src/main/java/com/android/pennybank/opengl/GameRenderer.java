package com.android.pennybank.opengl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.android.pennybank.R;

public class GameRenderer implements Renderer {
    private final Context context;

    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXCOORDS_COMPONENT_COUNT = 2;
    private static final int COMPONENT_COUNT = POSITION_COMPONENT_COUNT + TEXCOORDS_COMPONENT_COUNT;
    private static final int STRIDE = COMPONENT_COUNT * BYTES_PER_FLOAT;

    private static final String A_POSITION = "a_Position";
    private static final String A_TEXCOORDS = "a_TexCoords";
    private static final String U_COLOR = "u_Color";
    private static final String U_MATRIX = "u_Matrix";
    private int aPositionLocation;
    private int aTexCoordsLocation;
    private int uColorLocation;
    private int uMatrixLocation;

    private int program;

    private final FloatBuffer vertexData;

    private int checkerTexture;

    float[] vertices =
    {
//           x   y  s  t
            +1, -1, 1, 1,
            -1, -1, 0, 1,
            -1, +1, 0, 0,
            +1, -1, 1, 1,
            -1, +1, 0, 0,
            +1, +1, 1, 0
    };

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    public GameRenderer() {
        context = null;
        vertexData = null;
    }

    public GameRenderer(Context context) {
        this.context = context;

        vertexData = ByteBuffer
                .allocateDirect(vertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexData.put(vertices);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        glUseProgram(program);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTexCoordsLocation = glGetAttribLocation(program, A_TEXCOORDS);
        uColorLocation = glGetUniformLocation(program, U_COLOR);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        vertexData.position(0);
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        vertexData.position(POSITION_COMPONENT_COUNT);
        glEnableVertexAttribArray(aTexCoordsLocation);
        glVertexAttribPointer(aTexCoordsLocation, TEXCOORDS_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

        checkerTexture = TextureHelper.loadTexture(context, R.drawable.wheel_of_fortune);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        glViewport(0, 0, width, height);
        final float aspectRatio = width > height ? (float) width / (float) height : (float) height / (float) width;
        if (width > height) {
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        glClear(GL_COLOR_BUFFER_BIT);

        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, 1, 0, 0, -1);

        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

        glBindTexture(GL_TEXTURE_2D, checkerTexture);
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        glDrawArrays(GL_TRIANGLES, 0, vertices.length / COMPONENT_COUNT);
    }
}
