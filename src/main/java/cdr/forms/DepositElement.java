package cdr.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import crosswalk.DateInputField;
import crosswalk.FileBlock;
import crosswalk.FormElement;
import crosswalk.InputField;
import crosswalk.MetadataBlock;
import crosswalk.TextInputField;

public class DepositElement {

	private String append;
	private FormElement formElement;
	private List<DepositEntry> entries;
	
	public FormElement getFormElement() {
		return formElement;
	}
	
	public void setFormElement(FormElement formElement) {
		this.formElement = formElement;
	}
	
	public List<DepositEntry> getEntries() {
		return entries;
	}
	
	public void setEntries(List<DepositEntry> entries) {
		this.entries = entries;
	}
	
	public String getAppend() {
		return append;
	}
	
	public void setAppend(String append) {
		this.append = append;
	}
	
	public void appendEntry() {
		
		DepositEntry depositEntry = new DepositEntry();
		
		List<DepositField<?>> fields = new ArrayList<DepositField<?>>();
		
		if (formElement instanceof MetadataBlock) {
			
			for (InputField<?> field : ((MetadataBlock) formElement).getPorts()) {
				if (field instanceof DateInputField) {
					DateDepositField depositField = new DateDepositField();
					depositField.setValue(new Date());
					fields.add(depositField);
				} else if (field instanceof TextInputField) {
					TextDepositField depositField = new TextDepositField();
					depositField.setValue(new String());
					fields.add(depositField);
				} else {
					throw new Error("Unknown input field type");
				}
			}
			
		} else if (formElement instanceof FileBlock) {
			
			fields.add(new FileDepositField());
			
		}
		
		depositEntry.setFields(fields);

		entries.add(depositEntry);
		
	}
	
}
