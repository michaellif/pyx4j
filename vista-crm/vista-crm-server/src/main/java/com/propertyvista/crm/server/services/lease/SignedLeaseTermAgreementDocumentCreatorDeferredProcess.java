/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 2, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.biz.tenant.lease.print.LeaseTermAgreementDocumentDataCreatorFacade;
import com.propertyvista.biz.tenant.lease.print.LeaseTermAgreementDocumentDataCreatorFacade.LeaseTermAgreementSignaturesMode;
import com.propertyvista.biz.tenant.lease.print.LeaseTermAgreementPdfCreatorFacade;
import com.propertyvista.biz.tenant.lease.print.LeaseTermAgreementSigningProgressFacade;
import com.propertyvista.domain.blob.LeaseTermAgreementDocumentBlob;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;
import com.propertyvista.dto.LeaseAgreementSigningProgressDTO;
import com.propertyvista.dto.LeaseAgreementStakeholderSigningProgressDTO;
import com.propertyvista.dto.LeaseAgreementStakeholderSigningProgressDTO.SignatureType;

@SuppressWarnings("serial")
class SignedLeaseTermAgreementDocumentCreatorDeferredProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(SignedLeaseTermAgreementDocumentCreatorDeferredProcess.class);

    private final LeaseTerm leaseTerm;

    private final LeaseTermAgreementDocument agreementDocument;

    public SignedLeaseTermAgreementDocumentCreatorDeferredProcess(LeaseTerm leaseTerm) {
        super();
        this.leaseTerm = leaseTerm;
        this.agreementDocument = EntityFactory.create(LeaseTermAgreementDocument.class);
        this.agreementDocument.leaseTermV().set(this.leaseTerm.version());
    }

    @Override
    public void execute() {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                try {
                    byte[] agreementPdf = ServerSideFactory.create(LeaseTermAgreementPdfCreatorFacade.class).createPdf(
                            ServerSideFactory.create(LeaseTermAgreementDocumentDataCreatorFacade.class).createAgreementData(leaseTerm,
                                    LeaseTermAgreementSignaturesMode.SignaturesOnly, false));
                    saveDocumentBlob(agreementPdf);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        });

        log.info("Created and saved signed lease agreement document for lease term pk={}", this.leaseTerm.getPrimaryKey());
        completed = true;

    }

    private void saveDocumentBlob(byte[] bytes) {
        Persistence.ensureRetrieve(leaseTerm.lease(), AttachLevel.IdOnly);

        LeaseTermAgreementDocumentBlob blob = EntityFactory.create(LeaseTermAgreementDocumentBlob.class);
        blob.data().setValue(bytes);
        blob.contentType().setValue(MimeMap.getContentType(DownloadFormat.PDF));
        Persistence.service().persist(blob);
        agreementDocument.file().fileName().setValue("agreement.pdf");
        agreementDocument.file().fileSize().setValue(bytes.length);
        agreementDocument.file().blobKey().set(blob.id());

        agreementDocument.leaseTermV().set(leaseTerm.version());

        agreementDocument.isSignedByInk().setValue(false);

        LeaseAgreementSigningProgressDTO signingProgress = ServerSideFactory.create(LeaseTermAgreementSigningProgressFacade.class).getSigningProgress(
                this.leaseTerm.lease().<Lease> createIdentityStub());
        for (LeaseAgreementStakeholderSigningProgressDTO stackholderProgress : signingProgress.stackholdersProgressBreakdown()) {
            if (stackholderProgress.singatureType().getValue() != SignatureType.Digital) {
                throw new RuntimeException(
                        "Aborting lease term agreement document creation: all lease participants required to sign digitally in order to create digitally signed document leaseId="
                                + leaseTerm.lease().getPrimaryKey());
            }
            if (!stackholderProgress.stakeholderLeaseParticipant().isEmpty()) {
                agreementDocument.signedParticipants().add(stackholderProgress.stakeholderLeaseParticipant());
            } else if (!stackholderProgress.stakeholderUser().isEmpty()) {
                agreementDocument.signedEmployeeUploader().set(stackholderProgress.stakeholderUser());
            }
        }
        FileUploadRegistry.register(agreementDocument.file());
        Persistence.service().persist(agreementDocument);

    }

}
