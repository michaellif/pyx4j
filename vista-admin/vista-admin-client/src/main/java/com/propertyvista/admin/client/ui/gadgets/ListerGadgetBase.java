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
package com.propertyvista.admin.client.ui.gadgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberPrimitiveColumnDescriptor;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.essentials.client.crud.EntityListPanel;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translation;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.domain.dashboard.GadgetMetadata;

public abstract class ListerGadgetBase<E extends IEntity> extends GadgetBase {

    protected final EntityListPanel<E> listPanel;

    private final AbstractCrudService<E> service;

    private final Class<E> entityClass;

    public enum RefreshInterval {

        @Translation("Never")
        Never(-1),

        @Translation("15 min")
        min15L(15),

        @Translation("30 min")
        min30(30),

        @Translation("1 hours")
        hour1(60),

        @Translation("2 hours")
        hour2(120);

        RefreshInterval(int value) {
            this.value = value;
        }

        private final int value;

        public int value() {
            return value;
        }

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    };

    protected RefreshInterval refreshInterval = RefreshInterval.Never;

    public ListerGadgetBase(GadgetMetadata gmd, AbstractCrudService<E> service, Class<E> entityClass) {
        super(gmd);
        this.service = service;
        this.entityClass = entityClass;

        listPanel = new EntityListPanel<E>(entityClass) {
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

    protected void populateData(final int pageNumber) {
        EntityListCriteria<E> criteria = new EntityListCriteria<E>(entityClass);
        criteria.setPageSize(getListPanel().getPageSize());
        criteria.setPageNumber(pageNumber);

        service.list(new AsyncCallback<EntitySearchResult<E>>() {
            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(EntitySearchResult<E> result) {
                ListerGadgetBase.this.getListPanel().populateData(result.getData(), pageNumber, result.hasMoreData(), result.getTotalRows());
            }
        }, criteria);
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
        return listPanel.asWidget();
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
