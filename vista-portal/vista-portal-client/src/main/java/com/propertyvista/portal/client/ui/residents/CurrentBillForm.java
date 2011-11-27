/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.CEntityViewer;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.ChargeLineFolder;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.domain.financial.Money;
import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.client.ui.util.Utils;
import com.propertyvista.portal.domain.dto.BillDTO;
import com.propertyvista.portal.domain.dto.PaymentMethodDTO;

public class CurrentBillForm extends CEntityEditor<BillDTO> implements CurrentBillView {

    private static final I18n i18n = I18n.get(CurrentBillForm.class);

    private Presenter presenter;

    public CurrentBillForm() {
        super(BillDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel container = new FormFlexPanel();

        int row = -1;

        container.setWidget(++row, 0, inject(proto().charges(), new ChargeLineFolder(isEditable())));
        container.setHR(++row, 0, 1);
        container.setWidget(++row, 0, inject(proto().total(), new TotalLineViewer()));
        container.setWidget(++row, 0, inject(proto().dueDate(), new DueDateViewer()));
        container.setWidget(++row, 0, inject(proto().paymentMethod(), new PaymentMethodViewer()));
        container.setWidget(++row, 0, inject(proto().preAuthorized(), new PreauthorizedOutcomeViewer()));
        return container;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    class DueDateViewer extends CViewer<LogicalDate> {

        public DueDateViewer() {
            super();
        }

        @Override
        public void populate(LogicalDate value) {
            super.populate(value);
        }

        @Override
        public IsWidget createContent(LogicalDate value) {
            if (value != null) {
                FlowPanel container = new FlowPanel();
                container.setWidth("100%");
                container.add(DecorationUtils.inline(new Label(i18n.tr("Due Date")), "14em"));
                container.add(DecorationUtils.inline(new Label(value.toString())));

                container.asWidget().getElement().getStyle().setMarginLeft(20, Unit.PX);
                container.asWidget().getElement().getStyle().setMarginTop(20, Unit.PX);
                return container;
            } else {
                return null;
            }
        }

    }

    class PaymentMethodViewer extends CEntityViewer<PaymentMethodDTO> {

        public PaymentMethodViewer() {
            super();
        }

        @Override
        public IsWidget createContent(PaymentMethodDTO paymentMethod) {
            FlowPanel container = new FlowPanel();
            container.setWidth("100%");
            container.add(DecorationUtils.inline(new Label(i18n.tr("Payment Method")), "14em"));
            Image paymentImage = Utils.getPaymentCardImage(paymentMethod.type().getValue());
            paymentImage.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            paymentImage.getElement().getStyle().setMarginRight(5d, Unit.PX);
            container.add(DecorationUtils.inline(paymentImage));
            container.add(DecorationUtils.inline(new Label(paymentMethod.number().getStringView())));
            CHyperlink changePayment = new CHyperlink(null, new Command() {
                @Override
                public void execute() {
                    presenter.changePaymentMethod();
                }
            });
            changePayment.setValue(i18n.tr("Change"));
            changePayment.asWidget().getElement().getStyle().setMarginLeft(1d, Unit.EM);
            container.add(DecorationUtils.inline(changePayment));

            container.asWidget().getElement().getStyle().setMarginLeft(20, Unit.PX);

            return container;
        }

    }

    class PreauthorizedOutcomeViewer extends CEntityViewer<IEntity> {

        @Override
        public IsWidget createContent(IEntity isPreauthorized) {
            FlowPanel container = new FlowPanel();
            container.setWidth("100%");
            // if (isPreauthorized != null && !isPreauthorized.isNull() && isPreauthorized.isBooleanTrue()) {//pre-authorized
            if (isPreauthorized != null && !isPreauthorized.isNull()) {//pre-authorized
                container.add(DecorationUtils.inline(new Label(i18n.tr("Your Current Arrangement")), "14em"));
                container.add(DecorationUtils.inline(new Label(i18n.tr("Pre-Authorized Monthly Payment Plan"))));

                CHyperlink cancelPayment = new CHyperlink(null, new Command() {
                    @Override
                    public void execute() {
                        presenter.changeAuthorization(false);
                    }
                });
                cancelPayment.setValue(i18n.tr("Cancel"));
                cancelPayment.asWidget().getElement().getStyle().setMarginLeft(1d, Unit.EM);
                container.add(DecorationUtils.inline(cancelPayment));

            } else {//notification
                HorizontalPanel info = new HorizontalPanel();
                info.asWidget().getElement().getStyle().setMarginLeft(20, Unit.PX);

                info.getElement().getStyle().setMarginTop(1, Unit.EM);
                Image image;
                info.add(image = new Image(PortalImages.INSTANCE.warningSide()));
                image.getElement().getStyle().setMarginRight(5, Unit.PX);

                info.add(image = new Image(PortalImages.INSTANCE.warning()));
                image.getElement().getStyle().setMarginRight(5, Unit.PX);

                FlowPanel msg = new FlowPanel();
                msg.add(new HTML(PortalImages.INSTANCE.paymentPreauthorisedNotes().getText()));
                msg.getElement().getStyle().setMarginRight(5, Unit.PX);
                CheckBox cb = new CheckBox(i18n.tr("Yes! I Want To Take Advantage Of The Pre-Authorization."));
                cb.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.changeAuthorization(true);

                    }
                });
                cb.setValue(true);
                msg.add(cb);
                info.add(msg);
                info.getElement().getStyle().setMarginBottom(1, Unit.EM);
                container.add(info);

                Button payBtn = new Button(i18n.tr("Pay Now"));
                payBtn.getElement().getStyle().setMargin(20, Unit.PX);
                payBtn.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);

                payBtn.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.payBill();

                    }
                });
                container.add(payBtn);
            }

            return container;
        }
    }

    class TotalLineViewer extends CEntityViewer<Money> {

        @Override
        public IsWidget createContent(Money money) {
            HorizontalPanel container = (HorizontalPanel) formatBillLine(money.getMeta().getCaption(), money.getStringView());
            container.getElement().getStyle().setFontSize(1.3d, Unit.EM);
            container.getElement().getStyle().setPaddingTop(10d, Unit.PX);
            return container;
        }

    }

    private IsWidget formatBillLine(String label, String value) {
        HorizontalPanel container = new HorizontalPanel();
        Label lbl = new Label(label);
        container.add(lbl);
        lbl.setWidth("530px");
        lbl.getElement().getStyle().setMarginLeft(6, Unit.PX);
        container.setCellHorizontalAlignment(lbl, HasHorizontalAlignment.ALIGN_LEFT);
        lbl = new Label(value);
        container.add(lbl);
        lbl.setWidth("100px");
        container.setCellHorizontalAlignment(lbl, HasHorizontalAlignment.ALIGN_LEFT);
        return container;
    }

}
