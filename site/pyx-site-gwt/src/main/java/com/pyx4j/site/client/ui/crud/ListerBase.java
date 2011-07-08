/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.DataTable.CheckSelectionHandler;
import com.pyx4j.entity.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.essentials.client.crud.EntityListPanel;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CDoubleField;
import com.pyx4j.forms.client.ui.CIntegerField;
import com.pyx4j.forms.client.ui.CLongField;
import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupBoolean;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.INativeEditableComponent;
import com.pyx4j.site.client.resources.SiteImages;
import com.pyx4j.site.client.ui.crud.FilterData.Operands;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.ImageButton;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public abstract class ListerBase<E extends IEntity> extends VerticalPanel implements IListerView<E> {

    public static String DEFAULT_STYLE_PREFIX = "vista_Lister";

    public static enum StyleSuffix implements IStyleSuffix {
        actionsPanel, filtersPanel, listPanel
    }

    private static I18n i18n = I18nFactory.getI18n(ListerBase.class);

    protected Button btnNewItem;

    protected final HorizontalPanel actionsPanel;

    protected final HorizontalPanel filtersPanel;

    protected final Filters filters;

    protected Button btnApply;

    protected final EntityListPanel<E> listPanel;

    protected Presenter presenter;

    public ListerBase(Class<E> clazz) {
        setStyleName(DEFAULT_STYLE_PREFIX);

        listPanel = new EntityListPanel<E>(clazz) {
            @Override
            public List<ColumnDescriptor<E>> getColumnDescriptors() {
                ArrayList<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
                ListerBase.this.fillDefaultColumnDescriptors(columnDescriptors, proto());
                return columnDescriptors;
            }
        };
        listPanel.setPrevActionHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onPrevPage();
            }
        });
        listPanel.setNextActionHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onNextPage();
            }
        });
        listPanel.getDataTable().setCheckboxColumnShown(true);
        listPanel.getDataTable().addCheckSelectionHandler(new CheckSelectionHandler() {
            @Override
            public void onCheck(boolean isAnyChecked) {
                setActionsActive(isAnyChecked);
            }
        });
        listPanel.getDataTable().setColumnClickSorting(true);
        listPanel.getDataTable().addSortChangeHandler(new SortChangeHandler<E>() {
            @Override
            public void onChange(ColumnDescriptor<E> column) {
                List<Sort> sorts = new ArrayList<Sort>(1);
                sorts.add(new Sort(column.getColumnName(), !column.isSortAscending()));
                presenter.applySorting(sorts);
            }
        });

        showColumnSelector(true);
        listPanel.removeUpperActionsBar();
        listPanel.setPageSize(ApplicationMode.isDevelopment() ? 10 : 30);
        listPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.listPanel);
        listPanel.getDataTable().setAutoColumnsWidth(true);
        listPanel.getDataTable().renderTable();

        // actions & filters:
        actionsPanel = new HorizontalPanel();
        actionsPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.actionsPanel);
        actionsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        actionsPanel.setWidth("100%");
        actionsPanel.add(new HTML()); // just for %-tage cells alignment...

        filtersPanel = new HorizontalPanel();
        filtersPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.filtersPanel);
        filtersPanel.add(filters = new Filters());

        Widget widgetAddApply = createAddApplyPanel();
        filtersPanel.add(widgetAddApply);
        filtersPanel.setCellWidth(widgetAddApply, "25%");
        filtersPanel.setCellVerticalAlignment(widgetAddApply, HasVerticalAlignment.ALIGN_BOTTOM);
        filtersPanel.setWidth("100%");

        // put UI bricks together:
        add(actionsPanel);
        add(filtersPanel);
        add(listPanel);
        setWidth("100%");
    }

    public ListerBase(Class<E> clazz, final Class<? extends CrudAppPlace> itemOpenPlaceClass) {
        this(clazz, itemOpenPlaceClass, false, true);
    }

    public ListerBase(Class<E> clazz, final Class<? extends CrudAppPlace> itemOpenPlaceClass, boolean readOnly) {
        this(clazz, itemOpenPlaceClass, !readOnly, !readOnly);
    }

    public ListerBase(Class<E> clazz, final Class<? extends CrudAppPlace> itemOpenPlaceClass, final boolean openEditor, boolean allowAddNew) {
        this(clazz);

        getListPanel().getDataTable().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (getListPanel().getDataTable().getSelectedRow() >= 0) {
                    E item = getListPanel().getDataTable().getSelectedItem();
                    if (item != null) {
                        if (openEditor) {
                            presenter.edit(itemOpenPlaceClass, item.getPrimaryKey());
                        } else {
                            presenter.view(itemOpenPlaceClass, item.getPrimaryKey());
                        }
                    }
                }
            }
        });

// new item button stuff:
        if (allowAddNew) {
            actionsPanel.add(btnNewItem = new Button(i18n.tr("Add&nbspnew&nbspitem...")));
            actionsPanel.setCellWidth(btnNewItem, "1%");
            btnNewItem.getElement().getStyle().setMarginRight(1, Unit.EM);
            btnNewItem.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.editNew(itemOpenPlaceClass, null);
                }
            });
        }
    }

    protected void addActionButton(Button action) {
        actionsPanel.insert(action, 1);
        actionsPanel.setCellWidth(action, "1%");
        action.getElement().getStyle().setMarginRight(1, Unit.EM);
    }

    public void setFiltersVisible(boolean visible) {
        filtersPanel.setVisible(visible);
    }

    public void showColumnSelector(boolean show) {
        if (show) {
            ArrayList<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
            fillAvailableColumnDescriptors(columnDescriptors, listPanel.proto());
            listPanel.getDataTable().setUseColumnSelector(columnDescriptors);
        } else {
            listPanel.getDataTable().setUseColumnSelector(null);
        }
    }

    private Widget createAddApplyPanel() {

        ImageButton btnAdd = new ImageButton(SiteImages.INSTANCE.add(), SiteImages.INSTANCE.addHover());
        btnAdd.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                filters.addFilter();
                btnApply.setEnabled(filters.getFilterCount() > 0);
            }
        });

        btnApply = new Button(i18n.tr("Apply"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.applyFiltering(filters.getFiltersData());
            }
        });
        btnApply.setEnabled(false);

        HorizontalPanel addWrap = new HorizontalPanel();
        addWrap.add(btnAdd);
        btnAdd.getElement().getStyle().setMarginTop(0.3, Unit.EM);
        addWrap.setCellVerticalAlignment(btnAdd, HasVerticalAlignment.ALIGN_MIDDLE);
        HTML lblAdd = new HTML(i18n.tr("Add filter..."));
        lblAdd.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        addWrap.add(lblAdd);
        addWrap.setCellVerticalAlignment(lblAdd, HasVerticalAlignment.ALIGN_MIDDLE);

        HorizontalPanel panel = new HorizontalPanel();
        panel.add(addWrap);
        panel.add(btnApply);
        panel.setCellHorizontalAlignment(btnApply, HasHorizontalAlignment.ALIGN_RIGHT);
        btnApply.getElement().getStyle().setMarginRight(1, Unit.EM);
        btnApply.getElement().getStyle().setMarginBottom(0.5, Unit.EM);
        panel.setWidth("100%");
        return panel;
    }

    // EntityListPanel access:
    protected EntityListPanel<E> getListPanel() {
        return listPanel;
    }

    /*
     * Implement in derived class to set default table columns set.
     * Note, that it's called from within constructor!
     */
    protected abstract void fillDefaultColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors, E proto);

    /*
     * Override in derived class to set available table columns set.
     * Note, that it's called from within constructor!
     */
    protected void fillAvailableColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors, E proto) {
        columnDescriptors.addAll(listPanel.getDataTable().getDataTableModel().getColumnDescriptors());
    }

    // IListerView implementation:

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public int getPageSize() {
        return getListPanel().getPageSize();
    }

    @Override
    public void populateData(List<E> entityes, int pageNumber, boolean hasMoreData) {
        setActionsActive(false);
        getListPanel().populateData(entityes, pageNumber, hasMoreData);
    }

    protected void populateData(int pageNumber) {
        if (presenter != null) {
            presenter.populateData(pageNumber);
        }
    }

    /*
     * Override in derived class to fill pages with data.
     */
    protected void onPrevPage() {
        populateData(getListPanel().getDataTable().getDataTableModel().getPageNumber() - 1);
    }

    protected void onNextPage() {
        populateData(getListPanel().getDataTable().getDataTableModel().getPageNumber() + 1);
    }

    private void setActionsActive(boolean active) {
        for (Widget w : actionsPanel) {
            if (!w.equals(btnNewItem) && w instanceof FocusWidget) {
                ((FocusWidget) w).setEnabled(active);
            }
        }
    }

    // ------------------------------------------------

    class Filters extends FlowPanel {

        public Filters() {
            setWidth("100%");
        }

        public void addFilter() {
            add(new Filter());
        }

        public int getFilterCount() {
            return getWidgetCount();
        }

        @SuppressWarnings("unchecked")
        public List<FilterData> getFiltersData() {
            ArrayList<FilterData> filters = new ArrayList<FilterData>();

            for (Widget w : this) {
                if (w instanceof ListerBase.Filters.Filter) {
                    filters.add(((Filter) w).getFilterData());
                }
            }

            return filters;
        }

        private class Filter extends HorizontalPanel {

            protected final CComboBox<FieldData> fieldsList = new CComboBox<FieldData>(true);

            protected final CComboBox<Operands> operandsList = new CComboBox<Operands>(true);

            protected final SimplePanel valueHolder = new SimplePanel();

            private class FieldData {
                private final ColumnDescriptor<E> cd;

                public FieldData(ColumnDescriptor<E> cd) {
                    this.cd = cd;
                }

                public String getPath() {
                    return cd.getColumnName();
                }

                @Override
                public String toString() {
                    return cd.getColumnTitle();
                }
            }

            Filter() {
                Image btnDel = new ImageButton(SiteImages.INSTANCE.del(), SiteImages.INSTANCE.delHover(), i18n.tr("Remove filter"));
                btnDel.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Filters.this.remove(Filter.this);
                        if (filters.getFilterCount() == 0) {
                            btnApply.setEnabled(false);
                            presenter.applyFiltering(filters.getFiltersData());
                        }
                    }
                });

                SimplePanel wrap = new SimplePanel();
                wrap.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                wrap.getElement().getStyle().setPaddingLeft(1.3, Unit.EM);
                wrap.setWidget(btnDel);
                add(wrap);
                formatCell(wrap);

                Collection<FieldData> fds = new ArrayList<FieldData>();
                for (ColumnDescriptor<E> cd : getListPanel().getDataTable().getDataTableModel().getColumnDescriptors()) {
                    fds.add(new FieldData(cd));
                }

                fieldsList.setOptions(fds);
                if (!fds.isEmpty()) {
                    fieldsList.setValue(fds.iterator().next());
                }
                fieldsList.addValueChangeHandler(new ValueChangeHandler<FieldData>() {
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    public void onValueChange(ValueChangeEvent<FieldData> event) {
                        String path = event.getValue().getPath();
                        Class<?> valueClass = getListPanel().proto().getMember(new Path(path)).getValueClass();
                        if (valueClass.isEnum()) {
                            CComboBox valuesList = new CComboBox(true);
                            valuesList.setOptions(EnumSet.allOf((Class<Enum>) valueClass));
                            valueHolder.setWidget(valuesList);
                        } else if (valueClass.equals(LogicalDate.class)) {
                            valueHolder.setWidget(new CDatePicker());
                        } else if (valueClass.equals(Boolean.class)) {
                            valueHolder.setWidget(new CRadioGroupBoolean(CRadioGroup.Layout.HORISONTAL));
                        } else if (valueClass.equals(Double.class)) {
                            valueHolder.setWidget(new CDoubleField());
                        } else if (valueClass.equals(Integer.class)) {
                            valueHolder.setWidget(new CLongField());
                        } else if (valueClass.equals(Integer.class)) {
                            valueHolder.setWidget(new CIntegerField());
                        } else {
                            valueHolder.setWidget(new CTextField());
                        }
                    }
                });
                fieldsList.setWidth("100%");

                add(fieldsList);
                setCellWidth(fieldsList, "40%");
                formatCell(fieldsList.asWidget());

                operandsList.setOptions(EnumSet.allOf(Operands.class));
                operandsList.setValue(Operands.is);
                operandsList.setWidth("100%");

                add(operandsList);
                setCellWidth(operandsList, "20%");
                formatCell(operandsList.asWidget());

                valueHolder.setWidget(new CTextField());
                valueHolder.setWidth("100%");

                add(valueHolder);
                setCellWidth(valueHolder, "40%");
                formatCell(valueHolder);

                setWidth("100%");
            }

            public FilterData getFilterData() {
                String path = null;
                if (fieldsList.getValue() != null) {
                    path = fieldsList.getValue().getPath();
                }

                Operands operand = operandsList.getValue();

                Serializable value = null;
                if (((INativeEditableComponent<?>) valueHolder.getWidget()).getNativeValue() != null) {
                    value = (Serializable) ((INativeEditableComponent<?>) valueHolder.getWidget()).getNativeValue();
                }

                return new FilterData(path, operand, value);
            }

            private void formatCell(Widget w) {
                Element cell = DOM.getParent(w.getElement());
                cell.getStyle().setPaddingRight(1.5, Unit.EM);
            }
        }
    }
}
