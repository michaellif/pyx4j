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

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;
import com.propertyvista.portal.rpc.ptapp.services.steps.ChargesService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.ptapp.services.ApplicationEntityServiceImpl;
import com.propertyvista.portal.server.ptapp.services.util.DigitalSignatureMgr;
import com.propertyvista.portal.server.ptapp.util.ChargesServerCalculation;

public class ChargesServiceImpl extends ApplicationEntityServiceImpl implements ChargesService {

    private final static Logger log = LoggerFactory.getLogger(ChargesServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<Charges> callback, Key tenantId) {
        log.debug("Retrieving charges for tenant {}", tenantId);
        callback.onSuccess(retrieveData());
    }

    @Override
    public void save(AsyncCallback<Charges> callback, Charges entity) {
        log.debug("Saving charges\n{}", entity);

        saveApplicationEntity(entity);

        // update tenant's percentage:
        for (TenantCharge charge : entity.paymentSplitCharges().charges()) {
            Persistence.service().merge(charge.tenant());
        }

        DigitalSignatureMgr.resetAll();
        Persistence.service().commit();

        // we do not use return value, so return the same as input one:        
        callback.onSuccess(entity);
        // but, strictly speaking, this call should look like:        
//        callback.onSuccess(retrieveData());
    }

    public Charges retrieveData() {
        Lease lease = PtAppContext.retrieveCurrentUserLease();
        Persistence.service().retrieve(lease.currentTerm().version().tenants());

        Charges charges = retrieveApplicationEntity(Charges.class);
        if (charges == null) {
            log.debug("Creating new charges");
            charges = EntityFactory.create(Charges.class);
            charges.application().set(PtAppContext.retrieveCurrentUserApplication());
            ChargesServerCalculation.updatePaymentSplitCharges(charges, lease.currentTerm().version().tenants());
        }

        BillableItem serviceItem = lease.currentTerm().version().leaseProducts().serviceItem();
        if (serviceItem != null && !serviceItem.isNull()) {
            charges.monthlyCharges().charges().clear();

            charges.monthlyCharges().charges()
                    .add(DomainUtil.createChargeLine(serviceItem.item().type().getStringView(), serviceItem.agreedPrice().getValue()));

            // create/update deposits:
            charges.applicationCharges().charges().clear();
            charges.applicationCharges().charges().add(DomainUtil.createChargeLine(ChargeLine.ChargeType.deposit, serviceItem.agreedPrice().getValue()));

            // TODO: find where get/put this info (application/equifax check fee)!
            charges.applicationCharges().charges().add(DomainUtil.createChargeLine(ChargeLine.ChargeType.oneTimePayment, new BigDecimal("24.99"))); // get value from policy ! 

            // fill agreed items:
            for (BillableItem item : lease.currentTerm().version().leaseProducts().featureItems()) {
                if (item.item().type().isInstanceOf(FeatureItemType.class)) {

                    switch (item.item().type().<FeatureItemType> cast().featureType().getValue()) {
                    case utility:
                    case pet:
                    case parking:
                    case locker:
                        charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(item.item().type().getStringView(), item.agreedPrice().getValue()));
                        break;

                    default:
                        charges.oneTimeCharges().charges().add(DomainUtil.createChargeLine(item.item().type().getStringView(), item.agreedPrice().getValue()));
                    }
                }
            }

            charges.rentStart().setValue(lease.leaseFrom().getValue());
        }

        ChargesSharedCalculation.calculateCharges(charges);

        return charges;
    }
}
