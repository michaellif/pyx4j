/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.reference;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.contact.AddressStructured.StreetType;

/**
 * 
 * Lists of Abbreviations
 * 
 * Postal Explorer > Publication 28 - Postal Addressing Standards > Appendix C > C1 Street Suffix Abbreviations
 * http://pe.usps.com/text/pub28/28apc_002.htm
 * 
 * Symbols and Abbreviations Recognized by Canada Post
 * http://www.canadapost.ca/tools/pg/manual/PGaddress-e.asp#1423617
 * 
 */
public class StreetTypeAbbreviations {

    private static Map<String, StreetType> streetTypeAbbreviations = new HashMap<String, StreetType>();

    private static void loadData() {
        if (!streetTypeAbbreviations.isEmpty()) {
            return;
        }
        Map<String, StreetType> supportedStreetTypes = new HashMap<String, StreetType>();
        for (StreetType streetType : StreetType.values()) {
            // TODO assumed loaded in Canadian Locale 
            supportedStreetTypes.put(streetType.toString().toLowerCase(Locale.ENGLISH), streetType);
        }
        streetTypeAbbreviations.putAll(supportedStreetTypes);

        {
            List<StreetTypeAbbreviationsImportCanada> canadaPost = EntityCSVReciver.create(StreetTypeAbbreviationsImportCanada.class).loadResourceFile(
                    IOUtils.resourceFileName("streetSuffixAbbreviationsCanada.xlsx", StreetTypeAbbreviations.class));
            for (StreetTypeAbbreviationsImportCanada abr : canadaPost) {
                StreetType streetType = supportedStreetTypes.get(abr.streetType().getValue().toLowerCase(Locale.ENGLISH).trim());
                if (streetType != null) {
                    streetTypeAbbreviations.put(abr.commonAbbreviation().getValue().toLowerCase(Locale.ENGLISH).trim(), streetType);
                }
            }
        }

        {
            List<StreetTypeAbbreviationsImportUS> usps = EntityCSVReciver.create(StreetTypeAbbreviationsImportUS.class).loadResourceFile(
                    IOUtils.resourceFileName("streetSuffixAbbreviationsUS.xlsx", StreetTypeAbbreviations.class));

            StreetType prevStreetType = null;
            for (StreetTypeAbbreviationsImportUS abr : usps) {
                StreetType streetType = prevStreetType;
                if (abr.streetSuffixName().isNull()) {
                    streetType = prevStreetType;
                } else {
                    streetType = supportedStreetTypes.get(abr.streetSuffixName().getValue().toLowerCase(Locale.ENGLISH).trim());
                }
                if (streetType != null) {
                    streetTypeAbbreviations.put(abr.commonAbbreviation().getValue().toLowerCase(Locale.ENGLISH).trim(), streetType);
                    if (!abr.USPSAbbreviation().isNull()) {
                        streetTypeAbbreviations.put(abr.USPSAbbreviation().getValue().toLowerCase(Locale.ENGLISH).trim(), streetType);
                    }
                }
                prevStreetType = streetType;
            }
        }
    }

    /**
     * @param abbreviation
     *            or StreetType name, case is ignored
     * @return null if abbreviation not found
     */
    public static StreetType getStreetType(String abbreviation) {
        loadData();
        return streetTypeAbbreviations.get(abbreviation.toLowerCase(Locale.ENGLISH).trim());
    }

    /**
     * @return Abbreviations in lower case that can be converted to StreetType
     */
    public static Collection<String> getAllAbbreviations() {
        loadData();
        return Collections.unmodifiableCollection(streetTypeAbbreviations.keySet());
    }

}
