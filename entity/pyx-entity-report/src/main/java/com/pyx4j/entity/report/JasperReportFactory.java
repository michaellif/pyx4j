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

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.IOUtils;

public class JasperReportFactory {

    private static final Logger log = LoggerFactory.getLogger(JasperReportFactory.class);

    public static final String REPORT_DESIGN_EXT = ".jrxml";

    public static final String REPORT_COMPILED_EXT = ".jasper";

    private static final Map<String, JasperReport> cashReports = new HashMap<String, JasperReport>();

    private static String getDesignResourceName(String reportName) {
        return reportName.replace(".", "/") + REPORT_DESIGN_EXT;
    }

    private static String getCompiledResourceName(String reportName) {
        return reportName.replace(".", "/") + REPORT_COMPILED_EXT;
    }

    @SuppressWarnings("deprecation")
    public static JasperReport create(JasperReportModel model) {

        if (model.getJrXml() != null) {
            log.debug("creating report {}", model.getDesignName());

            JasperReport jasperReport = findInCahsOrCompiled(model.getDesignName());
            if (jasperReport == null) {
                jasperReport = create(model.getDesignName(), new StringBufferInputStream(model.getJrXml()));
            }

            return jasperReport;
        }

        return create(model.getDesignName());
    }

    public static JasperReport create(String reportName) {
        log.debug("creating report {}", reportName);

        JasperReport jasperReport = findInCahsOrCompiled(reportName);
        if (jasperReport == null) {
            InputStream designStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(getDesignResourceName(reportName));
            if (designStream == null) {
                throw new RuntimeException("Report Design " + reportName + " not found in resources");
            }

            jasperReport = create(reportName, designStream);
        }

        return jasperReport;
    }

    // internals:
    private static JasperReport findInCahsOrCompiled(String reportName) {
        JasperReport jasperReport = null;

        if (!ServerSideConfiguration.instance().isDevelopmentBehavior()) {
            jasperReport = cashReports.get(reportName);
            if (jasperReport == null) {
                InputStream compiledStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(getCompiledResourceName(reportName));
                try {
                    jasperReport = (JasperReport) JRLoader.loadObject(compiledStream);
                } catch (JRException e) {
                    throw new RuntimeException("Report loading " + reportName + " error", e);
                } finally {
                    IOUtils.closeQuietly(compiledStream);
                }
                // Save in cash:
                cashReports.put(reportName, jasperReport);
            }
        }

        return jasperReport;
    }

    private static JasperReport create(String reportName, InputStream designStream) {
        JasperReport jasperReport = null;

        log.debug("compiling report {}", reportName);

        try {
            jasperReport = JasperCompileManager.compileReport(designStream);
        } catch (JRException e) {
            throw new RuntimeException("Compile Report " + reportName + " error", e);
        } finally {
            IOUtils.closeQuietly(designStream);
        }
        // Save in cash:
        if (!ServerSideConfiguration.instance().isDevelopmentBehavior()) {
            cashReports.put(reportName, jasperReport);
        }

        return jasperReport;
    }
}
