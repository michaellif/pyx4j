/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.crm.client.ui.crud.settings.content.page.PageEditor;
import com.propertyvista.crm.client.ui.crud.settings.content.page.PageEditorImpl;
import com.propertyvista.crm.client.ui.crud.settings.content.page.PageViewer;
import com.propertyvista.crm.client.ui.crud.settings.content.page.PageViewerImpl;
import com.propertyvista.crm.client.ui.crud.settings.content.site.SiteEditor;
import com.propertyvista.crm.client.ui.crud.settings.content.site.SiteEditorImpl;
import com.propertyvista.crm.client.ui.crud.settings.content.site.SiteViewer;
import com.propertyvista.crm.client.ui.crud.settings.content.site.SiteViewerImpl;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceDictionaryView;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceDictionaryViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeEditorView;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeViewerView;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeViewerViewImpl;

public class SettingsViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IView<E>> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (SiteViewer.class.equals(type)) {
                map.put(type, new SiteViewerImpl());
            } else if (SiteEditor.class.equals(type)) {
                map.put(type, new SiteEditorImpl());

            } else if (PageViewer.class.equals(type)) {
                map.put(type, new PageViewerImpl());
            } else if (PageEditor.class.equals(type)) {
                map.put(type, new PageEditorImpl());

            } else if (ServiceDictionaryView.class.equals(type)) {
                map.put(type, new ServiceDictionaryViewImpl());

            } else if (ServiceTypeViewerView.class.equals(type)) {
                map.put(type, new ServiceTypeViewerViewImpl());
            } else if (ServiceTypeEditorView.class.equals(type)) {
                map.put(type, new ServiceTypeEditorViewImpl());
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
