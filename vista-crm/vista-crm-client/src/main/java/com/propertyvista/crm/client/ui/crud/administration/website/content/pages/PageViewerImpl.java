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
package com.propertyvista.crm.client.ui.crud.administration.website.content.pages;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.Key;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.site.PageDescriptor;

public class PageViewerImpl extends CrmViewerViewImplBase<PageDescriptor> implements PageViewer {

    private static final I18n i18n = I18n.get(PageViewerImpl.class);

    public PageViewerImpl() {
        setForm(new PageForm(this));

        // Add actions:
        addHeaderToolbarItem(new Button(i18n.tr("Add Child Page"), new Command() {
            @Override
            public void execute() {
                Key valueKey = getForm().getValue().getPrimaryKey();
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