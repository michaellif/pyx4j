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

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.events.NValueChangeEvent;
import com.pyx4j.forms.client.events.NValueChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.forms.client.validators.password.PasswordStrengthValueValidator;
import com.pyx4j.forms.client.validators.password.PasswordStrengthWidget;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
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
import com.propertyvista.portal.shared.ui.landing.TermsLinkPanel;
import com.propertyvista.portal.shared.ui.util.decorators.LoginWidgetDecoratorBuilder;

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
        signupForm.initContent();
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
            break;

        default:
            signupForm.signUpBuildingImage.setVisible(true);
            signupForm.signUpPersonalImage.setVisible(true);
            signupForm.signUpSecurity.setVisible(true);
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

    class SignUpForm extends CEntityForm<ResidentSelfRegistrationDTO> {

        private BuildingSuggestBox buildingSelector;

        private EntityValidationException entityValidationError;

        private Image signUpBuildingImage;

        private Image signUpPersonalImage;

        private Image signUpSecurity;

        private PasswordStrengthWidget passwordStrengthWidget;

        private final TenantPasswordStrengthRule passwordStrengthRule;

        public SignUpForm() {
            super(ResidentSelfRegistrationDTO.class);
            this.passwordStrengthRule = new TenantPasswordStrengthRule(null, null);
        }

        @SuppressWarnings("unchecked")
        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel flexPanel = new BasicFlexFormPanel();
            flexPanel.getColumnFormatter().setWidth(0, "50px");
            flexPanel.getColumnFormatter().setWidth(1, "300px");
            int row = -1;

            signUpBuildingImage = new Image(PortalImages.INSTANCE.signUpBuilding());
            flexPanel.setWidget(++row, 0, signUpBuildingImage);
            flexPanel.getFlexCellFormatter().setRowSpan(row, 0, 2);
            flexPanel.getFlexCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);

            flexPanel.setH4(row, 1, 1, i18n.tr("Which building do you live in?"));

            buildingSelector = (inject(proto().building(), new BuildingSuggestBox()));
            buildingSelector.setWatermark(i18n.tr("Your building's address"));
            buildingSelector.setNote(i18n.tr("Search by typing your building's street, postal code, province etc..."));
            flexPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(buildingSelector).build());
            flexPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);

            flexPanel.setBR(++row, 0, 2);

            signUpPersonalImage = new Image(PortalImages.INSTANCE.signUpPersonal());
            flexPanel.setWidget(++row, 0, signUpPersonalImage);
            flexPanel.getFlexCellFormatter().setRowSpan(row, 0, 5);
            flexPanel.getFlexCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);

            flexPanel.setH4(row, 1, 1, i18n.tr("Enter your first, middle and last name the way it is spelled in your lease agreement:"));

            flexPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(inject(proto().firstName())).build());
            flexPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);

            flexPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(inject(proto().middleName())).build());
            flexPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);

            flexPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(inject(proto().lastName())).build());
            flexPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);

            CTextFieldBase<?, ?> emailField = (CTextFieldBase<?, ?>) inject(proto().email());
            emailField.setNote(i18n.tr("Please note: your email will be your user name"));
            Widget widget = new LoginWidgetDecoratorBuilder(emailField).build();
            flexPanel.setWidget(++row, 0, widget);
            flexPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);

            flexPanel.setBR(++row, 0, 2);

            signUpSecurity = new Image(PortalImages.INSTANCE.signUpSecurity());
            flexPanel.setWidget(++row, 0, signUpSecurity);
            flexPanel.getFlexCellFormatter().setRowSpan(row, 0, 4);
            flexPanel.getFlexCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);

            flexPanel.setH4(row, 1, 1, i18n.tr("The Security Code is a secure identifier that is provided by your Property Manager specifically for you."));

            CTextFieldBase<?, ?> securityCodeField;
            flexPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(securityCodeField = (CTextFieldBase<?, ?>) inject(proto().securityCode()))
                    .componentWidth("180px").build());
            flexPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);

            securityCodeField
                    .setTooltip(i18n
                            .tr("You should have received Security Code by mail. Don't have a Security Code? To get your own unique access code, please contact the Property Manager directly."));

            passwordStrengthWidget = new PasswordStrengthWidget(passwordStrengthRule);
            flexPanel.setWidget(++row, 0,
                    new LoginWidgetDecoratorBuilder(inject(proto().password())).watermark(i18n.tr("Create a Password")).componentWidth("180px")
                            .assistantWidget(passwordStrengthWidget).build());
            flexPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);

            flexPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(inject(proto().passwordConfirm())).componentWidth("180px").build());
            flexPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);

            get(proto().passwordConfirm()).addComponentValidator(new AbstractComponentValidator<String>() {
                @Override
                public FieldValidationError isValid() {
                    String password = (get(proto().password())).getValue();
                    if ((password == null & getComponent().getValue() != null) | (password != null & getComponent().getValue() == null)
                            || (!password.equals(getComponent().getValue()))) {
                        return new FieldValidationError(getComponent(), i18n.tr("Passwords don't match"));
                    }
                    return null;
                }
            });
            get(proto().password()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().passwordConfirm())));

            for (String memberName : proto().getEntityMeta().getMemberNames()) {
                final IObject<?> member = proto().getMember(memberName);
                CComponent<?> boundMember = null;
                try {
                    boundMember = get(member);
                } catch (Throwable e) {
                    // just skip the unbound member
                }
                if (boundMember != null) {
                    boundMember.addComponentValidator(new AbstractComponentValidator() {
                        @Override
                        public FieldValidationError isValid() {
                            if (SignUpForm.this.entityValidationError != null) {
                                for (MemberValidationError memberValidationError : SignUpForm.this.entityValidationError.getErrors()) {
                                    if (memberValidationError.getMember().getPath().equals(member.getPath())) {
                                        return new FieldValidationError(getComponent(), memberValidationError.getMessage());
                                    }
                                }
                            }
                            return null;
                        }
                    });
                }
            }

            flexPanel.setBR(++row, 0, 2);

            return flexPanel;
        }

        public void init(List<SelfRegistrationBuildingDTO> buildings) {
            buildingSelector.setOptions(buildings);
            populateNew();
        }

        public void setEntityValidationError(EntityValidationException caught) {
            this.entityValidationError = caught;
            setVisitedRecursive();
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

}
