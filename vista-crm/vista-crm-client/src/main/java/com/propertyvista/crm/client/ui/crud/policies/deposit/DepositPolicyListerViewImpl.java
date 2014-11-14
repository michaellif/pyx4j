/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.deposit;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.services.policies.policy.DepositPolicyCrudService;
import com.propertyvista.domain.policy.dto.DepositPolicyDTO;

public class DepositPolicyListerViewImpl extends AbstractListerView<DepositPolicyDTO> implements DepositPolicyListerView {

    public DepositPolicyListerViewImpl() {
        setDataTablePanel(new DepositPolicyLister());
    }

    public static class DepositPolicyLister extends PolicyListerBase<DepositPolicyDTO> {

        public DepositPolicyLister() {
            super(DepositPolicyDTO.class, GWT.<DepositPolicyCrudService> create(DepositPolicyCrudService.class));

            setDataTableModel(new DataTableModel<DepositPolicyDTO>( //
                    new MemberColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(), //
                    new MemberColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build() //
            ));
        }
    }

}
