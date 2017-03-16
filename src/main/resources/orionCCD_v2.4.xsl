<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:n3="http://www.w3.org/1999/xhtml"
	xmlns:n1="urn:hl7-org:v3" xmlns:n2="urn:hl7-org:v3/meta/voc" xmlns:voc="urn:hl7-org:v3/voc"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output method="html" indent="yes" version="4.01"
		encoding="ISO-8859-1" doctype-public="-//W3C//DTD HTML 4.01//EN" />
	
	<!-- CDA document -->

	<xsl:variable name="tableWidth">
		50%
	</xsl:variable>

	<xsl:variable name="title">
		<xsl:choose>
			<xsl:when test="/n1:ClinicalDocument/n1:title">
				<xsl:value-of select="/n1:ClinicalDocument/n1:title" />
			</xsl:when>
			<xsl:otherwise>
				Clinical Document
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="/n1:ClinicalDocument">
				<xsl:apply-templates select="n1:ClinicalDocument" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="*"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="*">
			<html>
				<body>
					<h2>
					 Unable to display document using CCD style sheet. Document is not a valid CCD as it does not contain a ClinicalDocument xml root element.
					</h2>
				</body>
			</html>
	</xsl:template>
	
	<xsl:template match="n1:ClinicalDocument">
		<html>
			<head>
				<!-- <meta name='Generator' content='&CDA-Stylesheet;'/> -->
				<link href="http://www.orionhealth.com/software/ccd/style/skin_v2.4.css" rel="stylesheet" type="text/css" />
				<xsl:comment>Do NOT edit this HTML directly, it was generated via an XSLt transformation from the original release 2 CDA Document.</xsl:comment>
				<title>
					<xsl:value-of select="$title" />
				</title>
			</head>
			<xsl:comment>
				Copyright 2011 Orion Health group of companies. All Rights Reserved.  			
			</xsl:comment>
			<body>

				<h2 align="center">
					<xsl:value-of select="$title" />
				</h2>
				<p align='center'>
					<b>
						<xsl:text>Created On: </xsl:text>
					</b>
					<xsl:call-template name="formatDate">
						<xsl:with-param name="date"
							select="/n1:ClinicalDocument/n1:effectiveTime/@value" />
					</xsl:call-template>
				</p>
				<hr />
				<div class="header">

					<div class="demographics sticky">
						<xsl:variable name="patientRole"
							select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole" />
						<div class="bl">

							<div class="br">

								<div class="tr">

									<div class="person-name">
										<xsl:call-template name="getName">
											<xsl:with-param name="name"
												select="$patientRole/n1:patient/n1:name" />
										</xsl:call-template>
										<xsl:if test="$patientRole/n1:patient/n1:name/n1:prefix != '' ">
											<span class="title"> (<xsl:value-of select="$patientRole/n1:patient/n1:name/n1:prefix"/>)</span>
										</xsl:if>
									</div>

									<div class="sex-age">
										<xsl:variable name="gender"
												select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:administrativeGenderCode/@displayName" />
										<xsl:choose>
											<xsl:when test="$gender = 'M'">
												<span>Male</span>
											</xsl:when>
											<xsl:when test="$gender = 'F'">
												<span>Female</span>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of
													select="$gender" />
											</xsl:otherwise>
										</xsl:choose>
										<span id="calculatedAge"/>
										<xsl:call-template name="formatDateSticky">
											<xsl:with-param name="date"
												select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:birthTime/@value" />
											<xsl:with-param name="gender"
												select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:administrativeGenderCode/@displayName" />	
										</xsl:call-template>
									</div>
									<div class="id">
										<xsl:value-of select="$patientRole/n1:id/@extension" />
										<span class="label">
											(<xsl:choose>
												<xsl:when test="$patientRole/n1:id/@assigningAuthorityName">
													<xsl:value-of select="$patientRole/n1:id/@assigningAuthorityName" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="$patientRole/n1:id/@root" />
												</xsl:otherwise>
											</xsl:choose>)
										</span>
										 <xsl:if test="$patientRole/n1:addr">
											 <xsl:call-template name="getAddress">
											 	<xsl:with-param name="addr" select="$patientRole/n1:addr"/>
											 </xsl:call-template>
										 </xsl:if>
										 <xsl:if test="$patientRole/n1:telecom">
											 <xsl:call-template name="getTelecom">
												 <xsl:with-param name="telecom" select="$patientRole/n1:telecom"/>
											 </xsl:call-template>
										 </xsl:if>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<xsl:apply-templates select="n1:component/n1:structuredBody" />
			</body>
			<script language="JavaScript" type="text/javascript">
				var today = new Date();
				var age = 0;
				var xmlDob  = '<xsl:value-of select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:birthTime/@value" />';
				if (xmlDob.length > 0) {
					var dob = parseInt(xmlDob.substring(0, 8));
					//Script return month from 0 to 11 not from 1 to 12. Thats why the month has been incremented by 1.
					var todayMonth = (today.getMonth() + 1) + '';
					if (todayMonth.length == 1) {
						todayMonth = "0" + todayMonth;
					}
					var todayDate = today.getDate() + '';
					if (todayDate.length == 1) {
						todayDate = "0" + todayDate;
					}
					var today = parseInt('' + today.getFullYear() + todayMonth + todayDate);
					age = Math.floor((today - dob) / 10000);
				}
				var ageWithSeparator='';

				var gender = '<xsl:value-of select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:administrativeGenderCode/@displayName" />';
				
				// The forward slash depends on gender and age i.e age is greater than one year.
				if (gender.length != 0 &amp;&amp; age != 0) {
					ageWithSeparator = "/";
				}
				if (age != 0) {
					ageWithSeparator = ageWithSeparator + age + 'y';
				}
				//First inner condition: When the Gender is present and the DOB is greater than or equal to one year
				//Second inner condition: When the Gender is not present and the DOB greater than or equal to one year
				if ((xmlDob.length != 0 &amp;&amp; gender.length != 0) || age != 0) {
					ageWithSeparator = ageWithSeparator + ', ';
				}
				document.getElementById('calculatedAge').innerHTML = ageWithSeparator;
			</script>
		</html>
	</xsl:template>

	<xsl:template name="getParticipant">
		<xsl:param name="participant" />

		<p>
			<xsl:call-template name="getName">
				<xsl:with-param name="name"
					select="$participant/n1:associatedPerson/n1:name" />
			</xsl:call-template>
			<xsl:if test="$participant/n1:addr">
				<xsl:call-template name="getAddress">
					<xsl:with-param name="addr" select="$participant/n1:addr" />
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$participant/n1:telecom">
				<xsl:call-template name="getTelecom">
					<xsl:with-param name="telecom" select="$participant/n1:telecom" />
				</xsl:call-template>
			</xsl:if>

		</p>
	</xsl:template>

	<xsl:template name="getAddress">
		<xsl:param name="addr" />
		<br />
		<xsl:if test="$addr/n1:streetAddressLine">
			<xsl:for-each select="$addr/n1:streetAddressLine">
				<xsl:if test=". != ''">
					<xsl:value-of select="."/><br />
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="$addr/n1:city != ''">
			<xsl:choose>
				<xsl:when test="$addr/n1:state != ''">
					<xsl:value-of select="$addr/n1:city" />,
				</xsl:when>
				<xsl:when test="$addr/n1:country != ''">
					<xsl:value-of select="$addr/n1:city" />,
				</xsl:when>
				<xsl:when test="$addr/n1:postalCode != ''">
					<xsl:value-of select="$addr/n1:city" />,
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$addr/n1:city" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="$addr/n1:state != ''">
			<xsl:choose>
				<xsl:when test="$addr/n1:country != ''">
					<xsl:value-of select="$addr/n1:state" />,
				</xsl:when>
				<xsl:when test="$addr/n1:postalCode != ''">
					<xsl:value-of select="$addr/n1:state" />,
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$addr/n1:state" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="$addr/n1:country != ''">
			<xsl:choose>
				<xsl:when test="$addr/n1:postalCode != ''">
					<xsl:value-of select="$addr/n1:country" />,
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$addr/n1:country" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="$addr/n1:postalCode != ''">
		    <xsl:value-of select="$addr/n1:postalCode" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="getTelecom">
		<xsl:param name="telecom" />
		<br />
		<xsl:value-of select="$telecom/@value" />
	</xsl:template>

	<!-- Get a Name -->
	<xsl:template name="getName">
		<xsl:param name="name" />
		<xsl:choose>
			<xsl:when test="$name/n1:family">
				<xsl:value-of select="translate($name/n1:family, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
				<xsl:if test="string-length($name/n1:given)!=0">, <xsl:value-of select="$name/n1:given"/>
				</xsl:if>
				<xsl:if test="string-length($name/n1:suffix)>0">
					<xsl:text> (</xsl:text>					
					<xsl:value-of select="$name/n1:suffix"/>
					<xsl:text>)</xsl:text>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$name"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Format Date outputs a date in Month Day, Year form e.g., 19991207 ==> 
		December 07, 1999 -->
	<xsl:template name="formatDate">
		<xsl:param name="date" />
		<xsl:variable name="month" select="substring ($date, 5, 2)" />
		<xsl:choose>
			<xsl:when test="$month='01'">
				<xsl:text>January </xsl:text>
			</xsl:when>
			<xsl:when test="$month='02'">
				<xsl:text>February </xsl:text>
			</xsl:when>
			<xsl:when test="$month='03'">
				<xsl:text>March </xsl:text>
			</xsl:when>
			<xsl:when test="$month='04'">
				<xsl:text>April </xsl:text>
			</xsl:when>
			<xsl:when test="$month='05'">
				<xsl:text>May </xsl:text>
			</xsl:when>
			<xsl:when test="$month='06'">
				<xsl:text>June </xsl:text>
			</xsl:when>
			<xsl:when test="$month='07'">
				<xsl:text>July </xsl:text>
			</xsl:when>
			<xsl:when test="$month='08'">
				<xsl:text>August </xsl:text>
			</xsl:when>
			<xsl:when test="$month='09'">
				<xsl:text>September </xsl:text>
			</xsl:when>
			<xsl:when test="$month='10'">
				<xsl:text>October </xsl:text>
			</xsl:when>
			<xsl:when test="$month='11'">
				<xsl:text>November </xsl:text>
			</xsl:when>
			<xsl:when test="$month='12'">
				<xsl:text>December </xsl:text>
			</xsl:when>
		</xsl:choose>

		<xsl:choose>
			<xsl:when test='substring ($date, 7, 1)="0"'>
				<xsl:value-of select="substring ($date, 8, 1)" />
				<xsl:text>, </xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="substring ($date, 7, 2)" />
				<xsl:text>, </xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="substring ($date, 1, 4)" />
	</xsl:template>
	
	<!-- Format Date outputs a date in Month-Day-Year form e.g., 19861225 ==> 
		25-Dec-1986 -->
	<xsl:template name="formatDateSticky">
		<xsl:param name="date" />
		<xsl:param name="gender" />

		<xsl:choose>
			<xsl:when test="string-length($date)!=0">
			
				<xsl:choose>
					<xsl:when test="substring ($date, 7, 1)='0'">
						<xsl:value-of select="substring ($date, 8, 1)" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="substring ($date, 7, 2)" />
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>-</xsl:text>
				<xsl:variable name="month" select="substring ($date, 5, 2)" />
				<xsl:choose>
					<xsl:when test="$month='01'">
						<xsl:text>Jan</xsl:text>
					</xsl:when>
					<xsl:when test="$month='02'">
						<xsl:text>Feb</xsl:text>
					</xsl:when>
					<xsl:when test="$month='03'">
						<xsl:text>Mar</xsl:text>
					</xsl:when>
					<xsl:when test="$month='04'">
						<xsl:text>Apr</xsl:text>
					</xsl:when>
					<xsl:when test="$month='05'">
						<xsl:text>May</xsl:text>
					</xsl:when>
					<xsl:when test="$month='06'">
						<xsl:text>Jun</xsl:text>
					</xsl:when>
					<xsl:when test="$month='07'">
						<xsl:text>Jul</xsl:text>
					</xsl:when>
					<xsl:when test="$month='08'">
						<xsl:text>Aug</xsl:text>
					</xsl:when>
					<xsl:when test="$month='09'">
						<xsl:text>Sep</xsl:text>
					</xsl:when>
					<xsl:when test="$month='10'">
						<xsl:text>Oct</xsl:text>
					</xsl:when>
					<xsl:when test="$month='11'">
						<xsl:text>Nov</xsl:text>
					</xsl:when>
					<xsl:when test="$month='12'">
						<xsl:text>Dec</xsl:text>
					</xsl:when>
				</xsl:choose>
				<xsl:text>-</xsl:text>
				<xsl:value-of select="substring ($date, 1, 4)" />
				<span class="label"> (DOB)</span>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- StructuredBody -->
	<xsl:template match="n1:component/n1:structuredBody">
	<xsl:variable name="generatedBy" select="/n1:ClinicalDocument/n1:legalAuthenticator/n1:assignedEntity/n1:representedOrganization/n1:name" />
	<xsl:variable name="generationDate" select="//n1:ClinicalDocument/n1:legalAuthenticator/n1:time/@value" />
	<!-- Only show section when fields are populated -->	
	<xsl:if test="($generatedBy and $generatedBy!='') or ($generationDate and $generationDate!='')">
		<div class="section">
			<b><xsl:text>Electronically generated</xsl:text></b>
		    <xsl:if test="$generatedBy!=''">
				<b><xsl:text> by </xsl:text></b>
				<xsl:call-template name="getName">
					<xsl:with-param name="name" select="$generatedBy" />
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$generationDate!=''">
				<b><xsl:text> on </xsl:text></b>
				<xsl:call-template name="formatDate">
					<xsl:with-param name="date" select="$generationDate" />
				</xsl:call-template>
			</xsl:if>
		</div>
	</xsl:if>
	<xsl:apply-templates select="n1:component/n1:section" />
	</xsl:template>

	<!-- Component/Section -->
	<xsl:template match="n1:component/n1:section">
		<xsl:apply-templates select="n1:title" />

		<xsl:apply-templates select="n1:text" />

		<xsl:apply-templates select="n1:component/n1:section" />


	</xsl:template>

	<!-- Title -->
	<xsl:template match="n1:title">

		<div class="section">
			<span>
				<a name="{generate-id(.)}" href="#toc">
					<h2><xsl:value-of select="." /></h2>
				</a>
			</span>
		</div>

	</xsl:template>
	
	<xsl:template match="text()">
	   <xsl:value-of select="." disable-output-escaping="no" />
	</xsl:template>	

	<!-- Text -->
	<xsl:template match="n1:text">
		<xsl:apply-templates />
	</xsl:template>

	<!-- paragraph -->
	<xsl:template match="n1:paragraph">
		<p>
			<xsl:apply-templates />
		</p>
	</xsl:template>

	<!-- Content w/ deleted text is hidden -->
	<xsl:template match="n1:content[@revised='delete']" />

	<!-- content -->
	<xsl:template match="n1:content">
		<xsl:apply-templates />
	</xsl:template>


	<!-- list -->
	<xsl:template match="n1:list">
		<xsl:if test="n1:caption">
			<span style="font-weight:bold; ">
				<xsl:apply-templates select="n1:caption" />
			</span>
		</xsl:if>
		<ul>
			<xsl:for-each select="n1:item">
				<li>
					<xsl:apply-templates />
				</li>
			</xsl:for-each>
		</ul>
	</xsl:template>

	<xsl:template match="n1:list[@listType='ordered']">
		<xsl:if test="n1:caption">
			<span style="font-weight:bold; ">
				<xsl:apply-templates select="n1:caption" />
			</span>
		</xsl:if>
		<ol>
			<xsl:for-each select="n1:item">
				<li>
					<xsl:apply-templates />
				</li>
			</xsl:for-each>
		</ol>
	</xsl:template>


	<!-- caption -->
	<xsl:template match="n1:caption">
		<xsl:apply-templates />
		<xsl:text>: </xsl:text>
	</xsl:template>

	<!-- Tables -->
	<xsl:template
		match="n1:table/@*|n1:thead/@*|n1:tfoot/@*|n1:tbody/@*|n1:colgroup/@*|n1:col/@*|n1:tr/@*|n1:th/@*|n1:td/@*">
		<xsl:copy>

			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="n1:table">
		<table class="data">

			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="n1:thead">
		<thead>

			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</thead>
	</xsl:template>

	<xsl:template match="n1:tfoot">
		<tfoot>

			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</tfoot>
	</xsl:template>

	<xsl:template match="n1:tbody">
		<tbody>

			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</tbody>
	</xsl:template>

	<xsl:template match="n1:colgroup">
		<colgroup>

			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</colgroup>
	</xsl:template>

	<xsl:template match="n1:col">
		<col>

			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</col>
	</xsl:template>

	<xsl:template match="n1:tr">
		<xsl:choose>
			<!-- modulus must be by 4 when being displayed in portal -->
			<xsl:when test="position() mod 4 = 0">
				<tr class="yui-dt-odd">
					<xsl:copy-of select="@*" />
					<xsl:apply-templates />
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<tr class="yui-dt-even">
					<xsl:copy-of select="@*" />
					<xsl:apply-templates />
				</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="n1:th">
		<th class="table" style="background-color:#ffffff; padding:6px; color:#5D646C">
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</th>
	</xsl:template>

	<xsl:template match="n1:td">
		<xsl:choose>
			<xsl:when test="@rowspan &gt; 1">
				<td class="table" style="background-color:#ffffff; padding:6px">
					<xsl:copy-of select="@*" />
					<xsl:apply-templates />
				</td>
			</xsl:when>
			<xsl:otherwise>
				<td class="table" style="padding:6px">
					<xsl:copy-of select="@*" />
					<xsl:apply-templates />
				</td>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="n1:table/n1:caption">
		<span style="font-weight:bold; ">
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<!-- RenderMultiMedia this currently only handles GIF's and JPEG's. It could, 
		however, be extended by including other image MIME types in the predicate 
		and/or by generating <object> or <applet> tag with the correct params depending 
		on the media type @ID =$imageRef referencedObject -->
	<xsl:template match="n1:renderMultiMedia">
		<xsl:variable name="imageRef" select="@referencedObject" />
		<xsl:choose>
			<xsl:when test="//n1:regionOfInterest[@ID=$imageRef]">
				<!-- Here is where the Region of Interest image referencing goes -->
				<xsl:if
					test='//n1:regionOfInterest[@ID=$imageRef]//n1:observationMedia/n1:value[@mediaType="image/gif" or @mediaType="image/jpeg"]'>
					<br clear='all' />
					<xsl:element name='img'>
						<xsl:attribute name='src'>
		 		 		 		 <xsl:value-of
							select='//n1:regionOfInterest[@ID=$imageRef]//n1:observationMedia/n1:value/n1:reference/@value' />
		 		 		     </xsl:attribute>
					</xsl:element>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<!-- Here is where the direct MultiMedia image referencing goes -->
				<xsl:if
					test='//n1:observationMedia[@ID=$imageRef]/n1:value[@mediaType="image/gif" or @mediaType="image/jpeg"]'>
					<br clear='all' />
					<xsl:element name='img'>
						<xsl:attribute name='src'>
		 		 		 		 <xsl:value-of
							select='//n1:observationMedia[@ID=$imageRef]/n1:value/n1:reference/@value' />
		 		 		     </xsl:attribute>
					</xsl:element>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Stylecode processing Supports Bold, Underline and Italics display -->

	<xsl:template match="//n1:*[@styleCode]">

		<xsl:if test="contains(@styleCode,'warning')">
			<p class="information-notification">
				<xsl:apply-templates />
			</p>
		</xsl:if>

		 <xsl:if test="contains(@styleCode,'Bold') and not (contains(@styleCode,'warning'))">
			<xsl:element name='b'>
				<xsl:apply-templates />
			</xsl:element>
		</xsl:if>

		 <xsl:if test="contains(@styleCode, 'Italics')">
			<xsl:element name='i'>
				<xsl:apply-templates />
			</xsl:element>
		</xsl:if>

		 <xsl:if test="contains(@styleCode, 'Underline')">
			<xsl:element name='u'>
				<xsl:apply-templates />
			</xsl:element>
		</xsl:if>

		<xsl:if
			test="contains(@styleCode,'Bold') and contains(@styleCode,'Italics') and not (contains(@styleCode, 'Underline'))">
			<xsl:element name='b'>
				<xsl:element name='i'>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:element>
		</xsl:if>

		<xsl:if
			test="contains(@styleCode,'Bold') and contains(@styleCode,'Underline') and not (contains(@styleCode, 'Italics'))">
			<xsl:element name='b'>
				<xsl:element name='u'>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:element>
		</xsl:if>

		<xsl:if
			test="contains(@styleCode,'Italics') and contains(@styleCode,'Underline') and not (contains(@styleCode, 'Bold'))">
			<xsl:element name='i'>
				<xsl:element name='u'>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:element>
		</xsl:if>

		<xsl:if
			test="contains(@styleCode,'Italics') and contains(@styleCode,'Underline') and contains(@styleCode, 'Bold')">
			<xsl:element name='b'>
				<xsl:element name='i'>
					<xsl:element name='u'>
						<xsl:apply-templates />
					</xsl:element>
				</xsl:element>
			</xsl:element>
		</xsl:if>

	</xsl:template>

	<!-- Superscript or Subscript -->
	<xsl:template match="n1:sup">
		<xsl:element name='sup'>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<xsl:template match="n1:sub">
		<xsl:element name='sub'>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<xsl:variable name="zebraStripes" >
		<xsl:choose>
			<xsl:when test="position() mod 2 = 0">
			<xsl:text>#9e9e9e</xsl:text>
			</xsl:when>
			<xsl:otherwise>
			<xsl:text>#ffffff</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
</xsl:stylesheet>