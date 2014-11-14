/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 5, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.portal.resident;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.services.policies.policy.ResidentPortalPolicyCrudService;
import com.propertyvista.domain.policy.dto.ResidentPortalPolicyDTO;

public class ResidentPortalPolicyListerViewImpl extends CrmListerViewImplBase<ResidentPortalPolicyDTO> implements ResidentPortalPolicyListerView {

    public ResidentPortalPolicyListerViewImpl() {
        setDataTablePanel(new ResidentPortalPolicyLister());
    }

    public static class ResidentPortalPolicyLister extends PolicyListerBase<ResidentPortalPolicyDTO> {

        public ResidentPortalPolicyLister() {
            super(ResidentPortalPolicyDTO.class, GWT.<ResidentPortalPolicyCrudService> create(ResidentPortalPolicyCrudService.class));

            setDataTableModel(new DataTableModel<ResidentPortalPolicyDTO>( //
                    new MemberColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(), //
                    new MemberColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build() //
            ));
        }
    }

}
