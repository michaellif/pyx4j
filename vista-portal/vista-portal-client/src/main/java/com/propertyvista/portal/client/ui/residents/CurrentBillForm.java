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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityViewer;
import com.pyx4j.entity.client.ui.flex.folder.BoxReadOnlyFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.BoxReadOnlyFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderBoxEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CHyperlink;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.financial.Money;
import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.client.ui.util.Utils;
import com.propertyvista.portal.domain.dto.BillDTO;
import com.propertyvista.portal.domain.dto.PaymentMethodDTO;

public class CurrentBillForm extends CEntityEditor<BillDTO> implements CurrentBillView {

    private static I18n i18n = I18nFactory.getI18n(CurrentBillForm.class);

    private Presenter presenter;

    public CurrentBillForm() {
        super(BillDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();
        container.add(inject(proto().paymentMethod(), new PaymentMethodViewer()));
        container.add(inject(proto().preAuthorized(), new PreauthorizedOutcomeViewer()));
        container.add(new VistaWidgetDecorator(inject(proto().dueDate())));
        container.add(inject(proto().charges(), new ChargeLineFolder()));
        container.add(inject(proto().total(), new TotalLineViewer()));
        return container;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    class PaymentMethodViewer extends CEntityViewer<PaymentMethodDTO> {

        public PaymentMethodViewer() {
            super();
        }

        @Override
        public IsWidget createContent(PaymentMethodDTO paymentMethod) {
            FlowPanel container = new FlowPanel();
            container.setWidth("100%");
            container.add(DecorationUtils.inline(new Label(i18n.tr("Your Payment Method")), "14em"));
            Image paymentImage = Utils.getPaymentCardImage(paymentMethod.type().getValue());
            paymentImage.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            paymentImage.getElement().getStyle().setMarginRight(5d, Unit.PX);
            container.add(DecorationUtils.inline(paymentImage));
            container.add(DecorationUtils.inline(new Label(paymentMethod.cardNumber().getStringView())));
            CHyperlink changePayment = new CHyperlink(null, new Command() {
                @Override
                public void execute() {
                    presenter.changePaymentMethod();
                }
            });
            changePayment.setValue(i18n.tr("Change"));
            changePayment.asWidget().getElement().getStyle().setMarginLeft(1d, Unit.EM);
            container.add(DecorationUtils.inline(changePayment));
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
                info.getElement().getStyle().setMarginTop(1, Unit.EM);
                info.add(new Image(PortalImages.INSTANCE.warningSide()));
                info.add(new Image(PortalImages.INSTANCE.warning()));
                FlowPanel msg = new FlowPanel();
                msg.add(new HTML(PortalImages.INSTANCE.paymentPreauthorisedNotes().getText()));
                CheckBox cb = new CheckBox(i18n.tr("YES, I WANT TO ENROLL INTO PRE-AUTHORIZED PAYMENT PLAN AND SAVE!"));
                cb.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.changeAuthorization(true);

                    }
                });
                msg.add(cb);
                info.add(msg);
                info.getElement().getStyle().setMarginBottom(1, Unit.EM);
                container.add(info);

                Button payBtn = new Button(i18n.tr("Pay Now"));
                payBtn.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.payBill();

                    }
                });
                payBtn.getElement().getStyle().setMarginBottom(1d, Unit.EM);
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
            container.getElement().getStyle().setProperty("borderTop", "1px dotted");
            return container;
        }

    }

    private IsWidget formatBillLine(String label, String value) {
        HorizontalPanel container = new HorizontalPanel();
        container.setWidth("100%");
        Label lbl = new Label(label);
        container.add(lbl);
        container.setCellWidth(lbl, "50%");
        container.setCellHorizontalAlignment(lbl, HasHorizontalAlignment.ALIGN_LEFT);
        lbl = new Label(value);
        container.add(lbl);
        container.setCellWidth(lbl, "50%");
        container.setCellHorizontalAlignment(lbl, HasHorizontalAlignment.ALIGN_RIGHT);
        container.setWidth("100%");
        return container;
    }

    class ChargeLineFolder extends CEntityFolder<ChargeLine> {

        public ChargeLineFolder() {
            super(ChargeLine.class);
        }

        @Override
        protected IFolderDecorator<ChargeLine> createDecorator() {
            return new BoxReadOnlyFolderDecorator<ChargeLine>() {

                @Override
                public void setComponent(CEntityFolder w) {
                    super.setComponent(w);
                    this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                }
            };
        }

        @Override
        protected CEntityFolderBoxEditor<ChargeLine> createItem() {

            return new CEntityFolderBoxEditor<ChargeLine>(ChargeLine.class) {

                @Override
                public IFolderItemDecorator<ChargeLine> createDecorator() {
                    return new BoxReadOnlyFolderItemDecorator<ChargeLine>();
                }

                @Override
                public IsWidget createContent() {
                    FlowPanel main = new FlowPanel();
                    main.add(DecorationUtils.inline(inject(proto().type()), "300px", null));
                    main.add(DecorationUtils.inline(inject(proto().label()), "100px", "right"));
                    main.add(DecorationUtils.inline(inject(proto().charge()), "100px", "right"));
                    return main;
                }
            };
        }

    }
}
