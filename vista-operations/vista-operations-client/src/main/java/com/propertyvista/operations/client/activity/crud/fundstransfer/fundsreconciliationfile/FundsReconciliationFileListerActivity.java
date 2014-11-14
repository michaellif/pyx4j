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
package com.propertyvista.operations.client.activity.crud.fundstransfer.fundsreconciliationfile;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.fundstransfer.fundsreconciliationfile.FundsReconciliationFileListerView;
import com.propertyvista.operations.rpc.dto.FundsReconciliationFileDTO;

public class FundsReconciliationFileListerActivity extends AbstractListerActivity<FundsReconciliationFileDTO> {

    public FundsReconciliationFileListerActivity(AppPlace place) {
        super(FundsReconciliationFileDTO.class, place, OperationsSite.getViewFactory().getView(FundsReconciliationFileListerView.class));
    }
}
