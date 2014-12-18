/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Sep 9, 2014
 * @author michaellif
 */
package com.pyx4j.site.client.ui.sidemenu;

import com.google.gwt.user.client.ui.Image;

import com.pyx4j.security.shared.Permission;
import com.pyx4j.site.client.resources.SiteImages;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.images.ButtonImages;

public class SideMenuFolderItem extends SideMenuItem {

    private SideMenuList submenu;

    private Image expantionHandler;

    private boolean expanded;

    public SideMenuFolderItem(SideMenuList submenu, String caption, ButtonImages images, Permission... permission) {
        super(null, caption, images, permission);
        assert submenu != null;

        this.submenu = submenu;
        getContentPanel().add(submenu);
        submenu.setParent(this);

        expantionHandler = new Image(SiteImages.INSTANCE.expand());
        expantionHandler.setStyleName(SideMenuTheme.StyleName.SideMenuExpantionHandler.name());
        getItemPanel().add(expantionHandler);

        setCommand(new SideMenuCommand() {
            @Override
            public boolean execute() {
                triggerExpanded();
                return false;
            }
        });

        setExpanded(false);
    }

    @Override
    protected void setIndentation(int indentation) {
        super.setIndentation(indentation);

        if (submenu != null) {
            submenu.setIndentation(indentation + 1);
        }
    }

    public SideMenuItem getSelectedLeaf() {
        if (submenu == null) {
            return this;
        } else {
            return submenu.getSelectedLeaf();
        }
    }

    public void setExpanded(boolean expanded) {
        if (submenu != null) {
            submenu.setVisible(expanded);
            this.expanded = expanded;

            if (expanded) {
                expantionHandler.setResource(SiteImages.INSTANCE.collapse());
            } else {
                expantionHandler.setResource(SiteImages.INSTANCE.expand());
            }

        }
    }

    public void triggerExpanded() {
        setExpanded(!expanded);
    }

    @Override
    public void setSelected(boolean select) {
        super.setSelected(select);
        if (select) {
            setExpanded(true);
        }
    }

    @Override
    public void select(AppPlace appPlace) {
        if (submenu != null) {
            submenu.select(appPlace);
        }
    }
}
