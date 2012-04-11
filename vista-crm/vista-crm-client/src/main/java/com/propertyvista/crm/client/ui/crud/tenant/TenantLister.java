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
package com.propertyvista.crm.client.ui.crud.tenant;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.dto.TenantDTO;

public class TenantLister extends ListerBase<TenantDTO> {

    public TenantLister() {
        super(TenantDTO.class, false, true);

        setColumnDescriptors(//@formatter:off
                new Builder(proto().customer().tenantId(), false).build(),
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
                
                new Builder(proto().leaseV().holder()).searchable(false).build(),
                new Builder(proto().leaseV().holder().leaseId()).searchableOnly().build()
            ); // @formatter:on
    }

    @Override
    protected EntityListCriteria<TenantDTO> updateCriteria(EntityListCriteria<TenantDTO> criteria) {
//        criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
        return super.updateCriteria(criteria);
    }
}
