/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.field.client.ui;

import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.RootPane;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel;

import com.propertyvista.field.client.mvp.ContentActivityMapper;
import com.propertyvista.field.client.mvp.FooterActivityMapper;
import com.propertyvista.field.client.mvp.HeaderActivityMapper;
import com.propertyvista.field.client.mvp.MenuActivityMapper;
import com.propertyvista.field.client.mvp.StickyHeaderActivityMapper;

public class FieldRootPane extends RootPane<ResponsiveLayoutPanel> {

    public FieldRootPane() {
        super(new ResponsiveLayoutPanel());

        bind(new HeaderActivityMapper(), asWidget().getHeaderDisplay());
        bind(new StickyHeaderActivityMapper(), asWidget().getStickyHeaderDisplay());
        bind(new ContentActivityMapper(), asWidget().getContentDisplay());
        bind(new MenuActivityMapper(), asWidget().getMenuDisplay());
        bind(new FooterActivityMapper(), asWidget().getFooterDisplay());

    }

    @Override
    protected void onPlaceChange(Place place) {

    }

}
