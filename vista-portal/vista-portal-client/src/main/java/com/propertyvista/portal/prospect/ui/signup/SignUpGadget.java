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
 */
package com.propertyvista.portal.prospect.ui.signup;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CPasswordBox;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.forms.client.validators.password.HasDescription;
import com.pyx4j.forms.client.validators.password.PasswordStrengthValueValidator;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.security.TenantPasswordStrengthRule;
import com.propertyvista.domain.legal.TermsAndPoliciesType;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.dto.ProspectSignUpDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;
import com.propertyvista.portal.shared.ui.LoginFormPanel;
import com.propertyvista.portal.shared.ui.landing.TermsLinkPanel;

public class SignUpGadget extends AbstractGadget<SignUpViewImpl> {

    static final I18n i18n = I18n.get(SignUpGadget.class);

    private final SignUpForm signupForm;

    SignUpGadget(SignUpViewImpl view) {
        super(view, null, i18n.tr("Create an Account to Begin"), ThemeColor.contrast3, 1);
        setActionsToolbar(new SignUpToolbar());

        final FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        signupForm = new SignUpForm();
        signupForm.init();
        contentPanel.add(signupForm);

        TermsLinkPanel termsLinkPanel = new TermsLinkPanel(i18n.tr("CREATE ACCOUNT"), TermsAndPoliciesType.PVProspectPortalTermsAndConditions,
                PortalSiteMap.PortalTerms.VistaTermsAndConditions.class, TermsAndPoliciesType.PMCProspectPortalTermsAndConditions,
                PortalSiteMap.PortalTerms.PmcTermsAndConditions.class);
        termsLinkPanel.getElement().getStyle().setProperty("maxWidth", 500, Unit.PX);

        contentPanel.add(termsLinkPanel);

        setContent(contentPanel);
    }

    public void init() {
        signupForm.populateNew();
    }

    public void doLayout(LayoutType layoutType) {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            signupForm.signUpPersonalImage.setVisible(false);
            signupForm.signUpTimeImage.setVisible(false);
            break;

        default:
            signupForm.signUpPersonalImage.setVisible(true);
            signupForm.signUpTimeImage.setVisible(true);
            break;
        }
    }

    class SignUpToolbar extends GadgetToolbar {

        private final Button signUpButton;

        public SignUpToolbar() {

            signUpButton = new Button(i18n.tr("CREATE ACCOUNT"), new Command() {
                @Override
                public void execute() {
                    signupForm.setVisitedRecursive();
                    if (signupForm.isValid()) {
                        getGadgetView().getPresenter().signUp(signupForm.getValue());
                    }
                }
            });
            signUpButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));
            addItem(signUpButton);

        }
    }

    class SignUpForm extends CForm<ProspectSignUpDTO> {

        private Image signUpTimeImage;

        private Image signUpPersonalImage;

        public SignUpForm() {
            super(ProspectSignUpDTO.class);
        }

        @Override
        protected IsWidget createContent() {

            FlexTable mainPanel = new FlexTable();
            mainPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            mainPanel.getElement().getStyle().setProperty("maxWidth", "500px");

            signUpTimeImage = new Image(PortalImages.INSTANCE.signUpTime());
            mainPanel.setWidget(0, 0, signUpTimeImage);
            mainPanel.getFlexCellFormatter().setRowSpan(0, 0, 2);
            mainPanel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);

            mainPanel.setWidget(0, 1, new HTML(i18n.tr("<b>Time to Complete</b><div>The online rental application will guide you through several steps."
                    + " The process takes approximately 20 minutes to complete. Required fields are indicated with an (*).</div><br/>")));
            mainPanel.getFlexCellFormatter().getElement(0, 1).getStyle().setTextAlign(TextAlign.LEFT);

            mainPanel.setWidget(1, 0, new HTML(i18n
                    .tr("<b> Don't Worry!</b><div>If you need to step away from your computer to gather information, feel free to log out."
                            + " Upon returning, log in and you will find all your information in the same place you left it.</div><br/>")));
            mainPanel.getFlexCellFormatter().getElement(1, 0).getStyle().setTextAlign(TextAlign.LEFT);

            signUpPersonalImage = new Image(PortalImages.INSTANCE.signUpPersonal());
            mainPanel.setWidget(2, 0, signUpPersonalImage);
            mainPanel.getFlexCellFormatter().setRowSpan(2, 0, 7);
            mainPanel.getFlexCellFormatter().setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);

            LoginFormPanel formPanel = new LoginFormPanel(this);
            mainPanel.setWidget(2, 1, formPanel);

            formPanel.h4(i18n.tr("Enter your first, middle and last name the way it is spelled in your lease agreement:"));
            formPanel.append(Location.Left, proto().firstName()).decorate();
            formPanel.append(Location.Left, proto().middleName()).decorate();
            formPanel.append(Location.Left, proto().lastName()).decorate();
            formPanel.append(Location.Left, proto().email()).decorate();
            formPanel.append(Location.Left, proto().password()).decorate().componentWidth(240);
            formPanel.append(Location.Left, proto().passwordConfirm()).decorate().componentWidth(240);
            formPanel.br();

            CTextFieldBase<?, ?> emailField = (CTextFieldBase<?, ?>) get(proto().email());
            emailField.setNote(i18n.tr("Please note: your email will be your user name"));

            ((CPasswordBox) get(proto().password())).setWatermark(i18n.tr("Create a Password"));

            ((CPasswordBox) get(proto().passwordConfirm())).setWatermark(i18n.tr("Confirm Password"));

            get(proto().passwordConfirm()).addComponentValidator(new AbstractComponentValidator<String>() {
                @Override
                public BasicValidationError isValid() {
                    String password = (get(proto().password())).getValue();
                    if ((password == null && getCComponent().getValue() != null) || (password != null && getCComponent().getValue() == null)
                            || (password != null && !password.equals(getCComponent().getValue()))) {
                        return new BasicValidationError(getCComponent(), i18n.tr("Passwords don't match"));
                    }
                    return null;
                }
            });
            get(proto().password()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().passwordConfirm())));

            return mainPanel;
        }

        @Override
        public void generateMockData() {
            get(proto().firstName()).setMockValue("John");
            get(proto().middleName()).setMockValue("A");
            get(proto().lastName()).setMockValue("Doe");
            String email = "johndoe" + (int) System.currentTimeMillis() + "@pyx4j.com";
            get(proto().email()).setMockValue(email);
            get(proto().password()).setMockValue(email);
            get(proto().passwordConfirm()).setMockValue(email);
        }

        @Override
        public void addValidations() {
            TenantPasswordStrengthRule passwordStrengthRule = new TenantPasswordStrengthRule(null, null);
            ((CPasswordBox) get(proto().password())).setPasswordStrengthRule(passwordStrengthRule);
            get(proto().password()).addComponentValidator(new PasswordStrengthValueValidator(passwordStrengthRule));

            if (passwordStrengthRule != null && (passwordStrengthRule instanceof HasDescription)) {
                get(proto().password()).setTooltip(((HasDescription) passwordStrengthRule).getDescription());
            } else {
                get(proto().password()).setTooltip(null);
            }
        }

    }

}
