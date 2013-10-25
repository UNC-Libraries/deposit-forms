package cdr.forms;

import java.util.Date;

import crosswalk.InputField;

public class DateDepositField implements DepositField<Date> {

	Date value;
	InputField<?> formInputField;
	
	public Date getValue() {
		return value;
	}
	
	public void setValue(Date value) {
		this.value = value;
	}

	public InputField<?> getFormInputField() {
		return formInputField;
	}
	
	public void setFormInputField(InputField<?> formInputField) {
		this.formInputField = formInputField;
	}
	
}
