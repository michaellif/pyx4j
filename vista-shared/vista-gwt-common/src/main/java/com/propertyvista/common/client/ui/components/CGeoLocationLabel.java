/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.text.ParseException;

import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.forms.client.ui.CAbstractLabel;
import com.pyx4j.forms.client.ui.IFormat;

import com.propertyvista.domain.GeoLocation;

public class CGeoLocationLabel extends CAbstractLabel<GeoLocation> {

    public CGeoLocationLabel() {
        super();
        setGeoFormat(null);
    }

    public CGeoLocationLabel(String title) {
        super(title);
        setGeoFormat(null);
    }

    public void setGeoFormat(IFormat<GeoLocation> format) {
        setFormat(format != null ? format : new GeoLocationFormatter());
    }

    // ==========================================================================

    public static class GeoLocationFormatter implements IFormat<GeoLocation> {

        private final NumberFormat nf = NumberFormat.getFormat("#0.000000");;

        @Override
        public String format(GeoLocation value) {

            if (value != null && !value.isNull()) {
                String ret = new String();

                if (!value.latitude().isNull()) {

                    ret += nf.format(value.latitude().getValue());
                    ret += " " + value.latitudeType().getStringView();
                }

                if (!value.longitude().isNull()) {
                    ret += " & ";

                    ret += nf.format(value.longitude().getValue());
                    ret += " " + value.longitudeType().getStringView();
                }

                return ret;
            }
            return "";
        }

        @Override
        public GeoLocation parse(String string) throws ParseException {
            return null;
        }
    }
}
