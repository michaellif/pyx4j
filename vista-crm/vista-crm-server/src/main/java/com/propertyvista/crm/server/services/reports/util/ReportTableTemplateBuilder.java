/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 24, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.util;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;

import com.propertyvista.domain.dashboard.gadgets.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.type.ListerGadgetBaseMetadata;

/**
 * 
 * Warning: this is not THREAD SAFE!!!
 * 
 */
public class ReportTableTemplateBuilder {

    private final IEntity proto;

    private final ListerGadgetBaseMetadata metadata;

    private StringBuilder template;

    private Map<String, Integer> columnWidths;

    private int tableWidth;

    private int identLevel = 0;

    private String identString;

    public static void main(String[] argv) {

    }

    public ReportTableTemplateBuilder(IEntity proto, ListerGadgetBaseMetadata metadata) {
        this.proto = proto;
        this.metadata = metadata;
    }

    public String generateReportTemplate() {
        init();

        return template.toString();
    }

    private void init() {
        template = new StringBuilder();
        identLevel = 0;
        identString = "";
        initEvenColumnWidths();

        genReportBegin();
        genPropertiesDeclarations();
        genStyles();
        genSubStatasetSection();
        genParametersSection();
        genReportEnd();
    }

    private void initEvenColumnWidths() {
        columnWidths = new HashMap<String, Integer>();

        if (metadata.columnDescriptors().isEmpty()) {
            return;
        }

        int columnWidth = tableWidth / metadata.columnDescriptors().size();
        for (ColumnDescriptorEntity columnDescriptor : metadata.columnDescriptors()) {
            columnWidths.put(columnDescriptor.propertyPath().getValue(), columnWidth);
        }
        columnWidths.put(metadata.columnDescriptors().get(metadata.columnDescriptors().size() - 1).propertyPath().getValue(), tableWidth - columnWidth
                * metadata.columnDescriptors().size());
    }

    private void genReportBegin() {
        template.append("<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" name=\"Property Vista Report\" pageWidth=\"555\" pageHeight=\"300\" whenNoDataType=\"AllSectionsNoDetail\" columnWidth=\"555\" leftMargin=\"0\" rightMargin=\"0\" topMargin=\"10\" bottomMargin=\"10\">\n");
    }

    private void genReportEnd() {
        template.append("</jasperReport>\n");
    }

    private void genPropertiesDeclarations() {
        template.append("<property name=\"ireport.zoom\" value=\"1.0\"/>").append("<property name=\"ireport.x\" value=\"0\"/>\n")
                .append("<property name=\"ireport.y\" value=\"0\"/>\n");
    }

    private void genStyles() {
        //@formatter:off
        template
        .append("<style name=\"table\">")
            .append("<box>")
                .append("<pen lineWidth=\"0.5\" lineColor=\"#FFFFFF\"/>")
            .append("</box>")
        .append("</style>")
        
        .append("<style name=\"table_TH\" style=\"table\" mode=\"Opaque\" forecolor=\"#FFFFF\" backcolor=\"#FFFFFF\">")
            .append("<box>")
                .append("<pen lineWidth=\"0.5\" lineColor=\"#000000\"/>")
            .append("</box>")
        .append("</style>")
        
        .append("<style name=\"table_TD\" style=\"table\" mode=\"Opaque\" forecolor=\"#FFFFF\" backcolor=\"#FFFFFF\">")
            .append("<box>")
                .append("<pen lineWidth=\"0.5\" lineColor=\"#000000\"/>")
             .append("</box>")
        .append("</style>")
        
        .append("<style name=\"table_text\" fontSize=\"6\"/>")
        
        .append("<style name=\"table_TH_text\" style=\"table_text\" forecolor=\"#000000\"/>")
        
        .append("<style name=\"table_TD_text\" style=\"table_text\" forecolor=\"#000000\"/>");
        //@formatter:on
    }

    private void genSubStatasetSection() {
        template.append("<subDataset name=\"Dataset\">\n");
        template.append("<parameter name=\"COLUMNS\" class=\"java.util.HashMap\"/>\n");
        for (String memberName : proto.getEntityMeta().getMemberNames()) {
            generateFieldDeclaration(proto.getMember(memberName));
        }
        template.append("</subDataset>\n");
    }

    private void genParametersSection() {
        append("<parameter name=\"COLUMNS\" class=\"java.util.HashMap\"/>");
        append("<parameter name=\"TITLE\" class=\"java.lang.String\"/>");
    }

    private void genTitleWithTableSection() {
        //@formatter:off
        appendI("<title>");            
            appendI("<band height=\"100\" splitType=\"Stretch\">");                
                appendI("<frame>");
                                       
                appendD("/<frame>");                
            appendD("</band>");            
        appendD("</title>");
        //@formatter:on
    }

    private void generateFieldDeclaration(IObject<?> member) {
        template.append("<field name =\"").append(member.getFieldName()).append("\" class=\"").append(member.getValueClass().getName()).append("\">")
                .append('\n');
    }

    private void generateTableColumn(ColumnDescriptorEntity columnDescriptor) {
        String path = columnDescriptor.propertyPath().getValue();
        IObject<?> property = proto.getMember(new Path(path));

        String columnValueExpression = columnValueExpression(property);
        String columnWidthExpression = columnWidths.get(path).toString();
        String columnSelectExpression = "$P{COLUMNS}.containsKey(\"" + path + "\")";
        String columnNameExpression = "$P{COLUMNS}.get(\"" + path + "\")";
        String patternExpression = patternExpression(property);

        template.append("<jr:column width=\"").append(columnWidthExpression).append("\">");
        template.append("<printWhenExpression><![CDATA[").append(columnSelectExpression).append("]]></printWhenExpression>");
        template.append("<jr:columnHeader style=\"table_TH\" height=\"10\" rowSpan=\"1\">");
        template.append("<textField isStretchWithOverflow=\"true\">");
        template.append("<reportElement style=\"table_TH_text\" x=\"0\" y=\"0\" width=\"").append(columnWidthExpression).append("\" height=\"10\"/>");
        template.append("<textElement markup=\"none\"/>");
        template.append("<textFieldExpression><![CDATA[").append(columnNameExpression).append("]]></textFieldExpression>");
        template.append("</textField>");
        template.append("</jr:columnHeader>");
        template.append("<jr:detailCell style=\"table_TD\" height=\"10\" rowSpan=\"1\">");
        template.append("<textField isStretchWithOverflow=\"true\" isBlankWhenNull=\"true\"").append(patternExpression).append(">");
        template.append("<reportElement style=\"table_TD_text\" x=\"0\" y=\"0\" width=\"").append(columnWidthExpression).append("\" height=\"10\"/>");
        template.append("<textElement/>");
        template.append("<textFieldExpression><![CDATA[").append(columnValueExpression).append("]]></textFieldExpression>");
        template.append("</textField>");
        template.append("</jr:detailCell>");
        template.append("</jr:column>");
    }

    private static String patternExpression(IObject<?> property) {
        return "";
    }

    private static String columnValueExpression(IObject<?> property) {
        String fieldName = property.getFieldName();
        String[] splittedName = fieldName.split("/");
        boolean isSubProperty = splittedName.length > 1;
        String fieldIdenitfier = isSubProperty ? splittedName[0] : fieldName;
        StringBuilder columnValueExpressionBuilder = new StringBuilder(fieldIdenitfier);
        if (isSubProperty) {
            columnValueExpressionBuilder.append(".");
            for (int i = 1; i < splittedName.length; ++i) {
                columnValueExpressionBuilder.append(splittedName[i]).append("()");
                if (i != (splittedName.length - 1)) {
                    columnValueExpressionBuilder.append(".");
                }
            }
        }
        if (!(property instanceof IPrimitive)) {
            columnValueExpressionBuilder.append(".getStringView()");
        } else if (isSubProperty) {
            columnValueExpressionBuilder.append(".getValue()");
        }
        return columnValueExpressionBuilder.toString();
    }

    private void append(String str) {
        template.append(identString).append(str).append("\n");
    }

    private void appendI(String str) {
        append(str);
        identInc();
    }

    private void appendD(String str) {
        identDec();
        append(str);
    }

    private void identInc() {
        identLevel += 1;

        identString = identString + "    ";
    }

    private void identDec() {
        identLevel -= 1;
        identString = identString.substring(0, identString.length() - 4);
    }
}
