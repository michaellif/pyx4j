/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 23, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import com.propertyvista.yardi.mock.model.domain.YardiTenant;
import com.propertyvista.yardi.mock.model.manager.YardiGuestManager.ApplicationBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiGuestManager.GuestBuilder;

public class GuestBuilderImpl extends TenantBuilderImpl implements GuestBuilder {

    private final ApplicationBuilder parent;

    GuestBuilderImpl(YardiTenant tenant, ApplicationBuilder parent) {
        super(tenant, parent);
        this.parent = parent;
    }

    @Override
    public ApplicationBuilder done() {
        return parent;
    }
}
