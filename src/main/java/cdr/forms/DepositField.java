package cdr.forms;

import crosswalk.InputField;

public interface DepositField<F> {

	public F getValue();
	public void setValue(F value);
	
	public InputField<?> getFormInputField();
	public void setFormInputField(InputField<?> formInputField);
	
}
