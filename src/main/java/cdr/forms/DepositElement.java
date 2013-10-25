package cdr.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import crosswalk.DateInputField;
import crosswalk.EmailInputField;
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
	
	/**
	 * Appends a new entry to the entries array, based on this element's
	 * FormElement.
	 */
	
	public void appendEntry() {
		
		// If our form element is a metadata block and we have added as many entries
		// as the maxRepeat attribute, silently refuse to add more entries.
		
		if (formElement instanceof MetadataBlock) {
			if (entries.size() >= ((MetadataBlock) formElement).getMaxRepeat()) {
				return;
			}
		}
		
		// Create a new instance of DepositEntry. If this element's FormElement
		// is an instance of MetadataBlock, a list of DepositField is created, one
		// field for every port. Otherwise, the entry's list of fields is left unset.
		
		DepositEntry depositEntry = new DepositEntry();
		
		depositEntry.setFormElement(formElement);
		
		if (formElement instanceof MetadataBlock) {
			
			List<DepositField<?>> fields = new ArrayList<DepositField<?>>();
			
			for (InputField<?> field : ((MetadataBlock) formElement).getPorts()) {
				if (field instanceof DateInputField) {
					DateDepositField depositField = new DateDepositField();
					depositField.setValue(new Date());
					depositField.setFormInputField(field);
					fields.add(depositField);
				} else if (field instanceof TextInputField) {
					TextDepositField depositField = new TextDepositField();
					depositField.setValue(new String());
					depositField.setFormInputField(field);
					fields.add(depositField);
				} else if (field instanceof EmailInputField) {
					EmailDepositField depositField = new EmailDepositField();
					depositField.setValue(new String());
					depositField.setFormInputField(field);
					fields.add(depositField);
				} else {
					throw new Error("Unknown input field type");
				}
			}
			
			depositEntry.setFields(fields);
			
		}

		entries.add(depositEntry);
		
	}
	
}
