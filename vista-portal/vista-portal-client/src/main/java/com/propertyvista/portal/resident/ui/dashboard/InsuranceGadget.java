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
package com.propertyvista.portal.resident.ui.dashboard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.portal.resident.ui.services.dashboard.InsuranceGadgetMessages;
import com.propertyvista.portal.resident.ui.services.dashboard.InsuranceToolbar;
import com.propertyvista.portal.rpc.portal.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.AbstractGadget;

public class InsuranceGadget extends AbstractGadget<MainDashboardViewImpl> {

    static final I18n i18n = I18n.get(InsuranceGadget.class);

    private final InsuranceStatusViewer insuranceViewer;

    private final InsuranceToolbar toolbar;

    private final NavigationBar navigationBar;

    InsuranceGadget(MainDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Tenant Insurance"), ThemeColor.contrast3, 1);

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
            viewServicesAnchor = new Anchor(i18n.tr("View details in Resident Services"), new Command() {

                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.ResidentServices());
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

    class InsuranceStatusViewer extends CEntityForm<InsuranceStatusDTO> {

        private Label message;

        public InsuranceStatusViewer() {
            super(InsuranceStatusDTO.class);

        }

        @Override
        public IsWidget createContent() {
            return message = new Label();
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            switch (getValue().status().getValue()) {
            case noInsurance:
                message.setHTML("<b>" + InsuranceGadgetMessages.noInsuranceStatusMessage + "</b><br/>"
                        + InsuranceGadgetMessages.noInsuranceTenantSureInvitation);
                break;
            case hasOtherInsurance:
                message.setHTML(SimpleMessageFormat.format(InsuranceGadgetMessages.hasInsuranceStatusMessage, getValue().coverageExpiryDate().getValue())
                        + "<br/>" + InsuranceGadgetMessages.otherInsuranceTenantSureInvitation);
                break;
            case hasTenantSure:
                message.setText(SimpleMessageFormat.format(InsuranceGadgetMessages.hasInsuranceStatusMessage, getValue().coverageExpiryDate().getValue()));
                break;
            }

        }
    }

}
