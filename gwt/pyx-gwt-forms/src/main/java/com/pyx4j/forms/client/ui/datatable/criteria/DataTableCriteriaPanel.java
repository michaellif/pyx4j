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
package com.pyx4j.forms.client.ui.datatable.criteria;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.gwt.commons.FocusUtil;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

public class DataTableCriteriaPanel<E extends IEntity> extends DockPanel {

    private static final I18n i18n = I18n.get(DataTableCriteriaPanel.class);

    private final DataTablePanel<E> dataTablePanel;

    private final ICriteriaForm<E> form;

    private final Button btnSearch;

    private final Button btnClear;

    private Command filterActionCommand;

    public DataTableCriteriaPanel(DataTablePanel<E> dataTablePanel, final ICriteriaForm<E> form) {
        this.dataTablePanel = dataTablePanel;
        this.form = form;

        setStyleName(DefaultDataTableTheme.StyleName.DataTableFilter.name());

        final SimplePanel formHolder = new SimplePanel();

        final SimplePanel footer = new SimplePanel();

        DataTableCriteriaHeader header = new DataTableCriteriaHeader(dataTablePanel.getImages()) {
            @Override
            protected void onCollapse() {
                super.onCollapse();
                boolean visible = !formHolder.isVisible();
                formHolder.setVisible(visible);
                footer.setVisible(visible);
                setExpanded(visible);
            }
        };

        header.setStyleName(DefaultDataTableTheme.StyleName.DataTableFilterHeader.name());
        add(header, DockPanel.NORTH);

        formHolder.setStyleName(DefaultDataTableTheme.StyleName.DataTableFilterMain.name());
        formHolder.setWidget(form);
        add(formHolder, DockPanel.CENTER);

        footer.setStyleName(DefaultDataTableTheme.StyleName.DataTableFilterFooter.name());

        HorizontalPanel buttonsPanel = new HorizontalPanel();
        footer.setWidget(buttonsPanel);

        add(footer, DockPanel.SOUTH);

        btnSearch = new Button(i18n.tr("Search"), new Command() {

            @Override
            public void execute() {
                search();
            }
        });
        btnSearch.getElement().getStyle().setMarginLeft(1, Unit.EM);
        btnSearch.getElement().getStyle().setMarginBottom(0.3, Unit.EM);
        buttonsPanel.add(btnSearch);

        btnClear = new Button(i18n.tr("Clear"), new Command() {

            @Override
            public void execute() {
                resetCriteria();
            }
        });
        btnClear.getElement().getStyle().setMarginLeft(1, Unit.EM);
        btnClear.getElement().getStyle().setMarginBottom(0.3, Unit.EM);
        buttonsPanel.add(btnClear);

        this.addAttachHandler(new AttachEvent.Handler() {

            private HandlerRegistration handlerRegistration;

            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
                        @Override
                        public void onPreviewNativeEvent(NativePreviewEvent event) {
                            if (event.getTypeInt() == Event.ONKEYUP && (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
                                    && FocusUtil.hasActiveElement(DataTableCriteriaPanel.this.getElement())) {
                                search();
                            }
                        }
                    });
                } else {
                    handlerRegistration.removeHandler();
                }
            }
        });
    }

    public void setFilterApplyCommand(Command filterActionCommand) {
        this.filterActionCommand = filterActionCommand;
    }

    public List<Criterion> getFilters() {
        return form.getFilters();
    }

    public void setFilters(List<Criterion> filters) {
        form.setFilters(filters);
    }

    protected void search() {
        if (filterActionCommand != null) {
            filterActionCommand.execute();
        }
    }

    public void resetCriteria() {
        form.resetCriteria();
        setVisible(false);
        dataTablePanel.getFilterButton().setEnabled(true);
    }

}
