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
package com.propertyvista.crm.server.services.organization;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.organization.VendorCrudService;
import com.propertyvista.domain.property.vendor.Vendor;

public class VendorCrudServiceImpl extends AbstractCrudServiceImpl<Vendor> implements VendorCrudService {

    public VendorCrudServiceImpl() {
        super(Vendor.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void persist(Vendor bo, Vendor vendor) {
        Persistence.service().merge(bo.phones());
        Persistence.service().merge(bo.emails());
        Persistence.service().persist(bo);
    }
}
