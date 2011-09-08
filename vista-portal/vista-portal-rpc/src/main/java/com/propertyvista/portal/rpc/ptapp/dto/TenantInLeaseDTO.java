/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.IBoundToApplication;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantIn;
import com.propertyvista.domain.tenant.TenantScreening;

@Transient
public interface TenantInLeaseDTO extends Tenant, TenantIn, TenantScreening, IBoundToApplication {

    IPrimitive<Double> payment();
}
