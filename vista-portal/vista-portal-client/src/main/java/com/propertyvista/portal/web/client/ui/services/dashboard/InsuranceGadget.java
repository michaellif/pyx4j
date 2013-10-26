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
package com.propertyvista.portal.web.client.ui.services.dashboard;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.GeneralInsuranceCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.TenantSureCertificateSummaryDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.util.decorators.FormWidgetDecoratorBuilder;

public class InsuranceGadget extends AbstractGadget<ServicesDashboardViewImpl> {

    private static final I18n i18n = I18n.get(InsuranceGadget.class);

    private final InsuranceStatusViewer insuranceViewer;

    private final InsuranceToolbar toolbar;

    InsuranceGadget(ServicesDashboardViewImpl view) {
        super(view, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Tenant Insurance"), ThemeColor.contrast3, 1);

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

    }

    protected void populate(InsuranceStatusDTO value) {
        insuranceViewer.populate(value);
        toolbar.recalculateState(value);
    }

    class InsuranceStatusViewer extends CEntityForm<InsuranceStatusDTO> {

        private final Label message;

        public InsuranceStatusViewer() {
            super(InsuranceStatusDTO.class);
            message = new Label();
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();

            int row = -1;

            main.setH4(++row, 0, 1, i18n.tr("Certificates"));

            main.setWidget(++row, 0, inject(proto().certificates(), new InsuranceCertificatesFolder()));

            main.setWidget(++row, 0, message);

            return main;

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
                break;
            }

        }
    }

    private class InsuranceCertificatesFolder extends VistaBoxFolder<InsuranceCertificateSummaryDTO> {

        public InsuranceCertificatesFolder() {
            super(InsuranceCertificateSummaryDTO.class, true);
            setOrderable(false);
            setAddable(false);
            setEditable(false);
        }

        @Override
        public IFolderItemDecorator<InsuranceCertificateSummaryDTO> createItemDecorator() {
            BoxFolderItemDecorator<InsuranceCertificateSummaryDTO> decor = (BoxFolderItemDecorator<InsuranceCertificateSummaryDTO>) super.createItemDecorator();
            return decor;
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

                setViewable(true);
                inheritViewable(false);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().insuranceProvider(), new CLabel<String>()), 180).build());
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().insuranceCertificateNumber(), new CLabel<String>()), 180).build());
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().liabilityCoverage(), new CLabel<String>()), 180).build());
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().inceptionDate(), new CLabel<String>()), 180).build());
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().expiryDate(), new CLabel<String>()), 180).build());

                detailsAnchor = new Anchor(i18n.tr("View Details"), new Command() {

                    @Override
                    public void execute() {
                        if (getValue() instanceof GeneralInsuranceCertificateSummaryDTO) {
                            AppSite.getPlaceController().goTo(
                                    new PortalSiteMap.Resident.ResidentServices.TenantInsurance.GeneralPolicyPage().formPlace(getValue().insurancePolicy()
                                            .getPrimaryKey()));
                        } else if (getValue() instanceof TenantSureCertificateSummaryDTO) {
                            AppSite.getPlaceController().goTo(
                                    new PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage().formPlace(getValue()
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
