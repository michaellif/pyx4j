/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-16
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.admin.rpc.PmcExportTenantsParamsDTO;

public class ExportTenantsDeferredProcess extends AbstractDeferredProcess {

    /**
     *
     */
    private static final long serialVersionUID = 5026863365365592547L;

    private volatile int progress;

    private volatile int maximum;

    private final String fileName;

    public ExportTenantsDeferredProcess(PmcExportTenantsParamsDTO pmcExportTenantsParamsDTO) {
        completed = false;
        fileName = "pmc-tenants.csv";
    }

    @Override
    public void execute() {
        completed = false;
        Downloadable d = new Downloadable("tenant,download,plumbing".getBytes(), Downloadable.getContentType(DownloadFormat.XML));
        d.save(fileName);
        completed = true;

    }

    @Override
    public DeferredProcessProgressResponse status() {
        if (completed) {
            DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
            r.setCompleted();
            r.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
            return r;
        } else {
            DeferredProcessProgressResponse r = super.status();
            r.setProgress(progress);
            r.setProgressMaximum(maximum);
            return r;
        }

    }

}
