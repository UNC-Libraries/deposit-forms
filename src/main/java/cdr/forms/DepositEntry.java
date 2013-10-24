package cdr.forms;

import java.util.List;

import crosswalk.FormElement;

public class DepositEntry {

	private List<DepositField<?>> fields;
	private DepositFile file;
	private FormElement formElement;

	public List<DepositField<?>> getFields() {
		return fields;
	}

	public void setFields(List<DepositField<?>> fields) {
		this.fields = fields;
	}

	public DepositFile getFile() {
		return file;
	}

	public void setFile(DepositFile file) {
		this.file = file;
	}

	public FormElement getFormElement() {
		return formElement;
	}

	public void setFormElement(FormElement formElement) {
		this.formElement = formElement;
	}

}
