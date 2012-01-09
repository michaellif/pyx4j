/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.numberofids;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.policy.dto.NumberOfIDsPolicyDTO;

public class NumberOfIDsPolicyListerViewImpl extends CrmListerViewImplBase<NumberOfIDsPolicyDTO> implements NumberOfIDsPolicyListerView {

    public NumberOfIDsPolicyListerViewImpl() {
        super(CrmSiteMap.Settings.Policies.NumberOfIds.class);
        setLister(new NumberOfIdsPolicyLister());
    }

    private static class NumberOfIdsPolicyLister extends PolicyListerBase<NumberOfIDsPolicyDTO> {

        public NumberOfIdsPolicyLister() {
            super(NumberOfIDsPolicyDTO.class, CrmSiteMap.Settings.Policies.NumberOfIds.class);
            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().numberOfIDs()).build()
            );//@formatter:on
        }
    }
}
