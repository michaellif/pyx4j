/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
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
package com.propertyvista.crm.client.ui.crud.policies.paymenttypeselection;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.domain.policy.dto.PaymentTypeSelectionPolicyDTO;

public class PaymentTypeSelectionPolicyListerViewImpl extends CrmListerViewImplBase<PaymentTypeSelectionPolicyDTO> implements
        PaymentTypeSelectionPolicyListerView {

    public PaymentTypeSelectionPolicyListerViewImpl() {
        setLister(new PaymentTypeSelectionPolicyLister());
    }

    public static class PaymentTypeSelectionPolicyLister extends PolicyListerBase<PaymentTypeSelectionPolicyDTO> {

        public PaymentTypeSelectionPolicyLister() {
            super(PaymentTypeSelectionPolicyDTO.class);
            setColumnDescriptors(// @formatter:off
            ); // @formatter:on
        }
    }
}
