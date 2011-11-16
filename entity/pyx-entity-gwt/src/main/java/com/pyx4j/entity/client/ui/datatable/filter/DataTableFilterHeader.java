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
 * Created on Oct 4, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.datatable.filter;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.widgets.client.images.WidgetsImages;

public class DataTableFilterHeader extends HorizontalPanel {

    private final Image collapseImage;

    private final Label caption;

    private final WidgetsImages images;

    public DataTableFilterHeader(WidgetsImages images) {
        this.images = images;

        SimplePanel collapseImageHolder = new SimplePanel();
        collapseImageHolder.getElement().getStyle().setPadding(2, Unit.PX);

        collapseImage = new Image();
        collapseImage.setResource(images.collapse());
        collapseImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                onCollapse();
            }
        });

        collapseImageHolder.setWidget(collapseImage);

        add(collapseImageHolder);

        caption = new Label("Filter");
        caption.setStyleName(DefaultEntityFolderTheme.StyleName.EntityFolderBoxDecoratorCollapsedCaption.name());

        add(caption);
        setCellVerticalAlignment(caption, HorizontalPanel.ALIGN_MIDDLE);
        setCellWidth(caption, "100%");

    }

    protected void onCollapse() {
    }

    protected void setExpanded(boolean expanded) {
        collapseImage.setResource(expanded ? EntityFolderImages.INSTANCE.collapse() : images.expand());
    }
}