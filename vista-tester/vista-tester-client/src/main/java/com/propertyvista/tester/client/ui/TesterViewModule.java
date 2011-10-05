/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 14, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.tester.client.ui;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

import com.pyx4j.widgets.client.style.Palette;
import com.pyx4j.widgets.client.style.Theme;

import com.propertyvista.common.client.theme.VistaPalette;
import com.propertyvista.common.client.theme.VistaTheme;

public class TesterViewModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(TesterView.class);
        bind(FooterView.class).to(FooterViewImpl.class).in(Singleton.class);
        bind(HeaderActionView.class).to(HeaderActionViewImpl.class).in(Singleton.class);
        bind(NavigationView.class).to(NavigationViewImpl.class).in(Singleton.class);
        bind(LogView.class).to(LogViewImpl.class).in(Singleton.class);
        bind(TestAreaView.class).to(TestAreaViewImpl.class).in(Singleton.class);
        bind(Theme.class).to(VistaTheme.class).in(Singleton.class);
        bind(Palette.class).to(VistaPalette.class).in(Singleton.class);
    }
}
