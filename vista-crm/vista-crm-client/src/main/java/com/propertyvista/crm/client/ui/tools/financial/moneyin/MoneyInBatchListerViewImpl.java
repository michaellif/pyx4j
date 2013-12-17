/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin;

import java.util.Arrays;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;

public class MoneyInBatchListerViewImpl extends CrmListerViewImplBase<MoneyInBatchDTO> {

    public MoneyInBatchListerViewImpl() {
        setLister(new MoneyInBatchLister());
    }

    private static class MoneyInBatchLister extends AbstractLister<MoneyInBatchDTO> {

        public MoneyInBatchLister() {
            super(MoneyInBatchDTO.class);
            setColumnDescriptors(Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().building()).build(),
                    new MemberColumnDescriptor.Builder(proto().bankAccount()).build(),
                    new MemberColumnDescriptor.Builder(proto().bankDepositDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().depositSlipNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().totalReceivedAmount()).build(),
                    new MemberColumnDescriptor.Builder(proto().numberOfReceipts()).build(),
                    new MemberColumnDescriptor.Builder(proto().postingStatus()).build()
            ));//@formatter:off
        }
        
    }
}
