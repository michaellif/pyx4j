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
package com.propertyvista.portal.web.client.ui.maintenance;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceRequestStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceSummaryDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;

public class OpenMaintenanceRequestsGadget extends AbstractGadget<MaintenanceDashboardViewImpl> {

    private static final I18n i18n = I18n.get(OpenMaintenanceRequestsGadget.class);

    private final OpenMaintenanceRequestsViewer openMaintenanceRequestsViewer;

    private final MaintenanceToolbar toolbar;

    OpenMaintenanceRequestsGadget(MaintenanceDashboardViewImpl view) {
        super(view, PortalImages.INSTANCE.maintenanceIcon(), i18n.tr("Open Maintenance Requests"), ThemeColor.contrast5, 1);

        openMaintenanceRequestsViewer = new OpenMaintenanceRequestsViewer();
        openMaintenanceRequestsViewer.setViewable(true);
        openMaintenanceRequestsViewer.initContent();

        setContent(openMaintenanceRequestsViewer);

        setActionsToolbar(toolbar = new MaintenanceToolbar() {

            @Override
            protected void onNewRequestClicked() {
                getGadgetView().getPresenter().createMaintenanceRequest();
            }

        });

    }

    protected void populate(MaintenanceSummaryDTO value) {
        openMaintenanceRequestsViewer.populate(value);
        toolbar.recalculateState(value);
    }

    class OpenMaintenanceRequestsViewer extends CEntityForm<MaintenanceSummaryDTO> {

        private final Label message;

        public OpenMaintenanceRequestsViewer() {
            super(MaintenanceSummaryDTO.class);
            message = new Label();
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();

            int row = -1;

            main.setWidget(++row, 0, inject(proto().openMaintenanceRequests(), new OpenMaintenanceRequestsFolder()));

            main.setWidget(++row, 0, message);

            return main;

        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

        }
    }

    private class OpenMaintenanceRequestsFolder extends VistaBoxFolder<MaintenanceRequestStatusDTO> {

        public OpenMaintenanceRequestsFolder() {
            super(MaintenanceRequestStatusDTO.class, true);
            setOrderable(false);
            setAddable(false);
            setEditable(false);
        }

        @Override
        public IFolderItemDecorator<MaintenanceRequestStatusDTO> createItemDecorator() {
            BoxFolderItemDecorator<MaintenanceRequestStatusDTO> decor = (BoxFolderItemDecorator<MaintenanceRequestStatusDTO>) super.createItemDecorator();
            decor.setExpended(false);
            return decor;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof MaintenanceRequestStatusDTO) {
                return new MaintenanceRequestFolderItem();
            }
            return super.create(member);
        }

    }

}
