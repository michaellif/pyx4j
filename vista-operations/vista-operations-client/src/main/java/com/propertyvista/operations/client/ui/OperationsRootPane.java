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
package com.propertyvista.operations.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.RootPane;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;
import com.pyx4j.site.client.ui.layout.backoffice.BackOfficeLayoutPanel;
import com.pyx4j.site.shared.meta.PublicPlace;

import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.operations.client.mvp.ContentActivityMapper;
import com.propertyvista.operations.client.mvp.FooterActivityMapper;
import com.propertyvista.operations.client.mvp.HeaderActivityMapper;
import com.propertyvista.operations.client.mvp.NavigActivityMapper;
import com.propertyvista.operations.client.mvp.ShortCutsActivityMapper;
import com.propertyvista.operations.rpc.OperationsSiteMap;

public class OperationsRootPane extends RootPane<BackOfficeLayoutPanel> implements IsWidget {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public OperationsRootPane() {
        super(new BackOfficeLayoutPanel(null, null));

        asWidget().setHeaderHeight(50);
        asWidget().setStyleName(SiteViewTheme.StyleName.SiteView.name());

        bind(new HeaderActivityMapper(), asWidget().getDisplay(DisplayType.header));
        bind(new FooterActivityMapper(), asWidget().getDisplay(DisplayType.footer));
        bind(new NavigActivityMapper(), asWidget().getDisplay(DisplayType.menu));
        bind(new ShortCutsActivityMapper(), asWidget().getDisplay(DisplayType.extra1));
        bind(new ContentActivityMapper(), asWidget().getDisplay(DisplayType.content));

    }

    @Override
    protected void onPlaceChange(Place place) {
        if (place instanceof PublicPlace || place instanceof OperationsSiteMap.PasswordReset) {
            asWidget().setMenuVisible(false);
        } else {
            asWidget().setMenuVisible(true);
        }
        asWidget().forceLayout(0);
    }
}
