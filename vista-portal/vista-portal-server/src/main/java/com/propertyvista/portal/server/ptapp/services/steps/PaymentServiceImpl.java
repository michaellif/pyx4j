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
package com.propertyvista.portal.server.ptapp.services.steps;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.LeaseTermsPolicy;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.PaymentInformation;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;
import com.propertyvista.portal.rpc.ptapp.dto.PaymentInformationDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseListDTO;
import com.propertyvista.portal.rpc.ptapp.services.steps.PaymentService;
import com.propertyvista.portal.server.campaign.CampaignManager;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.ptapp.services.ApplicationEntityServiceImpl;
import com.propertyvista.portal.server.ptapp.services.util.LegalStuffUtils;
import com.propertyvista.server.common.util.CustomerRetriever;
import com.propertyvista.server.domain.CampaignTrigger;

public class PaymentServiceImpl extends ApplicationEntityServiceImpl implements PaymentService {

    private static final I18n i18n = I18n.get(PaymentServiceImpl.class);

    private final static Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<PaymentInformationDTO> callback, Key tenantId) {
        log.info("Retrieving PaymentInfo for tenant {}", tenantId);

        PaymentInformation paymentDBO = retrieveApplicationEntity(PaymentInformation.class);
        if (paymentDBO == null) {
            log.debug("Creating new payment");
            paymentDBO = EntityFactory.create(PaymentInformationDTO.class);
            paymentDBO.paymentMethod().type().setValue(PaymentType.Echeck);
            paymentDBO.preauthoriseAgree().setValue(Boolean.TRUE);
        }

        PaymentInformationDTO payment = paymentDBO.duplicate(PaymentInformationDTO.class);

        // TODO VladS find a better way to retrieve just monthlyCharges
        Charges charges = retrieveApplicationEntity(Charges.class);
        if (charges != null) {
            payment.applicationCharges().charges().addAll(charges.applicationCharges().charges());
            ChargesSharedCalculation.calculateTotal(payment.applicationCharges());
        }

        // Legal stuff:
        LeaseTermsPolicy termsPolicy = LegalStuffUtils.retrieveLegalTermsPolicy();
        payment.oneTimePaymentTerms().set(LegalStuffUtils.formLegalTerms(termsPolicy.oneTimePaymentTerms()));
        payment.recurrentPaymentTerms().set(LegalStuffUtils.formLegalTerms(termsPolicy.recurrentPaymentTerms()));

        callback.onSuccess(payment);
    }

    @Override
    public void save(AsyncCallback<PaymentInformationDTO> callback, PaymentInformationDTO payment) {
//        log.info("Saving PaymentInformationDTO\n", VistaDataPrinter.print(payment));

        saveApplicationEntity(payment.duplicate(PaymentInformation.class));

        boolean callFireDemo = false;
        if (callFireDemo) {
            EntityQueryCriteria<TenantInLeaseListDTO> criteria = EntityQueryCriteria.create(TenantInLeaseListDTO.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.retrieveCurrentUserApplication()));
            CampaignManager.fireEvent(CampaignTrigger.ApplicationCompleated, Persistence.secureRetrieve(criteria));
        }

        if ((EnumSet.of(PaymentType.Visa, PaymentType.MasterCard, PaymentType.Discover).contains(payment.paymentMethod().type().getValue()))
                && ("2011".equals(payment.paymentMethod().creditCard().number().getValue()))) {
            // Ok
        } else if (PaymentType.Echeck == payment.paymentMethod().type().getValue()) {
            // Ok for now
        } else {
            throw new UserRuntimeException(i18n.tr("Your Card Has Been Declined"));
        }

        Persistence.service().commit();
        callback.onSuccess(payment);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressStructured> callback) {
        CustomerRetriever r = new CustomerRetriever(PtAppContext.retrieveCurrentUserCustomer().getPrimaryKey());
        callback.onSuccess(r.getScreening().currentAddress().duplicate(AddressStructured.class));
    }
}
