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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;

import com.propertyvista.crm.rpc.services.admin.GlCodeCategoryCrudService;
import com.propertyvista.domain.financial.GlCodeCategory;

public class GlCodeCategoryCrudServiceImpl extends AbstractCrudServiceImpl<GlCodeCategory> implements GlCodeCategoryCrudService {

    public GlCodeCategoryCrudServiceImpl() {
        super(GlCodeCategory.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void retrievedSingle(GlCodeCategory bo, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieveMember(bo.glCodes());
    }

    @Override
    protected void retrievedForList(GlCodeCategory entity) {
        Persistence.service().retrieveMember(entity.glCodes(), AttachLevel.ToStringMembers);
    }

}
