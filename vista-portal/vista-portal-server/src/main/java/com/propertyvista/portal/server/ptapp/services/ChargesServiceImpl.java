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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;
import com.propertyvista.portal.rpc.ptapp.services.ChargesService;
import com.propertyvista.portal.server.ptapp.ChargesServerCalculation;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;
import com.propertyvista.server.common.util.TenantInLeaseRetriever;

public class ChargesServiceImpl extends ApplicationEntityServiceImpl implements ChargesService {

    private final static Logger log = LoggerFactory.getLogger(ChargesServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<Charges> callback, Key tenantId) {
        log.debug("Retrieving charges for tenant {}", tenantId);

        Lease lease = PtAppContext.getCurrentUserLease();
        TenantInLeaseRetriever.UpdateLeaseTenants(lease);
        Persistence.service().retrieve(lease.tenants());

        Charges charges = retrieveApplicationEntity(Charges.class);
        if (charges == null) {
            log.debug("Creating new charges");
            charges = EntityFactory.create(Charges.class);
            charges.application().set(PtAppContext.getCurrentUserApplication());
            ChargesServerCalculation.updatePaymentSplitCharges(charges, lease.tenants());
        }

        ChargeItem serviceItem = lease.serviceAgreement().serviceItem();
        if (serviceItem != null && !serviceItem.isNull()) {
            charges.monthlyCharges().charges().clear();
            charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(serviceItem.item().type().getStringView(), serviceItem.originalPrice().getValue()));

            // fill agreed items:
            for (ChargeItem item : lease.serviceAgreement().featureItems()) {
                if (item.item().type().type().getValue().equals(ServiceItemType.Type.feature)) {
                    PriceCalculationHelpers.calculateChargeItemAdjustments(item);

                    switch (item.item().type().featureType().getValue()) {
                    case utility:
                    case pet:
                    case parking:
                    case locker:
                        charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(item.item().type().getStringView(), item.originalPrice().getValue()));
                        break;

                    default:
                        charges.oneTimeCharges().charges().add(DomainUtil.createChargeLine(item.item().type().getStringView(), item.originalPrice().getValue()));
                    }
                }
            }

            charges.rentStart().setValue(lease.leaseFrom().getValue());

            ChargesSharedCalculation.calculateCharges(charges);
        }

        loadTransientData(charges);

        callback.onSuccess(charges);
    }

    @Override
    public void save(AsyncCallback<Charges> callback, Charges charges) {
        //log.debug("Saving charges\n{}", VistaDataPrinter.print(charges));

        saveApplicationEntity(charges);

        loadTransientData(charges);

        callback.onSuccess(charges);
    }

    // If adding new data here sync with @see SummaryServicesImpl#retrieveSummary 
    private void loadTransientData(Charges charges) {
        for (TenantCharge charge : charges.paymentSplitCharges().charges()) {
            TenantInLease tenant = Persistence.service().retrieve(TenantInLease.class, charge.tenant().getPrimaryKey());
            charge.tenantName().set(tenant.tenant().person().name().detach());
        }
    }
}
