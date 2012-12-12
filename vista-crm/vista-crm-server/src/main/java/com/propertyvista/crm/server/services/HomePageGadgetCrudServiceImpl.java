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
    protected void enhanceRetrieved(HomePageGadget entity, HomePageGadget dto, RetrieveTraget retrieveTraget) {
        // set the gadget type based on the content
        @SuppressWarnings("unchecked")
        GadgetType type = GadgetType.getGadgetType((Class<? extends GadgetContent>) entity.content().getInstanceValueClass());
        dto.type().setValue(type);
    }

    @Override
    protected void persist(HomePageGadget entity, HomePageGadget dto) {
        EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
        SiteDescriptor site = Persistence.service().retrieve(criteria);
        if (entity.getPrimaryKey() == null) {
            // update corresponding gadget list
            switch (dto.area().getValue()) {
            case wide:
                site.homePageGadgetsWide().add(entity);
                break;
            case narrow:
                site.homePageGadgetsNarrow().add(entity);
                break;
            }
            Persistence.service().merge(site);
        } else {
            super.persist(entity, dto);
        }
        PMSiteContentCache.siteDescriptorUpdated();
    }
}
