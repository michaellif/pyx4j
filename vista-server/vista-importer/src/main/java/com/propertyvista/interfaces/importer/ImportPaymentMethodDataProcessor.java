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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.CreditCardFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.interfaces.importer.converter.AddressSimpleConverter;
import com.propertyvista.interfaces.importer.model.CreditCardIO;
import com.propertyvista.interfaces.importer.model.EcheckIO;
import com.propertyvista.interfaces.importer.model.PaymentMethodIO;

public class ImportPaymentMethodDataProcessor {

    public LeasePaymentMethod importModel(ImportProcessorContext context, Lease lease, LeaseTermTenant leaseTermTenant, PaymentMethodIO model) {
        Persistence.ensureRetrieve(leaseTermTenant.leaseParticipant().customer().paymentMethods(), AttachLevel.Attached);
        LeasePaymentMethod paymentMethod = retrievePaymentMethod(leaseTermTenant.leaseParticipant().customer(), model);
        if (paymentMethod == null) {
            paymentMethod = createPaymentMethod(context, lease, leaseTermTenant.leaseParticipant().customer(), model);
        }
        return paymentMethod;
    }

    private LeasePaymentMethod createPaymentMethod(ImportProcessorContext context, Lease lease, Customer customer, PaymentMethodIO model) {
        LeasePaymentMethod method = EntityFactory.create(LeasePaymentMethod.class);
        method.isProfiledMethod().setValue(Boolean.TRUE);
        method.sameAsCurrent().setValue(Boolean.FALSE);
        method.customer().set(customer);

        if (model.details().isInstanceOf(EcheckIO.class)) {
            method.type().setValue(PaymentType.Echeck);
            EcheckIO ec = model.details().cast();
            EcheckInfo details = EntityFactory.create(EcheckInfo.class);
            details.nameOn().setValue(ec.nameOnAccount().getValue());
            details.accountNo().newNumber().setValue(ec.accountNumber().getValue().trim());
            details.bankId().setValue(ec.bankId().getValue().trim());
            details.branchTransitNumber().setValue(ec.transitNumber().getValue().trim());
            method.details().set(details);
        } else if (model.details().isInstanceOf(CreditCardIO.class)) {
            method.type().setValue(PaymentType.CreditCard);
            CreditCardIO cc = model.details().cast();
            CreditCardInfo details = EntityFactory.create(CreditCardInfo.class);

            details.nameOn().setValue(cc.nameOnAccount().getValue());
            details.cardType().setValue(cc.cardType().getValue());
            details.card().newNumber().setValue(cc.cardNumber().getValue());
            details.expiryDate().setValue(cc.expiryDate().getValue());

            if (!ServerSideFactory.create(CreditCardFacade.class).validateCreditCard(details)) {
                if (cc.propertyVistaId().isNull()) {
                    context.monitor.addErredEvent("PaymentMethod", "Lease " + lease.leaseId().getStringView() + " Invalid Credit Card Number");
                } else {
                    Pmc pmc = VistaDeployment.getCurrentPmc();
                    if (cc.propertyVistaId().getValue().startsWith(pmc.id().getStringView() + ":")) {
                        context.monitor.addErredEvent("PaymentMethod", "Lease " + lease.leaseId().getStringView() + " Credit Card Reference Not Found");
                    } else {
                        context.monitor.addFailedEvent("PaymentMethod", "Lease " + lease.leaseId().getStringView()
                                + " Cross PMC Credit Card migration not supported");
                    }
                }
                return null;
            }

            method.details().set(details);
        }

        method.billingAddress().set(new AddressSimpleConverter().createBO(model.billingAddress()));

        ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(method, context.building);

        return method;
    }

    private LeasePaymentMethod retrievePaymentMethod(Customer customer, PaymentMethodIO model) {
        for (LeasePaymentMethod method : customer.paymentMethods()) {
            if (method.type().getValue().equals(PaymentType.Echeck) && model.details().isInstanceOf(EcheckIO.class)) {
                EcheckInfo details = method.details().cast();
                EcheckIO ec = model.details().cast();
                if (details.bankId().getValue().equals(ec.bankId().getValue()) //
                        && details.branchTransitNumber().getValue().equals(ec.transitNumber().getValue()) //
                        && details.accountNo().number().getValue().equals(ec.accountNumber().getValue())) {
                    return method;
                }
            } else if (method.type().getValue().equals(PaymentType.CreditCard) && model.details().isInstanceOf(CreditCardIO.class)) {
                CreditCardInfo details = method.details().cast();
                CreditCardIO cc = model.details().cast();

                Pmc pmc = VistaDeployment.getCurrentPmc();
                if (cc.propertyVistaId().getValue().startsWith(pmc.id().getStringView() + ":")) {
                    // Only use ID in the same PMC
                    String paymentMethodId = cc.propertyVistaId().getValue().substring(cc.propertyVistaId().getValue().indexOf(':') + 1);
                    if (method.id().getValue().toString().equals(paymentMethodId) //
                            && details.cardType().getValue().equals(cc.cardType().getValue())) {
                        return method;
                    }
                }
            }
        }
        return null;
    }
}
