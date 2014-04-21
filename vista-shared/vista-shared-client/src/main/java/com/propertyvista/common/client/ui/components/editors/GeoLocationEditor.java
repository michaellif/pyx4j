/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 23, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import java.text.ParseException;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormat;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.domain.GeoLocation;
import com.propertyvista.domain.GeoLocation.LatitudeType;
import com.propertyvista.domain.GeoLocation.LongitudeType;

public class GeoLocationEditor extends CEntityForm<GeoLocation> {

    private static final I18n i18n = I18n.get(GeoLocationEditor.class);

    public GeoLocationEditor() {
        super(GeoLocation.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
        int row = -1;

        main.setWidget(++row, 0, inject(proto().latitude(), new FieldDecoratorBuilder(10).build()));
        main.setWidget(row, 1, inject(proto().latitudeType(), new FieldDecoratorBuilder(6).customLabel("Latitude Direction").build()));

        main.setWidget(++row, 0, inject(proto().longitude(), new FieldDecoratorBuilder(10).build()));
        main.setWidget(row, 1, inject(proto().longitudeType(), new FieldDecoratorBuilder(6).customLabel("Longitude Direction").build()));

        return main;
    }

    @Override
    public void addValidations() {
        ((CTextFieldBase<Double, ?>) get(proto().latitude())).setFormat(new GeoNumberFormat());

        get(proto().latitude()).addComponentValidator(new AbstractComponentValidator<Double>() {
            @Override
            public FieldValidationError isValid() {
                CComponent<?, LatitudeType, ?> latitudeType = get(proto().latitudeType());
                Double value = getComponent().getValue();
                return ((value == null && latitudeType.getValue() == null) || (value != null && (value >= 0 && value <= 90))) ? null
                        : new FieldValidationError(getComponent(), i18n.tr("Latitude may be in range [0-90] degree"));
            }
        });
        get(proto().latitude()).addValueChangeHandler(new RevalidationTrigger<Double>(get(proto().latitudeType())));

        get(proto().latitudeType()).addComponentValidator(new AbstractComponentValidator<LatitudeType>() {
            @Override
            public FieldValidationError isValid() {
                CComponent<?, Double, ?> latitude = get(proto().latitude());
                return (getComponent().getValue() != null || latitude.getValue() == null) ? null : new FieldValidationError(getComponent(), i18n
                        .tr("This field is Mandatory"));
            }
        });
        get(proto().latitudeType()).addValueChangeHandler(new RevalidationTrigger<LatitudeType>(get(proto().latitude())));

        ((CTextFieldBase<Double, ?>) get(proto().longitude())).setFormat(new GeoNumberFormat());

        get(proto().longitude()).addComponentValidator(new AbstractComponentValidator<Double>() {
            @Override
            public FieldValidationError isValid() {
                CComponent<?, LongitudeType, ?> longitudeType = get(proto().longitudeType());
                return ((getComponent().getValue() == null && longitudeType.getValue() == null) || (getComponent().getValue() != null && (getComponent()
                        .getValue() >= 0 && getComponent().getValue() <= 180))) ? null : new FieldValidationError(getComponent(), i18n
                        .tr("Longitude may be in range [0-180] degree"));
            }
        });
        get(proto().longitude()).addValueChangeHandler(new RevalidationTrigger<Double>(get(proto().longitudeType())));

        get(proto().longitudeType()).addComponentValidator(new AbstractComponentValidator<LongitudeType>() {
            @Override
            public FieldValidationError isValid() {
                CComponent<?, Double, ?> longitude = get(proto().longitude());
                return (getComponent().getValue() != null || longitude.getValue() == null) ? null : new FieldValidationError(getComponent(), i18n
                        .tr("This field is Mandatory"));
            }
        });
        get(proto().longitudeType()).addValueChangeHandler(new RevalidationTrigger<LongitudeType>(get(proto().longitude())));

    }

    public static class GeoNumberFormat implements IFormat<Double> {

        private final NumberFormat nf = NumberFormat.getFormat(i18n.tr("#0.000000"));

        @Override
        public String format(Double value) {
            if (value != null) {
                return nf.format(value);
            } else {
                return "";
            }
        }

        @Override
        public Double parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null;
            } else {
                // Allow to enter Degrees Minutes Seconds , e.g. 12� 11' 22.9986"
                if (string.matches("\\d+[�\\xB0]\\s*\\d+['\\x27]\\s*\\d+(\\.\\d+)?[\"]")) {
                    String parts[] = string.split("[�\\xB0'\\x27\\\"]");
                    long d = Math.round(Integer.valueOf(parts[0].trim()) * 1000000.);
                    long min = Math.round(Integer.valueOf(parts[1].trim()) * 1000000.);
                    double sec = Math.round(Double.valueOf(parts[2].trim()) * 1000000.);
                    return Math.round(d + (min / 60.) + (sec / 3600.)) / 1000000.;
                } else {
                    try {
                        return Math.abs(nf.parse(string));
                    } catch (NumberFormatException e) {
                        throw new ParseException(i18n.tr("Coordinate Format error"), 0);
                    }
                }
            }
        }
    }
}
