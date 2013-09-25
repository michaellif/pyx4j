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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
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
    protected void bind() {
        this.bindCompleteObject();
    }

    @Override
    public void save(AsyncCallback<Key> callback, CityIntroPage dto) {
        // check for city duplicates
        CityIntroPage dup = findDuplicate(dto);
        if (dup != null) {
            callback.onFailure(new UserRuntimeException(i18n.tr("CityIntroPage already exists for city: {0}, {1}", dup.cityName().getValue(), dup.province()
                    .name().getValue())));
        } else {
            super.save(callback, dto);
        }
    }

    @Override
    protected void persist(CityIntroPage bo, CityIntroPage to) {
        EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
        SiteDescriptor site = Persistence.service().retrieve(criteria);
        if (bo.getPrimaryKey() == null) {
            site.cityIntroPages().add(bo);
            Persistence.service().merge(site);
        } else {
            super.persist(bo, to);
        }
        PMSiteContentCache.siteDescriptorUpdated();
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
