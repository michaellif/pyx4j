/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.components.boxes.UpdateUploadDialog;
import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.dto.BuildingDTO;

public class BuildingListerViewImpl extends CrmListerViewImplBase<BuildingDTO> implements BuildingListerView {

    private static final I18n i18n = I18n.get(BuildingListerViewImpl.class);

    private Button upload;

    public BuildingListerViewImpl() {
        setDataTablePanel(new BuildingLister());

        // TODO implement Upload
        // This used to be shown when Leases was disabled.
        if (false && SecurityController.check(VistaBasicBehavior.PropertyVistaSupport)) {
            upload = new Button(i18n.tr("Upload Update"), new Command() {

                @Override
                public void execute() {
                    new UpdateUploadDialog().show();
                }
            });
            addHeaderToolbarItem(upload);
        }

    }
}
