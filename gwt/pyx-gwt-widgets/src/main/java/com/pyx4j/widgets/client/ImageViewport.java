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
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.gwt.shared.Dimension;

public class ImageViewport extends LayoutPanel {

    public enum ScaleMode {
        None, Cover, Contain;
    }

    private final ScaleMode scaleMode;

    public ImageViewport(Dimension dimension, ScaleMode scaleMode) {
        setStyleName("TESTTEST");
        setPixelSize(dimension.getWidth(), dimension.getHeight());
        this.scaleMode = scaleMode;
        getElement().getStyle().setOverflow(Overflow.HIDDEN);
    }

    public void setImage(final Image img) {
        if (img == null) {
            return;
        }

        clear();

        img.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                adoptImage(img);
            }
        });

        adoptImage(img);
    }

    private void adoptImage(Image img) {
        getElement().getStyle().setProperty("background", "url('" + img.getUrl() + "') no-repeat center center");
        switch (scaleMode) {
        case None:
            break;
        case Cover:
            getElement().getStyle().setProperty("backgroundSize", "cover");
            break;
        case Contain:
            getElement().getStyle().setProperty("backgroundSize", "contain");
            break;
        }

    }

}
