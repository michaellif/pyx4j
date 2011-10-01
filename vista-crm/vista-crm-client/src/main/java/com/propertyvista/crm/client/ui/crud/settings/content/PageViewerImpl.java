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

import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.site.PageDescriptor;

public class PageViewerImpl extends CrmViewerViewImplBase<PageDescriptor> implements PageViewer {
    public PageViewerImpl() {
        super(CrmSiteMap.Settings.Page.class);
        // create/init/set main form here: 
        CrmEntityForm<PageDescriptor> form = new PageEditorForm(this, new CrmViewersComponentFactory());
        form.initContent();
        setForm(form);
    }

    @Override
    public void viewChild(Key id) {
        ((PageViewer.Presenter) getPresenter()).viewChild(id);
    }

    @Override
    public void newChild(Key parentid) {
        ((PageViewer.Presenter) getPresenter()).editNew(parentid);
    }
}