/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.crm.rpc.domain.GadgetMetadata;
import com.propertyvista.crm.rpc.domain.GadgetMetadata.GadgetType;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.portal.domain.Building;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;

public class BuildingListerGadget extends ListerGadgetBase<Building> {

    public BuildingListerGadget(GadgetMetadata gmd) {
        super(gmd, Building.class);
        getListPanel().removeUpperActionsBar();
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.BuildingLister);
        gmd.name().setValue(i18n.tr("Building Lister"));
    }

    @Override
    protected void fillColumnDescriptors(List<ColumnDescriptor<Building>> columnDescriptors, Building proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketingName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.buildingType()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.website()));
    }

    @Override
    public void start() {
        super.start();
        populateData();
    }

    public void populateData() {
        BuildingCrudService bcs = GWT.create(BuildingCrudService.class);
        if (bcs != null) {
            bcs.getTestBuildingsList(new AsyncCallback<Vector<Building>>() {
                @Override
                public void onFailure(Throwable caught) {
                }

                @Override
                public void onSuccess(Vector<Building> result) {
                    BuildingListerGadget.this.getListPanel().populateData(result, 0, false);
                }
            });
        }
    }
}
