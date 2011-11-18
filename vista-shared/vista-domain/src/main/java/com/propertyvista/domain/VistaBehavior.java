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
package com.propertyvista.domain;

import java.util.EnumSet;
import java.util.List;
import java.util.Vector;

import com.pyx4j.security.shared.Behavior;

public enum VistaBehavior implements Behavior {

    // TODO 

    PROSPECTIVE_TENANT,

    TENANT,

    GUARANTOR,

    PROPERTY_MANAGER(true),

    PROPERTY_EMPLOYEE(true),

    PRODUCTION_SUPPORT(true),

    ADMIN;

    private final boolean crmUser;

    VistaBehavior() {
        crmUser = false;
    }

    VistaBehavior(boolean crmUser) {
        this.crmUser = crmUser;
    }

    public boolean isCrmUser() {
        return crmUser;
    }

    public static List<VistaBehavior> getCrmBehaviors() {
        List<VistaBehavior> c = new Vector<VistaBehavior>();
        for (VistaBehavior b : EnumSet.allOf(VistaBehavior.class)) {
            if (b.isCrmUser()) {
                c.add(b);
            }
        }
        return c;
    }

}
