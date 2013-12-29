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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.Path;

import com.propertyvista.domain.dashboard.gadgets.common.ColumnDescriptorEntity;

/**
 * This class knows to build templates (designs) for tabular reports that has columns that can hide themselves on demand;
 * The created report will have two parameters:
 * <ul>
 * <li><b>COLUMNS</b> which is a <code>java.util.HashMap</code> of (propertyPath, columnName)</li> of the <i>visible</i> columns</li>
 * <li><b>TITLE</b> which is a <code>java.lang.String</code> that holds the table title</li>
 * </ul>
 * 
 * <b>WARNING:</b> Please keep in mind that this class not THREAD SAFE!!!
 */
public class ReportTableTemplateBuilder {

    private static final int DEFAULT_TABLE_WIDTH = 554;

    private final IEntity proto;

    private StringBuilder template;

    private Map<String, Integer> columnWidths;

    private int tableWidth;

    private String identString;

    public LinkedList<String> elementStack;

    public boolean isElementDefinitionInProgress = false;

    private final List<ColumnDescriptorEntity> columnDescriptors;

    public ReportTableTemplateBuilder(IEntity proto, List<ColumnDescriptorEntity> columnDescriptors) {
        this.proto = proto;
        this.columnDescriptors = columnDescriptors;
    }

    public String generateReportTemplate() {
        init();
        build();
        String generatedtemplate = template.toString();

        if (false) {
            int lineNum = 0;
            for (String line : generatedtemplate.split("\n")) {
                System.out.print(++lineNum);
                System.out.print(": ");
                System.out.println(line);
            }
        }

        return generatedtemplate;
    }

    private void init() {
        template = new StringBuilder();
        elementStack = new LinkedList<String>();
        identString = "";
        tableWidth = DEFAULT_TABLE_WIDTH;
        initEvenColumnWidths();
    }

    private void build() {
        //@formatter:off
        elo("jasperReport")
                .attr("xmlns", "http://jasperreports.sourceforge.net/jasperreports")
                .attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
                .attr("xsi:schemaLocation", "http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd")
                .attr("name", "Property Vista Report")
                .attr("whenNoDataType", "AllSectionsNoDetail")
                .attr("pageWidth", "555")
                .attr("pageHeight", "300")                
                .attr("columnWidth", "555")
                .attr("leftMargin", "0")
                .attr("rightMargin", "0")
                .attr("topMargin", "10")
                .attr("bottomMargin", "10")
                .add();
        
            addProperties();
            addStyles();
            addSubDtatasetDeclaration();
            addParametersDeclaration();
                        
            addTitleSectionWithTable();
            
            // TODO not sure whether the following sections are mandatory
            addPageHeader();
            addColumnHeader();
            addDetail();
            addColumnFooter();
            addPageFooter();
            addSummary();
            
        elc("jasperReport");
        //@formatter:on        
    }

    private void initEvenColumnWidths() {
        columnWidths = new HashMap<String, Integer>();

        if (columnDescriptors.isEmpty()) {
            return;
        }

        int columnWidth = tableWidth / columnDescriptors.size();
        for (ColumnDescriptorEntity columnDescriptor : columnDescriptors) {
            columnWidths.put(columnDescriptor.propertyPath().getValue(), columnWidth);
        }

        // compensate for integer truncation for the last column
        String lastColumnPath = columnDescriptors.get(columnDescriptors.size() - 1).propertyPath().getValue();
        columnWidths.put(lastColumnPath, tableWidth - columnWidth * (columnDescriptors.size() - 1));
    }

    private void addProperties() {
        el("property").attr("name", "ireport.zoom").attr("value", "1.0").add();
        el("property").attr("name", "ireport.x").attr("value", "0").add();
        el("property").attr("name", "ireport.y").attr("value", "0").add();
    }

    private void addStyles() {//@formatter:off
        elo("style").attr("name", "table").add();
            elo("box").add();
                el("pen").attr("lineWidth", "0.5").attr("lineColor", "#FFFFFF").add();
            elc("box");
        elc("style");
        
        elo("style")
                .attr("name", "table_TH")
                .attr("style", "table")
                .attr("mode", "Opaque")
                .attr("forecolor", "#FFFFFF")
                .attr("backcolor", "#FFFFFF")
                .add();
            elo("box").add();
                el("pen").attr("lineWidth", "0.5").attr("lineColor", "#000000").add();
            elc("box");
        elc("style");
        
        elo("style")
                .attr("name", "table_TD")
                .attr("style", "table")
                .attr("mode", "Opaque")
                .attr("forecolor", "#FFFFFF")
                .attr("backcolor", "#FFFFFF")
                .add();
            elo("box").add();
                el("pen").attr("lineWidth", "0.5").attr("lineColor", "#000000").add();
            elc("box");
        elc("style");
        
        el("style").attr("name", "table_text").attr("fontSize", "6").add();
        
        el("style").attr("name", "table_TH_text").attr("style", "table_text").attr("forecolor", "#000000").add();
        
        el("style").attr("name", "table_TD_text").attr("style", "table_text").attr("forecolor", "#000000").add();
        //@formatter:on        
    }

    private void addSubDtatasetDeclaration() {//@formatter:off        
        elo("subDataset").attr("name", "Dataset").add();
            el("parameter").attr("name", "COLUMNS").attr("class", "java.util.HashMap").add();
            for(String memberName: proto.getEntityMeta().getMemberNames()) {
                addFieldDeclaration(proto.getMember(memberName));
            }
        elc("subStatSet");      
    } //@formatter:on

    private void addParametersDeclaration() {
        el("parameter").attr("name", "COLUMNS").attr("class", "java.util.HashMap").add();
        el("parameter").attr("name", "TITLE").attr("class", "java.lang.String").add();
    }

    private void addTitleSectionWithTable() {//@formatter:off
        elo("title").add();
            elo("band").attr("height", "100").attr("splitType", "Stretch").add();
                elo("frame").add();
                    el("reportElement").attr("x", "0").attr("y", "0").attr("width", "554").attr("height", "30").add();
                    elo("box").add();
                        el("pen").attr("lineWidth", "1.0").attr("lineStyle", "Solid").add();
                    elc("box");
                    elo("textField").add();
                        el("reportElement").attr("x", "0").attr("y", "0").attr("width", "553").attr("height", "29").add();
                        elo("textElement").attr("textAlignment", "Center").attr("verticalAlignment", "Middle").add();
                            el("font").attr("size", "10").add();
                        elc("textElement");
                        elo("textFieldExpression").add();
                            line("<![CDATA[$P{TITLE}]]>");
                        elc("textFieldExpression");
                    elc("textField");
                elc("frame");
                
                elo("componentElement").add();
                    el("reportElement").attr("key", "table").attr("x", "0").attr("y", "31").attr("width", "555").attr("height", "30").add();
                    elo("jr:table")
                            .attr("xmlns:jr", "http://jasperreports.sourceforge.net/jasperreports/components")
                            .attr("xsi:schemaLocation", "http://jasperreports.sourceforge.net/jasperreports/components http://jasperreports.sourceforge.net/xsd/components.xsd")
                            .add();
                        elo("datasetRun").attr("subDataset", "Dataset").add();
                            elo("parametersMapExpression").add();
                                line("<![CDATA[$P{REPORT_PARAMETERS_MAP}]]>");
                            elc("parametersMapExpression");
                            elo("dataSourceExpression").add();
                                line("<![CDATA[((com.pyx4j.entity.report.JRIEntityCollectionDataSource)$P{REPORT_DATA_SOURCE}).cloneDataSource()]]>");
                            elc("dataSourceExpression");
                        elc("datasetRun");
                                                
                        appendColumns();
                        
                    elc("jr:table");
                elc("componentElement");     
                
            elc("band");
        elc("title");
    } //@formatter:on

    private void addFieldDeclaration(IObject<?> member) {//@formatter:off
        el("field")
                .attr("name", member.getFieldName())
                .attr("class", member.getValueClass().getName())
                .add();
    }//@formatter:on

    private void addTableColumnDeclaration(ColumnDescriptorEntity columnDescriptor) {
        String path = columnDescriptor.propertyPath().getValue();
        IObject<?> property = proto.getMember(new Path(path));

        String columnWidth = columnWidths.get(path).toString();
        String columnSelectExpression = "$P{COLUMNS}.containsKey(\"" + path + "\")";
        String columnNameExpression = "$P{COLUMNS}.get(\"" + path + "\")";
        String columnValueExpression = columnValueExpression(property);
        String patternExpression = patternExpression(property);

        //@formatter:off
        elo("jr:column").attr("width", columnWidth).add();
            elo("printWhenExpression").add();
                CDATA(columnSelectExpression);
            elc("printWhenExpression");
            
            elo("jr:columnHeader").attr("style", "table_TH").attr("height", "10").attr("rowSpan", "1").add();
                elo("textField").attr("isStretchWithOverflow", "true").add();                
                    el("reportElement")
                            .attr("style", "table_TH_text")
                            .attr("x", "0")
                            .attr("y", "0")
                            .attr("width", columnWidth)
                            .attr("height", "10")                            
                            .add();
                    el("textElement").attr("markup", "none").add();
                    elo("textFieldExpression").add();                        
                        CDATA(columnNameExpression);
                    elc("textFieldExpression");
                elc("textField");
            elc("jr:columnHeader");
            
            elo("jr:detailCell").attr("style", "table_TD").attr("height", "10").attr("rowSpan", "1").add();
                elo("textField")
                        .attr("isStretchWithOverflow", "true")
                        .attr("isBlankWhenNull", "true")
                        .attr("pattern", patternExpression).add();
                   
                    el("reportElement")
                            .attr("style", "table_TD_text")
                            .attr("x", "0")
                            .attr("y", "0")
                            .attr("width", columnWidth)
                            .attr("height", "10")
                            .add();
                    el("textElement").add();
                    elo("textFieldExpression").add();
                        CDATA(columnValueExpression);
                    elc("textFieldExpression");
                elc("textField");
            elc("jr:detailCell");
        elc("jr:column");
    }//@formatter:on

    private void addPageHeader() {//@formatter:off
        elo("pageHeader").add();
            el("band").attr("splitType", "Stretch").add();            
        elc();
    }//@formatter:on

    private void addColumnHeader() {//@formatter:off
        elo("columnHeader").add();
            el("band").attr("splitType", "Stretch").add();            
        elc();
    }//@formatter:on

    private void addDetail() {//@formatter:off
        elo("detail").add();
            el("band").attr("splitType", "Stretch").add();            
        elc();
    }//@formatter:on

    private void addColumnFooter() {//@formatter:off
        elo("columnFooter").add();
            el("band").attr("splitType", "Stretch").add();            
        elc();
    }//@formatter:on

    private void addPageFooter() {//@formatter:off
        elo("pageFooter").add();
            el("band").attr("splitType", "Stretch").add();            
        elc();
    }//@formatter:on

    private void addSummary() {//@formatter:off
        elo("summary").add();
            el("band").attr("splitType", "Stretch").add();            
        elc();
    }//@formatter:on

    private void appendColumns() {
        for (ColumnDescriptorEntity columnDescriptor : columnDescriptors) {
            addTableColumnDeclaration(columnDescriptor);
        }
    }

    private static String patternExpression(IObject<?> property) {
        Class<?> clazz = property.getValueClass();
        // TODO refactor in declarative style and use property @Format if possible 
        if (BigDecimal.class.equals(clazz)) {
            return "###0.00";

        } else if (Double.class.equals(clazz)) {
            return "###0.00";

        } else if (LogicalDate.class.equals(clazz)) {
            return "MM/dd/yyyy";

        } else {
            return null;
        }
    }

    private static String columnValueExpression(IObject<?> property) {
        String path = property.getPath().toString();
        String fieldName = path.substring(path.indexOf('/') + 1, path.lastIndexOf('/'));
        String[] splittedName = fieldName.split("/");
        boolean isSubProperty = splittedName.length > 1;
        String fieldIdenitfier = isSubProperty ? splittedName[0] : fieldName;
        StringBuilder columnValueExpressionBuilder = new StringBuilder();

        columnValueExpressionBuilder.append("$F{");
        if (isSubProperty) {
            columnValueExpressionBuilder.append(splittedName[0]);
        } else {
            columnValueExpressionBuilder.append(fieldIdenitfier);
        }
        columnValueExpressionBuilder.append("}");

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

    private void line(String str) {
        template.append(identString).append(str).append("\n");
    }

    private void CDATA(String expression) {
        line("<![CDATA[" + expression + "]]>");
    }

    private void identInc() {
        identString = identString + "    ";
    }

    private void identDec() {
        identString = identString.substring(0, identString.length() - 4);
    }

    private ElementBuilder elo(String element) {
        return new ElementBuilder(element, false);
    }

    private ElementBuilder el(String element) {
        return new ElementBuilder(element, true);
    }

    private void elc() {
        if (ReportTableTemplateBuilder.this.isElementDefinitionInProgress) {
            throw new IllegalStateException(
                    "attempting to close element definition before finishing element openning: please end previous definition with 'add()'");
        }
        String element = ReportTableTemplateBuilder.this.elementStack.pop();
        identDec();
        line("</" + element + ">");
    }

    /** this is just for comment */
    private void elc(String element) {
        elc();
    }

    private class ElementBuilder {

        private final StringBuffer element;

        private final boolean isClosed;

        public ElementBuilder(String element, boolean isClosed) {
            if (ReportTableTemplateBuilder.this.isElementDefinitionInProgress) {
                throw new IllegalStateException(
                        "attempting to start new element definition before finishing another one: please end previous definition with 'add()'");
            }
            ReportTableTemplateBuilder.this.isElementDefinitionInProgress = true;

            this.isClosed = isClosed;
            this.element = new StringBuffer();
            this.element.append("<").append(element);

            if (!isClosed) {
                ReportTableTemplateBuilder.this.elementStack.push(element);
            }
        }

        /**
         * add an attribute if <code>value != null</code>
         * 
         * @param attribute
         * @param value
         * @return
         */
        public ElementBuilder attr(String attribute, String value) {
            if (value != null) {
                element.append(" ").append(attribute).append("=\"").append(value).append("\"");
            }
            return this;
        }

        public void add() {
            if (isClosed) {
                element.append("/");
            }
            element.append(">");

            ReportTableTemplateBuilder.this.line(element.toString());
            if (!isClosed) {
                identInc();
            }
            ReportTableTemplateBuilder.this.isElementDefinitionInProgress = false;
        }
    }
}
