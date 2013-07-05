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
package com.pyx4j.forms.client.ui.decorators;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.ui.CEntityContainer;
import com.pyx4j.widgets.client.images.WidgetsImages;

public class EntityContainerDecoratorToolbar extends HorizontalPanel {

    public static enum DebugIds implements IDebugId {
        Caption, Decorator, ImageWarn, TitleIcon;

        @Override
        public String debugId() {
            return name();
        }
    }

    private CEntityContainer<?> entityContainer;

    private final SimplePanel actionsPanelHolder;

    private final Label caption;

    private final Image warnImage;

    private final Image titleIcon;

    private final HorizontalPanel captionHolder;

    public EntityContainerDecoratorToolbar(WidgetsImages images) {

        setWidth("100%");

        captionHolder = new HorizontalPanel();
        captionHolder.getElement().getStyle().setMarginLeft(50, Unit.PX);
        captionHolder.getElement().getStyle().setMarginRight(5, Unit.PX);

        caption = new Label("");
        caption.setStyleName(DefaultWidgetDecoratorTheme.StyleName.EntityContainerDecoratorCollapsedCaption.name());

        titleIcon = new Image();
        titleIcon.getElement().getStyle().setMarginTop(2, Unit.PX);
        titleIcon.getElement().getStyle().setPaddingRight(2, Unit.PX);
        titleIcon.setVisible(false);

        captionHolder.add(titleIcon);

        captionHolder.add(caption);

        add(captionHolder);
        setCellVerticalAlignment(captionHolder, HorizontalPanel.ALIGN_MIDDLE);
        setCellWidth(captionHolder, "100%");

        warnImage = new Image(images.warn());
        warnImage.setVisible(false);
        warnImage.getElement().getStyle().setMargin(2, Unit.PX);
        warnImage.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        add(warnImage);

        actionsPanelHolder = new SimplePanel();
        add(actionsPanelHolder);

        caption.ensureDebugId(new CompositeDebugId(DecoratorDebugIds.BoxFolderItemToolbar, DebugIds.Caption).debugId());
        titleIcon.ensureDebugId(new CompositeDebugId(DecoratorDebugIds.BoxFolderItemToolbar, DebugIds.TitleIcon).debugId());
        warnImage.ensureDebugId(new CompositeDebugId(DecoratorDebugIds.BoxFolderItemToolbar, DebugIds.ImageWarn).debugId());

    }

    public void addCaptionHolderClickHandler(ClickHandler clickHandler) {
        captionHolder.addDomHandler(clickHandler, ClickEvent.getType());
    }

    public void update(boolean expanded) {
        if (entityContainer != null && entityContainer.getValue() != null) {
            setCaption(entityContainer.getValue().getStringView());
        }
        caption.setVisible(!expanded);
    }

    public void setEntityContainer(CEntityContainer<?> entityContainer) {
        this.entityContainer = entityContainer;
        ImageResource icon = entityContainer.getIcon();
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

    public void setActionsBar(IsWidget actionsBar) {
        actionsPanelHolder.setWidget(actionsBar);
    }

    public void setWarningMessage(String message) {
        if (message != null) {
            warnImage.setTitle(message);
            warnImage.setVisible(true);
        } else {
            warnImage.setTitle(null);
            warnImage.setVisible(false);
        }
    }

}