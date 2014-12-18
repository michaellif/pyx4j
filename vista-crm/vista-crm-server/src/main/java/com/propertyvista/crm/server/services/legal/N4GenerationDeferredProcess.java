/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2013
 * @author vlads
 */
package com.propertyvista.crm.server.services.legal;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Pair;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.legal.N4ManagementFacade;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.domain.legal.errors.FormFillError;
import com.propertyvista.domain.tenant.lease.Lease;

public class N4GenerationDeferredProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(N4GenerationDeferredProcess.class);

    private static final I18n i18n = I18n.get(N4GenerationDeferredProcess.class);

    private static final long serialVersionUID = 1L;

    private final AtomicInteger progress;

    private final int progressMax;

    private final N4BatchRequestDTO batchRequest;

    private Exception error;

    private String fileName;

    public N4GenerationDeferredProcess(N4BatchRequestDTO batchRequest) {
        this.progress = new AtomicInteger();
        this.progress.set(0);
        this.progressMax = batchRequest.targetDelinquentLeases().size();
        this.batchRequest = batchRequest;
    }

    @Override
    public void execute() {
        try {
            List<Pair<Lease, Exception>> erredLeases = ServerSideFactory.create(N4ManagementFacade.class).issueN4(batchRequest, progress);

            if (!erredLeases.isEmpty()) {
                Pair<byte[], DownloadFormat> report = makeErredReport(erredLeases);
                Downloadable erredLeasesDownloadableReport = new Downloadable(report.getA(), MimeMap.getContentType(report.getB()));
                erredLeasesDownloadableReport.save(fileName = "failed-n4s-report-" + System.currentTimeMillis() + "." + report.getB().getExtension());
            }
        } catch (Exception e) {
            error = e;
            log.error("N4 generation failed", e);
        } finally {
            completed = true;
        }
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredReportProcessProgressResponse status = new DeferredReportProcessProgressResponse();
        if (canceled) {
            status.setCanceled();
        } else if (completed) {
            if (error != null) {
                status.setErrorStatusMessage(i18n.tr("N4 Generation Failed"));
            } else {
                status.setProgress(progress.get());
                status.setProgress(progressMax);
                if (fileName != null) {
                    status.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
                }
            }
            status.setCompleted();
        } else {
            status.setProgress(progress.get());
            status.setProgressMaximum(progressMax);
        }
        return status;
    }

    private Pair<byte[], DownloadFormat> makeErredReport(List<Pair<Lease, Exception>> erredLeases) {
        StringBuilder report = new StringBuilder();
        for (Pair<Lease, Exception> erredLease : erredLeases) {
            Lease lease = Persistence.service().retrieve(Lease.class, erredLease.getA().getPrimaryKey());

            //@formatter:off
            report.append("Lease ").append(lease.leaseId().getStringView())
                  .append(", Unit ").append(lease.unit().getStringView())
                  .append(":\r\n");
            //@formatter:on
            for (String errorMessageLine : errorMessage(erredLease.getB()).split("\n")) {
                report.append("\t").append(errorMessageLine).append("\r\n");
            }
            report.append("\r\n");
        }
        return new Pair<byte[], DownloadFormat>(report.toString().getBytes(), DownloadFormat.TXT);
    }

    private String errorMessage(Exception e) {
        if (e instanceof UserRuntimeException || e instanceof FormFillError) {
            return e.getMessage();
        } else {
            return i18n.tr("Unidentified Error: please contact support for more details");
        }

    }

}
