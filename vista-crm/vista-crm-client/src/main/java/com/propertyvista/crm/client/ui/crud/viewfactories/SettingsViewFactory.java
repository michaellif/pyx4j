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
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ProductDictionaryView;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ProductDictionaryViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeEditorView;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeViewerView;
import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.role.CrmRoleEditorView;
import com.propertyvista.crm.client.ui.crud.settings.role.CrmRoleEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.role.CrmRoleListerView;
import com.propertyvista.crm.client.ui.crud.settings.role.CrmRoleListerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.role.CrmRoleViewerView;
import com.propertyvista.crm.client.ui.crud.settings.role.CrmRoleViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.tax.ChargeCodeEditorView;
import com.propertyvista.crm.client.ui.crud.settings.tax.ChargeCodeEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.tax.ChargeCodeListerView;
import com.propertyvista.crm.client.ui.crud.settings.tax.ChargeCodeListerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.tax.ChargeCodeViewerView;
import com.propertyvista.crm.client.ui.crud.settings.tax.ChargeCodeViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.tax.GlCodeEditorView;
import com.propertyvista.crm.client.ui.crud.settings.tax.GlCodeEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.tax.GlCodeListerView;
import com.propertyvista.crm.client.ui.crud.settings.tax.GlCodeListerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.tax.GlCodeViewerView;
import com.propertyvista.crm.client.ui.crud.settings.tax.GlCodeViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.tax.TaxEditorView;
import com.propertyvista.crm.client.ui.crud.settings.tax.TaxEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.tax.TaxListerView;
import com.propertyvista.crm.client.ui.crud.settings.tax.TaxListerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.tax.TaxViewerView;
import com.propertyvista.crm.client.ui.crud.settings.tax.TaxViewerViewImpl;

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

            } else if (ProductDictionaryView.class.equals(type)) {
                map.put(type, new ProductDictionaryViewImpl());

            } else if (ServiceTypeViewerView.class.equals(type)) {
                map.put(type, new ServiceTypeViewerViewImpl());
            } else if (ServiceTypeEditorView.class.equals(type)) {
                map.put(type, new ServiceTypeEditorViewImpl());

            } else if (CrmRoleListerView.class.equals(type)) {
                map.put(type, new CrmRoleListerViewImpl());
            } else if (CrmRoleEditorView.class.equals(type)) {
                map.put(type, new CrmRoleEditorViewImpl());
            } else if (CrmRoleViewerView.class.equals(type)) {
                map.put(type, new CrmRoleViewerViewImpl());

            } else if (TaxListerView.class.equals(type)) {
                map.put(type, new TaxListerViewImpl());
            } else if (TaxEditorView.class.equals(type)) {
                map.put(type, new TaxEditorViewImpl());
            } else if (TaxViewerView.class.equals(type)) {
                map.put(type, new TaxViewerViewImpl());

            } else if (GlCodeListerView.class.equals(type)) {
                map.put(type, new GlCodeListerViewImpl());
            } else if (GlCodeEditorView.class.equals(type)) {
                map.put(type, new GlCodeEditorViewImpl());
            } else if (GlCodeViewerView.class.equals(type)) {
                map.put(type, new GlCodeViewerViewImpl());

            } else if (ChargeCodeListerView.class.equals(type)) {
                map.put(type, new ChargeCodeListerViewImpl());
            } else if (ChargeCodeEditorView.class.equals(type)) {
                map.put(type, new ChargeCodeEditorViewImpl());
            } else if (ChargeCodeViewerView.class.equals(type)) {
                map.put(type, new ChargeCodeViewerViewImpl());
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
