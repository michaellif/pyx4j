/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.client.crud.EntityListPanel;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.ImageButton;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.crud.FilterData.Operands;
import com.propertyvista.crm.rpc.CrudAppPlace;

public abstract class ListerBase<E extends IEntity> extends VerticalPanel implements IListerView<E> {

    private static I18n i18n = I18nFactory.getI18n(ListerBase.class);

    protected final Filters filters;

    protected Button btnApply;

    protected final EntityListPanel<E> listPanel;

    protected Presenter presenter;

    public ListerBase(Class<E> clazz) {

        listPanel = new EntityListPanel<E>(clazz) {
            @Override
            public List<ColumnDescriptor<E>> getColumnDescriptors() {
                ArrayList<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
                ListerBase.this.fillDefaultColumnDescriptors(columnDescriptors, proto());
                return columnDescriptors;
            }
        };

        listPanel.setPageSize(ApplicationMode.isDevelopment() ? 10 : 30);

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

        listPanel.removeUpperActionsBar();
        DOM.setStyleAttribute(listPanel.getDataTable().getElement(), "tableLayout", "auto");

        // -------------------------

        HorizontalPanel functional = new HorizontalPanel();
        functional.add(filters = new Filters());

        Widget widgetAddApply = createAddApplyPanel();
        functional.add(widgetAddApply);
        functional.setCellWidth(widgetAddApply, "25%");
        functional.setCellVerticalAlignment(widgetAddApply, HasVerticalAlignment.ALIGN_BOTTOM);
        functional.setWidth("100%");

        // put UI bricks together:
        add(functional);
        add(listPanel);
        setWidth("100%");
        getElement().getStyle().setMarginTop(0.5, Unit.EM);
        getElement().getStyle().setMarginBottom(0.5, Unit.EM);
    }

    public ListerBase(Class<E> clazz, final Class<? extends CrudAppPlace> link) {
        this(clazz);

        // add editing on double-click: 
        getListPanel().getDataTable().addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                // put selected item ID in link arguments:
                DataTable<E> dt = getListPanel().getDataTable();
                int selectedRow = dt.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < dt.getDataTableModel().getData().size()) {
                    E item = dt.getDataTableModel().getData().get(selectedRow).getEntity();
                    AppSite.getPlaceController().goTo(CrudAppPlace.formViewerPlace(AppSite.getHistoryMapper().createPlace(link), item.getPrimaryKey()));
                }
            }
        });
    }

    private Widget createAddApplyPanel() {

        Image btnAdd = new ImageButton(CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover());
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
                presenter.applyFiletering(filters.getFiltersData());
            }
        });
        btnApply.setEnabled(false);

        HorizontalPanel addWrap = new HorizontalPanel();
        addWrap.add(btnAdd);
        addWrap.setCellVerticalAlignment(btnAdd, HasVerticalAlignment.ALIGN_BOTTOM);
        HTML lblAdd = new HTML(i18n.tr("Add filter..."));
        lblAdd.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        addWrap.add(lblAdd);
        addWrap.setCellVerticalAlignment(lblAdd, HasVerticalAlignment.ALIGN_MIDDLE);

        HorizontalPanel panel = new HorizontalPanel();
        panel.add(addWrap);
        panel.add(btnApply);
        panel.setCellHorizontalAlignment(btnApply, HasHorizontalAlignment.ALIGN_RIGHT);
        btnApply.getElement().getStyle().setMarginRight(1, Unit.EM);
        panel.setWidth("100%");
        return panel;
    }

    // EntityListPanel access:
    protected EntityListPanel<E> getListPanel() {
        return listPanel;
    }

    /*
     * Implement in derived class to set default table structure.
     * Note, that it's called from within constructor!
     */
    protected abstract void fillDefaultColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors, E proto);

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

            protected final ListBox fieldsList = new ListBox();

            protected final ListBox operandsList = new ListBox();

            protected final ListBox valuesList = new ListBox();

            protected final TextBox valueText = new TextBox();

            Filter() {
                Image btnDel = new ImageButton(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr("Remove filter"));
                btnDel.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Filters.this.remove(Filter.this);
                        btnApply.setEnabled(getFilterCount() > 0);
                    }
                });

                SimplePanel wrap = new SimplePanel();
                wrap.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                wrap.getElement().getStyle().setPaddingLeft(1.3, Unit.EM);
                wrap.setWidget(btnDel);
                add(wrap);
                formatCell(wrap);

                for (ColumnDescriptor<E> cd : getListPanel().getDataTable().getDataTableModel().getColumnDescriptors()) {
                    fieldsList.addItem(cd.getColumnTitle());
                    fieldsList.setValue(fieldsList.getItemCount() - 1, cd.getColumnName());
                }
                fieldsList.setWidth("100%");
                add(fieldsList);
                setCellWidth(fieldsList, "40%");
                formatCell(fieldsList);

                for (Operands op : Operands.values()) {
                    operandsList.addItem(op.toString());
                    operandsList.setValue(operandsList.getItemCount() - 1, op.name());
                }
                operandsList.setWidth("100%");
                add(operandsList);
                setCellWidth(operandsList, "20%");
                formatCell(operandsList);

                valueText.setWidth("100%");
                add(valueText);
                setCellWidth(valueText, "40%");
                formatCell(valueText);

                setWidth("100%");
            }

            public FilterData getFilterData() {
                String path = fieldsList.getValue(fieldsList.getSelectedIndex());
                Operands operand = Operands.valueOf(operandsList.getValue(operandsList.getSelectedIndex()));
                return new FilterData(path, operand, valueText.getText());
            }

            private void formatCell(Widget w) {
                Element cell = DOM.getParent(w.getElement());
                cell.getStyle().setPaddingRight(1.5, Unit.EM);
            }
        }
    }
}
