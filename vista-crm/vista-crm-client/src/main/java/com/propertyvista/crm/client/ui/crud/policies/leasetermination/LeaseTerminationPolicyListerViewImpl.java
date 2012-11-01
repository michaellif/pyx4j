/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-31
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leasetermination;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.policy.dto.LeaseTerminationPolicyDTO;

public class LeaseTerminationPolicyListerViewImpl extends CrmListerViewImplBase<LeaseTerminationPolicyDTO> {

    public LeaseTerminationPolicyListerViewImpl() {
        super(CrmSiteMap.Settings.Policies.LeaseTermination.class);
        setLister(new LeaseTerminationPolicyLister());
    }

    private static class LeaseTerminationPolicyLister extends PolicyListerBase<LeaseTerminationPolicyDTO> {

        public LeaseTerminationPolicyLister() {
            super(LeaseTerminationPolicyDTO.class);
        }

    }

}
