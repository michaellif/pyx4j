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
package com.propertyvista.portal.client.ui.residents.yardimaintenance;

import com.propertyvista.dto.YardiServiceRequestDTO;
import com.propertyvista.portal.client.ui.residents.EditImpl;

public class EditYardiMaintenanceRequestViewImpl extends EditImpl<YardiServiceRequestDTO> implements EditYardiMaintenanceRequestView {

    public EditYardiMaintenanceRequestViewImpl() {
        super(new YardiMaintenanceRequestForm());
    }

    @Override
    public void populate(YardiServiceRequestDTO value) {
        boolean editable = true;

        getForm().setViewable(!editable);

        getSubmit().setVisible(editable);
        getCancel().setText(editable ? i18n.tr("Cancel") : i18n.tr("Back"));

        super.populate(value);
    }
}
