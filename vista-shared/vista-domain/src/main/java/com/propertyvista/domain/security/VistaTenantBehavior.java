/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security;

import java.util.EnumSet;
import java.util.List;
import java.util.Vector;

import com.pyx4j.security.shared.Behavior;

public enum VistaTenantBehavior implements Behavior {

    Prospective,

    ProspectivePrimary,

    ProspectiveSecondary,

    Guarantor,

    ProspectiveSubmited,

    Tenant,

    TenantPrimary,

    TenantSecondary,

    // TODO   remove

    @Deprecated
    PROSPECTIVE_TENANT,

    @Deprecated
    TENANT,

    @Deprecated
    GUARANTOR,

    @Deprecated
    PROPERTY_MANAGER(true),

    @Deprecated
    PROPERTY_EMPLOYEE(true),

    @Deprecated
    PRODUCTION_SUPPORT(true),

    @Deprecated
    ADMIN;

    private final boolean crmUser;

    VistaTenantBehavior() {
        crmUser = false;
    }

    VistaTenantBehavior(boolean crmUser) {
        this.crmUser = crmUser;
    }

    public boolean isCrmUser() {
        return crmUser;
    }

    public static List<VistaTenantBehavior> getCrmBehaviors() {
        List<VistaTenantBehavior> c = new Vector<VistaTenantBehavior>();
        for (VistaTenantBehavior b : EnumSet.allOf(VistaTenantBehavior.class)) {
            if (b.isCrmUser()) {
                c.add(b);
            }
        }
        return c;
    }

}
