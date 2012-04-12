/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.guarantor;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.dto.GuarantorDTO;

public class GuarantorLister extends ListerBase<GuarantorDTO> {

    public GuarantorLister() {
        super(GuarantorDTO.class, false, false);

        // TODO: currently we use just person tenant, so we'll display more data for them:
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().displayName()));
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().type()));

        setColumnDescriptors(//@formatter:off
            new Builder(proto().id()).build(),
            new Builder(proto().customer().person().name()).searchable(false).build(),
            new Builder(proto().customer().person().name().firstName(), false).build(),
            new Builder(proto().customer().person().name().lastName(), false).build(),
            new Builder(proto().customer().person().sex()).build(),
            new Builder(proto().customer().person().birthDate()).build(),
            new Builder(proto().customer().person().homePhone()).build(),
            new Builder(proto().customer().person().mobilePhone()).build(),
            new Builder(proto().customer().person().workPhone()).build(),
            new Builder(proto().customer().person().email()).title("E-mail address").build()
        );//@formatter:on
    }
}
