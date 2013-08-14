/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.admin;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.crm.rpc.VistaCrmBehaviorDTOCoverter;
import com.propertyvista.crm.rpc.services.admin.CrmRoleCrudService;
import com.propertyvista.domain.security.CrmRole;

public class CrmRoleCrudServiceImpl extends AbstractCrudServiceImpl<CrmRole> implements CrmRoleCrudService {

    public CrmRoleCrudServiceImpl() {
        super(CrmRole.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void persist(CrmRole entity, CrmRole dto) {
        entity.behaviors().clear();
        VistaCrmBehaviorDTOCoverter.toDBO(entity.permissions(), entity.behaviors());
        super.persist(entity, null);
    }

    @Override
    protected void enhanceRetrieved(CrmRole entity, CrmRole dto, RetrieveTarget retrieveTarget) {
        dto.permissions().clear();
        dto.permissions().addAll(VistaCrmBehaviorDTOCoverter.toDTO(entity.behaviors()));
    }

}
