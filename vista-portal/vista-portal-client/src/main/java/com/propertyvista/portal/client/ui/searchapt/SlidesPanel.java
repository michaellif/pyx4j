/*
 * Curious Kids
 * Copyright (C) 2008-2010 curiouskids.ca.
 *
 * Created on Apr 12, 2010
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.searchapt;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.photoalbum.Slideshow;

import com.propertyvista.portal.client.resources.PortalImages;

public class SlidesPanel extends SimplePanel {

    private static I18n i18n = I18nFactory.getI18n(SlidesPanel.class);

    public SlidesPanel() {

        getElement().getStyle().setMarginBottom(10, Unit.PX);

        VerticalPanel contentPanel = new VerticalPanel();
        setWidget(contentPanel);

        Slideshow banner = new Slideshow(300, 234);
        banner.setSlideChangeSpeed(15000);

        {
            AbsolutePanel infoBunner = new AbsolutePanel();
            Image infoBunnerImage = new Image();
            infoBunner.add(infoBunnerImage, 0, 0);
            infoBunnerImage.setResource(PortalImages.INSTANCE.building2());
            banner.addItem(infoBunner);
        }

        {
            AbsolutePanel infoBunner = new AbsolutePanel();
            Image infoBunnerImage = new Image();
            infoBunner.add(infoBunnerImage, 0, 0);
            infoBunnerImage.setResource(PortalImages.INSTANCE.building3());
            banner.addItem(infoBunner);
        }

        {
            AbsolutePanel infoBunner = new AbsolutePanel();
            Image infoBunnerImage = new Image();
            infoBunner.add(infoBunnerImage, 0, 0);
            infoBunnerImage.setResource(PortalImages.INSTANCE.building4());
            banner.addItem(infoBunner);
        }

        {
            AbsolutePanel infoBunner = new AbsolutePanel();
            Image infoBunnerImage = new Image();
            infoBunner.add(infoBunnerImage, 0, 0);
            infoBunnerImage.setResource(PortalImages.INSTANCE.building5());
            banner.addItem(infoBunner);
        }

        contentPanel.add(banner);

        final Timer prefetchTimer = new Timer() {

            @Override
            public void run() {
                Image.prefetch(PortalImages.INSTANCE.building2().getURL());
                Image.prefetch(PortalImages.INSTANCE.building3().getURL());
                Image.prefetch(PortalImages.INSTANCE.building4().getURL());
                Image.prefetch(PortalImages.INSTANCE.building5().getURL());
            }
        };

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                prefetchTimer.schedule(4000);
            }
        });
    }

}
