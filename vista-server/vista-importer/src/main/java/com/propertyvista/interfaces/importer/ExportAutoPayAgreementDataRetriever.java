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

import java.util.ArrayList;
import java.util.Collection;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.PaymentBillableUtils;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.interfaces.importer.model.AutoPayAgreementCoveredItemIO;
import com.propertyvista.interfaces.importer.model.AutoPayAgreementIO;

public class ExportAutoPayAgreementDataRetriever {

    private Collection<AutopayAgreement> retrive(LeaseTermTenant leaseTermTenant) {
        EntityQueryCriteria<AutopayAgreement> criteria = EntityQueryCriteria.create(AutopayAgreement.class);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        criteria.eq(criteria.proto().tenant(), leaseTermTenant.leaseParticipant());
        criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.Echeck);
        return Persistence.service().query(criteria);

    }

    public Collection<AutoPayAgreementIO> getModel(LeaseTermTenant leaseTermTenant) {
        Collection<AutoPayAgreementIO> agreementModels = new ArrayList<>();
        for (AutopayAgreement agreement : retrive(leaseTermTenant)) {
            AutoPayAgreementIO model = EntityFactory.create(AutoPayAgreementIO.class);

            model.paymentMethod().set(new ExportPaymentMethodDataRetriever().getModel(agreement.paymentMethod()));

            for (AutopayAgreementCoveredItem item : agreement.coveredItems()) {
                AutoPayAgreementCoveredItemIO itemModel = EntityFactory.create(AutoPayAgreementCoveredItemIO.class);

                itemModel.amount().setValue(item.amount().getValue());
                itemModel.chargeAmount().setValue(PaymentBillableUtils.getActualPrice(item.billableItem()));

                itemModel.chargeId().setValue(item.billableItem().uid().getStringView());
                itemModel.chargeCode().setValue(item.billableItem().yardiChargeCode().getValue());
                if (!item.billableItem().item().product().holder().code().type().isNull()) {
                    itemModel.chargeARCodeType().setValue(item.billableItem().item().product().holder().code().type().getValue().name());
                }
                itemModel.description().setValue(item.billableItem().item().getStringView());

                model.items().add(itemModel);
            }

            agreementModels.add(model);
        }
        return agreementModels;
    }

}
