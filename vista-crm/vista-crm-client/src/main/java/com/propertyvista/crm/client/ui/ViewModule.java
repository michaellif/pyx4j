/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.ui;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

import com.pyx4j.widgets.client.style.Theme;

import com.propertyvista.crm.client.themes.GainsboroTheme;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.client.ui.dashboard.DashboardViewImpl;
import com.propertyvista.crm.client.ui.editors.BuildingEditorView;
import com.propertyvista.crm.client.ui.editors.BuildingEditorViewImpl;
import com.propertyvista.crm.client.ui.listers.ArrearsListerView;
import com.propertyvista.crm.client.ui.listers.ArrearsListerViewImpl;
import com.propertyvista.crm.client.ui.listers.BuildingListerView;
import com.propertyvista.crm.client.ui.listers.BuildingListerViewImpl;
import com.propertyvista.crm.client.ui.login.LoginView;
import com.propertyvista.crm.client.ui.login.LoginViewImpl;
import com.propertyvista.crm.client.ui.login.NewPasswordView;
import com.propertyvista.crm.client.ui.login.NewPasswordViewImpl;
import com.propertyvista.crm.client.ui.login.RetrievePasswordView;
import com.propertyvista.crm.client.ui.login.RetrievePasswordViewImpl;
import com.propertyvista.crm.client.ui.report.ReportView;
import com.propertyvista.crm.client.ui.report.ReportViewImpl;

public class ViewModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(CrmView.class);

        bind(LogoView.class).to(LogoViewImpl.class).in(Singleton.class);
        bind(FooterView.class).to(FooterViewImpl.class).in(Singleton.class);
        bind(NavigView.class).to(NavigViewImpl.class).in(Singleton.class);

        bind(TopRightActionsView.class).to(TopRightActionsViewImpl.class).in(Singleton.class);

        bind(LoginView.class).to(LoginViewImpl.class).in(Singleton.class);
        bind(RetrievePasswordView.class).to(RetrievePasswordViewImpl.class).in(Singleton.class);
        bind(NewPasswordView.class).to(NewPasswordViewImpl.class).in(Singleton.class);
// Listers:      
        bind(BuildingListerView.class).to(BuildingListerViewImpl.class).in(Singleton.class);
        bind(ArrearsListerView.class).to(ArrearsListerViewImpl.class).in(Singleton.class);
// Editors:      
        bind(BuildingEditorView.class).to(BuildingEditorViewImpl.class).in(Singleton.class);

        bind(ReportView.class).to(ReportViewImpl.class).in(Singleton.class);
        bind(DashboardView.class).to(DashboardViewImpl.class).in(Singleton.class);

        bind(Theme.class).to(GainsboroTheme.class).in(Singleton.class);
        bind(ShortCutsView.class).to(ShortCutsViewImpl.class).in(Singleton.class);
    }
}
