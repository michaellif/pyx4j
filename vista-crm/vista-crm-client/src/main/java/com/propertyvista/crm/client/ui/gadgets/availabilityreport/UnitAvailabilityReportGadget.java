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
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.crm.client.ui.board.BoardBase.DebugIds;
import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.IBuildingBoardGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatusDTO;
import com.propertyvista.domain.dashboard.gadgets.type.AvailabilitySummary;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability.FilterPreset;
import com.propertyvista.domain.property.asset.building.Building;

public class UnitAvailabilityReportGadget extends AbstractGadget<UnitAvailability> {

    private static final I18n i18n = I18n.get(UnitAvailabilityReportGadget.class);

    public static class UnitAvailabilityReportGadgetInstance extends ListerGadgetInstanceBase<UnitAvailabilityStatusDTO, UnitAvailability> implements
            IBuildingBoardGadgetInstance {
    	
    	public static enum DebugIds implements IDebugId {
    		
    		allFilter, vacantFilter, noticeFilter, vacantAndNoticeFilter, rentedFilter, netExposureFilter;

			@Override
			public String debugId() {				
				return this.name();
			} 
    	}
    	
        private VerticalPanel gadgetPanel;
    
        private FlexTable controlsPanel;
    
        private List<FilterButton> filteringButtons;
    
        private FilterButtonClickHanlder filterButtonClickHandler;
        
        private final AvailabilityReportService service;

        public UnitAvailabilityReportGadgetInstance(GadgetMetadata gmd) {
            super(gmd, UnitAvailabilityStatusDTO.class, UnitAvailability.class);
            service = GWT.create(AvailabilityReportService.class);
            filterButtonClickHandler = new FilterButtonClickHanlder();
        }

        @Override
        protected UnitAvailability createDefaultSettings(Class<UnitAvailability> metadataClass) {
            UnitAvailability settings = super.createDefaultSettings(metadataClass);
            settings.defaultFilteringPreset().setValue(FilterPreset.All);
            return settings;
        }

        @Override
        public void setContainerBoard(final BoardView board) {
            super.setContainerBoard(board);
            board.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
                @Override
                public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                    populate();
                }
            });
        }

        @Override
        public void populatePage(int pageNumber) {
            if (containerBoard.getSelectedBuildings() == null) {
                setPageData(new Vector<UnitAvailabilityStatusDTO>(), 0, 0, false);
                populateSucceded();
                return;
            }

            final int page = pageNumber;            
            final Vector<Key> buildingPks = new Vector<Key>(containerBoard.getSelectedBuildings().size());
            for (Building b : containerBoard.getSelectedBuildings()) {
                buildingPks.add(b.getPrimaryKey());
            }

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
            }, buildingPks, getMetadata().defaultFilteringPreset().getValue(), getStatusDate(), new Vector<Sort>(getSorting()),
                    pageNumber, getPageSize());
        }

        @Override
        public Widget initContentPanel() {
        	
            gadgetPanel = new VerticalPanel();
            gadgetPanel.add(initFilteringConrolsPanel());
            gadgetPanel.add(initListerWidget());
            gadgetPanel.setCellHorizontalAlignment(controlsPanel, VerticalPanel.ALIGN_CENTER);
            gadgetPanel.setWidth("100%");

            return gadgetPanel;
        }
        
        private Widget initFilteringConrolsPanel() {
        	
            controlsPanel = new FlexTable();
            
            filteringButtons = Arrays.asList(//@formatter:off
            		new FilterButton(FilterPreset.All, DebugIds.allFilter),
            		new FilterButton(FilterPreset.Vacant, DebugIds.vacantFilter),
            		new FilterButton(FilterPreset.Notice, DebugIds.noticeFilter),
            		new FilterButton(FilterPreset.VacantAndNotice, DebugIds.vacantAndNoticeFilter),
            		new FilterButton(FilterPreset.Rented, DebugIds.rentedFilter),
            		new FilterButton(FilterPreset.NetExposure, DebugIds.netExposureFilter)
            );//@formatter:on

            int col = -1;
            for (ToggleButton button : filteringButtons) {
                controlsPanel.setWidget(0, ++col, button);
            }

        	return controlsPanel;
        }

        @Override
        public void start() {
            setupFilteringButtons();
            super.start();
        }

        /**
         * Set the filtering buttons according to the state in metadata.
         */
        private void setupFilteringButtons() {
            for (FilterButton button : filteringButtons) {
                if (button.filterPreset ==  getMetadata().defaultFilteringPreset().getValue()) {
                    button.setValue(true, false);
                } else {
                    button.setValue(false, false);
                }
            }
        }

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
        
        private class FilterButton extends ToggleButton {
        	
        	public final UnitAvailability.FilterPreset filterPreset;
        	        	
        	public FilterButton(FilterPreset filterPreset, IDebugId debugId) {        		
        		super(new SafeHtmlBuilder().appendEscaped(filterPreset.toString()).toSafeHtml().asString());
        		this.filterPreset = filterPreset;
        		this.ensureDebugId(debugId.toString());
        		this.addClickHandler(filterButtonClickHandler);        		
        	}
        	
        }

        private class FilterButtonClickHanlder implements ClickHandler {
        	
            @Override
            public void onClick(ClickEvent event) {
                for (FilterButton button : filteringButtons) {
                    if (event.getSource().equals(button)) {                    	
                        button.setDown(true);
                        getMetadata().defaultFilteringPreset().setValue(button.filterPreset);
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
        return new UnitAvailabilityReportGadgetInstance(gadgetMetadata);
    }
}