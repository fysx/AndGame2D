package com.fysx.game2d;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import com.fysx.game2d.AccelerometerListener;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;
import android.content.Context;
import android.content.pm.ActivityInfo;

public class AndGame2DActivity extends Activity implements AccelerometerListener {
	static Context _context;
	private AGRenderer _renderer;
	private GLSurfaceView _glSurface;
	/** x,y*/
	private float _x, _y;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

		//создаем супфейс в активити
    	_glSurface = new GLSurfaceView(this);
		//устанавливаем рендерер
		_renderer = new AGRenderer(this);
		_glSurface.setRenderer(_renderer);
		//устанавливаеем ориентацию
	//	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//оставлл€ем подсветку
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(_glSurface);
    }
    public static Context getContext(){
    	return _context;
    }

    public void onShake(float force) {
        Toast.makeText(this, "Phone shaked : " + force, 1000).show();
    }
    public void onAccelerationChanged(float x, float y, float z) {
		_renderer.setXY((-y/4) , 
						(x));
    }
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glShadeModel(GL10.GL_SMOOTH); 
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClearDepthf(1.0f); 		
	}

	public boolean onTouchEvent(final MotionEvent event) {
		_renderer.touch(event.getAction());
		return true;
		}
	/**
	 * цикл отрисоки
	 */
	public void onDrawFrame(GL10 gl) {
		//очищаем экран
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		gl.glLoadIdentity();					//грузим единичную матрицу
		GLU.gluLookAt(gl, 0,   -23.0f, 30.0f, // ѕоложение глаз, взгл€д "из"
						  0,    18.f+2000000000f*(_y/1000000000f),  0.0f, // ÷ель, взгл€д "на"
						  1000000000f*(_x/1000000000f), 10.0f, 10.0f);	
		/*GLU.gluLookAt(gl, gamer.getPos(),   -13.5f, 4.5f, // ѕоложение глаз, взгл€д "из"
				gamer.getPos(),    1000f+2f*_y,  2.5f, // ÷ель, взгл€д "на"
				  _x, 10.0f, 10.0f);	*/
		Log.e("1","x="+_x);
		//gl.glPushMatrix();
		gl.glPushMatrix();
		//рисуем игрока 
	}
	/**
	 * пауза в игре
	 */
	public void PauseGame()
	{
		
	}
	/** ресетим игру
	 * 
	 */
	public void ResetGame()
	{
		_x = 0;
		_y = 0;
	}
	/** начинаем игру
	 * 
	 */
	public void StartGame()
	{
		
	}
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) { 						//Prevent A Divide By Zero By
			height = 1; 						//Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 400.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}
}