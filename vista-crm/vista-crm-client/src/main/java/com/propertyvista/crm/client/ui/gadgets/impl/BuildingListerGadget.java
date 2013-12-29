/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.impl;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;

import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.forms.BuildingListerGadgetMetadataForm;
import com.propertyvista.crm.client.ui.gadgets.util.ListerUtils;
import com.propertyvista.crm.client.ui.gadgets.util.ListerUtils.ItemSelectCommand;
import com.propertyvista.crm.client.ui.gadgets.util.Provider;
import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingListerGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.dto.BuildingDTO;

public class BuildingListerGadget extends GadgetInstanceBase<BuildingListerGadgetMetadata> {

    private static final I18n i18n = I18n.get(BuildingListerGadget.class);

    static List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;
    static {
        BuildingDTO proto = EntityFactory.getEntityPrototype(BuildingDTO.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto.complex()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.propertyCode()).build(),
                new MemberColumnDescriptor.Builder(proto.propertyManager()).build(),
                new MemberColumnDescriptor.Builder(proto.marketing().name()).title(i18n.ntr("Marketing Name")).build(),
                new MemberColumnDescriptor.Builder(proto.info().name()).build(),
                new MemberColumnDescriptor.Builder(proto.info().type()).build(),
                new MemberColumnDescriptor.Builder(proto.info().shape()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.info().address().streetName()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.info().address().city()).build(),
                new MemberColumnDescriptor.Builder(proto.info().address().province()).build(),
                new MemberColumnDescriptor.Builder(proto.info().address().country()).build()
        );//@formatter:on
    }

    private EntityDataTablePanel<BuildingDTO> lister;

    public BuildingListerGadget(BuildingListerGadgetMetadata metadata) {
        super(metadata, BuildingListerGadgetMetadata.class, new BuildingListerGadgetMetadataForm());
        setDefaultPopulator(new Populator() {
            @Override
            public void populate() {
                lister.getDataTablePanel().setPageSize(getMetadata().buildingListerSettings().pageSize().getValue());
                lister.obtain(0);
                populateSucceded();
            }
        });
    }

    @Override
    protected Widget initContentPanel() {
        lister = new EntityDataTablePanel<BuildingDTO>(BuildingDTO.class, true, false);
        lister.setSize("100%", "100%");
        lister.setDataSource(new ListerDataSource<BuildingDTO>(BuildingDTO.class, GWT.<BuildingCrudService> create(BuildingCrudService.class)));
        ListerUtils.bind(lister.getDataTablePanel())//@formatter:off
            .columnDescriptors(DEFAULT_COLUMN_DESCRIPTORS)            
            .setupable(ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getMetadata().ownerUser().getPrimaryKey()))
            .userSettingsProvider(new Provider<ListerUserSettings>() {
                @Override
                public ListerUserSettings get() {
                    return getMetadata().buildingListerSettings();
                }
             })
            .onColumnSelectionChanged(new Command() {
                @Override
                public void execute() {
                    saveMetadata();
                }
            })
            .onItemSelectedCommand(new ItemSelectCommand<BuildingDTO>() {                
                @Override
                public void execute(BuildingDTO item) {
                    AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(item.getInstanceValueClass()).formViewerPlace(item.getPrimaryKey()));                    
                }
            })
            .init();//@formatter:on       
        return lister;
    }

}