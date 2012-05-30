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
 * Created on May 26, 2010
 * @author stanp
 * @version $Id$
 */
package com.pyx4j.entity.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.widgets.client.images.WidgetsImages;

public class BaseCollapsableDecorator<E extends IEntity> extends SimplePanel implements ICollapsableDecorator<E> {

    private final WidgetsImages images;

    private final SimplePanel contentHolder;

    private final Image collapseImage = new Image();

    private CEntityCollapsableViewer<E> viewer;

    public BaseCollapsableDecorator(WidgetsImages images) {
        this.images = images;

        HorizontalPanel mainPanel = new HorizontalPanel();
        mainPanel.setWidth("100%");
        collapseImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                toggleCollapsed();
            }
        });
        mainPanel.add(collapseImage);
        reset();
        mainPanel.setCellWidth(collapseImage, collapseImage.getWidth() + "px");
        contentHolder = new SimplePanel();
//        contentHolder.setStyleName(EntityFolderBoxItem.name());
        mainPanel.add(contentHolder);

        setWidget(mainPanel);
    }

    private void toggleCollapsed() {
        viewer.setCollapsed(!viewer.isCollapsed());
        reset();
    }

    private void reset() {
        collapseImage.setResource(viewer.isCollapsed() ? images.expand() : images.collapse());
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setComponent(CEntityCollapsableViewer<E> viewer) {
        this.viewer = viewer;
        contentHolder.setWidget(viewer.createContent());
        reset();
    }

}
