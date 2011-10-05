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
package com.pyx4j.entity.client.ui.flex.folder;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.widgets.client.util.BrowserType;

public class BoxFolderItemToolbar extends HorizontalPanel {

    private final BoxFolderItemDecorator<?> decorator;

    private final ItemActionsBar actionsPanel;

    private final Image collapseImage;

    private final Label caption;

    private final Image imageWarn;

    private final Image titleIcon;

    public BoxFolderItemToolbar(final BoxFolderItemDecorator<?> decorator) {

        this.decorator = decorator;
        setWidth("100%");

        SimplePanel collapseImageHolder = new SimplePanel();
        collapseImageHolder.getElement().getStyle().setPadding(2, Unit.PX);

        collapseImage = new Image();
        //Fix the ensureDebugId initialisation
        collapseImage.setResource(decorator.getImages().collapse());
        collapseImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                decorator.setExpended(!decorator.isExpended());
            }
        });

        collapseImageHolder.setWidget(collapseImage);

        add(collapseImageHolder);

        HorizontalPanel captionHolder = new HorizontalPanel();
        captionHolder.setWidth("300px");
        captionHolder.getElement().getStyle().setMarginLeft(5, Unit.PX);
        captionHolder.getElement().getStyle().setMarginRight(5, Unit.PX);
        captionHolder.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                decorator.setExpended(!decorator.isExpended());
            }
        }, ClickEvent.getType());

        caption = new Label("");

        titleIcon = new Image();
        titleIcon.getElement().getStyle().setMarginTop(2, Unit.PX);
        titleIcon.getElement().getStyle().setPaddingRight(2, Unit.PX);
        captionHolder.add(titleIcon);

        captionHolder.add(caption);

        add(captionHolder);
        setCellWidth(captionHolder, "100%");

        imageWarn = new Image(decorator.getImages().warn());
        imageWarn.setVisible(false);
        imageWarn.getElement().getStyle().setMargin(2, Unit.PX);
        imageWarn.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        add(imageWarn);

        actionsPanel = new ItemActionsBar(true, Direction.LTR, decorator.getImages());
        add(actionsPanel);
        actionsPanel.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        if (BrowserType.isIE7()) {
            actionsPanel.getElement().getStyle().setMarginRight(40, Unit.PX);
        }

        onExpended(decorator.isExpended());

    }

    protected ItemActionsBar getActionsPanel() {
        return actionsPanel;
    }

    protected void onExpended(boolean expanded) {
        if (decorator.getFolderItem() != null && decorator.getFolderItem().getValue() != null) {
            caption.setText(decorator.getFolderItem().getValue().getStringView());
        }
        caption.setVisible(!expanded);
        collapseImage.setResource(expanded ? decorator.getImages().collapse() : decorator.getImages().expand());
    }

    protected void setTitleIcon(ImageResource icon) {
        if (icon != null) {
            titleIcon.setResource(icon);
        }
    }

    protected void setCaption(String text) {
        caption.setText(text);
    }

}