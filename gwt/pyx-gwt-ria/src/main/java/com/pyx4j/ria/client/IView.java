/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.menu.PopupMenuBar;
import com.pyx4j.widgets.client.tabpanel.ITab;

public interface IView extends ITab {

    Widget getToolbarPane();

    Widget getFooterPane();

    ViewMemento getViewMemento();

    void setFolder(FolderSectionPanel folderSectionPanel);

    FolderSectionPanel getFolder();

    PopupMenuBar getMenu();

}
