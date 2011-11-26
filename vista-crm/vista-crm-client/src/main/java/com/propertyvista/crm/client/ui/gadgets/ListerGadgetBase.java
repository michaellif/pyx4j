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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.entity.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.entity.client.ui.datatable.DataTablePanel;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.AbstractGadgetSettings;
import com.propertyvista.domain.dashboard.gadgets.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.ListerGadgetBaseSettings;
import com.propertyvista.domain.dashboard.gadgets.ListerGadgetBaseSettings.RefreshInterval;

//TODO column selection doesn't trigger presenter's populate() call (must hack into DataTable in order to do that or implement the whole thing using GWT CellTable)
//TODO somehow use GWT Places in order to store the state instead of clumsy settings
public abstract class ListerGadgetBase<E extends IEntity> extends GadgetBase {

    /**
     * Refresh interval for the list when in development mode in milliseconds (relevant when refreshing is activated)
     */
    protected static final RefreshInterval DEFAULT_REFRESH_INTERVAL = RefreshInterval.Never;

    protected static final int DEFAULT_PAGE_SIZE = 10;

    private final DataTablePanel<E> dataTablePanel;

    private ListerGadgetBaseSettings settings;

    private final E proto;

    public ListerGadgetBase(GadgetMetadata gmd, Class<E> entityClass) {
        super(gmd);
        proto = EntityFactory.getEntityPrototype(entityClass);

        // validate that we got correct settings class 'cause our subclasses could not be trusted (they may have messed something) 
        if (isSettingsInstanceOk(gadgetMetadata.settings())) {
            settings = gadgetMetadata.settings().cast();
        } else {
            settings = createSettings();
            gadgetMetadata.settings().set(settings);
        }

        dataTablePanel = new DataTablePanel<E>(entityClass);
        dataTablePanel.setColumnDescriptors(fetchColumnDescriptorsFromSettings());
        dataTablePanel.setFilterActionHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                populateList();
            }
        });
        dataTablePanel.setPrevActionHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                prevListPage();
            }
        });
        dataTablePanel.setNextActionHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                nextListPage();
            }
        });
        dataTablePanel.getDataTable().addSortChangeHandler(new SortChangeHandler<E>() {
            @Override
            public void onChange(ColumnDescriptor<E> column) {
                populateList();
            }
        });
        // FIXME add handler for column selection (store the selected columns in settings)

        dataTablePanel.setWidth("100%");
        dataTablePanel.setFilterEnabled(isFilterRequired());
        dataTablePanel.getDataTable().setHasColumnClickSorting(true);
        dataTablePanel.getDataTable().setHasCheckboxColumn(false);
        dataTablePanel.getDataTable().setMarkSelectedRow(false);
        dataTablePanel.getDataTable().setAutoColumnsWidth(true);
        dataTablePanel.getDataTable().renderTable();
        dataTablePanel.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    // FIXME possible memory leak???
                    // commented out because when the application returns to the gadget's page it seems to restart the page refreshing
                    // but draws another instance of ListerGadget, i.e: on the new gadget's setup, the refresh
                    // interval is "Never", but the "loading..." message on top of the screen begins to appear at regular period

                    // start()
                } else {
                    ListerGadgetBase.this.stop();
                }
            }
        });
    }

    protected final E proto() {
        return proto;
    }

    protected abstract boolean isFilterRequired();

    /**
     * Implement in derived class to fill the page via {@link #setPageData(List, int, int, boolean)}.
     * 
     * @param pageNumber
     */
    public abstract void populatePage(int pageNumber);

    public abstract List<ColumnDescriptor<E>> defineColumnDescriptors();

    /**
     * Convenience method that allows to avoid a lot of unnecessary typing.
     * 
     * @param member
     * @param title
     *            use <code>null</code> to set default title.
     * @param visible
     * @return
     */
    protected final ColumnDescriptor<E> col(IObject<?> member, String title, boolean visible) {
        if (title == null) {
            return ColumnDescriptorFactory.createColumnDescriptor(proto(), member, visible);
        } else {
            return ColumnDescriptorFactory.createTitledColumnDescriptor(proto(), member, title, visible);
        }
    }

    protected final ColumnDescriptor<E> col(IObject<?> member, boolean visible) {
        return col(member, null, visible);
    }

    protected final ColumnDescriptor<E> colv(IObject<?> member) {
        return col(member, true);
    }

    protected final ColumnDescriptor<E> colv(IObject<?> member, String title) {
        return col(member, title, true);
    }

    /** Create invisible (hidden) column */
    protected final ColumnDescriptor<E> colh(IObject<?> member) {
        return col(member, false);
    }

    /** Create invisible (hidden) column */
    protected final ColumnDescriptor<E> colh(IObject<?> member, String title) {
        return col(member, title, false);
    }

    private List<ColumnDescriptor<E>> fetchColumnDescriptorsFromSettings() {
        // FIXME remove the next line when the stringholder string duplication is solved
        ListerGadgetBaseSettings settings = EntityFactory.create(ListerGadgetBaseSettings.class);

        if (settings.columnDescriptors().isEmpty()) {
            List<ColumnDescriptor<E>> descriptors = defineColumnDescriptors();
            for (ColumnDescriptor<E> columnDescriptor : descriptors) {
                ColumnDescriptorEntity columnDescriptorEntity = EntityFactory.create(ColumnDescriptorEntity.class);
                columnDescriptorEntity.propertyPath().setValue(columnDescriptor.getColumnName());
                columnDescriptorEntity.title().setValue(columnDescriptor.getColumnTitle());
                columnDescriptorEntity.visible().setValue(columnDescriptor.isVisible());
                columnDescriptorEntity.sortingPrecedence().setValue(columnDescriptor.isSortable() ? 1 : null);
                columnDescriptorEntity.sortAscending().setValue(columnDescriptor.isSortAscending());
                settings.columnDescriptors().add(columnDescriptorEntity);
            }
        }

        List<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
        for (ColumnDescriptorEntity columnDescriptorEntity : settings.columnDescriptors()) {
            Path propertyPath = new Path(columnDescriptorEntity.propertyPath().getValue());
            String title = columnDescriptorEntity.title().getValue();
            boolean visibility = columnDescriptorEntity.visible().getValue();
            ColumnDescriptor<E> columnDescriptor = col(proto().getMember(propertyPath), title, visibility);
            // TODO add sorting
            columnDescriptors.add(columnDescriptor);
        }
        return columnDescriptors;
    }

    protected void setColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors) {
        dataTablePanel.setColumnDescriptors(columnDescriptors);
    }

    protected boolean isSettingsInstanceOk(AbstractGadgetSettings abstractSettings) {
        return abstractSettings.isInstanceOf(ListerGadgetBaseSettings.class);
    }

    @Override
    protected ListerGadgetBaseSettings createSettings() {
        ListerGadgetBaseSettings settings = EntityFactory.create(ListerGadgetBaseSettings.class);
        settings.refreshInterval().setValue(DEFAULT_REFRESH_INTERVAL);
        settings.pageSize().setValue(DEFAULT_PAGE_SIZE);
        settings.pageNumber().setValue(0);
        settings.columnDescriptors().clear();

        return settings;
    }

    @Override
    public GadgetMetadata getGadgetMetadata() {
        // FIXME clear columns from settings if they are still doubled
        return this.gadgetMetadata;
    }

    @Override
    public void start() {
        super.start();
        settings.pageNumber().setValue(0);
        populateList();
    }

    @Override
    public boolean isSetupable() {
        return true;
    }

    @Override
    public ISetup getSetup() {
        return new SetupLister();
    }

    @Override
    protected void onRefreshTimer() {
        populateList();
    }

    protected IsWidget getListerWidget() {
        return dataTablePanel;
    }

    protected List<DataTableFilterData> getListerFilterData() {
        return dataTablePanel.getFilterData();
    }

    public int getPageSize() {
        return settings.pageSize().getValue();
    }

    /**
     * Sets page size but does not populate the list: the specified page size will be applied on next population.
     * 
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        settings.pageSize().setValue(pageSize > 1 ? pageSize : DEFAULT_PAGE_SIZE);
    }

    public int getPageNumber() {
        return settings.pageNumber().getValue();
    }

    public List<Sort> getSorting() {
        return dataTablePanel.getDataTableModel().getSortCriteria();
    }

    /**
     * Fills the lister with data for a single page.
     * 
     * @param data
     * @param pageNumber
     * @param totalRows
     * @param hasMoreData
     */
    public final void setPageData(List<E> data, int pageNumber, int totalRows, boolean hasMoreData) {
        settings.pageNumber().setValue(pageNumber);
        if (data.size() == 0 & pageNumber > 0) {
            prevListPage();
        } else {
            dataTablePanel.setPageSize(settings.pageSize().getValue());
            dataTablePanel.populateData(data, pageNumber, hasMoreData, totalRows);
        }
    }

    private void nextListPage() {
        populatePage(getPageNumber() + 1);
    }

    private void prevListPage() {
        if (getPageNumber() != 0) {
            populatePage(getPageNumber() - 1);
        }
    }

    private void populateList() {
        populatePage(getPageNumber());
    }

    private void setRefreshInterval(RefreshInterval refreshInterval) {
        settings.refreshInterval().setValue(refreshInterval);
        int refreshIntervalMillis;

        if (refreshInterval != RefreshInterval.Never) {
            if (GWT.isProdMode()) {
                refreshIntervalMillis = refreshInterval.value() * 60000;
            } else {
                refreshIntervalMillis = refreshInterval.value() * 1000;
            }
        } else {
            refreshIntervalMillis = -1;
        }
        getRefreshTimer().setRefreshInterval(refreshIntervalMillis);
    }

    private static Sort ColumnDescriptorEntityToSort(ColumnDescriptorEntity entity) {
        Sort sort = new Sort(entity.propertyPath().getValue(), !entity.sortAscending().getValue());
        return sort;
    }

    private class SetupLister implements ISetup {

        protected final FlexTable setupPanel = new FlexTable();

        protected final TextBox itemsPerPage = new TextBox();

        protected final ListBox intervalList = new ListBox(false);

        protected SetupLister() {
            super();

            setupPanel.setWidget(0, 0, new Label(i18n.tr("Items Per Page") + ":"));

            itemsPerPage.setText(String.valueOf(settings.pageSize().getValue()));
            itemsPerPage.setWidth("3em");
            setupPanel.setWidget(0, 1, itemsPerPage);

            setupPanel.setWidget(1, 0, new Label(i18n.tr("Refresh Interval") + ":"));

            for (RefreshInterval i : RefreshInterval.values()) {
                intervalList.addItem(i.toString(), i.name());
                if (settings.refreshInterval().getValue() == i) {
                    intervalList.setSelectedIndex(intervalList.getItemCount() - 1);
                }
            }
            intervalList.setWidth("100%");
            setupPanel.setWidget(1, 1, intervalList);

            setupPanel.getElement().getStyle().setPaddingLeft(2, Unit.EM);
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
            int itemsPerPageCount = settings.pageSize().getValue();
            try {
                itemsPerPageCount = Integer.parseInt(itemsPerPage.getText());
            } catch (Throwable e) {
                // TODO ignore? show an error message? return false? make control validate the input?
            }
            setPageSize(itemsPerPageCount);

            if (intervalList.getSelectedIndex() != -1) {
                setRefreshInterval(RefreshInterval.valueOf(intervalList.getValue(intervalList.getSelectedIndex())));
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
