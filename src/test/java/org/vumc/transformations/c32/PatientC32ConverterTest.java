package org.vumc.transformations.c32;

import org.junit.Before;
import org.junit.Test;
import org.vumc.model.Patient;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PatientC32ConverterTest
{
  private PatientC32Converter converter;
  private PatientC32DocumentTransformer transformer;


  @Before
  public void setUp() throws Exception
  {
    transformer = new PatientC32DocumentTransformer(new TestConfig().patientC32DocumentTransformer());

    CCDXslNamespaceXPathSource xPathSource = new CCDXslNamespaceXPathSource();

    PatientDocumentC32Finder finder = new PatientDocumentC32Finder(xPathSource);
    PatientC32Extractor extractor = new PatientC32Extractor(xPathSource, transformer);
    converter = new PatientC32Converter(finder, extractor);
  }

  @Test
  public void canCreateAPatientFromC32Message() throws Exception
  {

    Patient patient = converter.convert(sampleMessage());

    assertEquals(sampleMessage_PatientBody(), patient.getBody().replaceAll("(\\r)", ""));
    assertEquals(sampleMessage(), patient.getRawMessage());
    assertEquals("HOOT", patient.getName().getFamily());
    assertEquals("SCOOT", patient.getName().getGiven());
    assertEquals("Full name is not populated currently", null, patient.getName().getName());
    assertEquals("Suffix not supplied in this message", null, patient.getName().getSuffix());
    assertEquals("AACJ-4531-1", patient.getPatientId());
    assertEquals("Gender not supplied in this message", null, patient.getGender());
    assertTrue("CreatedDate not set within 1000 ms of actual time."
            ,ChronoUnit.MILLIS.between(patient.getCreated(), ZonedDateTime.now())< 1000);

  }

  @Test
  public void canParseMessageWithEncodedBody() throws Exception
  {
    Patient patient = converter.convert(sampleMessage2());
    assertEquals(sampleMessage_PatientBody2(), patient.getBody().replaceAll("(\\r)", ""));
    assertEquals(sampleMessage2(), patient.getRawMessage());
    assertEquals("CADENCE", patient.getName().getFamily());
    assertEquals("JOANIE", patient.getName().getGiven());
    assertEquals("Full name is not populated currently", null, patient.getName().getName());
    assertEquals("Suffix not supplied in this message", null, patient.getName().getSuffix());
    assertEquals("002102283", patient.getPatientId());
    assertEquals("F", patient.getGender());
    assertTrue("CreatedDate not set within 1000 ms of actual time."
            ,ChronoUnit.MILLIS.between(patient.getCreated(), ZonedDateTime.now())< 1000);
  }





  private String sampleMessage()
  {
    return
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><?xml-stylesheet type=\"text/xsl\" href=\"toPatientJson.xsl\"?>" +
            "<ns3:ClinicalDocument xmlns:ns2=\"urn:hl7-org:sdtc\" xmlns:ns3=\"urn:hl7-org:v3\" xmlns:ns4=\"urn:gov:hhs:fha:nhinc:common:nhinccommon\" xmlns:ns5=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:ns6=\"urn:gov:hhs:fha:nhinc:common:patientcorrelationfacade\" xmlns=\"urn:hl7-org:v3\">" +
            "    <ns3:realmCode code=\"US\"></ns3:realmCode>" +
            "    <ns3:typeId extension=\"POCD_HD000040\" root=\"2.16.840.1.113883.1.3\"></ns3:typeId>" +
            "    <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1\"></ns3:templateId>" +
            "    <ns3:templateId assigningAuthorityName=\"HL7/CDT Header\" root=\"2.16.840.1.113883.10.20.3\"></ns3:templateId>" +
            "    <ns3:templateId assigningAuthorityName=\"HITSP/C32\" root=\"2.16.840.1.113883.3.88.11.32.1\"></ns3:templateId>" +
            "    <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.1.1\"></ns3:templateId>" +
            "    <ns3:templateId assigningAuthorityName=\"IHE PCC\" extension=\"XPHR Summary\" root=\"1.3.6.1.4.1.19376.1.5.3.1.1.5\"></ns3:templateId>" +
            "    <ns3:templateId assigningAuthorityName=\"IHE PCC\" extension=\"Referral Summary\" root=\"1.3.6.1.4.1.19376.1.5.3.1.1.3\"></ns3:templateId>" +
            "    <ns3:templateId assigningAuthorityName=\"IHE PCC\" extension=\"Medical Summary\" root=\"1.3.6.1.4.1.19376.1.5.3.1.1.2\"></ns3:templateId>" +
            "    <ns3:templateId assigningAuthorityName=\"HL7 CCD\" extension=\"IMPL_CDAR2_LEVEL1\" root=\"2.16.840.1.113883.10\"></ns3:templateId>" +
            "    <ns3:id root=\"661aca54-c579-44a9-a0a6-165a51e7e00a\"></ns3:id>" +
            "    <ns3:code displayName=\"Summarization of episode note\" codeSystemName=\"LOINC\" codeSystem=\"2.16.840.1.113883.6.1\" code=\"34133-9\"></ns3:code>" +
            "    <ns3:title>Continuity of Care Document</ns3:title>" +
            "    <ns3:effectiveTime value=\"20170313141013+0000\"></ns3:effectiveTime>" +
            "    <ns3:confidentialityCode displayName=\"Normal\" codeSystem=\"2.16.840.1.113883.5.25\" code=\"N\"></ns3:confidentialityCode>" +
            "    <ns3:languageCode code=\"en\"></ns3:languageCode>" +
            "    <ns3:recordTarget>" +
            "        <ns3:patientRole>" +
            "            <ns3:id extension=\"AACJ-4531-1\" root=\"ORION\"></ns3:id>" +
            "            <ns3:addr>" +
            "                <ns3:city nullFlavor=\"UNK\"></ns3:city>" +
            "                <ns3:state nullFlavor=\"UNK\"></ns3:state>" +
            "                <ns3:postalCode nullFlavor=\"UNK\"></ns3:postalCode>" +
            "                <ns3:country nullFlavor=\"UNK\"></ns3:country>" +
            "            </ns3:addr>" +
            "            <ns3:telecom></ns3:telecom>" +
            "            <ns3:patient>" +
            "                <ns3:name>" +
            "                    <ns3:given>SCOOT </ns3:given>" +
            "                    <ns3:family>HOOT</ns3:family>" +
            "                </ns3:name>" +
            "                <ns3:administrativeGenderCode nullFlavor=\"NI\"></ns3:administrativeGenderCode>" +
            "                <ns3:birthTime nullFlavor=\"UNK\"></ns3:birthTime>" +
            "                <ns3:languageCommunication>" +
            "                    <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.83.2\"></ns3:templateId>" +
            "                    <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.2.1\"></ns3:templateId>" +
            "                    <ns3:languageCode nullFlavor=\"UNK\"></ns3:languageCode>" +
            "                </ns3:languageCommunication>" +
            "            </ns3:patient>" +
            "        </ns3:patientRole>" +
            "    </ns3:recordTarget>" +
            "    <ns3:author>" +
            "        <ns3:time value=\"20170313141013+0000\"></ns3:time>" +
            "        <ns3:assignedAuthor classCode=\"ASSIGNED\">" +
            "            <ns3:id extension=\"E8624F84-D2A8-45FD-8386-F783B7721CA0\" root=\"ConcertoAccount\"></ns3:id>" +
            "            <ns3:id extension=\"orion.jmcclure.clin\" root=\"clinical-portal-user\"></ns3:id>" +
            "            <ns3:addr>" +
            "                <ns3:city nullFlavor=\"UNK\"></ns3:city>" +
            "                <ns3:state nullFlavor=\"UNK\"></ns3:state>" +
            "                <ns3:postalCode nullFlavor=\"UNK\"></ns3:postalCode>" +
            "                <ns3:country nullFlavor=\"UNK\"></ns3:country>" +
            "            </ns3:addr>" +
            "            <ns3:telecom nullFlavor=\"NI\"></ns3:telecom>" +
            "            <ns3:assignedPerson determinerCode=\"INSTANCE\" classCode=\"PSN\">" +
            "                <ns3:name>" +
            "                    <ns3:prefix nullFlavor=\"NI\"></ns3:prefix>" +
            "                    <ns3:given>Jack</ns3:given>" +
            "                    <ns3:family>McClure</ns3:family>" +
            "                    <ns3:suffix nullFlavor=\"NI\"></ns3:suffix>" +
            "                </ns3:name>" +
            "            </ns3:assignedPerson>" +
            "        </ns3:assignedAuthor>" +
            "    </ns3:author>" +
            "    <ns3:author>" +
            "        <ns3:time value=\"20170313141013+0000\"></ns3:time>" +
            "        <ns3:assignedAuthor classCode=\"ASSIGNED\">" +
            "            <ns3:id root=\"2.16.840.1.113883.3.89.200.7\"></ns3:id>" +
            "            <ns3:addr>" +
            "                <ns3:streetAddressLine></ns3:streetAddressLine>" +
            "            </ns3:addr>" +
            "            <ns3:telecom></ns3:telecom>" +
            "            <ns3:assignedAuthoringDevice>" +
            "                <ns3:manufacturerModelName nullFlavor=\"NI\"></ns3:manufacturerModelName>" +
            "                <ns3:softwareName>Orion Health HIE 3.3.1</ns3:softwareName>" +
            "            </ns3:assignedAuthoringDevice>" +
            "            <ns3:representedOrganization classCode=\"ORG\">" +
            "                <ns3:name>Vanderbilt Health Affiliated Network</ns3:name>" +
            "                <ns3:telecom></ns3:telecom>" +
            "                <ns3:addr>" +
            "                    <ns3:streetAddressLine></ns3:streetAddressLine>" +
            "                </ns3:addr>" +
            "            </ns3:representedOrganization>" +
            "        </ns3:assignedAuthor>" +
            "    </ns3:author>" +
            "    <ns3:custodian>" +
            "        <ns3:assignedCustodian>" +
            "            <ns3:representedCustodianOrganization>" +
            "                <ns3:id root=\"2.16.840.1.113883.3.89.318.400.10\"></ns3:id>" +
            "                <ns3:name>Vanderbilt Health Affiliated Network</ns3:name>" +
            "                <ns3:telecom></ns3:telecom>" +
            "                <ns3:addr>" +
            "                    <ns3:streetAddressLine></ns3:streetAddressLine>" +
            "                </ns3:addr>" +
            "            </ns3:representedCustodianOrganization>" +
            "        </ns3:assignedCustodian>" +
            "    </ns3:custodian>" +
            "    <ns3:legalAuthenticator>" +
            "        <ns3:time value=\"20170313141013+0000\"></ns3:time>" +
            "        <ns3:signatureCode code=\"S\"></ns3:signatureCode>" +
            "        <ns3:assignedEntity>" +
            "            <ns3:id></ns3:id>" +
            "            <ns3:addr>" +
            "                <ns3:streetAddressLine></ns3:streetAddressLine>" +
            "            </ns3:addr>" +
            "            <ns3:telecom></ns3:telecom>" +
            "            <ns3:assignedPerson>" +
            "                <ns3:name/>" +
            "            </ns3:assignedPerson>" +
            "            <ns3:representedOrganization classCode=\"ORG\">" +
            "                <ns3:id root=\"2.16.840.1.113883.3.89.200.7\"></ns3:id>" +
            "                <ns3:name>Vanderbilt Health Affiliated Network</ns3:name>" +
            "                <ns3:telecom></ns3:telecom>" +
            "                <ns3:addr>" +
            "                    <ns3:streetAddressLine></ns3:streetAddressLine>" +
            "                </ns3:addr>" +
            "            </ns3:representedOrganization>" +
            "        </ns3:assignedEntity>" +
            "    </ns3:legalAuthenticator>" +
            "    <ns3:documentationOf>" +
            "        <ns3:serviceEvent classCode=\"PCPR\">" +
            "            <ns3:effectiveTime><ns3:low nullFlavor=\"UNK\"></ns3:low>" +
            "                <ns3:high nullFlavor=\"UNK\"></ns3:high>" +
            "            </ns3:effectiveTime>" +
            "        </ns3:serviceEvent>" +
            "    </ns3:documentationOf>" +
            "    <ns3:component>" +
            "        <ns3:structuredBody>" +
            "            <ns3:component>" +
            "                <ns3:section>" +
            "                    <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.83.102\"></ns3:templateId>" +
            "                    <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.3.13\"></ns3:templateId>" +
            "                    <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.2\"></ns3:templateId>" +
            "                    <ns3:id root=\"6a3fa39d-d625-48a4-a23b-832daea8ba72\"></ns3:id>" +
            "                    <ns3:code displayName=\"Allergies, adverse reactions, alerts\" codeSystemName=\"LOINC\" codeSystem=\"2.16.840.1.113883.6.1\" code=\"48765-2\"></ns3:code>" +
            "                    <ns3:title>Alerts, Allergies and Adverse Reactions</ns3:title>" +
            "                    <ns3:text>" +
            "                        <ns3:paragraph styleCode=\"xFilterMetadata\">FILTER APPLIED: All Known Active Allergies</ns3:paragraph>" +
            "                        <ns3:paragraph ID=\"NoAllergies\">Allergies Unknown</ns3:paragraph>" +
            "                    </ns3:text>" +
            "                    <ns3:entry typeCode=\"DRIV\">" +
            "                        <ns3:act moodCode=\"EVN\" classCode=\"ACT\">" +
            "                            <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.83.6\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.32.6\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.4.5.1\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.4.5.3\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.27\"></ns3:templateId>" +
            "                            <ns3:id nullFlavor=\"UNK\"></ns3:id>" +
            "                            <ns3:code nullFlavor=\"NA\"></ns3:code>" +
            "                            <ns3:statusCode code=\"completed\"></ns3:statusCode>" +
            "                            <ns3:effectiveTime><ns3:low nullFlavor=\"UNK\"></ns3:low>" +
            "                                <ns3:high nullFlavor=\"UNK\"></ns3:high>" +
            "                            </ns3:effectiveTime>" +
            "                            <ns3:entryRelationship inversionInd=\"false\" typeCode=\"SUBJ\">" +
            "                                <ns3:observation moodCode=\"EVN\" classCode=\"OBS\" nullFlavor=\"UNK\">" +
            "                                    <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.28\"></ns3:templateId>" +
            "                                    <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.18\"></ns3:templateId>" +
            "                                    <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.4.5\"></ns3:templateId>" +
            "                                    <ns3:templateId assigningAuthorityName=\"IHE PCC\" extension=\"Allergy\" root=\"1.3.6.1.4.1.19376.1.5.3.1.4.6\"></ns3:templateId>" +
            "                                    <ns3:id nullFlavor=\"NI\"></ns3:id>" +
            "                                    <ns3:code displayName=\"Propensity to adverse reactions (disorder)\" codeSystemName=\"SNOMED-CT\" codeSystem=\"2.16.840.1.113883.6.96\" code=\"420134006\"></ns3:code>" +
            "                                    <ns3:text><ns3:reference value=\"#NoAllergies\"></ns3:reference>" +
            "                                    </ns3:text>" +
            "                                    <ns3:statusCode code=\"completed\"></ns3:statusCode>" +
            "                                    <ns3:effectiveTime><ns3:low nullFlavor=\"UNK\"></ns3:low>" +
            "                                    </ns3:effectiveTime>" +
            "                                    <ns3:value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CD\" nullFlavor=\"NA\"></ns3:value>" +
            "                                </ns3:observation>" +
            "                            </ns3:entryRelationship>" +
            "                        </ns3:act>" +
            "                    </ns3:entry>" +
            "                </ns3:section>" +
            "            </ns3:component>" +
            "            <ns3:component>" +
            "                <ns3:section>" +
            "                    <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.83.103\"></ns3:templateId>" +
            "                    <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.3.6\"></ns3:templateId>" +
            "                    <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.11\"></ns3:templateId>" +
            "                    <ns3:id root=\"0ac4dc4d-6043-4462-8edf-33f9109f73eb\"></ns3:id>" +
            "                    <ns3:code displayName=\"PROBLEM LIST\" codeSystemName=\"LOINC\" codeSystem=\"2.16.840.1.113883.6.1\" code=\"11450-4\"></ns3:code>" +
            "                    <ns3:title>Problems</ns3:title>" +
            "                    <ns3:text>" +
            "                        <ns3:paragraph styleCode=\"xFilterMetadata\">FILTER APPLIED: Only known Active and Resolved Problems with Onset Date within the last 3 year(s)</ns3:paragraph>" +
            "                        <ns3:paragraph ID=\"NoProblem\">Medical History Unknown</ns3:paragraph>" +
            "                    </ns3:text>" +
            "                    <ns3:entry typeCode=\"DRIV\">" +
            "                        <ns3:act moodCode=\"EVN\" classCode=\"ACT\">" +
            "                            <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.83.7\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.32.7\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.4.5.2\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.4.5.1\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.27\"></ns3:templateId>" +
            "                            <ns3:id nullFlavor=\"UNK\"></ns3:id>" +
            "                            <ns3:code nullFlavor=\"NA\"></ns3:code>" +
            "                            <ns3:statusCode code=\"completed\"></ns3:statusCode>" +
            "                            <ns3:effectiveTime><ns3:low nullFlavor=\"UNK\"></ns3:low>" +
            "                                <ns3:high nullFlavor=\"UNK\"></ns3:high>" +
            "                            </ns3:effectiveTime>" +
            "                            <ns3:entryRelationship inversionInd=\"false\" typeCode=\"SUBJ\">" +
            "                                <ns3:observation moodCode=\"EVN\" classCode=\"OBS\" nullFlavor=\"UNK\">" +
            "                                    <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.28\"></ns3:templateId>" +
            "                                    <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.4.5\"></ns3:templateId>" +
            "                                    <ns3:id nullFlavor=\"NI\"></ns3:id>" +
            "                                    <ns3:code></ns3:code>" +
            "                                    <ns3:text><ns3:reference value=\"#NoProblem\"></ns3:reference>" +
            "                                    </ns3:text>" +
            "                                    <ns3:statusCode code=\"completed\"></ns3:statusCode>" +
            "                                    <ns3:effectiveTime><ns3:low nullFlavor=\"UNK\"></ns3:low>" +
            "                                    </ns3:effectiveTime>" +
            "                                    <ns3:value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CD\" displayName=\"Medical History Unknown\" codeSystemName=\"SNOMED-CT\" codeSystem=\"2.16.840.1.113883.6.96\" code=\"396782006\"></ns3:value>" +
            "                                </ns3:observation>" +
            "                            </ns3:entryRelationship>" +
            "                        </ns3:act>" +
            "                    </ns3:entry>" +
            "                </ns3:section>" +
            "            </ns3:component>" +
            "            <ns3:component>" +
            "                <ns3:section>" +
            "                    <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.83.112\"></ns3:templateId>" +
            "                    <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.3.19\"></ns3:templateId>" +
            "                    <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.8\"></ns3:templateId>" +
            "                    <ns3:id root=\"acba52e3-e0bd-4f76-aef3-ae40bd28fc41\"></ns3:id>" +
            "                    <ns3:code displayName=\"HISTORY OF MEDICATION USE\" codeSystemName=\"LOINC\" codeSystem=\"2.16.840.1.113883.6.1\" code=\"10160-0\"></ns3:code>" +
            "                    <ns3:title>Medications</ns3:title>" +
            "                    <ns3:text>" +
            "                        <ns3:paragraph styleCode=\"xFilterMetadata\">FILTER APPLIED: Only known Active Medications with Stop Date within the last 0 day(s)</ns3:paragraph>" +
            "                        <ns3:paragraph ID=\"NoMedication\">Drug Treatment Unknown</ns3:paragraph>" +
            "                    </ns3:text>" +
            "                    <ns3:entry typeCode=\"DRIV\">" +
            "                        <ns3:substanceAdministration moodCode=\"INT\" classCode=\"SBADM\">" +
            "                            <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.83.8\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.32.8\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"IHE PCC\" extension=\"medication\" root=\"1.3.6.1.4.1.19376.1.5.3.1.4.7\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.4.7.1\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.24\"></ns3:templateId>" +
            "                            <ns3:id root=\"05b41e69-ce49-40be-95c1-685e92ff1643\"></ns3:id>" +
            "                            <ns3:code displayName=\"Drug Treatment Unknown\" codeSystemName=\"SNOMED-CT\" codeSystem=\"2.16.840.1.113883.6.96\" code=\"182904002\"></ns3:code>" +
            "                            <ns3:text><ns3:reference value=\"#NoMedication\"></ns3:reference>" +
            "                            </ns3:text>" +
            "                            <ns3:statusCode code=\"completed\"></ns3:statusCode>" +
            "                            <ns3:routeCode nullFlavor=\"NI\"></ns3:routeCode>" +
            "                            <ns3:consumable>" +
            "                                <ns3:manufacturedProduct>" +
            "                                    <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.83.8.2\"></ns3:templateId>" +
            "                                    <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.4.7.2\"></ns3:templateId>" +
            "                                    <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.53\"></ns3:templateId>" +
            "                                    <ns3:manufacturedMaterial>" +
            "                                        <ns3:code nullFlavor=\"NA\"><ns3:originalText><ns3:reference nullFlavor=\"NI\"></ns3:reference>" +
            "                                        </ns3:originalText>" +
            "                                            <ns3:translation nullFlavor=\"NI\"></ns3:translation>" +
            "                                        </ns3:code>" +
            "                                    </ns3:manufacturedMaterial>" +
            "                                </ns3:manufacturedProduct>" +
            "                            </ns3:consumable>" +
            "                        </ns3:substanceAdministration>" +
            "                    </ns3:entry>" +
            "                </ns3:section>" +
            "            </ns3:component>" +
            "            <ns3:component>" +
            "                <ns3:section>" +
            "                    <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.3.11\"></ns3:templateId>" +
            "                    <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.12\"></ns3:templateId>" +
            "                    <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.83.108\"></ns3:templateId>" +
            "                    <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.3.12\"></ns3:templateId>" +
            "                    <ns3:id root=\"4c80b07b-1375-47ab-94ef-38e66188d4cd\"></ns3:id>" +
            "                    <ns3:code displayName=\"HISTORY OF PROCEDURES\" codeSystemName=\"LOINC\" codeSystem=\"2.16.840.1.113883.6.1\" code=\"47519-4\"></ns3:code>" +
            "                    <ns3:title>Procedures</ns3:title>" +
            "                    <ns3:text>" +
            "                        <ns3:paragraph styleCode=\"xFilterMetadata\">FILTER APPLIED: Only known Procedures with Onset Date within the last 3 year(s)</ns3:paragraph>" +
            "                        <ns3:paragraph ID=\"NoProcedure\">No Known Procedure</ns3:paragraph>" +
            "                    </ns3:text>" +
            "                    <ns3:entry contextConductionInd=\"true\" typeCode=\"DRIV\">" +
            "                        <ns3:procedure moodCode=\"EVN\" classCode=\"PROC\">" +
            "                            <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.83.17\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.29\"></ns3:templateId>" +
            "                            <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.4.19\"></ns3:templateId>" +
            "                            <ns3:id nullFlavor=\"NI\"></ns3:id>" +
            "                            <ns3:code nullFlavor=\"NI\"><ns3:originalText><ns3:reference value=\"#NoProcedure\"></ns3:reference>" +
            "                            </ns3:originalText>" +
            "                            </ns3:code>" +
            "                            <ns3:text><ns3:reference value=\"#NoProcedure\"></ns3:reference>" +
            "                            </ns3:text>" +
            "                            <ns3:statusCode code=\"completed\"></ns3:statusCode>" +
            "                        </ns3:procedure>" +
            "                    </ns3:entry>" +
            "                </ns3:section>" +
            "            </ns3:component>" +
            "            <ns3:component>" +
            "                <ns3:section>" +
            "                    <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.83.122\"></ns3:templateId>" +
            "                    <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.3.28\"></ns3:templateId>" +
            "                    <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.14\"></ns3:templateId>" +
            "                    <ns3:id root=\"07b71e2a-bd89-444a-99cd-0dd8123b4944\"></ns3:id>" +
            "                    <ns3:code displayName=\"Relevant diagnostic tests and/or laboratory data\" codeSystemName=\"LOINC\" codeSystem=\"2.16.840.1.113883.6.1\" code=\"30954-2\"></ns3:code>" +
            "                    <ns3:title>Results</ns3:title>" +
            "                    <ns3:text>" +
            "                        <ns3:paragraph styleCode=\"xFilterMetadata\">FILTER APPLIED: Only known Results with Collection Date within the last 6 month(s)</ns3:paragraph>" +
            "                        <ns3:paragraph ID=\"NoResults\">No Known Results</ns3:paragraph>" +
            "                    </ns3:text>" +
            "                    <ns3:entry typeCode=\"DRIV\">" +
            "                        <ns3:organizer moodCode=\"EVN\" classCode=\"CLUSTER\">" +
            "                            <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.32\"></ns3:templateId>" +
            "                            <ns3:id nullFlavor=\"NI\"></ns3:id>" +
            "                            <ns3:code nullFlavor=\"NI\"></ns3:code>" +
            "                            <ns3:statusCode code=\"completed\"></ns3:statusCode>" +
            "                            <ns3:effectiveTime><ns3:low nullFlavor=\"UNK\"></ns3:low>" +
            "                                <ns3:high nullFlavor=\"UNK\"></ns3:high>" +
            "                            </ns3:effectiveTime>" +
            "                            <ns3:component>" +
            "                                <ns3:observation negationInd=\"true\" moodCode=\"EVN\" classCode=\"OBS\">" +
            "                                    <ns3:templateId assigningAuthorityName=\"HITSP/C83\" root=\"2.16.840.1.113883.3.88.11.83.15.1\"></ns3:templateId>" +
            "                                    <ns3:templateId assigningAuthorityName=\"IHE PCC\" root=\"1.3.6.1.4.1.19376.1.5.3.1.4.13\"></ns3:templateId>" +
            "                                    <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.31\"></ns3:templateId>" +
            "                                    <ns3:id nullFlavor=\"NI\"></ns3:id>" +
            "                                    <ns3:code displayName=\"Laboratory Studies\" codeSystemName=\"LOINC\" codeSystem=\"2.16.840.1.113883.6.1\" code=\"26436-6\"></ns3:code>" +
            "                                    <ns3:text><ns3:reference value=\"#NoResults\"></ns3:reference>" +
            "                                    </ns3:text>" +
            "                                    <ns3:statusCode code=\"completed\"></ns3:statusCode>" +
            "                                    <ns3:effectiveTime nullFlavor=\"UNK\"></ns3:effectiveTime>" +
            "                                    <ns3:value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CD\" nullFlavor=\"NI\"></ns3:value>" +
            "                                </ns3:observation>" +
            "                            </ns3:component>" +
            "                        </ns3:organizer>" +
            "                    </ns3:entry>" +
            "                </ns3:section>" +
            "            </ns3:component>" +
            "            <ns3:component>" +
            "                <ns3:section>" +
            "                    <ns3:templateId assigningAuthorityName=\"HL7 CCD\" root=\"2.16.840.1.113883.10.20.1.3\"></ns3:templateId>" +
            "                    <ns3:id root=\"0c35feac-1336-4db6-89e0-0e2dc790207c\"></ns3:id>" +
            "                    <ns3:code displayName=\"HISTORY OF ENCOUNTERS\" codeSystemName=\"LOINC\" codeSystem=\"2.16.840.1.113883.6.1\" code=\"46240-8\"></ns3:code>" +
            "                    <ns3:title>Encounters</ns3:title>" +
            "                    <ns3:text>" +
            "                        <ns3:paragraph styleCode=\"xFilterMetadata\">FILTER APPLIED: Only known Encounters with Admission Date within the last 3 year(s)</ns3:paragraph>" +
            "                        <ns3:paragraph ID=\"NoEncounter\">No Known Encounter</ns3:paragraph>" +
            "                    </ns3:text>" +
            "                </ns3:section>" +
            "            </ns3:component>" +
            "        </ns3:structuredBody>" +
            "    </ns3:component>" +
            "</ns3:ClinicalDocument>";
  }

  private String sampleMessage_PatientBody()
  {
    return "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\">\n" +
            "<html xmlns:n1=\"urn:hl7-org:v3\">\n" +
            "<head>\n" +
            "<META http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n" +
            "<link href=\"https://www.orionhealth.com/software/ccd/style/skin_v2.4.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
            "<!--Do NOT edit this HTML directly, it was generated via an XSLt transformation from the original release 2 CDA Document.-->\n" +
            "<title>Continuity of Care Document</title>\n" +
            "</head>\n" +
            "<!--\n" +
            "\t\t\t\tCopyright 2011 Orion Health group of companies. All Rights Reserved.  \t\t\t\n" +
            "\t\t\t-->\n" +
            "<body>\n" +
            "<h2 align=\"center\">Continuity of Care Document</h2>\n" +
            "<p align=\"center\">\n" +
            "<b>Created On: </b>March 13, 2017</p>\n" +
            "<hr>\n" +
            "<div class=\"header\">\n" +
            "<div class=\"demographics sticky\">\n" +
            "<div class=\"bl\">\n" +
            "<div class=\"br\">\n" +
            "<div class=\"tr\">\n" +
            "<div class=\"person-name\">HOOT, SCOOT </div>\n" +
            "<div class=\"sex-age\">\n" +
            "<span id=\"calculatedAge\"></span>\n" +
            "</div>\n" +
            "<div class=\"id\">AACJ-4531-1<span class=\"label\">\n" +
            "\t\t\t\t\t\t\t\t\t\t\t(ORION)\n" +
            "\t\t\t\t\t\t\t\t\t\t</span>\n" +
            "<br>\n" +
            "<br>\n" +
            "</div>\n" +
            "</div>\n" +
            "</div>\n" +
            "</div>\n" +
            "</div>\n" +
            "</div>\n" +
            "<div class=\"section\">\n" +
            "<b>Electronically generated</b><b> by </b>Vanderbilt Health Affiliated Network<b> on </b>March 13, 2017</div>\n" +
            "<div class=\"section\">\n" +
            "<span><a name=\"N65879\" href=\"#toc\">\n" +
            "<h2>Alerts, Allergies and Adverse Reactions</h2>\n" +
            "</a></span>\n" +
            "</div>                                                \n" +
            "<p>Allergies Unknown</p>                    \n" +
            "<div class=\"section\">\n" +
            "<span><a name=\"N66021\" href=\"#toc\">\n" +
            "<h2>Problems</h2>\n" +
            "</a></span>\n" +
            "</div>                                                \n" +
            "<p>Medical History Unknown</p>                    \n" +
            "<div class=\"section\">\n" +
            "<span><a name=\"N66153\" href=\"#toc\">\n" +
            "<h2>Medications</h2>\n" +
            "</a></span>\n" +
            "</div>                                                \n" +
            "<p>Drug Treatment Unknown</p>                    \n" +
            "<div class=\"section\">\n" +
            "<span><a name=\"N66280\" href=\"#toc\">\n" +
            "<h2>Procedures</h2>\n" +
            "</a></span>\n" +
            "</div>                                                \n" +
            "<p>No Known Procedure</p>                    \n" +
            "<div class=\"section\">\n" +
            "<span><a name=\"N66362\" href=\"#toc\">\n" +
            "<h2>Results</h2>\n" +
            "</a></span>\n" +
            "</div>                                                \n" +
            "<p>No Known Results</p>                    \n" +
            "<div class=\"section\">\n" +
            "<span><a name=\"N66471\" href=\"#toc\">\n" +
            "<h2>Encounters</h2>\n" +
            "</a></span>\n" +
            "</div>                                                \n" +
            "<p>No Known Encounter</p>                    \n" +
            "</body>\n" +
            "<script language=\"JavaScript\" type=\"text/javascript\">\n" +
            "\t\t\t\tvar today = new Date();\n" +
            "\t\t\t\tvar age = 0;\n" +
            "\t\t\t\tvar xmlDob  = '';\n" +
            "\t\t\t\tif (xmlDob.length > 0) {\n" +
            "\t\t\t\t\tvar dob = parseInt(xmlDob.substring(0, 8));\n" +
            "\t\t\t\t\t//Script return month from 0 to 11 not from 1 to 12. Thats why the month has been incremented by 1.\n" +
            "\t\t\t\t\tvar todayMonth = (today.getMonth() + 1) + '';\n" +
            "\t\t\t\t\tif (todayMonth.length == 1) {\n" +
            "\t\t\t\t\t\ttodayMonth = \"0\" + todayMonth;\n" +
            "\t\t\t\t\t}\n" +
            "\t\t\t\t\tvar todayDate = today.getDate() + '';\n" +
            "\t\t\t\t\tif (todayDate.length == 1) {\n" +
            "\t\t\t\t\t\ttodayDate = \"0\" + todayDate;\n" +
            "\t\t\t\t\t}\n" +
            "\t\t\t\t\tvar today = parseInt('' + today.getFullYear() + todayMonth + todayDate);\n" +
            "\t\t\t\t\tage = Math.floor((today - dob) / 10000);\n" +
            "\t\t\t\t}\n" +
            "\t\t\t\tvar ageWithSeparator='';\n" +
            "\n" +
            "\t\t\t\tvar gender = '';\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t// The forward slash depends on gender and age i.e age is greater than one year.\n" +
            "\t\t\t\tif (gender.length != 0 && age != 0) {\n" +
            "\t\t\t\t\tageWithSeparator = \"/\";\n" +
            "\t\t\t\t}\n" +
            "\t\t\t\tif (age != 0) {\n" +
            "\t\t\t\t\tageWithSeparator = ageWithSeparator + age + 'y';\n" +
            "\t\t\t\t}\n" +
            "\t\t\t\t//First inner condition: When the Gender is present and the DOB is greater than or equal to one year\n" +
            "\t\t\t\t//Second inner condition: When the Gender is not present and the DOB greater than or equal to one year\n" +
            "\t\t\t\tif ((xmlDob.length != 0 && gender.length != 0) || age != 0) {\n" +
            "\t\t\t\t\tageWithSeparator = ageWithSeparator + ', ';\n" +
            "\t\t\t\t}\n" +
            "\t\t\t\tdocument.getElementById('calculatedAge').innerHTML = ageWithSeparator;\n" +
            "\t\t\t</script>\n" +
            "</html>\n";
  }


  private String sampleMessage2()
  {
    return
        "<env:Envelope xmlns:a=\"http://www.w3.org/2005/08/addressing\" xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">" +
            "  <env:Header>" +
            "    <a:RelatesTo>urn:uuid:9521242710204A53951493140978158</a:RelatesTo>" +
            "    <a:Action env:mustUnderstand=\"1\">urn:ihe:iti:2007:RetrieveDocumentSetResponse</a:Action>" +
            "  </env:Header>" +
            "  <env:Body>" +
            "    <xds-b:RetrieveDocumentSetResponse xmlns:lcm=\"urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0\" xmlns:query=\"urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0\" xmlns:rim=\"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0\" xmlns:rs=\"urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0\" xmlns:xds-b=\"urn:ihe:iti:xds-b:2007\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "      <rs:RegistryResponse status=\"urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success\"/>" +
            "      <xds-b:DocumentResponse>" +
            "        <xds-b:RepositoryUniqueId>2.16.840.1.113883.3.89.318.400.10.2.1</xds-b:RepositoryUniqueId>" +
            "        <xds-b:DocumentUniqueId>002102283&amp;VUMC&amp;550e8400-e29b-41d4-a716-446658440000</xds-b:DocumentUniqueId>" +
            "        <xds-b:mimeType>text/xml</xds-b:mimeType>" +
            "        <xds-b:Document>PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/Pgo8bnMzOkNsaW5pY2FsRG9jdW1lbnQgeG1sbnM6bnMyPSJ1cm46aGw3LW9yZzpzZHRjIiB4bWxuczpuczM9InVybjpobDctb3JnOnYzIiB4bWxuczpuczQ9InVybjpnb3Y6aGhzOmZoYTpuaGluYzpjb21tb246bmhpbmNjb21tb24iIHhtbG5zOm5zNT0iaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvd3MvMjAwNC8wOC9hZGRyZXNzaW5nIiB4bWxuczpuczY9InVybjpnb3Y6aGhzOmZoYTpuaGluYzpjb21tb246cGF0aWVudGNvcnJlbGF0aW9uZmFjYWRlIiB4bWxucz0idXJuOmhsNy1vcmc6djMiPgogICAgPG5zMzpyZWFsbUNvZGUgY29kZT0iVVMiLz4KICAgIDxuczM6dHlwZUlkIGV4dGVuc2lvbj0iUE9DRF9IRDAwMDA0MCIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMS4zIi8+CiAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSEw3IENDRCIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMTAuMjAuMSIvPgogICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IkhMNy9DRFQgSGVhZGVyIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4xMC4yMC4zIi8+CiAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSElUU1AvQzMyIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4zLjg4LjExLjMyLjEiLz4KICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJJSEUgUENDIiByb290PSIxLjMuNi4xLjQuMS4xOTM3Ni4xLjUuMy4xLjEuMSIvPgogICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IklIRSBQQ0MiIGV4dGVuc2lvbj0iWFBIUiBTdW1tYXJ5IiByb290PSIxLjMuNi4xLjQuMS4xOTM3Ni4xLjUuMy4xLjEuNSIvPgogICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IklIRSBQQ0MiIGV4dGVuc2lvbj0iUmVmZXJyYWwgU3VtbWFyeSIgcm9vdD0iMS4zLjYuMS40LjEuMTkzNzYuMS41LjMuMS4xLjMiLz4KICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJJSEUgUENDIiBleHRlbnNpb249Ik1lZGljYWwgU3VtbWFyeSIgcm9vdD0iMS4zLjYuMS40LjEuMTkzNzYuMS41LjMuMS4xLjIiLz4KICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJITDcgQ0NEIiBleHRlbnNpb249IklNUExfQ0RBUjJfTEVWRUwxIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4xMCIvPgogICAgPG5zMzppZCByb290PSJkZjFkMWE2ZS0zOWIxLTQ1NjctYTA4ZC0yNTExN2UzNmFiOWYiLz4KICAgIDxuczM6Y29kZSBkaXNwbGF5TmFtZT0iU3VtbWFyaXphdGlvbiBvZiBlcGlzb2RlIG5vdGUiIGNvZGVTeXN0ZW1OYW1lPSJMT0lOQyIgY29kZVN5c3RlbT0iMi4xNi44NDAuMS4xMTM4ODMuNi4xIiBjb2RlPSIzNDEzMy05Ii8+CiAgICA8bnMzOnRpdGxlPkNvbnRpbnVpdHkgb2YgQ2FyZSBEb2N1bWVudDwvbnMzOnRpdGxlPgogICAgPG5zMzplZmZlY3RpdmVUaW1lIHZhbHVlPSIyMDE3MDQyNTE3MTk1NCswMDAwIi8+CiAgICA8bnMzOmNvbmZpZGVudGlhbGl0eUNvZGUgZGlzcGxheU5hbWU9Ik5vcm1hbCIgY29kZVN5c3RlbT0iMi4xNi44NDAuMS4xMTM4ODMuNS4yNSIgY29kZT0iTiIvPgogICAgPG5zMzpsYW5ndWFnZUNvZGUgY29kZT0iZW4iLz4KICAgIDxuczM6cmVjb3JkVGFyZ2V0PgogICAgICAgIDxuczM6cGF0aWVudFJvbGU+CiAgICAgICAgICAgIDxuczM6aWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iVmFuZGVyYmlsdCBVbml2ZXJzaXR5IE1lZGljYWwgQ2VudGVyIiBleHRlbnNpb249IjAwMjEwMjI4MyIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMy4xNzgyIi8+CiAgICAgICAgICAgIDxuczM6YWRkcj4KICAgICAgICAgICAgICAgIDxuczM6c3RyZWV0QWRkcmVzc0xpbmU+MTEgSU5XT09EIFJEPC9uczM6c3RyZWV0QWRkcmVzc0xpbmU+CiAgICAgICAgICAgICAgICA8bnMzOmNpdHk+U0hPUlQgSElMTFM8L25zMzpjaXR5PgogICAgICAgICAgICAgICAgPG5zMzpzdGF0ZT5OSjwvbnMzOnN0YXRlPgogICAgICAgICAgICAgICAgPG5zMzpwb3N0YWxDb2RlPjA3MDc4PC9uczM6cG9zdGFsQ29kZT4KICAgICAgICAgICAgICAgIDxuczM6Y291bnRyeT5VUzwvbnMzOmNvdW50cnk+CiAgICAgICAgICAgIDwvbnMzOmFkZHI+CiAgICAgICAgICAgIDxuczM6dGVsZWNvbSB2YWx1ZT0iKDk3MykgOTEyLTQ0MzkiLz4KICAgICAgICAgICAgPG5zMzpwYXRpZW50PgogICAgICAgICAgICAgICAgPG5zMzpuYW1lPgogICAgICAgICAgICAgICAgICAgIDxuczM6Z2l2ZW4+Sk9BTklFIDwvbnMzOmdpdmVuPgogICAgICAgICAgICAgICAgICAgIDxuczM6ZmFtaWx5PkNBREVOQ0U8L25zMzpmYW1pbHk+CiAgICAgICAgICAgICAgICA8L25zMzpuYW1lPgogICAgICAgICAgICAgICAgPG5zMzphZG1pbmlzdHJhdGl2ZUdlbmRlckNvZGUgZGlzcGxheU5hbWU9IkYiIGNvZGVTeXN0ZW09IjIuMTYuODQwLjEuMTEzODgzLjUuMSIgY29kZT0iRiIvPgogICAgICAgICAgICAgICAgPG5zMzpiaXJ0aFRpbWUgdmFsdWU9IjE5NzUwNDI1Ii8+CiAgICAgICAgICAgICAgICA8bnMzOmxhbmd1YWdlQ29tbXVuaWNhdGlvbj4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSElUU1AvQzgzIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4zLjg4LjExLjgzLjIiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSUhFIFBDQyIgcm9vdD0iMS4zLjYuMS40LjEuMTkzNzYuMS41LjMuMS4yLjEiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOmxhbmd1YWdlQ29kZSBudWxsRmxhdm9yPSJVTksiLz4KICAgICAgICAgICAgICAgIDwvbnMzOmxhbmd1YWdlQ29tbXVuaWNhdGlvbj4KICAgICAgICAgICAgPC9uczM6cGF0aWVudD4KICAgICAgICA8L25zMzpwYXRpZW50Um9sZT4KICAgIDwvbnMzOnJlY29yZFRhcmdldD4KICAgIDxuczM6YXV0aG9yPgogICAgICAgIDxuczM6dGltZSB2YWx1ZT0iMjAxNzA0MjUxNzE5NTQrMDAwMCIvPgogICAgICAgIDxuczM6YXNzaWduZWRBdXRob3IgY2xhc3NDb2RlPSJBU1NJR05FRCI+CiAgICAgICAgICAgIDxuczM6aWQgbnVsbEZsYXZvcj0iTkEiLz4KICAgICAgICAgICAgPG5zMzphZGRyLz4KICAgICAgICAgICAgPG5zMzp0ZWxlY29tIG51bGxGbGF2b3I9Ik5JIi8+CiAgICAgICAgICAgIDxuczM6YXNzaWduZWRQZXJzb24gZGV0ZXJtaW5lckNvZGU9IklOU1RBTkNFIiBjbGFzc0NvZGU9IlBTTiI+CiAgICAgICAgICAgICAgICA8bnMzOm5hbWU+CiAgICAgICAgICAgICAgICAgICAgPG5zMzpwcmVmaXggbnVsbEZsYXZvcj0iTkkiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOmdpdmVuIG51bGxGbGF2b3I9Ik5JIi8+CiAgICAgICAgICAgICAgICAgICAgPG5zMzpmYW1pbHkgbnVsbEZsYXZvcj0iTkkiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOnN1ZmZpeCBudWxsRmxhdm9yPSJOSSIvPgogICAgICAgICAgICAgICAgPC9uczM6bmFtZT4KICAgICAgICAgICAgPC9uczM6YXNzaWduZWRQZXJzb24+CiAgICAgICAgPC9uczM6YXNzaWduZWRBdXRob3I+CiAgICA8L25zMzphdXRob3I+CiAgICA8bnMzOmF1dGhvcj4KICAgICAgICA8bnMzOnRpbWUgdmFsdWU9IjIwMTcwNDI1MTcxOTU0KzAwMDAiLz4KICAgICAgICA8bnMzOmFzc2lnbmVkQXV0aG9yIGNsYXNzQ29kZT0iQVNTSUdORUQiPgogICAgICAgICAgICA8bnMzOmlkIHJvb3Q9IjIuMTYuODQwLjEuMTEzODgzLjMuODkuMjAwLjciLz4KICAgICAgICAgICAgPG5zMzphZGRyPgogICAgICAgICAgICAgICAgPG5zMzpzdHJlZXRBZGRyZXNzTGluZS8+CiAgICAgICAgICAgIDwvbnMzOmFkZHI+CiAgICAgICAgICAgIDxuczM6dGVsZWNvbS8+CiAgICAgICAgICAgIDxuczM6YXNzaWduZWRBdXRob3JpbmdEZXZpY2U+CiAgICAgICAgICAgICAgICA8bnMzOm1hbnVmYWN0dXJlck1vZGVsTmFtZSBudWxsRmxhdm9yPSJOSSIvPgogICAgICAgICAgICAgICAgPG5zMzpzb2Z0d2FyZU5hbWU+T3Jpb24gSGVhbHRoIEhJRSAzLjMuMTwvbnMzOnNvZnR3YXJlTmFtZT4KICAgICAgICAgICAgPC9uczM6YXNzaWduZWRBdXRob3JpbmdEZXZpY2U+CiAgICAgICAgICAgIDxuczM6cmVwcmVzZW50ZWRPcmdhbml6YXRpb24gY2xhc3NDb2RlPSJPUkciPgogICAgICAgICAgICAgICAgPG5zMzpuYW1lPlZhbmRlcmJpbHQgSGVhbHRoIEFmZmlsaWF0ZWQgTmV0d29yazwvbnMzOm5hbWU+CiAgICAgICAgICAgICAgICA8bnMzOnRlbGVjb20vPgogICAgICAgICAgICAgICAgPG5zMzphZGRyPgogICAgICAgICAgICAgICAgICAgIDxuczM6c3RyZWV0QWRkcmVzc0xpbmUvPgogICAgICAgICAgICAgICAgPC9uczM6YWRkcj4KICAgICAgICAgICAgPC9uczM6cmVwcmVzZW50ZWRPcmdhbml6YXRpb24+CiAgICAgICAgPC9uczM6YXNzaWduZWRBdXRob3I+CiAgICA8L25zMzphdXRob3I+CiAgICA8bnMzOmN1c3RvZGlhbj4KICAgICAgICA8bnMzOmFzc2lnbmVkQ3VzdG9kaWFuPgogICAgICAgICAgICA8bnMzOnJlcHJlc2VudGVkQ3VzdG9kaWFuT3JnYW5pemF0aW9uPgogICAgICAgICAgICAgICAgPG5zMzppZCByb290PSIyLjE2Ljg0MC4xLjExMzg4My4zLjg5LjMxOC40MDAuMTAiLz4KICAgICAgICAgICAgICAgIDxuczM6bmFtZT5WYW5kZXJiaWx0IEhlYWx0aCBBZmZpbGlhdGVkIE5ldHdvcms8L25zMzpuYW1lPgogICAgICAgICAgICAgICAgPG5zMzp0ZWxlY29tLz4KICAgICAgICAgICAgICAgIDxuczM6YWRkcj4KICAgICAgICAgICAgICAgICAgICA8bnMzOnN0cmVldEFkZHJlc3NMaW5lLz4KICAgICAgICAgICAgICAgIDwvbnMzOmFkZHI+CiAgICAgICAgICAgIDwvbnMzOnJlcHJlc2VudGVkQ3VzdG9kaWFuT3JnYW5pemF0aW9uPgogICAgICAgIDwvbnMzOmFzc2lnbmVkQ3VzdG9kaWFuPgogICAgPC9uczM6Y3VzdG9kaWFuPgogICAgPG5zMzpsZWdhbEF1dGhlbnRpY2F0b3I+CiAgICAgICAgPG5zMzp0aW1lIHZhbHVlPSIyMDE3MDQyNTE3MTk1NCswMDAwIi8+CiAgICAgICAgPG5zMzpzaWduYXR1cmVDb2RlIGNvZGU9IlMiLz4KICAgICAgICA8bnMzOmFzc2lnbmVkRW50aXR5PgogICAgICAgICAgICA8bnMzOmlkLz4KICAgICAgICAgICAgPG5zMzphZGRyPgogICAgICAgICAgICAgICAgPG5zMzpzdHJlZXRBZGRyZXNzTGluZS8+CiAgICAgICAgICAgIDwvbnMzOmFkZHI+CiAgICAgICAgICAgIDxuczM6dGVsZWNvbS8+CiAgICAgICAgICAgIDxuczM6YXNzaWduZWRQZXJzb24+CiAgICAgICAgICAgICAgICA8bnMzOm5hbWUvPgogICAgICAgICAgICA8L25zMzphc3NpZ25lZFBlcnNvbj4KICAgICAgICAgICAgPG5zMzpyZXByZXNlbnRlZE9yZ2FuaXphdGlvbiBjbGFzc0NvZGU9Ik9SRyI+CiAgICAgICAgICAgICAgICA8bnMzOmlkIHJvb3Q9IjIuMTYuODQwLjEuMTEzODgzLjMuODkuMjAwLjciLz4KICAgICAgICAgICAgICAgIDxuczM6bmFtZT5WYW5kZXJiaWx0IEhlYWx0aCBBZmZpbGlhdGVkIE5ldHdvcms8L25zMzpuYW1lPgogICAgICAgICAgICAgICAgPG5zMzp0ZWxlY29tLz4KICAgICAgICAgICAgICAgIDxuczM6YWRkcj4KICAgICAgICAgICAgICAgICAgICA8bnMzOnN0cmVldEFkZHJlc3NMaW5lLz4KICAgICAgICAgICAgICAgIDwvbnMzOmFkZHI+CiAgICAgICAgICAgIDwvbnMzOnJlcHJlc2VudGVkT3JnYW5pemF0aW9uPgogICAgICAgIDwvbnMzOmFzc2lnbmVkRW50aXR5PgogICAgPC9uczM6bGVnYWxBdXRoZW50aWNhdG9yPgogICAgPG5zMzpkb2N1bWVudGF0aW9uT2Y+CiAgICAgICAgPG5zMzpzZXJ2aWNlRXZlbnQgY2xhc3NDb2RlPSJQQ1BSIj4KICAgICAgICAgICAgPG5zMzplZmZlY3RpdmVUaW1lPgogICAgICAgICAgICAgICAgPG5zMzpsb3cgbnVsbEZsYXZvcj0iVU5LIi8+CiAgICAgICAgICAgICAgICA8bnMzOmhpZ2ggbnVsbEZsYXZvcj0iVU5LIi8+CiAgICAgICAgICAgIDwvbnMzOmVmZmVjdGl2ZVRpbWU+CiAgICAgICAgPC9uczM6c2VydmljZUV2ZW50PgogICAgPC9uczM6ZG9jdW1lbnRhdGlvbk9mPgogICAgPG5zMzpjb21wb25lbnQ+CiAgICAgICAgPG5zMzpzdHJ1Y3R1cmVkQm9keT4KICAgICAgICAgICAgPG5zMzpjb21wb25lbnQ+CiAgICAgICAgICAgICAgICA8bnMzOnNlY3Rpb24+CiAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IkhJVFNQL0M4MyIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMy44OC4xMS44My4xMDIiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSUhFIFBDQyIgcm9vdD0iMS4zLjYuMS40LjEuMTkzNzYuMS41LjMuMS4zLjEzIi8+CiAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IkhMNyBDQ0QiIHJvb3Q9IjIuMTYuODQwLjEuMTEzODgzLjEwLjIwLjEuMiIvPgogICAgICAgICAgICAgICAgICAgIDxuczM6aWQgcm9vdD0iZmMxMjZjYjAtNWI4Ny00YTQwLWFkMzUtNGEwMGRkYTY3MzY1Ii8+CiAgICAgICAgICAgICAgICAgICAgPG5zMzpjb2RlIGRpc3BsYXlOYW1lPSJBbGxlcmdpZXMsIGFkdmVyc2UgcmVhY3Rpb25zLCBhbGVydHMiIGNvZGVTeXN0ZW1OYW1lPSJMT0lOQyIgY29kZVN5c3RlbT0iMi4xNi44NDAuMS4xMTM4ODMuNi4xIiBjb2RlPSI0ODc2NS0yIi8+CiAgICAgICAgICAgICAgICAgICAgPG5zMzp0aXRsZT5BbGVydHMsIEFsbGVyZ2llcyBhbmQgQWR2ZXJzZSBSZWFjdGlvbnM8L25zMzp0aXRsZT4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRleHQ+CiAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6cGFyYWdyYXBoIHN0eWxlQ29kZT0ieEZpbHRlck1ldGFkYXRhIj5GSUxURVIgQVBQTElFRDogQWxsIEtub3duIEFjdGl2ZSBBbGxlcmdpZXM8L25zMzpwYXJhZ3JhcGg+CiAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6cGFyYWdyYXBoIElEPSJOb0FsbGVyZ2llcyI+QWxsZXJnaWVzIFVua25vd248L25zMzpwYXJhZ3JhcGg+CiAgICAgICAgICAgICAgICAgICAgPC9uczM6dGV4dD4KICAgICAgICAgICAgICAgICAgICA8bnMzOmVudHJ5IHR5cGVDb2RlPSJEUklWIj4KICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzphY3QgbW9vZENvZGU9IkVWTiIgY2xhc3NDb2RlPSJBQ1QiPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IkhJVFNQL0M4MyIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMy44OC4xMS44My42Ii8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSElUU1AvQzgzIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4zLjg4LjExLjMyLjYiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJJSEUgUENDIiByb290PSIxLjMuNi4xLjQuMS4xOTM3Ni4xLjUuMy4xLjQuNS4xIi8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSUhFIFBDQyIgcm9vdD0iMS4zLjYuMS40LjEuMTkzNzYuMS41LjMuMS40LjUuMyIvPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IkhMNyBDQ0QiIHJvb3Q9IjIuMTYuODQwLjEuMTEzODgzLjEwLjIwLjEuMjciLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6aWQgbnVsbEZsYXZvcj0iVU5LIi8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOmNvZGUgbnVsbEZsYXZvcj0iTkEiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6c3RhdHVzQ29kZSBjb2RlPSJjb21wbGV0ZWQiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6ZWZmZWN0aXZlVGltZT4KPG5zMzpsb3cgbnVsbEZsYXZvcj0iVU5LIi8+CjxuczM6aGlnaCBudWxsRmxhdm9yPSJVTksiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDwvbnMzOmVmZmVjdGl2ZVRpbWU+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOmVudHJ5UmVsYXRpb25zaGlwIGludmVyc2lvbkluZD0iZmFsc2UiIHR5cGVDb2RlPSJTVUJKIj4KPG5zMzpvYnNlcnZhdGlvbiBtb29kQ29kZT0iRVZOIiBjbGFzc0NvZGU9Ik9CUyIgbnVsbEZsYXZvcj0iVU5LIj4KICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJITDcgQ0NEIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4xMC4yMC4xLjI4Ii8+CiAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSEw3IENDRCIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMTAuMjAuMS4xOCIvPgogICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IklIRSBQQ0MiIHJvb3Q9IjEuMy42LjEuNC4xLjE5Mzc2LjEuNS4zLjEuNC41Ii8+CiAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSUhFIFBDQyIgZXh0ZW5zaW9uPSJBbGxlcmd5IiByb290PSIxLjMuNi4xLjQuMS4xOTM3Ni4xLjUuMy4xLjQuNiIvPgogICAgPG5zMzppZCBudWxsRmxhdm9yPSJOSSIvPgogICAgPG5zMzpjb2RlIGRpc3BsYXlOYW1lPSJQcm9wZW5zaXR5IHRvIGFkdmVyc2UgcmVhY3Rpb25zIChkaXNvcmRlcikiIGNvZGVTeXN0ZW1OYW1lPSJTTk9NRUQtQ1QiIGNvZGVTeXN0ZW09IjIuMTYuODQwLjEuMTEzODgzLjYuOTYiIGNvZGU9IjQyMDEzNDAwNiIvPgogICAgPG5zMzp0ZXh0PgogICAgICAgIDxuczM6cmVmZXJlbmNlIHZhbHVlPSIjTm9BbGxlcmdpZXMiLz4KICAgIDwvbnMzOnRleHQ+CiAgICA8bnMzOnN0YXR1c0NvZGUgY29kZT0iY29tcGxldGVkIi8+CiAgICA8bnMzOmVmZmVjdGl2ZVRpbWU+CiAgICAgICAgPG5zMzpsb3cgbnVsbEZsYXZvcj0iVU5LIi8+CiAgICA8L25zMzplZmZlY3RpdmVUaW1lPgogICAgPG5zMzp2YWx1ZSB4bWxuczp4c2k9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hLWluc3RhbmNlIiB4c2k6dHlwZT0iQ0QiIG51bGxGbGF2b3I9Ik5BIi8+CjwvbnMzOm9ic2VydmF0aW9uPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPC9uczM6ZW50cnlSZWxhdGlvbnNoaXA+CiAgICAgICAgICAgICAgICAgICAgICAgIDwvbnMzOmFjdD4KICAgICAgICAgICAgICAgICAgICA8L25zMzplbnRyeT4KICAgICAgICAgICAgICAgIDwvbnMzOnNlY3Rpb24+CiAgICAgICAgICAgIDwvbnMzOmNvbXBvbmVudD4KICAgICAgICAgICAgPG5zMzpjb21wb25lbnQ+CiAgICAgICAgICAgICAgICA8bnMzOnNlY3Rpb24+CiAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IkhJVFNQL0M4MyIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMy44OC4xMS44My4xMDMiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSUhFIFBDQyIgcm9vdD0iMS4zLjYuMS40LjEuMTkzNzYuMS41LjMuMS4zLjYiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSEw3IENDRCIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMTAuMjAuMS4xMSIvPgogICAgICAgICAgICAgICAgICAgIDxuczM6aWQgcm9vdD0iMmM5OGU2ZWItYzFmNC00MmM4LWI4ZGUtYjBjMzQyM2JiOGFkIi8+CiAgICAgICAgICAgICAgICAgICAgPG5zMzpjb2RlIGRpc3BsYXlOYW1lPSJQUk9CTEVNIExJU1QiIGNvZGVTeXN0ZW1OYW1lPSJMT0lOQyIgY29kZVN5c3RlbT0iMi4xNi44NDAuMS4xMTM4ODMuNi4xIiBjb2RlPSIxMTQ1MC00Ii8+CiAgICAgICAgICAgICAgICAgICAgPG5zMzp0aXRsZT5Qcm9ibGVtczwvbnMzOnRpdGxlPgogICAgICAgICAgICAgICAgICAgIDxuczM6dGV4dD4KICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzpwYXJhZ3JhcGggc3R5bGVDb2RlPSJ4RmlsdGVyTWV0YWRhdGEiPkZJTFRFUiBBUFBMSUVEOiBPbmx5IGtub3duIEFjdGl2ZSBhbmQgUmVzb2x2ZWQgUHJvYmxlbXMgd2l0aCBPbnNldCBEYXRlIHdpdGhpbiB0aGUgbGFzdCAzIHllYXIocyk8L25zMzpwYXJhZ3JhcGg+CiAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6cGFyYWdyYXBoIElEPSJOb1Byb2JsZW0iPk1lZGljYWwgSGlzdG9yeSBVbmtub3duPC9uczM6cGFyYWdyYXBoPgogICAgICAgICAgICAgICAgICAgIDwvbnMzOnRleHQ+CiAgICAgICAgICAgICAgICAgICAgPG5zMzplbnRyeSB0eXBlQ29kZT0iRFJJViI+CiAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6YWN0IG1vb2RDb2RlPSJFVk4iIGNsYXNzQ29kZT0iQUNUIj4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJISVRTUC9DODMiIHJvb3Q9IjIuMTYuODQwLjEuMTEzODgzLjMuODguMTEuODMuNyIvPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IkhJVFNQL0M4MyIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMy44OC4xMS4zMi43Ii8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSUhFIFBDQyIgcm9vdD0iMS4zLjYuMS40LjEuMTkzNzYuMS41LjMuMS40LjUuMiIvPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IklIRSBQQ0MiIHJvb3Q9IjEuMy42LjEuNC4xLjE5Mzc2LjEuNS4zLjEuNC41LjEiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJITDcgQ0NEIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4xMC4yMC4xLjI3Ii8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOmlkIG51bGxGbGF2b3I9IlVOSyIvPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzpjb2RlIG51bGxGbGF2b3I9Ik5BIi8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOnN0YXR1c0NvZGUgY29kZT0iY29tcGxldGVkIi8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOmVmZmVjdGl2ZVRpbWU+CjxuczM6bG93IG51bGxGbGF2b3I9IlVOSyIvPgo8bnMzOmhpZ2ggbnVsbEZsYXZvcj0iVU5LIi8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8L25zMzplZmZlY3RpdmVUaW1lPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzplbnRyeVJlbGF0aW9uc2hpcCBpbnZlcnNpb25JbmQ9ImZhbHNlIiB0eXBlQ29kZT0iU1VCSiI+CjxuczM6b2JzZXJ2YXRpb24gbW9vZENvZGU9IkVWTiIgY2xhc3NDb2RlPSJPQlMiIG51bGxGbGF2b3I9IlVOSyI+CiAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSEw3IENDRCIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMTAuMjAuMS4yOCIvPgogICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IklIRSBQQ0MiIHJvb3Q9IjEuMy42LjEuNC4xLjE5Mzc2LjEuNS4zLjEuNC41Ii8+CiAgICA8bnMzOmlkIG51bGxGbGF2b3I9Ik5JIi8+CiAgICA8bnMzOmNvZGUvPgogICAgPG5zMzp0ZXh0PgogICAgICAgIDxuczM6cmVmZXJlbmNlIHZhbHVlPSIjTm9Qcm9ibGVtIi8+CiAgICA8L25zMzp0ZXh0PgogICAgPG5zMzpzdGF0dXNDb2RlIGNvZGU9ImNvbXBsZXRlZCIvPgogICAgPG5zMzplZmZlY3RpdmVUaW1lPgogICAgICAgIDxuczM6bG93IG51bGxGbGF2b3I9IlVOSyIvPgogICAgPC9uczM6ZWZmZWN0aXZlVGltZT4KICAgIDxuczM6dmFsdWUgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIgeHNpOnR5cGU9IkNEIiBkaXNwbGF5TmFtZT0iTWVkaWNhbCBIaXN0b3J5IFVua25vd24iIGNvZGVTeXN0ZW1OYW1lPSJTTk9NRUQtQ1QiIGNvZGVTeXN0ZW09IjIuMTYuODQwLjEuMTEzODgzLjYuOTYiIGNvZGU9IjM5Njc4MjAwNiIvPgo8L25zMzpvYnNlcnZhdGlvbj4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDwvbnMzOmVudHJ5UmVsYXRpb25zaGlwPgogICAgICAgICAgICAgICAgICAgICAgICA8L25zMzphY3Q+CiAgICAgICAgICAgICAgICAgICAgPC9uczM6ZW50cnk+CiAgICAgICAgICAgICAgICA8L25zMzpzZWN0aW9uPgogICAgICAgICAgICA8L25zMzpjb21wb25lbnQ+CiAgICAgICAgICAgIDxuczM6Y29tcG9uZW50PgogICAgICAgICAgICAgICAgPG5zMzpzZWN0aW9uPgogICAgICAgICAgICAgICAgICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJISVRTUC9DODMiIHJvb3Q9IjIuMTYuODQwLjEuMTEzODgzLjMuODguMTEuODMuMTEyIi8+CiAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IklIRSBQQ0MiIHJvb3Q9IjEuMy42LjEuNC4xLjE5Mzc2LjEuNS4zLjEuMy4xOSIvPgogICAgICAgICAgICAgICAgICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJITDcgQ0NEIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4xMC4yMC4xLjgiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOmlkIHJvb3Q9IjYxNmE0ZjNmLWNmZjgtNDE2Ni04OWRlLWZkYzA2MDlmYzYwMyIvPgogICAgICAgICAgICAgICAgICAgIDxuczM6Y29kZSBkaXNwbGF5TmFtZT0iSElTVE9SWSBPRiBNRURJQ0FUSU9OIFVTRSIgY29kZVN5c3RlbU5hbWU9IkxPSU5DIiBjb2RlU3lzdGVtPSIyLjE2Ljg0MC4xLjExMzg4My42LjEiIGNvZGU9IjEwMTYwLTAiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRpdGxlPk1lZGljYXRpb25zPC9uczM6dGl0bGU+CiAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZXh0PgogICAgICAgICAgICAgICAgICAgICAgICA8bnMzOnBhcmFncmFwaCBzdHlsZUNvZGU9InhGaWx0ZXJNZXRhZGF0YSI+RklMVEVSIEFQUExJRUQ6IE9ubHkga25vd24gQWN0aXZlIE1lZGljYXRpb25zIHdpdGggU3RvcCBEYXRlIHdpdGhpbiB0aGUgbGFzdCAwIGRheShzKTwvbnMzOnBhcmFncmFwaD4KICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzpwYXJhZ3JhcGggSUQ9Ik5vTWVkaWNhdGlvbiI+RHJ1ZyBUcmVhdG1lbnQgVW5rbm93bjwvbnMzOnBhcmFncmFwaD4KICAgICAgICAgICAgICAgICAgICA8L25zMzp0ZXh0PgogICAgICAgICAgICAgICAgICAgIDxuczM6ZW50cnkgdHlwZUNvZGU9IkRSSVYiPgogICAgICAgICAgICAgICAgICAgICAgICA8bnMzOnN1YnN0YW5jZUFkbWluaXN0cmF0aW9uIG1vb2RDb2RlPSJJTlQiIGNsYXNzQ29kZT0iU0JBRE0iPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IkhJVFNQL0M4MyIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMy44OC4xMS44My44Ii8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSElUU1AvQzgzIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4zLjg4LjExLjMyLjgiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJJSEUgUENDIiBleHRlbnNpb249Im1lZGljYXRpb24iIHJvb3Q9IjEuMy42LjEuNC4xLjE5Mzc2LjEuNS4zLjEuNC43Ii8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSUhFIFBDQyIgcm9vdD0iMS4zLjYuMS40LjEuMTkzNzYuMS41LjMuMS40LjcuMSIvPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IkhMNyBDQ0QiIHJvb3Q9IjIuMTYuODQwLjEuMTEzODgzLjEwLjIwLjEuMjQiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6aWQgcm9vdD0iMGQzMWZlYzQtMTU4ZC00MDI3LThjZjQtZjFlNjgwY2Y3YjJjIi8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOmNvZGUgZGlzcGxheU5hbWU9IkRydWcgVHJlYXRtZW50IFVua25vd24iIGNvZGVTeXN0ZW1OYW1lPSJTTk9NRUQtQ1QiIGNvZGVTeXN0ZW09IjIuMTYuODQwLjEuMTEzODgzLjYuOTYiIGNvZGU9IjE4MjkwNDAwMiIvPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZXh0Pgo8bnMzOnJlZmVyZW5jZSB2YWx1ZT0iI05vTWVkaWNhdGlvbiIvPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPC9uczM6dGV4dD4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6c3RhdHVzQ29kZSBjb2RlPSJjb21wbGV0ZWQiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6cm91dGVDb2RlIG51bGxGbGF2b3I9Ik5JIi8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOmNvbnN1bWFibGU+CjxuczM6bWFudWZhY3R1cmVkUHJvZHVjdD4KICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJISVRTUC9DODMiIHJvb3Q9IjIuMTYuODQwLjEuMTEzODgzLjMuODguMTEuODMuOC4yIi8+CiAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSUhFIFBDQyIgcm9vdD0iMS4zLjYuMS40LjEuMTkzNzYuMS41LjMuMS40LjcuMiIvPgogICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IkhMNyBDQ0QiIHJvb3Q9IjIuMTYuODQwLjEuMTEzODgzLjEwLjIwLjEuNTMiLz4KICAgIDxuczM6bWFudWZhY3R1cmVkTWF0ZXJpYWw+CiAgICAgICAgPG5zMzpjb2RlIG51bGxGbGF2b3I9Ik5BIj4KICAgICAgICAgICAgPG5zMzpvcmlnaW5hbFRleHQ+CiAgICAgICAgICAgICAgICA8bnMzOnJlZmVyZW5jZSBudWxsRmxhdm9yPSJOSSIvPgogICAgICAgICAgICA8L25zMzpvcmlnaW5hbFRleHQ+CiAgICAgICAgICAgIDxuczM6dHJhbnNsYXRpb24gbnVsbEZsYXZvcj0iTkkiLz4KICAgICAgICA8L25zMzpjb2RlPgogICAgPC9uczM6bWFudWZhY3R1cmVkTWF0ZXJpYWw+CjwvbnMzOm1hbnVmYWN0dXJlZFByb2R1Y3Q+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8L25zMzpjb25zdW1hYmxlPgogICAgICAgICAgICAgICAgICAgICAgICA8L25zMzpzdWJzdGFuY2VBZG1pbmlzdHJhdGlvbj4KICAgICAgICAgICAgICAgICAgICA8L25zMzplbnRyeT4KICAgICAgICAgICAgICAgIDwvbnMzOnNlY3Rpb24+CiAgICAgICAgICAgIDwvbnMzOmNvbXBvbmVudD4KICAgICAgICAgICAgPG5zMzpjb21wb25lbnQ+CiAgICAgICAgICAgICAgICA8bnMzOnNlY3Rpb24+CiAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IklIRSBQQ0MiIHJvb3Q9IjEuMy42LjEuNC4xLjE5Mzc2LjEuNS4zLjEuMy4xMSIvPgogICAgICAgICAgICAgICAgICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJITDcgQ0NEIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4xMC4yMC4xLjEyIi8+CiAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IkhJVFNQL0M4MyIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMy44OC4xMS44My4xMDgiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSUhFIFBDQyIgcm9vdD0iMS4zLjYuMS40LjEuMTkzNzYuMS41LjMuMS4zLjEyIi8+CiAgICAgICAgICAgICAgICAgICAgPG5zMzppZCByb290PSI4N2U3ODRiNi1mYjBhLTQzYjMtYWZlZC01MGU5ZThlOTA0OTQiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOmNvZGUgZGlzcGxheU5hbWU9IkhJU1RPUlkgT0YgUFJPQ0VEVVJFUyIgY29kZVN5c3RlbU5hbWU9IkxPSU5DIiBjb2RlU3lzdGVtPSIyLjE2Ljg0MC4xLjExMzg4My42LjEiIGNvZGU9IjQ3NTE5LTQiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRpdGxlPlByb2NlZHVyZXM8L25zMzp0aXRsZT4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRleHQ+CiAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6cGFyYWdyYXBoIHN0eWxlQ29kZT0ieEZpbHRlck1ldGFkYXRhIj5GSUxURVIgQVBQTElFRDogT25seSBrbm93biBQcm9jZWR1cmVzIHdpdGggT25zZXQgRGF0ZSB3aXRoaW4gdGhlIGxhc3QgMyB5ZWFyKHMpPC9uczM6cGFyYWdyYXBoPgogICAgICAgICAgICAgICAgICAgICAgICA8bnMzOnBhcmFncmFwaCBJRD0iTm9Qcm9jZWR1cmUiPk5vIEtub3duIFByb2NlZHVyZTwvbnMzOnBhcmFncmFwaD4KICAgICAgICAgICAgICAgICAgICA8L25zMzp0ZXh0PgogICAgICAgICAgICAgICAgICAgIDxuczM6ZW50cnkgY29udGV4dENvbmR1Y3Rpb25JbmQ9InRydWUiIHR5cGVDb2RlPSJEUklWIj4KICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzpwcm9jZWR1cmUgbW9vZENvZGU9IkVWTiIgY2xhc3NDb2RlPSJQUk9DIj4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJISVRTUC9DODMiIHJvb3Q9IjIuMTYuODQwLjEuMTEzODgzLjMuODguMTEuODMuMTciLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJITDcgQ0NEIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4xMC4yMC4xLjI5Ii8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSUhFIFBDQyIgcm9vdD0iMS4zLjYuMS40LjEuMTkzNzYuMS41LjMuMS40LjE5Ii8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOmlkIG51bGxGbGF2b3I9Ik5JIi8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOmNvZGUgbnVsbEZsYXZvcj0iTkkiPgo8bnMzOm9yaWdpbmFsVGV4dD4KICAgIDxuczM6cmVmZXJlbmNlIHZhbHVlPSIjTm9Qcm9jZWR1cmUiLz4KPC9uczM6b3JpZ2luYWxUZXh0PgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPC9uczM6Y29kZT4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6dGV4dD4KPG5zMzpyZWZlcmVuY2UgdmFsdWU9IiNOb1Byb2NlZHVyZSIvPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPC9uczM6dGV4dD4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6c3RhdHVzQ29kZSBjb2RlPSJjb21wbGV0ZWQiLz4KICAgICAgICAgICAgICAgICAgICAgICAgPC9uczM6cHJvY2VkdXJlPgogICAgICAgICAgICAgICAgICAgIDwvbnMzOmVudHJ5PgogICAgICAgICAgICAgICAgPC9uczM6c2VjdGlvbj4KICAgICAgICAgICAgPC9uczM6Y29tcG9uZW50PgogICAgICAgICAgICA8bnMzOmNvbXBvbmVudD4KICAgICAgICAgICAgICAgIDxuczM6c2VjdGlvbj4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSElUU1AvQzgzIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4zLjg4LjExLjgzLjEyMiIvPgogICAgICAgICAgICAgICAgICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJJSEUgUENDIiByb290PSIxLjMuNi4xLjQuMS4xOTM3Ni4xLjUuMy4xLjMuMjgiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSEw3IENDRCIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMTAuMjAuMS4xNCIvPgogICAgICAgICAgICAgICAgICAgIDxuczM6aWQgcm9vdD0iMWExOGUwMmItMjBiZC00NDViLWI5MmMtMzBjYjI5ZGJhZDJlIi8+CiAgICAgICAgICAgICAgICAgICAgPG5zMzpjb2RlIGRpc3BsYXlOYW1lPSJSZWxldmFudCBkaWFnbm9zdGljIHRlc3RzIGFuZC9vciBsYWJvcmF0b3J5IGRhdGEiIGNvZGVTeXN0ZW1OYW1lPSJMT0lOQyIgY29kZVN5c3RlbT0iMi4xNi44NDAuMS4xMTM4ODMuNi4xIiBjb2RlPSIzMDk1NC0yIi8+CiAgICAgICAgICAgICAgICAgICAgPG5zMzp0aXRsZT5SZXN1bHRzPC9uczM6dGl0bGU+CiAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZXh0PgogICAgICAgICAgICAgICAgICAgICAgICA8bnMzOnBhcmFncmFwaCBzdHlsZUNvZGU9InhGaWx0ZXJNZXRhZGF0YSI+RklMVEVSIEFQUExJRUQ6IE9ubHkga25vd24gUmVzdWx0cyB3aXRoIENvbGxlY3Rpb24gRGF0ZSB3aXRoaW4gdGhlIGxhc3QgNiBtb250aChzKTwvbnMzOnBhcmFncmFwaD4KICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzpwYXJhZ3JhcGggSUQ9Ik5vUmVzdWx0cyI+Tm8gS25vd24gUmVzdWx0czwvbnMzOnBhcmFncmFwaD4KICAgICAgICAgICAgICAgICAgICA8L25zMzp0ZXh0PgogICAgICAgICAgICAgICAgICAgIDxuczM6ZW50cnkgdHlwZUNvZGU9IkRSSVYiPgogICAgICAgICAgICAgICAgICAgICAgICA8bnMzOm9yZ2FuaXplciBtb29kQ29kZT0iRVZOIiBjbGFzc0NvZGU9IkNMVVNURVIiPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IkhMNyBDQ0QiIHJvb3Q9IjIuMTYuODQwLjEuMTEzODgzLjEwLjIwLjEuMzIiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6aWQgbnVsbEZsYXZvcj0iTkkiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6Y29kZSBudWxsRmxhdm9yPSJOSSIvPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzpzdGF0dXNDb2RlIGNvZGU9ImNvbXBsZXRlZCIvPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzplZmZlY3RpdmVUaW1lPgo8bnMzOmxvdyBudWxsRmxhdm9yPSJVTksiLz4KPG5zMzpoaWdoIG51bGxGbGF2b3I9IlVOSyIvPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPC9uczM6ZWZmZWN0aXZlVGltZT4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6Y29tcG9uZW50Pgo8bnMzOm9ic2VydmF0aW9uIG5lZ2F0aW9uSW5kPSJ0cnVlIiBtb29kQ29kZT0iRVZOIiBjbGFzc0NvZGU9Ik9CUyI+CiAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSElUU1AvQzgzIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4zLjg4LjExLjgzLjE1LjEiLz4KICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJJSEUgUENDIiByb290PSIxLjMuNi4xLjQuMS4xOTM3Ni4xLjUuMy4xLjQuMTMiLz4KICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJITDcgQ0NEIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4xMC4yMC4xLjMxIi8+CiAgICA8bnMzOmlkIG51bGxGbGF2b3I9Ik5JIi8+CiAgICA8bnMzOmNvZGUgZGlzcGxheU5hbWU9IkxhYm9yYXRvcnkgU3R1ZGllcyIgY29kZVN5c3RlbU5hbWU9IkxPSU5DIiBjb2RlU3lzdGVtPSIyLjE2Ljg0MC4xLjExMzg4My42LjEiIGNvZGU9IjI2NDM2LTYiLz4KICAgIDxuczM6dGV4dD4KICAgICAgICA8bnMzOnJlZmVyZW5jZSB2YWx1ZT0iI05vUmVzdWx0cyIvPgogICAgPC9uczM6dGV4dD4KICAgIDxuczM6c3RhdHVzQ29kZSBjb2RlPSJjb21wbGV0ZWQiLz4KICAgIDxuczM6ZWZmZWN0aXZlVGltZSBudWxsRmxhdm9yPSJVTksiLz4KICAgIDxuczM6dmFsdWUgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIgeHNpOnR5cGU9IkNEIiBudWxsRmxhdm9yPSJOSSIvPgo8L25zMzpvYnNlcnZhdGlvbj4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDwvbnMzOmNvbXBvbmVudD4KICAgICAgICAgICAgICAgICAgICAgICAgPC9uczM6b3JnYW5pemVyPgogICAgICAgICAgICAgICAgICAgIDwvbnMzOmVudHJ5PgogICAgICAgICAgICAgICAgPC9uczM6c2VjdGlvbj4KICAgICAgICAgICAgPC9uczM6Y29tcG9uZW50PgogICAgICAgICAgICA8bnMzOmNvbXBvbmVudD4KICAgICAgICAgICAgICAgIDxuczM6c2VjdGlvbj4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSEw3IENDRCIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMTAuMjAuMS4zIi8+CiAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZW1wbGF0ZUlkIGFzc2lnbmluZ0F1dGhvcml0eU5hbWU9IkhJVFNQL0M4MyIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMy44OC4xMS44My4xMjciLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSUhFIFBDQyIgcm9vdD0iMS4zLjYuMS40LjEuMTkzNzYuMS41LjMuMS4xLjUuMy4zIi8+CiAgICAgICAgICAgICAgICAgICAgPG5zMzppZCByb290PSIzNmVjMzMxZC0xYzBiLTRiM2QtYTQ2ZC1kY2RiMTdjM2NhNmYiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOmNvZGUgZGlzcGxheU5hbWU9IkhJU1RPUlkgT0YgRU5DT1VOVEVSUyIgY29kZVN5c3RlbU5hbWU9IkxPSU5DIiBjb2RlU3lzdGVtPSIyLjE2Ljg0MC4xLjExMzg4My42LjEiIGNvZGU9IjQ2MjQwLTgiLz4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRpdGxlPkVuY291bnRlcnM8L25zMzp0aXRsZT4KICAgICAgICAgICAgICAgICAgICA8bnMzOnRleHQ+CiAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6cGFyYWdyYXBoIHN0eWxlQ29kZT0ieEZpbHRlck1ldGFkYXRhIj5GSUxURVIgQVBQTElFRDogT25seSBrbm93biBFbmNvdW50ZXJzIHdpdGggQWRtaXNzaW9uIERhdGUgd2l0aGluIHRoZSBsYXN0IDMgeWVhcihzKTwvbnMzOnBhcmFncmFwaD4KICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzp0YWJsZT4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6dGhlYWQ+CjxuczM6dHI+CiAgICA8bnMzOnRoPkVuY291bnRlcjwvbnMzOnRoPgogICAgPG5zMzp0aD5Mb2NhdGlvbjwvbnMzOnRoPgogICAgPG5zMzp0aD5BZG1pc3Npb248L25zMzp0aD4KICAgIDxuczM6dGg+RGlzY2hhcmdlPC9uczM6dGg+CiAgICA8bnMzOnRoPkJpbGxpbmcgQ29kZTwvbnMzOnRoPgo8L25zMzp0cj4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDwvbnMzOnRoZWFkPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzp0Ym9keT4KPG5zMzp0cj4KICAgIDxuczM6dGQ+CiAgICAgICAgPG5zMzpjb250ZW50IElEPSJFbmNvdW50ZXIxIj5PdXRwYXRpZW50PC9uczM6Y29udGVudD4KICAgIDwvbnMzOnRkPgogICAgPG5zMzp0ZD5WVU1DLUZBQzwvbnMzOnRkPgogICAgPG5zMzp0ZD4wNC8yNS8yMDE3PC9uczM6dGQ+CiAgICA8bnMzOnRkPjwvbnMzOnRkPgogICAgPG5zMzp0ZC8+CjwvbnMzOnRyPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPC9uczM6dGJvZHk+CiAgICAgICAgICAgICAgICAgICAgICAgIDwvbnMzOnRhYmxlPgogICAgICAgICAgICAgICAgICAgIDwvbnMzOnRleHQ+CiAgICAgICAgICAgICAgICAgICAgPG5zMzplbnRyeSB0eXBlQ29kZT0iRFJJViI+CiAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6ZW5jb3VudGVyIG1vb2RDb2RlPSJFVk4iIGNsYXNzQ29kZT0iRU5DIj4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJISVRTUC9DODMiIHJvb3Q9IjIuMTYuODQwLjEuMTEzODgzLjMuODguMTEuODMuMTYiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJJSEUgUENDIiByb290PSIxLjMuNi4xLjQuMS4xOTM3Ni4xLjUuMy4xLjQuMTQiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxuczM6dGVtcGxhdGVJZCBhc3NpZ25pbmdBdXRob3JpdHlOYW1lPSJITDcgQ0NEIiByb290PSIyLjE2Ljg0MC4xLjExMzg4My4xMC4yMC4xLjIxIi8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOmlkIGV4dGVuc2lvbj0iMTgwMDIwMDE4NjIzNiIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMy4xNzgyIi8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOmNvZGUgZGlzcGxheU5hbWU9Ik91dHBhdGllbnQiIGNvZGVTeXN0ZW09IjIuMTYuODQwLjEuMTEzODgzLjEyLjQiIGNvZGU9Ik8iPgo8bnMzOm9yaWdpbmFsVGV4dD4KICAgIDxuczM6cmVmZXJlbmNlIHZhbHVlPSIjRW5jb3VudGVyMSIvPgo8L25zMzpvcmlnaW5hbFRleHQ+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8L25zMzpjb2RlPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPG5zMzp0ZXh0Pgo8bnMzOnJlZmVyZW5jZSB2YWx1ZT0iI0VuY291bnRlcjEiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDwvbnMzOnRleHQ+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOnN0YXR1c0NvZGUgY29kZT0iY29tcGxldGVkIi8+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOmVmZmVjdGl2ZVRpbWU+CjxuczM6bG93IHZhbHVlPSIyMDE3MDQyNTExNTExNi4wMC0wNTAwIi8+CjxuczM6aGlnaCBudWxsRmxhdm9yPSJVTksiLz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDwvbnMzOmVmZmVjdGl2ZVRpbWU+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8bnMzOnBhcnRpY2lwYW50IHR5cGVDb2RlPSJMT0MiPgo8bnMzOnRlbXBsYXRlSWQgYXNzaWduaW5nQXV0aG9yaXR5TmFtZT0iSEw3IENDRCIgcm9vdD0iMi4xNi44NDAuMS4xMTM4ODMuMTAuMjAuMS40NSIvPgo8bnMzOnBhcnRpY2lwYW50Um9sZSBjbGFzc0NvZGU9IlNETE9DIj4KICAgIDxuczM6cGxheWluZ0VudGl0eSBjbGFzc0NvZGU9IlBMQyI+CiAgICAgICAgPG5zMzpuYW1lPlZVTUMtRkFDPC9uczM6bmFtZT4KICAgIDwvbnMzOnBsYXlpbmdFbnRpdHk+CjwvbnMzOnBhcnRpY2lwYW50Um9sZT4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDwvbnMzOnBhcnRpY2lwYW50PgogICAgICAgICAgICAgICAgICAgICAgICA8L25zMzplbmNvdW50ZXI+CiAgICAgICAgICAgICAgICAgICAgPC9uczM6ZW50cnk+CiAgICAgICAgICAgICAgICA8L25zMzpzZWN0aW9uPgogICAgICAgICAgICA8L25zMzpjb21wb25lbnQ+CiAgICAgICAgPC9uczM6c3RydWN0dXJlZEJvZHk+CiAgICA8L25zMzpjb21wb25lbnQ+CjwvbnMzOkNsaW5pY2FsRG9jdW1lbnQ+Cg==</xds-b:Document>" +
            "      </xds-b:DocumentResponse>" +
            "    </xds-b:RetrieveDocumentSetResponse>" +
            "  </env:Body>" +
            "</env:Envelope>";
  }

  private String sampleMessage_PatientBody2()
  {
    return "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\">\n" +
            "<html xmlns:n1=\"urn:hl7-org:v3\">\n" +
            "<head>\n" +
            "<META http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n" +
            "<link href=\"https://www.orionhealth.com/software/ccd/style/skin_v2.4.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
            "<!--Do NOT edit this HTML directly, it was generated via an XSLt transformation from the original release 2 CDA Document.-->\n" +
            "<title>Continuity of Care Document</title>\n" +
            "</head>\n" +
            "<!--\n" +
            "\t\t\t\tCopyright 2011 Orion Health group of companies. All Rights Reserved.  \t\t\t\n" +
            "\t\t\t-->\n" +
            "<body>\n" +
            "<h2 align=\"center\">Continuity of Care Document</h2>\n" +
            "<p align=\"center\">\n" +
            "<b>Created On: </b>April 25, 2017</p>\n" +
            "<hr>\n" +
            "<div class=\"header\">\n" +
            "<div class=\"demographics sticky\">\n" +
            "<div class=\"bl\">\n" +
            "<div class=\"br\">\n" +
            "<div class=\"tr\">\n" +
            "<div class=\"person-name\">CADENCE, JOANIE </div>\n" +
            "<div class=\"sex-age\">\n" +
            "<span>Female</span><span id=\"calculatedAge\"></span>25-Apr-1975<span class=\"label\"> (DOB)</span>\n" +
            "</div>\n" +
            "<div class=\"id\">002102283<span class=\"label\">\n" +
            "\t\t\t\t\t\t\t\t\t\t\t(Vanderbilt University Medical Center)\n" +
            "\t\t\t\t\t\t\t\t\t\t</span>\n" +
            "<br>11 INWOOD RD<br>SHORT HILLS,\n" +
            "\t\t\t\tNJ,\n" +
            "\t\t\t\tUS,\n" +
            "\t\t\t\t07078<br>(973) 912-4439</div>\n" +
            "</div>\n" +
            "</div>\n" +
            "</div>\n" +
            "</div>\n" +
            "</div>\n" +
            "<div class=\"section\">\n" +
            "<b>Electronically generated</b><b> by </b>Vanderbilt Health Affiliated Network<b> on </b>April 25, 2017</div>\n" +
            "<div class=\"section\">\n" +
            "<span><a name=\"N65869\" href=\"#toc\">\n" +
            "<h2>Alerts, Allergies and Adverse Reactions</h2>\n" +
            "</a></span>\n" +
            "</div>\n" +
            "                        \n" +
            "                        \n" +
            "<p>Allergies Unknown</p>\n" +
            "                    \n" +
            "<div class=\"section\">\n" +
            "<span><a name=\"N66014\" href=\"#toc\">\n" +
            "<h2>Problems</h2>\n" +
            "</a></span>\n" +
            "</div>\n" +
            "                        \n" +
            "                        \n" +
            "<p>Medical History Unknown</p>\n" +
            "                    \n" +
            "<div class=\"section\">\n" +
            "<span><a name=\"N66149\" href=\"#toc\">\n" +
            "<h2>Medications</h2>\n" +
            "</a></span>\n" +
            "</div>\n" +
            "                        \n" +
            "                        \n" +
            "<p>Drug Treatment Unknown</p>\n" +
            "                    \n" +
            "<div class=\"section\">\n" +
            "<span><a name=\"N66279\" href=\"#toc\">\n" +
            "<h2>Procedures</h2>\n" +
            "</a></span>\n" +
            "</div>\n" +
            "                        \n" +
            "                        \n" +
            "<p>No Known Procedure</p>\n" +
            "                    \n" +
            "<div class=\"section\">\n" +
            "<span><a name=\"N66364\" href=\"#toc\">\n" +
            "<h2>Results</h2>\n" +
            "</a></span>\n" +
            "</div>\n" +
            "                        \n" +
            "                        \n" +
            "<p>No Known Results</p>\n" +
            "                    \n" +
            "<div class=\"section\">\n" +
            "<span><a name=\"N66483\" href=\"#toc\">\n" +
            "<h2>Encounters</h2>\n" +
            "</a></span>\n" +
            "</div>\n" +
            "                        \n" +
            "                        \n" +
            "<table class=\"data\">\n" +
            "                            \n" +
            "<thead>\n" +
            "\n" +
            "<tr class=\"yui-dt-even\">\n" +
            "    \n" +
            "<th class=\"table\" style=\"background-color:#ffffff; padding:6px; color:#5D646C\">Encounter</th>\n" +
            "    <th class=\"table\" style=\"background-color:#ffffff; padding:6px; color:#5D646C\">Location</th>\n" +
            "    <th class=\"table\" style=\"background-color:#ffffff; padding:6px; color:#5D646C\">Admission</th>\n" +
            "    <th class=\"table\" style=\"background-color:#ffffff; padding:6px; color:#5D646C\">Discharge</th>\n" +
            "    <th class=\"table\" style=\"background-color:#ffffff; padding:6px; color:#5D646C\">Billing Code</th>\n" +
            "\n" +
            "</tr>\n" +
            "                            \n" +
            "</thead>\n" +
            "                            \n" +
            "<tbody>\n" +
            "\n" +
            "<tr class=\"yui-dt-even\">\n" +
            "    \n" +
            "<td class=\"table\" style=\"padding:6px\">\n" +
            "        Outpatient\n" +
            "    </td>\n" +
            "    <td class=\"table\" style=\"padding:6px\">VUMC-FAC</td>\n" +
            "    <td class=\"table\" style=\"padding:6px\">04/25/2017</td>\n" +
            "    <td class=\"table\" style=\"padding:6px\"></td>\n" +
            "    <td class=\"table\" style=\"padding:6px\"></td>\n" +
            "\n" +
            "</tr>\n" +
            "                            \n" +
            "</tbody>\n" +
            "                        \n" +
            "</table>\n" +
            "                    \n" +
            "</body>\n" +
            "<script language=\"JavaScript\" type=\"text/javascript\">\n" +
            "\t\t\t\tvar today = new Date();\n" +
            "\t\t\t\tvar age = 0;\n" +
            "\t\t\t\tvar xmlDob  = '19750425';\n" +
            "\t\t\t\tif (xmlDob.length > 0) {\n" +
            "\t\t\t\t\tvar dob = parseInt(xmlDob.substring(0, 8));\n" +
            "\t\t\t\t\t//Script return month from 0 to 11 not from 1 to 12. Thats why the month has been incremented by 1.\n" +
            "\t\t\t\t\tvar todayMonth = (today.getMonth() + 1) + '';\n" +
            "\t\t\t\t\tif (todayMonth.length == 1) {\n" +
            "\t\t\t\t\t\ttodayMonth = \"0\" + todayMonth;\n" +
            "\t\t\t\t\t}\n" +
            "\t\t\t\t\tvar todayDate = today.getDate() + '';\n" +
            "\t\t\t\t\tif (todayDate.length == 1) {\n" +
            "\t\t\t\t\t\ttodayDate = \"0\" + todayDate;\n" +
            "\t\t\t\t\t}\n" +
            "\t\t\t\t\tvar today = parseInt('' + today.getFullYear() + todayMonth + todayDate);\n" +
            "\t\t\t\t\tage = Math.floor((today - dob) / 10000);\n" +
            "\t\t\t\t}\n" +
            "\t\t\t\tvar ageWithSeparator='';\n" +
            "\n" +
            "\t\t\t\tvar gender = 'F';\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t// The forward slash depends on gender and age i.e age is greater than one year.\n" +
            "\t\t\t\tif (gender.length != 0 && age != 0) {\n" +
            "\t\t\t\t\tageWithSeparator = \"/\";\n" +
            "\t\t\t\t}\n" +
            "\t\t\t\tif (age != 0) {\n" +
            "\t\t\t\t\tageWithSeparator = ageWithSeparator + age + 'y';\n" +
            "\t\t\t\t}\n" +
            "\t\t\t\t//First inner condition: When the Gender is present and the DOB is greater than or equal to one year\n" +
            "\t\t\t\t//Second inner condition: When the Gender is not present and the DOB greater than or equal to one year\n" +
            "\t\t\t\tif ((xmlDob.length != 0 && gender.length != 0) || age != 0) {\n" +
            "\t\t\t\t\tageWithSeparator = ageWithSeparator + ', ';\n" +
            "\t\t\t\t}\n" +
            "\t\t\t\tdocument.getElementById('calculatedAge').innerHTML = ageWithSeparator;\n" +
            "\t\t\t</script>\n" +
            "</html>\n";
  }



}