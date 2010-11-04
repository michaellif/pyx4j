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
 * Created on Apr 21, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;

public class ThreeFoldersMainPanel extends SplitLayoutPanel {

    private final FolderSectionPanel leftFolder;

    private final FolderSectionPanel topFolder;

    private final FolderSectionPanel bottomFolder;

    public ThreeFoldersMainPanel() {
        super();

        leftFolder = new FolderSectionPanel();
        addWest(leftFolder, 220);

        bottomFolder = new FolderSectionPanel();
        addSouth(bottomFolder, 350);

        topFolder = new FolderSectionPanel();
        add(topFolder);

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
