/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-31
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.pmc.merchantaccount;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.pmc.mercantaccount.MerchantAccountListerView;
import com.propertyvista.operations.rpc.dto.PmcMerchantAccountDTO;

public class MerchantAccountListerActivity extends AbstractPrimeListerActivity<PmcMerchantAccountDTO> {

    public MerchantAccountListerActivity(AppPlace place) {
        super(PmcMerchantAccountDTO.class, place, OperationsSite.getViewFactory().getView(MerchantAccountListerView.class));
    }
}
