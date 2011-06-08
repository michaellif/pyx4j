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
package com.propertyvista.crm.client.activity.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.client.activity.editors.EditorActivityBase;
import com.propertyvista.crm.client.ui.settings.ContentEditor;
import com.propertyvista.crm.rpc.services.AbstractCrudService;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.portal.domain.site.PageDescriptor;
import com.propertyvista.portal.domain.site.PageDescriptor.Type;

public class ContentEditorActivity extends EditorActivityBase<PageDescriptor> {

    @Inject
    @SuppressWarnings("unchecked")
    public ContentEditorActivity(ContentEditor view) {
        super(view, (AbstractCrudService<PageDescriptor>) GWT.create(PageDescriptorCrudService.class), PageDescriptor.class);
    }

    @Override
    protected void createNewEntity(AsyncCallback<PageDescriptor> callback) {
        PageDescriptor descriptor = EntityFactory.create(PageDescriptor.class);
        descriptor.type().setValue(Type.staticContent);
        callback.onSuccess(descriptor);
    }

}
