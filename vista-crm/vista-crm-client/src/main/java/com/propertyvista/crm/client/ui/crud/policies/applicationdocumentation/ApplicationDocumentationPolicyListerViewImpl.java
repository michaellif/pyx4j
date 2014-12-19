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
 */
package com.propertyvista.crm.client.ui.crud.policies.applicationdocumentation;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.services.policies.policy.ApplicationDocumentationPolicyCrudService;
import com.propertyvista.domain.policy.dto.ApplicationDocumentationPolicyDTO;

public class ApplicationDocumentationPolicyListerViewImpl extends AbstractListerView<ApplicationDocumentationPolicyDTO> implements
        ApplicationDocumentationPolicyListerView {

    public ApplicationDocumentationPolicyListerViewImpl() {
        setDataTablePanel(new ApplicationDocumentationPolicyLister());
    }

    public static class ApplicationDocumentationPolicyLister extends PolicyListerBase<ApplicationDocumentationPolicyDTO> {

        public ApplicationDocumentationPolicyLister() {
            super(ApplicationDocumentationPolicyDTO.class, GWT
                    .<ApplicationDocumentationPolicyCrudService> create(ApplicationDocumentationPolicyCrudService.class));

            setColumnDescriptors(new ColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(), //
                    new ColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build(), //
                    new ColumnDescriptor.Builder(proto().numberOfRequiredIDs()).build(), //
                    new ColumnDescriptor.Builder(proto().allowedIDs()).build(), //
                    new ColumnDescriptor.Builder(proto().mandatoryProofOfIncome()).build() //

            );

            setDataTableModel(new DataTableModel<ApplicationDocumentationPolicyDTO>());
        }
    }
}
