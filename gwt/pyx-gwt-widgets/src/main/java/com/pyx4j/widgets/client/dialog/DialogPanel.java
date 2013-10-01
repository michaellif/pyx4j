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
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.dialog;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.PopupPanel;

public class DialogPanel extends PopupPanel implements ProvidesResize {

    private final DockPanel container;

    private HandlerRegistration resizeHandlerRegistration;

    private final CaptionPanel captionPanel;

    public DialogPanel() {
        super(false, true);

        setStylePrimaryName(DefaultDialogTheme.StyleName.Dialog.name());

        getElement().getStyle().setProperty("zIndex", "20");

        container = new DockPanel();
        container.getElement().getStyle().setProperty("cursor", "default");

        captionPanel = new CaptionPanel();
        container.add(captionPanel, DockPanel.NORTH);

        setWidget(container);

    }

    public void setContentWidget(Widget widget) {
        container.add(widget, DockPanel.CENTER);
        container.setCellHeight(widget, "100%");
    }

    public void setCaption(String caption) {
        captionPanel.setHTML(caption);
    }

    @Override
    public void show() {
        super.show();
        center();
        if (resizeHandlerRegistration == null) {
            resizeHandlerRegistration = Window.addResizeHandler(new ResizeHandler() {
                @Override
                public void onResize(ResizeEvent event) {
                    center();
                }
            });
        }
    }

    class CaptionPanel extends HTML {

        public CaptionPanel() {
            setWordWrap(false);
            setStylePrimaryName(DefaultDialogTheme.StyleName.DialogCaption.name());
            getElement().getStyle().setHeight(1.5, Unit.EM);
            getElement().getStyle().setLineHeight(1.5, Unit.EM);
        }

    }

}
