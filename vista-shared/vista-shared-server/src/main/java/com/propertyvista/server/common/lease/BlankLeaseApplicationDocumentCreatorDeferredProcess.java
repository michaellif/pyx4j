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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.biz.tenant.lease.print.LeaseApplicationDocumentDataCreatorFacade;
import com.propertyvista.biz.tenant.lease.print.LeaseApplicationDocumentPdfCreatorFacade;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.dto.LeaseApplicationDocumentDataDTO;

/**
 * Creates Blank Form for Lease Application
 */
public class BlankLeaseApplicationDocumentCreatorDeferredProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(BlankLeaseApplicationDocumentCreatorDeferredProcess.class);

    private static final long serialVersionUID = 1L;

    private final Lease leaseId;

    private final Customer customerId;

    private volatile Throwable error;

    private LeaseApplication application;

    private String fileName;

    public BlankLeaseApplicationDocumentCreatorDeferredProcess(Lease leaseId, Customer customerId) {
        this.progress.progress.set(0);
        this.progress.progressMaximum.set(1);

        this.leaseId = leaseId;
        this.customerId = customerId;
    }

    @Override
    public void execute() {
        try {
            retrieveApplication();
            LeaseApplicationDocumentDataDTO data = ServerSideFactory.create(LeaseApplicationDocumentDataCreatorFacade.class).createApplicationDataForBlankForm(
                    application);
            byte[] pdfBytes = ServerSideFactory.create(LeaseApplicationDocumentPdfCreatorFacade.class).createPdf(data);
            Downloadable applicationDocument = new Downloadable(pdfBytes, MimeMap.getContentType(DownloadFormat.PDF));
            applicationDocument.save(fileName = "application.pdf");
        } catch (Throwable e) {
            error = e;
            log.error("failed to create online application for customer=" + customerId.getPrimaryKey() + ", lease=" + leaseId.getPrimaryKey(), e);
        } finally {
            completed = true;
        }

    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
        r.setProgress(progress.progress.get());
        r.setProgressMaximum(progress.progressMaximum.get());
        if (completed) {
            r.setCompleted();
            r.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
        }
        if (error != null) {
            r.setError();
            if (error instanceof UserRuntimeException) {
                r.setErrorStatusMessage("failed to agreement document printout due to " + error.getMessage());
            } else {
                r.setErrorStatusMessage("failed to agreement document printout due to error");
            }
        }
        return r;
    }

    private void retrieveApplication() {
        EntityQueryCriteria<LeaseApplication> criteria = EntityQueryCriteria.create(LeaseApplication.class);
        criteria.eq(criteria.proto().lease(), this.leaseId);
        application = Persistence.service().retrieve(criteria);
        if (application == null) {
            throw new RuntimeException("Application for customer=" + customerId.getPrimaryKey() + ", lease=" + leaseId.getPrimaryKey() + " not found");
        }
    }

}
