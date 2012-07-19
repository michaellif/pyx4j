/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;

public class EmployeeLister extends ListerBase<EmployeeDTO> {

    public EmployeeLister() {
        super(EmployeeDTO.class, true);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().title()).build(),
            new MemberColumnDescriptor.Builder(proto().name().firstName()).build(),
            new MemberColumnDescriptor.Builder(proto().name().lastName()).build(),
            new MemberColumnDescriptor.Builder(proto().email(), false).build(),
            new MemberColumnDescriptor.Builder(proto().updated(), false).build(),
            new MemberColumnDescriptor.Builder(proto().credentialUpdated(), false).build()
        );//@formatter:on
    }
}
