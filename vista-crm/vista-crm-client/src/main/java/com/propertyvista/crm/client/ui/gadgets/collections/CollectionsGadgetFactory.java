/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.collections;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.shared.IObject;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.components.PaymentDetailsFactory;
import com.propertyvista.crm.rpc.dto.gadgets.CollectionsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.CollectionsGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.CollectionsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class CollectionsGadgetFactory extends AbstractGadget<CollectionsGadgetMetadata> {

    public static class CollectionsGaget extends CounterGadgetInstanceBase<CollectionsGadgetDataDTO, Vector<Building>, CollectionsGadgetMetadata> {

        public CollectionsGaget(GadgetMetadata metadata) {
            super(CollectionsGadgetDataDTO.class, GWT.<CollectionsGadgetService> create(CollectionsGadgetService.class), new CollectionsSummaryForm(),
                    metadata, CollectionsGadgetMetadata.class);
        }

        @Override
        protected Vector<Building> prepareSummaryQuery() {
            return new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs());
        }

        @Override
        protected void bindDetailsFactories() {
            bindPaymentDetailsFactory(proto().fundsCollectedThisMonth());
            bindPaymentDetailsFactory(proto().fundsInProcessing());
        }

        private void bindPaymentDetailsFactory(IObject<?> member) {
            bindDetailsFactory(member, new PaymentDetailsFactory(GWT.<CollectionsGadgetService> create(CollectionsGadgetService.class), this, member.getPath()
                    .toString()));
        }
    }

    public CollectionsGadgetFactory() {
        super(CollectionsGadgetMetadata.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Payments.toString());
    }

    @Override
    protected GadgetInstanceBase<CollectionsGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new CollectionsGaget(gadgetMetadata);
    }

}
