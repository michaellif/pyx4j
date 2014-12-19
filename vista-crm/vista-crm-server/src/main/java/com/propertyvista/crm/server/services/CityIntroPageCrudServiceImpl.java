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
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.CityIntroPageCrudService;
import com.propertyvista.domain.site.CityIntroPage;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.server.common.reference.PMSiteContentCache;

public class CityIntroPageCrudServiceImpl extends AbstractCrudServiceImpl<CityIntroPage> implements CityIntroPageCrudService {

    private static final I18n i18n = I18n.get(CityIntroPageCrudServiceImpl.class);

    public CityIntroPageCrudServiceImpl() {
        super(CityIntroPage.class);
    }

    @Override
    protected boolean persist(CityIntroPage bo, CityIntroPage to) {
        // check for city duplicates
        CityIntroPage dup = findDuplicate(to);
        if (dup != null) {
            throw new UserRuntimeException(i18n.tr("CityIntroPage already exists for city: {0}, {1}", dup.cityName().getValue(), dup.province().getValue()));
        }

        EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
        SiteDescriptor site = Persistence.service().retrieve(criteria);
        if (bo.getPrimaryKey() == null) {
            site.cityIntroPages().add(bo);
            Persistence.service().merge(site);
        } else {
            super.persist(bo, to);
        }
        PMSiteContentCache.siteDescriptorUpdated();
        return true;
    }

    private CityIntroPage findDuplicate(CityIntroPage newPage) {
        EntityQueryCriteria<CityIntroPage> criteria = EntityQueryCriteria.create(CityIntroPage.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().province(), newPage.province()));
        String newCity = preprocess(newPage.cityName().getValue());
        for (CityIntroPage page : Persistence.service().query(criteria)) {
            if (newCity.equalsIgnoreCase(preprocess(page.cityName().getValue()))) {
                return page;
            }
        }
        return null;
    }

    private String preprocess(String city) {
        return city.replaceAll("[\\W_]", "");
    }
}
