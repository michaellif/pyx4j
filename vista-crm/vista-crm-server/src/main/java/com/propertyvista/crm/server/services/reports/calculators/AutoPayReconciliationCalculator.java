/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 10, 2015
 * @author vlads
 */
package com.propertyvista.crm.server.services.reports.calculators;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.crm.rpc.dto.reports.AutoPayReconciliationDTO;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.ARCode.ActionType;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.shared.config.VistaFeatures;

public class AutoPayReconciliationCalculator {

    public static void calculate(AutoPayReconciliationDTO reconciliationTo) {
        reconciliationTo.price().setValue(BigDecimal.ZERO);
        reconciliationTo.payment().setValue(BigDecimal.ZERO);

        reconciliationTo.rentCharge().setValue(BigDecimal.ZERO);
        reconciliationTo.parkingCharges().setValue(BigDecimal.ZERO);
        reconciliationTo.otherCharges().setValue(BigDecimal.ZERO);

        for (AutopayAgreementCoveredItem item : reconciliationTo.coveredItems()) {
            reconciliationTo.payment().setValue(reconciliationTo.payment().getValue().add(item.amount().getValue()));
        }

        BillingCycle nextPaymentCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(reconciliationTo.tenant().lease());

        for (BillableItem billableItem : ServerSideFactory.create(PaymentMethodFacade.class).getAutoPpayApplicabelItems(reconciliationTo.tenant().lease(),
                nextPaymentCycle)) {
            BigDecimal actualPrice = ServerSideFactory.create(BillingFacade.class).getActualPrice(billableItem);
            reconciliationTo.price().setValue(reconciliationTo.price().getValue().add(actualPrice));

            ARCode arCode = null;
            if (!billableItem.item().product().holder().code().type().isNull()) {
                arCode = billableItem.item().product().holder().code();
            } else if (VistaFeatures.instance().yardiIntegration() && !billableItem.yardiChargeCode().isNull()) {
                arCode = retrieveARCode(ActionType.Debit, billableItem.yardiChargeCode().getValue());
            }

            if (arCode != null) {
                switch (arCode.type().getValue()) {
                case Residential:
                    reconciliationTo.rentCharge().setValue(reconciliationTo.rentCharge().getValue().add(actualPrice));
                    reconciliationTo.renewalDate().setValue(billableItem.effectiveDate().getValue());
                    break;
                case Parking:
                    reconciliationTo.parkingCharges().setValue(reconciliationTo.parkingCharges().getValue().add(actualPrice));
                    break;
                default:
                    reconciliationTo.otherCharges().setValue(reconciliationTo.otherCharges().getValue().add(actualPrice));
                }
            } else {
                reconciliationTo.otherCharges().setValue(reconciliationTo.otherCharges().getValue().add(actualPrice));
            }

            if (!billableItem.effectiveDate().isNull() && reconciliationTo.renewalDate().isNull()) {
                reconciliationTo.renewalDate().setValue(billableItem.effectiveDate().getValue());
            }
        }

        // Count other active agreements 
        {
            EntityQueryCriteria<AutopayAgreement> criteria = EntityQueryCriteria.create(AutopayAgreement.class);
            criteria.eq(criteria.proto().tenant().lease(), reconciliationTo.tenant().lease());
            criteria.eq(criteria.proto().isDeleted(), false);
            int agreementsCount = Persistence.service().count(criteria);
            reconciliationTo.paymentShareAmount().setValue(
                    reconciliationTo.price().getValue().divide(BigDecimal.valueOf(agreementsCount), RoundingMode.HALF_UP));
        }
        reconciliationTo.discrepancy().setValue(reconciliationTo.payment().getValue().subtract(reconciliationTo.paymentShareAmount().getValue()));

        reconciliationTo.count().setValue(1);
    }

    private static ARCode retrieveARCode(ARCode.ActionType actionType, String chargeCode) {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.eq(criteria.proto().yardiChargeCodes().$().yardiChargeCode(), chargeCode);
        criteria.in(criteria.proto().type(), ARCode.Type.allOfActionType(actionType));
        return Persistence.service().retrieve(criteria);
    }
}
