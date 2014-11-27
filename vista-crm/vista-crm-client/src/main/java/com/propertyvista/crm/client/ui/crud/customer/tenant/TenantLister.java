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

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.customer.common.LeaseParticipantLister;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.dto.TenantDTO;

public class TenantLister extends LeaseParticipantLister<TenantDTO> {

    protected static final I18n i18n = I18n.get(TenantLister.class);

    public TenantLister() {
        super(TenantDTO.class, GWT.<TenantCrudService> create(TenantCrudService.class));

        setDataTableModel(new DataTableModel<TenantDTO>(//@formatter:off
            new Builder(proto().participantId()).build(),
            new Builder(proto().role()).sortable(false).searchable(false).build(),

            new Builder(proto().customer().person().name()).searchable(false).build(),
            new Builder(proto().customer().person().name().firstName(), false).build(),
            new Builder(proto().customer().person().name().lastName(), false).build(),
            new Builder(proto().customer().person().sex(), false).build(),
            new Builder(proto().customer().person().birthDate()).build(),

            new Builder(proto().customer().person().homePhone()).build(),
            new Builder(proto().customer().person().mobilePhone(), false).build(),
            new Builder(proto().customer().person().workPhone(), false).build(),
            new Builder(proto().customer().person().email()).build(),
            new Builder(proto().customer().registeredInPortal()).visible(false).build(),

            new Builder(proto().lease()).searchable(false).build(),
            new Builder(proto().lease().leaseId()).columnTitle(i18n.tr("Lease Id")).searchableOnly().build(),
            new Builder(proto().lease().status()).columnTitle(i18n.tr("Lease Status")).build(),

            new Builder(proto().lease().unit().info().number()).columnTitle(i18n.tr("Unit #")).build(),
            new Builder(proto().lease().unit().building().propertyCode()).visible(false).build(),
            new Builder(proto().lease().unit().building().info().name()).visible(false).columnTitle(i18n.tr("Building Name")).build()
        )); // @formatter:on
    }
}
