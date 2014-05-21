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
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.ref.Province;

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

    public static InternationalAddress getAddress(com.propertyvista.domain.property.asset.building.Building building) {
        InternationalAddress address = building.marketing().marketingAddress();
        if (isNull(address) || address.isEmpty() || !building.marketing().useCustomAddress().getValue(false)) {
            address = building.info().address();
        }
        return address;
    }

    public static String getProvinceCode(InternationalAddress address) {
        EntityQueryCriteria<Province> crit = EntityQueryCriteria.create(Province.class);
        crit.eq(crit.proto().name(), address.province());
        crit.eq(crit.proto().country(), address.country());
        Province prov = Persistence.service().retrieve(crit);
        return prov == null ? null : prov.code().getValue();
    }

    public static String formatStreetOnly(InternationalAddress address) {
        if (isNull(address) || address.isEmpty()) {
            return null;
        }
        return address.streetName().getValue();
    }

    public static String formatStreetAndNumber(InternationalAddress address) {
        if (isNull(address) || address.isEmpty()) {
            return null;
        }
        return SimpleMessageFormat.format("{0,choice,null#|!null#{0} }{1}", address.streetNumber().getValue(), address.streetName().getValue());
    }

    public static String formatStreetNumber(InternationalAddress address) {
        if (isNull(address) || address.isEmpty()) {
            return null;
        }
        return address.streetNumber().getValue();
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
