/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.yardiinterface;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.domain.policy.dto.YardiInterfacePolicyDTO;

public class YardiInterfacePolicyListerViewImpl extends CrmListerViewImplBase<YardiInterfacePolicyDTO> implements YardiInterfacePolicyListerView {

    public YardiInterfacePolicyListerViewImpl() {
        setLister(new YardiInterfacePolicyLister());

    }

    public static class YardiInterfacePolicyLister extends PolicyListerBase<YardiInterfacePolicyDTO> {

        public YardiInterfacePolicyLister() {
            super(YardiInterfacePolicyDTO.class);

            setDataTableModel(new DataTableModel<YardiInterfacePolicyDTO>(// @formatter:off
                    new MemberColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(),
                    new MemberColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build()
            )); // @formatter:on
        }

    }

}
