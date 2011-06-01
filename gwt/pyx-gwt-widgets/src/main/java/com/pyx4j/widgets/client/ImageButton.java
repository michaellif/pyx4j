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

public class ImageButton extends Image {

    private String title = null;

    private ImageResource imageRegular = null;

    private ImageResource imageHover = null;

    private ImageResource imageDown = null;

    public ImageButton(ImageResource imageRegular) {
        this(imageRegular, null, null, null);
    }

    public ImageButton(ImageResource imageRegular, String title) {
        this(imageRegular, null, null, title);
    }

    public ImageButton(ImageResource imageRegular, ImageResource imageHover) {
        this(imageRegular, imageHover, null, null);
    }

    public ImageButton(ImageResource imageRegular, ImageResource imageHover, String title) {
        this(imageRegular, imageHover, null, title);
    }

    public ImageButton(ImageResource imageRegular, ImageResource imageHover, ImageResource imageDown, String title) {
        this.imageRegular = imageRegular;
        this.imageHover = imageHover;
        this.imageDown = imageDown;
        this.title = title;
        create();
    }

    public void create() {

        setResource(imageRegular);
        getElement().getStyle().setCursor(Cursor.POINTER);
        setTitle(title);

        if (imageHover != null) {
            addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    setResource(imageHover);
                }
            });
            addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    setResource(imageRegular);
                }
            });
        }
        if (imageDown != null) {
            addMouseDownHandler(new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    setResource(imageDown);
                }
            });
        }
        if (imageHover != null || imageDown != null) {
            addMouseUpHandler(new MouseUpHandler() {
                @Override
                public void onMouseUp(MouseUpEvent event) {
                    setResource(imageRegular);
                }
            });
        }
    }
}
