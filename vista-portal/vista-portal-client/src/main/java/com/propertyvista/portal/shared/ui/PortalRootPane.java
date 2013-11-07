/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.RootPane;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel;

public class PortalRootPane extends RootPane<ResponsiveLayoutPanel> {

    public PortalRootPane() {
        super(new ResponsiveLayoutPanel());

    }

    @Override
    protected void onPlaceChange(Place place) {
        asWidget().scrollToTop();
    }
}
