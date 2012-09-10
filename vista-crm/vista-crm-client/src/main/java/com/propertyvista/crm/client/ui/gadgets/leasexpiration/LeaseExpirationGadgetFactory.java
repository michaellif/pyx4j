/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.leasexpiration;

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

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeaseExpirationGadgetService;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.LeaseExpirationGadgetMeta;
import com.propertyvista.domain.dashboard.gadgets.type.LeaseExpirationGadgetMeta.GadgetView;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.LeaseDTO;

public class LeaseExpirationGadgetFactory extends AbstractGadget<LeaseExpirationGadgetMeta> {

    public class LeaseExpirationGadget extends GadgetInstanceBase<LeaseExpirationGadgetMeta> {

        private CEntityDecoratableForm<LeaseExpirationGadgetDataDTO> expirationSummaryForm;

        private IBuildingFilterContainer board;

        private LeaseExpirationDetailsLister leaseExpirationDetailsLister;

        public LeaseExpirationGadget(GadgetMetadata metadata) {
            super(metadata, LeaseExpirationGadgetMeta.class);
            final LeaseExpirationGadgetService service = GWT.<LeaseExpirationGadgetService> create(LeaseExpirationGadgetService.class);

            setDefaultPopulator(new Populator() {

                @Override
                public void populate() {
                    GadgetView activeView = getMetadata().activeView().getValue();
                    boolean displaySummary = (activeView == GadgetView.SUMMARY | activeView == null);
                    boolean displayLeaseDetails = activeView == GadgetView.LEASES_ENDING_90_DAYS | activeView == GadgetView.LEASES_ENDING_NEXT_MONTH
                            | activeView == GadgetView.LEASES_ENDING_THIS_MONTH | activeView == GadgetView.LEASES_ON_MONTH_TO_MONTH;

                    expirationSummaryForm.setVisible(displaySummary);
                    leaseExpirationDetailsLister.setVisible(displayLeaseDetails);

                    if (displaySummary) {

                        service.leaseExpriation(new DefaultAsyncCallback<LeaseExpirationGadgetDataDTO>() {

                            @Override
                            public void onSuccess(LeaseExpirationGadgetDataDTO result) {
                                expirationSummaryForm.populate(result);
                                populateSucceded();
                            }

                        }, new Vector<Building>(board.getSelectedBuildingsStubs()));

                    } else if (displayLeaseDetails) {
                        service.listCriteria(new DefaultAsyncCallback<EntityListCriteria<LeaseDTO>>() {

                            @Override
                            public void onSuccess(EntityListCriteria<LeaseDTO> result) {
                                ListerDataSource<LeaseDTO> listerDataSource = new ListerDataSource<LeaseDTO>(LeaseDTO.class, GWT
                                        .<AbstractListService<LeaseDTO>> create(LeaseViewerCrudService.class));
                                listerDataSource.setPreDefinedFilters(result.getFilters());
                                leaseExpirationDetailsLister.setDataSource(listerDataSource);

                                leaseExpirationDetailsLister.obtain(0);
                                populateSucceded();
                            }
                        }, new Vector<Building>(board.getSelectedBuildingsStubs()), activeView);
                    }
                }

            });
        }

        @Override
        public void setContainerBoard(IBuildingFilterContainer board) {
            this.board = board;
            this.board.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {

                @Override
                public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                    populate();
                }

            });
        }

        @Override
        protected Widget initContentPanel() {
            FlowPanel contentPanel = new FlowPanel();
            expirationSummaryForm = new LeaseExpirationSummaryForm(this, LeaseExpirationGadgetDataDTO.class);
            expirationSummaryForm.initContent();
            contentPanel.add(expirationSummaryForm);

            leaseExpirationDetailsLister = new LeaseExpirationDetailsLister();
            contentPanel.add(leaseExpirationDetailsLister);

            expirationSummaryForm.setVisible(true);
            leaseExpirationDetailsLister.setVisible(false);

            return contentPanel;
        }

        Command openListCmd() {
            return new Command() {
                @Override
                public void execute() {
                    getMetadata().activeView().setValue(GadgetView.LEASES_ENDING_THIS_MONTH);
                    populate();
                }
            };
        }

    }

    public LeaseExpirationGadgetFactory() {
        super(LeaseExpirationGadgetMeta.class);
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
    protected GadgetInstanceBase<LeaseExpirationGadgetMeta> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new LeaseExpirationGadget(gadgetMetadata);
    }

}
