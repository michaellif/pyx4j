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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.widgets.client.Tooltip;

public class TableFolderDecorator<E extends IEntity> extends BaseFolderDecorator<E> {

    private final FlowPanel header;

    public TableFolderDecorator(final List<EntityFolderColumnDescriptor> columns) {
        this(columns, null, null, false);
    }

    public TableFolderDecorator(final List<EntityFolderColumnDescriptor> columns, ImageResource addButton, String title) {
        this(columns, addButton, title, true);
    }

    public TableFolderDecorator(final List<EntityFolderColumnDescriptor> columns, ImageResource addButton) {
        this(columns, addButton, null, true);
    }

    public TableFolderDecorator(final List<EntityFolderColumnDescriptor> columns, ImageResource addButton, String title, boolean addable) {
        super(addButton, title, addable);

        header = new FlowPanel();
        setHeaderVisible(false);
        header.setWidth("100%");
        if (addImage != null) {
            header.getElement().getStyle().setPaddingLeft(addImage.getWidth(), Unit.PX);
        }

        for (EntityFolderColumnDescriptor column : columns) {
            HorizontalPanel cellPanel = new HorizontalPanel();
            cellPanel.getElement().getStyle().setFloat(Float.LEFT);
            cellPanel.setWidth(column.getWidth());
            cellPanel.getElement().getStyle().setMarginLeft(3, Unit.PX);
            cellPanel.getElement().getStyle().setMarginRight(3, Unit.PX);

            String caption = column.getObject().getMeta().getCaption();
            if (caption == "") {
                caption = "&nbsp";
            }
            HTML label = new HTML(caption);
            cellPanel.add(label);

            String descr = column.getObject().getMeta().getDescription();
            if ((descr != null) && !descr.trim().equals("")) {
                Image info = new Image(ImageFactory.getImages().formTooltipInfo());

                info.getElement().getStyle().setPaddingRight(2, Unit.PX);
                cellPanel.add(info);
                Tooltip.tooltip(info, column.getObject().getMeta().getDescription());
            }

            header.add(cellPanel);
        }

        add(header);

        add(content);

        if (this.addable) {
            add(imageHolder);
        }
    }

    public Widget getHeader() {
        return header;
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