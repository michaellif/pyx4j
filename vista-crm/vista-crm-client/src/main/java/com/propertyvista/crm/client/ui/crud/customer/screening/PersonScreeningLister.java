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
package com.propertyvista.crm.client.ui.crud.customer.screening;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.domain.tenant.PersonScreening;

public class PersonScreeningLister extends ListerBase<PersonScreening> {

    public PersonScreeningLister() {
        super(PersonScreening.class, true);
        getDataTablePanel().getDataTable().setHasCheckboxColumn(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().createDate()).build(),
            new MemberColumnDescriptor.Builder(proto().updateDate()).build(),
    
            new MemberColumnDescriptor.Builder(proto().equifaxApproval().percenrtageApproved()).build(),
            new MemberColumnDescriptor.Builder(proto().equifaxApproval().suggestedDecision()).build()
        );//@formatter:on
    }
}
