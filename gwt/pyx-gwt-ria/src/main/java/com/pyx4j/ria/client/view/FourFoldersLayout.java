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
package com.pyx4j.ria.client.view;

import java.util.HashMap;

import com.google.gwt.user.client.ui.SplitLayoutPanel;

import com.pyx4j.ria.client.FolderSectionPanel;

public class FourFoldersLayout extends SplitLayoutPanel implements ILayoutManager<FourFoldersPosition> {

    private final HashMap<FourFoldersPosition, FolderSectionPanel> folders;

    public FourFoldersLayout() {
        super();

        folders = new HashMap<FourFoldersPosition, FolderSectionPanel>();

        SplitLayoutPanel westPanel = new SplitLayoutPanel();
        addWest(westPanel, 350);

        {
            FolderSectionPanel folder = new FolderSectionPanel();
            westPanel.addSouth(folder, 350);
            folders.put(FourFoldersPosition.sw, folder);
        }

        {
            FolderSectionPanel folder = new FolderSectionPanel();
            westPanel.add(folder);
            folders.put(FourFoldersPosition.nw, folder);
        }

        {
            FolderSectionPanel folder = new FolderSectionPanel();
            addSouth(folder, 350);
            folders.put(FourFoldersPosition.south, folder);
        }

        {
            FolderSectionPanel folder = new FolderSectionPanel();
            add(folder);
            folders.put(FourFoldersPosition.center, folder);
        }

    }

    @Override
    public FolderSectionPanel getFolder(FourFoldersPosition position) {
        return folders.get(position);
    }

}
