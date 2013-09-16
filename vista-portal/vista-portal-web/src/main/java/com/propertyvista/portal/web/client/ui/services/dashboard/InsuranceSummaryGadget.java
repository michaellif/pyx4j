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
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.NoInsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.OtherProviderInsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.TenantSureInsuranceStatusDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class InsuranceSummaryGadget extends AbstractGadget<ServicesDashboardViewImpl> {

    private static final I18n i18n = I18n.get(InsuranceSummaryGadget.class);

    private final InsuranceStatusViewer insuranceViewer;

    InsuranceSummaryGadget(ServicesDashboardViewImpl view) {
        super(view, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Tenant Insurance"), ThemeColor.contrast3);
        setActionsToolbar(new InsuranceToolbar());

        insuranceViewer = new InsuranceStatusViewer();
        insuranceViewer.setViewable(true);
        insuranceViewer.initContent();

        setContent(insuranceViewer);
    }

    protected void populate(InsuranceStatusDTO value) {
        insuranceViewer.populate(value);
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
        protected void setEditorValue(InsuranceStatusDTO value) {
            CEntityForm<? extends InsuranceStatusDTO> form = null;
            if (value.isInstanceOf(NoInsuranceStatusDTO.class)) {
                form = new NoInsuranceStatusForm();
                form.initContent();
                ((NoInsuranceStatusForm) form).populate(value.<NoInsuranceStatusDTO> cast());
            } else if (value.isInstanceOf(OtherProviderInsuranceStatusDTO.class)) {
                form = new OtherProviderInsuranceStatusForm();
                form.initContent();
                ((OtherProviderInsuranceStatusForm) form).populate(value.<OtherProviderInsuranceStatusDTO> cast());
            } else if (value.isInstanceOf(TenantSureInsuranceStatusDTO.class)) {
                form = new TenantSureInsuranceStatusForm();
                form.initContent();
                ((TenantSureInsuranceStatusForm) form).populate(value.<TenantSureInsuranceStatusDTO> cast());
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

    class NoInsuranceStatusForm extends CEntityForm<NoInsuranceStatusDTO> {

        public NoInsuranceStatusForm() {
            super(NoInsuranceStatusDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();

            int row = -1;

            CLabel<String> noInsuranceStatusMessageLabel = new CLabel<String>();
            noInsuranceStatusMessageLabel.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            main.setWidget(++row, 0, inject(proto().noInsuranceStatusMessage(), noInsuranceStatusMessageLabel));
            main.setWidget(++row, 0, inject(proto().tenantInsuranceInvitation(), new CLabel<String>()));
            return main;

        }

    }

    class OtherProviderInsuranceStatusForm extends CEntityForm<OtherProviderInsuranceStatusDTO> {

        public OtherProviderInsuranceStatusForm() {
            super(OtherProviderInsuranceStatusDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();

            int row = -1;

            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().insuranceProvider()), "200px").build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().liabilityCoverage()), "200px").build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().inceptionDate()), "200px").build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().expiryDate()), "200px").build());
            return main;

        }

    }

    class TenantSureInsuranceStatusForm extends CEntityForm<TenantSureInsuranceStatusDTO> {

        public TenantSureInsuranceStatusForm() {
            super(TenantSureInsuranceStatusDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();

            int row = -1;

            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().insuranceProvider()), "200px").build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().insuranceCertificateNumber()), "200px").build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().inceptionDate()), "200px").build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().expiryDate()), "200px").build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().liabilityCoverage()), "200px").build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().contentsCoverage()), "200px").build());
            return main;

        }

    }
}
