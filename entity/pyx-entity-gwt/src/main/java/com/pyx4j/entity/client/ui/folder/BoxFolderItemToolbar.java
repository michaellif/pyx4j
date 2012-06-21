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
package com.pyx4j.entity.client.ui.folder;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;

public class BoxFolderItemToolbar extends HorizontalPanel {

    public static enum DebugIds implements IDebugId {
        CollapseImage, Caption, Decorator, ImageWarn, TitleIcon;

        @Override
        public String debugId() {
            return name();
        }
    }

    private final BoxFolderItemDecorator<?> decorator;

    private final SimplePanel actionsPanelHolder;

    private final Image collapseImage;

    private final Label caption;

    private final Image warnImage;

    private String warnMessage;

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
                setExpended(!decorator.isExpended());
            }
        });

        collapseImageHolder.setWidget(collapseImage);

        add(collapseImageHolder);

        HorizontalPanel captionHolder = new HorizontalPanel();
        captionHolder.getElement().getStyle().setMarginLeft(5, Unit.PX);
        captionHolder.getElement().getStyle().setMarginRight(5, Unit.PX);
        captionHolder.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setExpended(!decorator.isExpended());
            }
        }, ClickEvent.getType());

        caption = new Label("");
        caption.setStyleName(DefaultEntityFolderTheme.StyleName.EntityFolderBoxDecoratorCollapsedCaption.name());

        titleIcon = new Image();
        titleIcon.getElement().getStyle().setMarginTop(2, Unit.PX);
        titleIcon.getElement().getStyle().setPaddingRight(2, Unit.PX);
        titleIcon.setVisible(false);

        captionHolder.add(titleIcon);

        captionHolder.add(caption);

        add(captionHolder);
        setCellVerticalAlignment(captionHolder, HorizontalPanel.ALIGN_MIDDLE);
        setCellWidth(captionHolder, "100%");

        warnImage = new Image(decorator.getImages().warn());
        warnImage.setVisible(false);
        warnImage.getElement().getStyle().setMargin(2, Unit.PX);
        warnImage.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        add(warnImage);

        actionsPanelHolder = new SimplePanel();
        add(actionsPanelHolder);

        decorator.ensureDebugId(new CompositeDebugId(IFolderDecorator.DecoratorsIds.BoxFolderItemToolbar, DebugIds.Decorator).debugId());

        caption.ensureDebugId(new CompositeDebugId(IFolderDecorator.DecoratorsIds.BoxFolderItemToolbar, DebugIds.Caption).debugId());
        titleIcon.ensureDebugId(new CompositeDebugId(IFolderDecorator.DecoratorsIds.BoxFolderItemToolbar, DebugIds.TitleIcon).debugId());
        warnImage.ensureDebugId(new CompositeDebugId(IFolderDecorator.DecoratorsIds.BoxFolderItemToolbar, DebugIds.ImageWarn).debugId());
        collapseImage.ensureDebugId(new CompositeDebugId(IFolderDecorator.DecoratorsIds.BoxFolderItemToolbar, DebugIds.CollapseImage).debugId());

        update(decorator.isExpended());

    }

    private void setExpended(boolean expended) {
        decorator.setExpended(expended);

        showWarning();

    }

    protected void update(boolean expanded) {
        if (decorator.getFolderItem() != null && decorator.getFolderItem().getValue() != null) {
            setCaption(decorator.getFolderItem().getValue().getStringView());
        }
        caption.setVisible(!expanded);
        collapseImage.setResource(expanded ? decorator.getImages().collapse() : decorator.getImages().expand());
    }

    protected void setTitleIcon(ImageResource icon) {
        if (icon != null) {
            titleIcon.setResource(icon);
            titleIcon.setVisible(true);
        } else {
            titleIcon.setVisible(false);
        }
    }

    protected void setCaption(String text) {
        caption.setText(text);
    }

    public void setCollapseButtonVisible(boolean collapsible) {
        collapseImage.setVisible(collapsible);
    }

    public void setActionsBar(ItemActionsBar actionsPanel) {
        actionsPanelHolder.setWidget(actionsPanel);
    }

    public void setWarningMessage(String message) {
        warnMessage = message;
        showWarning();
    }

    private void showWarning() {
        if (warnMessage != null) {
            warnImage.setTitle(warnMessage);
            if (!decorator.isExpended()) {
                warnImage.setVisible(true);
            }
        } else {
            warnImage.setTitle(null);
            warnImage.setVisible(false);
        }
    }
}