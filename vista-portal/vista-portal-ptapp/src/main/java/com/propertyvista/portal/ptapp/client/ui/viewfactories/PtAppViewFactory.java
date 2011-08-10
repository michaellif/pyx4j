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
package com.propertyvista.portal.ptapp.client.ui.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.viewfactories.ViewFactoryBase;
import com.propertyvista.portal.ptapp.client.ui.CaptionView;
import com.propertyvista.portal.ptapp.client.ui.CaptionViewImpl;
import com.propertyvista.portal.ptapp.client.ui.FooterView;
import com.propertyvista.portal.ptapp.client.ui.FooterViewImpl;
import com.propertyvista.portal.ptapp.client.ui.GenericMessageView;
import com.propertyvista.portal.ptapp.client.ui.GenericMessageViewImpl;
import com.propertyvista.portal.ptapp.client.ui.LoginView;
import com.propertyvista.portal.ptapp.client.ui.LoginViewImpl;
import com.propertyvista.portal.ptapp.client.ui.LogoView;
import com.propertyvista.portal.ptapp.client.ui.LogoViewImpl;
import com.propertyvista.portal.ptapp.client.ui.MainNavigView;
import com.propertyvista.portal.ptapp.client.ui.MainNavigViewImpl;
import com.propertyvista.portal.ptapp.client.ui.NewPasswordView;
import com.propertyvista.portal.ptapp.client.ui.NewPasswordViewImpl;
import com.propertyvista.portal.ptapp.client.ui.RetrievePasswordView;
import com.propertyvista.portal.ptapp.client.ui.RetrievePasswordViewImpl;
import com.propertyvista.portal.ptapp.client.ui.SecondNavigView;
import com.propertyvista.portal.ptapp.client.ui.SecondNavigViewImpl;
import com.propertyvista.portal.ptapp.client.ui.StaticContentView;
import com.propertyvista.portal.ptapp.client.ui.StaticContentViewImpl;
import com.propertyvista.portal.ptapp.client.ui.TopRightActionsView;
import com.propertyvista.portal.ptapp.client.ui.TopRightActionsViewImpl;
import com.propertyvista.portal.ptapp.client.ui.UserMessageView;
import com.propertyvista.portal.ptapp.client.ui.UserMessageViewImpl;

public class PtAppViewFactory extends ViewFactoryBase {

    public static IsWidget instance(Class<?> type) {
        if (!map.containsKey(type)) {
            if (CaptionView.class.equals(type)) {
                map.put(type, new CaptionViewImpl());
            } else if (TopRightActionsView.class.equals(type)) {
                map.put(type, new TopRightActionsViewImpl());
            } else if (LogoView.class.equals(type)) {
                map.put(type, new LogoViewImpl());
            } else if (GenericMessageView.class.equals(type)) {
                map.put(type, new GenericMessageViewImpl());
            } else if (FooterView.class.equals(type)) {
                map.put(type, new FooterViewImpl());
            } else if (StaticContentView.class.equals(type)) {
                map.put(type, new StaticContentViewImpl());
            } else if (UserMessageView.class.equals(type)) {
                map.put(type, new UserMessageViewImpl());

            } else if (MainNavigView.class.equals(type)) {
                map.put(type, new MainNavigViewImpl());
            } else if (SecondNavigView.class.equals(type)) {
                map.put(type, new SecondNavigViewImpl());

            } else if (LoginView.class.equals(type)) {
                map.put(type, new LoginViewImpl());
            } else if (NewPasswordView.class.equals(type)) {
                map.put(type, new NewPasswordViewImpl());
            } else if (RetrievePasswordView.class.equals(type)) {
                map.put(type, new RetrievePasswordViewImpl());

            }
        }
        return map.get(type);
    }
}
