/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 13, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.preload;

import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;

public class PreloadConfig {

    public Integer defaultBillingCycleSartDay = 1;

    public LeaseBillingPolicy.BillConfirmationMethod billConfirmationMethod = LeaseBillingPolicy.BillConfirmationMethod.manual;

    public boolean existingLease = false;

}
