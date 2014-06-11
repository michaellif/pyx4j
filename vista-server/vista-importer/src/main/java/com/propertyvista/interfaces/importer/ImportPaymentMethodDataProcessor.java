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
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.interfaces.importer.converter.AddressSimpleConverter;
import com.propertyvista.interfaces.importer.model.PaymentMethodIO;

public class ImportPaymentMethodDataProcessor {

    public LeasePaymentMethod importModel(ImportProcessorContext context, LeaseTermTenant leaseTermTenant, PaymentMethodIO model) {
        Persistence.ensureRetrieve(leaseTermTenant.leaseParticipant().customer().paymentMethods(), AttachLevel.Attached);
        LeasePaymentMethod paymentMethod = retrievePaymentMethod(leaseTermTenant.leaseParticipant().customer(), model);
        if (paymentMethod == null) {
            paymentMethod = createPaymentMethod(context.building, leaseTermTenant.leaseParticipant().customer(), model);
        }
        return paymentMethod;
    }

    private LeasePaymentMethod createPaymentMethod(Building buildingId, Customer customer, PaymentMethodIO model) {
        EcheckInfo details = EntityFactory.create(EcheckInfo.class);
        details.nameOn().setValue(model.nameOnAccount().getValue());
        details.accountNo().newNumber().setValue(model.accountNumber().getValue().trim());
        details.bankId().setValue(model.bankId().getValue().trim());
        details.branchTransitNumber().setValue(model.transitNumber().getValue().trim());

        LeasePaymentMethod method = EntityFactory.create(LeasePaymentMethod.class);
        method.isProfiledMethod().setValue(Boolean.TRUE);
        method.sameAsCurrent().setValue(Boolean.FALSE);
        method.type().setValue(PaymentType.Echeck);
        method.details().set(details);
        method.customer().set(customer);

        method.billingAddress().set(new AddressSimpleConverter().createBO(model.billingAddress()));

        ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(method, buildingId);

        return method;
    }

    private LeasePaymentMethod retrievePaymentMethod(Customer customer, PaymentMethodIO model) {
        for (LeasePaymentMethod method : customer.paymentMethods()) {
            if (method.type().getValue().equals(PaymentType.Echeck)) {
                EcheckInfo details = method.details().duplicate(EcheckInfo.class);
                if (details.bankId().getValue().equals(model.bankId().getValue())
                        && details.branchTransitNumber().getValue().equals(model.transitNumber().getValue())
                        && details.accountNo().number().getValue().equals(model.accountNumber().getValue())) {
                    return method;
                }
            }
        }
        return null;
    }
}
