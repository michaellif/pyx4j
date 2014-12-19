/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 2, 2011
 * @author vlads
 */
package com.propertyvista.server.domain;

import java.util.Date;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.tenant.Customer;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Deprecated
public interface CampaignHistory extends IEntity {

    @MemberColumn(name = "trg")
    IPrimitive<CampaignTrigger> trigger();

    Customer tenant();

    PhoneCallCampaign campaign();

    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

}
