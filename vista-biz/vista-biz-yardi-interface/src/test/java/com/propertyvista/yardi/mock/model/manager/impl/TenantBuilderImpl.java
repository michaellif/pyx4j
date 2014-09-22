/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import com.propertyvista.yardi.mock.model.domain.YardiTenant;
import com.propertyvista.yardi.mock.model.domain.YardiTenant.Type;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.LeaseBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.TenantBuilder;

public class TenantBuilderImpl implements TenantBuilder {

    private final YardiTenant tenant;

    private final LeaseBuilder parent;

    TenantBuilderImpl(YardiTenant tenant, LeaseBuilder parent) {
        this.tenant = tenant;
        this.parent = parent;
    }

    @Override
    public TenantBuilder setType(Type type) {
        tenant.type().setValue(type);
        return this;
    }

    @Override
    public TenantBuilder setName(String name) {
        // parse name
        String[] nameParts = name.split(" ", 2);
        tenant.firstName().setValue(nameParts[0]);
        tenant.lastName().setValue(nameParts.length > 1 ? nameParts[1] : "");
        return this;
    }

    @Override
    public TenantBuilder setEmail(String email) {
        tenant.email().setValue(email);
        return this;
    }

    @Override
    public TenantBuilder setResponsibleForLease(boolean responsible) {
        tenant.responsibleForLease().setValue(responsible);
        return this;
    }

    @Override
    public LeaseBuilder done() {
        return parent;
    }

}
