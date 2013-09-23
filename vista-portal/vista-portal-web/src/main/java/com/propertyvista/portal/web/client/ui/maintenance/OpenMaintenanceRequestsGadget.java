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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.TenantSureCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MainenanceRequestStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceSummaryDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class OpenMaintenanceRequestsGadget extends AbstractGadget<MaintenanceDashboardViewImpl> {

    private static final I18n i18n = I18n.get(OpenMaintenanceRequestsGadget.class);

    private final OpenMaintenanceRequestsViewer openMaintenanceRequestsViewer;

    private final MaintenanceToolbar toolbar;

    OpenMaintenanceRequestsGadget(MaintenanceDashboardViewImpl view) {
        super(view, PortalImages.INSTANCE.maintenanceIcon(), i18n.tr("Open Maintenance Requests"), ThemeColor.contrast5);

        openMaintenanceRequestsViewer = new OpenMaintenanceRequestsViewer();
        openMaintenanceRequestsViewer.setViewable(true);
        openMaintenanceRequestsViewer.initContent();

        setContent(openMaintenanceRequestsViewer);

        setActionsToolbar(toolbar = new MaintenanceToolbar());

    }

    protected void populate(MaintenanceSummaryDTO value) {
        openMaintenanceRequestsViewer.populate(value);
        toolbar.recalculateState(value);
    }

    class MaintenanceToolbar extends Toolbar {

        private final Button createButton;

        public MaintenanceToolbar() {

            createButton = new Button(i18n.tr("New Maintenance Request"), new Command() {

                @Override
                public void execute() {
                }
            });
            createButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast5, 1));
            add(createButton);

            recalculateState(null);
        }

        public void recalculateState(MaintenanceSummaryDTO insuranceStatus) {
        }
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

            main.setWidget(++row, 0, inject(proto().maintenanceRequestStatuses(), new OpenMaintenanceRequestsFolder()));

            main.setWidget(++row, 0, message);

            return main;

        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

        }
    }

    private class OpenMaintenanceRequestsFolder extends VistaBoxFolder<MainenanceRequestStatusDTO> {

        public OpenMaintenanceRequestsFolder() {
            super(MainenanceRequestStatusDTO.class, true);
            setOrderable(false);
            setAddable(false);
            setEditable(false);
        }

        @Override
        public IFolderItemDecorator<MainenanceRequestStatusDTO> createItemDecorator() {
            BoxFolderItemDecorator<MainenanceRequestStatusDTO> decor = (BoxFolderItemDecorator<MainenanceRequestStatusDTO>) super.createItemDecorator();
            return decor;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof InsuranceCertificateSummaryDTO) {
                return new MaintenanceRequestViewer();
            }
            return super.create(member);
        }

        private class MaintenanceRequestViewer extends CEntityDecoratableForm<MainenanceRequestStatusDTO> {

            private Button detailsButton;

            public MaintenanceRequestViewer() {
                super(MainenanceRequestStatusDTO.class);

                setViewable(true);
                inheritViewable(false);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().description(), new CLabel<String>()), 180).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().status(), new CLabel<String>()), 180).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().submissionDate(), new CLabel<String>()), 180).build());

                detailsButton = new Button(i18n.tr("View Details"), new Command() {

                    @Override
                    public void execute() {
                        System.out.println("+++++++++View Details");
                    }
                });
                detailsButton.getElement().getStyle().setMarginTop(30, Unit.PX);

                detailsButton.setVisible(false);
                content.setWidget(++row, 0, detailsButton);

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);
                if (getValue().isInstanceOf(TenantSureCertificateSummaryDTO.class)) {
                    detailsButton.setVisible(true);
                } else {
                    detailsButton.setVisible(false);
                }
            }
        }

    }

}
