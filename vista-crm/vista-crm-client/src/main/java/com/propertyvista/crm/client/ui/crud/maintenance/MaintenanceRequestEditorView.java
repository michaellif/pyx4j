/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.maintenance;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.ui.prime.form.IEditor;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.MaintenanceRequestMetadataDTO;

public interface MaintenanceRequestEditorView extends IEditor<MaintenanceRequestDTO> {

    interface Presenter extends IEditor.Presenter {

        void getCategoryMeta(AsyncCallback<MaintenanceRequestMetadataDTO> callback, Building building);
    }
}
