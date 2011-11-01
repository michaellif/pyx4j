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
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.entity.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.essentials.client.crud.EntityListPanel;
import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.StyleSuffix;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.VacancyReportService;
import com.propertyvista.domain.dashboard.AbstractGadgetSettings;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitAvailabilityReportSettings;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyStatus;

// TODO column selection doesn't trigger presenter's populate() call (must hack into DataTable in order to do that or implement the whole thing using GWT CellTable)
// TODO somehow use GWT Places in order to store the state instead of clumsy settings
public class UnitAvailabilityReportGadget extends VacancyGadgetBase {
    private static final Integer DEFAULT_ITEMS_PER_PAGE_COUNT = 5;

    private static final String ALL_BUTTON_CAPTION = "All";

    private EntityListPanel<UnitVacancyStatus> unitListPanel;

    private UnitAvailabilityReportSettings settings;

    private VerticalPanel panel;

    private VacancyReportService service;

    private FlexTable controlsPanel;

    private boolean isOk = true;

    ToggleButton allButton;

    ToggleButton vacantButton;

    ToggleButton noticeButton;

    ToggleButton vacantNoticeButton;

    ToggleButton netExposureButton;

    ToggleButton rentedButton;

    List<ToggleButton> filteringButtons;

    public UnitAvailabilityReportGadget(GadgetMetadata gmd) {
        super(gmd);
        settings = gadgetMetadata.settings().cast();

        final List<ColumnDescriptor<UnitVacancyStatus>> defaultColumns = getDefaultColumns();
        final List<ColumnDescriptor<UnitVacancyStatus>> availableColumns = getAvailableColumns();
        unitListPanel = new EntityListPanel<UnitVacancyStatus>(UnitVacancyStatus.class) {

            @Override
            // although the name doesn't give a clue, this sets the default column descriptors for the EntityListPanelWidget
            public List<ColumnDescriptor<UnitVacancyStatus>> getColumnDescriptors() {
                return defaultColumns;
            }

        };
        unitListPanel.setPrevActionHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                prevUnitStatusListPage();
            }
        });
        unitListPanel.setNextActionHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                nextUnitStatusListPage();
            }
        });
        unitListPanel.getDataTable().addSortChangeHandler(new SortChangeHandler<UnitVacancyStatus>() {

            @Override
            public void onChange(ColumnDescriptor<UnitVacancyStatus> column) {
                populateUnitStatusList();
            }
        });

        // use the same style as ListerBase
        unitListPanel.setWidth("100%");
        unitListPanel.setStyleName(ListerBase.DEFAULT_STYLE_PREFIX + StyleSuffix.listPanel);
        unitListPanel.getDataTable().setColumnSelector(availableColumns);
        unitListPanel.getDataTable().setHasColumnClickSorting(true);
        unitListPanel.getDataTable().setHasCheckboxColumn(false);
        unitListPanel.getDataTable().setMarkSelectedRow(false);
        unitListPanel.getDataTable().setAutoColumnsWidth(true);
        unitListPanel.getDataTable().renderTable();

        controlsPanel = new FlexTable();

        ClickHandler filterButtonClickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                for (ToggleButton button : filteringButtons) {
                    if (event.getSource().equals(button)) {
                        button.setDown(true);
                    } else {
                        button.setDown(false);
                    }
                }
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        settings.currentPage().setValue(0);
                        populateUnitStatusList();
                    }
                });
            }
        };
        allButton = new ToggleButton(i18n.tr(ALL_BUTTON_CAPTION), filterButtonClickHandler);
        vacantButton = new ToggleButton(i18n.tr("Vacant"), filterButtonClickHandler);
        noticeButton = new ToggleButton(i18n.tr("Notice"), filterButtonClickHandler);
        vacantNoticeButton = new ToggleButton(i18n.tr("Vacant") + "/" + i18n.tr("Notice"), filterButtonClickHandler);
        rentedButton = new ToggleButton(i18n.tr("Rented"), filterButtonClickHandler);
        netExposureButton = new ToggleButton(i18n.tr("Net Exposure"), filterButtonClickHandler);

        filteringButtons = Arrays.asList(allButton, vacantButton, noticeButton, vacantNoticeButton, rentedButton, netExposureButton);

        int col = -1;
        // select the default button
        for (ToggleButton button : filteringButtons) {
            if (button.getText().equals(settings.defaultFilteringButton().getValue())) {
                button.setDown(true);
            } else {
                button.setDown(false);
            }
            controlsPanel.setWidget(0, ++col, button);
        }

        panel = new VerticalPanel();
        panel.add(controlsPanel);
        panel.add(unitListPanel);
        panel.setCellHorizontalAlignment(controlsPanel, VerticalPanel.ALIGN_CENTER);
        panel.setWidth("100%");

        service = GWT.create(VacancyReportService.class);
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.UnitAvailabilityReport);
        gmd.name().setValue(GadgetType.UnitAvailabilityReport.toString());
    }

    @Override
    protected AbstractGadgetSettings createSettings() {
        UnitAvailabilityReportSettings settings = EntityFactory.create(UnitAvailabilityReportSettings.class);
        settings.currentPage().setValue(0);
        settings.itemsPerPage().setValue(DEFAULT_ITEMS_PER_PAGE_COUNT);
        settings.defaultFilteringButton().setValue(ALL_BUTTON_CAPTION);
        return settings;
    }

    private static List<ColumnDescriptor<UnitVacancyStatus>> getDefaultColumns() {
        UnitVacancyStatus proto = EntityFactory.getEntityPrototype(UnitVacancyStatus.class);
        @SuppressWarnings("unchecked")
        List<ColumnDescriptor<UnitVacancyStatus>> x = Arrays.asList(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.owner()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyManager()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unit()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.vacancyStatus()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unitRent()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketRent()));
        return x;
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

    @Override
    public void start() {
        super.start();
        populateUnitStatusList();
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public boolean isSetupable() {
        return true;
    }

    @Override
    protected void setFilteringCriteria(FilterDataDemoAdapter filterDataDemoAdapter) {
        this.filter = filterDataDemoAdapter;
        settings.currentPage().setValue(0);
        populateUnitStatusList();
    }

    @Override
    public ISetup getSetup() {
        final double LEFT_PADDING = 2d;
        final FlexTable setupPanel = new FlexTable();
        setupPanel.getElement().getStyle().setPaddingTop(1, Unit.EM);

        final TextBox unitsPerPage = new TextBox();
        unitsPerPage.setValue(Integer.toString(settings.itemsPerPage().getValue()));
        unitsPerPage.setWidth("3em");
        final Label unitsPerPageLabel = new Label(i18n.tr("Units per page") + ":");
        unitsPerPageLabel.getElement().getStyle().setPaddingLeft(LEFT_PADDING, Unit.EM);
        unitsPerPageLabel.getElement().getStyle().setPaddingRight(2, Unit.EM);

        final Label defaultFilteringButtonLabel = new Label(i18n.tr("Default display") + ":");
        defaultFilteringButtonLabel.getElement().getStyle().setPaddingLeft(LEFT_PADDING, Unit.EM);
        defaultFilteringButtonLabel.getElement().getStyle().setPaddingRight(2, Unit.EM);
        final ListBox defaultFilteringButtonCombo = new ListBox(false);

        int index = 0;
        for (ToggleButton button : filteringButtons) {
            defaultFilteringButtonCombo.addItem(button.getText());

            if (button.getText().equals(settings.defaultFilteringButton().getValue())) {
                defaultFilteringButtonCombo.setSelectedIndex(index);
            }
            ++index;
        }

        setupPanel.setWidget(0, 0, unitsPerPageLabel);
        setupPanel.setWidget(0, 1, unitsPerPage);
        setupPanel.setWidget(1, 0, defaultFilteringButtonLabel);
        setupPanel.setWidget(1, 1, defaultFilteringButtonCombo);

        return new ISetup() {

            @Override
            public Widget asWidget() {
                return setupPanel;
            }

            @Override
            public boolean onStart() {
                UnitAvailabilityReportGadget.this.suspend();
                return true;
            }

            @Override
            public boolean onOk() {
                boolean isOk = true;
                try {
                    settings.itemsPerPage().setValue(Integer.parseInt(unitsPerPage.getValue()));
                } catch (NumberFormatException e) {
                    settings.itemsPerPage().setValue(DEFAULT_ITEMS_PER_PAGE_COUNT);
                    isOk = false;
                }
                int selectedIndex = defaultFilteringButtonCombo.getSelectedIndex();
                String defaultButton = selectedIndex == -1 ? allButton.getText() : defaultFilteringButtonCombo.getValue(selectedIndex);
                settings.defaultFilteringButton().setValue(defaultButton);
                UnitAvailabilityReportGadget.this.stop();
                UnitAvailabilityReportGadget.this.start();
                return isOk;
            }

            @Override
            public void onCancel() {
            }
        };
    }

    private void setPageData(List<UnitVacancyStatus> data, int pageNumber, int totalRows, boolean hasMoreData) {
        if (!isOk) {
            isOk = true;
            panel.add(unitListPanel);
        }
        settings.currentPage().setValue(pageNumber);
        unitListPanel.setPageSize(settings.itemsPerPage().getValue());
        unitListPanel.populateData(data, pageNumber, hasMoreData, totalRows);
    }

    private boolean isEnabled() {
        return asWidget().isVisible();
    }

    private int getPageNumber() {
        return settings.currentPage().getValue();
    }

    private int getPageSize() {
        return settings.itemsPerPage().getValue();
    }

    private List<Sort> getUnitStatusListSortingCriteria() {
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

    private void reportError(Throwable error) {
        isOk = false;
        panel.clear();
        panel.add(new CTextArea(error.toString()));
    }

    // PRESENTER PART:
    public void populateUnitStatusList() {
        populateUnitStatusListPage(this.getPageNumber());
    }

    public void nextUnitStatusListPage() {
        populateUnitStatusListPage(this.getPageNumber() + 1);
    }

    public void prevUnitStatusListPage() {
        if (this.getPageNumber() > 0) {
            populateUnitStatusListPage(this.getPageNumber() - 1);
        }
    }

    private void populateUnitStatusListPage(final int pageNumber) {
        if (this.isEnabled()) {

            if (filter == null) {
                setPageData(new Vector<UnitVacancyStatus>(), 0, 0, false);
                return;
            }
            UnitSelectionCriteria select = buttonStateToSelectionCriteria();
            service.unitStatusList(new AsyncCallback<EntitySearchResult<UnitVacancyStatus>>() {

                @Override
                public void onSuccess(EntitySearchResult<UnitVacancyStatus> result) {
                    if (pageNumber == 0 | !result.getData().isEmpty()) {
                        setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
                    } else {
                        populateUnitStatusListPage(pageNumber - 1);
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    reportError(caught);
                }
            }, new Vector<String>(filter.getBuildingsFilteringCriteria()), //
                    select.occupied, select.vacant, select.notice, select.rented, select.notrented, filter.getFrom(), //
                    filter.getTo(), //
                    new Vector<Sort>(getUnitStatusListSortingCriteria()), //
                    pageNumber, //
                    getPageSize());
        }
    }

    // AUXILLIARY STUFF

    private UnitSelectionCriteria buttonStateToSelectionCriteria() {
        // it's done here and not in the toggle button click handler because it separates view from presenter 
        UnitSelectionCriteria select = new UnitSelectionCriteria();
        if (allButton.isDown()) {
            select.occupied = true;
            select.vacant = true;
            select.notice = true;
            select.rented = true;
            select.notrented = true;
        } else if (vacantButton.isDown()) {
            select.occupied = false;
            select.vacant = true;
            select.notice = false;
            select.rented = true;
            select.notrented = true;
        } else if (noticeButton.isDown()) {
            select.occupied = false;
            select.vacant = false;
            select.notice = true;
            select.rented = true;
            select.notrented = true;
        } else if (vacantNoticeButton.isDown()) {
            select.occupied = false;
            select.vacant = true;
            select.notice = true;
            select.rented = true;
            select.notrented = true;
        } else if (rentedButton.isDown()) {
            select.occupied = false;
            select.vacant = true;
            select.notice = true;
            select.rented = true;
            select.notrented = false;
        } else if (netExposureButton.isDown()) {
            select.occupied = false;
            select.vacant = true;
            select.notice = true;
            select.rented = false;
            select.notrented = true;
        }

        return select;
    }

    private static class UnitSelectionCriteria {
        boolean occupied;

        boolean vacant;

        boolean notice;

        boolean rented;

        boolean notrented;
    }

}
