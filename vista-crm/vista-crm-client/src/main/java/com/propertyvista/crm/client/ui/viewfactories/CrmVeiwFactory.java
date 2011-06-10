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

import com.propertyvista.crm.client.ui.AccountView;
import com.propertyvista.crm.client.ui.AccountViewImpl;
import com.propertyvista.crm.client.ui.AlertView;
import com.propertyvista.crm.client.ui.AlertViewImpl;
import com.propertyvista.crm.client.ui.FooterView;
import com.propertyvista.crm.client.ui.FooterViewImpl;
import com.propertyvista.crm.client.ui.LogoView;
import com.propertyvista.crm.client.ui.LogoViewImpl;
import com.propertyvista.crm.client.ui.MessageView;
import com.propertyvista.crm.client.ui.MessageViewImpl;
import com.propertyvista.crm.client.ui.NavigView;
import com.propertyvista.crm.client.ui.NavigViewImpl;
import com.propertyvista.crm.client.ui.ShortCutsView;
import com.propertyvista.crm.client.ui.ShortCutsViewImpl;
import com.propertyvista.crm.client.ui.TopRightActionsView;
import com.propertyvista.crm.client.ui.TopRightActionsViewImpl;

public class CrmVeiwFactory extends ViewFactoryBase {

    public static IsWidget instance(Class<?> type) {
        if (!map.containsKey(type)) {
            if (NavigView.class.equals(type)) {
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

            } else if (AccountView.class.equals(type)) {
                map.put(type, new AccountViewImpl());
            }
        }
        return map.get(type);
    }
}
