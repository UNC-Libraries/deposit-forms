package cdr.forms;

import java.util.Date;

public class DateDepositField implements DepositField<Date> {

	Date value;
	
	public Date getValue() {
		return value;
	}
	
	public void setValue(Date value) {
		this.value = value;
	}
	
}
