/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-07
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.gadgets.impl;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.util.ListerUtils;
import com.propertyvista.crm.client.ui.gadgets.util.Provider;
import com.propertyvista.crm.rpc.dto.gadgets.BuildingResidentInsuranceCoverageDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.BuildingResidentInsuranceListService;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingResidentInsuranceCoverageGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;

public class BuildingResidentInsuranceCoverageGadget extends GadgetInstanceBase<BuildingResidentInsuranceCoverageGadgetMetadata> {

    private static final List<ColumnDescriptor> COLUMN_DESCRIPTORS;

    static {
        BuildingResidentInsuranceCoverageDTO proto = EntityFactory.getEntityPrototype(BuildingResidentInsuranceCoverageDTO.class);
        COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                new ColumnDescriptor.Builder(proto.building()).build(),
                new ColumnDescriptor.Builder(proto.complex()).build(), new ColumnDescriptor.Builder(proto.units()).build(),
                new ColumnDescriptor.Builder(proto.unitsWithInsuranceCount()).build(),
                new ColumnDescriptor.Builder(proto.unitsWithInsuranceShare()).build()                
        );//@formatter:on
    }

    private DataTablePanel<BuildingResidentInsuranceCoverageDTO> lister;

    public BuildingResidentInsuranceCoverageGadget(BuildingResidentInsuranceCoverageGadgetMetadata metadata) {
        super(metadata, BuildingResidentInsuranceCoverageGadgetMetadata.class);
        setDefaultPopulator(new Populator() {
            @Override
            public void populate() {
                lister.getDataTableModel().setPageSize(getMetadata().buildingInsuranceCoverageListerSettings().pageSize().getValue());

                lister.getDataSource().clearPreDefinedFilters();
                if (!containerBoard.getSelectedBuildingsStubs().isEmpty()) {
                    lister.getDataSource().addPreDefinedFilter(
                            PropertyCriterion.in(lister.proto().buildingFilter(), containerBoard.getSelectedBuildingsStubs()));
                }

                lister.populate();
                populateSucceded();
            }
        });
    }

    @Override
    public void setContainerBoard(IBuildingFilterContainer board) {
        super.setContainerBoard(board);
        board.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
            @Override
            public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                populate();
            }
        });
    }

    @Override
    protected Widget initContentPanel() {
        lister = new DataTablePanel<BuildingResidentInsuranceCoverageDTO>(BuildingResidentInsuranceCoverageDTO.class);
        lister.setDataSource(new ListerDataSource<BuildingResidentInsuranceCoverageDTO>(BuildingResidentInsuranceCoverageDTO.class, GWT
                .<BuildingResidentInsuranceListService> create(BuildingResidentInsuranceListService.class)));
        ListerUtils.bind(lister)//@formatter:off
            .columnDescriptors(COLUMN_DESCRIPTORS)
            .setupable(ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getMetadata().ownerUser().getPrimaryKey()))
            .userSettingsProvider(new Provider<ListerUserSettings>() {
                @Override
                public ListerUserSettings get() {                 
                    return getMetadata().buildingInsuranceCoverageListerSettings();
                }
            })
            .onColumnSelectionChanged(new Command() {
                @Override
                public void execute() {
                    saveMetadata();
                }                
            })
            .itemZoomInCommand(new ItemZoomInCommand<BuildingResidentInsuranceCoverageDTO>() {
                @Override
                public void execute(BuildingResidentInsuranceCoverageDTO item) {
                    
                }                
            })
            .init();//@formatter:on
        return lister;
    }
}
