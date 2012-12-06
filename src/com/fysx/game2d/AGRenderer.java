package com.fysx.game2d;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.MotionEvent;

import com.fysx.game2d.AGVector2D;

public class AGRenderer implements Renderer {
	private float _x, _y;
	
	private float _worldTimer=0;
	
	private AGObject2D[] _objects;
	private AGPhysics _phys;
	private int _state=1;
	public AGRenderer(Context context){
	}
	public void setXY(float x, float y){
		_x = 2*x;
		_y = 1.0f*y;
	}
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		_objects = new AGObject2D[2];
		_phys= new AGPhysics();
		float[] vertices = {
				-0.5f,-0.5f,
				0.5f,-0.5f,
				0.5f,0.5f,
				-0.5f,0.5f,
				//0f,0f,
		};
		float[] vertices2 = {
				-2.5f,-0.5f,
				2.5f,-0.5f,
				2.5f,0.5f,
				-2.5f,0.5f,
				//0f,0f,
		};
		_objects[0] = new AGObject2D(vertices2);
		_objects[1] = new AGObject2D(vertices);
		//_objects[2] = new AGObject2D(vertices);
		//_objects[3] = new AGObject2D(vertices2);
		_objects[0].setInvMass(0.0f);
		_objects[1].setInvMass(1000.1f);
		//_objects[2].setInvMass(10.1f);
		//_objects[3].setInvMass(0);
		_objects[0].setInertialMoment(0.0f);
		_objects[1].setInertialMoment(100.1f);
		/*_objects[2].setInertialMoment(0.001f);
		_objects[3].setInertialMoment(0);*/

		//_objects[0].setPosition(new AGVector2D(0,0));
		_objects[1].setPosition(new AGVector2D(1.5f,3.3f));
		//_objects[2].setPosition(new AGVector2D(-2.4f,3f));
		//_objects[3].setPosition(new AGVector2D(-1f,-2f));
		_objects[1].setOrientation((float)Math.PI/10);
		//_objects[2].setOrientation((float)Math.PI/8);
		//_objects[1]._linearVelocity = new AGVector2D(0,-1f);
		//_objects[2]._linearVelocity = new AGVector2D(0,0.1f);
		//_objects[1]._angularVelocity = 0.1f;
		//_objects[2]._angularVelocity = -0.1f;
	//	_objects[1]._linearAcceleration.setPosition(-1.5f, 13.9f);
		//_objects[2]._linearAcceleration.setPosition(0, -0.9f);
	}

	/**
	 * цикл отрисоВки
	 */
	private int tmp=0;
	public void onDrawFrame(GL10 gl) {
		if(_state==1)
			_worldTimer+=0.00001f;
		//очищаем экран
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		gl.glLoadIdentity();					//грузим единичную матрицу

		/*if(_objects[1]._linearAcceleration.getY()>-0.009f)
			_objects[1].applyForce(new AGVector2D(0.000f,-0.09f),new AGVector2D(0f,0f));
		if(_objects[2]._linearAcceleration.getY()>-0.009f)
			_objects[2].applyForce(new AGVector2D(0.000f,-0.09f),new AGVector2D(0f,0f));*/
		//_objects[1].setLinearAcceleration(new AGVector2D(0,-0.98f));
	//	_objects[2].setLinearAcceleration(new AGVector2D(0,-0.98f));
		AGVector2D gravity = new AGVector2D(0,-9.8);

		AGObject2D tmpObj = new AGObject2D(_objects[0].minkovskyDifference(_objects[1]));
		
		gl.glColor4f(1.0f, 1f, 1f, 1f);
		_objects[1].draw(gl);

		/*gl.glColor4f(1.0f, 0f, 0f, 1f);
		_objects[2].draw(gl);
		gl.glColor4f(1.0f, 0f, 0f, 1f);
		_objects[3].draw(gl);*/
		gl.glColor4f(1.0f, 0f, 0f, 1f);
		_objects[0].draw(gl);
		//gl.glColor4f(0.5f, 0.1f, 0.8f, 1f);
		tmpObj.draw(gl);
		
		//if(_state==1){
			//gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		//	_objects[0].calculatePosition(_worldTimer);
		//	_objects[1].calculatePosition(_worldTimer);
		//	_objects[2].calculatePosition(_worldTimer);
		//	_objects[3].calculatePosition(_worldTimer);
			_phys.calculateColisions(_objects);
			
			//_objects[0].update(gravity, _worldTimer);
			double dt = 0.01;
			if(_state==1) dt = 0.00;
			_objects[0].update(gravity, dt);
			_objects[1].update(gravity, dt);
			//_objects[2].update(gravity, _worldTimer);
			//_objects[3].update(gravity, _worldTimer);
			
		//}
		
	}
	/**
	 * пауза в игре
	 */
	public void PauseGame()
	{
		
	}
	/**
	 *  ресетим игру
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
		//GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, -5.1f, 400.0f);

		gl.glOrthof(-5f, 5f, -5f*height/width, 5f*height/width, -2f, 12f);
		   // Устанавливаем точку, в которой
		   // находится наш глаз ---(0,0,5)
		   // направление, куда смотрим --- (0,0,0)
		   // вектор, принимаемый за направление вверх --- (0,1,0)
		   // этим вектором является ось Y
		GLU.gluLookAt(gl,
						0f,0f,5f, 
						0f,0f,0f, 
						0f,1f,0f );

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}
	public void touch(int state){
		if(state==0)_state=_state==1?0:1;
	}
}
