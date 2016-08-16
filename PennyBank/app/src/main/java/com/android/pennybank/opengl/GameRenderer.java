package com.android.pennybank.opengl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.frustumM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.widget.Toast;

import com.android.pennybank.R;
import com.android.pennybank.activities.GameActivity;
import com.android.pennybank.util.Logger;

public class GameRenderer implements Renderer {
    private final Context context;

    private int width = 600;
    private int height = 951;

    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXCOORDS_COMPONENT_COUNT = 2;
    private static final int COMPONENT_COUNT = POSITION_COMPONENT_COUNT + TEXCOORDS_COMPONENT_COUNT;
    private static final int STRIDE = COMPONENT_COUNT * BYTES_PER_FLOAT;

    private static final String A_POSITION = "a_Position";
    private static final String A_TEXCOORDS = "a_TexCoords";
    private static final String U_COLOR = "u_Color";
    private static final String U_MODEL = "u_Model";
    private static final String U_VIEW = "u_View";
    private static final String U_PROJECTION = "u_Projection";

    private int aPositionLocation;
    private int aTexCoordsLocation;
    private int uColorLocation;
    private int uModelMatLocation;
    private int uViewMatLocation;
    private int uProjectionMatLocation;

    private int program;

    private final FloatBuffer vertexData;

    private static final float D = 0.25f;
    float[] vertices =
            {
//           x   y  s  t
                    -D, -D, 0, 0,
                    +D, -D, 1, 0,
                    +D, +D, 1, 1,
                    -D, -D, 0, 0,
                    +D, +D, 1, 1,
                    -D, +D, 0, 1
            };

    private int pennybankTexture;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

//    public GameRenderer() {
//        context = null;
//        vertexData = null;
//    }

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
        glEnable(GL_DEPTH_TEST);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        glUseProgram(program);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTexCoordsLocation = glGetAttribLocation(program, A_TEXCOORDS);
        uColorLocation = glGetUniformLocation(program, U_COLOR);
        uModelMatLocation = glGetUniformLocation(program, U_MODEL);
        uViewMatLocation = glGetUniformLocation(program, U_VIEW);
        uProjectionMatLocation = glGetUniformLocation(program, U_PROJECTION);

        vertexData.position(0);
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        vertexData.position(POSITION_COMPONENT_COUNT);
        glEnableVertexAttribArray(aTexCoordsLocation);
        glVertexAttribPointer(aTexCoordsLocation, TEXCOORDS_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

        pennybankTexture = TextureHelper.loadTexture(context, R.drawable.pennybank_icon);

        setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // This is not working.
        this.width = width;
        this.height = height;

        glViewport(0, 0, width, height);
        final float aspectRatio = width > height ? (float) width / (float) height : (float) height / (float) width;
        if (width > height) {
            frustumM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1.0f, 1.0f, 1.0f, 100.0f);
        } else {
            frustumM(projectionMatrix, 0, -1.0f, 1.0f, -aspectRatio, aspectRatio, 1.0f, 100.0f);
        }
    }

    private volatile PointF tapedPosition;
    private int scoredPoints = 0;

    public void setTapedPosition(PointF tapedPosition) {
        this.tapedPosition = tapedPosition;
        map();
    }

    private void map() {
        float oldX = tapedPosition.x;
        float oldY = tapedPosition.y;
        float aspectRatio = width > height ? (float) width / (float) height : (float) height / (float) width;

//        [A, B] --> [a, b]
//        (val - A)*(b-a)/(B-A) + a

//        [0, width] -> [-1, 1]
        float newX = oldX * 2.0f / (float) width - 1.0f;
//        [0, height] -> [-aspectRatio, aspectRatio]
        float newY = oldY * 2.0f * aspectRatio / (float) height - aspectRatio;

        if ((newX >= (this.x - 2 * D) && newX <= (this.x + 2 * D)) &&
                (newY >= (this.y - 2 * D) && newY <= (this.y + 2 * D))) {
            scoredPoints++;
            ((GameActivity) context).setScore(scoredPoints);
        }
    }

    private final Random random = new Random();
    private float x = 0.0f;
    private float y = 0.0f;

    private int sleepTimeMS = 500;

    @Override
    public void onDrawFrame(GL10 glUnused) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        setIdentityM(modelMatrix, 0);
        x = random.nextFloat() * 2.0f - 1.0f;
        y = random.nextFloat() * 2.0f - 1.0f;
        translateM(modelMatrix, 0, x, y, 0.0f);
        rotateM(modelMatrix, 0, random.nextFloat() * 360.0f, 0.0f, 0.0f, 1.0f);

        glBindTexture(GL_TEXTURE_2D, pennybankTexture);
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glUniformMatrix4fv(uModelMatLocation, 1, false, modelMatrix, 0);
        glUniformMatrix4fv(uViewMatLocation, 1, false, viewMatrix, 0);
        glUniformMatrix4fv(uProjectionMatLocation, 1, false, projectionMatrix, 0);
        glDrawArrays(GL_TRIANGLES, 0, vertices.length / COMPONENT_COUNT);

        try {
            Thread.sleep(sleepTimeMS);
            if (sleepTimeMS > 100) {
                sleepTimeMS--;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

