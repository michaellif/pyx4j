/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.ria.client.app.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface AppImages extends ClientBundle {

    @Source("folder-minimize.png")
    ImageResource minimizeFolder();

    @Source("view-menu.png")
    ImageResource viewMenu();

    @Source("debug_on.gif")
    ImageResource debugOn();

    @Source("header_background.png")
    ImageResource headerBackground();

}
