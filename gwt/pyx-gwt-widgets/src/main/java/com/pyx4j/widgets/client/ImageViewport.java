/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Oct 8, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.gwt.shared.Dimension;

public class ImageViewport extends LayoutPanel {

    public enum ScaleMode {
        None, ScaleToFill, ScaleToFit;
    }

    private final Dimension dimension;

    private final ScaleMode scaleMode;

    public ImageViewport(Dimension dimension, ScaleMode scaleMode) {
        this.dimension = dimension;
        this.scaleMode = scaleMode;
        setPixelSize(dimension.width, dimension.height);
        getElement().getStyle().setOverflow(Overflow.HIDDEN);
    }

    public void setImage(final Image img) {
        if (img == null) {
            return;
        }

        clear();
        add(img);
        img.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                adoptImage(img);
            }
        });
        img.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached() && img.getWidth() > 0) {
                    adoptImage(img);
                }
            }
        });

        // if widget has already been attached and image is loaded, call adopt() right away
        if (isAttached() && img.getWidth() > 0) {
            adoptImage(img);
        }
    }

    private static void adoptImage(Image img) {
        Widget parent = img.getParent();
        if (parent instanceof ImageViewport) {
            ((ImageViewport) parent).scale(img);
        }
    }

    private void scale(Image image) {
        if (image == null) {
            return;
        }

        float frameRatio = (float) dimension.width / dimension.height;
        float imageRatio = (float) image.getWidth() / image.getHeight();
        switch (scaleMode) {
        case ScaleToFill:
            if (imageRatio >= frameRatio) {
                fitVertically(image);
            } else {
                fitHorizontally(image);
            }
            break;
        case ScaleToFit:
            if (imageRatio >= frameRatio) {
                fitHorizontally(image);
            } else {
                fitVertically(image);
            }
            break;
        default:
            // no-op
        }
    }

    private void fitHorizontally(Image image) {
        // calculate top-bottom offset
        int ver = (dimension.height - image.getHeight() * dimension.width / image.getWidth()) / 2;
        setWidgetTopBottom(image, ver, Unit.PX, ver, Unit.PX);
        // scale to fill container's width
        image.getElement().getStyle().setProperty("width", "100%");
        image.getElement().getStyle().setProperty("backgroundSize", "100% auto");
    }

    private void fitVertically(Image image) {
        // calculate left-right offset
        int hor = (dimension.width - image.getWidth() * dimension.height / image.getHeight()) / 2;
        setWidgetLeftRight(image, hor, Unit.PX, hor, Unit.PX);
        // scale to fill container's height
        image.getElement().getStyle().setProperty("height", "100%");
        image.getElement().getStyle().setProperty("backgroundSize", "auto 100%");
    }
}
