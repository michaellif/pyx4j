/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.tabpanel.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface TabpanelImages extends ClientBundle {

    @Source("closeTab.gif")
    ImageResource closeTab();

    @Source("closeTabFocused.gif")
    ImageResource closeTabFocused();

    @Source("tabbar-left.png")
    ImageResource moveTabbarLeft();

    @Source("tabbar-right.png")
    ImageResource moveTabbarRight();

    @Source("tabbar-empty.png")
    ImageResource moveTabbarEmpty();
}
