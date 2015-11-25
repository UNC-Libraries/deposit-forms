/**
 * Copyright 2010 The University of North Carolina at Chapel Hill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cdr.forms;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crosswalk.EmailInputField;
import crosswalk.Form;
import crosswalk.FormElement;
import crosswalk.MajorBlock;
import crosswalk.MajorEntry;

public class Deposit {
	
	private Form form;
	private String formId;
	private String receiptEmailAddress;
	private DepositFile mainFile;
	private DepositFile[] supplementalFiles;
	private List<DepositElement> elements;
	private List<SupplementalObject> supplementalObjects;
	private Date agreementDate;
	private DepositFile agreementFile;
	private static final  Logger LOG = LoggerFactory
			.getLogger(Deposit.class);

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}
	
	public String getFormId() {
		return formId;
	}
	
	public void setFormId(String formId) {
		this.formId = formId;
	}
	
	public String getReceiptEmailAddress() {
		return receiptEmailAddress;
	}

	public void setReceiptEmailAddress(String receiptEmailAddress) {
		this.receiptEmailAddress = receiptEmailAddress;
	}
	
	public Date getAgreementDate() {
		return agreementDate;
	}
	
	public void setAgreementDate(Date agreementDate) {
		this.agreementDate = agreementDate;
	}
	
	public Boolean getAgreement() {
		return agreementDate != null;
	}

	public void setAgreement(Boolean agreement) {
		if (agreement) {
			this.agreementDate = new Date();
		} else {
			this.agreementDate = null;
		}
	}
	
	public void setAgreementFile(DepositFile agreementFile) {
		this.agreementFile = agreementFile;
	}
	
	public DepositFile getAgreementFile() { 
		if (agreementFile == null && getAgreement() && form.getAgreement() != null) {
			try {
				File agreementFileText = File.createTempFile("agreement", null);
				String agreementText = form.getAgreement();
					
				SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
				String agreementDate = DATE_FORMAT.format(this.getAgreementDate());
				
				PrintWriter fileText = new PrintWriter(agreementFileText);
				fileText.println(agreementText);
				fileText.println(agreementDate);
				fileText.println(form.getCurrentUser());
				fileText.close();		
					
				agreementFile = new DepositFile();
				agreementFile.setFile(agreementFileText);
				agreementFile.setContentType("text/plain");
				agreementFile.setFilename("agreement.txt");
				agreementFile.setSize(agreementFileText.length());
				agreementFile.setExternal(false); 
			} catch (IOException e) {
				LOG.error("Couldn't add agreement file", e);
				agreementFile = null;
			} 
		}
			
		return agreementFile;
	}
	
	public List<String> getAllDepositNoticeToEmailAddresses() {
		
		ArrayList<String> addresses = new ArrayList<String>();
		
		if (this.getForm().getEmailDepositNoticeTo() != null)
			addresses.addAll(this.getForm().getEmailDepositNoticeTo());
		
		for (FormElement element : this.getForm().getElements()) {
			if (element instanceof MajorBlock) {
				MajorEntry major = ((MajorBlock) element).getSelectedMajor();
				
				if (major != null && major.getEmailDepositNoticeTo() != null) {
					addresses.addAll(major.getEmailDepositNoticeTo());
				}
			}
		}
		
		for (DepositElement element : this.getElements()) {
			for (DepositEntry entry : element.getEntries()) {
				if (entry.getFields() != null) {
					for (DepositField<?> field : entry.getFields()) {
						if (field instanceof EmailDepositField && ((EmailInputField) field.getFormInputField()).isProvidesEmailDepositNoticeTo()) {
							addresses.add((String) field.getValue());
						}
					}
				}
			}
		}
		
		return addresses;
		
	}
	
	public DepositFile getMainFile() {
		return mainFile;
	}

	public void setMainFile(DepositFile mainFile) {
		this.mainFile = mainFile;
	}

	public DepositFile[] getSupplementalFiles() {
		return supplementalFiles;
	}

	public void setSupplementalFiles(DepositFile[] supplementalFiles) {
		this.supplementalFiles = supplementalFiles;
	}
	
	public void deleteAllFiles() {
		deleteAllFiles(false);
	}
	
	public void deleteAllFiles(boolean deleteExternal) {
		for (DepositFile depositFile : this.getAllFiles()) {
			if (depositFile.getFile() != null) {
				if (deleteExternal || !depositFile.isExternal())
					depositFile.getFile().delete();
			}
		}
	}
	
	public List<DepositFile> getAllFiles() {
		List<DepositFile> files = new ArrayList<DepositFile>();
		
		if (this.getElements() != null) {
			for (DepositElement element : this.getElements()) {
				for (DepositEntry entry : element.getEntries()) {
					if (entry.getFile() != null)
						files.add(entry.getFile());
				}
			}
		}
		
		if (this.getMainFile() != null)
			files.add(this.getMainFile());		
		
		if (this.getAgreementFile() != null) {
			files.add(this.getAgreementFile());
		}
		
		if (this.getSupplementalFiles() != null) {
			for (DepositFile depositFile : this.getSupplementalFiles()) {
				if (depositFile != null)
					files.add(depositFile);
			}
		}
		
		if (this.getSupplementalObjects() != null) {
			for (SupplementalObject object : this.getSupplementalObjects()) {
				if (object != null && object.getDepositFile() != null)
					files.add(object.getDepositFile());
			}
		}
		
		return files;
	}

	public List<DepositElement> getElements() {
		return elements;
	}

	public void setElements(List<DepositElement> elements) {
		this.elements = elements;
	}
	
	public List<SupplementalObject> getSupplementalObjects() {
		return supplementalObjects;
	}

	public void setSupplementalObjects(List<SupplementalObject> supplementalObjects) {
		this.supplementalObjects = supplementalObjects;
	}

}
