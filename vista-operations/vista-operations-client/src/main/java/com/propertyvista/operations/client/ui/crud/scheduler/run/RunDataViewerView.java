/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-22
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.scheduler.run;

import com.pyx4j.site.client.backoffice.ui.prime.form.IViewerView;

import com.propertyvista.operations.domain.scheduler.RunData;

public interface RunDataViewerView extends IViewerView<RunData> {

    interface Presenter extends IViewerView.IViewerPresenter {

        void cancelDataRun();
    }
}
