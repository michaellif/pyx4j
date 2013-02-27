/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.ob.rpc.OnboardingSiteMap;
import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;

// TODO refactor this form with using a special decorator (same or similar to one that is used in login/singup forms in portal
public class PmcAccountCreationRequestForm extends CEntityDecoratableForm<PmcAccountCreationRequest> {

    public enum Styles implements IStyleName {

        PmcAccountCreationRequestForm, PmcUrlNamePanel, PmcUrlFieldNote, PmcAccountCreationSubmitButton

    }

    public interface DnsCheckRequestHandler {

        public void checkDns(AsyncCallback<Boolean> callback, String dnsName);

    }

    private static final I18n i18n = I18n.get(PmcAccountCreationRequestForm.class);

    private boolean isDnsAvailable;

    private boolean isDnsCheckResponseRecieved;

    private final DnsCheckRequestHandler dnsNameCheckRequestHandler;

    private Button submitButton;

    public PmcAccountCreationRequestForm(DnsCheckRequestHandler dnsCheckRequestHandler) {
        super(PmcAccountCreationRequest.class);
        this.dnsNameCheckRequestHandler = dnsCheckRequestHandler;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel formPanel = new FlowPanel();
        FormFlexPanel contentPanel = new FormFlexPanel();
        contentPanel.addStyleName(Styles.PmcAccountCreationRequestForm.name());
        final double SPACE = 10.0;
        int row = -1;

        FlowPanel dnsNamePanel = new FlowPanel();
        dnsNamePanel.addStyleName(Styles.PmcUrlFieldNote.name());
        SimplePanel dnsSubdomainNameHolder = new SimplePanel();
        dnsSubdomainNameHolder.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        dnsSubdomainNameHolder.setWidget(new DecoratorBuilder(inject(proto().dnsName()), 10).customLabel("").labelWidth(0).useLabelSemicolon(false)
                .mandatoryMarker(false).build());

        Label dnsDomainName = new Label(".propertyvista.com");
        dnsDomainName.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        dnsNamePanel.add(dnsSubdomainNameHolder);
        dnsNamePanel.add(dnsDomainName);

        formPanel.add(dnsNamePanel);

        get(proto().dnsName()).addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                if (event.getValue() != null) {
                    isDnsAvailable = false;
                    isDnsCheckResponseRecieved = false;
                    PmcAccountCreationRequestForm.this.onDnsAvailabilityCheckRequested(event.getValue());
                }
            }
        });
        get(proto().dnsName()).addValueValidator(new EditableValueValidator<String>() {

            @Override
            public ValidationError isValid(CComponent<String, ?> component, String value) {
                if (value != null && !isDnsAvailable && isDnsCheckResponseRecieved) {
                    return new ValidationError(component, i18n.tr("DNS is not available"));
                } else {
                    return null;
                }
            }
        });

        contentPanel.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().name())).customLabel("").labelWidth(0).useLabelSemicolon(false).mandatoryMarker(false).customLabel("")
                        .labelWidth(0).build());
        contentPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingBottom(SPACE, Unit.PX);

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().countryOfOperation())).customLabel("").labelWidth(0).useLabelSemicolon(false)
                .mandatoryMarker(false).build());
        contentPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingBottom(SPACE, Unit.PX);

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().firstName())).customLabel("").labelWidth(0).useLabelSemicolon(false)
                .mandatoryMarker(false).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lastName())).customLabel("").labelWidth(0).useLabelSemicolon(false)
                .mandatoryMarker(false).build());
        contentPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingBottom(SPACE, Unit.PX);

        contentPanel.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().email())).customLabel("").labelWidth(0).useLabelSemicolon(false).mandatoryMarker(false).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().confirmEmail())).customLabel("").labelWidth(0).useLabelSemicolon(false)
                .mandatoryMarker(false).build());
        get(proto().confirmEmail()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String, ?> component, String emailConfirmationValue) {
                String email = get(proto().email()).getValue();
                if (email != null && !email.equals(emailConfirmationValue)) {
                    return new ValidationError(component, i18n.tr("Email and Email Confirmation don't match"));
                } else {
                    return null;
                }
            }
        });
        contentPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingBottom(SPACE, Unit.PX);

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().password())).customLabel("").labelWidth(0).useLabelSemicolon(false)
                .mandatoryMarker(false).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().confirmPassword())).customLabel("").labelWidth(0).useLabelSemicolon(false)
                .mandatoryMarker(false).build());
        get(proto().confirmPassword()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String, ?> component, String passwordConfirmationValue) {
                String password = get(proto().password()).getValue();
                if (password != null && !password.equals(passwordConfirmationValue)) {
                    return new ValidationError(component, i18n.tr("Password and Password Confirmation don't match"));
                } else {
                    return null;
                }
            }
        });

        HorizontalPanel signUpPanel = new HorizontalPanel();
        signUpPanel.getElement().getStyle().setMarginLeft(1, Unit.EM);
        signUpPanel.getElement().getStyle().setMarginTop(30, Unit.PX);
        signUpPanel.setWidth("26em");
        FlowPanel termsAgreementShortcutPanel = new FlowPanel();
        Label termsAgreementLabel = new Label(i18n.tr("By clicking Sign Up, you are acknowledging that you have read and agree to our "));
        termsAgreementLabel.getElement().getStyle().setDisplay(Display.INLINE);
        termsAgreementShortcutPanel.add(termsAgreementLabel);

        Anchor termsAnchor = new Anchor(i18n.tr("Terms of Service"),
                AppPlaceInfo.absoluteUrl(GWT.getHostPageBaseURL(), OnboardingSiteMap.PmcAccountTerms.class));
        termsAnchor.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                onTermsOpenRequest();
                DOM.eventPreventDefault((com.google.gwt.user.client.Event) event.getNativeEvent());
            }
        });

        termsAgreementShortcutPanel.add(termsAnchor);
        Label dot = new Label(".");
        dot.getElement().getStyle().setDisplay(Display.INLINE);
        termsAgreementShortcutPanel.add(dot);
        termsAgreementShortcutPanel.getElement().getStyle().setPaddingRight(20, Unit.PX);
        signUpPanel.add(termsAgreementShortcutPanel);

        submitButton = new Button(i18n.tr("Sign Up"), new Command() {
            @Override
            public void execute() {
                revalidate();
                setUnconditionalValidationErrorRendering(true);
                if (isValid()) {
                    onSubmit(getValue().duplicate(PmcAccountCreationRequest.class));
                }
            }

        });
        submitButton.addStyleName(PmcAccountCreationRequestForm.Styles.PmcAccountCreationSubmitButton.name());

        signUpPanel.add(submitButton);
        signUpPanel.setCellHorizontalAlignment(termsAgreementShortcutPanel, HasHorizontalAlignment.ALIGN_CENTER);
        signUpPanel.setCellVerticalAlignment(termsAgreementShortcutPanel, HasVerticalAlignment.ALIGN_MIDDLE);
        signUpPanel.setCellWidth(termsAgreementShortcutPanel, "60%");
        signUpPanel.setCellHorizontalAlignment(submitButton, HasHorizontalAlignment.ALIGN_CENTER);
        signUpPanel.setCellVerticalAlignment(submitButton, HasVerticalAlignment.ALIGN_MIDDLE);
        contentPanel.setWidget(++row, 0, signUpPanel);

        if (ApplicationMode.isDevelopment()) {
            this.addDevShortcutHandler(new DevShortcutHandler() {
                @Override
                public void onDevShortcut(DevShortcutEvent event) {
                    if (event.getKeyCode() == 'Q') {
                        event.consume();
                        devGeneratePmcData();
                    }
                }

            });
        }

        formPanel.add(contentPanel);
        return formPanel;
    }

    @Override
    public boolean isValid() {
        return super.isValid() & isDnsAvailable;
    }

    public void onSubmit(PmcAccountCreationRequest duplicate) {

    }

    public void setSubmitEnable(boolean isEnabled) {
        submitButton.setEnabled(isEnabled);
    }

    public void onTermsOpenRequest() {

    }

    private void onDnsAvailabilityCheckRequested(String dnsName) {
        if (dnsName.length() > 0) {
            this.dnsNameCheckRequestHandler.checkDns(new AsyncCallback<Boolean>() {

                @Override
                public void onSuccess(Boolean result) {
                    setDnsAvailability(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                    setDnsAvailability(false);
                    MessageDialog.error(i18n.tr("DNS availability check error"), caught.getMessage());
                }

            }, dnsName);
        }
    }

    private void setDnsAvailability(boolean isDnsAvailable) {
        this.isDnsAvailable = isDnsAvailable;
        this.isDnsCheckResponseRecieved = true;
        get(proto().dnsName()).revalidate();
        if (isDnsAvailable) {
            this.refresh(true);
        }
    }

    private void devGeneratePmcData() {
        PmcAccountCreationRequest request = EntityFactory.create(PmcAccountCreationRequest.class);

        String id = "p" + String.valueOf(System.currentTimeMillis());
        request.name().setValue(id);
        request.dnsName().setValue(id);
        request.firstName().setValue("F");
        request.lastName().setValue("L");
        request.email().setValue(id + "@pyx4j.com");
        request.confirmEmail().setValue(request.email().getValue());
        request.countryOfOperation().setValue(CountryOfOperation.Canada);

        String password = request.email().getValue();
        request.password().setValue(password);
        request.confirmPassword().setValue(password);

        setValue(request);
    }
}
