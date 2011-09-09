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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.site.PageCaption;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;

public class PageDescriptorCrudServiceImpl extends GenericCrudServiceImpl<PageDescriptor> implements PageDescriptorCrudService {

    public PageDescriptorCrudServiceImpl() {
        super(PageDescriptor.class);
    }

    @Override
    protected void enhanceRetrieve(PageDescriptor entity, boolean fromList) {
        if (!fromList) {
            // load content caption:
            for (PageContent content : entity.content()) {
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
    public void save(AsyncCallback<PageDescriptor> callback, PageDescriptor entity) {

        // update caption:
        entity.caption().clear();
        for (PageContent content : entity.content()) {
            content._caption().locale().set(content.locale());
            entity.caption().add(content._caption());
        }

        super.save(callback, entity);
    }
}
