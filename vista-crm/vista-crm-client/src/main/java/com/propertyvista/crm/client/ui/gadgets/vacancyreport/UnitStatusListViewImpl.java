/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-10-22
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.vacancyreport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.entity.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.essentials.client.crud.EntityListPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.dashboard.gadgets.UnitVacancyStatus;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportGadgetSettings;

// TODO column selection doesn't trigger presenter's populate() call (must hack into DataTable in order to do that or implement the whole thing using GWT CellTable)
// TODO somehow use GWT Places in order to store the state instead of clumsy settings
public class UnitStatusListViewImpl implements IsWidget, UnitStatusListView {
    private static final Integer DEFAULT_ITEMS_PER_PAGE_COUNT = 5;

    private static I18n i18n = I18n.get(UnitStatusListViewImpl.class);

    private static String UNIT_STATUS_LIST_CAPTION = "Units";

    private EntityListPanel<UnitVacancyStatus> unitListPanel;

    private final VerticalPanel widgetPanel;

    private UnitStatusListView.Presenter presenter;

    private UnitVacancyReportGadgetSettings settings;

    private UnitStatusListViewFilteringCriteria filterCriteria;

    public UnitStatusListViewImpl() {

        widgetPanel = new VerticalPanel();
        widgetPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        // TODO add more good looking *static* caption (but without GWT overhead (i.e. not Label) if possible)
        widgetPanel.add(new HTML("<div>" + i18n.tr(UNIT_STATUS_LIST_CAPTION) + "</div>"));

        final List<ColumnDescriptor<UnitVacancyStatus>> defaultColumns = getDefaultColumns();
        final List<ColumnDescriptor<UnitVacancyStatus>> availableColumns = getAvailableColumns();
        unitListPanel = new EntityListPanel<UnitVacancyStatus>(UnitVacancyStatus.class) {

            @Override
            // although the name doesn't give a clue, this sets the default column descriptors for the EntityListPanelWidget
            public List<ColumnDescriptor<UnitVacancyStatus>> getColumnDescriptors() {
                return defaultColumns;
            }

        };
        unitListPanel.setWidth("100%");
        unitListPanel.setPrevActionHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (presenter != null) {
                    presenter.prevUnitStatusListPage();
                }
            }
        });
        unitListPanel.setNextActionHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (presenter != null) {
                    presenter.nextUnitStatusListPage();
                }
            }
        });
        unitListPanel.getDataTable().setHasCheckboxColumn(false);
        unitListPanel.getDataTable().setHasColumnClickSorting(true);
        unitListPanel.getDataTable().addSortChangeHandler(new SortChangeHandler<UnitVacancyStatus>() {

            @Override
            public void onChange(ColumnDescriptor<UnitVacancyStatus> column) {
                if (presenter != null) {
                    presenter.populateUnitStatusList();
                }
            }
        });

        unitListPanel.getDataTable().setColumnSelector(availableColumns);
        unitListPanel.getDataTable().setHasCheckboxColumn(false);
        unitListPanel.getDataTable().setMarkSelectedRow(false);
        unitListPanel.getDataTable().setAutoColumnsWidth(true);
        unitListPanel.getDataTable().renderTable();

        widgetPanel.add(unitListPanel);
    }

    private List<ColumnDescriptor<UnitVacancyStatus>> getAvailableColumns() {
        UnitVacancyStatus proto = EntityFactory.getEntityPrototype(UnitVacancyStatus.class);
        @SuppressWarnings("unchecked")
        List<ColumnDescriptor<UnitVacancyStatus>> x = Arrays.asList(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.buildingName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.address()), ColumnDescriptorFactory.createColumnDescriptor(proto, proto.owner()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyManager()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.complexName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unit()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floorplanName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floorplanMarketingName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.vacancyStatus()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentedStatus()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.isScoped()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentReady()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unitRent()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketRent()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentDeltaAbsolute()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentDeltaRelative()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.moveOutDay()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.moveInDay()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentedFromDate()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.daysVacant()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.revenueLost()));
        return x;
    }

    private static List<ColumnDescriptor<UnitVacancyStatus>> getDefaultColumns() {
        UnitVacancyStatus proto = EntityFactory.getEntityPrototype(UnitVacancyStatus.class);
        @SuppressWarnings("unchecked")
        List<ColumnDescriptor<UnitVacancyStatus>> x = Arrays.asList(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.address()), ColumnDescriptorFactory.createColumnDescriptor(proto, proto.owner()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyManager()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.complexName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unit()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.vacancyStatus()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unitRent()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketRent()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.daysVacant()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.revenueLost()));
        return x;
    }

    @Override
    public Widget asWidget() {
        return widgetPanel;
    }

    /**
     * Attaches settings and updates them on relevant occasions.
     * 
     * @param settings
     */
    public void attachSettings(UnitVacancyReportGadgetSettings settings) {
        this.settings = settings;
        if (settings.currentPage().isNull()) {
            settings.currentPage().setValue(0);
        }
        if (settings.itemsPerPage().isNull()) {
            settings.itemsPerPage().setValue(DEFAULT_ITEMS_PER_PAGE_COUNT);
        }
        unitListPanel.setPageSize(settings.itemsPerPage().getValue());
        populate();
    }

    public void setFilteringCriteria(UnitStatusListViewFilteringCriteria criteria) {
        this.filterCriteria = criteria;
        populate();
    }

    @Override
    public void setPageData(List<UnitVacancyStatus> data, int pageNumber, int totalRows, boolean hasMoreData) {
        if (settings != null) {
            settings.currentPage().setValue(pageNumber);
            unitListPanel.setPageSize(settings.itemsPerPage().getValue());
        }

        unitListPanel.populateData(data, pageNumber, hasMoreData, totalRows);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        populate();
    }

    @Override
    public List<Path> getVisibleProperties() {
        // TODO should be retrieved from the settings if settings are attached
        List<Path> visibleProperties = new ArrayList<Path>();
        for (ColumnDescriptor<UnitVacancyStatus> d : unitListPanel.getDataTable().getDataTableModel().getColumnDescriptors()) {
            visibleProperties.add(new Path(d.getColumnName()));
        }
        return visibleProperties;
    }

    @Override
    public boolean isEnabled() {
        return asWidget().isVisible();
    }

    @Override
    public int getPageNumber() {
        if (settings != null && !settings.currentPage().isNull()) {
            return settings.currentPage().getValue();
        } else {
            return unitListPanel.getPageNumber();
        }
    }

    @Override
    public int getPageSize() {
        if (settings != null && !settings.itemsPerPage().isNull()) {
            return settings.itemsPerPage().getValue();
        } else {
            return unitListPanel.getPageSize();
        }
    }

    @Override
    public UnitStatusListViewFilteringCriteria getUnitStatusListFilterCriteria() {
        return filterCriteria;
    }

    @Override
    public List<Sort> getUnitStatusListSortingCriteria() {
        List<Sort> sorting = new ArrayList<Sort>(2);
        ColumnDescriptor<UnitVacancyStatus> primarySortColumn = unitListPanel.getDataTable().getDataTableModel().getSortColumn();
        if (primarySortColumn != null) {
            sorting.add(new Sort(primarySortColumn.getColumnName(), !primarySortColumn.isSortAscending()));
        }
        ColumnDescriptor<UnitVacancyStatus> secondarySortColumn = unitListPanel.getDataTable().getDataTableModel().getSecondarySortColumn();
        if (secondarySortColumn != null) {
            sorting.add(new Sort(secondarySortColumn.getColumnName(), !secondarySortColumn.isSortAscending()));
        }
        return sorting;
    }

    @Override
    public void reportError(Throwable error) {
        // TODO show a panel with an error message instead of the list
    }

    private void populate() {
        if (presenter != null) {
            presenter.populateUnitStatusList();
        }
    }
}
