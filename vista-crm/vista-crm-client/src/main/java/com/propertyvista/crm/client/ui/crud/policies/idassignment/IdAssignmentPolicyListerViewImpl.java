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
package com.propertyvista.crm.client.ui.crud.policies.idassignment;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.domain.policy.dto.IdAssignmentPolicyDTO;

public class IdAssignmentPolicyListerViewImpl extends CrmListerViewImplBase<IdAssignmentPolicyDTO> implements IdAssignmentPolicyListerView {

    public IdAssignmentPolicyListerViewImpl() {
        setDataTablePanel(new IdAssignmentPolicyLister());
    }

    public static class IdAssignmentPolicyLister extends PolicyListerBase<IdAssignmentPolicyDTO> {

        public IdAssignmentPolicyLister() {
            super(IdAssignmentPolicyDTO.class);
            setAddNewActionEnabled(false);
            setDeleteActionEnabled(false);

            setDataTableModel(new DataTableModel<IdAssignmentPolicyDTO>(// @formatter:off
                    new MemberColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(),
                    new MemberColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build()
            )); // @formatter:on
        }
    }

}
