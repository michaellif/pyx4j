/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.pet;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.services.policies.policy.PetPolicyCrudService;
import com.propertyvista.domain.policy.dto.PetPolicyDTO;

public class PetPolicyListerViewImpl extends AbstractListerView<PetPolicyDTO> implements PetPolicyListerView {

    public PetPolicyListerViewImpl() {
        setDataTablePanel(new PolicyListerBase<PetPolicyDTO>(PetPolicyDTO.class, GWT.<AbstractListCrudService<PetPolicyDTO>> create(PetPolicyCrudService.class)) {
            {

                setColumnDescriptors( //
                        new MemberColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(), //
                        new MemberColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build());

                setDataTableModel(new DataTableModel<PetPolicyDTO>());
            }
        });
    }
}
