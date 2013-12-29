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
 * Created on Nov 14, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.gwt.commons.FocusUtil;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.IconButton;

public class DataTableFilterPanel<E extends IEntity> extends DockPanel {

    private static final I18n i18n = I18n.get(DataTableFilterPanel.class);

    private final DataTablePanel<E> dataTablePanel;

    private final DataTableFilterGrid<E> grid;

    private final Button btnApply;

    private final Button btnClose;

    private Command filterActionCommand;

    public DataTableFilterPanel(DataTablePanel<E> dataTablePanel) {
        this.dataTablePanel = dataTablePanel;

        setStyleName(DefaultDataTableTheme.StyleName.DataTableFilter.name());

        final Widget addButtonWidget = createAddButton();

        final SimplePanel footer = new SimplePanel();

        DataTableFilterHeader header = new DataTableFilterHeader(dataTablePanel.getImages()) {
            @Override
            protected void onCollapse() {
                super.onCollapse();
                boolean visible = !grid.isVisible();
                grid.setVisible(visible);
                addButtonWidget.setVisible(visible);
                footer.setVisible(visible);
                setExpanded(visible);
            }
        };
        header.setStyleName(DefaultDataTableTheme.StyleName.DataTableFilterHeader.name());

        add(header, DockPanel.NORTH);
        grid = new DataTableFilterGrid<E>(dataTablePanel);
        grid.setStyleName(DefaultDataTableTheme.StyleName.DataTableFilterMain.name());

        add(grid, DockPanel.CENTER);

        footer.setStyleName(DefaultDataTableTheme.StyleName.DataTableFilterFooter.name());

        HorizontalPanel buttonsPanel = new HorizontalPanel();
        footer.setWidget(buttonsPanel);

        add(footer, DockPanel.SOUTH);

        btnApply = new Button(i18n.tr("Apply"));
        btnApply.getElement().getStyle().setMarginLeft(1, Unit.EM);
        btnApply.getElement().getStyle().setMarginBottom(0.3, Unit.EM);
        buttonsPanel.add(btnApply);
        btnApply.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                apply();
            }
        });

        btnClose = new Button(i18n.tr("Close"));
        btnClose.getElement().getStyle().setMarginLeft(1, Unit.EM);
        btnClose.getElement().getStyle().setMarginBottom(0.3, Unit.EM);
        buttonsPanel.add(btnClose);
        btnClose.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                resetFilters();
                apply();
            }
        });

        setVisible(false);

        add(addButtonWidget, DockPanel.SOUTH);

        this.addAttachHandler(new AttachEvent.Handler() {

            private HandlerRegistration handlerRegistration;

            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
                        @Override
                        public void onPreviewNativeEvent(NativePreviewEvent event) {
                            if (event.getTypeInt() == Event.ONKEYUP && (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
                                    && FocusUtil.hasActiveElement(DataTableFilterPanel.this.getElement())) {
                                apply();
                            }
                        }
                    });
                } else {
                    handlerRegistration.removeHandler();
                }
            }
        });
    }

    private Widget createAddButton() {

        IconButton btnAdd = new IconButton(i18n.tr("Add filter..."), EntityFolderImages.INSTANCE.addButton(), new Command() {
            @Override
            public void execute() {
                grid.addFilter(new DataTableFilterItem<E>(grid));
                btnApply.setEnabled(grid.getFilterCount() > 0);
            }
        });

        HTML lblAdd = new HTML(i18n.tr("Add filter..."));

        HorizontalPanel panel = new HorizontalPanel();
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        panel.add(btnAdd);
        btnAdd.getElement().getStyle().setMarginTop(0.3, Unit.EM);

        panel.add(lblAdd);
        lblAdd.getElement().getStyle().setMarginLeft(0.5, Unit.EM);

        return panel;
    }

    public void setFilterApplyCommand(Command filterActionCommand) {
        this.filterActionCommand = filterActionCommand;
    }

    public List<Criterion> getFilters() {
        return grid.getFilters();
    }

    public void setFilters(List<Criterion> filters) {
        grid.setFilters(filters);
        setVisible(true);
        dataTablePanel.getFilterButton().setEnabled(false);
        if (filters == null || filters.size() == 0) {
            grid.addFilter(new DataTableFilterItem<E>(grid));
        }
    }

    protected void apply() {
        if (filterActionCommand != null) {
            filterActionCommand.execute();
        }
    }

    public void resetFilters() {
        grid.clear();
        setVisible(false);
        dataTablePanel.getFilterButton().setEnabled(true);
    }
}
