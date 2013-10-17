package cdr.forms;

import java.util.List;

public class DepositEntry {

	private List<DepositField<?>> fields;
	
	public List<DepositField<?>> getFields() {
		return fields;
	}
	
	public void setFields(List<DepositField<?>> fields) {
		this.fields = fields;
	}
	
}
