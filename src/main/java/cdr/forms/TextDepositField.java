package cdr.forms;

public class TextDepositField implements DepositField<String> {

	private String value;
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
}
