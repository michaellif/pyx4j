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
 */
package com.pyx4j.forms.client.ui.decorators;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.TextOverflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.images.WidgetsImages;

public class EntityContainerDecoratorToolbar<E extends IEntity> extends FlowPanel {

    public static enum DebugIds implements IDebugId {
        Caption, Decorator, ImageWarn, TitleIcon;

        @Override
        public String debugId() {
            return name();
        }
    }

    private CForm<E> entityForm;

    private final SimplePanel actionsPanelHolder;

    private final HTML caption;

    private final Image warnImage;

    private final Image titleIcon;

    private final FlowPanel captionHolder;

    private IFormatter<E, SafeHtml> captionFormatter;

    public EntityContainerDecoratorToolbar(WidgetsImages images) {

        setWidth("100%");
        getElement().getStyle().setTextAlign(TextAlign.LEFT);
        setStyleName(WidgetDecoratorTheme.StyleName.EntityContainerDecoratorToolbar.name());

        captionFormatter = new IFormatter<E, SafeHtml>() {
            @Override
            public SafeHtml format(E value) {
                return SafeHtmlUtils.fromString(value.getStringView());
            }
        };

        captionHolder = new FlowPanel();
        captionHolder.getElement().getStyle().setMarginLeft(40, Unit.PX);
        captionHolder.getElement().getStyle().setMarginRight(60, Unit.PX);
        captionHolder.getElement().getStyle().setLineHeight(2, Unit.EM);

        caption = new HTML("");
        caption.setStyleName(WidgetDecoratorTheme.StyleName.EntityContainerDecoratorCollapsedCaption.name());
        caption.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
        caption.getElement().getStyle().setOverflow(Overflow.HIDDEN);
        caption.getElement().getStyle().setTextOverflow(TextOverflow.ELLIPSIS);

        titleIcon = new Image();
        titleIcon.getElement().getStyle().setFloat(Float.LEFT);
        titleIcon.getElement().getStyle().setMarginTop(2, Unit.PX);
        titleIcon.getElement().getStyle().setPaddingRight(2, Unit.PX);
        titleIcon.setVisible(false);

        captionHolder.add(titleIcon);

        captionHolder.add(caption);

        SimplePanel captionPanel = new SimplePanel(captionHolder);
        captionPanel.setWidth("100%");
        captionPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        captionPanel.getElement().getStyle().setTextAlign(TextAlign.LEFT);

        Toolbar actionsPanel = new Toolbar();
        actionsPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
        actionsPanel.getElement().getStyle().setTop(0, Unit.PX);
        actionsPanel.getElement().getStyle().setRight(0, Unit.PX);

        actionsPanelHolder = new SimplePanel();
        actionsPanel.addItem(actionsPanelHolder);

        warnImage = new Image(images.warn());
        warnImage.setVisible(false);
        actionsPanel.addItem(warnImage);

        add(captionPanel);
        add(actionsPanel);

        caption.ensureDebugId(new CompositeDebugId(DecoratorDebugIds.BoxFolderItemToolbar, DebugIds.Caption).debugId());
        titleIcon.ensureDebugId(new CompositeDebugId(DecoratorDebugIds.BoxFolderItemToolbar, DebugIds.TitleIcon).debugId());
        warnImage.ensureDebugId(new CompositeDebugId(DecoratorDebugIds.BoxFolderItemToolbar, DebugIds.ImageWarn).debugId());

    }

    public void addCaptionHolderClickHandler(ClickHandler clickHandler) {
        captionHolder.addDomHandler(clickHandler, ClickEvent.getType());
    }

    public void update(boolean expanded) {
        if (entityForm != null && entityForm.getValue() != null) {
            setCaption(captionFormatter.format(entityForm.getValue()));
        }
        caption.setVisible(!expanded);
    }

    public void setEntityForm(CForm<E> entityForm) {
        this.entityForm = entityForm;
        ImageResource icon = entityForm.getIcon();
        if (icon != null) {
            titleIcon.setResource(icon);
            titleIcon.setVisible(true);
        } else {
            titleIcon.setVisible(false);
        }
    }

    protected void setCaption(SafeHtml html) {
        caption.setHTML(html);
    }

    protected void setCaption(String text) {
        caption.setText(text);
    }

    public void setActionsBar(IsWidget actionsBar) {
        actionsPanelHolder.setWidget(actionsBar);
    }

    public void setWarningMessage(String message) {
        if (message != null && !message.isEmpty()) {
            warnImage.setTitle(message);
            warnImage.setVisible(true);
        } else {
            warnImage.setTitle(null);
            warnImage.setVisible(false);
        }
    }

    public void setCaptionFormatter(IFormatter<E, SafeHtml> formatter) {
        this.captionFormatter = formatter;
    }
}