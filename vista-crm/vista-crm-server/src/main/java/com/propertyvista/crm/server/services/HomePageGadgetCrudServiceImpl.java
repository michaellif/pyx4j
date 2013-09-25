/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 27, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.crm.rpc.services.HomePageGadgetCrudService;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.gadgets.GadgetContent;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.domain.site.gadgets.HomePageGadget.GadgetType;
import com.propertyvista.server.common.reference.PMSiteContentCache;

public class HomePageGadgetCrudServiceImpl extends AbstractCrudServiceImpl<HomePageGadget> implements HomePageGadgetCrudService {

    public HomePageGadgetCrudServiceImpl() {
        super(HomePageGadget.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected HomePageGadget init(InitializationData initializationData) {
        HomePageGadgetInitializationData initData = (HomePageGadgetInitializationData) initializationData;

        HomePageGadget newItem = EntityFactory.create(HomePageGadget.class);
        newItem.type().setValue(initData.type().getValue());

        return newItem;
    }

    @Override
    protected void enhanceRetrieved(HomePageGadget bo, HomePageGadget to, RetrieveTarget retrieveTarget) {
        // set the gadget type based on the content
        @SuppressWarnings("unchecked")
        GadgetType type = GadgetType.getGadgetType((Class<? extends GadgetContent>) bo.content().getInstanceValueClass());
        to.type().setValue(type);
    }

    @Override
    protected void persist(HomePageGadget bo, HomePageGadget to) {
        EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
        SiteDescriptor site = Persistence.service().retrieve(criteria);
        if (bo.getPrimaryKey() == null) {
            // update corresponding gadget list
            switch (to.area().getValue()) {
            case wide:
                site.homePageGadgetsWide().add(bo);
                break;
            case narrow:
                site.homePageGadgetsNarrow().add(bo);
                break;
            }
            Persistence.service().merge(site);
        } else {
            super.persist(bo, to);
        }
        PMSiteContentCache.siteDescriptorUpdated();
    }
}
