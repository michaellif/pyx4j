/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.autopaychangepolicy;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.services.policies.policy.AutoPayPolicyCrudService;
import com.propertyvista.domain.policy.dto.AutoPayPolicyDTO;

public class AutoPayChangePolicyListerViewImpl extends CrmListerViewImplBase<AutoPayPolicyDTO> {

    public AutoPayChangePolicyListerViewImpl() {
        setDataTablePanel(new AutoPayChangePolicyLister());
    }

    public static class AutoPayChangePolicyLister extends PolicyListerBase<AutoPayPolicyDTO> {

        public AutoPayChangePolicyLister() {
            super(AutoPayPolicyDTO.class, GWT.<AutoPayPolicyCrudService> create(AutoPayPolicyCrudService.class));

            setDataTableModel(new DataTableModel<AutoPayPolicyDTO>( //
                    new MemberColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(), //
                    new MemberColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build() //
            ));

        }
    }
}
