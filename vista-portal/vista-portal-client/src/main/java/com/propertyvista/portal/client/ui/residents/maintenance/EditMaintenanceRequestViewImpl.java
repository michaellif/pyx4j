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
package com.propertyvista.portal.client.ui.residents.maintenance;

import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.client.ui.residents.BasicViewImpl;

public class EditMaintenanceRequestViewImpl extends BasicViewImpl<MaintenanceRequestDTO> implements EditMaintenanceRequestView {

    public EditMaintenanceRequestViewImpl() {
        super(new MaintenanceRequestForm());
    }

    @Override
    public void populate(MaintenanceRequestDTO value) {
        boolean editable = (value.status().getValue() == MaintenanceRequestStatus.Submitted);

        form.setViewable(!editable);

        submitButton.setVisible(editable);
        cancel.setValue(editable ? i18n.tr("Cancel") : i18n.tr("Back"));

        super.populate(value);
    }
}
