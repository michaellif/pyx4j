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

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.domain.util.VistaDataPrinter;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class ApartmentServiceImpl extends ApplicationEntityServiceImpl implements ApartmentService {

    private final static Logger log = LoggerFactory.getLogger(ApartmentServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<UnitSelection> callback, Key tenantId) {
        EntityQueryCriteria<UnitSelection> criteria = EntityQueryCriteria.create(UnitSelection.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lease(), PtAppContext.getCurrentLease()));
        UnitSelection unitSelection = secureRetrieve(criteria);
        if (unitSelection == null) {
            log.debug("Creating new unit selection");
            unitSelection = EntityFactory.create(UnitSelection.class);
        } else {
            //            log.info("Loaded existing unit selection {}", unitSelection);
        }

        callback.onSuccess(unitSelection);
    }

    @Override
    public void save(AsyncCallback<UnitSelection> callback, UnitSelection unitSelection) {
        log.debug("Saving unit selection\n{}", VistaDataPrinter.print(unitSelection));

        saveApplicationEntity(unitSelection);

        callback.onSuccess(unitSelection);
    }

    @Override
    public void retrieveUnit(AsyncCallback<AptUnit> callback, Key unitId) {
        AptUnit unit = PersistenceServicesFactory.getPersistenceService().retrieve(AptUnit.class, unitId);
        callback.onSuccess(unit);
    }

    public boolean isUnitExist(Key unitId) {
        AptUnit unit = PersistenceServicesFactory.getPersistenceService().retrieve(AptUnit.class, unitId);
        if (unit != null) {
            log.debug("Unit {} is found", unit.getStringView());
        } else {
            log.debug("Unit is not found");
        }
        return unit != null;
    }

}
