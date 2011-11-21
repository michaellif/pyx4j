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
package com.propertyvista.crm.client.ui.crud.settings.content.page;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.commons.Key;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.site.PageDescriptor;

public class PageViewerImpl extends CrmViewerViewImplBase<PageDescriptor> implements PageViewer {
    public PageViewerImpl() {
        super(CrmSiteMap.Settings.Page.class, new PageEditorForm(new CrmViewersComponentFactory()));

        // Add actions:
        addToolbarItem(new Button(i18n.tr("Add Child Page"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Key valueKey = form.getValue().getPrimaryKey();
                if (valueKey != null) { // shouldn't be new unsaved value!..
                    newChild(valueKey);
                }
            }
        }).asWidget());
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