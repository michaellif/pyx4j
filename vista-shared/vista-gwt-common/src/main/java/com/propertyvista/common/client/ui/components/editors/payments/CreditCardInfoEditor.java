/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 2, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.payments;

import java.util.Date;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CTextComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.validators.CreditCardNumberValidator;
import com.propertyvista.common.client.ui.validators.FutureDateValidator;
import com.propertyvista.domain.payment.CreditCardInfo;

public class CreditCardInfoEditor extends CEntityDecoratableForm<CreditCardInfo> {

    private static final I18n i18n = I18n.get(CreditCardInfoEditor.class);

    public CreditCardInfoEditor() {
        super(CreditCardInfo.class);
    }

    public CreditCardInfoEditor(IEditableComponentFactory factory) {
        super(CreditCardInfo.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel panel = new FormFlexPanel();

        int row = -1;
        CMonthYearPicker monthYearPicker = new CMonthYearPicker(false);
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().nameOn()), 20).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().cardType()), 20).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().number()), 20).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().expiryDate(), monthYearPicker), 20).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().securityCode()), 5).build());

        // tweak:
        monthYearPicker.setYearRange(new Range(1900 + new Date().getYear(), 10));
        get(proto().securityCode()).setVisible(isEditable());

        return panel;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        updateVisibility(getValue());
    }

    private void updateVisibility(CreditCardInfo value) {
        if (isEditable()) {
            get(proto().number()).setMandatory(false);
            get(proto().securityCode()).setMandatory(false);
            if (!value.numberRefference().isNull()) {
                ((CTextComponent<?, ?>) get(proto().number())).setWatermark(i18n.tr("XXXX-XXXX-{0}", value.numberRefference().getValue()));
            } else {
                ((CTextComponent<?, ?>) get(proto().number())).setWatermark(null);
            }
        } else {
            get(proto().number()).setValue(i18n.tr("XXXX-XXXX-{0}", value.numberRefference().getValue()));
        }
    }

    @Override
    public void addValidations() {
        this.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyName.editable) {
                    get(proto().securityCode()).setVisible(isEditable());
                }
            }
        });

        get(proto().number()).addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                get(proto().number()).setMandatory(true);
                get(proto().securityCode()).setMandatory(true);
            }
        });

        get(proto().number()).addValueValidator(new CreditCardNumberValidator());
        get(proto().expiryDate()).addValueValidator(new FutureDateValidator());
    }
}
