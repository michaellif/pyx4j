/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 3, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.arrears;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;

import com.propertyvista.crm.client.ui.gadgets.ListerGadgetBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupTenantsArrearsDTO;

public class ArrearsListerGadget extends ListerGadgetBase<MockupTenantsArrearsDTO> implements IBuildingGadget {

    public ArrearsListerGadget(GadgetMetadata gmd) {
        super(gmd, MockupTenantsArrearsDTO.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<ColumnDescriptor<MockupTenantsArrearsDTO>> getDefaultColumnDescriptors(MockupTenantsArrearsDTO proto) {
        return Arrays.asList(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.belongsTo().belongsTo().propertyCode()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.belongsTo().info().number()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.firstName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lastName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.arrears1MonthAgo()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.arrears2MonthsAgo()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.arrears3MonthsAgo()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.arrears4MonthsAgo()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.arBalance()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.prepayments()));

    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<ColumnDescriptor<MockupTenantsArrearsDTO>> getAvailableColumnDescriptors(MockupTenantsArrearsDTO proto) {
        return Arrays.asList(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.belongsTo().belongsTo().propertyCode()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.belongsTo().info().number()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.firstName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lastName()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.arrears1MonthAgo()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.arrears2MonthsAgo()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.arrears3MonthsAgo()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.arrears4MonthsAgo()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.arBalance()),
                ColumnDescriptorFactory.createColumnDescriptor(proto, proto.prepayments()));
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.ArrearsGadget);
        gmd.name().setValue(GadgetType.ArrearsGadget.toString());
    }

    @Override
    public Widget asWidget() {
        return getListerWidget().asWidget();
    }

    @Override
    public void setFiltering(FilterData filterData) {
        // TODO Auto-generated method stub        
    }

    @Override
    public void populatePage(int pageNumber) {
        // TODO Auto-generated method stub
    }
}
