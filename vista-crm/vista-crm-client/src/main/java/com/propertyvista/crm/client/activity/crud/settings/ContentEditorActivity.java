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

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.settings.content.ContentEditor;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.domain.site.Locale.Lang;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.PageDescriptor.Type;

public class ContentEditorActivity extends EditorActivityBase<PageDescriptor> implements ContentEditor.Presenter {

    @SuppressWarnings("unchecked")
    public ContentEditorActivity(Place place) {
        super((ContentEditor) SettingsViewFactory.instance(ContentEditor.class), (AbstractCrudService<PageDescriptor>) GWT
                .create(PageDescriptorCrudService.class), PageDescriptor.class);
        withPlace(place);
    }

    @Override
    protected void initNewItem(PageDescriptor entity) {
        entity.type().setValue(Type.staticContent);

        if (placeClass.equals(CrmSiteMap.Settings.English.Content.class)) {
            entity.lang().setValue(Lang.english);
        } else if (placeClass.equals(CrmSiteMap.Settings.French.Content.class)) {
            entity.lang().setValue(Lang.french);
        } else if (placeClass.equals(CrmSiteMap.Settings.Spanish.Content.class)) {
            entity.lang().setValue(Lang.spanish);
        } else {
            entity.lang().setValue(Lang.english);
        }
    }

    @Override
    public void deleteChildPage(PageDescriptor page) {
        ((PageDescriptorCrudService) service).deleteChildPage(new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        }, page);
    }

    @Override
    public CrudAppPlace getPlace() {
        return AppSite.getHistoryMapper().createPlace(placeClass);
    }
}
