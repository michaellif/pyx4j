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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.photoalbum.Slideshow;

import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;

public class SlidesPanel extends SimplePanel {

    private static I18n i18n = I18nFactory.getI18n(SlidesPanel.class);

    private final Slideshow banner;

    public SlidesPanel() {

        getElement().getStyle().setMarginBottom(10, Unit.PX);

        VerticalPanel contentPanel = new VerticalPanel();
        setWidget(contentPanel);

        banner = new Slideshow(300, 234, -1, false);
        banner.setSlideChangeSpeed(15000);

        contentPanel.add(banner);

    }

    public void populate(PropertyDetailsDTO property) {

        banner.removeAllItems();

        {
            AbsolutePanel infoBunner = new AbsolutePanel();
            Image infoBunnerImage = new Image("media/1/large.jpg");
            infoBunner.add(infoBunnerImage, 0, 0);
            banner.addItem(infoBunner);
        }

        {
            AbsolutePanel infoBunner = new AbsolutePanel();
            Image infoBunnerImage = new Image("media/2/large.jpg");
            infoBunner.add(infoBunnerImage, 0, 0);
            banner.addItem(infoBunner);
        }

        {
            AbsolutePanel infoBunner = new AbsolutePanel();
            Image infoBunnerImage = new Image("media/3/large.jpg");
            infoBunner.add(infoBunnerImage, 0, 0);
            banner.addItem(infoBunner);
        }

        {
            AbsolutePanel infoBunner = new AbsolutePanel();
            Image infoBunnerImage = new Image("media/4/large.jpg");
            infoBunner.add(infoBunnerImage, 0, 0);
            banner.addItem(infoBunner);
        }

        banner.init();
        banner.start();

    }

}
