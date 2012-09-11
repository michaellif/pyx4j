/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 27, 2012
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.c;

import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.NTextBox;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.payment.TokenizedNumber;
import com.propertyvista.domain.util.DomainUtil;

public class CTokinazedNumberEditor<E extends TokenizedNumber> extends CTextFieldBase<E, NTextBox<E>> {

    private static final I18n i18n = I18n.get(CTokinazedNumberEditor.class);

    private final Class<? extends TokenizedNumber> clazz;

    public CTokinazedNumberEditor(Class<? extends TokenizedNumber> clazz) {
        super();
        this.clazz = clazz;

        setFormat(new TokinazedNumberFormat());
//        addValueValidator(new TextBoxParserValidator<E>());
    }

    @Override
    protected NTextBox<E> createWidget() {
        return new NTextBox<E>(this);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        setWatermark(getValue().getStringView());
    }

    class TokinazedNumberFormat implements IFormat<E> {

        TokinazedNumberFormat() {
        }

        @Override
        public String format(E value) {
            if (value == null) {
                return "";
            } else if (!value.newNumberValue().isNull()) {
                return value.newNumberValue().getStringView();
            } else {
                return value.getStringView();
            }
        }

        @Override
        public E parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }

            @SuppressWarnings("unchecked")
            E value = (E) EntityFactory.create(clazz);
            value.newNumberValue().setValue(string);
            value.reference().setValue(DomainUtil.last4Numbers(string));
            return value;
        }

    }
}