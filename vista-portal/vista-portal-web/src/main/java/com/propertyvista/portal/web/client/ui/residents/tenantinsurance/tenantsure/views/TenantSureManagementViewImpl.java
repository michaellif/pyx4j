/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceStatusDetailedDTO;
import com.propertyvista.portal.web.client.themes.TenantInsuranceTheme;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureStatusForm;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureViewDecorator;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.resources.TenantSureResources;

public class TenantSureManagementViewImpl extends Composite implements TenantSureManagementView {

    private static final I18n i18n = I18n.get(TenantSureManagementViewImpl.class);

    @Transient
    public static interface EmailHolder extends IEntity {

        @Editor(type = EditorType.email)
        IPrimitive<String> emailAddress();

    }

    // TODO (VISTA-2625) something is wrong here: email doesn't get propagated to ccomponent, because NFocusComponen. BlurHandler doesn't fire when user click OK button 
    @Deprecated
    private abstract static class EmailInputDialog extends OkCancelDialog {

        private CEntityForm<EmailHolder> form;

        public EmailInputDialog(String defaultEmail) {
            super(i18n.tr("Enter your email:"));

            form = new CEntityDecoratableForm<EmailHolder>(EmailHolder.class) {
                @Override
                public IsWidget createContent() {
                    FlowPanel panel = new FlowPanel();
                    panel.add(new DecoratorBuilder(inject(proto().emailAddress())).customLabel("").useLabelSemicolon(false).labelWidth(0).componentWidth(15)
                            .build());
                    return panel;
                }
            };
            form.initContent();

            EmailHolder emailHolder = EntityFactory.create(EmailHolder.class);
            emailHolder.emailAddress().setValue(defaultEmail);
            form.populate(emailHolder);

            setBody(form);
            setSize("20em", "2.5em");
        }

        protected final String getEmail() {
            form.revalidate();
            form.setUnconditionalValidationErrorRendering(true);
            if (form.isValid()) {
                return form.getValue().emailAddress().getValue();
            } else {
                return null;
            }

        }

    }

    private Presenter presenter;

    private TenantSureStatusForm statusForm;

    private Anchor cancelTenantSureButton;

    private Anchor updateCCButton;

    private Anchor updateCCAndPay;

    private Anchor sendDocumentationButton;

    private Anchor reinstateTenantSureButton;

    public TenantSureManagementViewImpl() {
        TenantSureViewDecorator viewDecorator = new TenantSureViewDecorator();
        viewDecorator.setPrivacyPolcyAddress(TenantSureConstants.HIGHCOURT_PARTNERS_PRIVACY_POLICY_HREF);
        viewDecorator.setCompensationDisclosureStatement(TenantSureConstants.HIGHCOURT_PARTNERS_COMPENSATION_DISCLOSURE_STATEMENT_HREF);

        FlowPanel viewPanel = new FlowPanel();
        viewPanel.add(makeGreetingPanel());

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.setStyleName(TenantInsuranceTheme.StyleName.TenantSureManagementContentPanel.name());
        contentPanel.add(makeStatusDetailsPanel());
        contentPanel.add(makeActionsPanel());

        viewPanel.add(contentPanel);

        viewDecorator.setContent(viewPanel);
        initWidget(viewDecorator);
    }

    @Override
    public void populate(TenantSureTenantInsuranceStatusDetailedDTO detailedStatus) {
        statusForm.populate(detailedStatus);
        boolean isCancelled = !detailedStatus.expiryDate().isNull();

        updateCCButton.setEnabled(!isCancelled);
        updateCCButton.setVisible(!detailedStatus.isPaymentFailed().isBooleanTrue());
        updateCCAndPay.setVisible(detailedStatus.isPaymentFailed().isBooleanTrue());

        cancelTenantSureButton.setEnabled(!isCancelled);
        reinstateTenantSureButton.setVisible(isCancelled);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void reportUpdateCreditCardUpdate(String errorMessage) {
        MessageDialog.error(i18n.tr("Error"), errorMessage);
    }

    @Override
    public void reportCancelFailure(String errorMessage) {
        MessageDialog.error(i18n.tr("Error"), errorMessage);
    }

    @Override
    public void reportError(String errorMessage) {
        MessageDialog.error(i18n.tr("Error"), errorMessage);
    }

    @Override
    public void reportSendCertificateSuccess(String emailAddress) {
        MessageDialog.info(i18n.tr("Your insurance certificate was sent to {0}.", emailAddress));
    }

    private Widget makeGreetingPanel() {
        FlowPanel tenantSureGreetingPanel = new FlowPanel();
        tenantSureGreetingPanel.setStyleName(TenantInsuranceTheme.StyleName.TenantSureManagementGreetingPanel.name());

        HTML greeting = new HTML(TenantSureResources.INSTANCE.managementPanelGreeting().getText());
        greeting.setStyleName(TenantInsuranceTheme.StyleName.TenantSureManagementGreeting.name());

        tenantSureGreetingPanel.add(greeting);
        return tenantSureGreetingPanel;
    }

    private Widget makeStatusDetailsPanel() {
        FlowPanel statusPanel = new FlowPanel();
        statusPanel.setStyleName(TenantInsuranceTheme.StyleName.TenantSureManagementStatusDetailsPanel.name());
        statusForm = new TenantSureStatusForm();
        statusForm.initContent();
        statusPanel.add(statusForm);
        return statusPanel;
    }

    private Widget makeActionsPanel() {
        FlowPanel actionsPanel = new FlowPanel();
        actionsPanel.setStyleName(TenantInsuranceTheme.StyleName.TenantSureManagementActionsPanel.name());

        sendDocumentationButton = new Anchor(i18n.tr("Resend Insurance Certificate"), new Command() {
            @Override
            public void execute() {
                presenter.sendCertificate(null);
            }
        });

        updateCCButton = new Anchor(i18n.tr("Update Credit Card Details"), new Command() {
            @Override
            public void execute() {
                presenter.updateCreditCardDetails();
            }
        });

        updateCCAndPay = new Anchor(i18n.tr("Update Credid Card and Pay"), new Command() {

            @Override
            public void execute() {
                presenter.updateCreditCardDetails();
            }
        });

        cancelTenantSureButton = new Anchor(i18n.tr("Cancel TenantSure"), new Command() {
            @Override
            public void execute() {
                TenantSureManagementViewImpl.this.onCancelTenantSure();
            }
        });

        reinstateTenantSureButton = new Anchor(i18n.tr("Reinstate"), new Command() {
            @Override
            public void execute() {
                onReinstateTenantSure();
            }
        });

        Anchor viewFaq = new Anchor(i18n.tr("FAQ"), new Command() {
            @Override
            public void execute() {
                presenter.viewFaq();
            }
        });

        Anchor aboutTenantSure = new Anchor(i18n.tr("About TenantSure / Contact Us"), new Command() {
            @Override
            public void execute() {
                presenter.viewAboutTenantSure();
            }
        });

        Anchor makeAClaim = new Anchor(i18n.tr("Make a Claim"), new Command() {
            @Override
            public void execute() {
                TenantSureManagementViewImpl.this.onMakeAClaim();
            }
        });

        actionsPanel.add(sendDocumentationButton);
        actionsPanel.add(makeAClaim);
        actionsPanel.add(updateCCButton);
        actionsPanel.add(updateCCAndPay);
        actionsPanel.add(new HTML("&nbsp;")); // add separator 
        actionsPanel.add(cancelTenantSureButton);
        actionsPanel.add(reinstateTenantSureButton);

        actionsPanel.add(new HTML("&nbsp;")); // add separator 

        actionsPanel.add(viewFaq);
        actionsPanel.add(aboutTenantSure);

        return actionsPanel;
    }

    private void onCancelTenantSure() {
        MessageDialog.confirm(i18n.tr("TenantSure cancellation"), i18n.tr("Are you sure you want to cancel TenantSure?"), new Command() {
            @Override
            public void execute() {
                presenter.cancelTenantSure();
            }
        });
    }

    private void onReinstateTenantSure() {
        MessageDialog.confirm(i18n.tr("TenantSure Reinstatement"), i18n.tr("Are you sure you want to reinstate TenantSure?"), new Command() {
            @Override
            public void execute() {
                presenter.reinstate();
            }
        });
    }

    private void onMakeAClaim() {
        MessageDialog.info(i18n.tr("To make a claim please call {0} at {1}", TenantSureConstants.TENANTSURE_LEGAL_NAME,
                TenantSureConstants.TENANTSURE_PHONE_NUMBER));
    }

}
