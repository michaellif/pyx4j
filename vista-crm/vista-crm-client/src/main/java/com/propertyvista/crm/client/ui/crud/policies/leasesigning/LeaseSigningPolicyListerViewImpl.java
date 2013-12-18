/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leasesigning;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.domain.policy.dto.LeaseSigningPolicyDTO;

public class LeaseSigningPolicyListerViewImpl extends CrmListerViewImplBase<LeaseSigningPolicyDTO> implements LeaseSigningPolicyListerView {

    public LeaseSigningPolicyListerViewImpl() {
        setLister(new LeaseSigningPolicyLister());
    }

    public static class LeaseSigningPolicyLister extends PolicyListerBase<LeaseSigningPolicyDTO> {
        public LeaseSigningPolicyLister() {
            super(LeaseSigningPolicyDTO.class);
        }
    }
}
