/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.pmc.mercantaccount;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.rpc.dto.PmcMerchantAccountDTO;

public class MerchantAccountLister extends AbstractLister<PmcMerchantAccountDTO> {

    public MerchantAccountLister() {
        super(PmcMerchantAccountDTO.class, false, false);
        setAllowZoomIn(true);

        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().pmc()).build(),
                new MemberColumnDescriptor.Builder(proto().pmc().namespace()).visible(false).build(),
                
                new MemberColumnDescriptor.Builder(proto().merchantTerminalId()).build(),
                
                new MemberColumnDescriptor.Builder(proto().merchantAccount().status()).searchable(false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().paymentsStatus()).searchable(false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().bankId()).searchable(false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().branchTransitNumber()).searchable(false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().accountNumber()).searchable(false).sortable(false).build(),
                
                new MemberColumnDescriptor.Builder(proto().merchantAccount().setup().acceptedEcheck()).searchable(false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().setup().acceptedDirectBanking()).searchable(false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().setup().acceptedCreditCard()).searchable(false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().setup().acceptedCreditCardConvenienceFee()).searchable(false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().setup().acceptedInterac()).searchable(false).sortable(false).build()
            );//@formatter:on
    }
}
