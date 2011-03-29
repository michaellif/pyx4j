/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-03-29
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.report;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRProperties;

public class JasperReportProcessor {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void createReport(final JasperReportModel model, JasperFileFormat format, OutputStream out) {

        JasperReport jasperReport = JasperReportFactory.create(model.getDesignName());

        Map parameters = model.getParameters();
        if (parameters == null) {
            parameters = new HashMap();
        }
        JRProperties.setProperty("net.sf.jasperreports.awt.ignore.missing.font", Boolean.TRUE);

        JasperPrint jasperPrint;
        try {
            JRDataSource dataSource;
            if (model.getData() != null) {
                dataSource = new JRIEntityCollectionDataSource(model.getData());
            } else {
                dataSource = new JREmptyDataSource();
            }

            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        } catch (JRException e) {
            throw new RuntimeException("Report fill error", e);
        }

        try {
            switch (format) {
            case RTF:
                JRRtfExporter exporterRtf = new JRRtfExporter() {
                    @Override
                    protected void exportImage(JRPrintImage printImage) throws JRException, IOException {
                        //if (!request.isExcludeImages()) {
                        super.exportImage(printImage);
                        //}
                    }
                };
                exporterRtf.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporterRtf.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
                exporterRtf.exportReport();
                break;
            case DOCX:
                JRDocxExporter exporterDocx = new JRDocxExporter();
                exporterDocx.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporterDocx.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
                exporterDocx.exportReport();
                break;
            case XLS:
                JRXlsAbstractExporter exporterXls = new JRXlsExporter();
                exporterXls.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporterXls.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
                exporterXls.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                exporterXls.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
                exporterXls.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
                exporterXls.setParameter(JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                exporterXls.exportReport();
                break;
            case ODT:
                JROdtExporter exporterOdt = new JROdtExporter();
                exporterOdt.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporterOdt.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
                exporterOdt.exportReport();
            case PDF:
                JasperExportManager.exportReportToPdfStream(jasperPrint, out);
                break;
            case XML:
                JasperExportManager.exportReportToXmlStream(jasperPrint, out);
                break;
            case HTML:
                JRHtmlExporter exporterHtml = new JRHtmlExporter() {
                    @Override
                    protected void exportImage(JRPrintImage image, JRExporterGridCell gridCell) throws JRException, IOException {
                        //Do not add images
                    }
                };
                exporterHtml.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporterHtml.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
                exporterHtml.setParameter(JRHtmlExporterParameter.CHARACTER_ENCODING, "UTF-8");
                exporterHtml.setParameter(JRHtmlExporterParameter.SIZE_UNIT, JRHtmlExporterParameter.SIZE_UNIT_POINT);
                exporterHtml.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                exporterHtml.exportReport();
                break;
            default:
                throw new RuntimeException("Unsupported report download format " + format);
            }
        } catch (JRException e) {
            throw new RuntimeException("Report export error", e);
        }
    }
}
