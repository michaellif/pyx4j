/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-20
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.lease;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.biz.tenant.lease.print.LeaseTermAgreementDocumentDataCreatorFacade;
import com.propertyvista.biz.tenant.lease.print.LeaseTermAgreementDocumentDataCreatorFacade.LeaseTermAgreementSignaturesMode;
import com.propertyvista.biz.tenant.lease.print.LeaseTermAgreementPdfCreatorFacade;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseAgreementDocumentDataDTO;

public class LeaseTermBlankAgreementDocumentCreationProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(LeaseTermBlankAgreementDocumentCreationProcess.class);

    private static final long serialVersionUID = 1L;

    private final AtomicInteger progress;

    private final int progressMax;

    private String fileName;

    private volatile Throwable error;

    private final Lease leaseId;

    private final boolean createDraft;

    public LeaseTermBlankAgreementDocumentCreationProcess(Lease leaseId, boolean createDraft) {
        this.progress = new AtomicInteger();
        this.progress.set(0);
        this.progressMax = 1;
        this.leaseId = leaseId;
        this.createDraft = createDraft;
    }

    @Override
    public void execute() {
        try {
            Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

            LeaseAgreementDocumentDataDTO documentData = ServerSideFactory.create(LeaseTermAgreementDocumentDataCreatorFacade.class).createAgreementData(
                    lease.currentTerm(), LeaseTermAgreementSignaturesMode.PlaceholdersAndAvailableSignatures, createDraft);
            byte[] pdfBytes = ServerSideFactory.create(LeaseTermAgreementPdfCreatorFacade.class).createPdf(documentData);

            Downloadable d = new Downloadable(pdfBytes, MimeMap.getContentType(DownloadFormat.PDF));

            fileName = "blank-lease-agreement.pdf";
            d.save(fileName);
        } catch (Throwable e) {
            log.error("blank agreement document generation failed", e);
            error = e;
        } finally {
            completed = true;
        }

    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
        r.setProgress(progress.get());
        r.setProgressMaximum(progressMax);
        if (completed) {
            r.setCompleted();
            r.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
        }
        if (error != null) {
            r.setError();
            r.setErrorStatusMessage("failed to agreement document printout due to " + error.getMessage());
        }
        return r;
    }

}
