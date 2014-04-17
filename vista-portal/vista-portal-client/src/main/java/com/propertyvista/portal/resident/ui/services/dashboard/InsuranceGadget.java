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
package com.propertyvista.portal.resident.ui.services.dashboard;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.status.GeneralInsuranceCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.status.InsuranceCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.status.TenantSureCertificateSummaryDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class InsuranceGadget extends AbstractGadget<ServicesDashboardViewImpl> {

    private static final I18n i18n = I18n.get(InsuranceGadget.class);

    private final InsuranceStatusViewer insuranceViewer;

    private final InsuranceToolbar toolbar;

    InsuranceGadget(ServicesDashboardViewImpl view) {
        super(view, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Tenant Insurance"), ThemeColor.contrast3, 1);

        insuranceViewer = new InsuranceStatusViewer();
        insuranceViewer.setViewable(true);
        insuranceViewer.init();

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

    }

    protected void populate(InsuranceStatusDTO value) {
        insuranceViewer.populate(value);
        toolbar.recalculateState(value);
    }

    class InsuranceStatusViewer extends CEntityForm<InsuranceStatusDTO> {

        private InsuranceCertificatesFolder folder;

        public InsuranceStatusViewer() {
            super(InsuranceStatusDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();

            int row = -1;

            main.setH4(++row, 0, 1, i18n.tr("Certificates"));

            main.setWidget(++row, 0, inject(proto().certificates(), folder = new InsuranceCertificatesFolder()));

            return main;

        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            switch (getValue().status().getValue()) {
            case noInsurance:
                folder.setNoDataNotificationWidget(new HTML("<b>" + InsuranceGadgetMessages.noInsuranceStatusMessage + "</b><br/>"
                        + InsuranceGadgetMessages.noInsuranceTenantSureInvitation));
                break;
            case hasOtherInsurance:
                folder.setNoDataNotificationWidget(new HTML(SimpleMessageFormat.format(InsuranceGadgetMessages.hasInsuranceStatusMessage, getValue()
                        .coverageExpiryDate().getValue())
                        + "<br/>" + InsuranceGadgetMessages.otherInsuranceTenantSureInvitation));
                break;
            case hasTenantSure:
                break;
            }

        }
    }

    private class InsuranceCertificatesFolder extends PortalBoxFolder<InsuranceCertificateSummaryDTO> {

        public InsuranceCertificatesFolder() {
            super(InsuranceCertificateSummaryDTO.class, false);
            setExpended(true);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof InsuranceCertificateSummaryDTO) {
                return new InsuranceCertificateViewer();
            }
            return super.create(member);
        }

        private class InsuranceCertificateViewer extends CEntityForm<InsuranceCertificateSummaryDTO> {

            private Anchor detailsAnchor;

            public InsuranceCertificateViewer() {
                super(InsuranceCertificateSummaryDTO.class);
            }

            @Override
            protected IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, inject(proto().insuranceProvider(), new CLabel<String>(), new FieldDecoratorBuilder(180).build()));
                content.setWidget(++row, 0, inject(proto().insuranceCertificateNumber(), new CLabel<String>(), new FieldDecoratorBuilder(180).build()));
                content.setWidget(++row, 0, inject(proto().liabilityCoverage(), new FieldDecoratorBuilder(180).build()));
                content.setWidget(++row, 0, inject(proto().inceptionDate(), new FieldDecoratorBuilder(180).build()));
                content.setWidget(++row, 0, inject(proto().expiryDate(), new FieldDecoratorBuilder(180).build()));

                detailsAnchor = new Anchor(i18n.tr("View Details"), new Command() {

                    @Override
                    public void execute() {
                        if (getValue() instanceof GeneralInsuranceCertificateSummaryDTO) {
                            AppSite.getPlaceController().goTo(
                                    new ResidentPortalSiteMap.ResidentServices.TenantInsurance.GeneralPolicyPage().formPlace(getValue().insurancePolicy()
                                            .getPrimaryKey()));
                        } else if (getValue() instanceof TenantSureCertificateSummaryDTO) {
                            AppSite.getPlaceController().goTo(
                                    new ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSurePage().formPlace(getValue()
                                            .insurancePolicy().getPrimaryKey()));

                        }
                    }
                });
                detailsAnchor.getElement().getStyle().setMarginTop(30, Unit.PX);

                content.setWidget(++row, 0, detailsAnchor);

                return content;
            }
        }

    }

}
