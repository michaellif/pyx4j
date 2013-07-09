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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.GeoLocation;
import com.propertyvista.domain.GeoLocation.LatitudeType;
import com.propertyvista.domain.GeoLocation.LongitudeType;

public class GeoLocationEditor extends CEntityDecoratableForm<GeoLocation> {

    private static final I18n i18n = I18n.get(GeoLocationEditor.class);

    public GeoLocationEditor() {
        super(GeoLocation.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().latitude()), 10).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().latitudeType()), 6).customLabel("Latitude Direction").build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().longitude()), 10).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().longitudeType()), 6).customLabel("Longitude Direction").build());

        main.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);
        return main;
    }

    @Override
    public void addValidations() {
        ((CTextFieldBase<Double, ?>) get(proto().latitude())).setFormat(new GeoNumberFormat());

        get(proto().latitude()).addValueValidator(new EditableValueValidator<Double>() {
            @Override
            public ValidationError isValid(CComponent<Double> component, Double value) {
                CComponent<LatitudeType> latitudeType = get(proto().latitudeType());
                return ((value == null && latitudeType.getValue() == null) || (value != null && (value >= 0 && value <= 90))) ? null : new ValidationError(
                        component, i18n.tr("Latitude may be in range [0-90] degree"));
            }
        });
        get(proto().latitude()).addValueChangeHandler(new RevalidationTrigger<Double>(get(proto().latitudeType())));

        get(proto().latitudeType()).addValueValidator(new EditableValueValidator<LatitudeType>() {
            @Override
            public ValidationError isValid(CComponent<LatitudeType> component, LatitudeType value) {
                CComponent<Double> latitude = get(proto().latitude());
                return (value != null || latitude.getValue() == null) ? null : new ValidationError(component, i18n.tr("This field is Mandatory"));
            }
        });
        get(proto().latitudeType()).addValueChangeHandler(new RevalidationTrigger<LatitudeType>(get(proto().latitude())));

        ((CTextFieldBase<Double, ?>) get(proto().longitude())).setFormat(new GeoNumberFormat());

        get(proto().longitude()).addValueValidator(new EditableValueValidator<Double>() {
            @Override
            public ValidationError isValid(CComponent<Double> component, Double value) {
                CComponent<LongitudeType> longitudeType = get(proto().longitudeType());
                return ((value == null && longitudeType.getValue() == null) || (value != null && (value >= 0 && value <= 180))) ? null : new ValidationError(
                        component, i18n.tr("Longitude may be in range [0-180] degree"));
            }
        });
        get(proto().longitude()).addValueChangeHandler(new RevalidationTrigger<Double>(get(proto().longitudeType())));

        get(proto().longitudeType()).addValueValidator(new EditableValueValidator<LongitudeType>() {
            @Override
            public ValidationError isValid(CComponent<LongitudeType> component, LongitudeType value) {
                CComponent<Double> longitude = get(proto().longitude());
                return (value != null || longitude.getValue() == null) ? null : new ValidationError(component, i18n.tr("This field is Mandatory"));
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
                // Allow to enter Degrees Minutes Seconds , e.g. 12° 11' 22.9986"
                if (string.matches("\\d+[°\\xB0]\\s*\\d+['\\x27]\\s*\\d+(\\.\\d+)?[\"]")) {
                    String parts[] = string.split("[°\\xB0'\\x27\\\"]");
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
