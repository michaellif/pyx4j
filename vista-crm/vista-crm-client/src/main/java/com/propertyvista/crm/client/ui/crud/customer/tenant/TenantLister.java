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
package com.propertyvista.crm.client.ui.crud.customer.tenant;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.dto.TenantDTO;

public class TenantLister extends ListerBase<TenantDTO> {

    protected static final I18n i18n = I18n.get(TenantLister.class);

    public TenantLister() {
        super(TenantDTO.class, false);

        setColumnDescriptors(//@formatter:off
            new Builder(proto().participantId()).build(),
            new Builder(proto().role()).build(),
            
            new Builder(proto().customer().person().name()).searchable(false).build(),
            new Builder(proto().customer().person().name().firstName(), false).build(),
            new Builder(proto().customer().person().name().lastName(), false).build(),
            new Builder(proto().customer().person().sex(), false).build(),
            new Builder(proto().customer().person().birthDate()).build(),
            
            new Builder(proto().customer().person().homePhone()).build(),
            new Builder(proto().customer().person().mobilePhone(), false).build(),
            new Builder(proto().customer().person().workPhone(), false).build(),
            new Builder(proto().customer().person().email()).build(),
            
            new Builder(proto().leaseV().holder()).columnTitle(i18n.tr("Lease")).searchable(false).build(),
            new Builder(proto().leaseV().holder().leaseId()).searchableOnly().build()
        ); // @formatter:on
    }
}
