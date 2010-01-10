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
 * Created on May 14, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.tabpanel;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

public class BasicTab implements ITab {

    private final Widget contentPane;

    private final ImageResource imageResource;

    private final String title;

    public BasicTab(Widget contentPane, String title, ImageResource imageResource) {
        this.contentPane = contentPane;
        this.imageResource = imageResource;
        this.title = title;
    }

    @Override
    public Widget getContentPane() {
        return contentPane;
    }

    @Override
    public ImageResource getImageResource() {
        return imageResource;
    }

    @Override
    public String getTitle() {
        return title;
    }

}
