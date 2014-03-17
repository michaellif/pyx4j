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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeaseBlankApplicationDocumentCreationProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(LeaseBlankApplicationDocumentCreationProcess.class);

    private static final long serialVersionUID = 1L;

    private final AtomicInteger progress;

    private final int progressMax;

    private volatile String fileName;

    private volatile Throwable error;

    private final Lease leaseId;

    private final Customer customerId;

    public LeaseBlankApplicationDocumentCreationProcess(Lease leaseId, Customer customerId) {
        this.progress = new AtomicInteger();
        this.progress.set(0);
        this.progressMax = 1;
        this.leaseId = leaseId;
        this.customerId = customerId;
    }

    @Override
    public void execute() {
//        try {
//            Lease lease = ServerSideFactory.create(LeaseFacade.class).load(leaseId, false);
//            LeaseAgreementDocumentDataDTO data = ServerSideFactory.create(LeaseTermAgreementDocumentDataCreatorFacade.class).createAgreementData(//@formatter:off
//                    lease.currentTerm(),
//                    LeaseTermAgreementSignaturesMode.PlaceholdersAndAvailableSignatures,
//            );//@formatter:on
//            byte[] pdfBytes = ServerSideFactory.create(LeaseTermAgreementPdfCreatorFacade.class).createPdf(data);
//            Downloadable d = new Downloadable(pdfBytes, MimeMap.getContentType(DownloadFormat.PDF));
//
//            fileName = "blank-lease-agreement.pdf";
//            d.save(fileName);
//        } catch (Throwable e) {
//            log.error("blank agreement document generation failed", e);
//            error = e;
//        } finally {
//            completed = true;
//        }
        // TODO implement this:
        Customer customer = Persistence.service().retrieve(Customer.class, customerId.getPrimaryKey());
        Downloadable downloadable = new Downloadable(("Lease Application for Customer: " + customer.getStringView()).getBytes(),
                MimeMap.getContentType(DownloadFormat.TXT));
        downloadable.save(fileName = "blank-lease-application.txt");
        completed = true;
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
