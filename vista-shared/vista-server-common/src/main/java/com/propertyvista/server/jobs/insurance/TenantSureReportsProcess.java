/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs.insurance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.report.ReportTableCSVFormater;
import com.pyx4j.essentials.server.report.ReportTableFormater;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.tenant.insurance.TenantSureProcessFacade;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.server.jobs.PmcProcess;
import com.propertyvista.server.jobs.PmcProcessContext;

public class TenantSureReportsProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(TenantSureReportsProcess.class);

    protected ReportTableFormater formater;

    public TenantSureReportsProcess() {
    }

    @Override
    public boolean start(PmcProcessContext context) {
        formater = new ReportTableCSVFormater();
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        ServerSideFactory.create(TenantSureProcessFacade.class).processReports(context.getRunStats(), new LogicalDate(context.getForDate()), formater);
    }

    @Override
    public void complete(PmcProcessContext context) {
        // create the file actually
        File sftpDir = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getTenantSureInterfaceSftpDirectory();
        File dirReports = new File(sftpDir, "reports");
        if (!dirReports.exists()) {
            if (!dirReports.mkdirs()) {
                log.error("Unable to create directory {}", dirReports.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", dirReports.getAbsolutePath()));
            }
        }

        String reportName = "subscribers-" + new SimpleDateFormat("yyyyMMdd").format(context.getForDate()) + ".csv";
        OutputStream out = null;
        try {
            out = new FileOutputStream(new File(sftpDir, reportName));
            out.write(formater.getBinaryData());

            // commit the changes of InsuranceTenantSureReport table that might have been updated during the report processing
            Persistence.service().commit();
        } catch (Throwable e) {
            System.err.println(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

}
