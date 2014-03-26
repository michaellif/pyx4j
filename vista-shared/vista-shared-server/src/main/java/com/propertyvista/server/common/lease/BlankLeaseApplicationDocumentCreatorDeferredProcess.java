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
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.biz.tenant.lease.print.LeaseApplicationDocumentDataCreatorFacade;
import com.propertyvista.biz.tenant.lease.print.LeaseApplicationDocumentDataCreatorFacade.DocumentMode;
import com.propertyvista.biz.tenant.lease.print.LeaseApplicationDocumentPdfCreatorFacade;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataDTO;

/**
 * Creates a lease application form for signing by ink
 */
public class BlankLeaseApplicationDocumentCreatorDeferredProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(BlankLeaseApplicationDocumentCreatorDeferredProcess.class);

    private static final long serialVersionUID = 1L;

    private final Lease leaseId;

    private final LeaseTermParticipant<?> participantId;

    private volatile Throwable error;

    private String fileName;

    private LeaseApplication application;

    private LeaseTermParticipant participant;

    public BlankLeaseApplicationDocumentCreatorDeferredProcess(Lease leaseId, LeaseTermParticipant<?> participantId) {
        this.progress.progress.set(0);
        this.progress.progressMaximum.set(1);

        this.leaseId = leaseId;
        this.participantId = participantId;
    }

    @Override
    public void execute() {
        try {
            retrieveApplication();
            retrieveLeaseParticipant();
            LeaseApplicationDocumentDataDTO data = ServerSideFactory.create(LeaseApplicationDocumentDataCreatorFacade.class).createApplicationData(
                    DocumentMode.InkSinging, application, participant);
            byte[] pdfBytes = ServerSideFactory.create(LeaseApplicationDocumentPdfCreatorFacade.class).createPdf(data);
            Downloadable applicationDocument = new Downloadable(pdfBytes, MimeMap.getContentType(DownloadFormat.PDF));
            applicationDocument.save(fileName = "application.pdf");
        } catch (Throwable e) {
            error = e;
            log.error("failed to create online application for participant=" + participantId.getPrimaryKey() + ", lease=" + leaseId.getPrimaryKey(), e);
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
            throw new RuntimeException("Application for participant=" + participantId.getPrimaryKey() + ", lease=" + leaseId.getPrimaryKey() + " not found");
        }
    }

    private void retrieveLeaseParticipant() {
        participant = Persistence.service().retrieve(LeaseTermParticipant.class, participantId.getPrimaryKey());
        Persistence.ensureRetrieve(participant.leaseParticipant(), AttachLevel.Attached);
        if (participant == null) {
            throw new RuntimeException("participant=" + participantId.getPrimaryKey() + " for lease application of lease=" + leaseId.getPrimaryKey()
                    + " was not found");
        }
    }

}
