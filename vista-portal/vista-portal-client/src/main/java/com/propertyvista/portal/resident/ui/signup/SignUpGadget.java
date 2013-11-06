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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.resident.resources.PortalImages;
import com.propertyvista.portal.resident.ui.AbstractGadget;
import com.propertyvista.portal.resident.ui.signup.SignUpView.SignUpPresenter;
import com.propertyvista.portal.resident.ui.util.decorators.LoginWidgetDecoratorBuilder;
import com.propertyvista.portal.rpc.portal.web.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.web.dto.SelfRegistrationDTO;
import com.propertyvista.portal.rpc.shared.EntityValidationException;
import com.propertyvista.portal.rpc.shared.EntityValidationException.MemberValidationError;

public class SignUpGadget extends AbstractGadget<SignUpViewImpl> {

    static final I18n i18n = I18n.get(SignUpGadget.class);

    private SignUpPresenter presenter;

    private final SignUpForm signupform;

    private final Anchor termsAndConditionsAnchor;

    SignUpGadget(SignUpViewImpl view) {
        super(view, null, i18n.tr("Create Your Account"), ThemeColor.contrast2, 1);
        setActionsToolbar(new SignUpToolbar());

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        signupform = new SignUpForm();
        signupform.initContent();
        contentPanel.add(signupform);

        FlowPanel loginTermsLinkPanel = new FlowPanel();
        loginTermsLinkPanel.getElement().getStyle().setTextAlign(TextAlign.LEFT);

        HTML termsPrefix = new HTML(i18n.tr("By clicking REGISTER, you are acknowledging that you have read and agree to our "));
        termsPrefix.getElement().getStyle().setDisplay(Display.INLINE);
        loginTermsLinkPanel.add(termsPrefix);

        termsAndConditionsAnchor = new Anchor(i18n.tr("RESIDENT PORTAL TERMS AND CONDITIONS"));
        termsAndConditionsAnchor.getElement().getStyle().setDisplay(Display.INLINE);
        termsAndConditionsAnchor.getElement().getStyle().setPadding(0, Unit.PX);
        termsAndConditionsAnchor.getElement().getStyle().setWhiteSpace(WhiteSpace.NORMAL);
        termsAndConditionsAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showVistaTerms();
                DOM.eventPreventDefault((com.google.gwt.user.client.Event) event.getNativeEvent());
            }
        });
        loginTermsLinkPanel.add(termsAndConditionsAnchor);

        HTML suffixPrefix = new HTML(".");
        suffixPrefix.getElement().getStyle().setDisplay(Display.INLINE);
        loginTermsLinkPanel.add(suffixPrefix);
        loginTermsLinkPanel.getElement().getStyle().setProperty("maxWidth", 500, Unit.PX);
        loginTermsLinkPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        contentPanel.add(loginTermsLinkPanel);

        setContent(contentPanel);
    }

    public void setPresenter(SignUpPresenter presenter) {
        this.presenter = presenter;
        this.termsAndConditionsAnchor.setHref(AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), true, presenter.getPortalTermsPlace()));
    }

    public void showValidationError(EntityValidationException caught) {
        signupform.setEntityValidationError(caught);
    }

    public void setBuildingOptions(List<SelfRegistrationBuildingDTO> buildings) {
        signupform.setBuildingOptions(buildings);
        signupform.setUnconditionalValidationErrorRendering(false);
        signupform.setVisited(false);
        signupform.populateNew();
    }

    public void doLayout(LayoutType layoutType) {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            signupform.signUpBuildingImage.setVisible(false);
            signupform.signUpPersonalImage.setVisible(false);
            signupform.signUpSecurity.setVisible(false);
            break;

        default:
            signupform.signUpBuildingImage.setVisible(true);
            signupform.signUpPersonalImage.setVisible(true);
            signupform.signUpSecurity.setVisible(true);
            break;
        }
    }

    class SignUpToolbar extends Toolbar {

        private final Button signUpButton;

        public SignUpToolbar() {

            signUpButton = new Button(i18n.tr("REGISTER"), new Command() {
                @Override
                public void execute() {
                    signupform.setEntityValidationError(null);
                    signupform.revalidate();
                    signupform.setUnconditionalValidationErrorRendering(true);
                    if (signupform.isValid()) {
                        presenter.register(signupform.getValue());
                    }
                }
            });
            signUpButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));
            add(signUpButton);

        }
    }

    class SignUpForm extends CEntityForm<SelfRegistrationDTO> {

        private BuildingSuggestBox buildingSelector;

        private EntityValidationException entityValidationError;

        private Image signUpBuildingImage;

        private Image signUpPersonalImage;

        private Image signUpSecurity;

        public SignUpForm() {
            super(SelfRegistrationDTO.class);
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
            flexPanel.setWidget(++row, 0,
                    new LoginWidgetDecoratorBuilder(securityCodeField = (CTextFieldBase<?, ?>) inject(proto().securityCode())).componentWidth("90%").build());
            flexPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);

            securityCodeField
                    .setTooltip(i18n
                            .tr("You should have received Security Code by mail. Don't have a Security Code? To get your own unique access code, please contact the Property Manager directly."));

            flexPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(inject(proto().password())).watermark(i18n.tr("Create a Password")).build());
            flexPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);

            flexPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(inject(proto().passwordConfirm())).build());
            flexPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);

            get(proto().passwordConfirm()).addValueValidator(new EditableValueValidator<String>() {
                @Override
                public ValidationError isValid(CComponent<String> component, String confirmPassword) {
                    String password = (get(proto().password())).getValue();
                    if ((password == null & confirmPassword != null) | (password != null & confirmPassword == null) || (!password.equals(confirmPassword))) {
                        return new ValidationError(component, i18n.tr("Passwords don't match"));
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
                    boundMember.addValueValidator(new EditableValueValidator() {
                        @Override
                        public ValidationError isValid(CComponent component, Object value) {
                            if (SignUpForm.this.entityValidationError != null) {
                                for (MemberValidationError memberValidationError : SignUpForm.this.entityValidationError.getErrors()) {
                                    if (memberValidationError.getMember().getPath().equals(member.getPath())) {
                                        return new ValidationError(component, memberValidationError.getMessage());
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

        public void setBuildingOptions(List<SelfRegistrationBuildingDTO> buildings) {
            buildingSelector.setOptions(buildings);
        }

        public void setEntityValidationError(EntityValidationException caught) {
            this.entityValidationError = caught;
            setUnconditionalValidationErrorRendering(true);
            revalidate();
        }

    }

}
