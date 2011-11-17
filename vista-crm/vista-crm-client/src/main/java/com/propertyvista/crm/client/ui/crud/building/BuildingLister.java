/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.BuildingDTO;

public class BuildingLister extends ListerBase<BuildingDTO> {

    public BuildingLister() {
        super(BuildingDTO.class, CrmSiteMap.Properties.Building.class);

//        // add custom actions (Buttons) here:
//        getListPanel().getDataTable().setHasCheckboxColumn(true);
//
//        addActionButton(new Button("Action1", new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                // TODO Auto-generated method stub
//            }
//        }));
//
//        addActionButton(new Button("Action2", new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                // TODO Auto-generated method stub
//            }
//        }));
    }

    @Override
    protected List<ColumnDescriptor<BuildingDTO>> getDefaultColumnDescriptors(BuildingDTO proto) {
        List<ColumnDescriptor<BuildingDTO>> columnDescriptors = new ArrayList<ColumnDescriptor<BuildingDTO>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyManager()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().type()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().streetName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().city()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().province()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().dateAcquired()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().purchasePrice()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().lastAppraisalDate()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().lastAppraisalValue()));

        columnDescriptors.add(ColumnDescriptorFactory.createTitledColumnDescriptor(proto, proto.marketing().name(), i18n.tr("Marketing Name")));
        return columnDescriptors;
    }

    @Override
    protected List<ColumnDescriptor<BuildingDTO>> getAvailableColumnDescriptors(BuildingDTO proto) {
        List<ColumnDescriptor<BuildingDTO>> columnDescriptors = new ArrayList<ColumnDescriptor<BuildingDTO>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.complex()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyManager()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().shape()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().streetNumber()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().streetNumberSuffix()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().streetName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().streetType()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().streetDirection()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().city()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().country()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().province()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().country()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().totalStoreys()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().residentialStoreys()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().structureType()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().structureBuildYear()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().constructionType()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().foundationType()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().floorType()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().landArea()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().waterSupply()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().centralAir()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().centralHeat()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.contacts().website()));
        columnDescriptors.add(ColumnDescriptorFactory.createTitledColumnDescriptor(proto, proto.contacts().email().address(), i18n.tr("Email")));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().dateAcquired()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().purchasePrice()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().marketPrice()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().lastAppraisalDate()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().lastAppraisalValue()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().currency()));

        columnDescriptors.add(ColumnDescriptorFactory.createTitledColumnDescriptor(proto, proto.marketing().name(), i18n.tr("Marketing Name")));
        return columnDescriptors;
    }
}
