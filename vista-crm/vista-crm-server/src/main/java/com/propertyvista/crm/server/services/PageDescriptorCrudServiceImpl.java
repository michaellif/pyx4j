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

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.domain.site.PageCaption;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.server.common.reference.PMSiteContentCache;

public class PageDescriptorCrudServiceImpl extends AbstractCrudServiceImpl<PageDescriptor> implements PageDescriptorCrudService {

    public PageDescriptorCrudServiceImpl() {
        super(PageDescriptor.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(PageDescriptor entity, PageDescriptor dto, RetrieveTarget retrieveTarget ) {
        // load content caption:
        for (PageContent content : dto.content()) {

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

    @Override
    protected void persist(PageDescriptor dbo, PageDescriptor dto) {
        // update caption:
        dbo.caption().clear();
        for (PageContent content : dbo.content()) {
            content._caption().locale().set(content.locale());
            dbo.caption().add(content._caption());
        }

        if (dbo.type().isNull()) {
            dbo.type().setValue(PageDescriptor.Type.staticContent);
        }

        // place new entry at the end
        if (dbo.orderInDescriptor().isNull()) {

            Persistence.service().retrieve(dbo.parent());
            int maxOrder = 0;
            for (PageDescriptor sibling : dbo.parent().childPages()) {
                Integer order = sibling.orderInDescriptor().getValue();
                if (order != null && maxOrder <= order) {
                    maxOrder = order + 1;
                }
            }
            dbo.orderInDescriptor().setValue(maxOrder);
        }
        super.persist(dbo, dto);

        // set update flag
        PMSiteContentCache.siteDescriptorUpdated();
    }
}
