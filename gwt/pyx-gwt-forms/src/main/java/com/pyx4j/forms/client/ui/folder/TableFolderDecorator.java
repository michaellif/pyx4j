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
 */
package com.pyx4j.forms.client.ui.folder;

import static com.pyx4j.forms.client.ui.folder.FolderTheme.StyleName.CFolderTableDecorator;
import static com.pyx4j.forms.client.ui.folder.FolderTheme.StyleName.CFolderTableHeader;
import static com.pyx4j.forms.client.ui.folder.FolderTheme.StyleName.CFolderTableHeaderLabel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.pyx4j.gwt.commons.ui.HTML;
import com.pyx4j.gwt.commons.ui.HorizontalPanel;
import com.pyx4j.gwt.commons.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.decorators.DecoratorDebugIds;

public class TableFolderDecorator<E extends IEntity> extends BaseFolderDecorator<E> {

    private final HorizontalPanel header;

    private boolean showHeader = true;

    private CFolder<E> folder;

    private final List<Image> mandatoryImages = new ArrayList<Image>();

    public TableFolderDecorator(final List<FolderColumnDescriptor> columns, FolderImages images) {
        this(columns, images, null, true);
    }

    public TableFolderDecorator(final List<FolderColumnDescriptor> columns, FolderImages images, String title) {
        this(columns, images, title, true);
    }

    public TableFolderDecorator(final List<FolderColumnDescriptor> columns, FolderImages images, String title, boolean addable) {
        super(images, title, addable);

        asWidget().setStyleName(CFolderTableDecorator.name());

        add(getMessagePannel());

        header = new HorizontalPanel();
        header.setStyleName(CFolderTableHeader.name());
        setHeaderVisible(false);

        for (FolderColumnDescriptor column : columns) {
            HorizontalPanel headerLabelPanel = new HorizontalPanel();
            headerLabelPanel.setWidth(column.getWidth());
            headerLabelPanel.setStyleName(CFolderTableHeaderLabel.name());

            String caption;
            if (column.getCaption() != null) {
                caption = column.getCaption();
            } else {
                caption = column.getObject().getMeta().getCaption();
                if (caption == "") {
                    caption = "&nbsp";
                }
            }

            if (column.getObject().getMeta().isAnnotationPresent(NotNull.class)) {
                Image mandatoryImage = new Image();
                mandatoryImage.setResource(ImageFactory.getImages().mandatory());
                mandatoryImage.setTitle("This field is mandatory");
                mandatoryImage.setVisible(false);

                mandatoryImage.ensureDebugId(new CompositeDebugId(DecoratorDebugIds.TableFolderDecorator,
                        column.getObject().getMeta().getFieldName() + "-" + DecoratorDebugIds.Mandatory).debugId());

                headerLabelPanel.add(mandatoryImage);
                headerLabelPanel.setCellWidth(mandatoryImage, "1px");
                mandatoryImages.add(mandatoryImage);

            }

            HTML label = new HTML(caption);
            label.getStyle().setMarginLeft(3, Unit.PX);
            headerLabelPanel.add(label);
            headerLabelPanel.setCellVerticalAlignment(label, HorizontalPanel.ALIGN_BOTTOM);

            String descr = column.getObject().getMeta().getDescription();
            if ((descr != null) && !descr.trim().equals("")) {
                Image info = new Image(ImageFactory.getImages().formTooltipInfo());
                headerLabelPanel.add(info);
                headerLabelPanel.setCellVerticalAlignment(info, HorizontalPanel.ALIGN_MIDDLE);
                headerLabelPanel.setCellHorizontalAlignment(info, HorizontalPanel.ALIGN_CENTER);
                info.setTitle(descr);
            }

            header.add(headerLabelPanel);
            header.setCellVerticalAlignment(headerLabelPanel, HorizontalPanel.ALIGN_BOTTOM);
        }

        add(header);

        add(getContentPanel());

        if (isAddable()) {
            add(getAddButton());
        }

    }

    @Override
    public void init(final CFolder<E> folder) {
        this.folder = folder;
        super.init(folder);
        folder.addPropertyChangeHandler(new PropertyChangeHandler() {

            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                applyFolderProperties();
            }
        });

        applyFolderProperties();

        folder.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyName.debugId) {
                    onSetDebugId(folder.getDebugId());
                }
            }
        });

        onSetDebugId(folder.getDebugId());

    }

    private void applyFolderProperties() {
        if (folder.isEditable() && folder.isEnabled() & !folder.isViewable()) {
            renderMandatoryStars(true);
        }
    }

    private void renderMandatoryStars(boolean visible) {
        for (Image image : mandatoryImages) {
            image.setVisible(visible);
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

    public void setHeaderVisible(boolean visible) {
        if (visible && showHeader) {
            header.getStyle().setDisplay(Display.INLINE_BLOCK);
        } else {
            header.getStyle().setDisplay(Display.NONE);
        }
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {

    }
}