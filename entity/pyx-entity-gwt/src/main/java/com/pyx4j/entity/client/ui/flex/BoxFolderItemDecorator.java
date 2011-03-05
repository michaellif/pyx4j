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

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;

public class BoxFolderItemDecorator extends BaseFolderItemDecorator {

    public BoxFolderItemDecorator(ImageResource removeButton, String title, boolean removable) {
        super(removeButton, title, removable);

        content.getElement().getStyle().setMarginTop(10, Unit.PX);
        content.getElement().getStyle().setMarginLeft(10, Unit.PX);
        content.getElement().getStyle().setPadding(10, Unit.PX);
        content.getElement().getStyle().setBorderStyle(BorderStyle.DASHED);
        content.getElement().getStyle().setBorderWidth(1, Unit.PX);
        content.getElement().getStyle().setBorderColor("#999");

        setWidget(rowHolder);

    }

    public BoxFolderItemDecorator(ImageResource removeButton, String title) {
        this(removeButton, title, true);
    }

    public BoxFolderItemDecorator(ImageResource removeButton) {
        this(removeButton, null, true);
    }

    @Override
    public HandlerRegistration addItemClickHandler(ClickHandler handler) {
        return null;
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
