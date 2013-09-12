/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.financial.autopayreview;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;

@Transient
public interface PapReviewCaptionDTO extends IEntity {

    IPrimitive<String> building();

    Building building_();

    IPrimitive<String> unit();

    AptUnit unit_();

    IPrimitive<String> tenant();

    Tenant tenant_();

    IPrimitive<String> paymentMethod();

    IPrimitive<String> lease();

    Lease lease_();

    IPrimitive<LogicalDate> expectedMoveOut();

    /** specifies that the lease of PAP that this caption belongs to has more PAPs */
    IPrimitive<Boolean> hasLeaseWithOtherPaps();

}
