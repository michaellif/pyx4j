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
 */
package com.propertyvista.portal.shared.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.RootPane;
import com.pyx4j.site.client.frontoffice.ui.layout.FrontOfficeLayoutPanel;

public class PortalRootPane extends RootPane<FrontOfficeLayoutPanel> {

    public PortalRootPane(String extra1Caption, String extra2Caption, String extra4Caption) {
        super(new FrontOfficeLayoutPanel(extra1Caption, extra2Caption, extra4Caption));

    }

    @Override
    protected void onPlaceChange(Place place) {
        final int originalSchrollPosition = asWidget().getScrollPosition();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                asWidget().scrollToTop(originalSchrollPosition);
            }
        });

    }
}
