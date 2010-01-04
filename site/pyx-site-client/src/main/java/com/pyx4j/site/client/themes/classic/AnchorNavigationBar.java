/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 3, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.site.client.themes.classic;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Anchor;

public class AnchorNavigationBar extends BaseNavigationBar {

    public AnchorNavigationBar() {
        super();
    }

    public void add(final Anchor anchor) {
        anchor.getElement().getStyle().setProperty("margin", "6px");
        anchor.getElement().getStyle().setColor("green");
        anchor.getElement().getStyle().setBackgroundImage(ImageFactory.getImages().divider().getURL());
        anchor.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
        anchor.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                // TODO Auto-generated method stub
                anchor.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
            }
        });
        anchor.addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                anchor.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
            }
        });

        anchor.getElement().getStyle().setCursor(Cursor.POINTER);

        super.add(anchor);
    }

}
