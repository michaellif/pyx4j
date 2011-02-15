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
 * Created on Feb 12, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.entity.client.ui.flex;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

public class TableFolderDecorator<E extends IEntity> extends FlowPanel implements FolderDecorator<E> {

    private final Image image;

    private final SimplePanel content;

    private FlowPanel header;

    public TableFolderDecorator(final List<EntityFolderColumnDescriptor> columns, ImageResource addButton) {

        image = new Image(addButton);
        header = new FlowPanel();
        setHeaderVisible(false);
        header.setWidth("100%");
        header.getElement().getStyle().setPaddingLeft(image.getWidth(), Unit.PX);
        for (EntityFolderColumnDescriptor column : columns) {
            Label label = new Label(column.getObject().getMeta().getCaption());
            label.setWidth(column.getWidth());
            label.asWidget().getElement().getStyle().setFloat(Float.LEFT);
            header.add(label);
        }

        add(header);

        content = new SimplePanel();
        add(content);

        add(image);
    }

    public TableFolderDecorator(final List<EntityFolderColumnDescriptor> columns, ImageResource addButton, String title) {
        this(columns, addButton);
        image.setTitle(title);
    }

    @Override
    public void setWidget(IsWidget w) {
        content.setWidget(w);
    }

    @Override
    public HandlerRegistration addRowAddClickHandler(ClickHandler handler) {
        return image.addClickHandler(handler);
    }

    @Override
    public void onValueChange(ValueChangeEvent<IList<E>> event) {
        setHeaderVisible(!event.getValue().isNull() && event.getValue().size() > 0);
    }

    private void setHeaderVisible(boolean visible) {
        if (visible) {
            header.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        } else {
            header.getElement().getStyle().setDisplay(Display.NONE);
        }
    }
}