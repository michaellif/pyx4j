/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 23, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.communication.mail.template.model;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.maintenance.MaintenanceRequest.DayTime;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface MaintenanceRequestT extends IEntity {
    IPrimitive<String> requestId();

    IPrimitive<String> category();

    IPrimitive<String> description();

    IPrimitive<String> summary();

    IPrimitive<Boolean> permissionToEnter();

    IPrimitive<String> petInstructions();

    IPrimitive<String> unitNo();

    IPrimitive<String> originatorName();

    IPrimitive<String> reporterName();

    IPrimitive<String> reporterPhone();

    IPrimitive<String> reporterEmail();

    IPrimitive<LogicalDate> preferredDate1();

    IPrimitive<DayTime> preferredTime1();

    IPrimitive<LogicalDate> preferredDate2();

    IPrimitive<DayTime> preferredTime2();

    IPrimitive<String> priority();

    IPrimitive<String> status();

}
