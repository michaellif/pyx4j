/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 12, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface UnitVacancyReportEvent extends IEntity {

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> eventDate();

    IPrimitive<String> propertyCode();

    IPrimitive<String> unit();

    IPrimitive<String> eventType();

    IPrimitive<String> rentReady();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> moveOutDate();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> moveInDate();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> rentFromDate();

}
