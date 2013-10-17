package cdr.forms;

public class FileDepositField implements DepositField<DepositFile> {

	DepositFile value;
	
	public DepositFile getValue() {
		return value;
	}
	
	public void setValue(DepositFile value) {
		this.value = value;
	}
	
}
