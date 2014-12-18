/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2012
 * @author ArtyomB
 */
package com.propertyvista.biz.validation.validators.lease;

import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;

public class GuarantorInApprovedLeaseValidator extends LeaseParticipantInApprovedLeaseValidator<LeaseTermGuarantor> {

    public GuarantorInApprovedLeaseValidator() {
        super(LeaseTermGuarantor.class);
    }

}
