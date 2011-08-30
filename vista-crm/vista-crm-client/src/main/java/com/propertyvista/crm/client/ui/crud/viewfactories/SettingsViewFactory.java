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

import com.propertyvista.crm.client.ui.crud.settings.content.ContentEditor;
import com.propertyvista.crm.client.ui.crud.settings.content.ContentEditorImpl;
import com.propertyvista.crm.client.ui.crud.settings.content.ContentViewer;
import com.propertyvista.crm.client.ui.crud.settings.content.ContentViewerImpl;
import com.propertyvista.crm.client.ui.crud.settings.content.ThemeEditor;
import com.propertyvista.crm.client.ui.crud.settings.content.ThemeEditorImpl;
import com.propertyvista.crm.client.ui.crud.settings.content.ThemeViewer;
import com.propertyvista.crm.client.ui.crud.settings.content.ThemeViewerImpl;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceDictionaryView;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceDictionaryViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeEditorView;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeViewerView;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeViewerViewImpl;

public class SettingsViewFactory extends ViewFactoryBase {

    public static IView<? extends IEntity> instance(Class<? extends IView<? extends IEntity>> type) {
        if (!map.containsKey(type)) {
            if (ContentViewer.class.equals(type)) {
                map.put(type, new ContentViewerImpl());
            } else if (ContentEditor.class.equals(type)) {
                map.put(type, new ContentEditorImpl());

            } else if (ThemeViewer.class.equals(type)) {
                map.put(type, new ThemeViewerImpl());
            } else if (ThemeEditor.class.equals(type)) {
                map.put(type, new ThemeEditorImpl());

            } else if (ServiceDictionaryView.class.equals(type)) {
                map.put(type, new ServiceDictionaryViewImpl());

            } else if (ServiceTypeViewerView.class.equals(type)) {
                map.put(type, new ServiceTypeViewerViewImpl());
            } else if (ServiceTypeEditorView.class.equals(type)) {
                map.put(type, new ServiceTypeEditorViewImpl());
            }
        }
        return map.get(type);
    }
}
