package cdr.forms;

import edu.unc.lib.schemas.acl.AccessControlType;
import edu.unc.lib.schemas.acl.AclFactory;
import edu.unc.lib.schemas.acl.AclPackage;
import edu.unc.lib.schemas.acl.GrantType;
import gov.loc.mets.AgentType;
import gov.loc.mets.AmdSecType;
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

import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xml.type.internal.XMLCalendar;

import crosswalk.DateInputField;
import crosswalk.FileBlock;
import crosswalk.Form;
import crosswalk.FormElement;
import crosswalk.InputField;
import crosswalk.MajorBlock;
import crosswalk.MajorEntry;
import crosswalk.MetadataBlock;
import crosswalk.OutputElement;
import crosswalk.OutputProfile;
import crosswalk.TextInputField;

public class Submission {
	
	IdentityHashMap<DepositFile, String> files;
	gov.loc.mets.DocumentRoot metsDocumentRoot;
	
	public static Submission create(Deposit deposit) {
		
		Submission submission = new Submission();

		submission.files = buildFilenameMap(deposit.getAllFiles());
		submission.metsDocumentRoot = buildMets(deposit, submission.files);
		
		return submission;
		
	}
	
	private static IdentityHashMap<DepositFile, String> buildFilenameMap(List<DepositFile> files) {

		IdentityHashMap<DepositFile, String> filenames = new IdentityHashMap<DepositFile, String>();

		int index = 0;

		for (DepositFile file : files) {
			filenames.put(file, "data_" + index + file.getExtension());
			index++;
		}

		return filenames;
		
	}
	
	private static gov.loc.mets.DocumentRoot buildMets(Deposit deposit, IdentityHashMap<DepositFile, String> filenames) {

		gov.loc.mets.DocumentRoot root;
		MetsType mets;
		AmdSecType amdSec;
		MdSecType dmdSec;
		DivType aggregateWorkDiv = null;
		
		Form form = deposit.getForm();
		
		IdentityHashMap<DepositFile, FileBlock> fileBlockMap = new IdentityHashMap<DepositFile, FileBlock>();

		for (Entry<FileBlock, Integer> entry : deposit.getBlockFileIndexMap().entrySet()) {
			DepositFile depositFile = deposit.getFiles()[entry.getValue()];

			if (depositFile != null)
				fileBlockMap.put(depositFile, entry.getKey());
		}

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

		// Metadata sections

		dmdSec = null;
		amdSec = MetsFactory.eINSTANCE.createAmdSecType();

		{

			int i = 0;

			for (OutputProfile profile : form.getOutputProfiles()) {
				MdSecType mdSec = makeMetadata(profile, deposit);
				
				if (mdSec != null) {
					mdSec.setID("md_" + i);

					switch (profile.getMetadataSection()) {
						case DIGIPROV_MD:
							amdSec.getDigiprovMD().add(mdSec);
							break;
						case RIGHTS_MD:
							amdSec.getRightsMD().add(mdSec);
							break;
						case SOURCE_MD:
							amdSec.getSourceMD().add(mdSec);
							break;
						case TECH_MD:
							amdSec.getTechMD().add(mdSec);
							break;
						case DMD_SEC:
							dmdSec = mdSec;
							mets.getDmdSec().add(mdSec);
							break;
					}

					i++;
				}
			}

			mets.getAmdSec().add(amdSec);

		}

		
		AccessControlType accessControl = null;
		// If the form specifies that the object should be reviewed before publication,
		// the ACL should specify that it is not published.

		// Set publication status 
		if (form.isReviewBeforePublication()) {
			accessControl = getRightsMD(amdSec);
			accessControl.setPublished(false);
		}
		
		// Set major related metadata if a MajorBlock is present, both description and rights 
		MajorBlock majorBlock = null;
		for (FormElement fe : form.getElements()) {
			if (fe instanceof MajorBlock) {
				majorBlock = (MajorBlock) fe;
				break;
			}
		}
		if (majorBlock != null) {
			MajorEntry major = majorBlock.getSelectedMajor();
			// Create the affiliation
			EObject generatedFeature = majorBlock.getNameElement().getGeneratedFeature();
			if (generatedFeature != null && generatedFeature instanceof NameDefinition) {
				NameDefinition nameDef = (NameDefinition) generatedFeature;
				XsString affiliation = MODSFactory.eINSTANCE.createXsString();
				affiliation.setValue(major.getName());
				nameDef.getAffiliation().add(affiliation);
			}
			// Create access control restrictions from the major
			if (accessControl != null)
				accessControl = getRightsMD(amdSec);
			for (String group: major.getObserverGroups()) {
				if (group != null && group.length() > 0) {
					GrantType grantType = AclFactory.eINSTANCE.createGrantType();
					grantType.setGroup(group);
					grantType.setRole("acl:observer");
					accessControl.getGrant().add(grantType);
				}
			}
			for (String group: major.getReviewerGroups()) {
				if (group != null && group.length() > 0) {
					GrantType grantType = AclFactory.eINSTANCE.createGrantType();
					grantType.setGroup(group);
					grantType.setRole("acl:processor");
					accessControl.getGrant().add(grantType);
				}
			}
		}

		// Files section

		IdentityHashMap<DepositFile, FileType> filesFiles = new IdentityHashMap<DepositFile, FileType>();

		{

			FileSecType fileSec = MetsFactory.eINSTANCE.createFileSecType();
			FileGrpType1 fileGrp = MetsFactory.eINSTANCE.createFileGrpType1();

			int i = 0;

			for (DepositFile depositFile : files) {

				FileType file = MetsFactory.eINSTANCE.createFileType();
				file.setID("f_" + i);
				file.setMIMETYPE(depositFile.getContentType());

				FLocatType fLocat = MetsFactory.eINSTANCE.createFLocatType();
				fLocat.setLOCTYPE(LOCTYPEType.URL);
				fLocat.setHref(filenames.get(depositFile));

				file.getFLocat().add(fLocat);
				fileGrp.getFile().add(file);

				filesFiles.put(depositFile, file);

				i++;

			}

			fileSec.getFileGrp().add(fileGrp);
			mets.setFileSec(fileSec);

		}

		// Structural map

		IdentityHashMap<DepositFile, DivType> fileDivs = new IdentityHashMap<DepositFile, DivType>();
		DivType rootDiv;

		if (mainFile != null && files.size() == 1) {

			StructMapType structMap = MetsFactory.eINSTANCE.createStructMapType();

			DivType fileDiv = MetsFactory.eINSTANCE.createDivType();

			fileDiv.setTYPE(METSConstants.Div_File);
			fileDiv.setID("d_0");
			fileDiv.setLABEL1(mainFile.getFilename());

			FptrType fptr = MetsFactory.eINSTANCE.createFptrType();
			fptr.setFILEID(filesFiles.get(mainFile).getID());
			fileDiv.getFptr().add(fptr);

			structMap.setDiv(fileDiv);
			mets.getStructMap().add(structMap);

			fileDivs.put(mainFile, fileDiv);
			rootDiv = fileDiv;

		} else {

			StructMapType structMap = MetsFactory.eINSTANCE.createStructMapType();

			aggregateWorkDiv = MetsFactory.eINSTANCE.createDivType();
			aggregateWorkDiv.setTYPE(METSConstants.Div_AggregateWork);
			aggregateWorkDiv.setID("d_0");

			int i = 1;

			for (DepositFile depositFile : files) {

				DivType fileDiv = MetsFactory.eINSTANCE.createDivType();
				fileDiv.setTYPE(METSConstants.Div_File);

				FileBlock fileBlock = fileBlockMap.get(depositFile);
				if (fileBlock != null && fileBlock.getLabel() != null && fileBlock.getLabel().trim().length() > 0)
					fileDiv.setLABEL1(fileBlock.getLabel());
				else
					fileDiv.setLABEL1(depositFile.getFilename());

				FptrType fptr = MetsFactory.eINSTANCE.createFptrType();
				fptr.setFILEID(filesFiles.get(depositFile).getID());
				fileDiv.getFptr().add(fptr);
				fileDiv.setID("d_" + i);

				aggregateWorkDiv.getDiv().add(fileDiv);

				fileDivs.put(depositFile, fileDiv);

				i++;

			}

			structMap.setDiv(aggregateWorkDiv);
			mets.getStructMap().add(structMap);

			rootDiv = aggregateWorkDiv;

		}

		// Add metadata

		if (dmdSec != null)
			rootDiv.getDmdSec().add(dmdSec);

		rootDiv.getMdSec().addAll(amdSec.getDigiprovMD());
		rootDiv.getMdSec().addAll(amdSec.getRightsMD());
		rootDiv.getMdSec().addAll(amdSec.getSourceMD());
		rootDiv.getMdSec().addAll(amdSec.getTechMD());

		// Structural Links

		// Add "default access" links from the Aggregate Work div to each File div
		// if its corresponding FileBlock has "default access role" set, or
		// if its corresponding DepositFile is the main file (if set).

		if (aggregateWorkDiv != null) {

			StructLinkType1 structLink = MetsFactory.eINSTANCE.createStructLinkType1();

			for (DepositFile depositFile : files) {

				FileBlock fileBlock = fileBlockMap.get(depositFile);

				if ((fileBlock != null && fileBlock.isDefaultAccess()) || (depositFile == mainFile)) {

					DivType fileDiv = fileDivs.get(depositFile);

					SmLinkType smLink = MetsFactory.eINSTANCE.createSmLinkType();
					smLink.setArcrole(Link.DEFAULTACCESS.uri);
					smLink.setXlinkFrom(aggregateWorkDiv);
					smLink.setXlinkTo(fileDiv);

					structLink.getSmLink().add(smLink);

				}

			}

			// Only add the structLink section if there are actually links

			if (structLink.getSmLink().size() > 0)
				mets.setStructLink(structLink);

		}

		return root;
		
	}
	
	private static AccessControlType getRightsMD(AmdSecType amdSec) {
		
		AccessControlType accessControl = null;

		for (MdSecType mdSec : amdSec.getRightsMD()) {

			if (mdSec.getMdWrap() != null && mdSec.getMdWrap().getMDTYPE().equals(MDTYPEType.OTHER)
					&& mdSec.getMdWrap().getOTHERMDTYPE().equals("ACL")) {
				return (AccessControlType) mdSec.getMdWrap().getXmlData().getAny()
						.list(AclPackage.eINSTANCE.getDocumentRoot_AccessControl()).get(0);
			}

		}

		accessControl = AclFactory.eINSTANCE.createAccessControlType();
		
		MdSecType rightsMdSec = MetsFactory.eINSTANCE.createMdSecType();
		rightsMdSec.setID("md_review");

		MdWrapType mdWrap = MetsFactory.eINSTANCE.createMdWrapType();
		mdWrap.setMDTYPE(MDTYPEType.OTHER);
		mdWrap.setOTHERMDTYPE("ACL");

		XmlDataType1 xmlData = MetsFactory.eINSTANCE.createXmlDataType1();
		xmlData.getAny().add(AclPackage.eINSTANCE.getDocumentRoot_AccessControl(), accessControl);

		mdWrap.setXmlData(xmlData);
		rightsMdSec.setMdWrap(mdWrap);

		amdSec.getRightsMD().add(rightsMdSec);
		
		return accessControl;
		
	}
	
	private static MdSecType makeMetadata(OutputProfile profile, Deposit deposit) {
		
		EClass outputElementClass = null;

		if (profile.isStartMappingAtChildren())
			outputElementClass = profile.getParentMappedFeature().getEReferenceType();
		else
			outputElementClass = profile.getParentMappedFeature().getEContainingClass();

		EObject outputElement = outputElementClass.getEPackage().getEFactoryInstance().create(outputElementClass);
		
		for (DepositElement element : deposit.getElements()) {
			
			if (element.getFormElement() instanceof MetadataBlock) {
				
				MetadataBlock metadataBlock = (MetadataBlock) element.getFormElement();
				
				// For each entry, "fill out" the metadata block's ports using the values from the entry's fields.
				// FIXME: clone metadata block rather than reusing the same instance?
				
				for (DepositEntry entry : element.getEntries()) {
					
					int portIndex = 0;
					
					for (InputField<?> inputField : metadataBlock.getPorts()) {
						if (inputField instanceof DateInputField) {
							((DateInputField) inputField).setEnteredValue((Date) entry.getFields().get(portIndex).getValue());
						} else if (inputField instanceof TextInputField) {
							((TextInputField) inputField).setEnteredValue((String) entry.getFields().get(portIndex).getValue());
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
			
		}

		if (outputElement.eContents() == null || outputElement.eContents().isEmpty())
			return null;

		if (!profile.isStartMappingAtChildren())
			outputElement = outputElement.eContents().get(0);

		MdWrapType mdWrap = MetsFactory.eINSTANCE.createMdWrapType();

		MDTYPEType mdType = MDTYPEType.get(profile.getMetadataType());
		if (mdType == null) {
			mdWrap.setMDTYPE(MDTYPEType.OTHER);
			mdWrap.setOTHERMDTYPE(profile.getMetadataType());
		} else {
			mdWrap.setMDTYPE(mdType);
		}

		XmlDataType1 xml = MetsFactory.eINSTANCE.createXmlDataType1();
		xml.getAny().add(profile.getParentMappedFeature(), outputElement);
		mdWrap.setXmlData(xml);

		MdSecType mdSec = MetsFactory.eINSTANCE.createMdSecType();
		mdSec.setMdWrap(mdWrap);

		return mdSec;

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
