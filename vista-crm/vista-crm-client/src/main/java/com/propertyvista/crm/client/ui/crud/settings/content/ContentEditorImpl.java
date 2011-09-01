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
package com.propertyvista.crm.client.ui.crud.settings.content;

import com.pyx4j.commons.Key;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.site.ContentDescriptor;

public class ContentEditorImpl extends CrmEditorViewImplBase<ContentDescriptor> implements ContentEditor {
    public ContentEditorImpl() {
        super(CrmSiteMap.Settings.Content.class);
        // create/init/set main form here: 
        CrmEntityForm<ContentDescriptor> form = new ContentEditorForm(this);
        form.initialize();
        setForm(form);
    }

    @Override
    public void viewChild(Key id) {
        ((ContentEditor.Presenter) getPresenter()).viewChild(id);
    }
}