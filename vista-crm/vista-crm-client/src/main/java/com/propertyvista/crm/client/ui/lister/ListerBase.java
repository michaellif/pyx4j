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
package com.propertyvista.crm.client.ui.lister;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.client.crud.EntityListPanel;

import com.propertyvista.crm.client.resources.CrmImages;

public abstract class ListerBase<E extends IEntity> extends VerticalPanel {

    protected final Filters filters;

    protected final EntityListPanel<E> listPanel;

    public ListerBase(Class<E> clazz) {

        listPanel = new EntityListPanel<E>(clazz) {
            @Override
            public List<ColumnDescriptor<E>> getColumnDescriptors() {
                ArrayList<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
                ListerBase.this.fillDefaultColumnDescriptors(columnDescriptors, proto());
                return columnDescriptors;
            }
        };

        listPanel.setPageSize(20);

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

        // -------------------------

        filters = new Filters(listPanel);

        Button apply = new Button("Apply", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                filters.getFiltersData();
                // TODO : apply filters here!..
            }
        });

        // put UI bricks together:
        add(filters);
        add(apply);
        Element cell = DOM.getParent(apply.getElement());
        cell.getStyle().setPaddingTop(7, Unit.PX);
        cell.getStyle().setProperty("borderTop", "2px dotted #bbb");
        cell.getStyle().setPaddingBottom(3, Unit.PX);

        apply.getElement().getStyle().setMarginRight(1, Unit.EM);
        setCellHorizontalAlignment(apply, HasHorizontalAlignment.ALIGN_RIGHT);
        add(listPanel);
        setWidth("100%");
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

    /*
     * Implement in derived class to fill table with data.
     */
    public abstract void populateData(int pageNumber);

    /*
     * Override in derived class to fill pages with data.
     */
    protected void onPrevPage() {
        populateData(getListPanel().getDataTable().getDataTableModel().getPageNumber() - 1);
    }

    protected void onNextPage() {
        populateData(getListPanel().getDataTable().getDataTableModel().getPageNumber() + 1);
    }

    // -------------------------
    // Filter Data stuff:

    public enum Operands {
        Is, IsNot, Contains, DoesNotcontain, BeginsWith, EndsWith, LessThen, GreaterThen
    }

    public static class FilterData {
        // TODO: formalise filter data here!..
    }

    // -------------------------

    class Filters extends VerticalPanel {

        protected final EntityListPanel<E> listPanel;

        public Filters(EntityListPanel<E> listPanel) {
            this.listPanel = listPanel;

            add(new Filter());
            setWidth("100%");
        }

        public List<FilterData> getFiltersData() {
            // TODO: compile filter data here!..
            return new ArrayList<FilterData>();
        }

        private class Filter extends HorizontalPanel {

            protected final ListBox fieldsList = new ListBox();

            protected final ListBox operandsList = new ListBox();

            protected final ListBox valuesList = new ListBox();

            protected final TextBox valuesText = new TextBox();

            Filter() {

                for (ColumnDescriptor<E> cd : listPanel.getDataTable().getDataTableModel().getColumnDescriptors()) {
                    fieldsList.addItem(cd.getColumnTitle());
                    fieldsList.setValue(fieldsList.getItemCount() - 1, cd.getColumnName());
                }
                add(fieldsList);

                for (Operands op : Operands.values()) {
                    operandsList.addItem(op.name());
                }
                add(operandsList);

                add(valuesText);

                final Image btnAdd = new Image(CrmImages.INSTANCE.add());
                btnAdd.getElement().getStyle().setCursor(Cursor.POINTER);
                btnAdd.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Filters.this.add(new Filter());
                    }
                });
                btnAdd.addMouseOverHandler(new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        btnAdd.setResource(CrmImages.INSTANCE.addHover());
                    }
                });
                btnAdd.addMouseOutHandler(new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        btnAdd.setResource(CrmImages.INSTANCE.add());
                    }
                });
                final Image btnDel = new Image(CrmImages.INSTANCE.del());
                btnDel.getElement().getStyle().setCursor(Cursor.POINTER);
                btnDel.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Filters.this.remove(Filter.this);
                    }
                });
                btnDel.addMouseOverHandler(new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        btnDel.setResource(CrmImages.INSTANCE.delHover());
                    }
                });
                btnDel.addMouseOutHandler(new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        btnDel.setResource(CrmImages.INSTANCE.del());
                    }
                });

                HorizontalPanel buttons = new HorizontalPanel();
                buttons.add(btnAdd);
                buttons.add(btnDel);
                buttons.setSpacing(3);
                add(buttons);
                buttons.getElement().getStyle().setMarginRight(1, Unit.EM);
                setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_RIGHT);

                setWidth("100%");
            }
        }
    }
}
