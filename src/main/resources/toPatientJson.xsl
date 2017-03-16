<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:n3="http://www.w3.org/1999/xhtml"
                xmlns:n1="urn:hl7-org:v3" xmlns:n2="urn:hl7-org:v3/meta/voc" xmlns:voc="urn:hl7-org:v3/voc"
                xmlns:exslt="http://exslt.org/common"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:output method="text" indent="yes"
                encoding="UTF-8" media-type="application/json" />

    <xsl:import href="orionCCD_v2.4.xsl" />

    <xsl:template name="transformAndSerializeDoc">
        <xsl:variable name="node">
            <xsl:apply-imports />
        </xsl:variable>
        <xsl:variable name="serializedNode">
            <xsl:call-template name="serializeNode">
                <xsl:with-param name="node" select="exslt:node-set($node)" />
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="normalize-space($serializedNode)" />
    </xsl:template>

    <xsl:template name="string-replace-all">
        <xsl:param name="text" />
        <xsl:param name="replace" />
        <xsl:param name="by" />
        <xsl:choose>
            <xsl:when test="$text = '' or $replace = ''or not($replace)" >
                <!-- Prevent this routine from hanging -->
                <xsl:value-of select="$text" />
            </xsl:when>
            <xsl:when test="contains($text, $replace)">
                <xsl:value-of select="substring-before($text,$replace)" />
                <xsl:value-of select="$by" />
                <xsl:call-template name="string-replace-all">
                    <xsl:with-param name="text" select="substring-after($text,$replace)" />
                    <xsl:with-param name="replace" select="$replace" />
                    <xsl:with-param name="by" select="$by" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="serializeNode">
        <xsl:param name="node" />
        <xsl:for-each select="$node">
            <xsl:choose>
                <xsl:when test="self::text()">
                    <xsl:variable name="newlines">
                        <xsl:call-template name="string-replace-all">
                            <xsl:with-param name="text" select="." />
                            <xsl:with-param name="replace" select="'&#xA;'" />
                            <xsl:with-param name="by" select="'\n'" />
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:variable name="Q">"</xsl:variable>
                    <xsl:variable name="EQ">\"</xsl:variable>
                    <xsl:variable name="quotes">
                        <xsl:call-template name="string-replace-all">
                            <xsl:with-param name="text" select="$newlines" />
                            <xsl:with-param name="replace" select="$Q" />
                            <xsl:with-param name="by" select="$EQ" />
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:value-of select="$quotes" />
                </xsl:when>
                <xsl:when test="self::*">
                    <xsl:text>&lt;</xsl:text>
                    <xsl:value-of select="name()"/>
                    <xsl:for-each select="@*">
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="name()" />
                        <xsl:if test=".">
                            <xsl:text>=\&quot;</xsl:text>
                            <xsl:value-of select="." />
                            <xsl:text>\&quot;</xsl:text>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:text>&gt;</xsl:text>
                    <xsl:for-each select="node()">
                        <xsl:call-template name="serializeNode">
                            <xsl:with-param name="node" select="." />
                        </xsl:call-template>
                    </xsl:for-each>
                    <xsl:text>&lt;/</xsl:text>
                    <xsl:value-of select="name()"/>
                    <xsl:text>&gt;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:for-each select="node()">
                        <xsl:call-template name="serializeNode">
                            <xsl:with-param name="node" select="." />
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <!-- CDA document -->
    <xsl:template match="/">
        <xsl:choose>
            <xsl:when test="/n1:ClinicalDocument">
                <xsl:apply-templates select="n1:ClinicalDocument" mode="json" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="*" mode="json"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="*" mode="json">
        {
        "error": "Unable to display document using CCD style sheet. Document is not a valid CCD as it does not contain a ClinicalDocument xml root element."
        }
    </xsl:template>

    <xsl:template match="n1:ClinicalDocument" mode="json">
        <xsl:variable name="patientRole" select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole" />
        <xsl:variable name="gender" select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:administrativeGenderCode/@displayName" />
        {
        "id": {
        "value": "<xsl:value-of select="$patientRole/n1:id/@extension" />"<xsl:if test="$patientRole/n1:id/@assigningAuthorityName">,
        "aaName": "<xsl:value-of select="$patientRole/n1:id/@assigningAuthorityName" />"</xsl:if><xsl:if test="$patientRole/n1:id/@root">,
        "root": "<xsl:value-of select="$patientRole/n1:id/@root" />"</xsl:if>
        },
        "name": {
        <xsl:call-template name="getNameJson"><xsl:with-param name="name" select="$patientRole/n1:patient/n1:name" /></xsl:call-template>
        },
        "gender": "<xsl:value-of select="$gender" />",
        "body": "<xsl:call-template name="transformAndSerializeDoc" />"
        }
    </xsl:template>

    <!-- Get a Name -->
    <xsl:template name="getNameJson">
        <xsl:param name="name" />
        <xsl:choose>
            <xsl:when test="$name/n1:family">
                "family": "<xsl:value-of select="translate($name/n1:family, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>"<xsl:if test="string-length($name/n1:given)!=0">,
                "given": "<xsl:value-of select="normalize-space($name/n1:given)"/>"</xsl:if><xsl:if test="string-length($name/n1:suffix)>0">,
                "suffix": "<xsl:value-of select="$name/n1:suffix"/>"</xsl:if>
            </xsl:when>
            <xsl:otherwise>
                "name": "<xsl:value-of select="normalize-space($name)"/>"
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>