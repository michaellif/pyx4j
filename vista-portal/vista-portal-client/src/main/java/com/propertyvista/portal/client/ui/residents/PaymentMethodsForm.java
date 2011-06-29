/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.viewer.BaseFolderItemViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderItemViewer;
import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderViewer;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderItemViewerDecorator;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderViewerDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.widgets.client.ImageButton;

import com.propertyvista.common.domain.IAddressFull;
import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.client.ui.decorations.PortalHeaderDecorator;
import com.propertyvista.portal.domain.dto.PaymentMethodDTO;
import com.propertyvista.portal.domain.dto.PaymentMethodListDTO;

public class PaymentMethodsForm extends CEntityForm<PaymentMethodListDTO> implements PaymentMethodsView {

    private static I18n i18n = I18nFactory.getI18n(PaymentMethodsForm.class);

    private PaymentMethodsView.Presenter presenter;

    public PaymentMethodsForm() {
        super(PaymentMethodListDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();

        container.add(inject(proto().paymentMethods(), createPaymentMethodsViewer()));

        return container;
    }

    @Override
    public void populate(PaymentMethodListDTO paymentMethods) {
        super.populate(paymentMethods);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    class TableFolderViewer<E extends IEntity> extends SimplePanel implements IFolderViewerDecorator<E> {

        private final FlowPanel content;

        TableFolderViewer() {
            content = new FlowPanel();
            content.setWidth("100%");
            content.add(new PortalHeaderDecorator(i18n.tr("Current Payment Methods"), "100%"));

            HorizontalPanel header = new HorizontalPanel();
            header.setWidth("100%");
            Label item = new Label(i18n.tr("Type"));
            header.add(item);
            header.setCellWidth(item, "15%");
            header.setCellHorizontalAlignment(item, HasHorizontalAlignment.ALIGN_CENTER);
            item = new Label(i18n.tr("Billing Address"));
            header.add(item);
            header.setCellHorizontalAlignment(item, HasHorizontalAlignment.ALIGN_CENTER);
            header.setCellWidth(item, "60%");

            item = new Label(i18n.tr("Primary"));
            header.setCellHorizontalAlignment(item, HasHorizontalAlignment.ALIGN_CENTER);
            header.add(item);
            header.setCellWidth(item, "9%");

            item = new Label("");
            header.add(item);
            header.setCellWidth(item, "16%");
            content.add(header);
        }

        @Override
        public void setFolder(CEntityFolderViewer<?> viewer) {
            content.add(viewer.getContainer());

            String lbl = i18n.tr("Add New Payment Method");
            ;
            Image addImage = new ImageButton(PortalImages.INSTANCE.addRow(), PortalImages.INSTANCE.addRowHover(), lbl);
            addImage.getElement().getStyle().setFloat(Style.Float.LEFT);
            addImage.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    presenter.addPaymentMethod();

                }
            });

            FlowPanel imageHolder = new FlowPanel();
            imageHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            imageHolder.getElement().getStyle().setPaddingLeft(addImage.getWidth(), Unit.PX);
            imageHolder.getElement().getStyle().setMarginTop(2, Unit.EM);
            imageHolder.add(addImage);

            Label btnLabel = new Label(lbl);
            btnLabel.getElement().getStyle().setPaddingLeft(3, Unit.PX);
            btnLabel.getElement().getStyle().setFloat(Style.Float.LEFT);
            imageHolder.add(btnLabel);

            content.add(imageHolder);

            setWidget(content);

        }
    }

    //TODO finish implementation
    private CEntityFolderViewer<PaymentMethodDTO> createPaymentMethodsViewer() {

        return new CEntityFolderViewer<PaymentMethodDTO>(PaymentMethodDTO.class) {

            @Override
            protected IFolderViewerDecorator<PaymentMethodDTO> createFolderDecorator() {
                return new TableFolderViewer<PaymentMethodDTO>();
            }

            @Override
            protected CEntityFolderItemViewer<PaymentMethodDTO> createItem() {
                return createPaymenLineViewer();
            }

        };
    }

    private CEntityFolderItemViewer<PaymentMethodDTO> createPaymenLineViewer() {

        return new CEntityFolderItemViewer<PaymentMethodDTO>() {

            @Override
            public IFolderItemViewerDecorator<PaymentMethodDTO> createFolderItemDecorator() {
                return new BaseFolderItemViewerDecorator<PaymentMethodDTO>() {

                };
            }

            @Override
            public IsWidget createContent(PaymentMethodDTO value) {
                return createPaymentMethodLine(value);
            }

        };
    }

    private IsWidget createPaymentMethodLine(final PaymentMethodDTO paymentMethod) {
        HorizontalPanel container = new HorizontalPanel();
        container.setWidth("100%");
        Label item = new Label(paymentMethod.type().getStringView());
        container.add(item);
        container.setCellWidth(item, "15%");
        item = new Label(formatAddress(paymentMethod.billingAddress()));
        container.add(item);
        container.setCellWidth(item, "60%");

        item = new Label(paymentMethod.primary().getStringView());
        container.add(item);
        container.setCellWidth(item, "9%");

        //Edit link
        CHyperlink link = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.editPaymentMethod(paymentMethod);
            }
        });
        link.setValue(i18n.tr("Edit"));
        container.add(link);
        container.setCellWidth(link, "8%");

        link = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.removePaymentMethod(paymentMethod);
            }
        });
        link.setValue(i18n.tr("Remove"));
        container.add(link);
        container.setCellWidth(link, "8%");

        return container;
    }

    private String formatAddress(IAddressFull address) {
        if (address.isNull())
            return "";

        StringBuffer addrString = new StringBuffer();

        addrString.append(address.streetNumber().getStringView());
        addrString.append(" ");
        addrString.append(address.streetName().getStringView());

        if (!address.city().isNull()) {
            addrString.append(", ");
            addrString.append(address.city().getStringView());
        }

        if (!address.province().isNull()) {
            addrString.append(" ");
            addrString.append(address.province().getStringView());
        }

        if (!address.postalCode().isNull()) {
            addrString.append(" ");
            addrString.append(address.postalCode().getStringView());
        }

        return addrString.toString();
    }

}
