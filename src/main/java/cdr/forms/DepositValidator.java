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

import java.util.Map.Entry;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import crosswalk.EmailInputField;
import crosswalk.FileBlock;
import crosswalk.Form;
import crosswalk.FormElement;
import crosswalk.InputField;
import crosswalk.MajorBlock;
import crosswalk.MetadataBlock;

public class DepositValidator implements Validator {
	private static final Logger LOG = LoggerFactory.getLogger(DepositValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		 return Deposit.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		Deposit deposit = (Deposit) target;
		Form form = deposit.getForm();
		
		// Validate receipt email address
		
		if (deposit.getReceiptEmailAddress() != null && deposit.getReceiptEmailAddress().trim().length() > 0) {
			try {
				InternetAddress address = new InternetAddress(deposit.getReceiptEmailAddress());
				address.validate();
			} catch (AddressException e) {
				errors.rejectValue("receiptEmailAddress", "invalidEmailAddress", "You must enter a valid email address.");
			}
		}
		
		// The main file is required if there are no FileBlock elements
		
		if (!form.isHasFileBlocks()) {
			if (deposit.getMainFile() == null)
				errors.rejectValue("mainFile", "file.required", "This file is required.");
		}
		
		// Validate the form
		
		int elementIndex = 0;
		
		for (DepositElement element : deposit.getElements()) {
			
			if (element.getFormElement() instanceof MetadataBlock) {

				int entryIndex = 0;
				
				MetadataBlock metadataBlock = (MetadataBlock) element.getFormElement();

				for (DepositEntry entry : element.getEntries()) {
					
					int portIndex = 0;
					
					for (InputField<?> inputField : metadataBlock.getPorts()) {
					
						if (inputField.isRequired()) {
							String path = "elements[" + elementIndex + "].entries[" + entryIndex + "].fields[" + portIndex + "].value";
							ValidationUtils.rejectIfEmptyOrWhitespace(errors, path, "field.required", "This field is required.");
						}
						
						if (inputField instanceof EmailInputField) {
							String path = "elements[" + elementIndex + "].entries[" + entryIndex + "].fields[" + portIndex + "].value";

							try {
								InternetAddress address = new InternetAddress((String) errors.getFieldValue(path));
								address.validate();
							} catch (AddressException e) {
								errors.rejectValue(path, "invalidEmailAddress", "You must enter a valid email address.");
							}
						}
						
						portIndex++;
						
					}
					
					entryIndex++;
					
				}
				
			}
			
			if (element.getFormElement() instanceof FileBlock) {

				int entryIndex = 0;
				
				FileBlock fileBlock = (FileBlock) element.getFormElement();

				for (DepositEntry entry : element.getEntries()) {
					
					if (fileBlock.isRequired() && entry.getFile() == null)
						errors.rejectValue("elements[" + elementIndex + "].entries[" + entryIndex + "].file", "file.required", "This file is required.");
					
					entryIndex++;
				
				}
				
			}
			
			if (element.getFormElement() instanceof MajorBlock) {
				
				MajorBlock majorBlock = (MajorBlock) element.getFormElement();

				if (majorBlock.getSelectedMajor() == null) {
					errors.rejectValue("elements[" + elementIndex + "]", "major.required", "You must select a major.");
				}
				
			}

			elementIndex++;
			
		}
		
	}

}
