/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.scheduler;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.scheduler.run.RunListerView;
import com.propertyvista.operations.domain.scheduler.Run;

public class RunListerActivity extends AbstractListerActivity<Run> {

    public RunListerActivity(AppPlace place) {
        super(Run.class, place, OperationsSite.getViewFactory().getView(RunListerView.class));
    }
}
