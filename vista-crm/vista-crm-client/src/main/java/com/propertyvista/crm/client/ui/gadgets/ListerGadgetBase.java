/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberPrimitiveColumnDescriptor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.essentials.client.crud.EntityListPanel;

import com.propertyvista.crm.rpc.domain.GadgetMetadata;

public abstract class ListerGadgetBase<E extends IEntity> extends GadgetBase {

    protected final EntityListPanel<E> listPanel;

    protected enum RefreshInterval {
        Never(-1, "Never"), min15L(15, "15 min"), min30(30, "30 min"), hour1(60, "1 hour"), hour2(120, "2 hours");

        RefreshInterval(int value, String name) {
            this.value = value;
            this.name = name;
        }

        private final int value;

        private final String name;

        public int value() {
            return value;
        }

        @Override
        public String toString() {
            return name;
        }
    };

    protected RefreshInterval refreshInterval = RefreshInterval.Never;

    public ListerGadgetBase(GadgetMetadata gmd, Class<E> clazz) {
        super(gmd);

        listPanel = new EntityListPanel<E>(clazz) {
            @Override
            public List<ColumnDescriptor<E>> getColumnDescriptors() {
                ArrayList<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
                ListerGadgetBase.this.fillDefaultColumnDescriptors(columnDescriptors, proto());
                return columnDescriptors;
            }
        };

        listPanel.setPageSize(10);

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

    protected RefreshInterval getRefreshInterval() {
        return refreshInterval;
    }

    protected void setRefreshInterval(RefreshInterval refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    //
    // IGadget:
    @Override
    public Widget asWidget() {
        ScrollPanel scroll = new ScrollPanel(listPanel.asWidget());
//        scroll.setWidth("100%");
        return scroll;
    }

    @Override
    public void start() {
        super.start();
        populateData(0);
    }

    @Override
    public boolean isSetupable() {
        return true;
    }

    @Override
    public ISetup getSetup() {
        return new SetupLister();
    }

    //
    // Setup UI implementation:
    class SetupLister implements ISetup {

        protected final HorizontalPanel setupPanel = new HorizontalPanel();

        protected final ListBox columnsList = new ListBox(true);

        protected final TextBox itemsPerPage = new TextBox();

        protected final ListBox intervalList = new ListBox(false);

        protected SetupLister() {
            super();

            columnsList.addItem(i18n.tr("Default Set"));
            columnsList.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    if (columnsList.getSelectedIndex() == 0) {
                        for (int i = 1; i < columnsList.getItemCount(); ++i) {
                            columnsList.setItemSelected(i, false);
                        }
                    }
                }
            });

            for (String name : getListPanel().proto().getEntityMeta().getMemberNames()) {
                MemberMeta meta = getListPanel().proto().getEntityMeta().getMemberMeta(name);
                if (meta.getObjectClassType() == ObjectClassType.Primitive) {
                    columnsList.addItem(meta.getCaption());
                    columnsList.setValue(columnsList.getItemCount() - 1, name);
                }
            }

            columnsList.setVisibleItemCount(8);

            FlowPanel columns = new FlowPanel();
            columns.add(new Label(i18n.tr("Select columns to display:")));
            columns.add(columnsList);
            columnsList.setWidth("100%");

            VerticalPanel addition = new VerticalPanel();

            addition.add(new HTML("&nbsp"));
            HorizontalPanel items = new HorizontalPanel();
            items.add(new HTML(i18n.tr("Items per page:")));

            itemsPerPage.setText(String.valueOf(listPanel.getPageSize()));
            itemsPerPage.setWidth("2em");
            items.add(itemsPerPage);
            items.setCellHorizontalAlignment(itemsPerPage, HasHorizontalAlignment.ALIGN_RIGHT);

            items.setSpacing(4);
            items.setWidth("100%");
            addition.add(items);

            HorizontalPanel refresh = new HorizontalPanel();
            refresh.add(new Label(i18n.tr("Refresh interval:")));

            for (RefreshInterval i : RefreshInterval.values()) {
                intervalList.addItem(i.toString());
                intervalList.setValue(intervalList.getItemCount() - 1, i.name());
                if (getRefreshInterval() == i) {
                    intervalList.setSelectedIndex(intervalList.getItemCount() - 1);
                }
            }
            refresh.add(intervalList);
            refresh.setCellHorizontalAlignment(intervalList, HasHorizontalAlignment.ALIGN_RIGHT);

            refresh.setSpacing(4);
            refresh.setWidth("100%");
            addition.add(refresh);
            addition.getElement().getStyle().setPaddingLeft(10, Unit.PX);

            setupPanel.add(columns);
//            setupPanel.setCellWidth(columns, "33%");
            setupPanel.add(addition);
            setupPanel.getElement().getStyle().setPadding(3, Unit.PX);
//            setupPanel.setWidth("100%");
        }

        @Override
        public Widget asWidget() {
            return setupPanel;
        }

        @Override
        public boolean onStart() {
            suspend();
            return true;
        }

        @Override
        public boolean onOk() {
            ArrayList<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
            for (int i = 0; i < columnsList.getItemCount(); ++i) {
                if (columnsList.isItemSelected(i)) {
                    if (i == 0) {
                        ListerGadgetBase.this.fillDefaultColumnDescriptors(columnDescriptors, getListPanel().proto());
                    } else {
                        columnDescriptors.add(new MemberPrimitiveColumnDescriptor<E>(getListPanel().proto().getMember(columnsList.getValue(i)).getPath(),
                                columnsList.getItemText(i)));
                    }
                }
            }

            listPanel.setPageSize(Integer.parseInt(itemsPerPage.getText()));

            if (intervalList.getSelectedIndex() > 0) {
                setRefreshInterval(RefreshInterval.valueOf(intervalList.getValue(intervalList.getSelectedIndex())));
            }

            if (!columnDescriptors.isEmpty()) {
                getListPanel().getDataTable().getDataTableModel().setColumnDescriptors(columnDescriptors);
            }

            // restart the gadget:
            stop();
            start();
            return true;
        }

        @Override
        public void onCancel() {
            resume();
        }
    }
}
