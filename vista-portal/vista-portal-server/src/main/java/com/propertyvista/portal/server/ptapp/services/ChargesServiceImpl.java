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
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityFromatUtils;

import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.domain.util.VistaDataPrinter;
import com.propertyvista.portal.rpc.ptapp.services.ChargesService;
import com.propertyvista.portal.server.ptapp.ChargesServerCalculation;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class ChargesServiceImpl extends ApplicationEntityServiceImpl implements ChargesService {

    private final static Logger log = LoggerFactory.getLogger(ChargesServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<Charges> callback, Key tenantId) {
        log.debug("Retrieving charges for tenant {}", tenantId);
        EntityQueryCriteria<Charges> criteria = EntityQueryCriteria.create(Charges.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication()));
        Charges charges = secureRetrieve(criteria);
        if (charges == null) {
            log.debug("Creating new charges");
            charges = EntityFactory.create(Charges.class);
            charges.application().set(PtAppContext.getCurrentUserApplication());
        }

        ChargesServerCalculation.updateChargesFromApplication(charges);

        loadTransientData(charges);

        callback.onSuccess(charges);
    }

    @Override
    public void save(AsyncCallback<Charges> callback, Charges charges) {
        log.debug("Saving charges\n{}", VistaDataPrinter.print(charges));

        saveApplicationEntity(charges);

        loadTransientData(charges);

        callback.onSuccess(charges);
    }

    // If adding new data here sync with @see SummaryServicesImpl#retrieveSummary 
    @SuppressWarnings("unchecked")
    private void loadTransientData(Charges charges) {
        for (TenantCharge charge : charges.paymentSplitCharges().charges()) {
            PotentialTenantInfo tenant = PersistenceServicesFactory.getPersistenceService()
                    .retrieve(PotentialTenantInfo.class, charge.tenant().getPrimaryKey());
            charge.tenantFullName().setValue(
                    EntityFromatUtils.nvl_concat(" ", tenant.person().name().firstName(), tenant.person().name().middleName(), tenant.person().name()
                            .lastName()));
        }
    }
}
