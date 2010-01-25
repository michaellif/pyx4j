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
 * Created on Apr 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.admin.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.ria.client.AbstractView;
import com.pyx4j.ria.client.ApplicationManager;
import com.pyx4j.site.shared.domain.Portlet;

public class PortletsView extends AbstractView {

    private static Logger log = LoggerFactory.getLogger(SiteMapView.class);

    private final SiteData siteData;

    private final Tree portletsTree;

    public PortletsView(SiteData siteData) {
        super(new VerticalPanel(), "Portlets", ImageFactory.getImages().image());
        this.siteData = siteData;
        VerticalPanel contentPane = (VerticalPanel) getContentPane();
        contentPane.setSpacing(4);

        portletsTree = new Tree();
        portletsTree.addSelectionHandler(new SelectionHandler<TreeItem>() {

            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                String name = event.getSelectedItem().getText();
                log.debug("Open portlet {} editor", name);
                Object item = event.getSelectedItem().getUserObject();
                if (item instanceof Portlet) {
                    ((AdminApplication) ApplicationManager.getCurrentApplication()).editPortlet((Portlet) item);
                } else {
                    log.error("Invalid ObjectClass {}", item);
                }
            }
        });

        contentPane.add(portletsTree);
    }

    @Override
    public Widget getFooterPane() {
        return null;
    }

    @Override
    public MenuBar getMenu() {
        return null;
    }

    @Override
    public Widget getToolbarPane() {
        return null;
    }

    public void update() {
        portletsTree.clear();
        for (Portlet portlet : siteData.getPortlets()) {
            TreeItem item = new TreeItem(portlet.portletId().getValue());
            item.setUserObject(portlet);
            portletsTree.addItem(item);
        }

    }

}
