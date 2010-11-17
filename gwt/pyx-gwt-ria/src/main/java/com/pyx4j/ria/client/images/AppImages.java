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
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.ria.client.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HorizontalSplitPanel.Resources;

public interface AppImages extends ClientBundle, Resources, com.google.gwt.user.client.ui.VerticalSplitPanel.Resources {

    @Source("bricks.png")
    ImageResource headerLogoImage();

    @Source("folder-minimize.png")
    ImageResource minimizeFolder();

    @Source("view-menu.png")
    ImageResource viewMenu();

    @Source("debug_on.gif")
    ImageResource debugOn();

    @Source("empty3x3.png")
    ImageResource horizontalSplitPanelThumb();

    @Source("empty3x3.png")
    ImageResource verticalSplitPanelThumb();

    @Source("empty1x1.png")
    ImageResource empty();

}
