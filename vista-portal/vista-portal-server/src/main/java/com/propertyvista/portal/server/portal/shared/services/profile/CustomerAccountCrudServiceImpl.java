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
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.shared.services.profile;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.Context;

import com.propertyvista.domain.tenant.CustomerDeliveryPreferences;
import com.propertyvista.domain.tenant.CustomerDeliveryPreferences.DeliveryType;
import com.propertyvista.domain.tenant.CustomerPreferences;
import com.propertyvista.portal.rpc.portal.CustomerUserVisit;
import com.propertyvista.portal.rpc.portal.shared.services.profile.CustomerAccountCrudService;
import com.propertyvista.portal.rpc.shared.dto.CustomerAccountDTO;
import com.propertyvista.portal.rpc.shared.dto.CustomerDeliveryPreferencesDTO;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class CustomerAccountCrudServiceImpl implements CustomerAccountCrudService {

    private class CustomerDeliveryPreferencesBinder extends CrudEntityBinder<CustomerDeliveryPreferences, CustomerDeliveryPreferencesDTO> {

        CustomerDeliveryPreferencesBinder() {
            super(CustomerDeliveryPreferences.class, CustomerDeliveryPreferencesDTO.class);
        }

        @Override
        protected void bind() {
            bindCompleteObject();
        }
    };

    @Override
    public void init(AsyncCallback<CustomerAccountDTO> callback, com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void retrieve(AsyncCallback<CustomerAccountDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        CustomerAccountDTO account = EntityFactory.create(CustomerAccountDTO.class);
        CustomerDeliveryPreferences cdpBO = Persistence.secureRetrieve(EntityQueryCriteria.create(CustomerDeliveryPreferences.class));

        CustomerDeliveryPreferencesDTO cdpTO = null;
        if (cdpBO == null) {
            cdpTO = EntityFactory.create(CustomerDeliveryPreferencesDTO.class);
            cdpTO.promotionalDelivery().setValue(DeliveryType.Individual);
            cdpTO.informationalDelivery().setValue(DeliveryType.Individual);
        } else {
            cdpTO = new CustomerDeliveryPreferencesBinder().createTO(cdpBO);
        }
        account.deliveryPreferences().set(cdpTO);

        callback.onSuccess(account);
    }

    @Override
    public void create(AsyncCallback<Key> callback, CustomerAccountDTO editableEntity) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void save(AsyncCallback<Key> callback, CustomerAccountDTO editableEntity) {
        if (editableEntity != null) {

            CustomerPreferences cp = Context.userPreferences(CustomerPreferences.class);
            if (cp == null || cp.getPrimaryKey() == null) {
                cp = EntityFactory.create(CustomerPreferences.class);
            }
            cp.customerUser().set(ResidentPortalContext.getCurrentUser());

            CustomerDeliveryPreferences cdpBO = new CustomerDeliveryPreferencesBinder().createBO(editableEntity.deliveryPreferences());
            cp.deliveryPreferences().set(cdpBO);
            Persistence.secureSave(cp);
            Persistence.service().commit();

            Context.visit(CustomerUserVisit.class).setPreferences(cp);
            callback.onSuccess(cdpBO.getPrimaryKey());
        }
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<CustomerAccountDTO>> callback, EntityListCriteria<CustomerAccountDTO> criteria) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new Error("Invalid Operation");
    }

}
