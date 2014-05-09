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
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.GeoLocation;
import com.propertyvista.domain.GeoLocation.LatitudeType;
import com.propertyvista.domain.GeoLocation.LongitudeType;

public class GeoLocationEditor extends CForm<GeoLocation> {

    private static final I18n i18n = I18n.get(GeoLocationEditor.class);

    private final NumberFormat nf = NumberFormat.getFormat(i18n.tr("#0.000000"));

    public GeoLocationEditor() {
        super(GeoLocation.class);
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().latitude()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().latitudeType()).decorate().componentWidth(100).customLabel("Latitude Direction");

        formPanel.append(Location.Right, proto().longitude()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().longitudeType()).decorate().componentWidth(100).customLabel("Longitude Direction");

        return formPanel;
    }

    @Override
    public void addValidations() {
        ((CTextFieldBase<Double, ?>) get(proto().latitude())).setFormatter(new GeoNumberFormatter());
        ((CTextFieldBase<Double, ?>) get(proto().latitude())).setParser(new GeoNumberParser());

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

        ((CTextFieldBase<Double, ?>) get(proto().longitude())).setFormatter(new GeoNumberFormatter());
        ((CTextFieldBase<Double, ?>) get(proto().longitude())).setParser(new GeoNumberParser());

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

    public class GeoNumberFormatter implements IFormatter<Double, String> {

        @Override
        public String format(Double value) {
            if (value != null) {
                return nf.format(value);
            } else {
                return "";
            }
        }
    }

    public class GeoNumberParser implements IParser<Double> {

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
