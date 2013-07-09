/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.payment;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.common.client.ui.components.folders.ChargeLineFolder;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.ptapp.client.PtAppSite;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.rpc.ptapp.dto.PaymentInformationDTO;

public class PaymentViewForm extends CEntityDecoratableForm<PaymentInformationDTO> {

    private static final I18n i18n = I18n.get(PaymentViewForm.class);

    public static String DEFAULT_STYLE_PREFIX = "PaymentViewForm";

    public static enum StyleSuffix implements IStyleName {
        oneTimePaymentTerms, recurrentPaymentTerms
    }

    private PaymentViewImpl view;

    public static enum StyleDependent implements IStyleDependent {
        item, selected
    }

    private final CLabel<String> termContent = new CLabel<String>();

    public PaymentViewForm() {
        super(PaymentInformationDTO.class, new VistaEditorsComponentFactory());
    }

    public void setView(PaymentViewImpl view) {
        this.view = view;
    }

    private static boolean isRecurring = false;

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;

        main.setH1(++row, 0, 1, proto().applicationCharges().charges().getMeta().getCaption());

        ChargeLineFolder folder = new ChargeLineFolder();
        folder.setViewable(true);
        main.setWidget(++row, 0, inject(proto().applicationCharges().charges(), folder));
        main.setWidget(++row, 0, createTotal(proto().applicationCharges().total()));

        main.setBR(++row, 0, 1);
        HorizontalPanel info = new HorizontalPanel();
        info.getElement().getStyle().setMarginTop(1, Unit.EM);
        info.add(new Image(PortalImages.INSTANCE.userMessageInfo()));

        CLabel<String> notes;
        inject(proto().oneTimePaymentTerms().content().content(), notes = new CLabel<String>());
        notes.asWidget().setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.oneTimePaymentTerms);
        notes.asWidget().getElement().getStyle().setMarginLeft(1.5, Unit.EM);
        notes.asWidget().setWidth("auto");
        info.add(notes.asWidget());

        main.setWidget(++row, 0, info);
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().depositAgree()), 5).build());
        main.setWidget(++row, 0, inject(proto().paymentMethod(), new PaymentMethodForm<LeasePaymentMethod>(LeasePaymentMethod.class, true) {
            @Override
            public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressStructured> comp) {
                if (set) {
                    assert (view != null);
                    view.getPresenter().getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
                        @Override
                        public void onSuccess(AddressStructured result) {
                            comp.setValue(result, false);
                        }
                    });
                } else {
                    comp.setValue(EntityFactory.create(AddressStructured.class), false);
                }
            }
        }));

        if (isRecurring) {
            main.setH1(++row, 0, 1, i18n.tr("Pre-Authorized Payment"));

            inject(proto().recurrentPaymentTerms().content().content(), termContent);

            ScrollPanel termsScroll = new ScrollPanel(termContent.asWidget());
            termsScroll.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.recurrentPaymentTerms);

            main.setWidget(++row, 0, termsScroll);
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().preauthoriseAgree()), 5).build());
        }

        // tune up:
        get(proto().depositAgree()).setMandatory(false);

        return main;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isRecurring) {
            // prepare term text:
            String termText = termContent.getValue();
            termText = termText.replace("$[PMC]", PtAppSite.getPmcName());
            termText = termText.replace("$[USER]", ClientContext.getUserVisit().getName());
            termText = termText.replace("$[AMOUNT]", getValue().applicationCharges().total().getStringView());
            termText = termText.replace("$[DATE]", "1st of January");
            termContent.setValue(termText);
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().depositAgree()).addValueValidator(new EditableValueValidator<Boolean>() {
            @Override
            public ValidationError isValid(CComponent<Boolean> component, Boolean value) {
                if (value == null) {
                    return null;
                }
                return value ? null : new ValidationError(component, i18n.tr("You should agree on the terms"));
            }
        });

    }

    private Widget createTotal(IPrimitive<BigDecimal> member) {
        FlowPanel totalRow = new FlowPanel();
        HTML total = new HTML("<b>" + member.getMeta().getCaption() + "</b>");
        total.getElement().getStyle().setPaddingLeft(0.7, Unit.EM);
        totalRow.add(DecorationUtils.inline(total, "40.5em", null));
        totalRow.add(DecorationUtils.inline(inject(member), "6em"));

        get(member).setViewable(true);
        get(member).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);

        FormFlexPanel main = new FormFlexPanel();
        main.setHR(0, 0, 1);
        main.setWidget(1, 0, totalRow);
        main.setWidth("48em");
        return main;
    }
}
