/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.site.PageCaption;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.SiteDescriptor;

public class PageDescriptorCrudServiceImpl extends GenericCrudServiceImpl<PageDescriptor> implements PageDescriptorCrudService {

    private final static Logger log = LoggerFactory.getLogger(PageDescriptorCrudServiceImpl.class);

    public PageDescriptorCrudServiceImpl() {
        super(PageDescriptor.class);
    }

    @Override
    protected void enhanceRetrieve(PageDescriptor entity, boolean fromList) {
        if (!fromList) {
            // load content caption:
            for (PageContent content : entity.content()) {

                // TODO VladS remove this hack!
                content.descriptor().set(entity);

                for (PageCaption caption : entity.caption()) {
                    if (content.locale().equals(caption.locale())) {
                        content._caption().set(caption);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void persistDBO(PageDescriptor dbo) {
        // update caption:
        dbo.caption().clear();
        for (PageContent content : dbo.content()) {
            content._caption().locale().set(content.locale());
            dbo.caption().add(content._caption());
        }

        boolean isCreate = dbo.id().isNull();
        if (dbo.type().isNull()) {
            dbo.type().setValue(PageDescriptor.Type.staticContent);
        }

        super.persistDBO(dbo);

        // update parent child list: 
        if (isCreate) {
            log.debug("1. create child of ", dbo);
            Persistence.service().retrieve(dbo.parent());
            dbo.parent().childPages().add(dbo);
            log.debug("2. create child of ", dbo);
            Persistence.service().merge(dbo.parent());
        } else {
            EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
            SiteDescriptor siteDescriptor = Persistence.service().retrieve(criteria);
            siteDescriptor._updateFlag().updated().setValue(new Date());
            Persistence.service().persist(siteDescriptor);
        }
    }
}
