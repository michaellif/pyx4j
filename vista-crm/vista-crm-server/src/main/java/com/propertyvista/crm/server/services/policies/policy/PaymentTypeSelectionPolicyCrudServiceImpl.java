/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-12
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policies.policy;

import com.propertyvista.crm.rpc.services.policies.policy.PaymentTypeSelectionPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.PaymentTypeSelectionPolicyDTO;
import com.propertyvista.domain.policy.policies.PaymentTypeSelectionPolicy;

public class PaymentTypeSelectionPolicyCrudServiceImpl extends GenericPolicyCrudService<PaymentTypeSelectionPolicy, PaymentTypeSelectionPolicyDTO> implements
        PaymentTypeSelectionPolicyCrudService {

    public PaymentTypeSelectionPolicyCrudServiceImpl() {
        super(PaymentTypeSelectionPolicy.class, PaymentTypeSelectionPolicyDTO.class);
    }
}
