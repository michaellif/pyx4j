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
package com.propertyvista.crm.client.activity.crud.settings.website.content;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.settings.website.content.pages.PageEditor;
import com.propertyvista.crm.client.ui.crud.settings.website.general.AvailableLocaleSelectorDialog;
import com.propertyvista.crm.client.ui.crud.viewfactories.WebsiteViewFactory;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.PageDescriptor.Type;
import com.propertyvista.domain.site.SiteDescriptor;

public class PageEditorActivity extends CrmEditorActivity<PageDescriptor> implements PageEditor.Presenter {

    private PageParent pageParentArg = null;

    public PageEditorActivity(CrudAppPlace place) {
        super(place, WebsiteViewFactory.instance(PageEditor.class), GWT.<PageDescriptorCrudService> create(PageDescriptorCrudService.class),
                PageDescriptor.class);

        String val = place.getFirstArg(PageEditor.Presenter.URL_PARAM_PAGE_PARENT);
        if (val != null) {
            pageParentArg = PageParent.valueOf(val);
        }
    }

    @Override
    protected void createNewEntity(final AsyncCallback<PageDescriptor> callback) {

        if (pageParentArg == null) {
            throw new Error("Incorrect parentClass argument");
        }

        final PageDescriptor entity = EntityFactory.create(PageDescriptor.class);
        entity.type().setValue(Type.staticContent);

        switch (pageParentArg) {
        case page:
            entity.parent().set(EntityFactory.create(PageDescriptor.class));
            break;
        case site:
            entity.parent().set(EntityFactory.create(SiteDescriptor.class));
            break;
        }

        new AvailableLocaleSelectorDialog(null, new ValueChangeHandler<AvailableLocale>() {
            @Override
            public void onValueChange(ValueChangeEvent<AvailableLocale> event) {
                PageContent content = EntityFactory.create(PageContent.class);
                content.locale().set(event.getValue());
                entity.content().add(content);
                callback.onSuccess(entity);
            }
        }) {
            @Override
            public boolean onClickCancel() {
                cancel();
                return true;
            }
        }.show();
    }
}
