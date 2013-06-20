/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.field.client.ui.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.viewfactories.ViewFactoryBase;
import com.propertyvista.field.client.ui.RuntimeErrorView;
import com.propertyvista.field.client.ui.RuntimeErrorViewImpl;
import com.propertyvista.field.client.ui.appselection.ApplicationSelectionView;
import com.propertyvista.field.client.ui.appselection.ApplicationSelectionViewImpl;
import com.propertyvista.field.client.ui.components.alerts.AlertDetailsView;
import com.propertyvista.field.client.ui.components.alerts.AlertDetailsViewImpl;
import com.propertyvista.field.client.ui.components.alerts.AlertsInfoView;
import com.propertyvista.field.client.ui.components.alerts.AlertsInfoViewImpl;
import com.propertyvista.field.client.ui.components.alerts.AlertsScreenView;
import com.propertyvista.field.client.ui.components.alerts.AlertsScreenViewImpl;
import com.propertyvista.field.client.ui.components.footer.FooterView;
import com.propertyvista.field.client.ui.components.footer.FooterViewImpl;
import com.propertyvista.field.client.ui.components.header.HeaderView;
import com.propertyvista.field.client.ui.components.header.HeaderViewImpl;
import com.propertyvista.field.client.ui.components.header.ToolbarView;
import com.propertyvista.field.client.ui.components.header.ToolbarViewImpl;
import com.propertyvista.field.client.ui.components.menu.MenuView;
import com.propertyvista.field.client.ui.components.menu.MenuViewImpl;
import com.propertyvista.field.client.ui.components.search.SearchResultsView;
import com.propertyvista.field.client.ui.components.search.SearchResultsViewImpl;

public class FieldViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (RuntimeErrorView.class.equals(type)) {
                map.put(type, new RuntimeErrorViewImpl());
            } else if (ApplicationSelectionView.class.equals(type)) {
                map.put(type, new ApplicationSelectionViewImpl());
            } else if (ToolbarView.class.equals(type)) {
                map.put(type, new ToolbarViewImpl());
            } else if (HeaderView.class.equals(type)) {
                map.put(type, new HeaderViewImpl());
            } else if (FooterView.class.equals(type)) {
                map.put(type, new FooterViewImpl());
            } else if (SearchResultsView.class.equals(type)) {
                map.put(type, new SearchResultsViewImpl());
            } else if (MenuView.class.equals(type)) {
                map.put(type, new MenuViewImpl());
            } else if (AlertsScreenView.class.equals(type)) {
                map.put(type, new AlertsScreenViewImpl());
            } else if (AlertsInfoView.class.equals(type)) {
                map.put(type, new AlertsInfoViewImpl());
            } else if (AlertDetailsView.class.equals(type)) {
                map.put(type, new AlertDetailsViewImpl());
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
