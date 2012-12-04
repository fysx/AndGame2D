package com.fysx.game2d;

import android.R.string;

public class AGVector2D {
	private double _x, _y;
	public AGVector2D(){
		_x=0;
		_y=0;
	}
	public AGVector2D(double x, double y){
		_x=x;
		_y=y;
	}
	public AGVector2D(AGVector2D v){
		_x=v.getX();
		_y=v.getY();
	}
	public AGVector2D normalize(){
		if(Math.abs(_x)<0.0001 && Math.abs(_y)<0.0001)
			return new AGVector2D(0,0);
		double inv_length = 1.0f / (double)(Math.sqrt(_x*_x)+Math.sqrt(_y*_y));
        return this.multiply(inv_length);
	}
	public double multiply(AGVector2D multiplier){
		return multiplier.getX()*_x + multiplier.getY()*_y;
	}
	public AGVector2D divide(double delimeter){
		return new AGVector2D(delimeter*_x, delimeter*_y);
	}
	public AGVector2D multiply(double multiplier){
		return new AGVector2D(multiplier*_x, multiplier*_y);
	}
	public AGVector2D plus(AGVector2D add){
		return new AGVector2D(add.getX()+_x, add.getY()+_y);
	}
	public AGVector2D minus(){
		return new AGVector2D(-_x, -_y);
	}
	public AGVector2D minus(AGVector2D minus){
		return new AGVector2D(_x-minus.getX(),_y-minus.getY());
	}
	public AGVector2D rotate(double angle){
        double cos = (double)Math.cos(angle);
        double sin = (double)Math.sin(angle);
		return new AGVector2D(
				_x*cos-_y*sin,
				_x*sin+_y*cos
		);
	}
	public AGVector2D rotate(double angle, AGVector2D axis){
        AGVector2D tmp = new AGVector2D(this._x - axis.getX(), this._y - axis.getX()).rotate(angle);
        return new AGVector2D(tmp.getX()+axis.getX(),tmp.getY()+axis.getY()) ;
	}
	public double cross(double x, double y){
		return this ._x * y - this ._y * x;
	}
	public double cross(AGVector2D v){
		return this ._x * v.getY() - this ._y * v.getX();
		
	}
	public double getAngle(AGVector2D in){
		return this.multiply(in)/(this.getLength()*in.getLength());
	}
	public boolean equals(AGVector2D v){
		if(v.getX()==_x && v.getY()==_y)return true;
		else return false;
	}
	public boolean isSameDirection(AGVector2D in){
		return this.multiply(in) < 0;
	}
	public static AGVector2D tripleProduct(AGVector2D a, AGVector2D b, AGVector2D c) {
        // expanded version of above formula
		AGVector2D r = new AGVector2D();
        // perform a.dot(c)
		double ac = a.getX() * c.getX() + a.getY() * c.getY();
        // perform b.dot(c)
        double bc = b.getX() * c.getX() + b.getY() * c.getY();
        // perform b * a.dot(c) - a * b.dot(c)
        r.setPosition((b.getX() * ac - a.getX() * bc), (b.getY() * ac - a.getY() * bc));
        return r;
    }
    public boolean isZero() {
        return (this ._x * this ._x + this ._y * this ._y) < 10e-10f;
    }
    public AGVector2D left() {
        double temp = this ._x;
        this ._x = -this ._y;
        this ._y = temp;
        return this;
    }
	/*
	 * getters and setters
	 */
	public AGVector2D getNormal(){
		return new AGVector2D(-_y,_x);
	}
	public double getLength(){
		return (double)Math.sqrt((double)(_x*_x+_y*_y));
	}
	public double getX(){
		return _x;
	}
	public double getY(){
		return _y;
	}
	public void setPosition(double x, double y){
		_x=x;_y=y;
	}
	public void setPosition(AGVector2D vector){
		_x=vector.getX();_y=vector.getY();
	}
	public void put(AGVector2D v){
		_x=v.getX();_y=v.getY();
	}
    public AGVector2D to(AGVector2D vector) {
        return new AGVector2D(vector.getX() - this._x, vector.getY() - this._y);
    }
    public String toString(){
    	return "x: "+this._x+"; y:"+this._y;
    }
}
