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
 * Created on Jun 19, 2013
 * @author michaellif
 */
package com.pyx4j.site.client.frontoffice.ui.layout;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutTheme;

public class InlineExtraHolder extends SimplePanel {

    private final FrontOfficeLayoutPanel parent;

    private final String extra1Caption;

    private final String extra2Caption;

    private FlowPanel contentPanel;

    public InlineExtraHolder(FrontOfficeLayoutPanel parent, String extra1Caption, String extra2Caption) {
        this.parent = parent;
        this.extra1Caption = extra1Caption;
        this.extra2Caption = extra2Caption;

        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        setWidget(contentPanel = new FlowPanel());
        contentPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        layout();
    }

    public void layout() {

        switch (LayoutType.getLayoutType(Window.getClientWidth())) {
        case huge:
            contentPanel.clear();
            contentPanel.setVisible(true);

            if (parent.getDisplay(DisplayType.extra1).getWidget() != null) {
                contentPanel.add(new ExtraPanel(parent.getDisplay(DisplayType.extra1), extra1Caption, ResponsiveLayoutTheme.StyleDependent.extra1.name()));
            }

            if (parent.getDisplay(DisplayType.extra2).getWidget() != null) {
                contentPanel.add(new ExtraPanel(parent.getDisplay(DisplayType.extra2), extra2Caption, ResponsiveLayoutTheme.StyleDependent.extra2.name()));
            }

            if (parent.getDisplay(DisplayType.extra3).getWidget() != null) {
                contentPanel.add(new ExtraPanel(parent.getDisplay(DisplayType.extra3), null, ResponsiveLayoutTheme.StyleDependent.extra3.name()));
            }
            break;
        default:
            contentPanel.setVisible(false);
            break;
        }

    }

    public void onPositionChange() {
        if (contentPanel != null && isAttached()) {
            int offsetTop = parent.getDisplay(DisplayType.toolbar).getOffsetHeight();
            int offsetBottom = parent.getDisplay(DisplayType.footer).getAbsoluteTop();
            contentPanel.setHeight("auto");

            //TODO investigate why container's getAbsoluteTop() changes when child's position changes from STATIC to FIXED
            //Workaround - use 10px threshold 
            if (getAbsoluteTop() > offsetTop + 10) {
                contentPanel.getElement().getStyle().setPosition(Position.STATIC);
                getElement().getStyle().setProperty("width", "auto");
            } else if (getAbsoluteTop() < offsetTop - 10) {
                contentPanel.getElement().getStyle().setPosition(Position.FIXED);
                getElement().getStyle().setWidth(contentPanel.getOffsetWidth(), Unit.PX);
                if ((offsetTop + contentPanel.getOffsetHeight()) <= offsetBottom) {
                    contentPanel.getElement().getStyle().setProperty("top", offsetTop + "px");
                    contentPanel.getElement().getStyle().setProperty("bottom", "auto");
                } else {
                    contentPanel.getElement().getStyle().setProperty("bottom", Window.getClientHeight() - offsetBottom + "px");
                    contentPanel.getElement().getStyle().setProperty("top", "auto");
                }
            }

        } else {
            getElement().getStyle().setWidth(0, Unit.PX);
        }

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        onPositionChange();
    }

    class ExtraPanel extends FlowPanel {

        ExtraPanel(IsWidget widget, String caption, String styleSuffix) {
            super();
            setStylePrimaryName(FrontOfficeLayoutTheme.StyleName.FrontOfficeLayoutInlineExtraPanel.name());
            addStyleDependentName(styleSuffix);

            if (caption != null && !caption.trim().equals("")) {
                HTML captionLabel = new HTML(caption);
                captionLabel.setStylePrimaryName(FrontOfficeLayoutTheme.StyleName.FrontOfficeLayoutInlineExtraPanelCaption.name());
                add(captionLabel);
            }
            add(widget);
        }
    }
}
