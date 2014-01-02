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

import java.util.Date;

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
                saveBlob(String.valueOf(new Date()).getBytes());
                return null;
            }
        });

    }

    private void saveBlob(byte[] bytes) {
        LeaseTermAgreementDocumentBlob blob = EntityFactory.create(LeaseTermAgreementDocumentBlob.class);
        blob.data().setValue(bytes);
        blob.contentType().setValue(MimeMap.getContentType(DownloadFormat.TXT));
        Persistence.service().persist(blob);
        agreementDocument.file().fileName().setValue("agreement.txt");
        agreementDocument.file().fileSize().setValue(bytes.length);
        agreementDocument.file().blobKey().set(blob.id());
        FileUploadRegistry.register(agreementDocument.file());
        Persistence.service().persist(agreementDocument);
    }

}
