<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:template match="invoice">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21.0cm" margin-top="1cm"
                    margin-left="2cm" margin-right="2cm" margin-bottom="1cm">
                    <fo:region-body margin-top="3cm" margin-bottom="2cm"/>
                    <fo:region-before extent="3cm"/>
                    <fo:region-after extent="1.5cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="A4">
                <!-- Header -->
                <fo:static-content flow-name="xsl-region-before">
                    <fo:block font-size="20pt" font-weight="bold" text-align="center" margin-bottom="1cm">
                        INVOICE
                    </fo:block>
                    <fo:block font-size="10pt" text-align="right">
                        Date: <xsl:value-of select="date"/>
                    </fo:block>
                    <fo:block font-size="10pt" text-align="right">
                        Invoice #: <xsl:value-of select="orderId"/>
                    </fo:block>
                </fo:static-content>

                <!-- Footer -->
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block font-size="8pt" text-align="center">
                        Page <fo:page-number/> of <fo:page-number-citation ref-id="last-page"/>
                    </fo:block>
                </fo:static-content>

                <!-- Content -->
                <fo:flow flow-name="xsl-region-body">
                    <!-- Customer Info -->
                    <fo:block font-size="10pt" margin-bottom="1cm">
                        <fo:block font-weight="bold">Customer Details:</fo:block>
                        <fo:block>Email: <xsl:value-of select="customerEmail"/></fo:block>
                    </fo:block>

                    <!-- Items Table -->
                    <fo:block>
                        <fo:table width="100%" border-collapse="collapse">
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="40%"/>
                            <fo:table-column column-width="15%"/>
                            <fo:table-column column-width="15%"/>
                            <fo:table-column column-width="20%"/>
                            
                            <!-- Table Header -->
                            <fo:table-header>
                                <fo:table-row background-color="#f5f5f5">
                                    <fo:table-cell border="1pt solid black" padding="2pt">
                                        <fo:block font-weight="bold">S.No</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black" padding="2pt">
                                        <fo:block font-weight="bold">Product</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black" padding="2pt">
                                        <fo:block font-weight="bold">Quantity</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black" padding="2pt">
                                        <fo:block font-weight="bold">Unit Price</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black" padding="2pt">
                                        <fo:block font-weight="bold">Total</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-header>

                            <!-- Table Body -->
                            <fo:table-body>
                                <xsl:for-each select="items/item">
                                    <fo:table-row>
                                        <fo:table-cell border="1pt solid black" padding="2pt">
                                            <fo:block><xsl:value-of select="position()"/></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid black" padding="2pt">
                                            <fo:block><xsl:value-of select="productName"/></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid black" padding="2pt">
                                            <fo:block><xsl:value-of select="quantity"/></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid black" padding="2pt">
                                            <fo:block><xsl:value-of select="unitPrice"/></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid black" padding="2pt">
                                            <fo:block><xsl:value-of select="total"/></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </xsl:for-each>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>

                    <!-- Total Amount -->
                    <fo:block margin-top="1cm" text-align="right" font-weight="bold">
                        Total Amount: <xsl:value-of select="totalAmount"/>
                    </fo:block>

                    <!-- Terms and Conditions -->
                    <fo:block margin-top="2cm" font-size="8pt">
                        <fo:block font-weight="bold">Terms and Conditions:</fo:block>
                        <fo:block>1. All prices are in INR</fo:block>
                        <fo:block>2. This is a computer generated invoice</fo:block>
                    </fo:block>

                    <fo:block id="last-page"/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet> 