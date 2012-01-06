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

import com.google.gwt.dom.client.Style.BorderStyle;
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
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.c.CMoneyLabel;
import com.propertyvista.common.client.ui.components.c.NewPaymentMethodForm;
import com.propertyvista.common.client.ui.components.folders.ChargeLineFolder;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.domain.financial.Money;
import com.propertyvista.portal.ptapp.client.PtAppSite;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.rpc.ptapp.dto.PaymentInformationDTO;

public class PaymentViewForm extends CEntityDecoratableEditor<PaymentInformationDTO> {

    private static I18n i18n = I18n.get(PaymentViewForm.class);

    private PaymentViewImpl view;

    public static enum StyleSuffix implements IStyleName {
        PaymentImages, PaymentFee, PaymentForm
    }

    public static enum StyleDependent implements IStyleDependent {
        item, selected
    }

    private final CLabel termContent = new CLabel();

    public PaymentViewForm() {
        super(PaymentInformationDTO.class, new VistaEditorsComponentFactory());
    }

    public void setView(PaymentViewImpl view) {
        this.view = view;
    }

    private static boolean isRecurring = true;

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;

        main.setH1(++row, 0, 1, proto().applicationCharges().charges().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().applicationCharges().charges(), new ChargeLineFolder(isEditable())));
        main.setWidget(++row, 0, createTotal(proto().applicationCharges().total()));

        main.setBR(++row, 0, 1);
        HorizontalPanel info = new HorizontalPanel();
        info.getElement().getStyle().setMarginTop(1, Unit.EM);
        info.add(new Image(PortalImages.INSTANCE.userMessageInfo()));

        CLabel notes;
        inject(proto().oneTimePaymentTerms().content().content(), notes = new CLabel());
        notes.setAllowHtml(true);
        notes.asWidget().getElement().getStyle().setMarginLeft(1.5, Unit.EM);
        notes.asWidget().setWidth("auto");
        info.add(notes.asWidget());

        main.setWidget(++row, 0, info);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().depositAgree()), 5).build());
        main.setWidget(++row, 0, inject(proto().paymentMethod(), new NewPaymentMethodForm(true) {
            @Override
            public void onBillingAddressSameAsCurrentOne(boolean set) {
                assert (view != null);
                view.getPresenter().onBillingAddressSameAsCurrentOne(set);
            }
        }));

        if (isRecurring) {
            main.setH1(++row, 0, 1, i18n.tr("Pre-Authorized Payment"));

            inject(proto().recurrentPaymentTerms().content().content(), termContent);
            termContent.setAllowHtml(true);
            termContent.setWordWrap(true);

            ScrollPanel terms = new ScrollPanel(termContent.asWidget());
            terms.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
            terms.getElement().getStyle().setBorderWidth(1, Unit.PX);
            terms.getElement().getStyle().setBorderColor("#bbb");

            terms.getElement().getStyle().setBackgroundColor("white");
            terms.getElement().getStyle().setColor("black");

            terms.getElement().getStyle().setPaddingLeft(0.5, Unit.EM);
            terms.setHeight("20em");

            main.setWidget(++row, 0, terms);
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().preauthoriseAgree()), 5).build());
        }

        return main;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

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

    private Widget createTotal(Money member) {
        FlowPanel totalRow = new FlowPanel();

        Widget sp = new VistaLineSeparator(48, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        sp.getElement().getStyle().setProperty("border", "1px dotted black");
        totalRow.add(sp);

        HTML total = new HTML("<b>" + member.getMeta().getCaption() + "</b>");
        total.getElement().getStyle().setPaddingLeft(0.7, Unit.EM);
        totalRow.add(DecorationUtils.inline(total, "40.5em", null));
        totalRow.add(DecorationUtils.inline(inject(member, new CMoneyLabel()), "7em"));
        get(member).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);

        return totalRow;
    }
}
