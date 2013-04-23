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
import com.pyx4j.site.client.ui.layout.RiaLayoutPanel;
import com.pyx4j.site.shared.meta.PublicPlace;

import com.propertyvista.operations.client.mvp.ContentActivityMapper;
import com.propertyvista.operations.client.mvp.FooterActivityMapper;
import com.propertyvista.operations.client.mvp.HeaderActivityMapper;
import com.propertyvista.operations.client.mvp.NavigActivityMapper;
import com.propertyvista.operations.client.mvp.ShortCutsActivityMapper;
import com.propertyvista.operations.rpc.OperationsSiteMap;

public class OperationsRootPane extends RootPane<RiaLayoutPanel> implements IsWidget {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public OperationsRootPane() {
        super(new RiaLayoutPanel());
        bind(new HeaderActivityMapper(), asWidget().getHeaderDisplay());
        bind(new FooterActivityMapper(), asWidget().getFooterDisplay());
        bind(new NavigActivityMapper(), asWidget().getNavigDisplay());
        bind(new ShortCutsActivityMapper(), asWidget().getShortcutsDisplay());
        bind(new ContentActivityMapper(), asWidget().getContentDisplay());

    }

    @Override
    protected void onPlaceChange(Place place) {
        if (place instanceof PublicPlace || place instanceof OperationsSiteMap.PasswordReset) {
            asWidget().setMenuVisible(false);
        } else {
            asWidget().setMenuVisible(true);
        }
        asWidget().forceLayout();
    }
}
