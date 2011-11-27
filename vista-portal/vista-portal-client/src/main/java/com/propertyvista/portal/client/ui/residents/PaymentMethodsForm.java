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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.portal.domain.dto.PaymentMethodDTO;
import com.propertyvista.portal.domain.dto.PaymentMethodListDTO;

public class PaymentMethodsForm extends CEntityEditor<PaymentMethodListDTO> implements PaymentMethodsView {

    private static I18n i18n = I18n.get(PaymentMethodsForm.class);

    private PaymentMethodsView.Presenter presenter;

    public PaymentMethodsForm() {
        super(PaymentMethodListDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();
        container.getElement().getStyle().setMargin(20, Unit.PX);

        container.add(inject(proto().paymentMethods(), createPaymentMethodsViewer()));
        return container;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    private CEntityFolder<PaymentMethodDTO> createPaymentMethodsViewer() {

        return new VistaTableFolder<PaymentMethodDTO>(PaymentMethodDTO.class, i18n.tr("Payment Method"), true) {

            @Override
            public List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().number(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().expiryDate(), "14em"));
                columns.add(new EntityFolderColumnDescriptor(proto().nameOnAccount(), "16em"));
                return columns;
            }

            @Override
            protected void addItem() {
                presenter.addPaymentMethod();
            }
        };
    }

//    class TableFolderViewer<E extends IEntity> extends TableFolderDecorator<PaymentMethodDTO> {
//
//        private final FlowPanel content;
//
//        TableFolderViewer() {
//            super(new ArrayList<EntityFolderColumnDescriptor>());
//            content = new FlowPanel();
//            content.setWidth("100%");
//            content.add(new PortalHeaderBar(i18n.tr("Current Payment Methods"), "100%"));
//
//            HorizontalPanel header = new HorizontalPanel();
//            //header.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Header);
//            header.setWidth("100%");
//            formatHeader("Type", "15%", header);
//            formatHeader("Last 4 digits", "10%", header);
//            formatHeader("Billing Address", "50%", header);
//            formatHeader("Primary", "9%", header);
//            formatHeader("", "16%", header);
//            content.add(header);
//        }
//
//        @Override
//        public void setComponent(CEntityFolder viewer) {
//            content.add(viewer.getContainer());
//
//            String lbl = i18n.tr("Add New Payment Method");
//
//            Image addImage = new ImageButton(PortalImages.INSTANCE.addRow(), PortalImages.INSTANCE.addRowHover(), lbl);
//            addImage.getElement().getStyle().setFloat(Style.Float.LEFT);
//            addImage.addClickHandler(new ClickHandler() {
//
//                @Override
//                public void onClick(ClickEvent event) {
//                    presenter.addPaymentMethod();
//
//                }
//            });
//
//            FlowPanel imageHolder = new FlowPanel();
//            imageHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
//            imageHolder.getElement().getStyle().setPaddingLeft(addImage.getWidth(), Unit.PX);
//            imageHolder.getElement().getStyle().setMarginTop(2, Unit.EM);
//            imageHolder.add(addImage);
//
//            Label btnLabel = new Label(lbl);
//            btnLabel.getElement().getStyle().setPaddingLeft(3, Unit.PX);
//            btnLabel.getElement().getStyle().setFloat(Style.Float.LEFT);
//            imageHolder.add(btnLabel);
//
//            content.add(imageHolder);
//            setWidget(content);
//        }
//
//        private void formatHeader(String title, String width, CellPanel parent) {
//            Label item = new Label(i18n.tr(title));
//            parent.add(item);
//            parent.setCellWidth(item, width);
//        }
//    }
//
//    private CEntityFolderItemEditor<PaymentMethodDTO> createPaymenLineViewer() {
//
//        return new CEntityFolderBoxEditor<PaymentMethodDTO>(PaymentMethodDTO.class) {
//
//            @Override
//            public IFolderItemDecorator<PaymentMethodDTO> createDecorator() {
//                return new BoxReadOnlyFolderItemDecorator<PaymentMethodDTO>();
//            }
//
//            @Override
//            public IsWidget createContent() {
//                return createPaymentMethodLine();
//            }
//
//            private IsWidget createPaymentMethodLine() {
//
//                FlowPanel main = new FlowPanel();
//                main.add(DecorationUtils.inline(inject(proto().type()), "100px", null));
//                main.add(DecorationUtils.inline(inject(proto().cardNumber()), "100px", "right"));
//                main.add(DecorationUtils.inline(inject(proto().billingAddress().streetName()), "300px", "right"));
//                main.add(inject(proto().primary()));
//                //Edit link
//                CHyperlink link = new CHyperlink(null, new Command() {
//
//                    @Override
//                    public void execute() {
//                        //   presenter.editPaymentMethod(paymentMethod);
//                    }
//                });
//                link.setValue(i18n.tr("Edit"));
//                main.add(DecorationUtils.inline(link, "100px", "right"));
//
//                link = new CHyperlink(null, new Command() {
//                    @Override
//                    public void execute() {
//                        //   presenter.removePaymentMethod(paymentMethod);
//                    }
//                });
//                link.setValue(i18n.tr("Remove"));
//                main.add(DecorationUtils.inline(link, "100px", "right"));
//                return main;
//            }
//
//            private void formatValue(String value, String width, CellPanel parent) {
//                Label item = new Label(i18n.tr(value));
//                parent.add(item);
//                parent.setCellWidth(item, width);
//
//            }
//
//            private void formatValue(Image value, String width, CellPanel parent) {
//                if (value == null)
//                    return;
//                value.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
//                parent.add(value);
//                parent.setCellWidth(value, width);
//
//            }
//
//            private String formatAddress(IAddressFull address) {
//                if (address.isNull())
//                    return "";
//
//                StringBuffer addrString = new StringBuffer();
//
//                addrString.append(address.streetNumber().getStringView());
//                addrString.append(" ");
//                addrString.append(address.streetName().getStringView());
//
//                if (!address.city().isNull()) {
//                    addrString.append(", ");
//                    addrString.append(address.city().getStringView());
//                }
//
//                if (!address.province().isNull()) {
//                    addrString.append(" ");
//                    addrString.append(address.province().getStringView());
//                }
//
//                if (!address.postalCode().isNull()) {
//                    addrString.append(" ");
//                    addrString.append(address.postalCode().getStringView());
//                }
//
//                return addrString.toString();
//            }
//        };
//    }

}
