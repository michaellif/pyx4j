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
package com.propertyvista.operations.client.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.viewfactories.ViewFactoryBase;
import com.propertyvista.operations.client.ui.AlertView;
import com.propertyvista.operations.client.ui.AlertViewImpl;
import com.propertyvista.operations.client.ui.FooterView;
import com.propertyvista.operations.client.ui.FooterViewImpl;
import com.propertyvista.operations.client.ui.LogoView;
import com.propertyvista.operations.client.ui.LogoViewImpl;
import com.propertyvista.operations.client.ui.MainDisplayView;
import com.propertyvista.operations.client.ui.MainDisplayViewImpl;
import com.propertyvista.operations.client.ui.MessageView;
import com.propertyvista.operations.client.ui.MessageViewImpl;
import com.propertyvista.operations.client.ui.NavigView;
import com.propertyvista.operations.client.ui.NavigViewImpl;
import com.propertyvista.operations.client.ui.SettingsView;
import com.propertyvista.operations.client.ui.SettingsViewImpl;
import com.propertyvista.operations.client.ui.ShortCutsView;
import com.propertyvista.operations.client.ui.ShortCutsViewImpl;
import com.propertyvista.operations.client.ui.TopRightActionsView;
import com.propertyvista.operations.client.ui.TopRightActionsViewImpl;

public class OperationsVeiwFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {

        if (!map.containsKey(type)) {

            if (MainDisplayView.class.equals(type)) {
                map.put(type, new MainDisplayViewImpl());

            } else if (NavigView.class.equals(type)) {
                map.put(type, new NavigViewImpl());

            } else if (ShortCutsView.class.equals(type)) {
                map.put(type, new ShortCutsViewImpl());

            } else if (TopRightActionsView.class.equals(type)) {
                map.put(type, new TopRightActionsViewImpl());

            } else if (LogoView.class.equals(type)) {
                map.put(type, new LogoViewImpl());

            } else if (FooterView.class.equals(type)) {
                map.put(type, new FooterViewImpl());

            } else if (AlertView.class.equals(type)) {
                map.put(type, new AlertViewImpl());

            } else if (MessageView.class.equals(type)) {
                map.put(type, new MessageViewImpl());

            } else if (SettingsView.class.equals(type)) {
                map.put(type, new SettingsViewImpl());

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
