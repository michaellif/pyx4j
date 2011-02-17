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
 * Created on Feb 12, 2011
 * @author vlads
 * @version $Id: FormsFolderDecorator.java 8142 2011-02-13 04:31:26Z vlads $
 */
package com.pyx4j.entity.client.ui.flex;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

public class TableFolderItemDecorator extends FlowPanel implements FolderItemDecorator {

    private final Image image;

    private final SimplePanel content;

    public TableFolderItemDecorator(ImageResource removeButton) {
        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        image = new Image(removeButton);
        image.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        add(image);

        content = new SimplePanel();
        content.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        add(content);

    }

    public TableFolderItemDecorator(ImageResource removeButton, String title) {
        this(removeButton);
        image.setTitle(title);
    }

    @Override
    public void setWidget(IsWidget w) {
        content.setWidget(w);
    }

    @Override
    public HandlerRegistration addItemRemoveClickHandler(ClickHandler handler) {
        return image.addClickHandler(handler);
    }

    @Override
    public HandlerRegistration addRowUpClickHandler(ClickHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addRowDownClickHandler(ClickHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addRowCollapseClickHandler(ClickHandler handler) {
        return null;
    }

}
