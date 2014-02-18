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
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.biz.tenant.lease.print.LeaseTermAgreementDocumentDataCreatorFacade;
import com.propertyvista.biz.tenant.lease.print.LeaseTermAgreementDocumentDataCreatorFacade.LeaseTermAgreementSignaturesMode;
import com.propertyvista.biz.tenant.lease.print.LeaseTermAgreementPdfCreatorFacade;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.blob.LeaseTermAgreementDocumentBlob;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CrmUserSignature;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;

@SuppressWarnings("serial")
class LeaseSignedTermAgreementPrinterDeferredProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(LeaseSignedTermAgreementPrinterDeferredProcess.class);

    private final LeaseTerm leaseTerm;

    private final LeaseTermAgreementDocument agreementDocument;

    private final CrmUser signingUser;

    public LeaseSignedTermAgreementPrinterDeferredProcess(LeaseTerm leaseTerm, CrmUser signingUser) {
        super();
        this.leaseTerm = leaseTerm;
        this.agreementDocument = EntityFactory.create(LeaseTermAgreementDocument.class);
        this.agreementDocument.leaseTermV().set(this.leaseTerm.version());
        this.signingUser = signingUser;
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

        log.info("Created and saved signed lease agreement document for lease term pk={}, by crm user pk={}", this.leaseTerm.getPrimaryKey(),
                this.signingUser.getPrimaryKey());
        completed = true;

    }

    private void saveDocumentBlob(byte[] bytes) {
        CrmUserSignature signature = EntityFactory.create(CrmUserSignature.class);
        signature.signatureFormat().setValue(SignatureFormat.FullName);
        signature.agree().setValue(true);
        signature.fullName().setValue(CrmAppContext.getCurrentUserEmployee().name().getStringView());

        Persistence.ensureRetrieve(leaseTerm, AttachLevel.Attached);
        leaseTerm.version().employeeSignature().set(signature);
        Persistence.service().merge(leaseTerm.version());

        // TODO Add lease agreement document generation logic that checks if all signatures present signed and creates a document

        LeaseTermAgreementDocumentBlob blob = EntityFactory.create(LeaseTermAgreementDocumentBlob.class);
        blob.data().setValue(bytes);
        blob.contentType().setValue(MimeMap.getContentType(DownloadFormat.PDF));
        Persistence.service().persist(blob);
        agreementDocument.file().fileName().setValue("agreement.pdf");
        agreementDocument.file().fileSize().setValue(bytes.length);
        agreementDocument.file().blobKey().set(blob.id());

        agreementDocument.leaseTermV().set(leaseTerm.version());
        // TODO set rest of signed people here.
        agreementDocument.isSignedByInk().setValue(false);
        agreementDocument.signedEmployeeUploader().set(signingUser);

        FileUploadRegistry.register(agreementDocument.file());
        Persistence.service().persist(agreementDocument);

    }

}
