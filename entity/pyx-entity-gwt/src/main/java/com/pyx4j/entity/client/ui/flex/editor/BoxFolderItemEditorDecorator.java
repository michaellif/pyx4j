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
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex.editor;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;

public class BoxFolderItemEditorDecorator extends BaseFolderItemEditorDecorator {

    public BoxFolderItemEditorDecorator(ImageResource removeButton, ImageResource removeButtonHover, String title, boolean removable) {
        super(removeButton, removeButtonHover, title, removable);

        getContent().getElement().getStyle().setMarginTop(10, Unit.PX);
        getContent().getElement().getStyle().setMarginLeft(10, Unit.PX);
        getContent().getElement().getStyle().setPadding(10, Unit.PX);
        getContent().getElement().getStyle().setBorderStyle(BorderStyle.DASHED);
        getContent().getElement().getStyle().setBorderWidth(1, Unit.PX);
        getContent().getElement().getStyle().setBorderColor("#999");

        setWidget(getRowHolder());

    }

    public BoxFolderItemEditorDecorator(ImageResource removeButton, String title, boolean removable) {
        this(removeButton, null, title, removable);
    }

    public BoxFolderItemEditorDecorator(ImageResource removeButton, ImageResource removeButtonHover, String title) {
        this(removeButton, removeButtonHover, title, true);
    }

    public BoxFolderItemEditorDecorator(ImageResource removeButton, String title) {
        this(removeButton, null, title);
    }

    public BoxFolderItemEditorDecorator(ImageResource removeButton, ImageResource removeButtonHover) {
        this(removeButton, removeButtonHover, null);
    }

    public BoxFolderItemEditorDecorator(ImageResource removeButton) {
        this(removeButton, (ImageResource) null);
    }

    @Override
    public HandlerRegistration addItemClickHandler(ClickHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addItemRemoveClickHandler(ClickHandler handler) {
        return getRemoveImage().addClickHandler(handler);
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
