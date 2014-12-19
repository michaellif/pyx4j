/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 18, 2014
 * @author stanp
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.crm.client.ui.crud.policies.eviction;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.services.policies.policy.EvictionFlowPolicyCrudService;
import com.propertyvista.domain.policy.dto.EvictionFlowPolicyDTO;

public class EvictionFlowPolicyListerViewImpl extends AbstractListerView<EvictionFlowPolicyDTO> implements EvictionFlowPolicyListerView {

    public EvictionFlowPolicyListerViewImpl() {
        setDataTablePanel(new EvictionFlowPolicyLister());
    }

    public static class EvictionFlowPolicyLister extends PolicyListerBase<EvictionFlowPolicyDTO> {

        public EvictionFlowPolicyLister() {
            super(EvictionFlowPolicyDTO.class, GWT.<EvictionFlowPolicyCrudService> create(EvictionFlowPolicyCrudService.class));

            setColumnDescriptors( //
                    new ColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(), //
                    new ColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build());

            setDataTableModel(new DataTableModel<EvictionFlowPolicyDTO>());
        }
    }
}
