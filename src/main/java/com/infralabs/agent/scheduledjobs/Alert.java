package com.infralabs.agent.scheduledjobs;

public class Alert {
	public String oldvalue="";
	public String newvalue;
	public Alert(String old,String newval) {
		if(old!=null)
			oldvalue=old;
		newvalue= newval;
	}
	@Override
	public String toString() {
		return "{old:"+oldvalue+",new:"+newvalue+"}";
	}
}
