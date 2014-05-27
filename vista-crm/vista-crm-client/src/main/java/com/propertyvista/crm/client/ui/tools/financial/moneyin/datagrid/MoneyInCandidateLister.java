/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-05-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin.datagrid;

import java.util.Iterator;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInCandidateDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInLeaseParticipantDTO;

public class MoneyInCandidateLister extends AbstractLister<MoneyInCandidateDTO> {

    private static final I18n i18n = I18n.get(MoneyInCandidateLister.class);

    public MoneyInCandidateLister() {
        super(MoneyInCandidateDTO.class);
        setColumnDescriptors(new MemberColumnDescriptor.Builder(proto().building()).build(), new MemberColumnDescriptor.Builder(proto().unit()).build(),
                new MemberColumnDescriptor.Builder(proto().leaseId()).build(),
                new MemberColumnDescriptor(new Builder(proto().payerCandidates()).searchable(false).sortable(false).title(i18n.tr("Tenants"))) {
                    @Override
                    public String convert(IEntity entity) {
                        Iterator<MoneyInLeaseParticipantDTO> payerTenants = ((MoneyInCandidateDTO) entity).payerCandidates().iterator();
                        StringBuilder payerTenantsStringBuilder = new StringBuilder();
                        while (payerTenants.hasNext()) {
                            payerTenantsStringBuilder.append(payerTenants.next().name().getValue());
                            if (payerTenants.hasNext()) {
                                payerTenantsStringBuilder.append(", ");
                            }
                        }
                        return payerTenantsStringBuilder.toString();
                    }
                }, new MemberColumnDescriptor.Builder(proto().totalOutstanding()).build());
    }

}
