/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.tenant.lead;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.tenant.lead.ShowingCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Showing;

public class ShowingCrudServiceImpl extends GenericCrudServiceImpl<Showing> implements ShowingCrudService {

    public ShowingCrudServiceImpl() {
        super(Showing.class);
    }

    @Override
    protected void enhanceRetrieved(Showing entity, boolean fromList) {
        if (!entity.unit().isNull()) {
            Persistence.service().retrieve(entity.unit().belongsTo());
            entity.building().set(entity.unit().belongsTo());
        }
    }

    @Override
    public void updateValue(AsyncCallback<AptUnit> callback, Key unitId) {
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitId);
        Persistence.service().retrieve(unit.belongsTo());
        callback.onSuccess(unit);
    }
}
