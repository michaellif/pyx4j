/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance.tenantsure.rules;

import com.propertyvista.domain.tenant.insurance.TenantSurePaymentSchedule;
import com.propertyvista.misc.VistaTODO;

public class TenantSurePaymentScheduleFactory {

    public static ITenantSurePaymentSchedule create(TenantSurePaymentSchedule paymentSchedule) {
        if (paymentSchedule == null) {
            throw new IllegalArgumentException("paymentSchedule must not be null");
        }
        ITenantSurePaymentSchedule ps = null;
        switch (paymentSchedule) {
        case Monthly:
            ps = new TenantSureMonthlyPaymentSchedule();
            break;
        case Annual:
            if (!VistaTODO.VISTA_3207_TENANT_SURE_YEARLY_PAY_SCHEDULE_IMPLEMENTED) {
                throw new IllegalStateException("Yearly payment schedule has not been implemented yet");
            }
            ps = new TenantSureAnnualPaymentSchedule();
            break;
        default:
            throw new Error("Implementation for " + paymentSchedule + " was not found");
        }
        return ps;
    }
}
