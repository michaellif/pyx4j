/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 15, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.gwt.commons.ClientEventBus;

import com.propertyvista.portal.shared.events.PointerEvent;
import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.ui.PointerId;

public class PointerLink extends HTML {

    public PointerLink(String text, final Command command, final PointerId pointerId) {
        super(text);
        setStyleName(DashboardTheme.StyleName.PointerLink.name());

        addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                command.execute();
            }
        });

        final Timer timer = new Timer() {

            @Override
            public void run() {
                ClientEventBus.fireEvent(new PointerEvent(pointerId));
            }

        };

        addMouseOverHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(MouseOverEvent event) {
                timer.schedule(300);

            }
        });

        addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                timer.cancel();
            }
        });

    }
}