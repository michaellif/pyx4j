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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.settings.content.PageEditor;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.PageDescriptor.Type;

public class PageEditorActivity extends EditorActivityBase<PageDescriptor> implements PageEditor.Presenter {

    @SuppressWarnings("unchecked")
    public PageEditorActivity(Place place) {
        super((PageEditor) SettingsViewFactory.instance(PageEditor.class), (AbstractCrudService<PageDescriptor>) GWT.create(PageDescriptorCrudService.class),
                PageDescriptor.class);
        setPlace(place);
    }

    @Override
    protected void createNewEntity(AsyncCallback<PageDescriptor> callback) {
        PageDescriptor entity = EntityFactory.create(entityClass);
        entity.type().setValue(Type.staticContent);
        super.createNewEntity(callback);
    }
}
