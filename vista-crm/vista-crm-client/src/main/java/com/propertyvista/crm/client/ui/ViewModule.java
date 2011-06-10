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

import com.propertyvista.crm.client.ui.login.NewPasswordView;
import com.propertyvista.crm.client.ui.login.NewPasswordViewImpl;

public class ViewModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(CrmView.class);

        bind(LogoView.class).to(LogoViewImpl.class).in(Singleton.class);
        bind(FooterView.class).to(FooterViewImpl.class).in(Singleton.class);
        bind(TopRightActionsView.class).to(TopRightActionsViewImpl.class).in(Singleton.class);

        bind(NewPasswordView.class).to(NewPasswordViewImpl.class).in(Singleton.class);

        bind(ShortCutsView.class).to(ShortCutsViewImpl.class).in(Singleton.class);
    }
}
