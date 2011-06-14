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
package com.propertyvista.admin.client.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.admin.client.activity.ShortCutsActivity;
import com.propertyvista.admin.client.ui.AccountView;
import com.propertyvista.admin.client.ui.AccountViewImpl;
import com.propertyvista.admin.client.ui.AlertView;
import com.propertyvista.admin.client.ui.AlertViewImpl;
import com.propertyvista.admin.client.ui.FooterView;
import com.propertyvista.admin.client.ui.FooterViewImpl;
import com.propertyvista.admin.client.ui.LogoView;
import com.propertyvista.admin.client.ui.LogoViewImpl;
import com.propertyvista.admin.client.ui.MessageView;
import com.propertyvista.admin.client.ui.MessageViewImpl;
import com.propertyvista.admin.client.ui.SettingsView;
import com.propertyvista.admin.client.ui.SettingsViewImpl;
import com.propertyvista.admin.client.ui.ShortCutsViewImpl;
import com.propertyvista.admin.client.ui.TopRightActionsView;
import com.propertyvista.admin.client.ui.TopRightActionsViewImpl;
import com.propertyvista.common.client.viewfactories.ViewFactoryBase;

public class AdminVeiwFactory extends ViewFactoryBase {

    public static IsWidget instance(Class<?> type) {
        if (!map.containsKey(type)) {
            if (com.propertyvista.admin.client.ui.NavigView.class.equals(type)) {
                map.put(type, new com.propertyvista.admin.client.ui.NavigViewImpl());
            } else if (ShortCutsActivity.class.equals(type)) {
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
            } else if (AccountView.class.equals(type)) {
                map.put(type, new AccountViewImpl());
            }
        }
        return map.get(type);
    }
}
