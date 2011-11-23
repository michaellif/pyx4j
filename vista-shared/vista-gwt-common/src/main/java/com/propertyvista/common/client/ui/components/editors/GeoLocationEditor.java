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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.GeoLocation;

public class GeoLocationEditor extends CEntityDecoratableEditor<GeoLocation> {

    protected I18n i18n = I18n.get(GeoLocationEditor.class);

    public GeoLocationEditor() {
        super(GeoLocation.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int col = -1;
        main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().latitude()), 15).build());
        main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().latitudeType()), 7).customLabel("").build());
        main.setWidget(0, ++col, new HTML("&"));
        main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().longitude()), 15).build());
        main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().longitudeType()), 7).customLabel("").build());
        return main;
    }

    @Override
    public void addValidations() {
        get(proto().latitude()).addValueValidator(new EditableValueValidator<Double>() {

            @Override
            public boolean isValid(CComponent<Double, ?> component, Double value) {
                return (value >= 0 && value <= 90);
            }

            @Override
            public String getValidationMessage(CComponent<Double, ?> component, Double value) {
                return i18n.tr("Latitude may be in range [0-90] degree");
            }
        });

        get(proto().longitude()).addValueValidator(new EditableValueValidator<Double>() {

            @Override
            public boolean isValid(CComponent<Double, ?> component, Double value) {
                return (value >= 0 && value <= 180);
            }

            @Override
            public String getValidationMessage(CComponent<Double, ?> component, Double value) {
                return i18n.tr("Longitude may be in range [0-180] degree");
            }
        });

    }
}
