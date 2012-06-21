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
package com.propertyvista.crm.server.services.customer.lead;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;

import com.propertyvista.crm.rpc.services.customer.lead.ShowingCrudService;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Showing;

public class ShowingCrudServiceImpl extends AbstractCrudServiceImpl<Showing> implements ShowingCrudService {

    public ShowingCrudServiceImpl() {
        super(Showing.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Showing entity, Showing dto) {
        if (!dto.unit().isNull()) {
            Persistence.service().retrieve(dto.unit().building(), AttachLevel.ToStringMembers);
            dto.building().set(dto.unit().building());
        }
    }

    @Override
    protected void enhanceListRetrieved(Showing entity, Showing dto) {
        enhanceRetrieved(entity, dto);
    }

    @Override
    public void updateValue(AsyncCallback<AptUnit> callback, Key unitId) {
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitId);
        Persistence.service().retrieve(unit.building());
        callback.onSuccess(unit);
    }
}
