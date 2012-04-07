/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Apr 6, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui;

import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;

public class BreadcrumbsBar extends HorizontalPanel {

    public BreadcrumbsBar() {
        setStyleName(DefaultSiteCrudPanelsTheme.StyleName.BreadcrumbsBar.name());
    }

    public void populate(Vector<IEntity> breadcrumbTrail) {
        clear();

        if (breadcrumbTrail != null) {
            for (int i = 0; i < breadcrumbTrail.size(); i++) {
                IEntity breadcrumb = breadcrumbTrail.get(i);
                add(new BreadcrumbAnchor(breadcrumb).asWidget());
                if (i < breadcrumbTrail.size() - 1) {
                    Label separator = new Label(">");
                    add(separator);
                    setCellVerticalAlignment(separator, HorizontalPanel.ALIGN_MIDDLE);
                }
            }
        }
    }

    public class BreadcrumbAnchor extends HorizontalPanel {

        private final Label anchor;

        public BreadcrumbAnchor(final IEntity breadcrumb) {
            setStyleName(DefaultSiteCrudPanelsTheme.StyleName.BreadcrumbAnchor.name());

            String label = !breadcrumb.getStringView().isEmpty() ? breadcrumb.getStringView() : breadcrumb.getEntityMeta().getCaption();
            anchor = new Label(label);
            anchor.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(breadcrumb.getObjectClass(), breadcrumb.getPrimaryKey()));
                }
            });
            add(new Image(AppPlaceEntityMapper.resolveImageResource(breadcrumb.getObjectClass())));
            add(anchor);
            setCellVerticalAlignment(anchor, HorizontalPanel.ALIGN_MIDDLE);

        }

    }
}