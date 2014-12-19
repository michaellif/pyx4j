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
 */
package com.propertyvista.crm.client.ui.crud.policies.leasesigning;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseAgreementLegalPolicyCrudService;
import com.propertyvista.domain.policy.dto.LeaseAgreementLegalPolicyDTO;

public class LeaseAgreementLegalPolicyListerViewImpl extends AbstractListerView<LeaseAgreementLegalPolicyDTO> implements LeaseAgreementLegalPolicyListerView {

    public LeaseAgreementLegalPolicyListerViewImpl() {
        setDataTablePanel(new AgreementLegalPolicyLister());
    }

    public static class AgreementLegalPolicyLister extends PolicyListerBase<LeaseAgreementLegalPolicyDTO> {
        public AgreementLegalPolicyLister() {
            super(LeaseAgreementLegalPolicyDTO.class, GWT.<LeaseAgreementLegalPolicyCrudService> create(LeaseAgreementLegalPolicyCrudService.class));

            setColumnDescriptors( //
                    new ColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(), //
                    new ColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build());

            setDataTableModel(new DataTableModel<LeaseAgreementLegalPolicyDTO>());
        }
    }
}
