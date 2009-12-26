/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 23, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client.app;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.ria.client.ImageFactory;
import com.pyx4j.widgets.client.util.BrowserType;

public class HeaderPanel extends TransparentDeckPanel {

    private static final String HEADER_HEIGHT = "25px";

    public HeaderPanel(String text) {

        String imageURL = new Image(ImageFactory.getImages().minimizeFolder()).getUrl();

        HTML holder = new HTML();
        holder.setHTML("<img src=" + imageURL + " style='width:100%; height:" + HEADER_HEIGHT + "' alt=''>");

        ensureDebugId("HeaderPanel");

        add(holder);

        Label label = new Label(text, false);
        DOM.setStyleAttribute(label.getElement(), "color", "white");
        DOM.setStyleAttribute(label.getElement(), "fontFamily", "Arial");
        DOM.setStyleAttribute(label.getElement(), "fontWeight", "bold");

        HorizontalPanel panel = new HorizontalPanel();
        panel.setSpacing(0);
        //TODO
        //        Image logoImage = ApplicationManager.getAppImages().headerLogoImage().createImage();
        //        DOM.setStyleAttribute(logoImage.getElement(), "margin", "0 6 0 6");
        //        DOM.setStyleAttribute(logoImage.getElement(), "border", "0");
        //
        //        panel.add(logoImage);
        //        panel.setCellVerticalAlignment(logoImage, HasVerticalAlignment.ALIGN_MIDDLE);
        panel.add(label);
        panel.setCellHorizontalAlignment(label, HasHorizontalAlignment.ALIGN_LEFT);
        panel.setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);
        panel.setCellWidth(label, "100%");
        add(panel);

        if (BrowserType.isIE()) {
            DOM.setStyleAttribute(holder.getElement(), "position", "absolute");
            DOM.setStyleAttribute(holder.getElement(), "zIndex", "-1");
            DOM.setStyleAttribute(panel.getElement(), "position", "static");
        } else {
            DOM.setStyleAttribute(holder.getElement(), "position", "fixed");
            DOM.setStyleAttribute(holder.getElement(), "height", HEADER_HEIGHT);
            DOM.setStyleAttribute(panel.getElement(), "position", "relative");
            DOM.setStyleAttribute(panel.getElement(), "zIndex", "1");
        }

        setHeight(HEADER_HEIGHT);
        setWidth("100%");
    }

}
