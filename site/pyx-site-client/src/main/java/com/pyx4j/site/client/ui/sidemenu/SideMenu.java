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
 * Created on May 31, 2014
 * @author michaellif
 */
package com.pyx4j.site.client.ui.sidemenu;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.security.shared.AccessControlContext;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.HasSecureConcern;

public class SideMenu extends ScrollPanel implements HasSecureConcern {

    private final SideMenuList root;

    public SideMenu(SideMenuList root) {
        this.root = root;
        setWidget(root);
    }

    public void select(AppPlace appPlace) {
        root.select(appPlace);
        SideMenuItem selectedItem = root.getSelectedLeaf();
        if (selectedItem != null && !isScrolledIntoView(selectedItem.asWidget())) {
            ensureVisible(selectedItem.asWidget());
        }
    }

    private boolean isScrolledIntoView(Widget widget) {
        if (widget != null) {
            return (widget.getAbsoluteTop() > getAbsoluteTop())
                    && (widget.getAbsoluteTop() + widget.getOffsetHeight()) < (getAbsoluteTop() + getOffsetHeight());
        }
        return false;
    }

    @Override
    public void setSecurityContext(AccessControlContext context) {
        root.setSecurityContext(context);
    }
}
