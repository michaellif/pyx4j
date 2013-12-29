/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.gadgets;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface NoticesGadgetDataDTO extends IEntity {

    IPrimitive<Integer> vacantUnits();

    IPrimitive<Integer> totalUnits();

    @Caption(name = "Notices Leaving This Month")
    IPrimitive<Integer> noticesLeavingThisMonth();

    @Caption(name = "Notices Leaving Next Month")
    IPrimitive<Integer> noticesLeavingNextMonth();

    @Caption(name = "Notices Leaving 60 to 90 Days")
    IPrimitive<Integer> noticesLeaving60to90Days();

    @Caption(name = "Notices Leaving 90+ Days")
    IPrimitive<Integer> noticesLeavingOver90Days();

}
