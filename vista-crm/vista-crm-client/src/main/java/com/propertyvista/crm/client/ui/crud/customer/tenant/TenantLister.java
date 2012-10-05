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

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.dto.TenantDTO;

public class TenantLister extends ListerBase<TenantDTO> {

    protected static final I18n i18n = I18n.get(TenantLister.class);

    public TenantLister() {
        super(TenantDTO.class, false);

        setColumnDescriptors(//@formatter:off
            new Builder(proto().leaseCustomer().participantId()).build(),
            new Builder(proto().role()).build(),
            
            new Builder(proto().leaseCustomer().customer().person().name()).searchable(false).build(),
            new Builder(proto().leaseCustomer().customer().person().name().firstName(), false).build(),
            new Builder(proto().leaseCustomer().customer().person().name().lastName(), false).build(),
            new Builder(proto().leaseCustomer().customer().person().sex(), false).build(),
            new Builder(proto().leaseCustomer().customer().person().birthDate()).build(),
            
            new Builder(proto().leaseCustomer().customer().person().homePhone()).build(),
            new Builder(proto().leaseCustomer().customer().person().mobilePhone(), false).build(),
            new Builder(proto().leaseCustomer().customer().person().workPhone(), false).build(),
            new Builder(proto().leaseCustomer().customer().person().email()).build(),
            
            new Builder(proto().leaseTermV().holder()).columnTitle(i18n.tr("Lease Term")).searchable(false).build(),
            new Builder(proto().leaseTermV().holder().lease().leaseId()).columnTitle(i18n.tr("Lease Id")).searchableOnly().build()
        ); // @formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().leaseTermV().holder().lease().leaseId().getPath().toString(), false), new Sort(proto().leaseCustomer()
                .participantId().getPath().toString(), false));
    }
}
