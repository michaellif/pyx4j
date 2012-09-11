/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.gadgets;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface LeaseExpirationGadgetDataDTO extends IEntity {

    @Caption(name = "% Unit Occupancy")
    IPrimitive<Double> unitOccupancyPct();

    @Caption(name = "# Unit Occupancy")
    IPrimitive<Integer> unitsOccupied();

    @Caption(name = "# Leases Ending This Month")
    IPrimitive<Integer> numOfLeasesEndingThisMonth();

    @Caption(name = "# Leases Ending Next Month")
    IPrimitive<Integer> numOfLeasesEndingNextMonth();

    @Caption(name = "# Leases Ending 90+ Days")
    IPrimitive<Integer> numOfLeasesEndingOver90Days();

    @Caption(name = "# Leases on Month to Month")
    IPrimitive<Integer> numOfLeasesOnMonthToMonth();

}
