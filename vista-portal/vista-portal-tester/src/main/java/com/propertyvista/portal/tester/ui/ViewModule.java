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
package com.propertyvista.portal.tester.ui;

import com.google.gwt.inject.client.AbstractGinModule;

import com.pyx4j.widgets.client.style.Theme;

public class ViewModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(SiteView.class);

        bind(GoodbyeView.class).to(GoodbyeViewImpl.class);
        bind(HelloView.class).to(HelloViewImpl.class);
        bind(SayHelloView.class).to(SayHelloViewImpl.class);
        bind(SayGoodbyeView.class).to(SayGoodbyeViewImpl.class);

        bind(LogoView.class).to(LogoViewImpl.class);
        bind(FooterView.class).to(FooterViewImpl.class);
        bind(MainNavigView.class).to(MainNavigViewImpl.class);
        bind(MainContentView.class).to(MainContentViewImpl.class);

        bind(Theme.class).to(DefaultTheme.class);

    }

}
