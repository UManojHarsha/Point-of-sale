<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:template match="invoice">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21.0cm" margin="2cm">
                    <fo:region-body/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="A4">
                <fo:flow flow-name="xsl-region-body">
                    <!-- Header -->
                    <fo:block font-size="24pt" font-weight="bold" text-align="center" space-after="1cm">
                        Invoice
                    </fo:block>

                    <!-- Order Details -->
                    <fo:block font-size="12pt" space-after="0.5cm">
                        <fo:block>Order ID: <xsl:value-of select="orderId"/></fo:block>
                        <fo:block>Date: <xsl:value-of select="date"/></fo:block>
                        <fo:block>Customer Email: <xsl:value-of select="customerEmail"/></fo:block>
                    </fo:block>

                    <!-- Items Table -->
                    <fo:block font-size="12pt" space-after="0.5cm">
                        <fo:table table-layout="fixed" width="100%" border-collapse="separate">
                            <fo:table-column column-width="33%"/>
                            <fo:table-column column-width="33%"/>
                            <fo:table-column column-width="33%"/>

                            <!-- Table Header -->
                            <fo:table-header>
                                <fo:table-row font-weight="bold" background-color="#f0f0f0">
                                    <fo:table-cell border="1pt solid black" padding="2pt">
                                        <fo:block>Product Name</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black" padding="2pt">
                                        <fo:block>Quantity</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black" padding="2pt">
                                        <fo:block>TotalPrice</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-header>

                            <!-- Table Body -->
                            <fo:table-body>
                                <xsl:for-each select="items/item">
                                    <fo:table-row>
                                        <fo:table-cell border="1pt solid black" padding="2pt">
                                            <fo:block><xsl:value-of select="productName"/></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid black" padding="2pt">
                                            <fo:block><xsl:value-of select="quantity"/></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid black" padding="2pt">
                                            <fo:block><xsl:value-of select="total"/></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </xsl:for-each>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>

                    <!-- Total -->
                    <fo:block font-size="14pt" font-weight="bold" text-align="right" space-before="0.5cm">
                        Total Amount: <xsl:value-of select="totalAmount"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet> 