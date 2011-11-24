/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.c;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.domain.GeoLocation;

public class CGeoLocation extends CComponent<GeoLocation, NativeGeoLocation> {

    public CGeoLocation() {
        addValueValidator(new GeoLocationValidator());
    }

    public CGeoLocation(String title) {
        super(title);
        addValueValidator(new GeoLocationValidator());
    }

    @Override
    protected NativeGeoLocation createWidget() {
        return new NativeGeoLocation(this);
    }

    @Override
    public void onEditingStop() {
        super.onEditingStop();
        if (isValid()) {
            setNativeValue(getValue());
        }
    }

    // ==========================================================================

    public class GeoLocationValidator implements EditableValueValidator<GeoLocation> {

        private String validationMessage = "";

        @Override
        public String getValidationMessage(CComponent<GeoLocation, ?> component, GeoLocation value) {
            return validationMessage;
        }

        @Override
        public boolean isValid(CComponent<GeoLocation, ?> component, GeoLocation value) {
            validationMessage = "";

            if (value != null && !value.isEmpty()) {
                if (!value.latitude().isNull()) {
                    if (value.latitude().getValue() < 0 || value.latitude().getValue() > 90) {
                        validationMessage = "Latitude may be in range [0-90] degree";
                        return false;
                    }
                }
                if (!value.longitude().isNull()) {
                    if (value.longitude().getValue() < 0 || value.longitude().getValue() > 180) {
                        validationMessage = "Longitude may be in range [0-180] degree";
                        return false;
                    }
                }
            }

            return true;
        }
    }
}
