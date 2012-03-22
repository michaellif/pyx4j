ENTITY = "UnitAvailabilityStatus"

# list of (propertyPath, class, isPrimitive, width)

COLUMNS = [
           ['building/propertyCode', 'com.propertyvista.domain.property.asset.building.Building', True, 5],
           ['building/externalId', 'com.propertyvista.domain.property.asset.building.Building', True, 5],
           ['building/info/name', 'com.propertyvista.domain.property.asset.building.Building', True, 5],
           ['building/info/address', 'com.propertyvista.domain.property.asset.building.Building', False, 5],
           ['building/propertyManager/name', 'com.propertyvista.domain.property.asset.building.Building', True, 5],
           ['building/complex/name', 'com.propertyvista.domain.property.asset.building.Building', True, 5],

           ['unit/info/number', 'com.propertyvista.domain.property.asset.unit.AptUnit', True, 5],
           
           ['floorplan/name', 'com.propertyvista.domain.property.asset.Floorplan', True, 5],
           ['floorplan/marketingName', 'com.propertyvista.domain.property.asset.Floorplan', True, 5],
           
           ['vacancyStatus', 'com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.Vacancy', True, 5],
           ['rentedStatus',  'com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentedStatus', True, 5],
           ['scoping', 'com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.Scoping', True, 5],
           ['rentReadinessStatus', 'com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentReadiness', True, 5],
           ['unitRent', 'java.math.BigDecimal', True, 5],
           ['marketRent', 'java.math.BigDecimal', True, 5],
           ['rentDeltaAbsolute', 'java.math.BigDecimal', True, 5],
           ['rentDeltaRelative', 'java.math.BigDecimal', True, 5],
           ['rentEndDay', 'com.pyx4j.commons.LogicalDate', True, 5],
           ['moveInDay', 'com.pyx4j.commons.LogicalDate', True, 5],
           ['rentedFromDay', 'com.pyx4j.commons.LogicalDate', True, 5],
           ['daysVacant', 'java.lang.Integer', True, 5],
           ['revenueLost', 'java.math.BigDecimal', True, 5]]

TYPE_TO_PATTERN = {
        'java.math.BigDecimal':'###0.00',
        'java.lang.Double':'###0.00',
        'com.pyx4j.commons.LogicalDate':'MM/dd/yyyy',        
}


TABLE_WIDTH = 554
EQUAL_WIDTH = True


# adjust column width
if EQUAL_WIDTH:
    column_width = int(TABLE_WIDTH / len(COLUMNS))    
    for column in COLUMNS:
        column[-1] = column_width
    # fix the last column with the remainder
    COLUMNS[-1][-1] += TABLE_WIDTH - column_width * len(COLUMNS)


COLUMN_TEMPLATE = """
<jr:column width="{columnWidthExp}">
    <printWhenExpression><![CDATA[{columnSelectExpression}]]></printWhenExpression>
        <jr:columnHeader style="table_TH" height="10" rowSpan="1">
            <textField isStretchWithOverflow="true">
                <reportElement style="table_TH_text" x="0" y="0" width="{columnWidthExp}" height="10"/>
		<textElement markup="none"/>
		<textFieldExpression><![CDATA[{columnNameExpression}]]></textFieldExpression>
            </textField>
    </jr:columnHeader>
    <jr:detailCell style="table_TD" height="10" rowSpan="1">
        <textField isStretchWithOverflow="true" isBlankWhenNull="true" {patternExp}>
            <reportElement style="table_TD_text" x="0" y="0" width="{columnWidthExp}" height="10"/>
            <textElement/>
            <textFieldExpression><![CDATA[{columnValueExpression}]]></textFieldExpression>
        </textField>
    </jr:detailCell>
</jr:column>
"""

REPORT_TEMPLATE = '''<?xml version="1.0" encoding="UTF-8"?>
<jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd" name="Unit Availability" pageWidth="555" pageHeight="300" whenNoDataType="AllSectionsNoDetail" columnWidth="555" leftMargin="0" rightMargin="0" topMargin="10" bottomMargin="10">
	<property name="ireport.zoom" value="1.0"/>
	<property name="ireport.x" value="0"/>
	<property name="ireport.y" value="0"/>
    <style name="table">
    	<box>
			<pen lineWidth="0.5" lineColor="#FFFFFF"/>
         </box>
	</style>
	<style name="table_TH" style="table" mode="Opaque" forecolor="#FFFFF" backcolor="#FFFFFF">
		<box>
			<pen lineWidth="0.5" lineColor="#000000"/>
		</box>
	</style>
	<style name="table_TD" style="table" mode="Opaque" forecolor="#FFFFF" backcolor="#FFFFFF">
		<box>
			<pen lineWidth="0.5" lineColor="#000000"/>
		</box>
	</style>
	<style name="table_text" fontSize="6"/>
	<style name="table_TH_text" style="table_text" forecolor="#000000"/>
	<style name="table_TD_text" style="table_text" forecolor="#000000"/>
	<subDataset name="Dataset">
            <parameter name="COLUMNS" class="java.util.HashMap"/>
            {fields}
	</subDataset>
	<parameter name="COLUMNS" class="java.util.HashMap"/>
	<parameter name="TITLE" class="java.lang.String"/>
	<title>
		<band height="100" splitType="Stretch">
			<frame>
				<reportElement x="0" y="0" width="554" height="30"/>
				<box>
					<pen lineWidth="1.0" lineStyle="Solid"/>
				</box>
				<textField>
					<reportElement x="0" y="0" width="553" height="29"/>
					<textElement textAlignment="Center" verticalAlignment="Middle">
						<font size="10"/>
					</textElement>
					<textFieldExpression><![CDATA[$P{{TITLE}}]]></textFieldExpression>
				</textField>
			</frame>
                        <componentElement>
                            <reportElement key="table" x="0" y="31" width="555" height="30"/>
                            <jr:table xmlns:jr="http://jasperreports.sourceforge.net/jasperreports/components" xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports/components http://jasperreports.sourceforge.net/xsd/components.xsd">
				<datasetRun subDataset="Dataset">
                                    <parametersMapExpression><![CDATA[$P{{REPORT_PARAMETERS_MAP}}]]></parametersMapExpression>
                                    <dataSourceExpression><![CDATA[((com.pyx4j.entity.report.JRIEntityCollectionDataSource)$P{{REPORT_DATA_SOURCE}}).cloneDataSource()]]></dataSourceExpression>
				</datasetRun>
				{columns}
			    </jr:table>
                        </componentElement>
		</band>
	</title>
	<pageHeader>
		<band splitType="Stretch"/>
	</pageHeader>
	<columnHeader>
		<band splitType="Stretch"/>
	</columnHeader>
	<detail>
		<band splitType="Stretch"/>
	</detail>
	<columnFooter>
		<band splitType="Stretch"/>
	</columnFooter>
	<pageFooter>
		<band splitType="Stretch"/>
	</pageFooter>
	<summary>
		<band splitType="Stretch"/>
	</summary>
</jasperReport>
'''

columns = []
fields = set()
reportFile = open(r'C:\work\projects\pyx\propertyvista\vista-crm\vista-crm-server\src\main\resources\reports\UnitAvailability.jrxml', 'w')

for name, clazz, isPrimitive, width in COLUMNS:    

    path = '/'.join([ENTITY, name, ''])
    columnSelectExpression = '$P{COLUMNS}.containsKey("' + path + '")'
    columnNameExpression = '$P{COLUMNS}.get("' + path + '")'

    splittedName = name.split('/')
    isSubProperty = len(splittedName) > 1
    fieldIdentifier = splittedName[0] if isSubProperty else name
    
    columnValueExpression = '$F{' + fieldIdentifier + '}'
    if isSubProperty:
        columnValueExpression += '.' + '.'.join([seg + '()' for seg in splittedName[1:]])    
    if not isPrimitive:
        columnValueExpression += ".getStringView()"
    elif isSubProperty:
        columnValueExpression += ".getValue()"
        
    patternExpr = 'pattern="{pattern}"'.format(pattern=TYPE_TO_PATTERN[clazz]) if clazz in TYPE_TO_PATTERN else ""
    #patternExpr = 'pattern="###0.00"' if any(clazz.endswith(numerictype) for numerictype in ('BigDecimal', 'Double')) else ""
    columns.append(COLUMN_TEMPLATE.format(columnSelectExpression=columnSelectExpression,
                                          columnNameExpression=columnNameExpression,
                                          columnValueExpression=columnValueExpression,
                                          columnWidthExp='{}'.format(width),                                          
                                          patternExp=patternExpr))
    fields.add('<field name="{name}" class="{clazz}"/>'.format(name=fieldIdentifier, clazz=clazz))

print (REPORT_TEMPLATE.format(fields="\n".join(fields), columns="\n".join(columns)), file=reportFile)
