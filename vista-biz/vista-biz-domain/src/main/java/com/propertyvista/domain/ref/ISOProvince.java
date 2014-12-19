/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 21, 2014
 * @author stanp
 */
package com.propertyvista.domain.ref;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.i18n.shared.I18n;

public enum ISOProvince {
    // United states
    Alabama("Alabama", "AL", ISOCountry.UnitedStates), //
    Alaska("Alaska", "AK", ISOCountry.UnitedStates), //
    Arizona("Arizona", "AZ", ISOCountry.UnitedStates), //
    Arkansas("Arkansas", "AR", ISOCountry.UnitedStates), //
    California("California", "CA", ISOCountry.UnitedStates), //
    Colorado("Colorado", "CO", ISOCountry.UnitedStates), //
    Connecticut("Connecticut", "CT", ISOCountry.UnitedStates), //
    Delaware("Delaware", "DE", ISOCountry.UnitedStates), //
    Florida("Florida", "FL", ISOCountry.UnitedStates), //
    Georgia("Georgia", "GA", ISOCountry.UnitedStates), //
    Hawaii("Hawaii", "HI", ISOCountry.UnitedStates), //
    Idaho("Idaho", "ID", ISOCountry.UnitedStates), //
    Illinois("Illinois", "IL", ISOCountry.UnitedStates), //
    Indiana("Indiana", "IN", ISOCountry.UnitedStates), //
    Iowa("Iowa", "IA", ISOCountry.UnitedStates), //
    Kansas("Kansas", "KS", ISOCountry.UnitedStates), //
    Kentucky("Kentucky", "KY", ISOCountry.UnitedStates), //
    Louisiana("Louisiana", "LA", ISOCountry.UnitedStates), //
    Maine("Maine", "ME", ISOCountry.UnitedStates), //
    Maryland("Maryland", "MD", ISOCountry.UnitedStates), //
    Massachusetts("Massachusetts", "MA", ISOCountry.UnitedStates), //
    Michigan("Michigan", "MI", ISOCountry.UnitedStates), //
    Minnesota("Minnesota", "MN", ISOCountry.UnitedStates), //
    Mississippi("Mississippi", "MS", ISOCountry.UnitedStates), //
    Missouri("Missouri", "MO", ISOCountry.UnitedStates), //
    Montana("Montana", "MT", ISOCountry.UnitedStates), //
    Nebraska("Nebraska", "NE", ISOCountry.UnitedStates), //
    Nevada("Nevada", "NV", ISOCountry.UnitedStates), //
    NewHampshire("New Hampshire", "NH", ISOCountry.UnitedStates), //
    NewJersey("New Jersey", "NJ", ISOCountry.UnitedStates), //
    NewMexico("New Mexico", "NM", ISOCountry.UnitedStates), //
    NewYork("New York", "NY", ISOCountry.UnitedStates), //
    NorthCarolina("North Carolina", "NC", ISOCountry.UnitedStates), //
    NorthDakota("North Dakota", "ND", ISOCountry.UnitedStates), //
    Ohio("Ohio", "OH", ISOCountry.UnitedStates), //
    Oklahoma("Oklahoma", "OK", ISOCountry.UnitedStates), //
    Oregon("Oregon", "OR", ISOCountry.UnitedStates), //
    Pennsylvania("Pennsylvania", "PA", ISOCountry.UnitedStates), //
    RhodeIsland("Rhode Island", "RI", ISOCountry.UnitedStates), //
    SouthCarolina("South Carolina", "SC", ISOCountry.UnitedStates), //
    SouthDakota("South Dakota", "SD", ISOCountry.UnitedStates), //
    Tennessee("Tennessee", "TN", ISOCountry.UnitedStates), //
    Texas("Texas", "TX", ISOCountry.UnitedStates), //
    Utah("Utah", "UT", ISOCountry.UnitedStates), //
    Vermont("Vermont", "VT", ISOCountry.UnitedStates), //
    Virginia("Virginia", "VA", ISOCountry.UnitedStates), //
    Washington("Washington", "WA", ISOCountry.UnitedStates), //
    WestVirginia("West Virginia", "WV", ISOCountry.UnitedStates), //
    Wisconsin("Wisconsin", "WI", ISOCountry.UnitedStates), //
    Wyoming("Wyoming", "WY", ISOCountry.UnitedStates), //
    DistrictOfColumbia("District of Columbia", "DC", ISOCountry.UnitedStates), //
    AmericanSamoa("American Samoa", "AS", ISOCountry.UnitedStates), //
    Guam("Guam", "GU", ISOCountry.UnitedStates), //
    NorthernMarianaIslands("Northern Mariana Islands", "MP", ISOCountry.UnitedStates), //
    PuertoRico("Puerto Rico", "PR", ISOCountry.UnitedStates), //
    MinorOutlyingIslands("Minor Outlying Islands", "UM", ISOCountry.UnitedStates), //
    VirginIslands("Virgin Islands", "VI", ISOCountry.UnitedStates), //
    // Canada
    Alberta("Alberta", "AB", ISOCountry.Canada), //
    BritishColumbia("British Columbia", "BC", ISOCountry.Canada), //
    Manitoba("Manitoba", "MB", ISOCountry.Canada), //
    NewBrunswick("New Brunswick", "NB", ISOCountry.Canada), //
    Newfoundland("Newfoundland and Labrador", "NL", ISOCountry.Canada), //
    NovaScotia("Nova Scotia", "NS", ISOCountry.Canada), //
    Ontario("Ontario", "ON", ISOCountry.Canada), //
    PrinceEdwardIsland("Prince Edward Island", "PE", ISOCountry.Canada), //
    Quebec("Quebec", "QC", ISOCountry.Canada), //
    Saskatchewan("Saskatchewan", "SK", ISOCountry.Canada), //
    NorthwestTerritories("Northwest Territories", "NT", ISOCountry.Canada), //
    Nunavut("Nunavut", "NU", ISOCountry.Canada), //
    YukonTerritory("Yukon Territory", "YT", ISOCountry.Canada);

    private static final I18n i18n = I18n.get(ISOProvince.class);

    public final String name;

    public final String code;

    public final ISOCountry country;

    private ISOProvince(String name, String code, ISOCountry country) {
        this.name = name;
        this.code = code;
        this.country = country;
    }

    public static List<ISOProvince> forCountry(ISOCountry country) {
        List<ISOProvince> provList = new ArrayList<>();
        if (country != null) {
            for (ISOProvince prov : values()) {
                if (prov.country == country) {
                    provList.add(prov);
                }
            }
        }
        return provList;
    }

    public static ISOProvince forCode(String code) {
        if (code != null) {
            for (ISOProvince prov : values()) {
                if (code.equalsIgnoreCase(prov.code)) {
                    return prov;
                }
            }
        }
        return null;
    }

    public static ISOProvince forName(String name, ISOCountry country) {
        if (name != null && country != null) {
            for (ISOProvince prov : values()) {
                if (name.equalsIgnoreCase(prov.name) && country.equals(prov.country)) {
                    return prov;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return i18n.tr(name);
    }
}
