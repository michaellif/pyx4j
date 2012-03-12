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
package com.propertyvista.crm.client.ui.crud.building.catalog;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.Concession;

public class ConcessionLister extends ListerBase<Concession> {

    public ConcessionLister() {
        this(false);
    }

    public ConcessionLister(boolean readOnly) {
        super(Concession.class, CrmSiteMap.Properties.Concession.class, false, !readOnly);
        getDataTablePanel().setFilteringEnabled(!readOnly);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().version().versionNumber()).build(),
            new MemberColumnDescriptor.Builder(proto().version().type()).build(),
            new MemberColumnDescriptor.Builder(proto().version().term()).build(),
            new MemberColumnDescriptor.Builder(proto().version().value()).build(), new MemberColumnDescriptor.Builder(proto().version().condition()).build(),
            new MemberColumnDescriptor.Builder(proto().version().status()).build(), new MemberColumnDescriptor.Builder(proto().version().effectiveDate()).build(),
            new MemberColumnDescriptor.Builder(proto().version().expirationDate()).build()
        );//@formatter:on
    }
}
