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
						if(objects[i].findPD(objects[j]))
							objects[i].findContactManifold(objects[j]);
						//){
				//	calculateReactions(objects[i], objects[j]);
					//Log.e("cont","contacted");
				//}
		//objects[i].findRange(objects[j]);
			}
		}
		AGContactConstraint constraint = AGContactConstraint.Instance();
		//Log.e("12","manifolds="+constraint.getContacts().size());
		for(int i=0;i<constraint.getContacts().size();i++){
			calculateReactions(constraint.getContacts().get(i));
		}
		constraint.getContacts().clear();
		
	}
	public void calculateReactions(AGContactManifold contactManifold){

		AGVector2D contactPoint = new AGVector2D(contactManifold.getContactPoint());
		AGObject2D obj1 = contactManifold.getObj1();
		AGObject2D obj2 = contactManifold.getObj2();
		AGVector2D contactPoint1 = new AGVector2D(contactPoint.minus(obj1.getPosition()));
		AGVector2D contactPoint2 = new AGVector2D(contactPoint.minus(obj2.getPosition()));
		
		double elasticity = (obj1.getRestitution()+obj2.getRestitution())/2;
		AGVector2D relativeLinearVelocity = obj1.getLinearVelocity().minus(obj2.getLinearVelocity());
		
		AGVector2D n1 = contactManifold.getNormal();
		double w1 =  -n1.cross(contactPoint1);
		AGVector2D n2 = n1.minus();
		double w2 = n1.cross(contactPoint2);

		//Log.e("c","n1: "+n1+"; n2: "+n2);
		//Log.e("c","w1: "+w1+"; w2: "+w2);
		
		double a = n1.multiply(obj1.getLinearVelocity()) + n2.multiply(obj2.getLinearVelocity())
				+ w1 * obj1.getAngularVelocity()
				+ w2 * obj2.getAngularVelocity();
		//Log.e("c","n1*v1: "+n1.multiply(obj1.getLinearVelocity())+";n2*v2: "+n2.multiply(obj2.getLinearVelocity()));

		//Log.e("c","v1: "+obj1.getLinearVelocity()+";v2: "+obj2.getLinearVelocity());
	    double b = n1.multiply(n1) * obj1.getInvMass() 
	    		+ w1 * w1 * obj1.getInertialMoment() 
	    		+ n2.multiply(n2) * obj2.getInvMass()  
	    		+ w2 * w2 * obj2.getInertialMoment();
	    
	//	Log.e("c","a: "+a+"; b: "+b);
	    double velocityProjection = relativeLinearVelocity.multiply(n2);
	    double lambda = ((/*velocityProjection*1+*/contactManifold.getPD()-a) / b);
	  //double lambda = (10-a) / b;

	    //lambda *= -1;
	    if(lambda<0)lambda=0;
		//Log.e("c","Lambda: "+lambda);
	    
	    AGVector2D velocityAfterCollision =
	    	obj1.getLinearVelocity().plus(n1.multiply(lambda * obj1.getInvMass()));
	    
	    double angularVelocityAfterCollision = obj1.getAngularVelocity() 
	        + (n2.multiply(lambda).cross(contactPoint1)) * obj1.getInertialMoment();
	    
	    
	    AGVector2D velocityAfterCollision2 =
	    	obj2.getLinearVelocity().plus(n2.multiply(lambda * obj2.getInvMass()));
	    
	    double angularVelocityAfterCollision2 = obj2.getAngularVelocity()
	        + (n1.multiply(lambda).cross(contactPoint2)) * obj2.getInertialMoment();
	    
	    obj1.setLinearVelocity(velocityAfterCollision);
	    obj1.setAngularVelocity(angularVelocityAfterCollision);
	    
	    obj2.setLinearVelocity(velocityAfterCollision2);
	    obj2.setAngularVelocity(angularVelocityAfterCollision2);

		//Log.e("c","av1: "+angularVelocityAfterCollision+"; Lambda: "+lambda);
	   /* obj1._linearAcceleration.setPosition(0,0);
	    obj2._linearAcceleration.setPosition(0,0);*/
 //// obj1._angularAcceleration = 0;
 //// obj2._angularAcceleration = 0;
	    //Log.e("ang","n1:"+n1.getX()+"+"+n1.getY()+";n2:"+n2.getX()+"+"+n2.getY());
	}
}
