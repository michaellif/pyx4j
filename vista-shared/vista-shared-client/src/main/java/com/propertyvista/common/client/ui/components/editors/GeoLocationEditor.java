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
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.GeoLocation;
import com.propertyvista.domain.GeoLocation.LatitudeDirection;
import com.propertyvista.domain.GeoLocation.LongitudeDirection;

public class GeoLocationEditor extends CForm<GeoLocation> {

    private static final I18n i18n = I18n.get(GeoLocationEditor.class);

    private final NumberFormat nf = NumberFormat.getFormat(i18n.tr("#0.000000"));

    public GeoLocationEditor() {
        super(GeoLocation.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().latitude()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().latitudeDirection()).decorate().componentWidth(100);

        formPanel.append(Location.Right, proto().longitude()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().longitudeDirection()).decorate().componentWidth(100);

        return formPanel;
    }

    @Override
    public void addValidations() {
        ((CTextFieldBase<Double, ?>) get(proto().latitude())).setFormatter(new GeoNumberFormatter());
        ((CTextFieldBase<Double, ?>) get(proto().latitude())).setParser(new GeoNumberParser());

        get(proto().latitude()).addComponentValidator(new AbstractComponentValidator<Double>() {
            @Override
            public BasicValidationError isValid() {
                Double value = getComponent().getValue();
                return (value == null || (value >= 0 && value <= 90)) ? null : new BasicValidationError(getComponent(), i18n
                        .tr("Latitude may be in range [0-90] degree"));
            }
        });
        get(proto().latitude()).addValueChangeHandler(new RevalidationTrigger<Double>(get(proto().latitudeDirection())));

        get(proto().latitudeDirection()).addComponentValidator(new AbstractComponentValidator<LatitudeDirection>() {
            @Override
            public BasicValidationError isValid() {
                return (getComponent().getValue() != null || get(proto().latitude()).getValue() == null) ? null : new BasicValidationError(getComponent(), i18n
                        .tr("Direction should be selected"));
            }
        });
        get(proto().latitudeDirection()).addValueChangeHandler(new RevalidationTrigger<LatitudeDirection>(get(proto().latitude())));

        ((CTextFieldBase<Double, ?>) get(proto().longitude())).setFormatter(new GeoNumberFormatter());
        ((CTextFieldBase<Double, ?>) get(proto().longitude())).setParser(new GeoNumberParser());

        get(proto().longitude()).addComponentValidator(new AbstractComponentValidator<Double>() {
            @Override
            public BasicValidationError isValid() {
                Double value = getComponent().getValue();
                return (value == null || (value >= 0 && value <= 180)) ? null : new BasicValidationError(getComponent(), i18n
                        .tr("Longitude may be in range [0-180] degree"));
            }
        });
        get(proto().longitude()).addValueChangeHandler(new RevalidationTrigger<Double>(get(proto().longitudeDirection())));

        get(proto().longitudeDirection()).addComponentValidator(new AbstractComponentValidator<LongitudeDirection>() {
            @Override
            public BasicValidationError isValid() {
                return (getComponent().getValue() != null || get(proto().longitude()).getValue() == null) ? null : new BasicValidationError(getComponent(),
                        i18n.tr("Direction should be selected"));
            }
        });
        get(proto().longitudeDirection()).addValueChangeHandler(new RevalidationTrigger<LongitudeDirection>(get(proto().longitude())));

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
                        throw new ParseException(i18n.tr("Coordinate Format Error"), 0);
                    }
                }
            }
        }
    }
}
