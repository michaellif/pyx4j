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

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.ui.FormNavigationDebugId;
import com.pyx4j.gwt.commons.HandlerRegistrationGC;
import com.pyx4j.widgets.client.Tooltip;

public class TableFolderDecorator<E extends IEntity> extends FlowPanel implements FolderDecorator<E> {

    private final Image addImage;

    private final Label addButtonLabel;

    private final SimplePanel content;

    private final FlowPanel header;

    private boolean addable;

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
        this.addable = addable && (addButton != null);

        FlowPanel imageHolder = null;
        if (this.addable) {
            addImage = new Image(addButton);
            addImage.getElement().getStyle().setCursor(Cursor.POINTER);
            addImage.getElement().getStyle().setFloat(Float.LEFT);

            imageHolder = new FlowPanel();
            imageHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            imageHolder.getElement().getStyle().setPaddingLeft(addImage.getWidth(), Unit.PX);
            imageHolder.add(addImage);

            addButtonLabel = new Label(title);
            addButtonLabel.getElement().getStyle().setPaddingLeft(3, Unit.PX);
            addButtonLabel.getElement().getStyle().setFloat(Float.LEFT);
            imageHolder.add(addButtonLabel);
        } else {
            addImage = null;
            addButtonLabel = null;
        }

        header = new FlowPanel();
        setHeaderVisible(false);
        header.setWidth("100%");
        if (this.addable) {
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

        content = new SimplePanel();
        add(content);

        if (this.addable) {
            add(imageHolder);
        }
    }

    @Override
    public void setFolder(CEntityFolder<?> entityFolder) {
        content.setWidget(entityFolder.getContent());
    }

    //TODO Misha how do we customise style properly!
    public Style getHeaderPanelStyle() {
        return header.getElement().getStyle();
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        //TODO use inheritance of objects
        //image.ensureDebugId(CompositeDebugId.debugId(parentFolder.getDebugId(), FormNavigationDebugId.Form_Add));
        if (addable) {
            addImage.ensureDebugId(baseID + "_" + FormNavigationDebugId.Form_Add.getDebugIdString());
            addButtonLabel.ensureDebugId(baseID + "_" + FormNavigationDebugId.Form_Add.getDebugIdString() + "_label");
        }
    }

    @Override
    public HandlerRegistration addItemAddClickHandler(ClickHandler handler) {
        if (!addable) {
            return null;
        }

        HandlerRegistrationGC h = new HandlerRegistrationGC();
        h.add(addImage.addClickHandler(handler));
        h.add(addButtonLabel.addClickHandler(handler));
        return h;
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