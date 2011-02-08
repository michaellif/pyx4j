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
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

import com.pyx4j.widgets.client.style.Theme;

public class ViewModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(SiteView.class);

        bind(LogoView.class).to(LogoViewImpl.class).in(Singleton.class);
        bind(FooterView.class).to(FooterViewImpl.class).in(Singleton.class);
        bind(MainNavigView.class).to(MainNavigViewImpl.class).in(Singleton.class);
        bind(MainContentView.class).to(MainContentViewImpl.class).in(Singleton.class);
        bind(LeftPortletsView.class).to(LeftPortletsViewImpl.class).in(Singleton.class);
        bind(SignInView.class).to(SignInViewImpl.class).in(Singleton.class);
        bind(TopRightActionsView.class).to(TopRightActionsViewImpl.class).in(Singleton.class);

        bind(Theme.class).to(DefaultTheme.class).in(Singleton.class);

    }

}
