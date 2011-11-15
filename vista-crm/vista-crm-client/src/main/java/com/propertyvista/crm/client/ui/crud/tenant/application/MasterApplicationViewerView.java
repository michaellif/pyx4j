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
package com.propertyvista.crm.client.ui.crud.tenant.application;

import com.pyx4j.site.client.ui.crud.IViewerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.dto.MasterApplicationDTO;

public interface MasterApplicationViewerView extends IViewerView<MasterApplicationDTO> {

    interface Presenter extends IViewerView.Presenter {

        IListerView.Presenter getScreeningPresenter();

        void approve();

        void decline();

        void moreInfo();
    }

    IListerView<ApplicationDTO> getApplicationsView();
}