<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="anything"
 xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">

  <xsl:output method="xml" indent="yes"/>
  <xsl:namespace-alias stylesheet-prefix="x" result-prefix="xsl"/>

  <xsl:template match="/">

    <!-- wrap docx in xsl -->
    <x:stylesheet version="1.0">
      <x:output method="xml" indent="yes"/>

      <x:template match="/*">
        <xsl:copy>
          <xsl:apply-templates/>
        </xsl:copy>
      </x:template>

      <!-- copy all elements by default -->
      <x:template match="@*|*">
        <x:copy>
          <x:apply-templates select="@*|node()"/>
        </x:copy>
      </x:template>
    </x:stylesheet>

  </xsl:template>

  <xsl:template match="w:sdt">
    <xsl:choose>
      <xsl:when test="w:sdtPr/w:tag/@w:val">
        <x:for-each select="{w:sdtPr/w:tag/@w:val}">
          <xsl:apply-templates select="w:sdtContent/*"/>
        </x:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="w:sdtContent/*"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- match a <w:t> element -->
  <xsl:template match="w:sdt/w:sdtContent//w:t">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:choose>
        <xsl:when test="ancestor::w:sdt/w:sdtPr/w:text">
          <x:value-of select="text()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>

  <!-- copy all elements by default -->
  <xsl:template match="@*|*">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>