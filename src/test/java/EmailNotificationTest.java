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
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cdr.forms.CachedXMIFormFactory;
import cdr.forms.DateDepositField;
import cdr.forms.Deposit;
import cdr.forms.DepositElement;
import cdr.forms.DepositEntry;
import cdr.forms.DepositField;
import cdr.forms.DepositResult;
import cdr.forms.Submission;
import cdr.forms.DepositResult.Status;
import cdr.forms.EmailDepositField;
import cdr.forms.EmailNotificationHandler;
import cdr.forms.TextDepositField;
import crosswalk.DateInputField;
import crosswalk.Form;
import crosswalk.FormElement;
import crosswalk.InputField;
import crosswalk.MetadataBlock;
import crosswalk.TextInputField;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/email-test-service-context.xml" })
public class EmailNotificationTest {
	
	@Resource
	JavaMailSender javaMailSender = null;
	
	@Resource
	EmailNotificationHandler emailNotificationHandler = null;
	
	@Resource
	CachedXMIFormFactory formFactory = null;
	
	public void setMailMock() {
		// setup mail sender mock invocations
		reset(this.javaMailSender);
		when(this.javaMailSender.createMimeMessage()).thenCallRealMethod();

		Answer dumpMessage = new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object arg = invocation.getArguments()[0];
				if (arg instanceof MimeMessage) {
					MimeMessage m = (MimeMessage) arg;
					System.out.println("EMAIL DUMP:");
					m.writeTo(System.out);
				} else if (arg instanceof SimpleMailMessage) {
					SimpleMailMessage m = (SimpleMailMessage) arg;
					System.out.println("EMAIL DUMP:");
					System.out.println(m.toString());
				} else {
					throw new Error("Could not print email: " + arg);
				}
				return null;
			}
		};
		doAnswer(dumpMessage).when(this.javaMailSender).send(any(MimeMessage.class));
		doAnswer(dumpMessage).when(this.javaMailSender).send(any(SimpleMailMessage.class));
	}
	
	@Test
	public void testDepositNotification() {
		setMailMock();
		
		Form form = this.formFactory.getForm("test");
		Deposit deposit = buildDeposit(form);
		Submission submission = Submission.create(deposit);
		
		DepositResult result = new DepositResult();
		result.setAccessURL("http://example.org/the/deposit/url");
		result.setStatus(Status.PENDING);
		
		emailNotificationHandler.notifyDeposit(deposit, result);
		verify(this.javaMailSender, times(2)).send(any(MimeMessage.class));
	}
	
	@Test
	public void testDepositError() {
		setMailMock();
		
		Form form = this.formFactory.getForm("test");
		Deposit deposit = buildDeposit(form);
		Submission submission = Submission.create(deposit);
		
		DepositResult result = new DepositResult();
		result.setAccessURL("http://example.org/the/deposit/url");
		result.setStatus(Status.FAILED);
		Throwable exception = new Exception("example error trace").fillInStackTrace();
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		result.setResponseBody(sw.toString());
		
		emailNotificationHandler.notifyError(deposit, result);
		verify(this.javaMailSender, times(1)).send(any(MimeMessage.class));
	}
	
	private Deposit buildDeposit(Form form) {
		
		form.setCurrentUser("testuser");

		Deposit deposit = new Deposit();
		
		deposit.setForm(form);
		deposit.setFormId("test");
		deposit.setElements(new ArrayList<DepositElement>());
		deposit.setReceiptEmailAddress("receipt@email.address");
		
		for (FormElement element : form.getElements()) {
			
			DepositElement depositElement = new DepositElement();
			depositElement.setFormElement(element);
			depositElement.setEntries(new ArrayList<DepositEntry>());
			depositElement.appendEntry();
			
			DepositEntry entry = depositElement.getEntries().get(0);
			
			if (entry.getFields() != null) {
			
				for (DepositField<?> field : entry.getFields()) {
					
					if (field instanceof DateDepositField) {
						((DateDepositField) field).setValue(new Date());
					} else if (field instanceof TextDepositField) {
						((TextDepositField) field).setValue("Test");
					} else if (field instanceof EmailDepositField) {
						((EmailDepositField) field).setValue("email@deposit.field");
					}
					
				}
				
			}
			
			deposit.getElements().add(depositElement);
			
		}
		
		return deposit;
		
	}

}
