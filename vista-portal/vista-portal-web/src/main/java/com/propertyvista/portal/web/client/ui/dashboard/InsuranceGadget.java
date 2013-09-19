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
package com.propertyvista.portal.web.client.ui.dashboard;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityContainer;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.services.dashboard.InsuranceToolbar;

public class InsuranceGadget extends AbstractGadget<MainDashboardViewImpl> {

    static final I18n i18n = I18n.get(InsuranceGadget.class);

    private final InsuranceStatusViewer insuranceViewer;

    private final InsuranceToolbar toolbar;

    private final NavigationBar navigationBar;

    InsuranceGadget(MainDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Tenant Insurance"), ThemeColor.contrast3);

        insuranceViewer = new InsuranceStatusViewer();
        insuranceViewer.setViewable(true);
        insuranceViewer.initContent();

        setContent(insuranceViewer);

        setActionsToolbar(toolbar = new InsuranceToolbar() {

            @Override
            protected void onPurchaseClicked() {
                getGadgetView().getPresenter().buyTenantSure();
            }

            @Override
            protected void onProofClicked() {
                getGadgetView().getPresenter().addThirdPartyTenantInsuranceCertificate();
            }

        });
        setNavigationBar(navigationBar = new NavigationBar());

    }

    protected void populate(InsuranceStatusDTO value) {
        insuranceViewer.populate(value);
        toolbar.recalculateState(value);
        navigationBar.recalculateState(value);
    }

    class NavigationBar extends FlowPanel {

        private final Anchor viewServicesAnchor;

        public NavigationBar() {
            viewServicesAnchor = new Anchor(i18n.tr("View my Resident Services"), new Command() {

                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ResidentServices());
                }
            });
            add(viewServicesAnchor);
        }

        public void recalculateState(InsuranceStatusDTO insuranceStatus) {

            if (insuranceStatus == null) {
                viewServicesAnchor.setVisible(false);
            } else {
                switch (insuranceStatus.status().getValue()) {
                case noInsurance:
                    viewServicesAnchor.setVisible(false);
                    break;
                case hasOtherInsurance:
                    viewServicesAnchor.setVisible(true);
                    break;
                case hasTenantSure:
                    viewServicesAnchor.setVisible(true);
                    break;
                }
            }
        }
    }

    class InsuranceStatusViewer extends CEntityContainer<InsuranceStatusDTO> {

        private SimplePanel container;

        public InsuranceStatusViewer() {

        }

        @Override
        public IsWidget createContent() {
            return container = new SimplePanel();
        }

        @Override
        protected void setEditorValue(InsuranceStatusDTO status) {
            CEntityForm<InsuranceStatusDTO> form = new InsuranceStatusForm();
            form.initContent();
            form.populate(status);
            form.setViewable(true);
            container.setWidget(form);
        }

        @Override
        protected void onReset() {
            container.setWidget(null);
            super.onReset();
        }

        @Override
        public Collection<? extends CComponent<?>> getComponents() {
            return Arrays.asList(new CComponent<?>[] {});
        }

        @Override
        protected void setComponentsValue(InsuranceStatusDTO value, boolean fireEvent, boolean populate) {
        }
    }

    class InsuranceStatusForm extends CEntityForm<InsuranceStatusDTO> {

        private final HTML message;

        public InsuranceStatusForm() {
            super(InsuranceStatusDTO.class);
            message = new Label();
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();

            int row = -1;

            main.setWidget(++row, 0, message);
            return main;

        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            if (getValue().certificates().size() == 0) {
                message.setHTML("<b>" + InsuranceStatusDTO.noInsuranceStatusMessage + "</b><br/>" + InsuranceStatusDTO.noInsuranceTenantSureInvitation);
            } else {
                message.setText(SimpleMessageFormat.format(InsuranceStatusDTO.hasInsuranceStatusMessage, getValue().coverageExpiryDate().getValue()));
            }

            switch (getValue().status().getValue()) {
            case noInsurance:
                message.setHTML("<b>" + InsuranceStatusDTO.noInsuranceStatusMessage + "</b><br/>" + InsuranceStatusDTO.noInsuranceTenantSureInvitation);
                break;
            case hasOtherInsurance:
                message.setHTML(SimpleMessageFormat.format(InsuranceStatusDTO.hasInsuranceStatusMessage, getValue().coverageExpiryDate().getValue()) + "<br/>"
                        + InsuranceStatusDTO.otherInsuranceTenantSureInvitation);
                break;
            case hasTenantSure:
                message.setText(SimpleMessageFormat.format(InsuranceStatusDTO.hasInsuranceStatusMessage, getValue().coverageExpiryDate().getValue()));
                break;
            }

        }
    }

}
