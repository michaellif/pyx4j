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
 * Created on May 15, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client.view;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.ria.client.FolderSectionPanel;
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
