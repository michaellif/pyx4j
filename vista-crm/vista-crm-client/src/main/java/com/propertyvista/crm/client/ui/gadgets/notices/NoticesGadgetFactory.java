/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.notices;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.leasexpiration.LeasesDetailsLister;
import com.propertyvista.crm.client.ui.gadgets.leasexpiration.UnitsDetailsLister;
import com.propertyvista.crm.rpc.dto.gadgets.NoticesGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.NoticesGadgetService;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.NoticesGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.NoticesGadgetMetadata.GadgetMode;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.LeaseDTO;

public class NoticesGadgetFactory extends AbstractGadget<NoticesGadgetMetadata> {

    public static class NoticesGadget extends GadgetInstanceBase<NoticesGadgetMetadata> {

        private NoticesSummaryForm summaryForm;

        private UnitsDetailsLister unitsDetailsLister;

        private LeasesDetailsLister leasesDetailsLister;

        private final NoticesGadgetService service;

        private IBuildingFilterContainer buildingsFilterContainer;

        public NoticesGadget(GadgetMetadata metadata) {
            super(metadata, NoticesGadgetMetadata.class);
            service = GWT.<NoticesGadgetService> create(NoticesGadgetService.class);
            setDefaultPopulator(new Populator() {

                @Override
                public void populate() {
                    NoticesGadgetMetadata.GadgetMode gadgetMode = getMetadata().activeMode().getValue();
                    gadgetMode = gadgetMode != null ? gadgetMode : GadgetMode.SUMMARY;

                    summaryForm.setVisible(gadgetMode == GadgetMode.SUMMARY);
                    leasesDetailsLister.setVisible(gadgetMode == gadgetMode.NOTICES_DETAILS);
                    unitsDetailsLister.setVisible(gadgetMode == GadgetMode.VACANT_UNITS_DETAILS);

                    switch (gadgetMode) {
                    case SUMMARY:
                        service.notices(new DefaultAsyncCallback<NoticesGadgetDataDTO>() {
                            @Override
                            public void onSuccess(NoticesGadgetDataDTO result) {
                                summaryForm.populate(result);
                                populateSucceded();
                            }
                        }, new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs()));
                        break;
                    case NOTICES_DETAILS:
                        service.makeNoticesFilterCriteria(new DefaultAsyncCallback<EntityListCriteria<LeaseDTO>>() {

                            @Override
                            public void onSuccess(EntityListCriteria<LeaseDTO> result) {
                                ListerDataSource<LeaseDTO> leasesDataSource = new ListerDataSource<LeaseDTO>(LeaseDTO.class, GWT
                                        .<LeaseViewerCrudService> create(LeaseViewerCrudService.class));
                                leasesDataSource.setPreDefinedFilters(result.getFilters());
                                leasesDetailsLister.setDataSource(leasesDataSource);
                                leasesDetailsLister.obtain(0);
                                populateSucceded();
                            }

                        }, getMetadata().activeNoticesFilter().getValue(), new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs()));

                        break;
                    case VACANT_UNITS_DETAILS:
                        service.makeVacantUnitsFilterCriteria(new DefaultAsyncCallback<EntityListCriteria<AptUnitDTO>>() {
                            @Override
                            public void onSuccess(EntityListCriteria<AptUnitDTO> result) {
                                ListerDataSource<AptUnitDTO> vacantUnitsDataSource = new ListerDataSource<AptUnitDTO>(AptUnitDTO.class, GWT
                                        .<AbstractListService<AptUnitDTO>> create(UnitCrudService.class));
                                vacantUnitsDataSource.setPreDefinedFilters(result.getFilters());
                                unitsDetailsLister.setDataSource(vacantUnitsDataSource);
                                unitsDetailsLister.obtain(0);
                                populateSucceded();
                            }
                        }, new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs()));
                        break;

                    }
                }

            });
        }

        @Override
        public void setContainerBoard(IBuildingFilterContainer buildingsFilterContainer) {
            this.buildingsFilterContainer = buildingsFilterContainer;
            this.buildingsFilterContainer.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
                @Override
                public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                    populate();
                }
            });
        }

        @Override
        protected Widget initContentPanel() {

            FlowPanel gadgetContent = new FlowPanel();

            summaryForm = new NoticesSummaryForm(this);
            summaryForm.initContent();
            summaryForm.setVisible(false);
            gadgetContent.add(summaryForm);

            leasesDetailsLister = new LeasesDetailsLister(backToSummary());
            leasesDetailsLister.setVisible(false);
            gadgetContent.add(leasesDetailsLister);

            unitsDetailsLister = new UnitsDetailsLister(backToSummary());
            unitsDetailsLister.setVisible(false);
            gadgetContent.add(unitsDetailsLister);

            return gadgetContent;
        }

        Command displayNotices(final NoticesGadgetMetadata.NoticesFilter filter) {
            return new Command() {
                @Override
                public void execute() {
                    getMetadata().activeMode().setValue(NoticesGadgetMetadata.GadgetMode.NOTICES_DETAILS);
                    getMetadata().activeNoticesFilter().setValue(filter);
                    saveMetadata();
                    populate();
                }
            };
        }

        Command displayVacantUnits() {
            return new Command() {
                @Override
                public void execute() {
                    getMetadata().activeMode().setValue(GadgetMode.VACANT_UNITS_DETAILS);
                    saveMetadata();
                    populate();
                }
            };
        }

        Command backToSummary() {
            return new Command() {
                @Override
                public void execute() {
                    getMetadata().activeMode().setValue(GadgetMode.SUMMARY);
                    saveMetadata();
                    populate();
                }
            };
        }

    }

    public NoticesGadgetFactory() {
        super(NoticesGadgetMetadata.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Leases.toString());
    }

    @Override
    public boolean isBuildingGadget() {
        return true;
    }

    @Override
    protected GadgetInstanceBase<NoticesGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new NoticesGadget(gadgetMetadata);
    }

}
