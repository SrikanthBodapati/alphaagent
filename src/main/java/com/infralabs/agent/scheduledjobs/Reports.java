package com.infralabs.agent.scheduledjobs;

import java.util.ArrayList;
import java.util.List;

public enum Reports {
	INSTANCE;
	List<String> values=new ArrayList<String>();
	public List<String> getInstance() {
		return values;
	}
	
	public void setInstance(List<String> newvalue) {
		 values= newvalue;
	}
}
