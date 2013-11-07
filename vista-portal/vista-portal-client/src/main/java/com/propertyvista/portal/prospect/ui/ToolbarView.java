/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-30
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui;

import java.util.List;

import com.pyx4j.site.client.IsView;

import com.propertyvista.shared.i18n.CompiledLocale;

public interface ToolbarView extends IsView {

    void setPresenter(ToolbarPresenter presenter);

    interface ToolbarPresenter {

        void logout();

        void login();

        void setLocale(CompiledLocale locale);
    }

    void onLogedOut(boolean hideLoginButton);

    void onLogedIn(String userName);

    void setAvailableLocales(List<CompiledLocale> locales);

}
