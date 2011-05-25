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
package com.propertyvista.portal.server.ptapp.services;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.portal.domain.payment.PaymentType;
import com.propertyvista.portal.domain.ptapp.ChargeLine;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.PaymentInfo;
import com.propertyvista.portal.domain.ptapp.PotentialTenant.Status;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.PotentialTenantList;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;
import com.propertyvista.portal.rpc.ptapp.services.PaymentService;
import com.propertyvista.portal.server.campaign.CampaignManager;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.server.domain.CampaignTriger;

public class PaymentServiceImpl extends ApplicationEntityServiceImpl implements PaymentService {

    private final static Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<PaymentInfo> callback, String tenantId) {
        log.debug("Retrieving PaymentInfo for tenant {}", tenantId);
        EntityQueryCriteria<PaymentInfo> criteria = EntityQueryCriteria.create(PaymentInfo.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication()));
        PaymentInfo payment = secureRetrieve(criteria);
        if (payment == null) {
            log.debug("Creating new payment");
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

        saveApplicationEntity(payment);

        boolean callFireDemo = false;
        if (callFireDemo) {
            EntityQueryCriteria<PotentialTenantList> criteria = EntityQueryCriteria.create(PotentialTenantList.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication()));
            CampaignManager.fireEvent(CampaignTriger.ApplicationCompleated, secureRetrieve(criteria));
        }

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
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication()));
        for (PotentialTenantInfo tenantInfo : secureRetrieve(criteria).tenants()) {
            if (tenantInfo.status().getValue().equals(Status.Applicant)) {
                paymentInfo.currentAddress().set(tenantInfo.currentAddress());
                paymentInfo.currentPhone().set(tenantInfo.homePhone());
                break;
            }
        }
    }
}
