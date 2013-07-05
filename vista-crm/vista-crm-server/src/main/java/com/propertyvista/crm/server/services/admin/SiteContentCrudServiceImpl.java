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
package com.propertyvista.crm.server.services.admin;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.crm.rpc.services.admin.SiteContentCrudService;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteContentCrudServiceImpl extends AbstractCrudServiceDtoImpl<SiteDescriptor, SiteDescriptorDTO> implements SiteContentCrudService {

    public SiteContentCrudServiceImpl() {
        super(SiteDescriptor.class, SiteDescriptorDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    public void retrieveHomeItem(AsyncCallback<Key> callback) {
        EntityQueryCriteria<SiteDescriptor> criteria = EntityQueryCriteria.create(SiteDescriptor.class);
        List<Key> list = Persistence.service().queryKeys(criteria);
        if (list.isEmpty()) {
            SiteDescriptor site = EntityFactory.create(SiteDescriptor.class);
            Persistence.service().persist(site);
            Persistence.service().commit();
            callback.onSuccess(site.getPrimaryKey());
        } else {
            callback.onSuccess(list.get(0));
        }
    }

    @Override
    protected void enhanceRetrieved(SiteDescriptor in, SiteDescriptorDTO dto, RetrieveTarget RetrieveTarget) {
        // load transient data:
        EntityQueryCriteria<AvailableLocale> criteria = EntityQueryCriteria.create(AvailableLocale.class);
        criteria.asc(criteria.proto().displayOrder().getPath().toString());
        dto.locales().addAll(Persistence.service().query(criteria));
    }

    @Override
    protected void persist(final SiteDescriptor dbo, final SiteDescriptorDTO in) {
        dbo._updateFlag().updated().setValue(new Date());
        super.persist(dbo, in);
    }
}
