/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 27, 2011
 * @author vlads
 */
package com.propertyvista.crm.server.services.profile;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.Context;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.crm.rpc.CrmUserVisit;
import com.propertyvista.crm.rpc.services.profile.CrmUserDeliveryPreferencesCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.preferences.CrmUserDeliveryPreferences;
import com.propertyvista.domain.preferences.CrmUserDeliveryPreferences.DeliveryType;
import com.propertyvista.domain.preferences.CrmUserPreferences;
import com.propertyvista.dto.CrmUserDeliveryPreferencesDTO;

public class CrmUserDeliveryPreferencesCrudServiceImpl implements CrmUserDeliveryPreferencesCrudService {

    private class CrmUserDeliveryPreferencesBinder extends CrudEntityBinder<CrmUserDeliveryPreferences, CrmUserDeliveryPreferencesDTO> {

        CrmUserDeliveryPreferencesBinder() {
            super(CrmUserDeliveryPreferences.class, CrmUserDeliveryPreferencesDTO.class);
        }

        @Override
        protected void bind() {
            bindCompleteObject();
        }
    };

    @Override
    public void init(AsyncCallback<CrmUserDeliveryPreferencesDTO> callback, com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void retrieve(AsyncCallback<CrmUserDeliveryPreferencesDTO> callback, Key entityId,
            com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        CrmUserDeliveryPreferences cdpBO = Persistence.secureRetrieve(EntityQueryCriteria.create(CrmUserDeliveryPreferences.class));

        CrmUserDeliveryPreferencesDTO cdpTO = null;
        if (cdpBO == null) {
            cdpTO = EntityFactory.create(CrmUserDeliveryPreferencesDTO.class);
            cdpTO.promotionalDelivery().setValue(DeliveryType.Individual);
            cdpTO.informationalDelivery().setValue(DeliveryType.Individual);
            createSave(cdpTO);
        } else {
            cdpTO = new CrmUserDeliveryPreferencesBinder().createTO(cdpBO);
        }

        callback.onSuccess(cdpTO);
    }

    @Override
    public void create(AsyncCallback<Key> callback, CrmUserDeliveryPreferencesDTO editableEntity) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void save(AsyncCallback<Key> callback, CrmUserDeliveryPreferencesDTO editableEntity) {
        if (editableEntity != null) {

            CrmUserDeliveryPreferences cdpBO = createSave(editableEntity);
            callback.onSuccess(cdpBO.getPrimaryKey());
        }
    }

    private CrmUserDeliveryPreferences createSave(CrmUserDeliveryPreferencesDTO editableEntity) {
        CrmUserPreferences cp = Context.userPreferences(CrmUserPreferences.class);
        if (cp == null || cp.getPrimaryKey() == null) {
            cp = EntityFactory.create(CrmUserPreferences.class);
        }
        cp.crmUser().set(CrmAppContext.getCurrentUser());

        CrmUserDeliveryPreferences cdpBO = new CrmUserDeliveryPreferencesBinder().createBO(editableEntity);
        cp.deliveryPreferences().set(cdpBO);
        Persistence.secureSave(cp);
        Persistence.service().commit();

        editableEntity.id().set(cdpBO.id());
        ServerContext.visit(CrmUserVisit.class).setPreferences(cp);
        return cdpBO;
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<CrmUserDeliveryPreferencesDTO>> callback, EntityListCriteria<CrmUserDeliveryPreferencesDTO> criteria) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new Error("Invalid Operation");
    }

}
