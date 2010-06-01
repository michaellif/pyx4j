<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="anything"
    xmlns:pyx="anything">

    <xsl:namespace-alias stylesheet-prefix="x" result-prefix="xsl" />

    <xsl:output method="xml" indent="yes" cdata-section-elements="style script" />

    <xsl:template match="/">

        <!-- wrap input in xsl -->
        <x:stylesheet version="1.0">

            <x:output method="xml" indent="yes" cdata-section-elements="style script" />

            <x:template match="/*">
                <xsl:copy>
                    <xsl:apply-templates />
                </xsl:copy>
            </x:template>

            <!-- copy all elements by default -->
            <x:template match="*">
                <x:copy>
                    <x:copy-of select="@*" />
                    <x:apply-templates select="node()" />
                </x:copy>
            </x:template>

        </x:stylesheet>
    </xsl:template>

    <!-- remove all pyx:comment elements -->
    <xsl:template match="pyx:comment" />

    <!-- remove content from xsl:value-of elements -->
    <xsl:template match="xsl:value-of">
        <xsl:copy>
            <xsl:copy-of select="@*" />
        </xsl:copy>
    </xsl:template>

    <!-- copy all elements by default -->
    <xsl:template match="*">
        <xsl:copy>
            <xsl:copy-of select="@*" />
            <xsl:apply-templates select="node()" />
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>