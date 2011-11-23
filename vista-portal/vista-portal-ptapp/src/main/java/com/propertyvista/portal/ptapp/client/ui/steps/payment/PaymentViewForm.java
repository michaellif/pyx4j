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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.NewPaymentMethodForm;
import com.propertyvista.common.client.ui.components.folders.ChargeLineFolder;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.portal.domain.ptapp.PaymentInformation;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.resources.PortalResources;

public class PaymentViewForm extends CEntityEditor<PaymentInformation> {

    private static I18n i18n = I18n.get(PaymentViewForm.class);

    public static enum StyleSuffix implements IStyleName {
        PaymentImages, PaymentFee, PaymentForm
    }

    public static enum StyleDependent implements IStyleDependent {
        item, selected
    }

    public PaymentViewForm() {
        super(PaymentInformation.class, new VistaEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;

        main.setH1(++row, 0, 1, i18n.tr(proto().applicationCharges().getMeta().getCaption()));
        main.setWidget(++row, 0, inject(proto().applicationCharges().charges(), new ChargeLineFolder(isEditable())));

        FlowPanel applicationFeePanel = new FlowPanel();
        applicationFeePanel.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        applicationFeePanel.add(DecorationUtils.inline(inject(proto().applicationFee().label()), "300px"));
        applicationFeePanel.add(DecorationUtils.inline(inject(proto().applicationFee().amount()), "100px", "right"));
        main.setWidget(++row, 0, applicationFeePanel);

        HorizontalPanel info = new HorizontalPanel();
        info.getElement().getStyle().setMarginTop(1, Unit.EM);
        info.add(new Image(PortalImages.INSTANCE.userMessageInfo()));
        info.add(new HTML(PortalResources.INSTANCE.paymentApprovalNotes().getText()));
        info.getElement().getStyle().setMarginBottom(1, Unit.EM);
        main.setWidget(++row, 0, info);

        main.setWidget(++row, 0, inject(proto().paymentMethod(), new NewPaymentMethodForm()));

        main.setH1(++row, 0, 1, i18n.tr("Pre-Authorized Payment"));

        HorizontalPanel preauthorisedNotes = new HorizontalPanel();
        preauthorisedNotes.add(new HTML(PortalResources.INSTANCE.paymentPreauthorisedNotes_VISA().getText()));
        main.setWidget(++row, 0, preauthorisedNotes);

        main.setWidget(++row, 0, inject(proto().preauthorised()));

        main.setWidget(++row, 0, new HTML(PortalResources.INSTANCE.paymentTermsNotes().getText()));

        return main;
    }

//    private void setAsCurrentAddress(Boolean value) {
//        boolean editable = true;
//        CAddressStructured addressForm = (CAddressStructured) get(proto().billingAddress());
//        if (value == Boolean.TRUE) {
//            //TODO use a better forms and copy of data
//            addressForm.get(addressForm.proto().suiteNumber()).setValue(getValue().currentAddress().suiteNumber().getValue());
//            addressForm.get(addressForm.proto().streetNumber()).setValue(getValue().currentAddress().streetNumber().getValue());
//            addressForm.get(addressForm.proto().streetNumberSuffix()).setValue(getValue().currentAddress().streetNumberSuffix().getValue());
//            addressForm.get(addressForm.proto().streetName()).setValue(getValue().currentAddress().streetName().getValue());
//            addressForm.get(addressForm.proto().streetType()).setValue(getValue().currentAddress().streetType().getValue());
//            addressForm.get(addressForm.proto().streetDirection()).setValue(getValue().currentAddress().streetDirection().getValue());
//            addressForm.get(addressForm.proto().city()).setValue(getValue().currentAddress().city().getValue());
//            addressForm.get(addressForm.proto().county()).setValue(getValue().currentAddress().county().getValue());
//            addressForm.get(addressForm.proto().postalCode()).setValue(getValue().currentAddress().postalCode().getValue());
//            get(proto().phone()).setValue(getValue().currentPhone());
//
//            CComponent<Country, ?> country = addressForm.get(addressForm.proto().country());
//            country.setValue(getValue().currentAddress().country());
//
//            CComponent<Province, ?> prov = addressForm.get(addressForm.proto().province());
//            prov.setValue(getValue().currentAddress().province());
//
//            editable = false;
//        }
//
//        addressForm.get(addressForm.proto().suiteNumber()).setEditable(editable);
//        addressForm.get(addressForm.proto().streetNumber()).setEditable(editable);
//        addressForm.get(addressForm.proto().streetNumberSuffix()).setEditable(editable);
//        addressForm.get(addressForm.proto().streetName()).setEditable(editable);
//        addressForm.get(addressForm.proto().streetType()).setEditable(editable);
//        addressForm.get(addressForm.proto().streetDirection()).setEditable(editable);
//        addressForm.get(addressForm.proto().city()).setEditable(editable);
//        addressForm.get(addressForm.proto().county()).setEditable(editable);
//        addressForm.get(addressForm.proto().province()).setEditable(editable);
//        addressForm.get(addressForm.proto().country()).setEditable(editable);
//        addressForm.get(addressForm.proto().postalCode()).setEditable(editable);
//        get(proto().phone()).setEditable(editable);
//    }

    @Override
    public void populate(PaymentInformation value) {
        super.populate(value);
    }
}
