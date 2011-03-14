/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.pt.services;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.payment.PaymentType;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.PaymentInfo;
import com.propertyvista.portal.domain.pt.PotentialTenant.Status;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.rpc.pt.ChargesSharedCalculation;
import com.propertyvista.portal.rpc.pt.services.PaymentServices;
import com.propertyvista.portal.server.campaign.CampaignManager;
import com.propertyvista.portal.server.pt.PtUserDataAccess;
import com.propertyvista.server.domain.CampaignTriger;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.UserRuntimeException;

public class PaymentServicesImpl extends ApplicationEntityServicesImpl implements PaymentServices {
    private final static Logger log = LoggerFactory.getLogger(PaymentServicesImpl.class);

    @Override
    public void retrieve(AsyncCallback<PaymentInfo> callback, Long tenantId) {
        log.info("Retrieving summary for tenant {}", tenantId);
        EntityQueryCriteria<PaymentInfo> criteria = EntityQueryCriteria.create(PaymentInfo.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
        PaymentInfo payment = secureRetrieve(criteria);
        if (payment == null) {
            log.info("Creating new payment");
            payment = EntityFactory.create(PaymentInfo.class);
            payment.type().setValue(PaymentType.Echeck);
            payment.preauthorised().setValue(Boolean.TRUE);

        }

        retrievePaymentInfo(payment);

        callback.onSuccess(payment);
    }

    @Override
    public void save(AsyncCallback<PaymentInfo> callback, PaymentInfo payment) {
//        log.info("Saving charges\n{}", PrintUtil.print(summary));

        applyApplication(payment);

        secureSave(payment);

        EntityQueryCriteria<PotentialTenantList> criteria = EntityQueryCriteria.create(PotentialTenantList.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
        CampaignManager.fireEvent(CampaignTriger.ApplicationCompleated, secureRetrieve(criteria));

        if ((EnumSet.of(PaymentType.Amex, PaymentType.Visa, PaymentType.MasterCard, PaymentType.Discover).contains(payment.type().getValue()))
                && ("2011".equals(payment.creditCard().cardNumber().getValue()))) {
            // Ok
        } else {
            throw new UserRuntimeException(i18n.tr("Your card has been declined"));
        }

        callback.onSuccess(payment);
    }

    private void retrievePaymentInfo(PaymentInfo paymentInfo) {
        // TODO VladS find a better way to retrieve just monthlyCharges
        Charges charges = EntityFactory.create(Charges.class);
        retrieveApplicationEntity(charges);
        ChargesSharedCalculation.calculateTotal(paymentInfo.applicationCharges());
        for (ChargeLine charge : charges.applicationCharges().charges()) {
            if (charge.type().getValue() == ChargeLine.ChargeType.applicationFee) {
                paymentInfo.applicationFee().set(charge);
            } else {
                paymentInfo.applicationCharges().charges().add(charge);
            }
        }
        // Get the currentAddress
        EntityQueryCriteria<PotentialTenantList> criteria = EntityQueryCriteria.create(PotentialTenantList.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
        for (PotentialTenantInfo tenantInfo : secureRetrieve(criteria).tenants()) {
            if (tenantInfo.status().getValue().equals(Status.Applicant)) {
                paymentInfo.currentAddress().set(tenantInfo.currentAddress());
                paymentInfo.currentPhone().set(tenantInfo.homePhone());
                break;
            }
        }
    }
}
