/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.pyx4j.commons.CommonsStringUtils;

import com.propertyvista.domain.contact.AddressStructured.StreetDirection;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.server.common.reference.StreetTypeAbbreviations;

public class CanadianStreetAddressParser implements StreetAddressParser {

    private static final List<String> unitDesignators = Arrays.asList(//@formatter:off
            "APARTMENT",
            "APT",
            "APPARTEMENT", // French
            "APP", // French abbreviation of the above
            "SUITE",
            "UNIT",            
            "BUREAU", // French for "suite"
            "UNITÉ" // French for unit            
    );//@formatter:on

    private static final List<String> streetDirectionKeywords = Arrays.asList(//@formatter:off
            "EAST",
            "E",
            "EST",
            "E",
            "NORTH",
            "N",
            "NORD",
            "N",
            "NORTHEAST",
            "NE",
            "NORD-EST",
            "NE",
            "NORTHWEST",
            "NW",
            "NORD-OUEST",
            "NO",
            "SOUTH",
            "S",
            "SUD",
            "S",
            "SOUTHEAST",
            "SE",
            "SUD-EST",
            "SE",
            "SOUTHWEST",
            "SW",
            "SUD-OUEST",
            "SO",
            "WEST",
            "W",
            "OUEST",
            "O"
    );//@formatter:on

    private static final Set<String> streetDirectionSet = Collections.unmodifiableSet(new HashSet<String>(streetDirectionKeywords));

    @Override
    public StreetAddress parse(String address1, String address2) throws ParseException {
        String[] addressTokens = address1.trim().split("\\s+");

        String streetNumber = addressTokens[0];
        String unitNumber = null;
        if (streetNumber.contains("-")) {
            String[] streetNumberAndUnitNumber = addressTokens[0].split("-");
            if (streetNumberAndUnitNumber.length != 2) {
                new ParseException("Failed to parse street address and unit number from `" + addressTokens[0] + "` for address : `" + address1 + "`", 0);
            }
            unitNumber = streetNumberAndUnitNumber[0];
            streetNumber = streetNumberAndUnitNumber[1];
        }
        int streetAddressPartLowerBound = 1;

        // check for street number fraction suffix
        if (streetAddressPartLowerBound < addressTokens.length) {
            if (addressTokens[streetAddressPartLowerBound].matches("\\d+/\\d+")) {
                streetNumber = streetNumber + ' ' + addressTokens[streetAddressPartLowerBound];
                streetAddressPartLowerBound += 1;
            }
        }
        int streetAddressPartUpperBound = addressTokens.length; // *exclusive* upper range of tokens that belong to street 

        // try to find unit number after the unit number designator (should be located at the last part of an address)
        if (unitNumber == null) {
            for (int i = addressTokens.length - 1; (unitNumber == null) & i >= streetAddressPartLowerBound; --i) {
                if (unitDesignators.contains(addressTokens[i].toUpperCase())) {
                    streetAddressPartUpperBound = i;

                    if (i + 1 < addressTokens.length) {
                        unitNumber = StringUtils.join(addressTokens, ' ', i + 1, addressTokens.length);
                    }
                }
            }
        }

        // if we still didn't get unit number try to get it from address2 line
        if (unitNumber == null & CommonsStringUtils.isStringSet(address2)) {
            String[] address2Tokens = address2.trim().split("\\s+");
            if (address2Tokens.length == 1) {
                unitNumber = address2Tokens[0];
            } else {
                // we *assume* that address is the next token after unit designator token                
                for (int i = 0; (unitNumber == null) & i < address2Tokens.length; ++i) {
                    if (unitDesignators.contains(address2Tokens[i].toUpperCase())) {
                        if (i + 1 < address2Tokens.length) {
                            unitNumber = address2Tokens[i + 1];
                        }
                    }
                }
            }
        }

        // try to parse street name:        
        // try to discover if it's a French or English type of street by street token index
        String streetName = "";
        StreetType streetType = null;
        StreetDirection streetDirection = null;

        int streetTypeTokenIndex = -1;
        for (int i = streetAddressPartUpperBound - 1; (i >= streetAddressPartLowerBound) && (streetTypeTokenIndex == -1); --i) {
            String normalizedToken = normalizeStreetTypeToken(addressTokens[i]);
            if (StreetTypeAbbreviations.getAllAbbreviations().contains(normalizedToken)) {
                streetTypeTokenIndex = i;
            }
        }

        // try to convert street type to enum
        if (streetTypeTokenIndex != -1) {
            streetType = StreetTypeAbbreviations.getStreetType(normalizeStreetTypeToken(addressTokens[streetTypeTokenIndex]));

            // parse street name
            if (streetTypeTokenIndex != streetAddressPartLowerBound) {
                // deal with English (or French numerical) street name
                int streetNameUpperBound;
                if (addressTokens[streetAddressPartLowerBound].toLowerCase().equals("the") & (streetAddressPartLowerBound + 1 == streetTypeTokenIndex)) {
                    streetNameUpperBound = streetTypeTokenIndex + 1;
                } else {
                    streetNameUpperBound = streetTypeTokenIndex != -1 ? streetTypeTokenIndex : streetAddressPartUpperBound;
                }
                streetName = StringUtils.join(addressTokens, ' ', streetAddressPartLowerBound, streetNameUpperBound);
            } else {
                // deal with French street name
                streetName = StringUtils.join(addressTokens, ' ', streetAddressPartLowerBound + 1, streetAddressPartUpperBound - 1);
                // TODO deal with French names                
                throw new ParseException("failed to parse street Name", streetTypeTokenIndex);
            }

            // try parse street direction: if present must be the last part of the street address part
            int streetDirectionTokenIndex = streetAddressPartUpperBound - 1;
            String streetDirectionCandidate = addressTokens[streetDirectionTokenIndex];

            if (streetDirectionSet.contains(addressTokens[streetDirectionTokenIndex].toUpperCase())) {
                int i = streetDirectionKeywords.indexOf(streetDirectionCandidate.toUpperCase());
                if (i != -1) {
                    streetDirectionCandidate = streetDirectionKeywords.get(i / 2 * 2);

                    streetDirectionFound: for (StreetDirection d : StreetDirection.values()) {
                        if (d.name().equalsIgnoreCase(streetDirectionCandidate)) {
                            streetDirection = d;
                            break streetDirectionFound;
                        }
                    }
                }
            }

        } else {
            // if we haven't found street type token assume everything is broken and don't care too much about rest of the parsing
            streetName = StringUtils.join(addressTokens, ' ', streetAddressPartLowerBound, streetAddressPartUpperBound);
            streetType = StreetType.other;
            streetDirection = null;
        }

        // if we haven't found the associated Enum value of street type, attach the street type back to the name of the street
        if (streetType == null && streetTypeTokenIndex != -1) {
            if (streetTypeTokenIndex == streetAddressPartLowerBound) {
                // French way of doing things
                streetName = addressTokens[streetTypeTokenIndex] + ' ' + streetName;
            } else {
                streetName = streetName + ' ' + streetName;
            }
            streetType = StreetType.other;
        }

        if ((unitNumber != null && unitNumber.contains(",")) || streetNumber == null || !streetNumber.matches("(\\d)+\\s*([a-zA-Z]*|\\d+/\\d+)")
                || streetName.startsWith("-")) {
            throw new ParseException("Parsed street address validation didn't pass! Parsing attempt failed", 0);
        }

        return new StreetAddress(unitNumber, streetNumber, streetName.toString(), streetType, streetDirection);
    }

    private String normalizeStreetTypeToken(String token) {
        String normalized = token.toLowerCase();
        if (normalized.endsWith(".")) {
            return normalized.substring(0, normalized.length() - 1);
        } else {
            return normalized;
        }
    }
}
