/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 21, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client.app;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;

public class ThreeFoldersMainPanel extends SimplePanel {

    private final FolderSectionPanel leftFolder;

    private final FolderSectionPanel topFolder;

    private final FolderSectionPanel bottomFolder;

    public ThreeFoldersMainPanel() {
        setSize("100%", "100%");

        HorizontalSplitPanel horizSplit = new HorizontalSplitPanel();
        setHorizontalSplitCursor(horizSplit.getElement());
        removeScrollBars(horizSplit.getElement());
        leftFolder = new FolderSectionPanel();
        horizSplit.setLeftWidget(leftFolder);

        VerticalSplitPanel vertSplit = new VerticalSplitPanel();
        setVerticalSplitCursor(vertSplit.getElement());
        removeScrollBars(vertSplit.getElement());

        vertSplit.setSize("100%", "100%");
        topFolder = new FolderSectionPanel();
        vertSplit.setTopWidget(topFolder);
        bottomFolder = new FolderSectionPanel();
        vertSplit.setBottomWidget(bottomFolder);
        vertSplit.setSplitPosition("70%");

        horizSplit.setRightWidget(vertSplit);
        horizSplit.setSplitPosition("20%");

        setWidget(horizSplit);

    }

    /**
     * SplitPanel fix that hides scroll
     * 
     * @param splitPanelElement
     */
    private static void removeScrollBars(Element splitPanelElement) {
        Element head = Element.as(splitPanelElement.getChildNodes().getItem(0).getChildNodes().getItem(0));
        head.getStyle().setProperty("overflow", "hidden");
        Element tail = Element.as(splitPanelElement.getChildNodes().getItem(0).getChildNodes().getItem(2));
        tail.getStyle().setProperty("overflow", "hidden");
    }

    private static void setHorizontalSplitCursor(Element splitPanelElement) {
        Element divider = Element.as(splitPanelElement.getChildNodes().getItem(0).getChildNodes().getItem(1));
        divider.getStyle().setProperty("cursor", "col-resize");
    }

    private static void setVerticalSplitCursor(Element splitPanelElement) {
        Element divider = Element.as(splitPanelElement.getChildNodes().getItem(0).getChildNodes().getItem(1));
        divider.getStyle().setProperty("cursor", "row-resize");
    }

    public FolderSectionPanel getLeftFolder() {
        return leftFolder;
    }

    public FolderSectionPanel getTopFolder() {
        return topFolder;
    }

    public FolderSectionPanel getBottomFolder() {
        return bottomFolder;
    }

}
