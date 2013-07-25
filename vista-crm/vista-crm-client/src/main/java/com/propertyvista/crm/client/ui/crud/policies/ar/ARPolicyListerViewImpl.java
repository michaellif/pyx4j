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
package com.propertyvista.crm.client.ui.crud.policies.ar;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.domain.policy.dto.ARPolicyDTO;

public class ARPolicyListerViewImpl extends CrmListerViewImplBase<ARPolicyDTO> implements ARPolicyListerView {

    public ARPolicyListerViewImpl() {
        setLister(new ARPolicyLister());
    }

    public static class ARPolicyLister extends PolicyListerBase<ARPolicyDTO> {

        public ARPolicyLister() {
            super(ARPolicyDTO.class);
            setColumnDescriptors(// @formatter:off
                    new MemberColumnDescriptor.Builder(proto().creditDebitRule()).build()

            ); // @formatter:on

        }
    }
}
