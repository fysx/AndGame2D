package com.fysx.game2d;

public class AGContactManifold {
	private AGVector2D _contactPoint;//���������� ����� �����������
	private double _penetrationDepth;//����� ������� �������������
	private AGVector2D _normal;//������� ���� 1 ������������ ���� 2
	private AGObject2D _obj1;//������ �� ���� 1 � ����� ��������
	private AGObject2D _obj2;//������ �� ���� 2 � ����� ��������
	private AGVector2D _objVec1;//������ �� ���� 1 � ����� ��������
	private AGVector2D _objVec2;//������ �� ���� 2 � ����� ��������
	//construct
	public AGContactManifold(AGObject2D obj1, AGObject2D obj2, AGVector2D normal, AGVector2D contactPoint, double pd/*, AGVector2D objVec1, AGVector2D objVec2*/){
		_obj1 = obj1;
		_obj2 = obj2;
		/*_objVec1 = obj1.getPosition().minus(contactPoint);
		_objVec2 = obj2.getPosition().minus(contactPoint);*/
		_objVec1 = contactPoint.minus(obj1.getPosition());
		_objVec2 = contactPoint.minus(obj2.getPosition());
		_normal= normal;
		_contactPoint = contactPoint;
		_penetrationDepth = pd;
	}
	//getters and setters
	public AGVector2D getContactPoint(){
		return _contactPoint;
	}
	public double getPD(){
		return _penetrationDepth;
	}
	public AGVector2D getNormal(){
		return _normal;
	}
	public AGObject2D getObj1(){
		return _obj1;
	}
	public AGObject2D getObj2(){
		return _obj2;
	}
	public AGVector2D getObjVec1(){
		return _objVec1;
	}
	public AGVector2D getObjVec2(){
		return _objVec2;
	}
}
