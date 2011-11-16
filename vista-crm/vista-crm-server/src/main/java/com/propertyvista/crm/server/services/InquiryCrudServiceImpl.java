/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.InquiryCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.tenant.Inquiry;
import com.propertyvista.dto.InquiryDTO;

public class InquiryCrudServiceImpl extends GenericCrudServiceDtoImpl<Inquiry, InquiryDTO> implements InquiryCrudService {

    public InquiryCrudServiceImpl() {
        super(Inquiry.class, InquiryDTO.class);
    }

    @Override
    public void setSelectedFloorplan(AsyncCallback<Floorplan> callback, Key id) {
        Floorplan item = Persistence.service().retrieve(Floorplan.class, id);
        Persistence.service().retrieve(item.building());
        callback.onSuccess(item);
    }
}
