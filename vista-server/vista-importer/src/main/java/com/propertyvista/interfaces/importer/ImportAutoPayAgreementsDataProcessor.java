/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2014
 * @author vlads
 */
package com.propertyvista.interfaces.importer;

import java.math.BigDecimal;
import java.util.Map;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.financial.payment.PaymentBillableUtils;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.interfaces.importer.model.AutoPayAgreementCoveredItemIO;
import com.propertyvista.interfaces.importer.model.AutoPayAgreementIO;

public class ImportAutoPayAgreementsDataProcessor {

    public void importModel(ImportProcessorContext context, Lease lease, LeaseTermTenant leaseTermTenant, AutoPayAgreementIO model) {

        LeasePaymentMethod paymentMethod = new ImportPaymentMethodDataProcessor().importModel(context, lease, leaseTermTenant, model.paymentMethod());
        if (paymentMethod == null) {
            return;
        }

        AutopayAgreement pap = EntityFactory.create(AutopayAgreement.class);
        pap.paymentMethod().set(paymentMethod);
        pap.tenant().set(leaseTermTenant.leaseParticipant());

        Map<String, BillableItem> billableItems = PaymentBillableUtils.getAllBillableItems(lease.currentTerm().version());

        BigDecimal total = BigDecimal.ZERO;
        for (AutoPayAgreementCoveredItemIO itemModel : model.items()) {
            BillableItem matchingBillableItem = findMatchingBillableItem(billableItems, itemModel);
            if (matchingBillableItem == null) {
                context.monitor.addErredEvent("AutoPayAgreement", "BillableItem " + lease.leaseId().getStringView() + " " + itemModel.getStringView()
                        + " not found");
                return;
            }
            billableItems.remove(matchingBillableItem.uuid().getValue());

            AutopayAgreementCoveredItem padItem = EntityFactory.create(AutopayAgreementCoveredItem.class);
            padItem.billableItem().set(matchingBillableItem);
            padItem.amount().setValue(itemModel.amount().getValue());
            pap.coveredItems().add(padItem);

            total = total.add(padItem.amount().getValue());
        }

        ServerSideFactory.create(PaymentMethodFacade.class).persistAutopayAgreement(pap, pap.tenant());
        context.monitor.addProcessedEvent("AutoPayAgreement", total, "AutoPayAgreement " + lease.leaseId().getStringView() + " imported");
    }

    private BillableItem findMatchingBillableItem(Map<String, BillableItem> billableItems, AutoPayAgreementCoveredItemIO itemModel) {
        if (!itemModel.chargeId().isNull()) {
            BillableItem billableItem = billableItems.get(itemModel.chargeId().getValue());
            if (billableItem != null) {
                return billableItem;
            }
        }

        for (BillableItem billableItem : billableItems.values()) {
            if (!itemModel.chargeCode().equals(billableItem.yardiChargeCode())) {
                continue;
            } else if (itemModel.chargeAmount().getValue(BigDecimal.ZERO).compareTo(billableItem.agreedPrice().getValue(BigDecimal.ZERO)) != 0) {
                continue;
            } else {
                return billableItem;
            }
        }

        return null;
    }
}
