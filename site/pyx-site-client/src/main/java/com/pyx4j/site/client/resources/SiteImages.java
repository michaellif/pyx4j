/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on Feb 12, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

import com.pyx4j.widgets.client.images.WidgetsImages;

public interface SiteImages extends WidgetsImages {

    SiteImages INSTANCE = GWT.create(SiteImages.class);

    @Source("close.gif")
    ImageResource closeButton();

    @Source("close-dev-console.png")
    ImageResource closeDevConsoleButton();

    @Source("open-dev-console.png")
    ImageResource openDevConsoleButton();

    //=============== Devices ====================

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Huge.gif")
    ImageResource huge();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Monitor.gif")
    ImageResource monitor();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("TabletL.gif")
    ImageResource tabletL();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Tablet.gif")
    ImageResource tablet();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("PhoneL.gif")
    ImageResource phoneL();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Phone.gif")
    ImageResource phone();
}
