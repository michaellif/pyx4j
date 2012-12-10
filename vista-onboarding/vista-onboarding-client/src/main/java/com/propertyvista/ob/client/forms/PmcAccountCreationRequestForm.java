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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEmailField;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;

public class PmcAccountCreationRequestForm extends CEntityDecoratableForm<PmcAccountCreationRequest> {

    public enum Style implements IStyleName {

        DnsCheckLabelChecking, DnsCheckLabelAvailable, DnsCheckLabelNotAvailable

    }

    public interface DnsCheckRequestHandler {

        public void checkDns(AsyncCallback<Boolean> callback, String dnsName);

    }

    private static final I18n i18n = I18n.get(PmcAccountCreationRequestForm.class);

    private boolean isDnsAvailable;

    private Label dnsNameCheckResult;

    private final DnsCheckRequestHandler dnsNameCheckRequestHandler;

    public PmcAccountCreationRequestForm(DnsCheckRequestHandler dnsCheckRequestHandler) {
        super(PmcAccountCreationRequest.class);
        this.dnsNameCheckRequestHandler = dnsCheckRequestHandler;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel contentPanel = new FormFlexPanel();
        int row = -1;

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().firstName())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lastName())).build());

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name())).build());

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().dnsName())).build());
        get(proto().dnsName()).addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                if (event.getValue() != null) {
                    PmcAccountCreationRequestForm.this.onDnsAvailabilityCheckRequested(event.getValue());
                }
            }
        });
        dnsNameCheckResult = new Label();
        contentPanel.setWidget(row, 1, dnsNameCheckResult);

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email())).build());
        CEmailField emailConfirmation = new CEmailField(i18n.tr("Email Confirmation"), true);
        emailConfirmation.addValueValidator(new EditableValueValidator<String>() {
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
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(emailConfirmation).build());

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().password())).build());
        CPasswordTextField passwordConfirmation = new CPasswordTextField(i18n.tr("Password Confirmation"), true);
        passwordConfirmation.addValueValidator(new EditableValueValidator<String>() {
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
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(passwordConfirmation).build());

        final Button submitButton = new Button(i18n.tr("Sign Up"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onSubmit(getValue().duplicate(PmcAccountCreationRequest.class));
            }

        });
        submitButton.setEnabled(false);
        contentPanel.setWidget(++row, 0, submitButton);

        addValueChangeHandler(new ValueChangeHandler<PmcAccountCreationRequest>() {
            @Override
            public void onValueChange(ValueChangeEvent<PmcAccountCreationRequest> event) {
                revalidate();
                submitButton.setEnabled(isValid());
            }
        });
        return contentPanel;
    }

    @Override
    public boolean isValid() {
        return super.isValid() & isDnsAvailable;
    }

    public void onSubmit(PmcAccountCreationRequest duplicate) {

    }

    private void onDnsAvailabilityCheckRequested(String dnsName) {
        this.dnsNameCheckResult.setText(i18n.tr("Checking Availability..."));
        this.dnsNameCheckRequestHandler.checkDns(new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                setDnsAvailability(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                MessageDialog.error(i18n.tr("DNS Check Failed"), caught.getMessage());
                PmcAccountCreationRequestForm.this.dnsNameCheckResult.setText("");
            }

        }, dnsName);
    }

    private void setDnsAvailability(boolean isDnsAvailable) {
        this.isDnsAvailable = isDnsAvailable;
        if (isDnsAvailable) {
            this.dnsNameCheckResult.setText(i18n.tr("DNS is Available"));
            this.dnsNameCheckResult.setStyleName(Style.DnsCheckLabelAvailable.name());
        } else {
            this.dnsNameCheckResult.setText(i18n.tr("DNS is Not Available"));
            this.dnsNameCheckResult.setStyleName(Style.DnsCheckLabelNotAvailable.name());
        }
    }

}
