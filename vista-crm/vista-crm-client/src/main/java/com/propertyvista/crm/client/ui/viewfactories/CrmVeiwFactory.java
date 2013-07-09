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
package com.propertyvista.crm.client.ui.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.viewfactories.ViewFactoryBase;
import com.propertyvista.crm.client.ui.FooterView;
import com.propertyvista.crm.client.ui.FooterViewImpl;
import com.propertyvista.crm.client.ui.HeaderView;
import com.propertyvista.crm.client.ui.HeaderViewImpl;
import com.propertyvista.crm.client.ui.MessageView;
import com.propertyvista.crm.client.ui.MessageViewImpl;
import com.propertyvista.crm.client.ui.NavigView;
import com.propertyvista.crm.client.ui.NavigViewImpl;
import com.propertyvista.crm.client.ui.NotificationsView;
import com.propertyvista.crm.client.ui.NotificationsViewImpl;
import com.propertyvista.crm.client.ui.RuntimeErrorView;
import com.propertyvista.crm.client.ui.RuntimeErrorViewImpl;
import com.propertyvista.crm.client.ui.ShortCutsView;
import com.propertyvista.crm.client.ui.ShortCutsViewImpl;

public class CrmVeiwFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (NavigView.class.equals(type)) {
                map.put(type, new NavigViewImpl());

            } else if (ShortCutsView.class.equals(type)) {
                map.put(type, new ShortCutsViewImpl());

            } else if (HeaderView.class.equals(type)) {
                map.put(type, new HeaderViewImpl());

            } else if (FooterView.class.equals(type)) {
                map.put(type, new FooterViewImpl());

            } else if (NotificationsView.class.equals(type)) {
                map.put(type, new NotificationsViewImpl());

            } else if (MessageView.class.equals(type)) {
                map.put(type, new MessageViewImpl());

            } else if (RuntimeErrorView.class.equals(type)) {
                map.put(type, new RuntimeErrorViewImpl());
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
