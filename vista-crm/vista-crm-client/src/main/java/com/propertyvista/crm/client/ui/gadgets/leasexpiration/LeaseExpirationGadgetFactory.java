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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeaseExpirationGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.LeaseExpirationGadgetMeta;
import com.propertyvista.domain.property.asset.building.Building;

public class LeaseExpirationGadgetFactory extends AbstractGadget<LeaseExpirationGadgetMeta> {

    public class LeaseExpriationGadget extends GadgetInstanceBase<LeaseExpirationGadgetMeta> {

        private CEntityDecoratableForm<LeaseExpirationGadgetDataDTO> form;

        private IBuildingFilterContainer board;

        public LeaseExpriationGadget(GadgetMetadata metadata) {
            super(metadata, LeaseExpirationGadgetMeta.class);
            final LeaseExpirationGadgetService service = GWT.<LeaseExpirationGadgetService> create(LeaseExpirationGadgetService.class);

            setDefaultPopulator(new Populator() {

                @Override
                public void populate() {
                    service.leaseExpriation(new DefaultAsyncCallback<LeaseExpirationGadgetDataDTO>() {

                        @Override
                        public void onSuccess(LeaseExpirationGadgetDataDTO result) {
                            form.populate(result);
                        }

                    }, new Vector<Building>(board.getSelectedBuildingsStubs()));
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
            form = new CEntityDecoratableForm<LeaseExpirationGadgetDataDTO>(LeaseExpirationGadgetDataDTO.class) {

                @Override
                public IsWidget createContent() {
                    int row = 0;

                    FormFlexPanel panel = new FormFlexPanel();
                    panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unitOccupancyPct())).build());
                    panel.setWidget(row, 1, new DecoratorBuilder(inject(proto().unitsOccupied())).build());
                    panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numOfLeasesEndingThisMonth())).build());
                    panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numOfLeasesEndingNextMonth())).build());
                    panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numOfLeasesEndingOver90Days())).build());
                    panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numOfLeasesOnMonthToMonth())).build());

                    return panel;
                }
            };
            form.initContent();

            return form.asWidget();
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
        return new LeaseExpriationGadget(gadgetMetadata);
    }

}
