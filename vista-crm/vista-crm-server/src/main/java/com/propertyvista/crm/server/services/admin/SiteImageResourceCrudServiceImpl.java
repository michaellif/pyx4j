/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2012
 * @author dev_vista
 * @version $Id$
 */
package com.propertyvista.crm.server.services.admin;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.crm.rpc.services.admin.SiteImageResourceCrudService;
import com.propertyvista.domain.site.SiteImageResource;

public class SiteImageResourceCrudServiceImpl extends AbstractCrudServiceImpl<SiteImageResource> implements SiteImageResourceCrudService {

    public SiteImageResourceCrudServiceImpl() {
        super(SiteImageResource.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

}
