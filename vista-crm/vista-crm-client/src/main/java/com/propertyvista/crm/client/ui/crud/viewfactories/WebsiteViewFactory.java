/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-05
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.crm.client.ui.crud.settings.website.branding.BrandingEditor;
import com.propertyvista.crm.client.ui.crud.settings.website.branding.BrandingEditorImpl;
import com.propertyvista.crm.client.ui.crud.settings.website.branding.BrandingViewer;
import com.propertyvista.crm.client.ui.crud.settings.website.branding.BrandingViewerImpl;
import com.propertyvista.crm.client.ui.crud.settings.website.content.ContentEditor;
import com.propertyvista.crm.client.ui.crud.settings.website.content.ContentEditorImpl;
import com.propertyvista.crm.client.ui.crud.settings.website.content.ContentViewer;
import com.propertyvista.crm.client.ui.crud.settings.website.content.ContentViewerImpl;
import com.propertyvista.crm.client.ui.crud.settings.website.content.cityintro.CityIntroPageEditor;
import com.propertyvista.crm.client.ui.crud.settings.website.content.cityintro.CityIntroPageEditorImpl;
import com.propertyvista.crm.client.ui.crud.settings.website.content.cityintro.CityIntroPageViewer;
import com.propertyvista.crm.client.ui.crud.settings.website.content.cityintro.CityIntroPageViewerImpl;
import com.propertyvista.crm.client.ui.crud.settings.website.content.gadgets.HomePageGadgetEditor;
import com.propertyvista.crm.client.ui.crud.settings.website.content.gadgets.HomePageGadgetEditorImpl;
import com.propertyvista.crm.client.ui.crud.settings.website.content.gadgets.HomePageGadgetViewer;
import com.propertyvista.crm.client.ui.crud.settings.website.content.gadgets.HomePageGadgetViewerImpl;
import com.propertyvista.crm.client.ui.crud.settings.website.content.pages.PageEditor;
import com.propertyvista.crm.client.ui.crud.settings.website.content.pages.PageEditorImpl;
import com.propertyvista.crm.client.ui.crud.settings.website.content.pages.PageViewer;
import com.propertyvista.crm.client.ui.crud.settings.website.content.pages.PageViewerImpl;
import com.propertyvista.crm.client.ui.crud.settings.website.general.GeneralEditor;
import com.propertyvista.crm.client.ui.crud.settings.website.general.GeneralEditorImpl;
import com.propertyvista.crm.client.ui.crud.settings.website.general.GeneralViewer;
import com.propertyvista.crm.client.ui.crud.settings.website.general.GeneralViewerImpl;

public class WebsiteViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IPane> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (GeneralViewer.class.equals(type)) {
                map.put(type, new GeneralViewerImpl());
            } else if (GeneralEditor.class.equals(type)) {
                map.put(type, new GeneralEditorImpl());

            } else if (ContentViewer.class.equals(type)) {
                map.put(type, new ContentViewerImpl());
            } else if (ContentEditor.class.equals(type)) {
                map.put(type, new ContentEditorImpl());

            } else if (BrandingViewer.class.equals(type)) {
                map.put(type, new BrandingViewerImpl());
            } else if (BrandingEditor.class.equals(type)) {
                map.put(type, new BrandingEditorImpl());

            } else if (PageViewer.class.equals(type)) {
                map.put(type, new PageViewerImpl());
            } else if (PageEditor.class.equals(type)) {
                map.put(type, new PageEditorImpl());

            } else if (HomePageGadgetViewer.class.equals(type)) {
                map.put(type, new HomePageGadgetViewerImpl());
            } else if (HomePageGadgetEditor.class.equals(type)) {
                map.put(type, new HomePageGadgetEditorImpl());

            } else if (CityIntroPageViewer.class.equals(type)) {
                map.put(type, new CityIntroPageViewerImpl());
            } else if (CityIntroPageEditor.class.equals(type)) {
                map.put(type, new CityIntroPageEditorImpl());
            }
        }

        @SuppressWarnings("unchecked")
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }
}
