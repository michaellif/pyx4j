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

import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.settings.content.ThemeEditor;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.SiteDescriptorCrudService;
import com.propertyvista.domain.site.ContentDescriptor.Lang;
import com.propertyvista.domain.site.SiteDescriptor;

public class ThemeEditorActivity extends EditorActivityBase<SiteDescriptor> implements ThemeEditor.Presenter {

    @SuppressWarnings("unchecked")
    public ThemeEditorActivity(Place place) {
        super((ThemeEditor) SettingsViewFactory.instance(ThemeEditor.class), (AbstractCrudService<SiteDescriptor>) GWT.create(SiteDescriptorCrudService.class),
                SiteDescriptor.class);
        withPlace(place);
    }

    @Override
    protected void initNewItem(SiteDescriptor entity) {

    }

    private Lang getLangByPlace() {

        Lang lang = Lang.english;

        if (placeClass.getEnclosingClass().equals(CrmSiteMap.Settings.English.class)) {
            lang = Lang.english;
        } else if (placeClass.getEnclosingClass().equals(CrmSiteMap.Settings.French.class)) {
            lang = Lang.french;
        } else if (placeClass.getEnclosingClass().equals(CrmSiteMap.Settings.Spanish.class)) {
            lang = Lang.spanish;
        }
        return lang;
    }
}
