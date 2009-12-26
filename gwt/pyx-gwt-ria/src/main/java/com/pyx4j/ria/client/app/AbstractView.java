/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on May 15, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client.app;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.tabpanel.BasicTab;

public abstract class AbstractView extends BasicTab implements IView {

    private final ViewMemento viewMemento = new ViewMemento();

    private FolderSectionPanel folder;

    public AbstractView(Widget contentPane, String title, ImageResource imageResource) {
        super(contentPane, title, imageResource);
    }

    public ViewMemento getViewMemento() {
        return viewMemento;
    }

    public void setFolder(FolderSectionPanel folderSectionPanel) {
        this.folder = folderSectionPanel;
    }

    public FolderSectionPanel getFolder() {
        return folder;
    }

}
