/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-16
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.creditchecks;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.dto.CustomerCreditCheckDTO;

public class CustomerCreditCheckLister extends ListerBase<CustomerCreditCheckDTO> {

    private static final I18n i18n = I18n.get(CustomerCreditCheckLister.class);

    public CustomerCreditCheckLister() {
        super(CustomerCreditCheckDTO.class, true);
        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().screening().screene().person().name()).title(i18n.tr("Screene")).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto().screening().screene().person().name().firstName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto().screening().screene().person().name().lastName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto().creditCheckDate()).build(),
                new MemberColumnDescriptor.Builder(proto().createdBy().name()).title(i18n.tr("Created By")).build(),
                new MemberColumnDescriptor.Builder(proto().createdBy().name().firstName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto().createdBy().name().lastName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto().amountChecked()).build(),
                new MemberColumnDescriptor.Builder(proto().riskCode()).build(),
                new MemberColumnDescriptor.Builder(proto().creditCheckResult()).build(),
                new MemberColumnDescriptor.Builder(proto().amountApproved()).build(),
                new MemberColumnDescriptor.Builder(proto().reason()).build()
        );//@formatter:on

    }

}
