/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.crm.rpc.services.CityIntroPageCrudService;
import com.propertyvista.domain.site.CityIntroPage;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.server.common.reference.PMSiteContentCache;

public class CityIntroPageCrudServiceImpl extends AbstractCrudServiceImpl<CityIntroPage> implements CityIntroPageCrudService {

    public CityIntroPageCrudServiceImpl() {
        super(CityIntroPage.class);
    }

    @Override
    protected void bind() {
        this.bindCompleteDBO();
    }

    @Override
    protected void persist(CityIntroPage entity, CityIntroPage dto) {
        EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
        SiteDescriptor site = Persistence.service().retrieve(criteria);
        if (entity.getPrimaryKey() == null) {
            site.cityIntroPages().add(entity);
            Persistence.service().merge(site);
        } else {
            super.persist(entity, dto);
        }
        PMSiteContentCache.siteDescriptorUpdated();
    }
}
