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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.PaymentInformation;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseListDTO;
import com.propertyvista.portal.rpc.ptapp.services.PaymentService;
import com.propertyvista.portal.server.campaign.CampaignManager;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.server.common.util.TenantRetriever;
import com.propertyvista.server.domain.CampaignTrigger;

public class PaymentServiceImpl extends ApplicationEntityServiceImpl implements PaymentService {

    private final static Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<PaymentInformation> callback, Key tenantId) {
        log.debug("Retrieving PaymentInfo for tenant {}", tenantId);
        EntityQueryCriteria<PaymentInformation> criteria = EntityQueryCriteria.create(PaymentInformation.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication()));
        PaymentInformation payment = secureRetrieve(criteria);
        if (payment == null) {
            log.debug("Creating new payment");
            payment = EntityFactory.create(PaymentInformation.class);
            payment.type().setValue(PaymentType.Echeck);
            payment.preauthorised().setValue(Boolean.TRUE);

        }

        retrievePaymentInfo(payment);

        callback.onSuccess(payment);
    }

    @Override
    public void save(AsyncCallback<PaymentInformation> callback, PaymentInformation payment) {
        //        log.info("Saving charges\n{}", PrintUtil.print(summary));

        saveApplicationEntity(payment);

        boolean callFireDemo = false;
        if (callFireDemo) {
            EntityQueryCriteria<TenantInLeaseListDTO> criteria = EntityQueryCriteria.create(TenantInLeaseListDTO.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication()));
            CampaignManager.fireEvent(CampaignTrigger.ApplicationCompleated, secureRetrieve(criteria));
        }

        if ((EnumSet.of(PaymentType.Amex, PaymentType.Visa, PaymentType.MasterCard, PaymentType.Discover).contains(payment.type().getValue()))
                && ("2011".equals(payment.creditCard().cardNumber().getValue()))) {
            // Ok
        } else {
            throw new UserRuntimeException(i18n.tr("Your Card Has Been Declined"));
        }

        callback.onSuccess(payment);
    }

    private void retrievePaymentInfo(PaymentInformation paymentInfo) {
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

        EntityQueryCriteria<TenantInLease> criteria = EntityQueryCriteria.create(TenantInLease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lease(), PtAppContext.getCurrentUserLease()));
        for (TenantInLease tenantInfo : secureQuery(criteria)) {
            if (tenantInfo.status().getValue().equals(TenantInLease.Status.Applicant)) {
                TenantRetriever r = new TenantRetriever(tenantInfo.getPrimaryKey());
                paymentInfo.currentAddress().set(r.tenantScreening.currentAddress());
                paymentInfo.currentPhone().set(tenantInfo.tenant().person().homePhone());
                break;
            }
        }
    }
}
