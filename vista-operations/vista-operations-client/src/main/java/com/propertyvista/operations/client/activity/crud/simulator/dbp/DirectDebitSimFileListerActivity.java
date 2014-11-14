/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.simulator.dbp;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimFileListerView;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimFile;

public class DirectDebitSimFileListerActivity extends AbstractListerActivity<DirectDebitSimFile> implements DirectDebitSimFileListerView.Presenter {

    public DirectDebitSimFileListerActivity(AppPlace place) {
        super(DirectDebitSimFile.class, place, OperationsSite.getViewFactory().getView(DirectDebitSimFileListerView.class));
    }

}
