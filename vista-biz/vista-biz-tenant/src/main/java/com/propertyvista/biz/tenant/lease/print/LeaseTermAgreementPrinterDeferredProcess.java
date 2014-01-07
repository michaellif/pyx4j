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
package com.propertyvista.biz.tenant.lease.print;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.domain.blob.LeaseTermAgreementDocumentBlob;
import com.propertyvista.domain.policy.policies.domain.AgreementLegalTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;

@SuppressWarnings("serial")
class LeaseTermAgreementPrinterDeferredProcess extends AbstractDeferredProcess {

    private final LeaseTerm leaseTerm;

    private final LeaseTermAgreementDocument agreementDocument;

    public LeaseTermAgreementPrinterDeferredProcess(LeaseTerm leaseTerm) {
        super();
        this.leaseTerm = leaseTerm;
        this.agreementDocument = EntityFactory.create(LeaseTermAgreementDocument.class);
        this.agreementDocument.leaseTermV().set(this.leaseTerm.version());
    }

    @Override
    public void execute() {
        //TODO Do actual blob creation

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                try {
                    LeaseAgreementData leaseAgreementData = EntityFactory.create(LeaseAgreementData.class);
                    Persistence.service().retrieve(leaseTerm.version().agreementLegalTerms());
                    leaseAgreementData.terms().addAll(makeTermsForPrint(leaseTerm));

                    byte[] agreementPdf = LeaseTermAgreementPdfCreator.createPdf(leaseAgreementData);
                    saveBlob(agreementPdf);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                return null;
            }

        });

    }

    private void saveBlob(byte[] bytes) {
        LeaseTermAgreementDocumentBlob blob = EntityFactory.create(LeaseTermAgreementDocumentBlob.class);
        blob.data().setValue(bytes);
        blob.contentType().setValue(MimeMap.getContentType(DownloadFormat.PDF));
        Persistence.service().persist(blob);
        agreementDocument.file().fileName().setValue("agreement.pdf");
        agreementDocument.file().fileSize().setValue(bytes.length);
        agreementDocument.file().blobKey().set(blob.id());
        FileUploadRegistry.register(agreementDocument.file());
        Persistence.service().persist(agreementDocument);
    }

    private Collection<? extends AgreementLegalTerm4Print> makeTermsForPrint(LeaseTerm leaseTerm) {
        List<AgreementLegalTerm4Print> legalTerms4Print = new LinkedList<AgreementLegalTerm4Print>();
        for (AgreementLegalTerm legalTerm : leaseTerm.version().agreementLegalTerms()) {
            legalTerms4Print.add(makeTermForPrint(legalTerm));
        }
        return legalTerms4Print;
    }

    private AgreementLegalTerm4Print makeTermForPrint(AgreementLegalTerm legalTerm) {
        AgreementLegalTerm4Print legalTerm4Print = EntityFactory.create(AgreementLegalTerm4Print.class);
        legalTerm4Print.title().setValue(legalTerm.title().getValue());
        legalTerm4Print.body().setValue(legalTerm.body().getValue());
        // TODO deal with signatures
        return legalTerm4Print;
    }

}
