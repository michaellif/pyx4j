/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-01
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

/*
 *  Image widget which acts as push-button.
 *  Could be supplied with up to three images for various button state.
 *  Also supports title for tool-tip.   
 */
public class ImageButton extends Image {

    private final String title;

    private final ImageResource regular;

    private final ImageResource hover;

    private final ImageResource pushed;

    public ImageButton(ImageResource regular) {
        this(regular, null, null, null);
    }

    public ImageButton(ImageResource regular, String title) {
        this(regular, null, null, title);
    }

    public ImageButton(ImageResource regular, ImageResource hover) {
        this(regular, hover, null, null);
    }

    public ImageButton(ImageResource regular, ImageResource hover, String title) {
        this(regular, hover, null, title);
    }

    public ImageButton(ImageResource regular, ImageResource hover, ImageResource pushed, String title) {
        this.regular = regular;
        this.hover = hover;
        this.pushed = pushed;
        this.title = title;
        create();
    }

    public void create() {
        assert (regular != null);

        setResource(regular);
        setTitle(title);
        getElement().getStyle().setCursor(Cursor.POINTER);

        if (hover != null) {
            addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    setResource(hover);
                }
            });
        }
        if (pushed != null) {
            addMouseDownHandler(new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    setResource(pushed);
                }
            });
        }
        if (hover != null || pushed != null) {
            addMouseUpHandler(new MouseUpHandler() {
                @Override
                public void onMouseUp(MouseUpEvent event) {
                    setResource(hover);
                }
            });
            addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    setResource(regular);
                }
            });
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        setResource(regular);
    }
}
