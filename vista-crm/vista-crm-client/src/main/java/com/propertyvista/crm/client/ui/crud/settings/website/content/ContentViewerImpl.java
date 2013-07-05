/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 5, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.website.content;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.commons.Key;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmCrudAppPlace;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Website;
import com.propertyvista.dto.SiteDescriptorDTO;

public class ContentViewerImpl extends CrmViewerViewImplBase<SiteDescriptorDTO> implements ContentViewer {

    private static final I18n i18n = I18n.get(ContentViewerImpl.class);

    public ContentViewerImpl() {
        setForm(new ContentForm(this));

        // Add actions:
        addAction(new MenuItem(i18n.tr("Add Child Page"), new Command() {
            @Override
            public void execute() {
                Key valueKey = getForm().getValue().getPrimaryKey();
                if (valueKey != null) { // shouldn't be new unsaved value!..
                    newChild(valueKey);
                }
            }
        }));
        addAction(new MenuItem(i18n.tr("Add City Page"), new Command() {
            @Override
            public void execute() {
                Key parentId = getForm().getValue().getPrimaryKey();
                if (parentId != null) {
                    ((ContentViewer.Presenter) getPresenter()).editNew(parentId, Website.Content.CityIntroPage.class);
                }
            }
        }));
    }

    @Override
    public void viewChild(Key id) {
        ((ContentViewer.Presenter) getPresenter()).viewChild(id);
    }

    @Override
    public void viewChild(Key id, Class<? extends CrmCrudAppPlace> openPlaceClass) {
        ((ContentViewer.Presenter) getPresenter()).viewChild(id, openPlaceClass);
    }

    @Override
    public void newChild(Key parentid) {
        ((ContentViewer.Presenter) getPresenter()).editNew(parentid);
    }
}