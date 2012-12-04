package com.fysx.game2d;

import java.util.Vector;

public class AGContactConstraint {
	public static AGContactConstraint Instance(){
		if(_instance==null){
			_instance = new AGContactConstraint();
		}
		return _instance;
	}
	protected AGContactConstraint(){
		_contacts = new Vector<AGContactManifold>();
	}
	private static AGContactConstraint _instance=null;
	private static Vector<AGContactManifold> _contacts;
	//getters
	public Vector<AGContactManifold> getContacts(){
		return _contacts;
	}
}
