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

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.pmc.mercantaccount.MerchantAccountListerView;
import com.propertyvista.operations.rpc.dto.PmcMerchantAccountDTO;
import com.propertyvista.operations.rpc.services.PmcMerchantAccountCrudService;

public class MerchantAccountListerActivity extends AbstractListerActivity<PmcMerchantAccountDTO> {

    public MerchantAccountListerActivity(Place place) {
        super(place, OperationsSite.getViewFactory().instantiate(MerchantAccountListerView.class), GWT
                .<PmcMerchantAccountCrudService> create(PmcMerchantAccountCrudService.class), PmcMerchantAccountDTO.class);
    }
}
