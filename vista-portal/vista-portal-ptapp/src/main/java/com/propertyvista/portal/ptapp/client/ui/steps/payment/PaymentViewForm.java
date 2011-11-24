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

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.c.CMoneyLabel;
import com.propertyvista.common.client.ui.components.c.NewPaymentMethodForm;
import com.propertyvista.common.client.ui.components.folders.ChargeLineFolder;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.domain.financial.Money;
import com.propertyvista.portal.domain.ptapp.PaymentInformation;
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

        main.setH1(++row, 0, 1, i18n.tr(proto().applicationCharges().getMeta().getCaption()));
        main.setWidget(++row, 0, inject(proto().applicationCharges().charges(), new ChargeLineFolder(isEditable())));
        main.setWidget(++row, 0, createTotal(proto().applicationCharges().total()));

        main.setBR(++row, 0, 1);
        HorizontalPanel info = new HorizontalPanel();
        info.getElement().getStyle().setMarginTop(1, Unit.EM);
        info.add(new Image(PortalImages.INSTANCE.userMessageInfo()));
        info.add(new HTML(PortalResources.INSTANCE.paymentApprovalNotes().getText()));
        info.getElement().getStyle().setMarginBottom(1, Unit.EM);
        main.setWidget(++row, 0, info);

        main.setWidget(++row, 0, inject(proto().paymentMethod(), new NewPaymentMethodForm(true) {
            @Override
            public void onBillingAddressSameAsCurrentOne(boolean set) {
                assert (view != null);
                view.getPresenter().onBillingAddressSameAsCurrentOne(set);
            }
        }));

        main.setH1(++row, 0, 1, i18n.tr("Pre-Authorized Payment"));

//        HorizontalPanel preauthorisedNotes = new HorizontalPanel();
//        preauthorisedNotes.add(new HTML(PortalResources.INSTANCE.paymentPreauthorisedNotes_VISA().getText()));
//        main.setWidget(++row, 0, preauthorisedNotes);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().preauthorised()), 5).build());
        main.setWidget(++row, 0, new HTML(PortalResources.INSTANCE.paymentTermsNotes().getText()));

        return main;
    }

    @Override
    public void populate(PaymentInformation value) {
        super.populate(value);
    }

    private Widget createTotal(Money member) {
        FlowPanel totalRow = new FlowPanel();

        Widget sp = new VistaLineSeparator(38, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        sp.getElement().getStyle().setProperty("border", "1px dotted black");
        totalRow.add(sp);

        HTML total = new HTML("<b>" + member.getMeta().getCaption() + "</b>");
        total.getElement().getStyle().setPaddingLeft(0.7, Unit.EM);
        totalRow.add(DecorationUtils.inline(total, "30.5em", null));
        totalRow.add(DecorationUtils.inline(inject(member, new CMoneyLabel()), "7em"));
        get(member).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);

        return totalRow;
    }
}
