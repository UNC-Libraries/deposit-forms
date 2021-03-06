package cdr.forms;

import edu.unc.lib.schemas.acl.AccessControlType;
import edu.unc.lib.schemas.acl.AclFactory;
import edu.unc.lib.schemas.acl.GrantType;
import gov.loc.mets.AgentType;
import gov.loc.mets.AmdSecType;
import gov.loc.mets.CHECKSUMTYPEType;
import gov.loc.mets.DivType;
import gov.loc.mets.FLocatType;
import gov.loc.mets.FileGrpType1;
import gov.loc.mets.FileSecType;
import gov.loc.mets.FileType;
import gov.loc.mets.FptrType;
import gov.loc.mets.LOCTYPEType;
import gov.loc.mets.MDTYPEType;
import gov.loc.mets.MdSecType;
import gov.loc.mets.MdWrapType;
import gov.loc.mets.MetsFactory;
import gov.loc.mets.MetsHdrType;
import gov.loc.mets.MetsType;
import gov.loc.mets.ROLEType;
import gov.loc.mets.SmLinkType;
import gov.loc.mets.StructLinkType1;
import gov.loc.mets.StructMapType;
import gov.loc.mets.TYPEType;
import gov.loc.mets.XmlDataType1;
import gov.loc.mets.util.Link;
import gov.loc.mets.util.METSConstants;
import gov.loc.mods.mods.MODSFactory;
import gov.loc.mods.mods.NameDefinition;
import gov.loc.mods.mods.XsString;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xml.type.internal.XMLCalendar;
import org.springframework.util.StringUtils;

import crosswalk.DateInputField;
import crosswalk.EmailInputField;
import crosswalk.FileBlock;
import crosswalk.Form;
import crosswalk.FormElement;
import crosswalk.InputField;
import crosswalk.MajorBlock;
import crosswalk.MajorEntry;
import crosswalk.MetadataBlock;
import crosswalk.OutputElement;
import crosswalk.OutputMetadataSections;
import crosswalk.OutputProfile;
import crosswalk.TextInputField;

public class Submission {
	
	IdentityHashMap<DepositFile, String> files;
	gov.loc.mets.DocumentRoot metsDocumentRoot;
	
	public static Submission create(Deposit deposit, ExternalDepositFileConfigurationProvider externalDepositFileConfigurationProvider) {
		
		Submission submission = new Submission();

		moveExternalFiles(deposit.getAllFiles(), externalDepositFileConfigurationProvider);
		
		submission.files = buildFilenameMap(deposit.getAllFiles(), externalDepositFileConfigurationProvider);
		submission.metsDocumentRoot = buildMets(deposit, submission.files);
		
		return submission;
		
	}
	
	private static void moveExternalFiles(List<DepositFile> files, ExternalDepositFileConfigurationProvider externalDepositFileConfigurationProvider) {
		
		for (DepositFile depositFile : files) {

			if (depositFile.isExternal()) {

				try {
					File temp = File.createTempFile("data", depositFile.getExtension(), new File(externalDepositFileConfigurationProvider.getExternalPath()));
					
					Files.move(depositFile.getFile().toPath(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
					depositFile.setFile(temp);
				} catch (IOException e) {
					throw new Error(e);
				}

			}

		}
		
	}
	
	private static IdentityHashMap<DepositFile, String> buildFilenameMap(
			List<DepositFile> files,
			ExternalDepositFileConfigurationProvider externalDepositFileConfigurationProvider) {

		IdentityHashMap<DepositFile, String> filenames = new IdentityHashMap<DepositFile, String>();

		int index = 0;

		for (DepositFile file : files) {
			
			if (file.isExternal()) {
				
				String path = file.getFile().getAbsolutePath();

				if (path.startsWith(externalDepositFileConfigurationProvider.getExternalPath()))
					path = path.substring(externalDepositFileConfigurationProvider.getExternalPath().length());
				else
					throw new Error("External file's path doesn't start with configured external directory path.");
				
				// Ensure the path always starts with a slash
				
				if (!path.startsWith("/")) {
					path = "/" + path;
				}

				try {
					java.net.URI uri = new java.net.URI(externalDepositFileConfigurationProvider.getExternalUriBase() + path);
					filenames.put(file, uri.toString());
				} catch (URISyntaxException e) {
					throw new Error(e);
				}
				
			} else {
			
				filenames.put(file, "data_" + index + file.getExtension());
			
			}
			
			index++;
		}

		return filenames;
		
	}
	
	private static gov.loc.mets.DocumentRoot buildMets(Deposit deposit, IdentityHashMap<DepositFile, String> filenames) {

		gov.loc.mets.DocumentRoot root;
		MetsType mets;
		
		// Mapping between files in the deposit and mets:file elements
		IdentityHashMap<DepositFile, FileType> filesFiles;
		
		// Reference to the root mets:div element
		DivType rootDiv;
		
		// Reference to the mets:div element for the agreement (if present)
		DivType agreementDiv = null;
		
		// Reference to the mets:div element for the main file (if present)
		DivType mainFileDiv = null;
		
		// Mapping between mets:div elements and deposit entries
		IdentityHashMap<DivType, DepositEntry> fileDivsEntries;
		
		// Mapping from mets:div elements to metadata
		IdentityHashMap<DivType, Map<OutputMetadataSections, EObject>> divsMetadata;
		
		
		// Grab file basics from the deposit: the form, a list of all files, and the main file (if present)
		
		Form form = deposit.getForm();
		List<DepositFile> files = deposit.getAllFiles();
		DepositFile mainFile = deposit.getMainFile();

		
		// Document root

		{

			root = MetsFactory.eINSTANCE.createDocumentRoot();
			root.setMets(MetsFactory.eINSTANCE.createMetsType1());
			mets = root.getMets();

			mets.setPROFILE("http://cdr.unc.edu/METS/profiles/Simple");

		}

		
		// Header

		{

			MetsHdrType head = MetsFactory.eINSTANCE.createMetsHdrType();
			Date currentTime = new Date(System.currentTimeMillis());
			head.setCREATEDATE(new XMLCalendar(currentTime, XMLCalendar.DATETIME));
			head.setLASTMODDATE(new XMLCalendar(currentTime, XMLCalendar.DATETIME));

			AgentType agent;

			if (form.getCurrentUser() != null) {
				agent = MetsFactory.eINSTANCE.createAgentType();
				agent.setROLE(ROLEType.CREATOR);
				agent.setTYPE(TYPEType.INDIVIDUAL);
				agent.setName(form.getCurrentUser());
				head.getAgent().add(agent);
			}

			agent = MetsFactory.eINSTANCE.createAgentType();
			agent.setROLE(ROLEType.CREATOR);
			agent.setTYPE(TYPEType.OTHER);
			agent.setName("CDR Forms");
			head.getAgent().add(agent);

			mets.setMetsHdr(head);

		}		
		
		
		// Files section

		filesFiles = new IdentityHashMap<DepositFile, FileType>();

		{

			FileSecType fileSec = MetsFactory.eINSTANCE.createFileSecType();
			FileGrpType1 fileGrp = MetsFactory.eINSTANCE.createFileGrpType1();

			int i = 0;

			for (DepositFile depositFile : files) {
				FileType file = MetsFactory.eINSTANCE.createFileType();
				file.setID("f_" + i);
				file.setMIMETYPE(depositFile.getContentType());
				
				if (depositFile.isExternal()) {
					file.setCHECKSUMTYPE(CHECKSUMTYPEType.MD5);
					
					try {
						file.setCHECKSUM(depositFile.getHexDigest("MD5"));
					} catch (IOException e) {
						throw new Error(e);
					} catch (NoSuchAlgorithmException e) {
						throw new Error(e);
					}
				}

				FLocatType fLocat = MetsFactory.eINSTANCE.createFLocatType();
				fLocat.setHref(filenames.get(depositFile));
				
				if (depositFile.isExternal()) {
					fLocat.setLOCTYPE(LOCTYPEType.OTHER);
					fLocat.setOTHERLOCTYPE("tag");
					fLocat.setUSE("STAGE");
				} else {
					fLocat.setLOCTYPE(LOCTYPEType.URL);
				}

				file.getFLocat().add(fLocat);
				fileGrp.getFile().add(file);

				filesFiles.put(depositFile, file);

				i++;
			}

			fileSec.getFileGrp().add(fileGrp);
			mets.setFileSec(fileSec);

		}
		
		
		// structMap section
		
		fileDivsEntries = new IdentityHashMap<DivType, DepositEntry>();
		
		if (mainFile != null && files.size() == 1) {

			StructMapType structMap = MetsFactory.eINSTANCE.createStructMapType();
			
			rootDiv = makeDivForFile(filesFiles.get(mainFile));
			rootDiv.setID("d_0");
			rootDiv.setLABEL1(mainFile.getFilename());
			
			mainFileDiv = rootDiv;
			
			mets.getStructMap().add(structMap);
			structMap.setDiv(rootDiv);
			
		} else {
			
			StructMapType structMap = MetsFactory.eINSTANCE.createStructMapType();
			
			rootDiv = MetsFactory.eINSTANCE.createDivType();
			rootDiv.setTYPE(METSConstants.Div_AggregateWork);
			rootDiv.setID("d_0");
			
			structMap.setDiv(rootDiv);
			mets.getStructMap().add(structMap);
			
			int i = 1;
			
			// Main file (if present)
			
			if (mainFile != null) {
				mainFileDiv = makeDivForFile(filesFiles.get(mainFile));
				mainFileDiv.setID("d_" + i++);
				mainFileDiv.setLABEL1(mainFile.getFilename());
				
				rootDiv.getDiv().add(mainFileDiv);
			}
			
			// Deposit entries
			
			for (DepositElement element : deposit.getElements()) {
				if (element.getFormElement() instanceof FileBlock) {
					
					FileBlock fileBlock = (FileBlock) element.getFormElement();
					
					String label = null;
					
					if (fileBlock != null && fileBlock.getLabel() != null && fileBlock.getLabel().trim().length() > 0)
						label = fileBlock.getLabel().trim();

					for (DepositEntry entry : element.getEntries()) {
						
						if (entry.getFile() != null) {
							DivType fileDiv = makeDivForFile(filesFiles.get(entry.getFile()));
							fileDiv.setID("d_" + i++);
							fileDiv.setLABEL1(label != null ? label : entry.getFile().getFilename());
							
							rootDiv.getDiv().add(fileDiv);
							fileDivsEntries.put(fileDiv, entry);
						}
						
					}
					
				}
			}
			
			// Supplemental files
			
			if (deposit.getSupplementalFiles() != null) {
				
				for (DepositFile depositFile : deposit.getSupplementalFiles()) {
					
					if (depositFile != null) {
						DivType fileDiv = makeDivForFile(filesFiles.get(depositFile));
						fileDiv.setID("d_" + i++);
						fileDiv.setLABEL1(depositFile.getFilename());

						rootDiv.getDiv().add(fileDiv);
					}
					
				}
				
			}
			
			// Add agreement file if one is set
			if (deposit.getAgreementFile() != null) {
				agreementDiv = makeDivForFile(filesFiles.get(deposit.getAgreementFile()));
				agreementDiv.setID("d_" + i++);
				agreementDiv.setLABEL1(deposit.getAgreementFile().getFilename());
					
				rootDiv.getDiv().add(agreementDiv);
			}	
		}
		
		
		// Metadata (amdSec and dmdSec sections)
		
		divsMetadata = new IdentityHashMap<DivType, Map<OutputMetadataSections, EObject>>();
		
		// Gather metadata for entries associated with FileBlocks
		
		for (Entry<DivType, DepositEntry> entry : fileDivsEntries.entrySet()) {
			divsMetadata.put(entry.getKey(), makeMetadata(form.getOutputProfiles(), Collections.singletonList(entry.getValue())));
		}
		
		// Metadata for the root div is gathered from all entries not associated with FileBlocks
		
		{
		
			ArrayList<DepositEntry> rootEntries = new ArrayList<DepositEntry>();
			
			for (DepositElement element : deposit.getElements()) {
				
				if (element.getFormElement() instanceof MetadataBlock && !(element.getFormElement() instanceof FileBlock)) {
					
					// If this isn't a repeating metadata block, just all all the entries.
					// Otherwise, only add entries with all non-blank values.
					
					if (((MetadataBlock) element.getFormElement()).getMaxRepeat() == 1) {
						
						rootEntries.addAll(element.getEntries());
						
					} else {
					
						for (DepositEntry depositEntry : element.getEntries()) {
							
							boolean allBlank = true;
							
							for (DepositField<?> depositField : depositEntry.getFields()) {
								Object value = depositField.getValue();
							
								if (value != null && StringUtils.hasText(value.toString())) {
									allBlank = false;
									break;
								}
							}
							
							if (allBlank == false) {
								rootEntries.add(depositEntry);
							}
							
						}
						
					}
					
				}
			}
			
			divsMetadata.put(rootDiv, makeMetadata(form.getOutputProfiles(), rootEntries));
		
		}
		
		// Special case: Add divs and metadata for supplemental objects
		
		{
			
			int i = 0;
		
			for (SupplementalObject object : deposit.getSupplementalObjects()) {
				
				DivType fileDiv = makeDivForFile(filesFiles.get(object.getDepositFile()));
				fileDiv.setID("s_" + i++);
				fileDiv.setLABEL1(object.getDepositFile().getFilename());
				
				rootDiv.getDiv().add(fileDiv);
				
				IdentityHashMap<OutputMetadataSections, EObject> metadata = new IdentityHashMap<OutputMetadataSections, EObject>();
				metadata.put(OutputMetadataSections.DMD_SEC, object.getDescriptiveMetadata());
				divsMetadata.put(fileDiv, metadata);
				
			}
		
		}
		
		if (agreementDiv != null) {
			divsMetadata.put(agreementDiv, makeMetadata(form.getOutputProfiles(), new ArrayList<DepositEntry>()));
		}
		
		// Special cases for metadata
		
		// Find the root div's access control metadata object, adding a new one if none is present
		
		AccessControlType rootAccessControl;
		
		{
			
			edu.unc.lib.schemas.acl.DocumentRoot documentRoot = (edu.unc.lib.schemas.acl.DocumentRoot) divsMetadata.get(rootDiv).get(OutputMetadataSections.RIGHTS_MD);
			
			if (documentRoot.eContents().isEmpty()) {
				rootAccessControl = AclFactory.eINSTANCE.createAccessControlType();
				documentRoot.setAccessControl(rootAccessControl);
			} else {
				rootAccessControl = (AccessControlType) documentRoot.eContents().get(0);
			}
			
		}
		
		// Special case: set published="false" if the submission should be reviewed before publication
		
		if (form.isReviewBeforePublication()) {
			
			rootAccessControl.setPublished(false);
			
		}
		
		// Add access control for agreement file if one is present
		if (agreementDiv != null) {
			AccessControlType agreementAccessControl;
			edu.unc.lib.schemas.acl.DocumentRoot documentRoot = (edu.unc.lib.schemas.acl.DocumentRoot) divsMetadata.get(agreementDiv).get(OutputMetadataSections.RIGHTS_MD);
			
			if (documentRoot.eContents().isEmpty()) {
				agreementAccessControl = AclFactory.eINSTANCE.createAccessControlType();
				documentRoot.setAccessControl(agreementAccessControl);
			} else {
				agreementAccessControl = (AccessControlType) documentRoot.eContents().get(0);
			}
			
			agreementAccessControl.setPublished(false);			
		}

		// Special case: set major-related metadata if a MajorBlock is present
		
		MajorBlock majorBlock = null;

		for (FormElement fe : form.getElements()) {
			if (fe instanceof MajorBlock) {
				majorBlock = (MajorBlock) fe;
				break;
			}
		}
		
		if (majorBlock != null) {
					
			MajorEntry major = majorBlock.getSelectedMajor();
			
			EObject generatedFeature = majorBlock.getNameElement().getGeneratedFeature();
			
			if (generatedFeature != null && generatedFeature instanceof NameDefinition) {
				NameDefinition nameDef = (NameDefinition) generatedFeature;
				XsString affiliation = MODSFactory.eINSTANCE.createXsString();
				affiliation.setValue(major.getName());
				nameDef.getAffiliation().add(affiliation);
			}
			
			// Create access control restrictions from the major
			
			for (String group : major.getObserverGroups()) {
				if (group != null && group.trim().length() > 0) {
					GrantType grantType = AclFactory.eINSTANCE.createGrantType();
					grantType.setGroup(group);
					grantType.setRole("observer");
					rootAccessControl.getGrant().add(grantType);
				}
			}
			
			for (String group : major.getReviewerGroups()) {
				if (group != null && group.trim().length() > 0) {
					GrantType grantType = AclFactory.eINSTANCE.createGrantType();
					grantType.setGroup(group);
					grantType.setRole("processor");
					rootAccessControl.getGrant().add(grantType);
				}
			}
			
		}
		
		// Special case: for all non-root divs associated with a file block that has any values for copyGrantsHavingRoles, copy grants
		// from the root access control object to the access control object for that div having those roles.
		
		for (Entry<DivType, Map<OutputMetadataSections, EObject>> pair : divsMetadata.entrySet()) {
			
			DepositEntry entry = fileDivsEntries.get(pair.getKey());
			
			if (entry == null)
				continue;
			
			FormElement element = entry.getFormElement();
			
			if (element == null || !(element instanceof FileBlock))
				continue;
			
			FileBlock fileBlock = (FileBlock) element;
			
			if (fileBlock.getCopyGrantsHavingRoles() != null && fileBlock.getCopyGrantsHavingRoles().size() > 0) {
				
				AccessControlType accessControl;
				
				{
					
					edu.unc.lib.schemas.acl.DocumentRoot documentRoot = (edu.unc.lib.schemas.acl.DocumentRoot) divsMetadata.get(pair.getKey()).get(OutputMetadataSections.RIGHTS_MD);
					
					if (documentRoot.eContents().isEmpty()) {
						accessControl = AclFactory.eINSTANCE.createAccessControlType();
						documentRoot.setAccessControl(accessControl);
					} else {
						accessControl = (AccessControlType) documentRoot.eContents().get(0);
					}
					
				}
				
				
				for (String role : fileBlock.getCopyGrantsHavingRoles()) {
					
					for (GrantType grant : rootAccessControl.getGrant()) {
						
						if (grant.getRole().equals(role))
							accessControl.getGrant().add(EcoreUtil.copy(grant));
						
					}
					
				}
				
			}
			
		}
		
		
		// Wrap, link, and add metadata
		
		{

			AmdSecType amdSec = MetsFactory.eINSTANCE.createAmdSecType();
			mets.getAmdSec().add(amdSec);
			
			int i = 0;

			for (Entry<DivType, Map<OutputMetadataSections, EObject>> divMetadataPair : divsMetadata.entrySet()) {
				
				DivType div = divMetadataPair.getKey();
				
				for (OutputProfile profile : form.getOutputProfiles()) {
					
					EObject output = divMetadataPair.getValue().get(profile.getMetadataSection());
					
					if (output == null || output.eContents() == null || output.eContents().isEmpty())
						continue;

					if (!profile.isStartMappingAtChildren())
						output = output.eContents().get(0);
					
					// Wrap metadata output
					
					MdWrapType mdWrap = MetsFactory.eINSTANCE.createMdWrapType();

					MDTYPEType mdType = MDTYPEType.get(profile.getMetadataType());
					if (mdType == null) {
						mdWrap.setMDTYPE(MDTYPEType.OTHER);
						mdWrap.setOTHERMDTYPE(profile.getMetadataType());
					} else {
						mdWrap.setMDTYPE(mdType);
					}

					XmlDataType1 xml = MetsFactory.eINSTANCE.createXmlDataType1();
					xml.getAny().add(profile.getParentMappedFeature(), output);
					mdWrap.setXmlData(xml);

					MdSecType mdSec = MetsFactory.eINSTANCE.createMdSecType();
					mdSec.setMdWrap(mdWrap);
					mdSec.setID("md_" + i);
					
					// Add to the METS object and link with div

					switch (profile.getMetadataSection()) {
						case DIGIPROV_MD:
							amdSec.getDigiprovMD().add(mdSec);
							div.getMdSec().add(mdSec);
							break;
						case RIGHTS_MD:
							amdSec.getRightsMD().add(mdSec);
							div.getMdSec().add(mdSec);
							break;
						case SOURCE_MD:
							amdSec.getSourceMD().add(mdSec);
							div.getMdSec().add(mdSec);
							break;
						case TECH_MD:
							amdSec.getTechMD().add(mdSec);
							div.getMdSec().add(mdSec);
							break;
						case DMD_SEC:
							mets.getDmdSec().add(mdSec);
							div.getDmdSec().add(mdSec);
							break;
					}

					i++;
					
				}

			}
			
		}
		
		
		// structLink section
		
		if (rootDiv.getTYPE().equals(METSConstants.Div_AggregateWork)) {
			
			StructLinkType1 structLink = MetsFactory.eINSTANCE.createStructLinkType1();
			
			// Add a link for the mainFileDiv, if we have it. (This means the form doesn't have any FileBlock instances.)
			
			if (mainFileDiv != null) {
				
				SmLinkType smLink = MetsFactory.eINSTANCE.createSmLinkType();
				smLink.setArcrole(Link.DEFAULTACCESS.uri);
				smLink.setXlinkFrom(rootDiv);
				smLink.setXlinkTo(mainFileDiv);

				structLink.getSmLink().add(smLink);
				
			} else {
			
				// The DepositEntry instances in this map should be guaranteed to have FileBlock instances for their formElement properties
				// and non-null valid DepositFile instances for their file properties by construction above.
			
				for (Entry<DivType, DepositEntry> entry : fileDivsEntries.entrySet()) {
				
					FileBlock fileBlock = (FileBlock) entry.getValue().getFormElement();
					DepositFile file = (DepositFile) entry.getValue().getFile();
				
					if (fileBlock.isDefaultAccess() || file == mainFile) {

						DivType fileDiv = entry.getKey();
					
						SmLinkType smLink = MetsFactory.eINSTANCE.createSmLinkType();
						smLink.setArcrole(Link.DEFAULTACCESS.uri);
						smLink.setXlinkFrom(rootDiv);
						smLink.setXlinkTo(fileDiv);

						structLink.getSmLink().add(smLink);
					
					}
			
				}
				
			}

			// Only add the structLink section if there are actually links

			if (structLink.getSmLink().size() > 0)
				mets.setStructLink(structLink);
			
		}

		return root;
		
	}
	
	private static DivType makeDivForFile(FileType file) {
		
		DivType div = MetsFactory.eINSTANCE.createDivType();
		div.setTYPE(METSConstants.Div_File);

		FptrType fptr = MetsFactory.eINSTANCE.createFptrType();
		fptr.setFILEID(file.getID());
		div.getFptr().add(fptr);
		
		return div;
		
	}
	
	private static Map<OutputMetadataSections, EObject> makeMetadata(List<OutputProfile> profiles, List<DepositEntry> entries) {
		
		IdentityHashMap<OutputMetadataSections, EObject> metadata = new IdentityHashMap<OutputMetadataSections, EObject>(); 
		
		for (OutputProfile profile : profiles) {
			
			EClass outputElementClass;

			if (profile.isStartMappingAtChildren())
				outputElementClass = profile.getParentMappedFeature().getEReferenceType();
			else
				outputElementClass = profile.getParentMappedFeature().getEContainingClass();

			EObject outputElement = outputElementClass.getEPackage().getEFactoryInstance().create(outputElementClass);
			
			
			for (DepositEntry entry : entries) {
				
				FormElement formElement = entry.getFormElement();
				
				if (formElement instanceof MetadataBlock) {

					MetadataBlock metadataBlock = (MetadataBlock) formElement;
					
					// For each entry, "fill out" the metadata block's ports using the values from the entry's fields.

					int portIndex = 0;
	
					for (InputField<?> inputField : metadataBlock.getPorts()) {
						if (inputField instanceof DateInputField) {
							((DateInputField) inputField).setEnteredValue((Date) entry.getFields().get(portIndex).getValue());
						} else if (inputField instanceof TextInputField) {
							((TextInputField) inputField).setEnteredValue((String) entry.getFields().get(portIndex).getValue());
						} else if (inputField instanceof EmailInputField) {
							((EmailInputField) inputField).setEnteredValue((String) entry.getFields().get(portIndex).getValue());
						} else {
							throw new Error("Unknown input field type");
						}
	
						portIndex++;
					}
	
					for (OutputElement oe : metadataBlock.getElements()) {
						oe.updateRecord(outputElement);
					}
					
				}

			}
			
			metadata.put(profile.getMetadataSection(), outputElement);
			
		}
		
		return metadata;
		
	}
	
	
	private Submission() {
		
	}
	
	public gov.loc.mets.DocumentRoot getMetsDocumentRoot() {
		return metsDocumentRoot;
	}

	public IdentityHashMap<DepositFile, String> getFiles() {
		return files;
	}

}
