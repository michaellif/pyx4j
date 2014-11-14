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
package com.propertyvista.crm.client.ui.crud.policies.onlineapplication;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseApplicationPolicyCrudService;
import com.propertyvista.domain.policy.dto.LeaseApplicationPolicyDTO;

public class LeaseApplicationPolicyListerViewImpl extends AbstractListerView<LeaseApplicationPolicyDTO> implements LeaseApplicationPolicyListerView {

    public LeaseApplicationPolicyListerViewImpl() {
        setDataTablePanel(new LeaseApplicationPolicyLister());
    }

    public static class LeaseApplicationPolicyLister extends PolicyListerBase<LeaseApplicationPolicyDTO> {
        public LeaseApplicationPolicyLister() {
            super(LeaseApplicationPolicyDTO.class, GWT.<LeaseApplicationPolicyCrudService> create(LeaseApplicationPolicyCrudService.class));

            setDataTableModel(new DataTableModel<LeaseApplicationPolicyDTO>( //
                    new MemberColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(), //
                    new MemberColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build() //
            ));
        }
    }
}
