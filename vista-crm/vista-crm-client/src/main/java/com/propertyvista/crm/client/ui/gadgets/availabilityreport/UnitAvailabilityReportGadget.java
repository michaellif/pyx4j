/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 10, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.availabilityreport;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.crm.client.ui.gadgets.ListerGadgetBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatusDTO;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatusReportSettings;

public class UnitAvailabilityReportGadget extends ListerGadgetBase<UnitAvailabilityStatusDTO> implements IBuildingGadget {
    //@formatter:off
    private static final String ALL_CAPTION = "All";
    private static final String VACANT_CAPTION = "Vacant";
    private static final String NOTICE_CAPTION = "Notice";
    private static final String RENTED_CAPTION = "Rented";
    private static final String NET_EXPOSURE_CAPTION = "Net Exposure";
    
    private FilterData filter;

    private final VerticalPanel gadgetPanel;

    private final FlexTable controlsPanel;

    ToggleButton allButton;
    ToggleButton vacantButton;
    ToggleButton noticeButton;
    ToggleButton vacantNoticeButton;
    ToggleButton netExposureButton;
    ToggleButton rentedButton;

    List<ToggleButton> filteringButtons;

    
    private final AvailabilityReportService service;
    
    private final UnitAvailabilityStatusReportSettings settings;
    //@formatter:on

    public UnitAvailabilityReportGadget(GadgetMetadata gmd) {
        super(gmd, UnitAvailabilityStatusDTO.class);
        settings = gadgetMetadata.settings().cast();
        service = GWT.create(AvailabilityReportService.class);

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
                        populatePage(0);
                    }
                });
            }
        };
        allButton = new ToggleButton(i18n.tr(ALL_CAPTION), filterButtonClickHandler);
        vacantButton = new ToggleButton(i18n.tr(VACANT_CAPTION), filterButtonClickHandler);
        noticeButton = new ToggleButton(i18n.tr(NOTICE_CAPTION), filterButtonClickHandler);
        vacantNoticeButton = new ToggleButton(i18n.tr(VACANT_CAPTION) + "/" + i18n.tr(NOTICE_CAPTION), filterButtonClickHandler);
        rentedButton = new ToggleButton(i18n.tr(RENTED_CAPTION), filterButtonClickHandler);
        netExposureButton = new ToggleButton(i18n.tr(NET_EXPOSURE_CAPTION), filterButtonClickHandler);
        filteringButtons = Arrays.asList(allButton, vacantButton, noticeButton, vacantNoticeButton, rentedButton, netExposureButton);

        int col = -1;
        // toggle the default button
        for (ToggleButton button : filteringButtons) {
            if (button.getText().equals(settings.defaultFilteringButton().getValue())) {
                button.setDown(true);
            } else {
                button.setDown(false);
            }
            controlsPanel.setWidget(0, ++col, button);
        }

        gadgetPanel = new VerticalPanel();
        gadgetPanel.add(controlsPanel);
        gadgetPanel.add(getListerWidget());
        gadgetPanel.setCellHorizontalAlignment(controlsPanel, VerticalPanel.ALIGN_CENTER);
        gadgetPanel.setWidth("100%");
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.UnitAvailabilityReportMk2);
        gmd.name().setValue(GadgetType.UnitAvailabilityReportMk2.toString());
    }

    @Override
    protected UnitAvailabilityStatusReportSettings createSettings() {
        UnitAvailabilityStatusReportSettings settings = super.createSettings().clone(UnitAvailabilityStatusReportSettings.class);
        settings.defaultFilteringButton().setValue(ALL_CAPTION);
        return settings;
    }

    //@formatter:off
    @SuppressWarnings("unchecked")
    @Override
    protected List<ColumnDescriptor<UnitAvailabilityStatusDTO>> getDefaultColumnDescriptors(UnitAvailabilityStatusDTO proto) {
        return Arrays.asList(
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyManagerName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unit()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.vacancyStatus()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unitRent()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketRent()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.daysVacant()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.revenueLost())
                );
    }

    
    @SuppressWarnings("unchecked")
    @Override
    protected List<ColumnDescriptor<UnitAvailabilityStatusDTO>> getAvailableColumnDescriptors(UnitAvailabilityStatusDTO proto) {

        return Arrays.asList(ColumnDescriptorFactory.createColumnDescriptor(
                proto, proto.propertyCode()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.buildingName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.address()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.owner()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyManagerName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.complexName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unit()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floorplanName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floorplanMarketingName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.vacancyStatus()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentedStatus()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.isScoped()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentReadinessStatus()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.unitRent()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketRent()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentDeltaAbsolute()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentDeltaRelative()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.moveOutDay()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.moveInDay()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentedFromDate()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.daysVacant()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.revenueLost())
                );
    }
    //@formatter:on

    @Override
    public void setFiltering(FilterData filterData) {
        this.filter = filterData;
        populatePage(0);
    }

    @Override
    public void populatePage(int pageNumber) {
        if (this.isEnabled()) {
            if (filter == null) {
                setPageData(new Vector<UnitAvailabilityStatusDTO>(), 0, 0, false);
                return;
            }

            final int page = pageNumber;
            final UnitSelectionCriteria select = buttonStateToSelectionCriteria();

            //@formatter:off
            service.unitStatusList(
                    new AsyncCallback<EntitySearchResult<UnitAvailabilityStatusDTO>>() {
                        @Override
                        public void onSuccess(EntitySearchResult<UnitAvailabilityStatusDTO> result) {                            
                                setPageData(result.getData(), page, result.getTotalRows(), result.hasMoreData());                        
                        }
        
                        @Override
                        public void onFailure(Throwable caught) {
                            // TODO somehow tell about the exception in civilized way
                        }
                    },
                    new Vector<Key>(filter.buildings), 
                    select.occupied, select.vacant, select.notice, select.rented, select.notrented,  
                    new LogicalDate(filter.toDate),
                    new Vector<Sort>(), 
                    pageNumber,
                    getPageSize());
            //@formatter:on
        }
    }

    @Override
    public Widget asWidget() {
        return gadgetPanel;
    }

    private boolean isEnabled() {
        return asWidget().isVisible() & isRunning();
    }

    // TODO override getSetup() to set the default button

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

    //@formatter:off
    private static class UnitSelectionCriteria {
        boolean occupied;
        boolean vacant;
        boolean notice;
        boolean rented;
        boolean notrented;
    }
    //@formatter:on

}
