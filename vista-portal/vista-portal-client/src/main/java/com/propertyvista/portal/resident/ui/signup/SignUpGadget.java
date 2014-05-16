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
package com.propertyvista.portal.resident.ui.signup;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.events.NValueChangeEvent;
import com.pyx4j.forms.client.events.NValueChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.forms.client.validators.password.PasswordStrengthValueValidator;
import com.pyx4j.forms.client.validators.password.PasswordStrengthWidget;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.security.TenantPasswordStrengthRule;
import com.propertyvista.domain.legal.TermsAndPoliciesType;
import com.propertyvista.portal.resident.ui.signup.SignUpView.SignUpPresenter;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.ResidentSelfRegistrationDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.shared.EntityValidationException;
import com.propertyvista.portal.rpc.shared.EntityValidationException.MemberValidationError;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;
import com.propertyvista.portal.shared.ui.LoginFormPanel;
import com.propertyvista.portal.shared.ui.landing.TermsLinkPanel;

public class SignUpGadget extends AbstractGadget<SignUpView> {

    static final I18n i18n = I18n.get(SignUpGadget.class);

    private SignUpPresenter presenter;

    private final SignUpForm signupForm;

    SignUpGadget(SignUpViewImpl view) {
        super(view, null, i18n.tr("Create Your Account"), ThemeColor.contrast2, 1);
        setActionsToolbar(new SignUpToolbar());

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        signupForm = new SignUpForm();
        signupForm.init();
        contentPanel.add(signupForm);

        TermsLinkPanel termsLinkPanel = new TermsLinkPanel(i18n.tr("REGISTER"), TermsAndPoliciesType.PVResidentPortalTermsAndConditions,
                PortalSiteMap.PortalTerms.VistaTermsAndConditions.class, TermsAndPoliciesType.PMCResidentPortalTermsAndConditions,
                PortalSiteMap.PortalTerms.PmcTermsAndConditions.class);
        termsLinkPanel.getElement().getStyle().setProperty("maxWidth", 500, Unit.PX);

        contentPanel.add(termsLinkPanel);

        setContent(contentPanel);
    }

    public void setPresenter(SignUpPresenter presenter) {
        this.presenter = presenter;
    }

    public void showValidationError(EntityValidationException caught) {
        signupForm.setEntityValidationError(caught);
    }

    public void init(List<SelfRegistrationBuildingDTO> buildings) {
        signupForm.init(buildings);
    }

    public void doLayout(LayoutType layoutType) {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            signupForm.signUpBuildingImage.setVisible(false);
            signupForm.signUpPersonalImage.setVisible(false);
            signupForm.signUpSecurity.setVisible(false);
            signupForm.mainPanel.getColumnFormatter().setWidth(0, "0px");
            break;

        default:
            signupForm.signUpBuildingImage.setVisible(true);
            signupForm.signUpPersonalImage.setVisible(true);
            signupForm.signUpSecurity.setVisible(true);
            signupForm.mainPanel.getColumnFormatter().setWidth(0, "150px");
            break;
        }
    }

    class SignUpToolbar extends GadgetToolbar {

        private final Button signUpButton;

        public SignUpToolbar() {

            signUpButton = new Button(i18n.tr("REGISTER"), new Command() {
                @Override
                public void execute() {
                    signupForm.setEntityValidationError(null);
                    signupForm.setVisitedRecursive();
                    if (signupForm.isValid()) {
                        presenter.register(signupForm.getValue());
                    }
                }
            });
            signUpButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));
            addItem(signUpButton);

        }
    }

    class SignUpForm extends CForm<ResidentSelfRegistrationDTO> {

        private FlexTable mainPanel;

        private BuildingSuggestBox buildingSelector;

        private EntityValidationException entityValidationException;

        private Image signUpBuildingImage;

        private Image signUpPersonalImage;

        private Image signUpSecurity;

        private PasswordStrengthWidget passwordStrengthWidget;

        private final TenantPasswordStrengthRule passwordStrengthRule;

        private HTML supportField;

        public SignUpForm() {
            super(ResidentSelfRegistrationDTO.class);
            this.passwordStrengthRule = new TenantPasswordStrengthRule(null, null);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected IsWidget createContent() {
            mainPanel = new FlexTable();
            mainPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            mainPanel.getElement().getStyle().setProperty("maxWidth", "500px");

            signUpBuildingImage = new Image(PortalImages.INSTANCE.signUpBuilding());
            mainPanel.setWidget(0, 0, signUpBuildingImage);
            mainPanel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);

            LoginFormPanel buildingSelectionPanel = new LoginFormPanel(this);
            mainPanel.setWidget(0, 1, buildingSelectionPanel);

            buildingSelectionPanel.h4(i18n.tr("Which building do you live in?"));

            buildingSelectionPanel.append(Location.Left, proto().building(), new BuildingSuggestBox()).decorate();

            buildingSelector = (BuildingSuggestBox) get(proto().building());
            buildingSelector.setWatermark(i18n.tr("Your building's address"));
            buildingSelector.setNote(i18n.tr("Search by typing your building's street, postal code, province etc..."));

            buildingSelectionPanel.br();

            signUpPersonalImage = new Image(PortalImages.INSTANCE.signUpPersonal());
            mainPanel.setWidget(1, 0, signUpPersonalImage);
            mainPanel.getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);

            LoginFormPanel userInfoPanel = new LoginFormPanel(this);
            mainPanel.setWidget(1, 1, userInfoPanel);

            userInfoPanel.h4(i18n.tr("Enter your first, middle and last name the way it is spelled in your lease agreement:"));
            userInfoPanel.append(Location.Left, proto().firstName()).decorate();
            userInfoPanel.append(Location.Left, proto().middleName()).decorate();
            userInfoPanel.append(Location.Left, proto().lastName()).decorate();
            userInfoPanel.append(Location.Left, proto().email()).decorate();

            CField<String, ?> emailField = (CField<String, ?>) get(proto().email());
            emailField.setNote(i18n.tr("Please note: your email will be your user name"));

            userInfoPanel.br();

            signUpSecurity = new Image(PortalImages.INSTANCE.signUpSecurity());
            mainPanel.setWidget(2, 0, signUpSecurity);
            mainPanel.getFlexCellFormatter().setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);

            LoginFormPanel securityPanel = new LoginFormPanel(this);
            mainPanel.setWidget(2, 1, securityPanel);

            securityPanel.h4(i18n.tr("The Security Code is a secure identifier that is provided by your Property Manager specifically for you."));
            securityPanel.append(Location.Left, proto().securityCode()).decorate().componentWidth(180);
            securityPanel.append(Location.Left, proto().password()).decorate().componentWidth(180)
                    .assistantWidget(passwordStrengthWidget = new PasswordStrengthWidget(passwordStrengthRule));
            securityPanel.append(Location.Left, proto().passwordConfirm()).decorate().componentWidth(180);

            final CTextFieldBase<String, ?> securityCodeField = (CTextFieldBase<String, ?>) get(proto().securityCode());
            securityCodeField
                    .setTooltip(i18n
                            .tr("You should have received Security Code by mail. Don't have a Security Code? To get your own unique access code, please contact the Property Manager directly."));
            securityCodeField.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    // This clears Security Code error when field value is changed
                    if (SignUpForm.this.entityValidationException != null && SignUpForm.this.entityValidationException.clearError(proto().securityCode())) {
                        securityCodeField.revalidate();
                    }
                }
            });

            ((CPasswordTextField) get(proto().password())).setWatermark(i18n.tr("Create a Password"));
            get(proto().password()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().passwordConfirm())));

            get(proto().passwordConfirm()).addComponentValidator(new AbstractComponentValidator<String>() {
                @Override
                public BasicValidationError isValid() {
                    String password = (get(proto().password())).getValue();
                    if ((password == null && getComponent().getValue() != null) || (password != null && !password.equals(getComponent().getValue()))) {
                        return new BasicValidationError(getComponent(), i18n.tr("Passwords don't match"));
                    }
                    return null;
                }
            });
            ((CPasswordTextField) get(proto().passwordConfirm())).setWatermark(i18n.tr("Confirm Password"));

            securityPanel.br();

            supportField = new HTML();
            supportField.getElement().getStyle().setPaddingBottom(20, Unit.PX);
            mainPanel.setWidget(4, 0, supportField);
            mainPanel.getFlexCellFormatter().setColSpan(4, 0, 2);
            mainPanel.getFlexCellFormatter().getElement(4, 0).getStyle().setTextAlign(TextAlign.LEFT);

            return mainPanel;
        }

        public void init(List<SelfRegistrationBuildingDTO> buildings) {
            buildingSelector.setOptions(buildings);
            buildingSelector.addValueChangeHandler(new ValueChangeHandler<SelfRegistrationBuildingDTO>() {
                @Override
                public void onValueChange(ValueChangeEvent<SelfRegistrationBuildingDTO> event) {
                    supportField.setVisible(false);
                    if (event.getValue() != null) {
                        supportField.setText(i18n.tr("In case you have issues with the registration please call ") + event.getValue().supportPhone().getValue());
                        supportField.setVisible(!event.getValue().supportPhone().isNull());
                    }
                }
            });

            populateNew();
        }

        public void setEntityValidationError(EntityValidationException caught) {
            this.entityValidationException = caught;
            if (caught != null) {
                for (MemberValidationError memberError : caught.getErrors()) {
                    get(memberError.getMember()).setAsyncValidationErrorMessage(memberError.getMessage());
                }
            }
        }

        @Override
        public void addValidations() {
            ((CTextFieldBase<?, ?>) get(proto().password())).addNValueChangeHandler(new NValueChangeHandler<String>() {

                @Override
                public void onNValueChange(NValueChangeEvent<String> event) {
                    passwordStrengthWidget.ratePassword(event.getValue());
                }
            });

            get(proto().password()).addComponentValidator(new PasswordStrengthValueValidator(passwordStrengthRule));
        }

    }

    public final void reset() {
        signupForm.reset();
    }

}
