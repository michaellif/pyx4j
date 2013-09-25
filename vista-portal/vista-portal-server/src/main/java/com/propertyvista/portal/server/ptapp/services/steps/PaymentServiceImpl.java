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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.LegalDocumentation;
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
import com.propertyvista.server.common.util.AddressConverter;
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
        LegalDocumentation termsPolicy = LegalStuffUtils.retrieveLegalTermsPolicy();

        // TODO somehow distinguish appropriate terms in the array: 

        if (termsPolicy.paymentAuthorization().size() > 0) {
            payment.oneTimePaymentTerms().set(LegalStuffUtils.formLegalTerms(termsPolicy.paymentAuthorization().get(0)));
        }
        if (termsPolicy.paymentAuthorization().size() > 1) {
            payment.recurrentPaymentTerms().set(LegalStuffUtils.formLegalTerms(termsPolicy.paymentAuthorization().get(1)));
        }

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
            CampaignManager.fireEvent(CampaignTrigger.ApplicationCompleted, Persistence.secureRetrieve(criteria));
        }

        if ((PaymentType.CreditCard == payment.paymentMethod().type().getValue())
                && ("2011".equals(((CreditCardInfo) payment.paymentMethod().details()).card().number().getValue()))) {
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
    public void getCurrentAddress(AsyncCallback<AddressSimple> callback) {
        CustomerRetriever r = new CustomerRetriever(PtAppContext.retrieveCurrentUserCustomer().getPrimaryKey());
        AddressSimple address = EntityFactory.create(AddressSimple.class);
        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(r.getScreening().version().currentAddress(), address);
        callback.onSuccess(address);
    }
}
