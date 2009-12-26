/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 29, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.demo.client.proving;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HorizontalSplitPanelImages;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.VerticalSplitPanelImages;

public interface AppImages extends ImageBundle, HorizontalSplitPanelImages, VerticalSplitPanelImages {

    @Resource("bricks.png")
    public AbstractImagePrototype headerLogoImage();

    @Resource("empty3x3.png")
    public AbstractImagePrototype horizontalSplitPanelThumb();

    @Resource("empty3x3.png")
    public AbstractImagePrototype verticalSplitPanelThumb();

    @Resource("empty1x1.png")
    public AbstractImagePrototype empty();

    @Resource("closeTab.gif")
    public AbstractImagePrototype closeTab();

    @Resource("closeTabFocused.gif")
    public AbstractImagePrototype closeTabFocused();

    @Resource("tabbar-left.png")
    public AbstractImagePrototype moveTabbarLeft();

    @Resource("tabbar-right.png")
    public AbstractImagePrototype moveTabbarRight();

    @Resource("tabbar-empty.png")
    public AbstractImagePrototype moveTabbarEmpty();

    @Resource("folder-minimize.png")
    public AbstractImagePrototype minimizeFolder();

    @Resource("view-menu.png")
    public AbstractImagePrototype viewMenu();

    //
    public AbstractImagePrototype groupBoxOpen();

    public AbstractImagePrototype groupBoxClose();

    // ---

    @Resource("logConsoleView.gif")
    public AbstractImagePrototype logConsoleView();

    @Resource("clear.gif")
    public AbstractImagePrototype clear();

    @Resource("marker.png")
    public AbstractImagePrototype marker();

    @Resource("debug_on.gif")
    public AbstractImagePrototype debugOn();

    @Resource("treeOpen.gif")
    public AbstractImagePrototype treeOpen();

    @Resource("treeClosed.gif")
    public AbstractImagePrototype treeClosed();

    @Resource("image.png")
    public AbstractImagePrototype image();

}
