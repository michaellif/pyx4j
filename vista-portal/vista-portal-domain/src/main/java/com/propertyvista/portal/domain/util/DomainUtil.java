/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 20, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.domain.util;

import java.math.BigDecimal;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.portal.domain.ptapp.TenantCharge;

public class DomainUtil {

    public static TenantCharge createTenantCharge(LeaseTermParticipant.Role role, BigDecimal money) {
        TenantCharge tc = EntityFactory.create(TenantCharge.class);

        tc.tenant().role().setValue(role);
        tc.amount().setValue(money);

        return tc;
    }
}
