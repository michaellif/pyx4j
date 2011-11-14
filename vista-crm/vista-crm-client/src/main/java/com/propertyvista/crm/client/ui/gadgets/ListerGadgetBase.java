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
import com.pyx4j.entity.client.ui.datatable.DataTablePanel;
import com.pyx4j.entity.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.site.client.ui.crud.lister.DefaultListerTheme;

import com.propertyvista.domain.dashboard.AbstractGadgetSettings;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.StringHolder;
import com.propertyvista.domain.dashboard.gadgets.ListerGadgetBaseSettings;
import com.propertyvista.domain.dashboard.gadgets.ListerGadgetBaseSettings.RefreshInterval;
import com.propertyvista.domain.dashboard.gadgets.SortEntity;

//TODO column selection doesn't trigger presenter's populate() call (must hack into DataTable in order to do that or implement the whole thing using GWT CellTable)
//TODO somehow use GWT Places in order to store the state instead of clumsy settings
public abstract class ListerGadgetBase<E extends IEntity> extends GadgetBase {

    /**
     * Refresh interval for the list when in development mode in milliseconds (relevant when refreshing is activated)
     */
    protected static final RefreshInterval DEFAULT_REFRESH_INTERVAL = RefreshInterval.Never;

    protected static final int DEFAULT_PAGE_SIZE = 10;

    private final DataTablePanel<E> entityListPanel;

    private ListerGadgetBaseSettings settings;

    public ListerGadgetBase(GadgetMetadata gmd, Class<E> entityClass) {
        super(gmd);

        // validate that we got correct settings class 'cause our subclasses could not be trusted (they may have messed something) 
        if (isSettingsInstanceOk(gadgetMetadata.settings())) {
            settings = gadgetMetadata.settings().cast();
        } else {
            settings = createSettings();
            gadgetMetadata.settings().set(settings);
        }

        entityListPanel = new DataTablePanel<E>(entityClass) {
            @Override
            // although the name doesn't give a clue, this sets the default column descriptors for the EntityListPanelWidget
            public List<ColumnDescriptor<E>> getColumnDescriptors() {
                return fetchColumnDescriptorsFromSettings(this.proto());
            }

            @Override
            protected void onSelect(int selectedRow) {
                //TODO make gadget navigable
            }
        };
        entityListPanel.setPrevActionHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                prevListPage();
            }
        });
        entityListPanel.setNextActionHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                nextListPage();
            }
        });
        entityListPanel.getDataTable().addSortChangeHandler(new SortChangeHandler<E>() {
            @Override
            public void onChange(ColumnDescriptor<E> column) {
                // get the sorting criteria and store it in settings
                List<Sort> sorting = new ArrayList<Sort>(2);
                ColumnDescriptor<E> primarySortColumn = entityListPanel.getDataTable().getDataTableModel().getSortColumn();
                if (primarySortColumn != null) {
                    sorting.add(new Sort(primarySortColumn.getColumnName(), !primarySortColumn.isSortAscending()));
                }
//                ColumnDescriptor<E> secondarySortColumn = entityListPanel.getDataTable().getDataTableModel().getSecondarySortColumn();
//                if (secondarySortColumn != null) {
//                    sorting.add(new Sort(secondarySortColumn.getColumnName(), !secondarySortColumn.isSortAscending()));
//                }
                settings.sorting().clear();
                for (Sort sort : sorting) {
                    SortEntity sortEntity = SortToEntity(sort);
                    settings.sorting().add(sortEntity);
                }

                populateList();
            }
        });
        // FIXME add handler for column selection (store the selected columns in settings)

        // use the same style as ListerBase
        entityListPanel.setWidth("100%");
        entityListPanel.setStyleName(DefaultListerTheme.StyleSuffix.ListerListPanel.name());
        entityListPanel.getDataTable().setColumnSelector(getAvailableColumnDescriptors(entityListPanel.proto()));
        entityListPanel.getDataTable().setHasColumnClickSorting(true);
        entityListPanel.getDataTable().setHasCheckboxColumn(false);
        entityListPanel.getDataTable().setMarkSelectedRow(false);
        entityListPanel.getDataTable().setAutoColumnsWidth(true);
        entityListPanel.getDataTable().renderTable();
        // TODO enable filters when they can be persisted

        entityListPanel.addAttachHandler(new AttachEvent.Handler() {
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

    /**
     * Implement in derived class to fill the page via {@link #setPageData(List, int, int, boolean)}.
     * 
     * @param pageNumber
     */
    public abstract void populatePage(int pageNumber);

    /**
     * Implement in derived class to set default columns.<br>
     * Warning: that method this called from within the constructor!
     */
    protected abstract List<ColumnDescriptor<E>> getDefaultColumnDescriptors(E proto);

    /**
     * Implement in derived class to set available columns.<br>
     * Warning: that method this called from within the constructor!
     */
    protected abstract List<ColumnDescriptor<E>> getAvailableColumnDescriptors(E proto);

    private List<ColumnDescriptor<E>> fetchColumnDescriptorsFromSettings(E proto) {
        if (settings.columnPaths().isEmpty()) {
            List<ColumnDescriptor<E>> descriptors = getDefaultColumnDescriptors(proto);
            for (ColumnDescriptor<E> columnDescriptor : descriptors) {
                StringHolder columnName = EntityFactory.create(StringHolder.class);
                columnName.stringValue().setValue(columnDescriptor.getColumnName());
                settings.columnPaths().add(columnName);
            }
        }
        List<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
        for (StringHolder columnName : settings.columnPaths()) {
            ColumnDescriptor<E> columnDescriptor = ColumnDescriptorFactory.createColumnDescriptor(proto,
                    proto.getMember(new Path(columnName.stringValue().getValue())));
            columnDescriptors.add(columnDescriptor);
        }

        return columnDescriptors;
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
        settings.columnPaths().clear();
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
        return entityListPanel;
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
        List<Sort> sorting = new ArrayList<Sort>(2);
        for (SortEntity sortEntity : settings.sorting()) {
            sorting.add(EntityToSort(sortEntity));
        }
        return sorting;
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
            entityListPanel.setPageSize(settings.pageSize().getValue());
            entityListPanel.populateData(data, pageNumber, hasMoreData, totalRows);
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

    private static SortEntity SortToEntity(Sort sort) {
        SortEntity entity = EntityFactory.create(SortEntity.class);
        entity.propertyName().setValue(sort.getPropertyName());
        entity.descending().setValue(sort.isDescending());
        return entity;
    }

    private static Sort EntityToSort(SortEntity entity) {
        Sort sort = new Sort(entity.propertyName().getValue(), entity.descending().getValue());
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
