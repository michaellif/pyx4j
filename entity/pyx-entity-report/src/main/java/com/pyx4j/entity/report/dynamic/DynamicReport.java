/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Aug 7, 2014
 * @author arminea
 */
package com.pyx4j.entity.report.dynamic;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicReport {

    private static final Logger log = LoggerFactory.getLogger(DynamicReport.class);

    private final JasperReportBuilder dynamicReport;

    private final String name;

    public DynamicReport(String logo, String title) {

        dynamicReport = report().title(ReportTemplate.reportTitleBuilder(logo, title))
                .pageFooter(cmp.horizontalList(cmp.hListCell(cmp.text("Page " + cmp.pageNumber().toString() + " of " + cmp.totalPages()))));
        this.name = title;
        dynamicReport.build();
    }

    public JasperReportBuilder getReport() {
        return dynamicReport;
    }

    public void setData(List<String> columns, List<Object> data) {

        DRDataSource dataSource = new DRDataSource(columns.toArray(new String[columns.size()]));
        for (Object entry : data) {
            dataSource.add(entry);
        }
        dynamicReport.setDataSource(dataSource);
    }

    public void export(ExportTo format, String exportPath) {
        try {
            switch (format) {
            case PDF:
                dynamicReport.toPdf(export.pdfExporter(exportPath + "\\" + name + ".pdf"));
                break;
            case RTF:
                dynamicReport.toRtf(export.rtfExporter(exportPath + "\\" + dynamicReport.getReport().getReportName() + ".rtf"));
                break;
            case XLSX:
                dynamicReport.toXlsx(export.xlsxExporter(exportPath + "\\" + dynamicReport.getReport().getReportName() + ".xls"));
                break;
            case DOCX:
                dynamicReport.toDocx(export.docxExporter(exportPath + "\\" + dynamicReport.getReport().getReportName() + ".docx"));
                break;
            case ODT:
                dynamicReport.toOdt(export.odtExporter(exportPath + "\\" + dynamicReport.getReport().getReportName() + ".odt"));
                break;
            case XML:
                dynamicReport.toXml(export.xmlExporter(exportPath + "\\" + dynamicReport.getReport().getReportName() + ".xml"));
                break;
            case HTML:
                dynamicReport.toHtml(export.htmlExporter(exportPath + "\\" + dynamicReport.getReport().getReportName() + ".html"));
                break;
            case TXT:
                dynamicReport.toText(export.textExporter(exportPath + "\\" + dynamicReport.getReport().getReportName() + ".txt"));
                break;
            case CSV:
                dynamicReport.toCsv(export.csvExporter(exportPath + "\\" + dynamicReport.getReport().getReportName() + ".csv"));
                break;
            case ODS:
                dynamicReport.toOds(export.odsExporter(exportPath + "\\" + dynamicReport.getReport().getReportName() + ".ods"));
                break;
            case PPTX:
                dynamicReport.toPptx(export.pptxExporter(exportPath + "\\" + dynamicReport.getReport().getReportName() + ".pptx"));
                break;
            default:
                dynamicReport.toJrXml(new FileOutputStream(exportPath + "\\" + dynamicReport.getReport().getReportName() + ".jrxml"));
            }
        } catch (IOException ioe) {
            log.error("IOException occurred during report export : Exception {}", ioe.getStackTrace().toString());
        } catch (DRException dre) {
            log.error("DRException occurred during report export : Exception {}", dre.getStackTrace().toString());
        }
    }
}
