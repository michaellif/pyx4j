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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.crm.client.ui.gadgets.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.Directory;
import com.propertyvista.crm.client.ui.gadgets.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.ListerGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatusDTO;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability.FilterPreset;

public class UnitAvailabilityReportGadget extends AbstractGadget<UnitAvailability> {
    private static final I18n i18n = I18n.get(UnitAvailabilityReportGadget.class);

    public static class UnitAvailabilityReportGadgetImpl extends ListerGadgetInstanceBase<UnitAvailabilityStatusDTO, UnitAvailability> implements
            IBuildingGadget {
        //@formatter:off        
        private FilterData filter;
    
        private VerticalPanel gadgetPanel;
    
        private FlexTable controlsPanel;
    
        ToggleButton allButton;
        ToggleButton vacantButton;
        ToggleButton noticeButton;
        ToggleButton vacantNoticeButton;
        ToggleButton netExposureButton;
        ToggleButton rentedButton;
    
        List<ToggleButton> filteringButtons;
    
        
        private final AvailabilityReportService service;    
        //@formatter:on

        public UnitAvailabilityReportGadgetImpl(GadgetMetadata gmd) {
            super(gmd, UnitAvailabilityStatusDTO.class, UnitAvailability.class);
            service = GWT.create(AvailabilityReportService.class);
        }

        @Override
        protected UnitAvailability createDefaultSettings(Class<UnitAvailability> metadataClass) {
            UnitAvailability settings = super.createDefaultSettings(metadataClass);
            settings.defaultFilteringPreset().setValue(FilterPreset.All);
            return settings;
        }

        @Override
        public void setFiltering(FilterData filterData) {
            this.filter = filterData;
            populatePage(0);
        }

        @Override
        public void populatePage(int pageNumber) {
            if (filter == null) {
                setPageData(new Vector<UnitAvailabilityStatusDTO>(), 0, 0, false);
                return;
            }

            final int page = pageNumber;
            final UnitSelectionCriteria select = buttonStateToSelectionCriteria();

            service.unitStatusList(new AsyncCallback<EntitySearchResult<UnitAvailabilityStatusDTO>>() {
                @Override
                public void onSuccess(EntitySearchResult<UnitAvailabilityStatusDTO> result) {
                    setPageData(result.getData(), page, result.getTotalRows(), result.hasMoreData());
                    populateSucceded();
                }

                @Override
                public void onFailure(Throwable caught) {
                    populateFailed(caught);
                }
            }, new Vector<Key>(filter.buildings), select.occupied, select.vacant, select.notice, select.rented, select.notrented, filter.toDate == null ? null
                    : new LogicalDate(filter.toDate), new Vector<Sort>(getSorting()), pageNumber, getPageSize());
        }

        @Override
        public Widget initContentPanel() {
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
                            getMetadata().pageNumber().setValue(0);
                            populate(false);
                        }
                    });
                }
            };
            allButton = new ToggleButton(FilterPreset.All.toString(), filterButtonClickHandler);
            vacantButton = new ToggleButton(FilterPreset.Vacant.toString(), filterButtonClickHandler);
            noticeButton = new ToggleButton(FilterPreset.Notice.toString(), filterButtonClickHandler);
            vacantNoticeButton = new ToggleButton(FilterPreset.VacantAndNotice.toString(), filterButtonClickHandler);
            rentedButton = new ToggleButton(FilterPreset.Rented.toString(), filterButtonClickHandler);
            netExposureButton = new ToggleButton(FilterPreset.NetExposure.toString(), filterButtonClickHandler);
            filteringButtons = Arrays.asList(allButton, vacantButton, noticeButton, vacantNoticeButton, rentedButton, netExposureButton);

            int col = -1;
            for (ToggleButton button : filteringButtons) {
                controlsPanel.setWidget(0, ++col, button);
            }

            gadgetPanel = new VerticalPanel();
            gadgetPanel.add(controlsPanel);
            gadgetPanel.add(initListerWidget());
            gadgetPanel.setCellHorizontalAlignment(controlsPanel, VerticalPanel.ALIGN_CENTER);
            gadgetPanel.setWidth("100%");

            return gadgetPanel;
        }

        @Override
        public void start() {
            setSelectedFiltering();
            super.start();
        }

        private void setSelectedFiltering() {
            for (ToggleButton button : filteringButtons) {
                if (button.getText().equals(getMetadata().defaultFilteringPreset().getValue().toString())) {
                    button.setDown(true);
                } else {
                    button.setDown(false);
                }
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

        //@formatter:off
        private static class UnitSelectionCriteria {
            boolean occupied;
            boolean vacant;
            boolean notice;
            boolean rented;
            boolean notrented;
        }
        //@formatter:on

        @Override
        protected boolean isFilterRequired() {
            return false;
        }

        @Override
        public List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().propertyCode()).build(),
                    new MemberColumnDescriptor.Builder(proto().buildingName()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().address()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().common().owner().company().name()).title(i18n.tr("Owner")).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().common().propertyManger().name()).title(i18n.tr("Property Manager")).build(),                
                    new MemberColumnDescriptor.Builder(proto().complexName()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().unitName()).build(),
                    new MemberColumnDescriptor.Builder(proto().floorplanName()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().floorplanMarketingName()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().vacancyStatus()).build(),
                    new MemberColumnDescriptor.Builder(proto().rentedStatus()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().isScoped()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().rentReadinessStatus()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().unitRent()).build(),
                    new MemberColumnDescriptor.Builder(proto().marketRent()).build(),
                    new MemberColumnDescriptor.Builder(proto().rentDeltaAbsolute()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().rentDeltaRelative()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().moveOutDay()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().moveInDay()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().rentedFromDate()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().daysVacant()).build(),
                    new MemberColumnDescriptor.Builder(proto().revenueLost()).build()
            );//@formatter:on
        }

        @Override
        public ISetup getSetup() {
            return new SetupForm(new CEntityDecoratableEditor<UnitAvailability>(UnitAvailability.class) {
                @Override
                public IsWidget createContent() {
                    FormFlexPanel p = new FormFlexPanel();
                    int row = -1;
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().refreshInterval())).build());
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().pageSize())).build());
                    p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().defaultFilteringPreset())).build());
                    return p;
                }
            });
        }

    }

    public UnitAvailabilityReportGadget() {
        super(UnitAvailability.class);
    }

    @Override
    public String getDescription() {
        return "Shows the information about units, whether they are available or rented, how long they have been vacant for and revenue lost as a result. Can be customized to show various information about buildings and units, for example their physical condition.";
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Availability.toString());
    }

    @Override
    public boolean isBuildingGadget() {
        return true;
    }

    @Override
    protected GadgetInstanceBase<UnitAvailability> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new UnitAvailabilityReportGadgetImpl(gadgetMetadata);
    }
}