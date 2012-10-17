/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.gadgets;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface CollectionsGadgetDataDTO extends IEntity {

    IPrimitive<Integer> leasesPaidThisMonth();

    IPrimitive<BigDecimal> fundsCollectedThisMonth();

    IPrimitive<BigDecimal> fundsInProcessing();

    /** for UI only */
    @Caption(name = "Funds Collected This Month")
    IPrimitive<String> fundsCollectedThisMonthLabel();

    /** for UI only */
    @Caption(name = "Funds In Processing")
    IPrimitive<String> fundsInProcessingLabel();

}
