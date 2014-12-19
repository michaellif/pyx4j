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
 */
package com.propertyvista.common.client.ui.components.editors;

import com.pyx4j.commons.IFormatter;

import com.propertyvista.domain.ref.ISOCountry;

public class PostalCodeFormatter implements IFormatter<String, String> {

    /** returns country name in English converted to lower case */
    interface ICountryContextProvider {

        ISOCountry getCountry();

    }

    private final ICountryContextProvider countryContextProvider;

    public PostalCodeFormatter(ICountryContextProvider countryContextProvider) {
        this.countryContextProvider = countryContextProvider;
    }

    @Override
    public String format(String value) {
        if (value == null) {
            return null;
        }

        ISOCountry country = countryContextProvider.getCountry();
        String formattedValue;

        if (ISOCountry.Canada.equals(country)) {
            String trimmed = removeWhitespace(value);
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

    private static String removeWhitespace(String str) {
        return str.replaceAll("\\s", "");
    }

}