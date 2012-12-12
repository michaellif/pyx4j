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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;

public class PmcAccountCreationRequestForm extends CEntityDecoratableForm<PmcAccountCreationRequest> {

    public enum Styles implements IStyleName {

        PmcAccountCreationSubmitButton

    }

    public interface DnsCheckRequestHandler {

        public void checkDns(AsyncCallback<Boolean> callback, String dnsName);

    }

    private static final I18n i18n = I18n.get(PmcAccountCreationRequestForm.class);

    private boolean isDnsAvailable;

    private boolean isDnsCheckResponseRecieved;

    private final DnsCheckRequestHandler dnsNameCheckRequestHandler;

    public PmcAccountCreationRequestForm(DnsCheckRequestHandler dnsCheckRequestHandler) {
        super(PmcAccountCreationRequest.class);
        this.dnsNameCheckRequestHandler = dnsCheckRequestHandler;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel contentPanel = new FormFlexPanel();
        final double SPACE = 10.0;
        int row = -1;

        FlowPanel dnsNamePanel = new FlowPanel();
        SimplePanel dnsNamePrefixHolder = new SimplePanel();
        dnsNamePrefixHolder.setWidget(new DecoratorBuilder(inject(proto().dnsName()), 10).build());
        dnsNamePrefixHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        dnsNamePanel.add(dnsNamePrefixHolder);

        Label dnsNameSuffix = new Label(".propertyvista.com");
        dnsNameSuffix.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        dnsNamePanel.add(dnsNameSuffix);
        contentPanel.setWidget(++row, 0, dnsNamePanel);
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        contentPanel.getFlexCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);
        contentPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingBottom(SPACE, Unit.PX);
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

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name())).build());
        contentPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingBottom(SPACE, Unit.PX);

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().firstName())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lastName())).build());
        contentPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingBottom(SPACE, Unit.PX);

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().confirmEmail())).build());
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

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().password())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().confirmPassword())).build());
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

        FlowPanel termsAgreementShortcutPanel = new FlowPanel();
        Label termsAgreementLabel = new Label(i18n.tr("By clicking Sign Up, you are acknowledging that you have read and agree to our "));
        termsAgreementLabel.getElement().getStyle().setDisplay(Display.INLINE);
        termsAgreementShortcutPanel.add(termsAgreementLabel);
        Anchor termsAnchor = new Anchor(i18n.tr("Terms of Service"));
        termsAnchor.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                onTermsOpenRequest();
            }
        });
        termsAgreementShortcutPanel.add(termsAnchor);
        Label dot = new Label(".");
        dot.getElement().getStyle().setDisplay(Display.INLINE);
        termsAgreementShortcutPanel.add(dot);
        contentPanel.setWidget(++row, 0, termsAgreementShortcutPanel);
        contentPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingTop(SPACE + SPACE, Unit.PX);
        contentPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingTop(SPACE + SPACE, Unit.PX);
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        final Button submitButton = new Button(i18n.tr("Sign Up"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                revalidate();
                if (isValid()) {
                    onSubmit(getValue().duplicate(PmcAccountCreationRequest.class));
                }
            }

        });
        submitButton.addStyleName(PmcAccountCreationRequestForm.Styles.PmcAccountCreationSubmitButton.name());
        submitButton.setEnabled(false);

        contentPanel.setWidget(++row, 0, submitButton);
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        addValueChangeHandler(new ValueChangeHandler<PmcAccountCreationRequest>() {
            @Override
            public void onValueChange(ValueChangeEvent<PmcAccountCreationRequest> event) {
                revalidate();
                submitButton.setEnabled(isValid());
            }
        });

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

        return contentPanel;
    }

    @Override
    public boolean isValid() {
        return super.isValid() & isDnsAvailable;
    }

    public void onSubmit(PmcAccountCreationRequest duplicate) {

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

        String password = request.email().getValue();
        request.password().setValue(password);
        request.confirmPassword().setValue(password);

        setValue(request);
    }
}
