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
package com.propertyvista.operations.client.activity.crud.fundstransfer.cardtransactionrecords;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.fundstransfer.cardtransactionrecords.CardTransactionRecordListerView;
import com.propertyvista.operations.domain.eft.cards.CardTransactionRecord;
import com.propertyvista.operations.rpc.services.PmcCardTransactionRecordCrudService;

public class CardTransactionRecordListerActivity extends AbstractListerActivity<CardTransactionRecord> {

    public CardTransactionRecordListerActivity(Place place) {
        super(CardTransactionRecord.class, place, OperationsSite.getViewFactory().getView(CardTransactionRecordListerView.class), GWT
                        .<AbstractCrudService<CardTransactionRecord>> create(PmcCardTransactionRecordCrudService.class));
    }

}
