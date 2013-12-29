/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.occupancy.opconstraints;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.lease.Lease;

@Transient
public interface CancelMoveOutConstraintsDTO extends IEntity {

    /**
     * The reasons have priorities, i.e. in situation when both {@linkplain ConstraintsReason#LeasedOrReserved} and
     * {@linkplain ConstraintsReason#RenovatedOrOffMarket} happen, only {@linkplain ConstraintsReason#LeasedOrReserved} will be returned.
     */
    enum ConstraintsReason {

        /** A unit is either OCCUPIED or RESERVED in the future. */
        LeasedOrReserved,

        /** A unit is either RENOVATED or OFF-MARKET in the future. */
        RenovatedOrOffMarket,

        /** There's nothing to cancel: i.e., there's no information when move-out is going to happen, and therefore there's nothing to cancel. */
        MoveOutNotExpected
    }

    IPrimitive<Boolean> canCancelMoveOut();

    /** If {@linkplain #canCancelMoveOut()} is <code>false</code>. */
    IPrimitive<ConstraintsReason> reason();

    /** If {@linkplain #reason()} is {@linkplain #ConstraintsReason.LeasedOrReserved} will contain a lease stub of a blocking lease. */
    Lease leaseStub();
}
