/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 1, 2014
 * @author arminea
 */
package com.pyx4j.entity.report.dynamic;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.hyperLink;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.tableOfContentsCustomizer;
import static net.sf.dynamicreports.report.builder.DynamicReports.template;

import java.awt.Color;
import java.util.Locale;

import javax.xml.transform.Templates;

import net.sf.dynamicreports.report.builder.HyperLinkBuilder;
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.datatype.BigDecimalType;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.tableofcontents.TableOfContentsCustomizerBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;

public class ReportTemplate {
        public static final StyleBuilder rootStyle;
        public static final StyleBuilder boldStyle;
        public static final StyleBuilder italicStyle;
        public static final StyleBuilder boldCenteredStyle;
        public static final StyleBuilder bold12CenteredStyle;
        public static final StyleBuilder bold16CenteredStyle;
        public static final StyleBuilder bold18CenteredStyle;
        public static final StyleBuilder bold22CenteredStyle;
        public static final StyleBuilder columnStyle;
        public static final StyleBuilder columnTitleStyle;
        public static final StyleBuilder groupStyle;
        public static final StyleBuilder subtotalStyle;
        public static final StyleBuilder italic12LeftStyle;
        public static final StyleBuilder mainTitleStyle;

        public static final ReportTemplateBuilder reportTemplate;
        public static final CurrencyType currencyType;
        public static final ComponentBuilder<?, ?> propertyCustomTitleComponent;
        public static final ComponentBuilder<?, ?> footerComponent;

        static{
            rootStyle = stl.style().setPadding(2);
            mainTitleStyle = stl.style(rootStyle).setFontSize(9);
            boldStyle = stl.style(rootStyle).bold();
            italicStyle  = stl.style(rootStyle).italic();
            boldCenteredStyle = stl.style(boldStyle).setAlignment(HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
            bold12CenteredStyle = stl.style(boldCenteredStyle).setFontSize(12);
            bold18CenteredStyle = stl.style(boldCenteredStyle).setFontSize(18);
            bold16CenteredStyle = stl.style(boldCenteredStyle).setFontSize(16);
            bold22CenteredStyle = stl.style(boldCenteredStyle).setFontSize(22);
            italic12LeftStyle = stl.style(italicStyle).setFontSize(8);
            columnStyle = stl.style(rootStyle).setVerticalAlignment(VerticalAlignment.MIDDLE);
            columnTitleStyle = stl.style(columnStyle)
                                    .setBorder(stl.pen1Point())
                                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                                    .setBackgroundColor(Color.LIGHT_GRAY)
                                    .bold();
            groupStyle = stl.style(boldStyle)
                                     .setHorizontalAlignment(HorizontalAlignment.LEFT);
            subtotalStyle = stl.style(boldStyle)
                                     .setTopBorder(stl.pen1Point());

            StyleBuilder crosstabGroupStyle = stl.style(columnTitleStyle);
            StyleBuilder crosstabGroupTotalStyle = stl.style(columnTitleStyle)
                                                      .setBackgroundColor(new Color(170, 170, 170));
            StyleBuilder crosstabGrandTotalStyle = stl.style(columnTitleStyle)
                                                      .setBackgroundColor(new Color(140, 140, 140));
            StyleBuilder crosstabCellStyle = stl.style(columnStyle)
                                                      .setBorder(stl.pen1Point());

            TableOfContentsCustomizerBuilder tableOfContentsCustomizer = tableOfContentsCustomizer()
                                                                            .setHeadingStyle(0, stl.style(rootStyle).bold());
            reportTemplate = template()
                                .setLocale(Locale.ENGLISH)
                                .setColumnStyle(columnStyle)
                                .setColumnTitleStyle(columnTitleStyle)
                                .setGroupStyle(groupStyle)
                                .setGroupTitleStyle(groupStyle)
                                .setSubtotalStyle(subtotalStyle)
                                .highlightDetailEvenRows()
                                .crosstabHighlightEvenRows()
                                .setCrosstabGroupStyle(crosstabGroupStyle)
                                .setCrosstabGroupTotalStyle(crosstabGroupTotalStyle)
                                .setCrosstabGrandTotalStyle(crosstabGrandTotalStyle)
                                .setCrosstabCellStyle(crosstabCellStyle)
                                .setTableOfContentsCustomizer(tableOfContentsCustomizer);

            currencyType = new CurrencyType();

            HyperLinkBuilder link = hyperLink("http://propertyvista.com/");
            propertyCustomTitleComponent = null;// reportTitleBuilder("http://propertyvista.com/", "images/logo.png", "Invalid Direct Debit Report");

            footerComponent = cmp.pageXofY()
                                 .setStyle(
                                         stl.style(boldCenteredStyle)
                                         .setTopBorder(stl.pen1Point()));
        }

        public static ComponentBuilder<?, ?> reportTitleBuilder(String link , String imageResourcePath, String reportTitle){
            return cmp.horizontalList(
                    cmp.image(Templates.class.getResource(imageResourcePath)).setFixedDimension(60, 60),
                    cmp.verticalList(
                            cmp.text(reportTitle).setStyle(bold16CenteredStyle).setHorizontalAlignment(HorizontalAlignment.JUSTIFIED),
                            cmp.text("generated by " + link).setStyle(italicStyle).setHyperLink(hyperLink(link))));
        }

        public static ComponentBuilder<?, ?> reportTitleBuilder(String imageResourcePath, String reportTitle){
            return reportTitleBuilder("http://propertyvista.com/", imageResourcePath, reportTitle);
        }

        public static class CurrencyType extends BigDecimalType {
            private static final long serialVersionUID = 1L;
            @Override
            public String getPattern() {
               return "$ #,###.00";
            }
        }

 }
