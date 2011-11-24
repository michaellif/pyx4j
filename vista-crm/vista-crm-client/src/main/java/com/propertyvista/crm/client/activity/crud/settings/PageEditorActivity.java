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
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.settings.content.page.PageEditor;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.PageDescriptor.Type;
import com.propertyvista.domain.site.SiteDescriptor;

public class PageEditorActivity extends EditorActivityBase<PageDescriptor> implements PageEditor.Presenter {

    private PageParent pageParentArg = null;

    @SuppressWarnings("unchecked")
    public PageEditorActivity(Place place) {
        super(place, SettingsViewFactory.instance(PageEditor.class), (AbstractCrudService<PageDescriptor>) GWT.create(PageDescriptorCrudService.class),
                PageDescriptor.class);

        String val = ((CrudAppPlace) place).getFirstArg(PageEditor.Presenter.URL_PARAM_PAGE_PARENT);
        if (val != null) {
            pageParentArg = PageParent.valueOf(val);
        }
    }

    @Override
    protected void createNewEntity(AsyncCallback<PageDescriptor> callback) {

        PageDescriptor entity = EntityFactory.create(entityClass);

        if (pageParentArg == null) {
            throw new Error("Incorrect parentClass argument");
        }
        switch (pageParentArg) {
        case page:
            entity.parent().set(EntityFactory.create(PageDescriptor.class));
            break;
        case site:
            entity.parent().set(EntityFactory.create(SiteDescriptor.class));
            break;
        }
        entity.type().setValue(Type.staticContent);

        callback.onSuccess(entity);
    }
}
