/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.BasicLister;

import com.propertyvista.dto.TenantDTO;

public class TenantsDetailsLister extends BasicLister<TenantDTO> {

    private static final I18n i18n = I18n.get(TenantsDetailsLister.class);

    public TenantsDetailsLister() {
        super(TenantDTO.class, true, false);
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
                
                new Builder(proto().leaseTermV().holder()).columnTitle(i18n.tr("Lease Term")).searchable(false).build(),
                new Builder(proto().leaseTermV().holder().lease().leaseId()).columnTitle(i18n.tr("Lease Id")).searchableOnly().build()
            ); // @formatter:on
    }

    @Override
    protected void onItemSelect(TenantDTO item) {
        AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(item.getInstanceValueClass()).formViewerPlace(item.getPrimaryKey()));
    }

}
