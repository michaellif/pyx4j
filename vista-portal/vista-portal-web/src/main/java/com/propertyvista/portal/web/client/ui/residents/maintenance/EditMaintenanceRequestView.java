/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.maintenance;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.MaintenanceRequestMetadataDTO;
import com.propertyvista.portal.web.client.ui.residents.Edit;

public interface EditMaintenanceRequestView extends Edit<MaintenanceRequestDTO> {

    interface Presenter extends Edit.Presenter<MaintenanceRequestDTO> {

        void getCategoryMeta(AsyncCallback<MaintenanceRequestMetadataDTO> callback);
    }
}
