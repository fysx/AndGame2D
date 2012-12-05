package com.fysx.game2d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.fysx.game2d.AGVector2D;

public class AGObject2D {
	Random generator;
	
	AGGeometry _geom;
	GL10 _gl;
	private AGVector2D direction;
	
	private AGVector2D _closestPoint;
	private AGVector2D _closestPoint1;
	private AGVector2D _closestPoint2;
	private AGVector2D _closestPoint3;
	private double _penetrationDepth;
	private int _last_index;
	private AGVector2D _normal = new AGVector2D();
	
	private AGVector2D[] _vertices;
	private AGVector2D[] _verticesGlobal;
	private double _cDepth = 0.1;
	
	private double[] _rectSize = new double[2];
	private boolean _visible = false;
	private FloatBuffer _vertexBuffer;
	private FloatBuffer _textureBuffer;
	
	private FloatBuffer _simplexBuffer;

	private AGVector2D _position;//позиция
	private double _orientation;//текущий угол в радианах
	private double _inertialMoment;//главный момент инерции
	public double _invMass;//величина, обратная массе тела
	private double _torque;//крутящий момент
	
	public AGVector2D _linearVelocity;//скорость тела
	public AGVector2D _linearAcceleration; //линейное ускорение
	
	public double _angularVelocity;//угловая скорость
	public double _angularAcceleration; //угловое
	
	public double _restitution;//коэффициент восстановления(упругость)
	public double _friction;
	
	Vector<AGVector2D> simplex;
	private int _size=0;
	AGObject2D(float[] vertices){
		//_vertexBuffer = 
		
		_geom = new AGGeometry();
		simplex = new Vector<AGVector2D>();
			buildBuffer(vertices);
			_orientation = 0;
			_restitution = 0.5f;
			_torque = 0;
			_position = new AGVector2D(0,0);
			_closestPoint = new AGVector2D(0,0);
			_closestPoint1 = new AGVector2D(0,0);
			_closestPoint2 = new AGVector2D(0,0);
			_closestPoint3 = new AGVector2D(0,0);
			_invMass = 0;
			_linearVelocity = new AGVector2D(0,0);
			_linearAcceleration = new AGVector2D(0,0);
			_penetrationDepth = 0;
			//_angularVelocity = new AGVector2D(0.1f,0.1f);
			//_angularAcceleration = new AGVector2D(0.0f,0.0f);
			_angularVelocity = 0f;
			_angularAcceleration = 0f;
			generateVertices(vertices);

			direction = new AGVector2D(0, 0);
			generator= new Random();
			_friction = 0.7;
	}
	private void calculateInertia(){
		double sum1 = 0, sum2 = 0, a=0, b = 0;
		for(int i=0;i<_vertices.length-1;i++){
			AGVector2D v1 = _vertices[i];
			AGVector2D v2 = _vertices[i+1];
			a = v2.cross(v1);
			b = v1.multiply(v1) + v2.multiply(v1)+v2.multiply(v2);
			
			sum1 += a*b;
			sum2 += a;
		}
		_inertialMoment = 6*_invMass*sum2/sum1;
	}
	private void  generateVertices(float[] vertices){
		_vertices = new AGVector2D[_size/2];
		int k=0;
		for (int i=0;i<_size;i+=2){
			_vertices[k]=new AGVector2D(vertices[i], vertices[i+1]);
			k++;
		}
	}
	private void buildBuffer(float[] v){
		_size = v.length;
		ByteBuffer Buf = ByteBuffer.allocateDirect(_size * 4);
		Buf.order(ByteOrder.nativeOrder());
		_vertexBuffer = Buf.asFloatBuffer();
		_vertexBuffer.put(v);
		_vertexBuffer.position(0);
		
		ByteBuffer Buf1 = ByteBuffer.allocateDirect(6 * 4);
		Buf1.order(ByteOrder.nativeOrder());
		_simplexBuffer = Buf1.asFloatBuffer();
		//_simplexBuffer.position(0);
		//return tmp;
	}
	/**
	 * 
	 * @param gravity - гравитация
	 * @param dt - отрезок времени
	 */
	public void update(AGVector2D gravity, double dt){
		_linearAcceleration = _linearAcceleration.plus(gravity.multiply(dt));
		_orientation += _angularAcceleration*dt;
		_position = _position.plus(_linearAcceleration.multiply(dt));
	}
	/**
	 * 
	 * @param point - точка приложения импульса в локальных координатах
	 * @param impulse - величина импульса
	 */
	public void applyImpulse(AGVector2D point, AGVector2D impulse){
		_linearAcceleration = _linearAcceleration.plus(impulse.multiply(_invMass));
		_angularAcceleration += point.cross(impulse)*_inertialMoment;
	}
	
	public void draw(GL10 gl){
		_gl=gl;
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, _vertexBuffer);
		//gl.glTexCoordPointer(2, GL10.GL_double, 0, textureBuffer);
		gl.glPushMatrix();
		gl.glTranslatef((float)_position.getX(), (float)_position.getY(), 0);
		double angle = _orientation/(Math.PI/180.);
		gl.glRotatef((float)angle, 0, 0,1);
		gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, _size/2);
		//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, _size/2);
		gl.glPopMatrix();
		
		//for debug
		float vertices[] = {
			      -0.05f,  0.05f,
			      -0.05f, -0.05f,
			      	0.05f, -0.05f,
			       0.05f,  0.05f, 
			};

			// a double is 4 bytes, therefore we multiply the number if vertices with 4.
			ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			FloatBuffer vertexBuffer = vbb.asFloatBuffer();
			vertexBuffer.put(vertices);
			vertexBuffer.position(0);

			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
			gl.glColor4f(0, 1, 0, 0);
			gl.glPushMatrix();
			gl.glTranslatef((float)_closestPoint.getX(), (float)_closestPoint.getY(), 0);
			gl.glDrawArrays(GL10.GL_LINE_LOOP, 0,4);
			//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, _size/2);
			gl.glPopMatrix();
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
			gl.glColor4f(0, 0, 1, 0);
			gl.glPushMatrix();
			gl.glTranslatef((float)_closestPoint1.getX(), (float)_closestPoint1.getY(), 0);
			gl.glDrawArrays(GL10.GL_LINE_LOOP, 0,4);
			//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, _size/2);
			gl.glPopMatrix();
			
			
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
			gl.glColor4f(0, 0, 1, 0);
			gl.glPushMatrix();
			gl.glTranslatef((float)_closestPoint2.getX(), (float)_closestPoint2.getY(), 0);
			gl.glDrawArrays(GL10.GL_LINE_LOOP, 0,4);
			//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, _size/2);
			gl.glPopMatrix();
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
			gl.glColor4f(0, 0, 1, 0);
			gl.glPushMatrix();
			gl.glTranslatef((float)_closestPoint3.getX(), (float)_closestPoint3.getY(), 0);
			gl.glDrawArrays(GL10.GL_LINE_LOOP, 0,4);
			//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, _size/2);
			gl.glPopMatrix();
	}
	public float[] minkovskyDifference(AGObject2D inner){
		int newSize = inner.getSize()*_size/2;
		float[] minkovskyDiff = new float[newSize];
		int counter=0;//счетчик для нового массива
		for (int i=0;i<_size/2;i++){
			for (int j=0;j<inner.getSize()/2;j++){
				minkovskyDiff[counter] = 
					((float)_vertices[i].rotate(_orientation).getX()+(float)_position.getX())-
					((float)inner.getVertex(j).rotate(inner.getOrientation()).getX()+(float)inner.getPosition().getX());
				minkovskyDiff[counter+1] = 
					((float)_vertices[i].rotate(_orientation).getY()+(float)_position.getY())-
					((float)inner.getVertex(j).rotate(inner.getOrientation()).getY()+(float)inner.getPosition().getY());
				counter+=2;
			}
		}
		/*Log.i("123", "*****************");
		for (int i=0;i<newSize;i++){
			Log.i("123", "+++ i="+i+" val = "+minkovskyDiff[i]);
		}
		Log.i("123", "*****************");*/
		return minkovskyDiff;
	}
	/**
	 * функция для нахождения support mapping
	 * @param normal - вектор в направлении которого ищется точка
	 */
	public AGVector2D getSupport(AGVector2D normal){
		AGVector2D rotatedVec = _vertices[0].rotate(_orientation);
		rotatedVec = rotatedVec.plus(_position);
		double max = normal.multiply(rotatedVec);
		int index=0;
		for (int i=1;i<_vertices.length;i++)
		{
			rotatedVec = _vertices[i].rotate(_orientation);
			rotatedVec = rotatedVec.plus(_position);
			double tmp = normal.multiply(rotatedVec);
			if(max<=tmp) 
			{
				max=tmp;
				index = i;
			}
		}
		_last_index = index;
		rotatedVec = _vertices[index].rotate(_orientation);
		return new AGVector2D(rotatedVec.getX()+_position.getX(), rotatedVec.getY()+_position.getY());
	}
	public AGVector2D getSupport(AGObject2D b, AGVector2D direction){
		return this.getSupport(direction).minus(b.getSupport(direction.minus()));
	}
	/**
	 * ищем взаимопроникновение с другим объектом или расстояние между объектами
	 * @param 
	 */
	/*public double findRange(AGObject2D inObject){
		int index1 = generator.nextInt(_size/2);
		int index2 = generator.nextInt(_size/2);
		return 0;
	}*/
	public boolean findRange(AGObject2D inObject){
		simplex.clear();
		// chose an initial direction
		// create the initial simplex
        if (direction.isZero())
        	direction.setPosition(inObject.getPosition().minus(_position));
        direction.normalize();
		//simplex.add(new AGVector2D(0,1));
		simplex.add(this.getSupport(inObject, direction));
		// set the direction to point towards the origin
		direction = direction.minus();
		int i=0;
		while(true)
		{
			// add a new point
			AGVector2D closestPoint = new AGVector2D(this.getSupport(inObject, direction));
			simplex.add(closestPoint);
			if(!isSameDirection(simplex.lastElement(), direction)) return false;
			else if(checkSimplex(simplex)){
				//_closestPoint.setPosition(_vertices[_last_index]);
				findPD(inObject);
				findContactManifold(inObject);
				return true;
			}
			// see if the new point was on the correct side of the origin
			// process the simplex
			i++;
		}
		//return false;
	}
	public boolean isSameDirection(AGVector2D v1, AGVector2D v2){
		return v1.multiply(v2) > 0;
	}
	protected boolean findPD(AGObject2D inObject){
		AGVector2D pd;
		if(direction.getLength()>0)
			pd = new AGVector2D(direction);
		else 
			pd = (this.getPosition().minus(inObject.getPosition())).normalize().minus();
		AGVector2D v = new AGVector2D(pd);
		double angEps = 1e-10f;
		double eps = 0.001f;
		int break1 =0;
		
		double OAlengthOld = 10000;
		
		while(true){
			v.setPosition(pd);
			int break2 =0;
			while(true){
				//ищем точку S в направлении v геометрии CSO
				AGVector2D S = this.getSupport(inObject, v);
				//Log.e("S", "S: "+S);
			double arr[] =  _geom.calcCoefViaNormal(v, S);
			if(_gl!=null)_geom.drawLine(arr, _gl, new double[] {1f, 0.4f, 0.4f, 0});
			
			double arr2[] =  _geom.calcCoefViaCenter(pd);
			if(_gl!=null)_geom.drawLine(arr2, _gl, new double[] {0.4f,1f, 0.4f, 0});
			
			double arr3[] =  _geom.calcCoef(new AGVector2D(), S);
			if(_gl!=null)_geom.drawLine(arr3, _gl, new double[] {0.4, 0.4, 1f, 0});
				
				AGVector2D A = _geom.findCross(_geom.calcCoefViaNormal(v, S), _geom.calcCoefViaCenter(pd));
				//Log.e("sa", "s-a="+S.minus(A).getLength());
				//Log.e("sa", "v.x="+v.plus(S.minus(A).multiply(eps)).normalize().getX()+"v.y="+v.plus(S.minus(A).multiply(eps)).normalize().getY());
				Log.e("iteration "+break2,  "A: "+A+"; \npd: "+pd);
				
				if(!A.isSameDirection(pd)) return false;
				if(A.minus(S).getLength()<=eps || break2>5) break;
				else v.setPosition(v.plus((A.minus(S).multiply(eps)).normalize()));
				if(A.getLength()>=OAlengthOld)break2++;
				else{
					break2 = 0;
					OAlengthOld = A.getLength();
				}
				//Log.e("a","0="+arr[0]+", 1="+arr[1]+", 2="+arr[2]);
				
			}

			AGVector2D S = inObject.getSupport(this, v);
				//Log.e("pd.getAngle(v)", "pd.getAngle(v): "+pd.getAngle(v));
			if(Math.abs(pd.getAngle(v))<angEps || break1>5) break;
			else pd.setPosition(v);
			break1++;
		}//Log.e("pd", "pd: "+pd);

		direction.setPosition(pd.normalize());
		return true;
	}
	protected void findContactManifold(AGObject2D inObject){
		AGContactConstraint constraint = AGContactConstraint.Instance();
		
		AGVector2D axis = new AGVector2D(direction);
		double delta = (double)Math.PI/30;
		AGVector2D a1 = this.getSupport(axis.rotate(delta));
		AGVector2D b1 = this.getSupport(axis.rotate(-delta));
		AGVector2D a2 = inObject.getSupport(axis.minus().rotate(delta));
		AGVector2D b2 = inObject.getSupport(axis.minus().rotate(-delta));
		//Log.e("1","a1="+a1+";\na2="+a2+";\nb1="+b1+";\nb2=b2"+b2+";");
		//_geom.drawLine(_geom.calcCoef(a2, b2), _gl, new double[] {0, 1, 0, 0});
		//_geom.drawLine(_geom.calcCoef(a1, b1), _gl,new double[] {1, 1, 0, 0});
		AGVector2D a11 = _geom.findCross(_geom.calcCoef(a2, b2),_geom.calcCoefViaDir(a1, axis));
		AGVector2D b11 = _geom.findCross(_geom.calcCoef(a2, b2),_geom.calcCoefViaDir(b1, axis));
		AGVector2D a21 = _geom.findCross(_geom.calcCoef(a1, b1),_geom.calcCoefViaDir(a2, axis));
		AGVector2D b21 = _geom.findCross(_geom.calcCoef(a1, b1),_geom.calcCoefViaDir(b2, axis));
		int index = -1;
		boolean checs[]={_geom.checkPointInSegment(a2,b2,a11),
						 _geom.checkPointInSegment(a2,b2,b11),
						 _geom.checkPointInSegment(a1,b1,a21),
						 _geom.checkPointInSegment(a1,b1,b21)};
		int contactCount = (checs[0]?1:0) + (checs[1]?1:0) + (checs[2]?1:0) + (checs[3]?1:0);
		Log.e("1","a11="+a11+";\nb11="+b11+"\na21="+a21+";\nb21="+b21);
		if(_gl!=null){
		/*_geom.drawLine(_geom.calcCoefViaDir(new AGVector2D(0,0), axis.minus()), _gl,new double[] {1, 1, 1, 0});*/
		_geom.drawLine(_geom.calcCoefViaDir(a1, axis), _gl,new double[] {0.5f, 1, 1, 0});
		_geom.drawLine(_geom.calcCoefViaDir(a2, axis), _gl,new double[] {1, 0.5f, 1, 0});
		_geom.drawLine(_geom.calcCoefViaDir(b1, axis), _gl,new double[] {0.5f, 1, 1, 0});
		_geom.drawLine(_geom.calcCoefViaDir(b2, axis), _gl,new double[] {1, 0.5f, 1, 0});
		
		_geom.drawLine(_geom.calcCoef(a1, b1), _gl,new double[] {1, 0, 1, 0});
		_geom.drawLine(_geom.calcCoef(a2, b2), _gl,new double[] {1, 1, 1, 0});
		}
		Log.e("cc","cc="+(checs[0]?1:0) + (checs[1]?1:0) + (checs[2]?1:0) + (checs[3]?1:0));
		if(contactCount==2)
		{
			if(checs[0]){
				_closestPoint.setPosition(a11);
				constraint.getContacts().add(
									new AGContactManifold(this, 
														  inObject,
														  _geom.findNormal(b2, a2),
														  a11,
														  a1.minus(a11).getLength()/2)
								);
			}
			if(checs[1]){
				_closestPoint1.setPosition(b11);
				if(!a11.equals(b11))
				{
					constraint.getContacts().add(
							new AGContactManifold(this, 
												  inObject,
												  _geom.findNormal(b2, a2),
												  b11,
												  b1.minus(b11).getLength()/2)
						);
				}
			}
			if(checs[2])
			{
				_closestPoint2.setPosition(a21);
				constraint.getContacts().add(
						new AGContactManifold(this, 
											  inObject,
											  _geom.findNormal(b1, a1),
											  a21,
											  a2.minus(a21).getLength()/2)
					);
			}
			if(checs[3]){
				_closestPoint3.setPosition(b21);
				if(!a21.equals(b21))
				{
					constraint.getContacts().add(
							new AGContactManifold(this, 
												  inObject,
												  _geom.findNormal(b1, a1),
												  b21,
												  b2.minus(b21).getLength()/2)
						);
				}
			}
		}
				_normal.setPosition(axis.minus());
			
		//}*/
	}
	protected boolean checkSimplex(Vector<AGVector2D> simplex) {
        // метож принимает только симплекс из двух или трех точек 
        // перем последнюю добавленную точку
        AGVector2D a = simplex.get(simplex.size() - 1);
        // это тоже самое что вектор a.to(начало координат);
        AGVector2D ao = a.minus();
        // проверяем тип симплекса
        if (simplex.size() == 3) {
            // имеем трекгольник
            AGVector2D b = simplex.get(1);
            AGVector2D c = simplex.get(0);
            // получаем стороны
            AGVector2D ab = a.to(b);
            AGVector2D ac = a.to(c);
            // получаем нормали
            AGVector2D abPerp = AGVector2D.tripleProduct(ac, ab, ab);
            AGVector2D acPerp = AGVector2D.tripleProduct(ab, ac, ac);
            // ghjdthztv ult yf[jlbncz yfxfkj rjjhlbyfn
            double acLocation = acPerp.multiply(ao);
            if (acLocation >= 0.0) {
                // the origin lies on the right side of A->C
                // because of the condition for the gjk loop to continue the origin 
                // must lie between A and C so remove B and set the
                // new search direction to A->C perpendicular vector
                simplex.remove(1);
                // this used to be direction.set(Vector.tripleProduct(ac, ao, ac));
                // but was changed since the origin may lie on the segment created
                // by a -> c in which case would produce a zero vector normal
                // calculating ac's normal using b is more robust
                direction.setPosition(acPerp);
            } else {
                double abLocation = abPerp.multiply(ao);
                // the origin lies on the left side of A->C
                if (abLocation < 0.0) {
                    // the origin lies on the right side of A->B and therefore in the
                    // triangle, we have an intersection
                    return true;
                } else {
                    // the origin lies between A and B so remove C and set the
                    // search direction to A->B perpendicular vector
                    simplex.remove(0);
                    // this used to be direction.set(Vector.tripleProduct(ab, ao, ab));
                    // but was changed since the origin may lie on the segment created
                    // by a -> b in which case would produce a zero vector normal
                    // calculating ab's normal using c is more robust
                    direction.setPosition(abPerp);
                }
            }
        } else {
            // get the b point
            AGVector2D b = simplex.get(0);
            AGVector2D ab = a.to(b);
            // otherwise we have 2 points (line segment)
            // because of the condition for the gjk loop to continue the origin 
            // must lie in between A and B, so keep both points in the simplex and
            // set the direction to the perp of the line segment towards the origin
            direction.setPosition(AGVector2D.tripleProduct(ab, ao, ab));
            // check for degenerate cases where the origin lies on the segment
            // created by a -> b which will yield a zero edge normal
            if (direction.isZero()) {
                // in this case just choose either normal (left or right)
                direction.setPosition(ab.left());
            }
        }
        return false;
    }
	public void applyForce(AGVector2D force, AGVector2D point){
		/*_linearAcceleration = _linearAcceleration.plus(force.multiply(_invMass));
		double torque = point.cross(force);
		double relativeTorque = _orientation * torque;
		double relativeAngularAcceleration = relativeTorque*_inertialMoment;
		_angularAcceleration += _orientation * relativeAngularAcceleration;
		Log.e("or","la.x="+_linearAcceleration.getX()+";la.y="+_linearAcceleration.getY());
		Log.e("or","f.x="+force.multiply(_invMass).getX()+";f.y="+force.multiply(_invMass).getY());*/
	}
	//physics going here
	public void calculateVelocity(double dt){
		/*_linearVelocity = _linearVelocity.plus(_linearAcceleration.multiply(dt));
		_angularVelocity += _angularAcceleration*dt;*/
	}
	public void calculatePosition(double dt){
		/*_position = _position.plus(_linearVelocity.multiply(dt));
		_orientation += _angularVelocity*dt;*/
	}
	public int getSize(){
		return _size;
	}
	public AGVector2D getPosition(){
		return _position;
	}
	public double getOrientation(){
		return _orientation;
	}
	public double getInertialMoment(){
		return _inertialMoment;
	}
	public void setInertialMoment(double i){
		_inertialMoment = i;
	}
	public void setPosition(AGVector2D position){
		_position = position;
	}
	public void setOrientation(double orientation){
		_orientation = orientation;
	}
	public void setLinearVelocity(AGVector2D v){
		_linearVelocity.setPosition(v);
	}
	public void setLinearAcceleration(AGVector2D v){
		_linearAcceleration.setPosition(v);
	}
	public void setAngularVelocity(double v){
		_angularVelocity = v;
	}
	public double getPoint(int num){
		return _vertexBuffer.get(num);
	}
	public AGVector2D getVertex(int num){
		return _vertices[num];
	}
	public void setInvMass(double invMass){
		_invMass=invMass;

		calculateInertia();
	}
	public double getInvMass(){
		return _invMass;
	}
	public AGVector2D getContactPoint(){
		return _closestPoint;
	}
	public AGVector2D getNormal(){
		return _normal;
	}
	public double getRestitution(){
		return _restitution;
	}
	public double getPD(){
		return _penetrationDepth;
	}
	public AGVector2D getLinearVelocity(){
		return _linearVelocity;
	}
	public AGVector2D getLinearAcceleration(){
		return _linearAcceleration;
	}
	public double getAngularAcceleration(){
		return _angularAcceleration;
	}
	public double getAngularVelocity(){
		return _angularVelocity;
	}
	public double getFriction(){
		return _friction;
	}
}
