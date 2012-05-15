/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.arrears;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsDTO;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsGadgetMeta;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class ArrearsStatusGadget extends AbstractGadget<ArrearsGadgetMeta> {

    private static class ArrearsStatusGadgetInstance extends ListerGadgetInstanceBase<ArrearsDTO, ArrearsGadgetMeta> {

        public ArrearsStatusGadgetInstance(GadgetMetadata gmd) {
            super(gmd, ArrearsDTO.class, ArrearsGadgetMeta.class);
        }

        @Override
        protected boolean isFilterRequired() {
            return false;
        }

        @Override
        public List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().lease().unit().belongsTo().propertyCode()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().lease().unit().belongsTo().info().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().lease().unit().belongsTo().info().address().streetNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().lease().unit().belongsTo().info().address().streetName()).build(),                    
                    new MemberColumnDescriptor.Builder(proto().lease().unit().belongsTo().info().address().province().name()).build(),                    
                    new MemberColumnDescriptor.Builder(proto().lease().unit().belongsTo().info().address().country().name()).build(),                    
                    new MemberColumnDescriptor.Builder(proto().lease().unit().belongsTo().complex().name()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().lease().unit().info().number()).build(),
                    new MemberColumnDescriptor.Builder(proto().lease().id()).build(),
                    new MemberColumnDescriptor.Builder(proto().lease().leaseFrom()).build(),
                    new MemberColumnDescriptor.Builder(proto().lease().leaseTo()).build(),
                    
                    // arrears
                    new MemberColumnDescriptor.Builder(proto().buckets().bucketCurrent()).build(),
                    new MemberColumnDescriptor.Builder(proto().buckets().bucket30()).build(),
                    new MemberColumnDescriptor.Builder(proto().buckets().bucket60()).build(),
                    new MemberColumnDescriptor.Builder(proto().buckets().bucket90()).build(),
                    new MemberColumnDescriptor.Builder(proto().buckets().bucketOver90()).build(),
                    
                    new MemberColumnDescriptor.Builder(proto().arBalance()).build(),
                    new MemberColumnDescriptor.Builder(proto().prepayments()).build(),                    
                    new MemberColumnDescriptor.Builder(proto().totalBalance()).build(),
                    new MemberColumnDescriptor.Builder(proto().lmrToUnitRentDifference()).build()
                    
            );//@formatter:on
        }

        @Override
        public void populatePage(int pageNumber) {
            // TODO Auto-generated method stub
        }

        @Override
        public Widget initContentPanel() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    protected ArrearsStatusGadget() {
        super(ArrearsGadgetMeta.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Arrears.toString());
    }

    @Override
    public boolean isBuildingGadget() {
        return true;
    }

    @Override
    protected GadgetInstanceBase<ArrearsGadgetMeta> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new ArrearsStatusGadgetInstance(gadgetMetadata);
    }

}
