/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 3, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.arrears;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface MockupTenantsArrearsDTO extends MockupTenant {
    @Caption(name = "0 - 30 Days")
    IPrimitive<Double> arrear1MonthAgo();

    @Caption(name = "30 - 60 Days")
    IPrimitive<Double> arrears2MonthsAgo();

    @Caption(name = "60 -90 Days")
    IPrimitive<Double> arrears3MonthsAgo();

    @Caption(name = "Over 90 Days")
    IPrimitive<Double> arrears4MonthsAgo();
}
