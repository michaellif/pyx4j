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
import com.propertyvista.portal.domain.ptapp.PaymentInformation;
import com.propertyvista.portal.ptapp.client.PtAppSite;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.resources.PortalResources;

public class PaymentViewForm extends CEntityDecoratableEditor<PaymentInformation> {

    private static I18n i18n = I18n.get(PaymentViewForm.class);

    private PaymentViewImpl view;

    public static enum StyleSuffix implements IStyleName {
        PaymentImages, PaymentFee, PaymentForm
    }

    public static enum StyleDependent implements IStyleDependent {
        item, selected
    }

    public PaymentViewForm() {
        super(PaymentInformation.class, new VistaEditorsComponentFactory());
    }

    public void setView(PaymentViewImpl view) {
        this.view = view;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;

        main.setH1(++row, 0, 1, i18n.tr("Charges"));
        main.setWidget(++row, 0, inject(proto().applicationCharges().charges(), new ChargeLineFolder(isEditable())));
        main.setWidget(++row, 0, createTotal(proto().applicationCharges().total()));

        main.setBR(++row, 0, 1);
        HTML notes;
        HorizontalPanel info = new HorizontalPanel();
        info.getElement().getStyle().setMarginTop(1, Unit.EM);
        info.add(new Image(PortalImages.INSTANCE.userMessageInfo()));
        info.add(notes = new HTML(PortalResources.INSTANCE.paymentApprovalNotes().getText()));
        notes.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        notes.getElement().getStyle().setMarginLeft(2, Unit.EM);
        notes.getElement().getStyle().setMarginRight(2, Unit.EM);
        main.setWidget(++row, 0, info);

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().depositAgree()), 5).build());

        main.setWidget(++row, 0, inject(proto().paymentMethod(), new NewPaymentMethodForm(true) {
            @Override
            public void onBillingAddressSameAsCurrentOne(boolean set) {
                assert (view != null);
                view.getPresenter().onBillingAddressSameAsCurrentOne(set);
            }
        }));

        main.setH1(++row, 0, 1, i18n.tr("Pre-Authorized Payment"));

        // prepare text:
        String termText = PortalResources.INSTANCE.paymentTermsNotes().getText();
        termText = termText.replace("$[PMC]", PtAppSite.getPmcName());
        termText = termText.replace("$[USER]", ClientContext.getUserVisit().getName());
        termText = termText.replace("$[AMOUNT]", "$1346.78");
        termText = termText.replace("$[DATE]", "1st of January");

        CLabel termContent = new CLabel();
        termContent.setAllowHtml(true);
        termContent.setWordWrap(true);
        termContent.setValue(termText);

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

        return main;
    }

    @Override
    public void populate(PaymentInformation value) {
        super.populate(value);
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
