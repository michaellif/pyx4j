/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
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

    @Source("header_background.png")
    ImageResource headerBackground();

    @Source("empty3x3.png")
    ImageResource horizontalSplitPanelThumb();

    @Source("empty3x3.png")
    ImageResource verticalSplitPanelThumb();

    @Source("empty1x1.png")
    ImageResource empty();

}
