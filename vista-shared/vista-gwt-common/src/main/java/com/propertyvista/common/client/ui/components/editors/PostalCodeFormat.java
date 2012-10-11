/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-11
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import java.text.ParseException;

import com.pyx4j.forms.client.ui.IFormat;

public class PostalCodeFormat implements IFormat<String> {

    /** returns country name in English converted to lower case */
    interface ICountryContextProvider {

        String getCountry();

    }

    private final ICountryContextProvider countryContextProvider;

    public PostalCodeFormat(ICountryContextProvider countryContextProvider) {
        this.countryContextProvider = countryContextProvider;
    }

    @Override
    public String format(String value) {
        String country = countryContextProvider.getCountry();
        String formattedValue;

        if ("canada".equals(country)) {
            String trimmed = value.replaceAll("\\s", "");
            if (trimmed.length() == 6) {
                formattedValue = (trimmed.substring(0, 3) + " " + trimmed.substring(3, 6)).toUpperCase();
            } else {
                formattedValue = value;
            }
        } else {
            formattedValue = value;
        }
        return formattedValue;
    }

    @Override
    public String parse(String postalCode) throws ParseException {
        return postalCode;
    }

}