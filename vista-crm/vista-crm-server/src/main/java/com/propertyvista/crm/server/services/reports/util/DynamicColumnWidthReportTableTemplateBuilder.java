/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 28, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.util;

import java.util.ArrayList;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;

import com.propertyvista.domain.dashboard.gadgets.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.type.ListerGadgetBaseMetadata;

public class DynamicColumnWidthReportTableTemplateBuilder extends XMLBuilder {

    private enum Styles {

        TITLE, TABLE, TABLE_HEADER, TABLE_COLUMN_TITLE, TABLE_ROW;
    }

    private final IEntity proto;

    private final ListerGadgetBaseMetadata metadata;

    private final int pageWidth = 555;

    private int[] columnWidths;

    private ArrayList<ColumnDescriptorEntity> visibleColumns;

    private final Integer fontSize = 6;

    private final Integer fontSizeTitle = 10;

    private final Integer padding = 2;

    public DynamicColumnWidthReportTableTemplateBuilder(IEntity proto, ListerGadgetBaseMetadata metadata) {
        this.proto = proto;
        this.metadata = metadata;
        createTemplate();
    }

    private void createTemplate() {//@formatter:off
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
        
            declareCustomProperties();
            declareStyles();
            declareParameters();
            declareFields();
            
            addTitle();
            addPageHeader();
            addColumnHeader();
            
            addDetail();
            
            addColumnFooter();
            addPageFooter();
            addSummary();
        elc("jasperReport");
    }//@formatter:on

    private void declareCustomProperties() {//@formatter:off
        // I'm commenting this out because it's probably something to do wiht IReport          
//        el("property").attr("name", "ireport.zoom").attr("value", "1.0").add();
//        el("property").attr("name", "ireport.x").attr("value", "0").add();
//        el("property").attr("name", "ireport.y").attr("value", "0").add();
    }//@formatter:on

    private void declareStyles() {//@formatter:off
        elo("style")
            .attr("name", Styles.TITLE.name())
            .attr("fontSize", fontSizeTitle.toString())
            .attr("pdfFontName", "Helvetica")
            .add();
        elc("style");        
        
        
        elo("style")
            .attr("name", Styles.TABLE.name())
            .attr("fontSize", fontSize.toString())
            .attr("pdfFontName", "Helvetica")
            .add();
        elc("style");
        
        elo("style")
                .attr("name", Styles.TABLE_HEADER.name())
                .attr("style", Styles.TABLE.name())
                .add();
            elo("box").add();
                el("topPen").attr("lineWidth", "0.5").attr("lineColor", "#000000").attr("lineStyle", "Solid").add();
                el("bottomPen").attr("lineWidth", "0.5").attr("lineColor", "#000000").attr("lineStyle", "Solid").add();
            elc("box");
        elc("style");        

        
        elo("style")
                .attr("name", Styles.TABLE_COLUMN_TITLE.name())
                .attr("style", Styles.TABLE.name())
                .add();
        elc("style");        
        
        elo("style")
                .attr("name", Styles.TABLE_ROW.name())
                .attr("style", Styles.TABLE.name())
                .attr("mode", "Opaque")
                .add();
            elo("conditionalStyle").add();
                elo("conditionExpression").add();
                    CDATA("new Boolean($V{REPORT_COUNT}.intValue() % 2 == 0)");
                elc("conditionExpression");
                el("style")
                        .attr("backcolor", "#E6E6E6").add();
            elc("conditionalStyle");            
        elc("style");
    }//@formatter:on

    private void declareParameters() {//@formatter:off
        el("parameter").attr("name", "TITLE").attr("class", "java.lang.String").add();
    }//@formatter:on

    private void declareFields() {
        for (String memberName : proto.getEntityMeta().getMemberNames()) {
            declareField(proto.getMember(memberName));
        }
    }

    private void declareField(IObject<?> member) {//@formatter:off
        el("field")
                .attr("name", member.getFieldName())
                .attr("class", member.getValueClass().getName())
                .add(); 
    }//@formatter:on

    // TODO for dynamic purposes maybe title should be "STATIC TEXT" element
    private void addTitle() {//@formatter:off
        int padding = 4;
        elo("title").add();
            elo("band").attr("height", "" + (fontSizeTitle + padding)).attr("splitType", "Stretch").add();
                    elo("textField").add();
                        el("reportElement")
                                .attr("style", Styles.TITLE.name())
                                .attr("x", "0")
                                .attr("y", "0")
                                .attr("width", "554")
                                .attr("height", "" + (fontSizeTitle + padding))
                                .attr("stretchType", "RelativeToBandHeight")
                                .add();
                        elo("textFieldExpression").add();
                            CDATA("$P{TITLE}");
                        elc("textFieldExpression");
                    elc("textField");                
            elc("band");
        elc("title");
    }//@formatter:off
    
    private void addPageHeader() {//@formatter:off
        elo("pageHeader").add();
            elo("band").attr("height", "" + (fontSize + padding + 2)).attr("splitType", "Stretch").add();
                elo("frame")
                        .add();
                    el("reportElement")
                            .attr("style", Styles.TABLE_HEADER.name())
                            .attr("x", "0")
                            .attr("y", "0")
                            .attr("width", "554")
                            .attr("height", "" + (fontSize + padding))
                            .add();
                    addTableColumnTitles();
                elc("frame");
            elc("band");
        elc();
    }//@formatter:on

    private void addColumnHeader() {//@formatter:off
        elo("columnHeader").add();
            el("band").attr("splitType", "Stretch").add();            
        elc();
    }//@formatter:on

    private void addDetail() {//@formatter:off
        elo("detail").add();
            elo("band").attr("height", "" + (fontSize + padding)).attr("splitType", "Stretch").add();
                addDetailMembers();
            elc("band");
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

    private void addTableColumnTitles() {
        visibleColumns = new ArrayList<ColumnDescriptorEntity>();

        int columnNum = 0;
        for (ColumnDescriptorEntity columnDescriptor : metadata.columnDescriptors()) {
            if (columnDescriptor.isVisible().isBooleanTrue()) {
                visibleColumns.add(columnDescriptor);
            }
        }

        int offset = 0;
        // TODO add variable column width option, maybe column width dependent on column title length? 
        int columnWidth = (pageWidth - 1) / visibleColumns.size();
        columnWidths = new int[visibleColumns.size()];

        for (ColumnDescriptorEntity columnDescriptor : visibleColumns) {
            addColumnTitle(columnDescriptor, offset, columnWidth);
            columnWidths[columnNum++] = columnWidth;
            offset += columnWidth;
        }
    }

    private void addColumnTitle(ColumnDescriptorEntity columnDescriptor, Integer offset, Integer width) {//@formatter:off
        String effectiveTitle = columnDescriptor.title().isNull() ? proto.getMember(columnDescriptor.propertyPath().getValue()).getMeta().getCaption() 
                                                                  : columnDescriptor.title().getValue();
        elo("textField").add();
            el("reportElement")
                    .attr("style", Styles.TABLE_COLUMN_TITLE.name())
                    .attr("x", offset.toString())
                    .attr("y", "0")
                    .attr("width", width.toString())
                    .attr("height", "" + (fontSize + 2))
                    .attr("stretchType", "RelativeToBandHeight")
                    .add();
            el("textElement").add();
            elo("textFieldExpression").add();
                CDATA("\"" + effectiveTitle +"\"");
            elc("textFieldExpression");
        elc("textField");
    }//@formatter:on

    private void addDetailMembers() {
        int offset = 0;
        int columnNum = 0;
        for (ColumnDescriptorEntity columnDescriptor : metadata.columnDescriptors()) {
            if (columnDescriptor.isVisible().isBooleanTrue()) {
                int width = columnWidths[columnNum++];
                addDetailMember(columnDescriptor, offset, width);
                offset += width;
            }
        }
    }

    private void addDetailMember(ColumnDescriptorEntity columnDescriptor, Integer offset, Integer width) {//@formatter:off
        IObject<?> member = proto.getMember(new Path(columnDescriptor.propertyPath().getValue())); 
                
        elo("textField").attr("isStretchWithOverflow", "true").add();
            el("reportElement")
                    .attr("style", Styles.TABLE_ROW.name())
                    .attr("x", offset.toString())
                    .attr("y", "0")
                    .attr("width", width.toString())
                    .attr("height", "" + (fontSize + padding))
                    .attr("stretchType", "RelativeToBandHeight")
                    .add();
            el("textElement").add();
            elo("textFieldExpression").add();
                CDATA(fieldValueExpression(member));
            elc("textFieldExpression");
        elc("textField");
        
    }//@formatter:on

    private static String fieldValueExpression(IObject<?> member) {
        String path = member.getPath().toString();
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
        if (!(member instanceof IPrimitive)) {
            columnValueExpressionBuilder.append(".getStringView()");
        } else if (isSubProperty) {
            columnValueExpressionBuilder.append(".getValue()");
        }
        return columnValueExpressionBuilder.toString();
    }
}
