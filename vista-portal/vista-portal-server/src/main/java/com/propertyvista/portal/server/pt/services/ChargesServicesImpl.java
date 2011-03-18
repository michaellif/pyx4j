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
package com.propertyvista.portal.server.pt.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.util.PrintUtil;
import com.propertyvista.portal.rpc.pt.services.ChargesServices;
import com.propertyvista.portal.server.pt.ChargesServerCalculation;
import com.propertyvista.portal.server.pt.PtUserDataAccess;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

public class ChargesServicesImpl extends ApplicationEntityServicesImpl implements ChargesServices {
    private final static Logger log = LoggerFactory.getLogger(ChargesServicesImpl.class);

    @Override
    public void retrieve(AsyncCallback<Charges> callback, Long tenantId) {
        log.info("Retrieving charges for tenant {}", tenantId);
        EntityQueryCriteria<Charges> criteria = EntityQueryCriteria.create(Charges.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
        Charges charges = secureRetrieve(criteria);
        if (charges == null) {
            log.info("Creating new charges");
            charges = EntityFactory.create(Charges.class);
            charges.application().set(PtUserDataAccess.getCurrentUserApplication());
        }

        ChargesServerCalculation.updateChargesFromApplication(charges);

        //        loadTransientData(ret);

        callback.onSuccess(charges);
    }

    @Override
    public void save(AsyncCallback<Charges> callback, Charges charges) {
        log.info("Saving charges\n{}", PrintUtil.print(charges));

        applyApplication(charges);
        secureSave(charges);

        //        loadTransientData(editableEntity);

        callback.onSuccess(charges);
    }
}
