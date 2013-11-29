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
package com.propertyvista.ils.common;

import java.text.DecimalFormat;

import com.kijiji.pint.rs.ILSUnit.BathroomsEnum;
import com.kijiji.pint.rs.ILSUnit.BedroomsEnum;

import com.pyx4j.commons.SimpleMessageFormat;

import com.propertyvista.domain.contact.AddressStructured;

public class ILSUtils {

    public static String boolean2String(Boolean value) {

        return value == null || !value.booleanValue() ? "0" : "1";
    }

    public static String booleanNot2String(Boolean value) {

        return value == null || !value.booleanValue() ? "1" : "0";
    }

    public static String formatStreetAddress(AddressStructured address) {
        return SimpleMessageFormat.format("{0,choice,null#|!null#{0}-}{1} {2} {3}{4,choice,null#|!null# {4}}{5,choice,null#|!null# {5}}", address.suiteNumber()
                .getValue(), address.streetNumber().getValue(), address.streetNumberSuffix().getValue(), address.streetName().getValue(), address.streetType()
                .getValue(), address.streetDirection().getValue());
    }

    public static String formatStreetOnly(AddressStructured address) {

        return SimpleMessageFormat.format("{0}{1,choice,null#|!null# {1}}{2,choice,null#|!null# {2}}", address.streetName().getValue(), address.streetType()
                .getValue(), address.streetDirection().getValue());
    }

    public static String formatStreetNumber(AddressStructured address) {
        return SimpleMessageFormat.format("{0,choice,null#|!null#{0}-}{1} {2}", address.suiteNumber().getValue(), address.streetNumber().getValue(), address
                .streetNumberSuffix().getValue());
    }

    public static BedroomsEnum getBedrooms(Integer beds) {
        if (beds != null) {
            if (beds >= 6) {
                return BedroomsEnum.Six_More;
            }
            String v = beds.toString();
            for (BedroomsEnum e : BedroomsEnum.values()) {
                if (v.equals(e.value())) {
                    return e;
                }
            }
        }
        return BedroomsEnum.None;
    }

    public static BathroomsEnum getBathrooms(Integer baths, Integer halfBath) {
        if (baths != null) {
            double addBaths = halfBath == null ? 0 : halfBath / 2.0;
            if (baths + addBaths >= 5) {
                return BathroomsEnum.Five;// Smolka, 4 gottarent 5 is max value
            }
            String v = addBaths > 0 ? new DecimalFormat("#.#").format(baths + addBaths) : baths.toString();
            for (BathroomsEnum e : BathroomsEnum.values()) {
                if (v.equals(e.value())) {
                    return e;
                }
            }
        }
        return BathroomsEnum.None;
    }
}
