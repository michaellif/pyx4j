/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.maintenance;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceRequestStatusDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceSummaryDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class MaintenanceHistoryGadget extends AbstractGadget<MaintenanceDashboardViewImpl> {

    private static final I18n i18n = I18n.get(MaintenanceHistoryGadget.class);

    private final ClosedMaintenanceRequestsViewer closedMaintenanceRequestsViewer;

    MaintenanceHistoryGadget(MaintenanceDashboardViewImpl view) {
        super(view, PortalImages.INSTANCE.maintenanceIcon(), i18n.tr("Maintenance History"), ThemeColor.contrast5, 1);

        closedMaintenanceRequestsViewer = new ClosedMaintenanceRequestsViewer();
        closedMaintenanceRequestsViewer.setViewable(true);
        closedMaintenanceRequestsViewer.initContent();

        setContent(closedMaintenanceRequestsViewer);

    }

    protected void populate(MaintenanceSummaryDTO value) {
        closedMaintenanceRequestsViewer.populate(value);
    }

    class ClosedMaintenanceRequestsViewer extends CEntityForm<MaintenanceSummaryDTO> {

        private final Label message;

        public ClosedMaintenanceRequestsViewer() {
            super(MaintenanceSummaryDTO.class);
            message = new Label();
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();

            int row = -1;

            main.setWidget(++row, 0, inject(proto().closedMaintenanceRequests(), new ClosedMaintenanceRequestsFolder()));

            main.setWidget(++row, 0, message);

            return main;

        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

        }
    }

    private class ClosedMaintenanceRequestsFolder extends PortalBoxFolder<MaintenanceRequestStatusDTO> {

        public ClosedMaintenanceRequestsFolder() {
            super(MaintenanceRequestStatusDTO.class, false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof MaintenanceRequestStatusDTO) {
                return new MaintenanceRequestFolderItem(getGadgetView().getPresenter());
            }
            return super.create(member);
        }

    }

}
