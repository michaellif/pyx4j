/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.cardtransactionrecords;

import com.pyx4j.site.client.ui.prime.form.IViewer;

import com.propertyvista.operations.domain.eft.cards.CardTransactionRecord;

public interface CardTransactionRecordViewerView extends IViewer<CardTransactionRecord> {

    interface Presenter extends IViewer.Presenter {
    }
}
