package com.fysx.game2d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class AGGeometry {
	/**
	 * провер€ем с какой стороны от пр€мой находитс€ точка
	 * @return
	 */
	public  int checkSide(AGVector2D v0, AGVector2D v1, AGVector2D v2){
		return (int)((v1.getX() - v0.getX()) * (v2.getY() - v0.getY()) - 
					 (v2.getX() - v0.getX()) * (v1.getY() - v0.getY()));
	}
	public  double[] calcCoef(AGVector2D v1, AGVector2D v2){
		double x1, y1, x2, y2, dx, dy, A, B, C;
		 x1 = v1.getX();
		 y1 = v1.getY();
		 x2 = v2.getX();
		 y2 = v2.getY();
		 dx = x2 - x1;
		 dy = y2 - y1;
		 if(dx == 0.0f){
			 A = 1; 
			 B = 0; 
			 C = -x1;
		 }
		 else if(dy == 0.0f){
			 A = 0; 
			if(x2>x1) {B = -1; C = y1;}
			else {B = 1; C = y2;}
			
		 }
		 else {
			 A = dy / dx; 
			if(y2>y1){ B = -1; 
			 C = -A * x1 + y1;}
			else{ B = 1; 
			 C = -A * x2 + y2;}
		 }
		 A = y1-y2;
		 B = x2-x1;
		 C = x1*y2 - x2*y1;
		 return new double[] {A, B, C};
	}
	public  double[] calcCoefViaDir(AGVector2D point, AGVector2D v){//поиск уравнени€ пр€мой по направл€ющему вектору
		double x, y, x1, y1, A, B, C=0;
		x = v.getX();
		y = v.getY();
		x1 = point.getX();
		y1 = point.getY();
		A = y;
		B = -x;
		C = -x1*y+y1*x;
		return new double[] {A, B, C};
	}
	public  double[] calcCoefViaNormal(AGVector2D v, AGVector2D point){
		double x, y, x1, y1, A, B, C=0;
		 x = v.getX();
		 y = v.getY();
		 x1 = point.getX();
		 y1 = point.getY();
		 A = x;
		 B = y;
		 C = -(x1*x+y1*y);
		 return new double[] {A, B, C};
	}
	public void drawLine(double[] in, GL10 gl, double[] color){
		float vertices[] = {
			      -10f,  -((float)in[0]*-10+(float)in[2])/(float)in[1],
			      10f,  -((float)in[0]*10+(float)in[2])/(float)in[1],
			};

			// a double is 4 bytes, therefore we multiply the number if vertices with 4.
			ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			FloatBuffer vertexBuffer = vbb.asFloatBuffer();
			vertexBuffer.put(vertices);
			vertexBuffer.position(0);
			try{
						gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);	
			}
			catch(Exception e){
				Log.e("exc", e.toString());
			}
			gl.glPushMatrix();
			gl.glColor4f((float)color[0],(float)color[1],(float)color[2],(float)color[3]);
			gl.glDrawArrays(GL10.GL_LINE_LOOP, 0,2);
			gl.glPopMatrix();
	}
	public  double[] calcCoefViaCenter(AGVector2D v){
		double A, B, C;
		 A = v.getY();
		 B = -v.getX();
		 C = 0;
		 return new double[] {A, B, C};
	}
	/**
	 * находим массив коэффициентов перпендикул€рной пр€мой
	 * @param inCoef - вход€ща€ пр€ма€
	 */
	public  double[] calcNormalCoef(double[] inCoef){
		 double A, B, C;
		 A = inCoef[0];
		 B = inCoef[1];
		 C = inCoef[2];
		 if(B == 0){
			 return new double[] {0, 1.0f, 0};
		 } 
		 else if (A == 0) {
			 return new double[] {1.0f, 0, 0};
		 }
		 else return new double[] {-1.0f / (A / B), 1.0f, 0};
	}
	/**
	 * поиск точки P
	 */
	public  AGVector2D findP(double[] inCoef){
		return findCross(inCoef, calcNormalCoef(inCoef));
	}
	/**
	 * поиск точки пересечени€ двух пр€мых 
	 */
	/*public  AGVector2D findCross(double[] v1, double[] v2){
		 double A, B, C, A2, B2, C2, x, y;
		 A = v1[0];
		 B = v1[1];
		 C = v1[2];
		 //¬тора€ пр€ма€ проходит через начало координат
		 //ѕр€мые перпендикул€рны
		 if(A == 0){
			 return new AGVector2D(0, -C/B);
		 }
		 if(B == 0){
			 return new AGVector2D(-C/A,0);
		 }
		 else{
		    A2 = v2[0];
		    B2 = v2[1];
		    C2 = v2[2];
		    double bb = B2/B;
		    x = C * bb/(A2 - A * bb) - C2;
		    y = -(A * x + C) / B;
		    return new AGVector2D(x, y);
		 }
	}*/
	double det (double a, double b, double c, double d) {
		return a * d - b * c;
	}
	public AGVector2D findCross(double[] v1, double[] v2) {
		double EPS = 1e-9f;
		double zn = det (v1[0], v1[1], v2[0], v2[1]);
	//	Log.e("p","zn="+zn);
		if (Math.abs (zn) < EPS) return  new AGVector2D();
		double x = - det (v1[2], v1[1], v2[2], v2[1]) / zn;
		double y = - det (v1[0], v1[2], v2[0], v2[2]) / zn;
		return  new AGVector2D(x,y);
	}
	public AGVector2D findCross1(double[] v1, double[] v2) {
		double EPS = 1e-9f;
		double zn = det (v1[0], v1[1], v2[0], v1[1]);
		Log.e("p","zn="+zn);
		if (Math.abs (zn) < EPS) return  new AGVector2D();
		double x=0,y;
		y = (-v2[2]+v2[0]*v1[2]/v1[0])/(-v2[0]*v1[1]/v1[0]+v2[1]);
		x = -(v1[2]+v1[1]*y)/v1[0];
		return  new AGVector2D(x,y);
	}
	public boolean checkPointInSegment(AGVector2D a, AGVector2D b, AGVector2D x){
		double EPS = 1e-3f;
		double coef[] = calcCoef(a, b);
		//if(coef[0]*x.getX()+coef[1]*x.getY()+coef[2]>EPS) return false;
		double p=-1;
		if(a.getX() - b.getX()!=0)
			p = (x.getX() - b.getX())/(a.getX() - b.getX());
		else if(a.getY() - b.getY()!=0)
			p = (x.getY() - b.getY())/(a.getY() - b.getY());
		
		if(p>=0 && p<=1) return true;
		else return false;
	}
	public AGVector2D findNormal(AGVector2D a, AGVector2D b){
		double coefs[] = calcCoef(a,b);
		return new AGVector2D(coefs[0], coefs[1]);
	}
}
