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
package com.propertyvista.crm.client.ui.gadgets.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityContainer;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.entity.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.entity.client.ui.datatable.DataTablePanel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;

import com.propertyvista.crm.client.ui.gadgets.util.ColumnDescriptorConverter;
import com.propertyvista.domain.dashboard.gadgets.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ListerGadgetBaseMetadata;

public abstract class ListerGadgetInstanceBase<E extends IEntity, GADGET_TYPE extends ListerGadgetBaseMetadata> extends GadgetInstanceBase<GADGET_TYPE> {

    protected static final int DEFAULT_PAGE_SIZE = 10;

    private final Class<E> listedEntityClass;

    private final boolean isSearchFilterEnabled;

    private DataTablePanel<E> dataTablePanel;

    private int pageNumber;

    private boolean needToSaveColumns;

    /**
     * @param gadgetMetadata
     *            instance of gadget metadata that defines gadget type and holds the state of the gadget (can be <code>null</code>), it's type is
     *            {@link GadgetMetadata} because it has to be provided via factory that doesn't have an option to provide with a concrete type
     * @param gadgetTypeClass
     *            gadget type
     * @param setupForm
     *            the form that is used to setup the gadget or <code>null</code> if gadget is not 'setupable' or is intended to be made 'setupable' by
     *            overriding of {@link #isSetupable()} and {@link #getSetup()} methods
     * @param listedEntityClass
     *            class of the entity that this list is supposed to display
     * @param isSearchFilterEnabled
     *            display search filter on the lister
     */
    public ListerGadgetInstanceBase(GadgetMetadata gadgetMetadata, Class<GADGET_TYPE> gadgetTypeClass, CEntityContainer<GADGET_TYPE> setupForm,
            Class<E> listedEntityClass, boolean isSearchFilterEnabled) {
        super(gadgetMetadata, gadgetTypeClass, setupForm);

        this.needToSaveColumns = false;

        this.pageNumber = 0;

        this.isSearchFilterEnabled = isSearchFilterEnabled;
        this.listedEntityClass = listedEntityClass;

        setDefaultPopulator(new Populator() {
            @Override
            public void populate() {
                populatePage(pageNumber);
            }
        });
    }

    @Override
    public void setPresenter(IGadgetInstancePresenter presenter) {
        super.setPresenter(presenter);
        // TODO this is a hack to save preloaded lister gadgets (that do not have columns in metadata):
        // i think this has to be fixed but not letting null gagdet metadata to be passed to the constructor, and creating default settings on server side
        // one of the advantages of server side creation of gadget metadata, is that it allows to select different settings based on user context
        if (needToSaveColumns) {
            saveMetadata();
            needToSaveColumns = false;
        }
    }

    /**
     * Implement in derived class to fill the page via {@link #setPageData(List, int, int, boolean)}, and {@link #populateSucceded()} or
     * {@link #populateFailed(Throwable)}
     * 
     * @param pageNumber
     *            the page that it's requested to populate
     */
    protected abstract void populatePage(int pageNumber);

    /**
     * Initializes the lister widget so it can be added to a gadget panel
     */
    protected Widget initListerWidget() {
        dataTablePanel = new DataTablePanel<E>(listedEntityClass);
        dataTablePanel.setColumnDescriptors(getColumnDescriptorsFromSettings());
        dataTablePanel.setFilterApplyCommand(new Command() {
            @Override
            public void execute() {
                populate(false);
            }
        });
        dataTablePanel.setFirstActionHandler(new Command() {
            @Override
            public void execute() {
                pageNumber = 0;
                populate(false);
            }
        });
        dataTablePanel.setPrevActionHandler(new Command() {
            @Override
            public void execute() {
                if (pageNumber != 0) {
                    --pageNumber;
                    populate(false);
                }
            }
        });
        dataTablePanel.setNextActionHandler(new Command() {
            @Override
            public void execute() {
                pageNumber += 1;
                populate(false);
            }
        });
        dataTablePanel.setLastActionHandler(new Command() {
            @Override
            public void execute() {
                pageNumber = dataTablePanel.getDataTableModel().getTotalRows() / getMetadata().pageSize().getValue();
                populate(false);
            }
        });
        dataTablePanel.getDataTable().addSortChangeHandler(new SortChangeHandler<E>() {
            @Override
            public void onChange(ColumnDescriptor column) {
                for (ColumnDescriptorEntity entity : getMetadata().columnDescriptors()) {
                    if (entity.propertyPath().getValue().equals(column.getColumnName())) {
                        getMetadata().sortAscending().setValue(dataTablePanel.getDataTableModel().isSortAscending());
                        getMetadata().primarySortColumn().set(entity);
                        break;
                    }
                }
                // TODO: warning: both calls are async
                saveMetadata();
                populate(false);
            }
        });
        dataTablePanel.getDataTable().addColumnSelectionChangeHandler(new DataTable.ColumnSelectionHandler() {
            @Override
            public void onColumSelectionChanged() {
                Iterator<ColumnDescriptor> columnDescriptors = dataTablePanel.getDataTableModel().getColumnDescriptors().iterator();
                Iterator<ColumnDescriptorEntity> columnDescriptorEntities = getMetadata().columnDescriptors().iterator();
                while (columnDescriptors.hasNext()) {
                    assert columnDescriptorEntities.hasNext() : "DataTable's column descriptors and gadget metadata's column descriptor arrays don't match";
                    ColumnDescriptorEntity entity = columnDescriptorEntities.next();
                    ColumnDescriptorConverter.saveColumnDescriptorToEntity(columnDescriptors.next(), entity);
                }
                saveMetadata();
            }
        });
        // user defined item selection handler:
        dataTablePanel.getDataTable().addItemSelectionHandler(new DataTable.ItemSelectionHandler() {
            @Override
            public void onSelect(int selectedRow) {
                E item = dataTablePanel.getDataTable().getSelectedItem();
                if (item != null) {
                    onItemSelect(item);
                }
            }
        });

        dataTablePanel.setSize("100%", "100%");
        dataTablePanel.setFilteringEnabled(isSearchFilterEnabled);
        dataTablePanel.getDataTable().setHasColumnClickSorting(true);
        dataTablePanel.getDataTable().setHasCheckboxColumn(false);
        dataTablePanel.getDataTable().setMarkSelectedRow(false);
        dataTablePanel.getDataTable().setAutoColumnsWidth(true);
        dataTablePanel.getDataTable().setHasDetailsNavigation(true);
        dataTablePanel.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.ListerListPanel.name());
        dataTablePanel.getDataTable().renderTable();
        return dataTablePanel;
    }

    protected IsWidget getListerWidget() {
        return dataTablePanel;
    }

    @Override
    public void start() {
        pageNumber = 0;
        dataTablePanel.getDataTableModel().setSortColumn(
                ColumnDescriptorConverter.columnDescriptorFromEntity(listedEntityClass, getMetadata().primarySortColumn()));
        dataTablePanel.getDataTableModel().setSortAscending(getMetadata().sortAscending().isBooleanTrue());
        dataTablePanel.getDataTable().renderTable();
        super.start();
    }

    /**
     * Fills the lister widget with data for a single page.
     * 
     * @param data
     * @param pageNumber
     * @param totalRows
     * @param hasMoreData
     */
    protected void setPageData(List<E> data, int pageNumber, int totalRows, boolean hasMoreData) {
        this.pageNumber = pageNumber;
        dataTablePanel.setPageSize(getMetadata().pageSize().getValue());
        dataTablePanel.populateData(data, pageNumber, hasMoreData, totalRows);
    }

    /**
     * Override in derived class for to define lister widget's item selection handler
     */
    protected void onItemSelect(E item) {
    }

    /**
     * convenience method that gets lister sorting criteria from gadget's metadata
     */
    protected final List<Sort> getListerSortingCriteria() {
        List<Sort> sortingCriteria = new ArrayList<Sort>();
        if (!getMetadata().primarySortColumn().isNull()) {
            sortingCriteria.add(new Sort(getMetadata().primarySortColumn().propertyPath().getValue(), !getMetadata().sortAscending().isBooleanTrue()));
        }
        return sortingCriteria;
    }

    // TODO need to save criteria in metadata and make it final
    @Deprecated
    protected List<Criterion> getListerSearchCriteria() {
        return dataTablePanel.getFilters();
    }

    /**
     * Convenience method that gets page size from gadget's metadata
     */
    protected final int getPageSize() {
        return getMetadata().pageSize().getValue();
    }

    @Override
    protected GADGET_TYPE createDefaultSettings(Class<GADGET_TYPE> metadataClass) {
        GADGET_TYPE settings = super.createDefaultSettings(metadataClass);
        settings.pageSize().setValue(DEFAULT_PAGE_SIZE);
        return settings;
    }

    private List<ColumnDescriptor> getColumnDescriptorsFromSettings() {
        List<ColumnDescriptor> columnDescriptors = new ArrayList<ColumnDescriptor>();
        if (getMetadata().getPrimaryKey() == null) {
            // normally this should happen only once in gadget's lifetime
            // we do it here because of the following reason:
            //      preloaded gadgets need this
            needToSaveColumns = true; // to save it when we get the presenter.
        }
        for (ColumnDescriptorEntity columnDescriptorEntity : getMetadata().columnDescriptors()) {
            columnDescriptors.add(ColumnDescriptorConverter.columnDescriptorFromEntity(listedEntityClass, columnDescriptorEntity));
        }
        return columnDescriptors;
    }

}
