/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.components;

import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.domain.Money;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.IFormat;

public class ReadOnlyMoneyForm extends CEntityEditableComponent<Money> {

    public ReadOnlyMoneyForm() {
        super(Money.class);
    }

    @Override
    public IsWidget createContent() {
        CNumberLabel amount = new CNumberLabel();
        amount.setFormat(new IFormat<Number>() {

            @Override
            public String format(Number value) {
                return "$" + value;
            }

            @Override
            public Number parse(String string) {
                return null;
            }

        });
        bind(amount, proto().amount());
        return amount;

    }

}
