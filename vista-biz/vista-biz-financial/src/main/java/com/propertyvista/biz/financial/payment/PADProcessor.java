/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.payment.pad.PadBatch;
import com.propertyvista.admin.domain.payment.pad.PadDebitRecord;
import com.propertyvista.admin.domain.payment.pad.PadFile;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.EcheckInfo;

public class PADProcessor {

    void queuePayment(PaymentRecord paymentRecord) {
        MerchantAccount merchantAccount = PaymentUtils.retrieveMerchantAccount(paymentRecord);
        Persistence.service().retrieve(paymentRecord.billingAccount());
        // TODO new Transaction
        String namespace = NamespaceManager.getNamespace();
        try {
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
            PadFile padFile = getPadFile();
            PadBatch padBatch = getPadBatch(padFile, namespace, merchantAccount);
            createPadDebitRecord(padBatch, paymentRecord);
        } finally {
            NamespaceManager.setNamespace(namespace);
        }
    }

    PadFile getPadFile() {
        EntityQueryCriteria<PadFile> criteria = EntityQueryCriteria.create(PadFile.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), PadFile.PadFileStatus.Creating));
        PadFile padFile = Persistence.service().retrieve(criteria);
        if (padFile == null) {
            padFile = EntityFactory.create(PadFile.class);
            padFile.status().setValue(PadFile.PadFileStatus.Creating);
            Persistence.service().persist(padFile);
        }
        return padFile;
    }

    private PadBatch getPadBatch(PadFile padFile, String namespace, MerchantAccount merchantAccount) {
        EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().padFile(), padFile));
        criteria.add(PropertyCriterion.eq(criteria.proto().pmcNamespace(), namespace));
        criteria.add(PropertyCriterion.eq(criteria.proto().merchantAccountKey(), merchantAccount.id()));
        PadBatch padBatch = Persistence.service().retrieve(criteria);
        if (padBatch == null) {
            padBatch = EntityFactory.create(PadBatch.class);
            padBatch.padFile().set(padFile);
            padBatch.pmcNamespace().setValue(namespace);
            padBatch.merchantAccount().set(merchantAccount);
            padBatch.merchantAccountKey().setValue(merchantAccount.id().getValue());
            Persistence.service().persist(padBatch);
        }
        return padBatch;
    }

    private void createPadDebitRecord(PadBatch padBatch, PaymentRecord paymentRecord) {
        PadDebitRecord padRecord = EntityFactory.create(PadDebitRecord.class);
        padRecord.padBatch().set(padBatch);
        padRecord.clientId().setValue(paymentRecord.billingAccount().accountNumber().getValue());
        padRecord.amount().setValue(paymentRecord.amount().getValue());
        EcheckInfo echeckInfo = paymentRecord.paymentMethod().details().cast();

        padRecord.bankId().setValue(echeckInfo.bankId().getValue());
        padRecord.branchTransitNumber().setValue(echeckInfo.branchTransitNumber().getValue());
        padRecord.accountNumber().setValue(echeckInfo.accountNo().getValue());

        padRecord.transactionId().setValue(paymentRecord.id().getStringView());

        Persistence.service().persist(padRecord);

    }
}
