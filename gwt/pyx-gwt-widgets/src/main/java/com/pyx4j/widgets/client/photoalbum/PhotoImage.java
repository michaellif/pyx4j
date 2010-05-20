/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on May 20, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.photoalbum;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

public class PhotoImage extends Image {

    private final int maxWidth;

    private final int maxHeight;

    public PhotoImage(String url, int maxWidth, int maxHeight) {
        super();
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        setUrl(url);
        addHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                resize();
            }
        }, LoadEvent.getType());
    }

    private void resize() {
        double ratio = (double) getHeight() / getWidth();
        double maxAR = ((double) maxHeight) / ((double) maxWidth);
        if (ratio > maxAR) {
            setHeight(maxHeight + "px");
            setWidth(((int) Math.round(maxHeight / ratio)) + "px");
        } else {
            setWidth(maxWidth + "px");
            setHeight(((int) Math.round(maxWidth * ratio)) + "px");
        }
    }

}
