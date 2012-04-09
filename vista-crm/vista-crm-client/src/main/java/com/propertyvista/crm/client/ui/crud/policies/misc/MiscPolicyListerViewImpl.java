/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.misc;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.policy.dto.MiscPolicyDTO;

public class MiscPolicyListerViewImpl extends CrmListerViewImplBase<MiscPolicyDTO> implements MiscPolicyListerView {

    public MiscPolicyListerViewImpl() {
        super(CrmSiteMap.Settings.Policies.Misc.class);
        setLister(new MiscPolicyLister());
    }

    private static class MiscPolicyLister extends PolicyListerBase<MiscPolicyDTO> {

        public MiscPolicyLister() {
            super(MiscPolicyDTO.class);
            setColumnDescriptors( // @formatter:off
                    new MemberColumnDescriptor.Builder(proto().occupantsOver18areApplicants()).build(), 
                    new MemberColumnDescriptor.Builder(proto().occupantsPerBedRoom()).build(), 
                    new MemberColumnDescriptor.Builder(proto().maxParkingSpots()).build(),
                    new MemberColumnDescriptor.Builder(proto().maxLockers()).build(),
                    new MemberColumnDescriptor.Builder(proto().maxPets()).build()
            ); // @formatter:on

        }
    }
}
