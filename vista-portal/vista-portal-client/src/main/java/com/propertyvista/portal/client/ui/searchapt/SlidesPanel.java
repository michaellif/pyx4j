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

import com.pyx4j.entity.shared.IList;
import com.pyx4j.widgets.client.photoalbum.Slideshow;

import com.propertyvista.portal.domain.dto.MediaDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;

public class SlidesPanel extends SimplePanel {

    private static I18n i18n = I18nFactory.getI18n(SlidesPanel.class);

    private final Slideshow banner;

    public SlidesPanel() {

        getElement().getStyle().setMarginBottom(10, Unit.PX);

        VerticalPanel contentPanel = new VerticalPanel();
        setWidget(contentPanel);

        banner = new Slideshow(300, 200, -1, false);
        banner.setSlideChangeSpeed(3000);

        contentPanel.add(banner);

    }

    public void populate(PropertyDetailsDTO property) {

        banner.removeAllItems();

        IList<MediaDTO> mediaList = property.media();
        for (MediaDTO photo : mediaList) {
            AbsolutePanel infoBunner = new AbsolutePanel();
            Image infoBunnerImage = new Image("media/" + photo.id().getValue().toString() + "/large.jpg");
            infoBunner.add(infoBunnerImage, 0, 0);
            banner.addItem(infoBunner);
        }

        banner.init();
        banner.start();

    }

}
