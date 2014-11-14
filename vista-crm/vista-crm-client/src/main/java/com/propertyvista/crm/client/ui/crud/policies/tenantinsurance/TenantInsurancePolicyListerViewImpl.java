/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-13
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.tenantinsurance;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.services.policies.policy.TenantInsurancePolicyCrudService;
import com.propertyvista.domain.policy.dto.TenantInsurancePolicyDTO;

public class TenantInsurancePolicyListerViewImpl extends AbstractListerView<TenantInsurancePolicyDTO> implements TenantInsurancePolicyListerView {

    public TenantInsurancePolicyListerViewImpl() {
        setDataTablePanel(new TenantInsurancePolicyLister());
    }

    public static class TenantInsurancePolicyLister extends PolicyListerBase<TenantInsurancePolicyDTO> {

        public TenantInsurancePolicyLister() {
            super(TenantInsurancePolicyDTO.class, GWT.<TenantInsurancePolicyCrudService> create(TenantInsurancePolicyCrudService.class));

            setDataTableModel(new DataTableModel<TenantInsurancePolicyDTO>(new MemberColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(), //
                    new MemberColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build() //
            ));
        }

    }

}
