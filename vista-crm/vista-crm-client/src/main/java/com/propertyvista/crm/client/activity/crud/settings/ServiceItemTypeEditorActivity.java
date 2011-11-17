/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;

import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.ServiceItemTypeCrudService;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ServiceItemTypeEditorActivity extends EditorActivityBase<ServiceItemType> {

    @SuppressWarnings("unchecked")
    public ServiceItemTypeEditorActivity(Place place) {
        super(place, SettingsViewFactory.instance(ServiceTypeEditorView.class), (AbstractCrudService<ServiceItemType>) GWT
                .create(ServiceItemTypeCrudService.class), ServiceItemType.class);
    }

    @Override
    protected void createNewEntity(AsyncCallback<ServiceItemType> callback) {
        ServiceItemType entity = EntityFactory.create(entityClass);
        entity.type().setValue(ServiceItemType.Type.service);
        callback.onSuccess(entity);
    }
}
