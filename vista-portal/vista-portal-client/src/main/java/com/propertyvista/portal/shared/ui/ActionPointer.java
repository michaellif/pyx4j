/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.events.PointerEvent;
import com.propertyvista.portal.shared.events.PointerHandler;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class ActionPointer extends SimplePanel {

    public enum Direction {
        left, top
    }

    public ActionPointer(final PointerId pointerId, Direction direction) {
        super();

        Image image = null;
        switch (direction) {
        case left:
            setWidget(image = new Image(PortalImages.INSTANCE.pointerH()));
            setStyleName(PortalRootPaneTheme.StyleName.LeftPointer.name());
            break;
        case top:
            setWidget(image = new Image(PortalImages.INSTANCE.pointerV()));
            setStyleName(PortalRootPaneTheme.StyleName.TopPointer.name());
            break;
        }
        image.getElement().getStyle().setOpacity(0.6);

        setVisible(false);

        PortalSite.getEventBus().addHandler(PointerEvent.getType(), new PointerHandler() {

            Timer timer;

            @Override
            public void showPointer(PointerEvent event) {

                if (event.getPointerId().equals(pointerId)) {
                    ActionPointer.this.setVisible(true);
                    timer = new Timer() {
                        @Override
                        public void run() {
                            ActionPointer.this.setVisible(false);
                        }
                    };
                    timer.schedule(4000);
                } else {
                    ActionPointer.this.setVisible(false);
                    if (timer != null) {
                        timer.cancel();
                    }
                }
            }
        });
    }

    @Override
    public void setVisible(boolean visible) {
        ActionPointer.this.setStyleDependentName(PortalRootPaneTheme.StyleDependent.animationPaused.name(), !visible);
        super.setVisible(visible);
    }
}
