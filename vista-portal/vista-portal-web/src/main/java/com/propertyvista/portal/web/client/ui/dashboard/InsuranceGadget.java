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

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityContainer;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class InsuranceGadget extends AbstractGadget<MainDashboardViewImpl> {

    static final I18n i18n = I18n.get(InsuranceGadget.class);

    private final InsuranceStatusViewer insuranceViewer;

    InsuranceGadget(MainDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Tenant Insurance"), ThemeColor.contrast3);
        setActionsToolbar(new InsuranceToolbar());

        insuranceViewer = new InsuranceStatusViewer();
        insuranceViewer.setViewable(true);
        insuranceViewer.initContent();

        setContent(insuranceViewer);

    }

    protected void populate(InsuranceStatusDTO insuranceStatus) {
        insuranceViewer.populate(insuranceStatus);
    }

    class InsuranceToolbar extends Toolbar {

        public InsuranceToolbar() {

            Button purchaseButton = new Button("Purchase Insurance", new Command() {

                @Override
                public void execute() {
                    getGadgetView().getPresenter().buyTenantSure();
                }
            });
            purchaseButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 1));
            add(purchaseButton);

            Button proofButton = new Button("Provide Proof of my Insurance", new Command() {

                @Override
                public void execute() {
                    getGadgetView().getPresenter().addThirdPartyTenantInsuranceCertificate();
                }
            });
            proofButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 0.6));
            add(proofButton);

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
            CEntityForm<? extends InsuranceStatusDTO> form = null;
            if (status.sertificates().size() == 0) {
                form = new NoInsuranceStatusForm();
                form.initContent();
                ((NoInsuranceStatusForm) form).populate(status.<InsuranceStatusDTO> cast());
            } else {
                form = new HasInsuranceStatusForm();
                form.initContent();
                ((HasInsuranceStatusForm) form).populate(status.sertificates().get(0).<InsuranceCertificateSummaryDTO> cast());
            }

            if (form != null) {
                form.setViewable(true);
                container.setWidget(form);
            }
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

    class NoInsuranceStatusForm extends CEntityForm<InsuranceStatusDTO> {

        public NoInsuranceStatusForm() {
            super(InsuranceStatusDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();

            int row = -1;

            CLabel<String> noInsuranceStatusMessageLabel = new CLabel<String>();
            noInsuranceStatusMessageLabel.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            main.setWidget(++row, 0, new Label(InsuranceStatusDTO.noInsuranceStatusMessage));
            main.setWidget(++row, 0, new Label(InsuranceStatusDTO.tenantSureInvitation));
            return main;

        }

    }

    class HasInsuranceStatusForm extends CEntityForm<InsuranceCertificateSummaryDTO> {

        public HasInsuranceStatusForm() {
            super(InsuranceCertificateSummaryDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();

            int row = -1;

            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().insuranceProvider()), "160px", "120px", "120px").build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().liabilityCoverage()), "160px", "120px", "120px").build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().expiryDate()), "160px", "120px", "120px").build());
            return main;

        }

    }

}
