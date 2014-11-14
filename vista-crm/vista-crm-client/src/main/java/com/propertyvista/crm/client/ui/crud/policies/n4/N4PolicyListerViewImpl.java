/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.n4;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.services.policies.policy.N4PolicyCrudService;
import com.propertyvista.domain.policy.dto.N4PolicyDTO;

public class N4PolicyListerViewImpl extends CrmListerViewImplBase<N4PolicyDTO> implements N4PolicyListerView {

    public N4PolicyListerViewImpl() {
        setDataTablePanel(new N4PolicyLister());
    }

    public static class N4PolicyLister extends PolicyListerBase<N4PolicyDTO> {

        public N4PolicyLister() {
            super(N4PolicyDTO.class, GWT.<N4PolicyCrudService> create(N4PolicyCrudService.class));

            setDataTableModel(new DataTableModel<N4PolicyDTO>(// @formatter:off
                    new MemberColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(),
                    new MemberColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build()
            )); // @formatter:on
        }
    }
}
