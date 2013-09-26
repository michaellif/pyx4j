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

import com.propertyvista.common.client.ui.components.login.LoginView;
import com.propertyvista.common.client.ui.components.login.LoginWithTokenView;
import com.propertyvista.common.client.ui.components.login.LoginWithTokenViewImpl;
import com.propertyvista.common.client.ui.components.login.PasswordResetRequestView;
import com.propertyvista.common.client.ui.components.login.PasswordResetRequestViewImpl;
import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.common.client.ui.components.security.PasswordChangeViewImpl;
import com.propertyvista.common.client.ui.components.security.PasswordResetView;
import com.propertyvista.common.client.ui.components.security.PasswordResetViewImpl;
import com.propertyvista.common.client.viewfactories.ViewFactoryBase;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.ptapp.client.ui.ApplicationSelectionView;
import com.propertyvista.portal.ptapp.client.ui.ApplicationSelectionViewImpl;
import com.propertyvista.portal.ptapp.client.ui.ApplicationStatusView;
import com.propertyvista.portal.ptapp.client.ui.ApplicationStatusViewImpl;
import com.propertyvista.portal.ptapp.client.ui.CaptionView;
import com.propertyvista.portal.ptapp.client.ui.CaptionViewImpl;
import com.propertyvista.portal.ptapp.client.ui.FooterView;
import com.propertyvista.portal.ptapp.client.ui.FooterViewImpl;
import com.propertyvista.portal.ptapp.client.ui.NotificationView;
import com.propertyvista.portal.ptapp.client.ui.NotificationViewImpl;
import com.propertyvista.portal.ptapp.client.ui.LoginViewImpl;
import com.propertyvista.portal.ptapp.client.ui.LogoView;
import com.propertyvista.portal.ptapp.client.ui.LogoViewImpl;
import com.propertyvista.portal.ptapp.client.ui.MainNavigView;
import com.propertyvista.portal.ptapp.client.ui.MainNavigViewImpl;
import com.propertyvista.portal.ptapp.client.ui.SecondNavigView;
import com.propertyvista.portal.ptapp.client.ui.SecondNavigViewImpl;
import com.propertyvista.portal.ptapp.client.ui.StaticContentView;
import com.propertyvista.portal.ptapp.client.ui.StaticContentViewImpl;
import com.propertyvista.portal.ptapp.client.ui.TopRightActionsView;
import com.propertyvista.portal.ptapp.client.ui.TopRightActionsViewImpl;
import com.propertyvista.portal.ptapp.client.ui.UserMessageView;
import com.propertyvista.portal.ptapp.client.ui.UserMessageViewImpl;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.welcome.WelcomeView;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.welcome.WelcomeViewImpl;

public class PtAppViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (CaptionView.class.equals(type)) {
                map.put(type, new CaptionViewImpl());
            } else if (TopRightActionsView.class.equals(type)) {
                map.put(type, new TopRightActionsViewImpl());
            } else if (LogoView.class.equals(type)) {
                map.put(type, new LogoViewImpl());
            } else if (NotificationView.class.equals(type)) {
                map.put(type, new NotificationViewImpl());
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
                if (VistaTODO.enableWelcomeWizardDemoMode) {
                    map.put(type, new WelcomeViewImpl());
                } else {
                    map.put(type, new LoginViewImpl());
                }
            } else if (LoginWithTokenView.class.equals(type)) {
                map.put(type, new LoginWithTokenViewImpl());

            } else if (ApplicationSelectionView.class.equals(type)) {
                map.put(type, new ApplicationSelectionViewImpl());

            } else if (PasswordResetRequestView.class.equals(type)) {
                map.put(type, new PasswordResetRequestViewImpl());

            } else if (PasswordResetView.class.equals(type)) {
                map.put(type, new PasswordResetViewImpl());

            } else if (ApplicationStatusView.class.equals(type)) {
                map.put(type, new ApplicationStatusViewImpl());

            } else if (PasswordChangeView.class.equals(type)) {
                map.put(type, new PasswordChangeViewImpl());

            }

            if (VistaTODO.enableWelcomeWizardDemoMode) {
                if (WelcomeView.class.equals(type)) {
                    map.put(type, new WelcomeViewImpl());
                }
            }

        }

        @SuppressWarnings("unchecked")
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }

    public static void clear() {
        map.clear();
    }
}
