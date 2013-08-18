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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

public class BreadcrumbsBar extends HorizontalPanel {

    public BreadcrumbsBar() {
        setStyleName(DefaultPaneTheme.StyleName.BreadcrumbsBar.name());
    }

    public void populate(Vector<IEntity> breadcrumbTrail) {
        clear();

        if (breadcrumbTrail != null) {
            // filter out breadcrumbs that don't have associated place
            List<IEntity> filteredTrail = new ArrayList<IEntity>(breadcrumbTrail.size());
            for (IEntity breadcrumb : breadcrumbTrail) {
                if (AppPlaceEntityMapper.resolvePlace(breadcrumb.getInstanceValueClass()) != null) {
                    filteredTrail.add(breadcrumb);
                }
            }

            // build UI representation of the trail
            for (int i = 0; i < filteredTrail.size(); i++) {
                IEntity breadcrumb = breadcrumbTrail.get(i);
                BreadcrumbAnchor anchor = new BreadcrumbAnchor(breadcrumb);
                add(anchor);
                setCellVerticalAlignment(anchor, HorizontalPanel.ALIGN_MIDDLE);
                if (i < breadcrumbTrail.size() - 1) {
                    Label separator = new HTML("&nbsp;>&nbsp;");
                    add(separator);
                    setCellVerticalAlignment(separator, HorizontalPanel.ALIGN_MIDDLE);
                }
            }
        }
    }

    public class BreadcrumbAnchor extends HorizontalPanel {

        private final Label anchor;

        public BreadcrumbAnchor(final IEntity breadcrumb) {
            setStyleName(DefaultPaneTheme.StyleName.BreadcrumbAnchor.name());

            String label = !breadcrumb.getStringView().isEmpty() ? breadcrumb.getStringView() : breadcrumb.getEntityMeta().getCaption();
            anchor = new Label(label);
            anchor.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(breadcrumb.getObjectClass(), breadcrumb.getPrimaryKey()));
                }
            });

            ImageResource image = AppPlaceEntityMapper.resolveImageResource(breadcrumb.getObjectClass());
            if (image != null) {
                Image icon = new Image(image);
                add(icon);
                setCellVerticalAlignment(icon, HorizontalPanel.ALIGN_MIDDLE);
            }
            add(anchor);
            setCellVerticalAlignment(anchor, HorizontalPanel.ALIGN_MIDDLE);

        }
    }
}