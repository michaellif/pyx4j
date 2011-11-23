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
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.GeoLocation;

public class GeoLocationEditor extends CEntityDecoratableEditor<GeoLocation> {

    private static I18n i18n = I18n.get(GeoLocationEditor.class);

    public GeoLocationEditor() {
        super(GeoLocation.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().latitude()), 15).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().latitudeType()), 7).customLabel("Latitude Direction").build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().longitude()), 15).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().longitudeType()), 7).customLabel("Longitude Direction").build());
        return main;
    }

    @Override
    public void addValidations() {
        get(proto().latitude()).addValueValidator(new EditableValueValidator<Double>() {

            @Override
            public boolean isValid(CComponent<Double, ?> component, Double value) {
                return (value != null) && (value >= 0 && value <= 90);
            }

            @Override
            public String getValidationMessage(CComponent<Double, ?> component, Double value) {
                return i18n.tr("Latitude may be in range [0-90] degree");
            }
        });

        get(proto().longitude()).addValueValidator(new EditableValueValidator<Double>() {

            @Override
            public boolean isValid(CComponent<Double, ?> component, Double value) {
                return (value != null) && (value >= 0 && value <= 180);
            }

            @Override
            public String getValidationMessage(CComponent<Double, ?> component, Double value) {
                return i18n.tr("Longitude may be in range [0-180] degree");
            }
        });
        ((CTextFieldBase) get(proto().latitude())).setFormat(new GeoNumberFormat());
        ((CTextFieldBase) get(proto().longitude())).setFormat(new GeoNumberFormat());

    }

    public static class GeoNumberFormat implements IFormat<Double> {

        private final NumberFormat nf = NumberFormat.getFormat("#0.000000");;

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
                        return Math.abs(Double.valueOf(string));
                    } catch (NumberFormatException e) {
                        throw new ParseException(i18n.tr("Coordinate Format error"), 0);
                    }
                }
            }
        }
    }
}
