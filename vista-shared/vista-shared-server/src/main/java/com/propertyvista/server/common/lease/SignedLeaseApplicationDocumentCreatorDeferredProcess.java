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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.tenant.lease.print.LeaseApplicationDocumentDataCreatorFacade;
import com.propertyvista.biz.tenant.lease.print.LeaseApplicationDocumentDataCreatorFacade.SignaturesMode;
import com.propertyvista.biz.tenant.lease.print.LeaseApplicationDocumentPdfCreatorFacade;
import com.propertyvista.domain.blob.LeaseApplicationDocumentBlob;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.prospect.LeaseApplicationDocument;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.dto.LeaseApplicationDocumentDataDTO;

// TODO There will be probably a need for another process for creating blank (i.e. for ink signing) lease application documents
/**
 * Creates Signed Lease Application
 */
public class SignedLeaseApplicationDocumentCreatorDeferredProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(SignedLeaseApplicationDocumentCreatorDeferredProcess.class);

    private static final long serialVersionUID = 1L;

    private final Lease leaseId;

    private final Customer customerId;

    private OnlineApplication onlineApplication;

    public SignedLeaseApplicationDocumentCreatorDeferredProcess(Lease leaseId, Customer customerId) {
        this.leaseId = leaseId;
        this.customerId = customerId;
    }

    @Override
    public void execute() {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {
                try {
                    retrieveOnlineApplication();
                    LeaseApplicationDocumentDataDTO data = ServerSideFactory.create(LeaseApplicationDocumentDataCreatorFacade.class).createApplicationData(
                            onlineApplication, SignaturesMode.SignaturesOnly);
                    byte[] pdfBytes = ServerSideFactory.create(LeaseApplicationDocumentPdfCreatorFacade.class).createPdf(data);
                    LeaseApplicationDocument documentId = saveDocument(pdfBytes);
                    ServerSideFactory.create(CommunicationFacade.class).sendApplicationDocumentCopy(documentId);
                } catch (Throwable e) {
                    log.error("failed to create online application for customer=" + customerId.getPrimaryKey() + ", lease=" + leaseId.getPrimaryKey(), e);
                } finally {
                    completed = true;
                }
                return null;
            }

        });
    }

    private void retrieveOnlineApplication() {
        EntityQueryCriteria<OnlineApplication> criteria = EntityQueryCriteria.create(OnlineApplication.class);
        criteria.eq(criteria.proto().customer(), this.customerId);
        criteria.eq(criteria.proto().masterOnlineApplication().leaseApplication().lease(), this.leaseId);
        onlineApplication = Persistence.service().retrieve(criteria);
        if (onlineApplication == null) {
            throw new RuntimeException("Online Application for customer=" + customerId.getPrimaryKey() + ", lease=" + leaseId.getPrimaryKey() + " not found");
        }
    }

    private LeaseApplicationDocument saveDocument(byte[] pdfBytes) {
        LeaseApplicationDocumentBlob blob = EntityFactory.create(LeaseApplicationDocumentBlob.class);
        blob.contentType().setValue(MimeMap.getContentType(DownloadFormat.PDF));
        blob.data().setValue(pdfBytes);
        Persistence.service().persist(blob);

        LeaseApplicationDocument document = EntityFactory.create(LeaseApplicationDocument.class);
        document.file().fileName().setValue("lease-application.pdf");
        document.file().fileSize().setValue(pdfBytes.length);
        document.file().blobKey().setValue(blob.getPrimaryKey());
        document.lease().set(leaseId);
        document.signedBy().set(customerId);
        document.signedByRole().setValue(onlineApplication.role().getValue());
        document.isSignedByInk().setValue(false);

        Persistence.service().persist(document);

        FileUploadRegistry.register(document.file());

        return document.createIdentityStub();
    }

}
