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
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.folder;

import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderTableDecorator;
import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderTableHeader;
import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderTableHeaderLabel;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ImageFactory;

public class TableFolderDecorator<E extends IEntity> extends BaseFolderDecorator<E> {

    private final HorizontalPanel header;

    private boolean showHeader = true;

    public TableFolderDecorator(final List<EntityFolderColumnDescriptor> columns) {
        this(columns, EntityFolderImages.INSTANCE, null, false);
    }

    public TableFolderDecorator(final List<EntityFolderColumnDescriptor> columns, EntityFolderImages images, String title) {
        this(columns, images, title, true);
    }

    public TableFolderDecorator(final List<EntityFolderColumnDescriptor> columns, EntityFolderImages images) {
        this(columns, images, null, true);
    }

    public TableFolderDecorator(final List<EntityFolderColumnDescriptor> columns, EntityFolderImages images, String title, boolean addable) {
        super(images, title, addable);

        asWidget().setStyleName(EntityFolderTableDecorator.name());

        header = new HorizontalPanel();
        header.setStyleName(EntityFolderTableHeader.name());
        setHeaderVisible(false);

        for (EntityFolderColumnDescriptor column : columns) {
            HorizontalPanel headerLabelPanel = new HorizontalPanel();
            headerLabelPanel.setWidth(column.getWidth());
            headerLabelPanel.setStyleName(EntityFolderTableHeaderLabel.name());

            String caption = column.getObject().getMeta().getCaption();
            if (caption == "") {
                caption = "&nbsp";
            }
            HTML label = new HTML(caption);
            label.getElement().getStyle().setMarginLeft(3, Unit.PX);
            headerLabelPanel.add(label);
            headerLabelPanel.setCellVerticalAlignment(label, HorizontalPanel.ALIGN_BOTTOM);

            String descr = column.getObject().getMeta().getDescription();
            if ((descr != null) && !descr.trim().equals("")) {
                Image info = new Image(ImageFactory.getImages().formTooltipInfo());

                info.getElement().getStyle().setMarginLeft(6, Unit.PX);
                headerLabelPanel.add(info);
                headerLabelPanel.setCellVerticalAlignment(info, HorizontalPanel.ALIGN_MIDDLE);
                headerLabelPanel.setCellHorizontalAlignment(info, HorizontalPanel.ALIGN_CENTER);
                info.setTitle(column.getObject().getMeta().getDescription());
            }

            header.add(headerLabelPanel);
            header.setCellVerticalAlignment(headerLabelPanel, HorizontalPanel.ALIGN_BOTTOM);
        }

        add(header);

        add(getContainer());

        if (isAddable()) {
            add(getActionsPanel());
        }
    }

    public Widget getHeader() {
        return header;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    @Override
    public void onValueChange(ValueChangeEvent<IList<E>> event) {
        setHeaderVisible(!event.getValue().isNull() && event.getValue().size() > 0);
    }

    protected void setHeaderVisible(boolean visible) {
        if (visible && showHeader) {
            header.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        } else {
            header.getElement().getStyle().setDisplay(Display.NONE);
        }
    }

}