/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import com.pyx4j.site.client.ui.crud.IViewerView;

import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.dto.LeaseDTO;

public interface LeaseViewerView extends IViewerView<LeaseDTO>, LeaseView {

    interface Presenter extends IViewerView.Presenter, LeaseView.Presenter {

        void convertToApplication();
    }

    public void onApplicationConvertionSuccess(Application result);

    // may return TRUE in case of processed event and no need to re-throw the exception further.
    // FALSE - re-throws the exception (new UnrecoverableClientError(caught)).
    boolean onConvertionFail(Throwable caught);
}
