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

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.entity.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.entity.client.ui.datatable.DataTablePanel;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.crm.client.ui.gadgets.util.ColumnDescriptorConverter;
import com.propertyvista.domain.dashboard.gadgets.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ListerGadgetBaseMetadata;

public abstract class ListerGadgetInstanceBase<E extends IEntity, GADGET_TYPE extends ListerGadgetBaseMetadata> extends GadgetInstanceBase<GADGET_TYPE> {

    protected static final int DEFAULT_PAGE_SIZE = 10;

    private DataTablePanel<E> dataTablePanel;

    private final E proto;

    private final Class<E> entityClass;

    public ListerGadgetInstanceBase(GadgetMetadata gmd, Class<E> entityClass, Class<GADGET_TYPE> gadgetTypeClass) {
        super(gmd, gadgetTypeClass);

        this.entityClass = entityClass;
        proto = EntityFactory.getEntityPrototype(entityClass);

        setDefaultPopulator(new Populator() {
            @Override
            public void populate() {
                doPopulate();
            }
        });
    }

    protected final E proto() {
        return proto;
    }

    protected Widget initListerWidget() {
        dataTablePanel = new DataTablePanel<E>(entityClass);
        dataTablePanel.setColumnDescriptors(fetchColumnDescriptorsFromSettings());
        dataTablePanel.setFilterApplyCommand(new Command() {
            @Override
            public void execute() {
                populate(false);
            }
        });
        dataTablePanel.setFirstActionHandler(new Command() {
            @Override
            public void execute() {
                firstListPage();
            }
        });
        dataTablePanel.setPrevActionHandler(new Command() {
            @Override
            public void execute() {
                prevListPage();
            }
        });
        dataTablePanel.setNextActionHandler(new Command() {
            @Override
            public void execute() {
                nextListPage();
            }
        });
        dataTablePanel.setLastActionHandler(new Command() {
            @Override
            public void execute() {
                lastListPage();
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
                // warning: both calls are async
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
                    ColumnDescriptorConverter.columnDescriptorToEntity(columnDescriptors.next(), entity);
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
        dataTablePanel.setFilteringEnabled(isFilterRequired());
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

    // FIXME put this in the constructor
    protected abstract boolean isFilterRequired();

    /**
     * Implement in derived class to fill the page via {@link #setPageData(List, int, int, boolean)}.
     * 
     * @param pageNumber
     */
    public abstract void populatePage(int pageNumber);

    // FIXME put this in the constructor
    public abstract List<ColumnDescriptor> defineColumnDescriptors();

    /**
     * Actions, Override in derived class for your own select item procedure.
     */
    protected void onItemSelect(E item) {
    }

    private List<ColumnDescriptor> fetchColumnDescriptorsFromSettings() {
        List<ColumnDescriptor> columnDescriptors = new ArrayList<ColumnDescriptor>();
        if (getMetadata().columnDescriptors().isEmpty()) {
            // normally this should happen only once in gadget's lifetime
            // we do it here because of two reasons:
            //      1. Preloaded gadgets need this
            //      2. createDefaultSettings() is called from the base class constructor before we can know the default columns, and
            //         before the dataTablePanel has been initialized.
            storeDefaultColumnDescriptorsToSettings(getMetadata());
        }
        for (ColumnDescriptorEntity columnDescriptorEntity : getMetadata().columnDescriptors()) {
            columnDescriptors.add(ColumnDescriptorConverter.columnDescriptorFromEntity(entityClass, columnDescriptorEntity));
        }
        return columnDescriptors;
    }

    @Override
    protected GADGET_TYPE createDefaultSettings(Class<GADGET_TYPE> metadataClass) {
        GADGET_TYPE settings = super.createDefaultSettings(metadataClass);
        settings.pageSize().setValue(DEFAULT_PAGE_SIZE);
        settings.pageNumber().setValue(0);
        return settings;
    }

    private void storeDefaultColumnDescriptorsToSettings(GADGET_TYPE settings) {
        for (ColumnDescriptor columnDescriptor : defineColumnDescriptors()) {
            ColumnDescriptorEntity entity = EntityFactory.create(ColumnDescriptorEntity.class);
            settings.columnDescriptors().add(ColumnDescriptorConverter.columnDescriptorToEntity(columnDescriptor, entity));
        }
    }

    @Override
    public void start() {
        getMetadata().pageNumber().setValue(0);
        dataTablePanel.getDataTableModel().setSortColumn(ColumnDescriptorConverter.columnDescriptorFromEntity(entityClass, getMetadata().primarySortColumn()));
        dataTablePanel.getDataTableModel().setSortAscending(getMetadata().sortAscending().isBooleanTrue());
        dataTablePanel.getDataTable().renderTable();
        super.start();
    }

    @Override
    public boolean isSetupable() {
        return true;
    }

    @Override
    public ISetup getSetup() {
        return new SetupForm(new CEntityDecoratableEditor<GADGET_TYPE>(metadataClass) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel p = new FormFlexPanel();
                int row = -1;
                p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().refreshInterval())).build());
                p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().pageSize())).build());
                return p;
            }
        });
    }

    protected List<DataTableFilterData> getListerFilterData() {
        return dataTablePanel.getFilters();
    }

    public int getPageSize() {
        return getMetadata().pageSize().getValue();
    }

    public int getPageNumber() {
        return getMetadata().pageNumber().getValue();
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
        getMetadata().pageNumber().setValue(pageNumber);
        if (data.size() == 0 & pageNumber > 0) {
            prevListPage();
        } else {
            dataTablePanel.setPageSize(getMetadata().pageSize().getValue());
            dataTablePanel.populateData(data, pageNumber, hasMoreData, totalRows);
        }
    }

    private void firstListPage() {
        getMetadata().pageNumber().setValue(0);
        populate(false);
    }

    private void nextListPage() {
        getMetadata().pageNumber().setValue(getPageNumber() + 1);
        populate(false);
    }

    private void prevListPage() {
        if (getPageNumber() != 0) {
            getMetadata().pageNumber().setValue(getPageNumber() - 1);
            populate(false);
        }
    }

    private void lastListPage() {
        getMetadata().pageNumber().setValue(dataTablePanel.getDataTableModel().getTotalRows() / dataTablePanel.getDataTableModel().getPageSize());
        populate(false);
    }

    private void doPopulate() {
        populatePage(getPageNumber());
    }

}
