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
		
		ArrayList<DepositField<?>> fields = new ArrayList<DepositField<?>>();
		
		if (formElement instanceof MetadataBlock) {
			
			for (InputField<?> field : ((MetadataBlock) formElement).getPorts()) {
				if (field instanceof DateInputField) {
					DepositField<Date> depositField = new DepositField<Date>();
					depositField.value = new Date();
					fields.add(depositField);
				} else if (field instanceof TextInputField) {
					DepositField<String> depositField = new DepositField<String>();
					depositField.value = new String();
					fields.add(depositField);
				} else {
					throw new Error("Unknown input field type");
				}
			}
			
		} else if (formElement instanceof FileBlock) {
			
			fields.add(new DepositField<DepositFile>());
			
		}
		
		depositEntry.setFields(fields);

		entries.add(depositEntry);
		
	}
	
}
