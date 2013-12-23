/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 15, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils.gottarent.mapper;

import java.text.DecimalFormat;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured;

public class GottarentMapperUtils {

    private static short MAX_TEL_LENGTH = 10;

    public static boolean isNull(IObject<?> o) {
        return o == null || o.isNull();
    }

    public static boolean isNullOrEmpty(IPrimitive<String> o) {
        return o == null || o.isNull() || o.getValue().trim().isEmpty();
    }

    public static String boolean2String(Boolean value) {

        return value == null || !value.booleanValue() ? "0" : "1";
    }

    public static String formatPhone(String value) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String result = value.trim().replaceAll("[^\\d]", "");
        if (result.startsWith("1") && result.length() > MAX_TEL_LENGTH) {
            result = result.substring("1".length());
        }
        result = result.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "$1-$2-$3");
        return result;
    }

    public static String booleanNot2String(Boolean value) {

        return value == null || !value.booleanValue() ? "1" : "0";
    }

    public static AddressStructured getAddress(com.propertyvista.domain.property.asset.building.Building building) {
        AddressStructured address = building.marketing().marketingAddress();
        if (isNull(address) || address.isEmpty() || !building.marketing().useCustomAddress().isBooleanTrue()) {
            address = building.info().address();
        }
        return address;
    }

    public static String formatStreetOnly(AddressStructured address) {
        if (isNull(address) || address.isEmpty()) {
            return null;
        }
        return SimpleMessageFormat.format("{0}{1,choice,null#|!null# {1}}{2,choice,null#|!null# {2}}", address.streetName().getValue(), address.streetType()
                .getValue(), address.streetDirection().getValue());
    }

    public static String formatStreetAndNumber(AddressStructured address) {
        if (isNull(address) || address.isEmpty()) {
            return null;
        }
        return SimpleMessageFormat.format("{0}{1,choice,null#|!null# {1}}{2}{3,choice,null#|!null# {3}}", address.streetNumber().getValue(), address
                .streetNumberSuffix().getValue(), address.streetName().getValue(), address.streetDirection().getValue());
    }

    public static String formatStreetNumber(AddressStructured address) {
        if (isNull(address) || address.isEmpty()) {
            return null;
        }
        return SimpleMessageFormat.format("{0}{1,choice,null#|!null#-{1}}", address.streetNumber().getValue(), address.streetNumberSuffix().getValue());
    }

    public static String getBedrooms(Integer beds) {
        if (beds == null || beds == 0) {
            return "B";
        }
        return beds.toString();
    }

    public static String getBathrooms(Integer baths, Integer halfBath) {
        if (baths != null) {
            double addBaths = halfBath == null ? 0 : halfBath / 2.0;
            if (baths + addBaths >= 5) {
                return "5";// Smolka, 4 gottarent 5 is max value
            }
            return (addBaths > 0 ? new DecimalFormat("#.#").format(baths + addBaths) : baths.toString());
        }
        return null;
    }
}
