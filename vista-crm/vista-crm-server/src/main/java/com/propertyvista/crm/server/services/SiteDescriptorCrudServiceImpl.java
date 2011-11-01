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
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.crm.rpc.services.SiteDescriptorCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.crm.server.util.TransientListHelpers;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.News;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.Testimonial;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteDescriptorCrudServiceImpl extends GenericCrudServiceDtoImpl<SiteDescriptor, SiteDescriptorDTO> implements SiteDescriptorCrudService {

    public SiteDescriptorCrudServiceImpl() {
        super(SiteDescriptor.class, SiteDescriptorDTO.class);
    }

    @Override
    public void retrieveHomeItem(AsyncCallback<Key> callback) {
        EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
        List<Key> list = Persistence.service().queryKeys(criteria);
        if (list.isEmpty()) {
            SiteDescriptor site = EntityFactory.create(SiteDescriptor.class);
            Persistence.service().persist(site);
            callback.onSuccess(site.getPrimaryKey());
        } else {
            callback.onSuccess(list.get(0));
        }
    }

    @Override
    protected void persistDBO(SiteDescriptor dbo, SiteDescriptorDTO dto) {
        // save transient data:
        TransientListHelpers.save(dto.news(), News.class);
        TransientListHelpers.save(dto.testimonials(), Testimonial.class);

        //TODO keep the sort order, Remove context for removed Locale
        TransientListHelpers.save(dto.locales(), AvailableLocale.class);

        dbo._updateFlag().updated().setValue(new Date());
        super.persistDBO(dbo, dto);
    }

    @Override
    protected void enhanceDTO(SiteDescriptor dbo, SiteDescriptorDTO dto, boolean fromList) {
        if (!fromList) {
            // load transient data:
            dto.news().addAll(Persistence.service().query(EntityQueryCriteria.create(News.class)));
            dto.testimonials().addAll(Persistence.service().query(EntityQueryCriteria.create(Testimonial.class)));

            {
                EntityQueryCriteria<AvailableLocale> criteria = EntityQueryCriteria.create(AvailableLocale.class);
                criteria.asc(criteria.proto().displayOrder().getPath().toString());
                dto.locales().addAll(Persistence.service().query(criteria));
            }

        }
    }
}
