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
package com.propertyvista.crm.client.activity.crud.administration.website.content;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.administration.website.content.pages.PageEditor;
import com.propertyvista.crm.client.ui.crud.administration.website.general.AvailableLocaleSelectorDialog;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService.PageDescriptorInitializationData;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.PageDescriptor;

public class PageEditorActivity extends CrmEditorActivity<PageDescriptor> implements PageEditor.Presenter {

    private PageDescriptorInitializationData.PageParent pageParentArg = null;

    public PageEditorActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(PageEditor.class), GWT.<PageDescriptorCrudService> create(PageDescriptorCrudService.class),
                PageDescriptor.class);

        String val = place.getFirstArg(PageEditor.Presenter.URL_PARAM_PAGE_PARENT);
        if (val != null) {
            pageParentArg = PageDescriptorInitializationData.PageParent.valueOf(val);
        }
    }

    @Override
    protected void obtainInitializationData(final AsyncCallback<InitializationData> callback) {
        new AvailableLocaleSelectorDialog(null) {
            @Override
            public boolean onClickOk() {
                AvailableLocale locale = getSelectedLocale();
                if (locale != null) {
                    PageDescriptorCrudService.PageDescriptorInitializationData id = EntityFactory
                            .create(PageDescriptorCrudService.PageDescriptorInitializationData.class);
                    id.pageParent().setValue(pageParentArg);
                    id.pageLocale().set(locale);
                    callback.onSuccess(id);
                }
                return true;
            }

            @Override
            public boolean onClickCancel() {
                cancel();
                return true;
            }
        }.show();
    }
}
