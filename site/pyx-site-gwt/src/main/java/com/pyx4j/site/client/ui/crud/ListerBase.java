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
import java.text.ParseException;
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

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.DataTable;
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
import com.pyx4j.site.client.NavigationIDs;
import com.pyx4j.site.client.resources.SiteImages;
import com.pyx4j.site.client.ui.crud.FilterData.Operands;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.ImageButton;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public abstract class ListerBase<E extends IEntity> extends VerticalPanel implements IListerView<E> {

    public static String DEFAULT_STYLE_PREFIX = "vista_Lister";

    public static enum StyleSuffix implements IStyleSuffix {
        actionsPanel, filtersPanel, listPanel, newItemButton, actionButton
    }

// Events:
    public interface ItemSelectionHandler<E> {
        void onSelect(E selectedItem);
    }

    protected static I18n i18n = I18nFactory.getI18n(ListerBase.class);

    protected Button btnNewItem;

    protected final HorizontalPanel actionsPanel;

    protected final HorizontalPanel filtersPanel;

    protected final Filters filters;

    protected Button btnApply;

    protected final EntityListPanel<E> listPanel;

    protected Presenter presenter;

    private List<ItemSelectionHandler<E>> itemSelectionHandlers;

    private Class<? extends CrudAppPlace> itemOpenPlaceClass;

    private boolean openEditor;

    private List<Sort> sorting;

    public ListerBase(Class<E> clazz) {
        setStyleName(DEFAULT_STYLE_PREFIX);

        listPanel = new EntityListPanel<E>(clazz) {
            @Override
            public List<ColumnDescriptor<E>> getColumnDescriptors() {
                ArrayList<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
                ListerBase.this.fillDefaultColumnDescriptors(columnDescriptors, proto());
                assert !columnDescriptors.isEmpty() : "shouldn't be empty!..";
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
        listPanel.getDataTable().setHasCheckboxColumn(true);
        listPanel.getDataTable().addCheckSelectionHandler(new CheckSelectionHandler() {
            @Override
            public void onCheck(boolean isAnyChecked) {
                setActionsActive(isAnyChecked);
            }
        });
        listPanel.getDataTable().setHasColumnClickSorting(true);
        listPanel.getDataTable().addSortChangeHandler(new SortChangeHandler<E>() {
            @Override
            public void onChange(ColumnDescriptor<E> column) {
                sorting = new ArrayList<Sort>(1);
                sorting.add(new Sort(column.getColumnName(), !column.isSortAscending()));
                presenter.populate(getListPanel().getDataTable().getDataTableModel().getPageNumber());
            }
        });

        showColumnSelector(true);
        listPanel.removeUpperActionsBar();
        listPanel.setPageSize(ApplicationMode.isDevelopment() ? 10 : 30);
        listPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.listPanel);
        listPanel.getDataTable().setHasCheckboxColumn(false);
        listPanel.getDataTable().setMarkSelectedRow(false);
        listPanel.getDataTable().setAutoColumnsWidth(true);
        listPanel.getDataTable().renderTable();

        // actions & filters:
        actionsPanel = new HorizontalPanel();
        actionsPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.actionsPanel);
        actionsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        actionsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        actionsPanel.setWidth("100%");
        actionsPanel.add(new HTML()); // just for %-tage cells alignment...
        actionsPanel.setSpacing(4);
        actionsPanel.setVisible(false);

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

    public ListerBase(Class<E> clazz, Class<? extends CrudAppPlace> itemOpenPlaceClass) {
        this(clazz, itemOpenPlaceClass, false, true);
    }

    public ListerBase(Class<E> clazz, Class<? extends CrudAppPlace> itemOpenPlaceClass, boolean readOnly) {
        this(clazz, itemOpenPlaceClass, !readOnly, !readOnly);
    }

    public ListerBase(Class<E> clazz, Class<? extends CrudAppPlace> itemOpenPlaceClass, boolean openEditor, boolean allowAddNew) {
        this(clazz);

        this.itemOpenPlaceClass = itemOpenPlaceClass;
        this.openEditor = openEditor;

        if (itemOpenPlaceClass != null) {
            // item selection stuff:
            listPanel.getDataTable().addItemSelectionHandler(new DataTable.ItemSelectionHandler() {
                @Override
                public void onSelect(int selectedRow) {
                    E item = getListPanel().getDataTable().getSelectedItem();
                    if (item != null) {
                        onItemSelect(item);
                    }
                }
            });

            // new item stuff:
            if (allowAddNew) {
                actionsPanel.setVisible(true);
                actionsPanel.add(btnNewItem = new Button(i18n.tr("Add")));
                actionsPanel.setCellWidth(btnNewItem, "1%");
                btnNewItem.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        onItemNew();
                    }
                });
                btnNewItem.ensureDebugId(new CompositeDebugId(NavigationIDs.Navigation_Button, NavigationIDs.ItemDescriptionIDs.Add_New_Item).toString());
                btnNewItem.addStyleName(btnNewItem.getStylePrimaryName() + StyleSuffix.newItemButton);
            }
        }
    }

    public void addActionButton(Widget action) {
        actionsPanel.setVisible(true);
        actionsPanel.insert(action, 1);
        actionsPanel.setCellWidth(action, "1%");
    }

    public void setFiltersVisible(boolean visible) {
        filtersPanel.setVisible(visible);
    }

    public void showColumnSelector(boolean show) {
        if (show) {
            ArrayList<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
            fillAvailableColumnDescriptors(columnDescriptors, listPanel.proto());
            listPanel.getDataTable().setColumnSelector(columnDescriptors);
        } else {
            listPanel.getDataTable().setColumnSelector(null);
        }
    }

    public boolean isMultiSelect() {
        return listPanel.getDataTable().isMultiSelect();
    }

    public void setMultiSelect(boolean isMultiSelect) {
        listPanel.getDataTable().setMultiSelect(isMultiSelect);
    }

    public void releaseSelection() {
        listPanel.getDataTable().releaseSelection();
    }

    public E getSelectedItem() {
        return listPanel.getDataTable().getSelectedItem();
    }

    public List<E> getSelectedItems() {
        return listPanel.getDataTable().getSelectedItems();
    }

    public void setSelectedItem(E item) {
        // TODO - implementation here...
    }

    public void addItemSelectionHandler(ItemSelectionHandler<E> handler) {
        if (itemSelectionHandlers == null) {
            itemSelectionHandlers = new ArrayList<ItemSelectionHandler<E>>(2);
        }

        itemSelectionHandlers.add(handler);
        listPanel.getDataTable().addItemSelectionHandler(new DataTable.ItemSelectionHandler() {
            @Override
            public void onSelect(int selectedRow) {
                if (itemSelectionHandlers != null) {
                    for (ItemSelectionHandler<E> handler : itemSelectionHandlers) {
                        handler.onSelect(listPanel.getDataTable().getSelectedItem());
                    }
                }
            }
        });
    }

    // EntityListPanel access:
    protected EntityListPanel<E> getListPanel() {
        return listPanel;
    }

    /**
     * Implement in derived class to set default table columns set.
     * Note, that it's called from within constructor!
     */
    protected abstract void fillDefaultColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors, E proto);

    /**
     * Override in derived class to set available table columns set.
     * Note, that it's called from within constructor!
     */
    protected void fillAvailableColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors, E proto) {
        columnDescriptors.addAll(listPanel.getDataTable().getDataTableModel().getColumnDescriptors());
    }

    // Actions:
    /**
     * Override in derived class for your own select item procedure.
     */
    protected void onItemSelect(E item) {
        if (itemOpenPlaceClass != null && openEditor) {
            presenter.edit(itemOpenPlaceClass, item.getPrimaryKey());
        } else {
            presenter.view(itemOpenPlaceClass, item.getPrimaryKey());
        }
    }

    /**
     * Override in derived class for your own new item creation procedure.
     */
    protected void onItemNew() {
        presenter.editNew(itemOpenPlaceClass, null);
    }

// IListerView implementation:

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Presenter getPresenter() {
        return presenter;
    }

    @Override
    public ListerBase<E> getLister() {
        return this;
    }

    @Override
    public int getPageSize() {
        return getListPanel().getPageSize();
    }

    @Override
    public int getPageNumber() {
        return getListPanel().getPageNumber();
    }

    @Override
    public void populate(List<E> entityes, int pageNumber, boolean hasMoreData, int totalRows) {
        setActionsActive(false);
        getListPanel().populateData(entityes, pageNumber, hasMoreData, totalRows);
    }

    @Override
    public List<FilterData> getFiltering() {
        return filters.getFiltersData();
    }

    @Override
    public List<Sort> getSorting() {
        return sorting;
    }

    /**
     * Override in derived class to fill pages with data.
     */
    protected void onPrevPage() {
        presenter.populate(getListPanel().getDataTable().getDataTableModel().getPageNumber() - 1);
    }

    protected void onNextPage() {
        presenter.populate(getListPanel().getDataTable().getDataTableModel().getPageNumber() + 1);
    }

    private void setActionsActive(boolean active) {
        for (Widget w : actionsPanel) {
            if (!w.equals(btnNewItem) && w instanceof FocusWidget) {
                ((FocusWidget) w).setEnabled(active);
            }
        }
    }

// Internals: ------------------------------------------------------------------------------------------   

    private Widget createAddApplyPanel() {

        ImageButton btnAdd = new ImageButton(SiteImages.INSTANCE.add(), SiteImages.INSTANCE.addHover());
        btnAdd.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                filters.addFilter();
                btnApply.setEnabled(filters.getFilterCount() > 0);
            }
        });
        HTML lblAdd = new HTML(i18n.tr("Add filter..."));

        btnApply = new Button(i18n.tr("Apply"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.populate(0);
            }
        });
        btnApply.setEnabled(false);

        HorizontalPanel panel = new HorizontalPanel();
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        panel.add(btnAdd);
        btnAdd.getElement().getStyle().setMarginTop(0.3, Unit.EM);

        panel.add(lblAdd);
        lblAdd.getElement().getStyle().setMarginLeft(0.5, Unit.EM);

        panel.add(btnApply);
        btnApply.getElement().getStyle().setMarginLeft(1, Unit.EM);
        btnApply.getElement().getStyle().setMarginBottom(0.3, Unit.EM);

        return panel;
    }

    // ------------------------------------------------

    protected class Filters extends FlowPanel {

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
                            presenter.populate(0);
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
                    setValueHolder(fieldsList.getValue().getPath());
                } else {
                    valueHolder.setWidget(new CTextField());
                }
                fieldsList.addValueChangeHandler(new ValueChangeHandler<FieldData>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<FieldData> event) {
                        setValueHolder(event.getValue().getPath());
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
                try {
                    if (((INativeEditableComponent<?>) valueHolder.getWidget()).getNativeValue() != null) {
                        value = (Serializable) ((INativeEditableComponent<?>) valueHolder.getWidget()).getNativeValue();
                    }
                } catch (ParseException e) {
                    // Do nothing here..
                }

                return new FilterData(path, operand, value);
            }

            private void formatCell(Widget w) {
                Element cell = DOM.getParent(w.getElement());
                cell.getStyle().setPaddingRight(1.5, Unit.EM);
            }

            @SuppressWarnings({ "unchecked", "rawtypes" })
            private void setValueHolder(String valuePath) {
                operandsList.setOptions(EnumSet.allOf(Operands.class));
                operandsList.setValue(Operands.is);

                Class<?> valueClass = getListPanel().proto().getMember(new Path(valuePath)).getValueClass();
                if (valueClass.isEnum()) {
                    CComboBox valuesList = new CComboBox(true);
                    valuesList.setOptions(EnumSet.allOf((Class<Enum>) valueClass));
                    valueHolder.setWidget(valuesList);

                    // correct operands list:
                    operandsList.removeOption(Operands.greaterThen);
                    operandsList.removeOption(Operands.lessThen);

                } else if (valueClass.equals(LogicalDate.class)) {
                    valueHolder.setWidget(new CDatePicker());
                } else if (valueClass.equals(Boolean.class)) {
                    valueHolder.setWidget(new CRadioGroupBoolean(CRadioGroup.Layout.HORISONTAL));

                    // correct operands list:
                    operandsList.removeOption(Operands.greaterThen);
                    operandsList.removeOption(Operands.lessThen);

                } else if (valueClass.equals(Double.class)) {
                    valueHolder.setWidget(new CDoubleField());
                } else if (valueClass.equals(Integer.class)) {
                    valueHolder.setWidget(new CLongField());
                } else if (valueClass.equals(Integer.class)) {
                    valueHolder.setWidget(new CIntegerField());
                } else {
                    valueHolder.setWidget(new CTextField());

                    // correct operands list:
                    operandsList.removeOption(Operands.greaterThen);
                    operandsList.removeOption(Operands.lessThen);
                }
            }
        }
    }
}
