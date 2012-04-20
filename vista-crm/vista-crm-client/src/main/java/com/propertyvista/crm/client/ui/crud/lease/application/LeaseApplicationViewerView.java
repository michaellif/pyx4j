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
package com.propertyvista.crm.client.ui.crud.lease.application;

import java.util.List;

import com.pyx4j.site.client.ui.crud.form.IViewerView;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerPresenterBase;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.dto.ApplicationUserDTO;
import com.propertyvista.dto.LeaseApplicationDTO;

public interface LeaseApplicationViewerView extends IViewerView<LeaseApplicationDTO> {

    interface Presenter extends IViewerView.Presenter, LeaseViewerPresenterBase {

        void startOnlineApplication();

        void inviteUsers(List<ApplicationUserDTO> users);

        void applicationAction(LeaseApplicationActionDTO action);
    }
}
