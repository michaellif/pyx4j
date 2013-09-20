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

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.ItemActionsBar.ActionType;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.portal.rpc.portal.web.dto.PaymentMethodInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureAgreementDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.TenantSureCertificateSummaryDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class InsuranceGadget extends AbstractGadget<ServicesDashboardViewImpl> {

    private static final I18n i18n = I18n.get(InsuranceGadget.class);

    private final InsuranceStatusViewer insuranceViewer;

    private final InsuranceToolbar toolbar;

    InsuranceGadget(ServicesDashboardViewImpl view) {
        super(view, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Tenant Insurance"), ThemeColor.contrast3);

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
                message.setHTML("<b>" + InsuranceStatusDTO.noInsuranceStatusMessage + "</b><br/>" + InsuranceStatusDTO.noInsuranceTenantSureInvitation);
                break;
            case hasOtherInsurance:
                message.setHTML(SimpleMessageFormat.format(InsuranceStatusDTO.hasInsuranceStatusMessage, getValue().coverageExpiryDate().getValue()) + "<br/>"
                        + InsuranceStatusDTO.otherInsuranceTenantSureInvitation);
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

        private class InsuranceCertificateViewer extends CEntityDecoratableForm<InsuranceCertificateSummaryDTO> {

            private Button detailsButton;

            public InsuranceCertificateViewer() {
                super(InsuranceCertificateSummaryDTO.class);

                setViewable(true);
                inheritViewable(false);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().insuranceProvider(), new CLabel<String>()), 180).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().insuranceCertificateNumber(), new CLabel<String>()), 180).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().liabilityCoverage(), new CLabel<String>()), 180).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().inceptionDate(), new CLabel<String>()), 180).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().expiryDate(), new CLabel<String>()), 180).build());

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
