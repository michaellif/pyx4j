/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.tabpanel.ITab;

public interface IView extends ITab {

    Widget getToolbarPane();

    Widget getFooterPane();

    ViewMemento getViewMemento();

    void setFolder(FolderSectionPanel folderSectionPanel);

    FolderSectionPanel getFolder();

    MenuBar getMenu();

}
