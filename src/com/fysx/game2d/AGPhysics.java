package com.fysx.game2d;

import java.util.Vector;

import android.util.Log;

public class AGPhysics {

	public void calculateColisions(AGObject2D[] objects){
		int size = objects.length;
		for(int i=0;i<size;i++)
		{
			for(int j=i+1;j<size;j++)
			{
				//if(
						objects[i].findRange(objects[j]);
						//){
				//	calculateReactions(objects[i], objects[j]);
					//Log.e("cont","contacted");
				//}
			}
		}
		AGContactConstraint constraint = AGContactConstraint.Instance();
		Log.e("12","manifolds="+constraint.getContacts().size());
		for(int i=0;i<constraint.getContacts().size();i++){
			calculateReactions(constraint.getContacts().get(i));
			Log.e("c"+i,"c"+i+": d="+constraint.getContacts().get(i).getPD());
		}
		constraint.getContacts().clear();
		
	}
	public void calculateReactions2(AGObject2D obj1, AGObject2D obj2){
		AGVector2D contactPoint = new AGVector2D(obj1.getContactPoint());
		//Log.e("c1","c1.x"+obj1.getContactPoint().getX()+";c1.y"+obj1.getContactPoint().getY());
		//Log.e("c2","c2.x"+obj2.getContactPoint().getX()+";c2.y"+obj2.getContactPoint().getY());
		AGVector2D contactPoint1 = new AGVector2D(contactPoint.minus(obj1.getPosition()));
		AGVector2D contactPoint2 = new AGVector2D(contactPoint.minus(obj2.getPosition()));
		
		double elasticity = (obj1.getRestitution()+obj2.getRestitution())/2;
		double relativeVelocity = obj1.getAngularVelocity()-obj2.getAngularVelocity();
		AGVector2D relativeLinearVelocity = obj1.getLinearVelocity().minus(obj2.getLinearVelocity());
		/*double numerator = -1*relativeVelocity*(elasticity+1);
		
		AGVector2D unitNormal = obj1.getPosition().minus(obj2.getPosition()).getNormal();
		
		double tmp = contactPoint.cross(unitNormal);*/
		AGVector2D n1 = obj1.getNormal().minus();
		double w1 =  n1.cross(contactPoint1);
		
		AGVector2D n2 = obj1.getNormal();
		double w2 = n2.cross(contactPoint2);
		
	    AGVector2D norm = obj1.getNormal();

		
		double a = n1.multiply(obj1.getLinearVelocity()) + n2.multiply(obj2.getLinearVelocity())
				+ w1 * obj1.getAngularVelocity()
				+ w2 * obj2.getAngularVelocity();

	    double b = n1.multiply(n1) * obj1.getInvMass() 
	    		+ w1 * w1 * obj1.getInertialMoment() 
	    		+ n2.multiply(n2) * obj2.getInvMass()  
	    		+ w2 * w2 * obj2.getInertialMoment();
	    
	    double lambda = (obj1.getPD()-a) / b;
	    //double lambda = (0.1f-a) / b;
	    
	    double velocityProjection = n1.multiply(obj1.getLinearVelocity()) + n2.multiply(obj2.getLinearVelocity())
        		+ w1 * obj1.getAngularVelocity() + w2 * obj2.getAngularVelocity()
        		+ (n1.multiply(n1) * obj1.getInvMass()  + w1 * w1 * obj1.getInertialMoment() 
        	    + n2.multiply(n2) * obj2.getInvMass()   + w2 * w2 * obj2.getInertialMoment()) * lambda;
	    
	    if(lambda<0)lambda=0;
	    
	    AGVector2D velocityAfterCollision =
	    	obj1.getLinearVelocity().plus(n1.multiply(lambda * obj1.getInvMass()));
	    
	    double angularVelocityAfterCollision = obj1.getAngularVelocity() 
	        + (n1.multiply(lambda).cross(contactPoint1)) * obj1.getInertialMoment();
	    
	    obj1.setLinearVelocity(velocityAfterCollision);
	    obj1.setAngularVelocity(-angularVelocityAfterCollision);
	    
	    AGVector2D velocityAfterCollision2 =
	    	obj2.getLinearVelocity().plus(n2.multiply(lambda * obj2.getInvMass()));
	    double angularVelocityAfterCollision2 = obj2.getAngularVelocity()
	        + (n2.multiply(lambda).cross(contactPoint2)) * obj2.getInertialMoment();
	    
	    obj2.setLinearVelocity(velocityAfterCollision2);
	    obj2.setAngularVelocity(-angularVelocityAfterCollision2);
	    //Log.e("ang","n1:"+n1.getX()+"+"+n1.getY()+";n2:"+n2.getX()+"+"+n2.getY());
	}
	public void calculateReactions1(AGContactManifold contactManifold){
		AGVector2D contactPoint = new AGVector2D(contactManifold.getContactPoint());
		//Log.e("c1","c1.x"+obj1.getContactPoint().getX()+";c1.y"+obj1.getContactPoint().getY());
		//Log.e("c2","c2.x"+obj2.getContactPoint().getX()+";c2.y"+obj2.getContactPoint().getY());
		AGObject2D obj1 = contactManifold.getObj1();
		AGObject2D obj2 = contactManifold.getObj2();
		AGVector2D contactPoint1 = new AGVector2D(contactPoint.minus(obj1.getPosition()));
		AGVector2D contactPoint2 = new AGVector2D(contactPoint.minus(obj2.getPosition()));
		
		double elasticity = (obj1.getRestitution()+obj2.getRestitution())/2;
		AGVector2D relativeLinearVelocity = obj1.getLinearVelocity().minus(obj2.getLinearVelocity());
		AGVector2D n1 = contactManifold.getNormal().normalize();
		double w1 =  n1.cross(contactPoint1);
		
		AGVector2D n2 = contactManifold.getNormal().minus().normalize();
		double w2 = -n1.cross(contactPoint2);

		
		double a = n1.multiply(obj1.getLinearVelocity()) + n2.multiply(obj2.getLinearVelocity())
				+ w1 * obj1.getAngularVelocity()
				+ w2 * obj2.getAngularVelocity();

	    double b = n1.multiply(n1) * obj1.getInvMass() 
	    		+ w1 * w1 * obj1.getInertialMoment() 
	    		+ n2.multiply(n2) * obj2.getInvMass()  
	    		+ w2 * w2 * obj2.getInertialMoment();
	    
	    double velocityProjection = relativeLinearVelocity.multiply(n1);
	    double lambda = (-velocityProjection*elasticity+contactManifold.getPD()-a) / b;
	  //double lambda = (10-a) / b;
	    
	    
	    if(lambda<0)lambda=0;
	    
	    AGVector2D velocityAfterCollision =
	    	obj1.getLinearVelocity().plus(n1.multiply(lambda * obj1.getInvMass()));
	    
	    double angularVelocityAfterCollision = obj1.getAngularVelocity() 
	        + (n1.multiply(lambda).cross(contactPoint1)) * obj1.getInertialMoment();
	    
	    
	    AGVector2D velocityAfterCollision2 =
	    	obj2.getLinearVelocity().plus(n1.multiply(lambda * obj2.getInvMass()));
	    double angularVelocityAfterCollision2 = obj2.getAngularVelocity()
	        + (n1.multiply(lambda).cross(contactPoint2)) * obj2.getInertialMoment();
	    
	    obj2.setLinearVelocity(velocityAfterCollision2);
	    obj2.setAngularVelocity(angularVelocityAfterCollision2);
	    
	    obj1.setLinearVelocity(velocityAfterCollision);
	    obj1.setAngularVelocity(-angularVelocityAfterCollision);
	   /* obj1._linearAcceleration.setPosition(0,0);
	    obj2._linearAcceleration.setPosition(0,0);*/
 //// obj1._angularAcceleration = 0;
 //// obj2._angularAcceleration = 0;
	    //Log.e("ang","n1:"+n1.getX()+"+"+n1.getY()+";n2:"+n2.getX()+"+"+n2.getY());
	}
	public void calculateReactions/*solveContact*/(AGContactManifold contact){
		AGObject2D obj1 = contact.getObj1();
		AGObject2D obj2 = contact.getObj2();
		AGVector2D v1, v2, vr, t, j;
		double vrn, jn, jnOld, bounce, e, u, mass_sum, r1cn, r2cn,
		vrt, kn, nMass, jtMax, jt, jtOld, r1ct, r2ct, kt, tMass, jnAcc, jtAcc;
		e = obj1.getFriction()*obj2.getFriction();//общий коэффициент трения
		u = obj1.getRestitution()*obj2.getRestitution();//общий коэф. упругости
		jtAcc = 0;
		jnAcc = 0;
		v1 = obj1.getLinearAcceleration()
					.plus(
							contact.getObjVec1()
									.left()
									.multiply(obj1.getAngularAcceleration())
						);
		v2 = obj2.getLinearAcceleration()
					.plus(
							contact.getObjVec1()
									.left()
									.multiply(obj2.getAngularAcceleration())
						);
		vr = v2.minus(v1);
		vrn = vr.multiply(contact.getNormal());
		bounce = contact.getNormal().multiply(vr)*e;
		mass_sum = obj1.getInvMass()+obj2.getInvMass();
		r1cn = contact.getObjVec1().cross(contact.getNormal());
		r2cn = contact.getObjVec2().cross(contact.getNormal());
		kn = mass_sum + r1cn*r1cn*obj1.getInertialMoment()+r2cn*r2cn*obj2.getInertialMoment();
		nMass = 1/kn;
		jn = -(bounce + vrn)*nMass;
		jnOld = jnAcc;
		jnAcc = Math.max(jnOld+jn, 0);
		jn = jnAcc-jnOld;
		t = contact.getNormal().left();
		vrt = vr.multiply(t);
		r1ct = contact.getObjVec1().cross(t);
		r2ct = contact.getObjVec2().cross(t);
		kt = mass_sum + r1ct*r1ct*obj1.getInertialMoment()+r2ct*r2ct*obj2.getInertialMoment();
		tMass = 1/kt;
		jtMax= u*jnAcc;
		jt = -vrt*tMass;
		jtOld = jtAcc;
		jtAcc = Math.min(Math.max(jtOld+jt, -jtMax), jtMax);
		jt = jtAcc - jtOld;
		j = contact.getNormal().multiply(jn).plus(t.multiply(jt));
		
		obj1.applyImpulse(j.minus(), contact.getObjVec1());
		obj2.applyImpulse(j, contact.getObjVec2());
		
	}
}
