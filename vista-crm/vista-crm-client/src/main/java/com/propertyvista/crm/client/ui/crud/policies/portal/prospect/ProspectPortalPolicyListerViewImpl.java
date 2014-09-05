/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.portal.prospect;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.domain.policy.dto.ProspectPortalPolicyDTO;

public class ProspectPortalPolicyListerViewImpl extends CrmListerViewImplBase<ProspectPortalPolicyDTO> implements ProspectPortalPolicyListerView {

    public ProspectPortalPolicyListerViewImpl() {
        setLister(new ProspectPortalPolicyLister());
    }

    public static class ProspectPortalPolicyLister extends PolicyListerBase<ProspectPortalPolicyDTO> {

        public ProspectPortalPolicyLister() {
            super(ProspectPortalPolicyDTO.class);

            setDataTableModel(new DataTableModel<ProspectPortalPolicyDTO>(// @formatter:off
                    new MemberColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(),
                    new MemberColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build()
            )); // @formatter:on
        }
    }

}
