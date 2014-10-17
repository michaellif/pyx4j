/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 17, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.pyx4j.commons.CommonsStringUtils;

// This is a modified version of CanadianStreetAddressParser
public class CanadianLegalAddressParser implements StreetAddressParser {

    public enum StreetType { // by canadapost.ca
        Abbey("ABBEY"), //
        Acres("ACRES"), //
        Allée("ALLÉE"), //
        Alley("ALLEY"), //
        Autoroute("AUT"), //
        Avenue("AVE", "Avenue", "AV"), //
        Bay("BAY"), //
        Beach("BEACH"), //
        Bend("BEND"), //
        Boulevard("BLVD", "Boulevard", "BOUL"), //
        Bypass("BYPASS", "BY-PASS"), //
        Byway("BYWAY"), //
        Campus("CAMPUS"), //
        Cape("CAPE"), //
        Carré("CAR"), //
        Carrefour("CARREF"), //
        Centre("CTR", "Centre", "C"), //
        Cercle("CERCLE"), //
        Chase("CHASE"), //
        Chemin("CH"), //
        Circle("CIR"), //
        Circuit("CIRCT"), //
        Close("CLOSE"), //
        Common("COMMON"), //
        Concession("CONC"), //
        Corners("CRNRS"), //
        Côte("CÔTE"), //
        Cour("COUR"), //
        Cours("COURS"), //
        Court("CRT"), //
        Cove("COVE"), //
        Crescent("CRES"), //
        Croissant("CROIS"), //
        Crossing("CROSS"), //
        CulDeSac("CDS", "CUL-DE-SAC"), //
        Dale("DALE"), //
        Dell("DELL"), //
        Diversion("DIVERS"), //
        Downs("DOWNS"), //
        Drive("DR"), //
        Échangeur("ÉCH"), //
        End("END"), //
        Esplanade("ESPL"), //
        Estates("ESTATE"), //
        Expressway("EXPY"), //
        Extension("EXTEN"), //
        Farm("FARM"), //
        Field("FIELD"), //
        Forest("FOREST"), //
        Freeway("FWY"), //
        Front("FRONT"), //
        Gardens("GDNS"), //
        Gate("GATE"), //
        Glade("GLADE"), //
        Glen("GLEN"), //
        Green("GREEN"), //
        Grounds("GRNDS"), //
        Grove("GROVE"), //
        Harbour("HARBR"), //
        Heath("HEATH"), //
        Heights("HTS"), //
        Highlands("HGHLDS"), //
        Highway("HWY"), //
        Hill("HILL"), //
        Hollow("HOLLOW"), //
        Île("ÎLE"), //
        Impasse("IMP"), //
        Inlet("INLET"), //
        Island("ISLAND"), //
        Key("KEY"), //
        Knoll("KNOLL"), //
        Landing("LANDNG"), //
        Lane("LANE"), //
        Limits("LMTS"), //
        Line("LINE"), //
        Link("LINK"), //
        Lookout("LKOUT"), //
        Loop("LOOP"), //
        Mall("MALL"), //
        Manor("MANOR"), //
        Maze("MAZE"), //
        Meadow("MEADOW"), //
        Mews("MEWS"), //
        Montée("MONTÉE"), //
        Moor("MOOR"), //
        Mount("MOUNT"), //
        Mountain("MTN"), //
        Orchard("ORCH"), //
        Parade("PARADE"), //
        Parc("PARC"), //
        Park("PK"), //
        Parkway("PKY"), //
        Passage("PASS"), //
        Path("PATH"), //
        Pathway("PTWAY"), //
        Pines("PINES"), //
        Place("PL", "Place", "PLACE"), //
        Plateau("PLAT"), //
        // Plaza("PLAZA"), // Not Supported by PV
        Point("PT"), //
        Pointe("POINTE"), //
        Port("PORT"), //
        Private("PVT"), //
        Promenade("PROM"), //
        Quai("QUAI"), //
        Quay("QUAY"), //
        Ramp("RAMP"), //
        Rang("RANG"), //
        Range("RG"), //
        Ridge("RIDGE"), //
        Rise("RISE"), //
        Road("RD"), //
        RondPoint("RDPT", "ROND-POINT"), //
        Route("RTE"), //
        Row("ROW"), //
        Rue("RUE"), //
        Ruelle("RLE"), //
        Run("RUN"), //
        Sentier("SENT"), //
        Square("SQ"), //
        Street("ST"), //
        Subdivision("SUBDIV"), //
        Terrace("TERR"), //
        Terrasse("TSSE"), //
        Thicket("THICK"), //
        Towers("TOWERS"), //
        Townline("TLINE"), //
        Trail("TRAIL"), //
        Turnabout("TRNABT"), //
        Vale("VALE"), //
        Via("VIA"), //
        View("VIEW"), //
        Village("VILLGE"), //
        Villas("VILLAS"), //
        Vista("VISTA"), //
        Voie("VOIE"), //
        Walk("WALK"), //
        Way("WAY"), //
        Wharf("WHARF"), //
        Wood("WOOD"), //
        Wynd("WYND");

        public final String[] names;

        StreetType(String... names) {
            this.names = names;
        }
    }

    public enum StreetDirection { // by canadapost.ca
        East("Est", "E"), //
        North("Nord", "N"), //
        Northeast("Nord-Est", "NE"), //
        Northwest("NW", "Nord-Ouest", "NO"), //
        South("Sud", "S"), //
        Southeast("Sud-Est", "SE"), //
        Southwest("SW", "Sud-Ouest", "SO"), //
        West("W", "Ouest", "O");

        public final String[] names;

        StreetDirection(String... names) {
            this.names = names;
        }
    }

    private static final List<String> unitDesignators = Arrays.asList( // by canadapost.ca
            "APARTMENT", //
            "APT", //
            "APPARTEMENT", // French
            "APP", // French abbreviation of the above
            "SUITE", //
            "UNIT", //
            "BUREAU", // French for "suite"
            "UNITÉ" // French for unit            
    );

    private static final Set<String> streetTypes = new HashSet<>();

    private static final Set<String> streetDirections = new HashSet<>();

    static {
        for (StreetType type : StreetType.values()) {
            streetTypes.add(type.name().toUpperCase());
            for (String name : type.names) {
                streetTypes.add(name.toUpperCase());
            }
        }
        for (StreetDirection dir : StreetDirection.values()) {
            streetDirections.add(dir.name().toUpperCase());
            for (String name : dir.names) {
                streetDirections.add(name.toUpperCase());
            }
        }
    }

    @Override
    public StreetAddress parse(String address1, String address2) throws ParseException {
        String[] addressTokens = address1.trim().split("[ .]+");

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
        // - discover if it's a French or English type of street by street token index
        String streetName = null;
        String streetType = null;
        String streetDirection = null;

        int streetTypeTokenIndex = -1;
        for (int i = streetAddressPartUpperBound - 1; i >= streetAddressPartLowerBound; --i) {
            if (streetTypes.contains(addressTokens[i].toUpperCase())) {
                streetTypeTokenIndex = i;
                streetType = addressTokens[i];
                break;
            }
        }

        // try to convert street type to enum
        if (streetTypeTokenIndex != -1) {
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
                // TODO deal with French names                
                streetName = StringUtils.join(addressTokens, ' ', streetAddressPartLowerBound + 1, streetAddressPartUpperBound - 1);
            }

            // try parse street direction: if present must be the last part of the street address part
            int streetDirectionTokenIndex = streetAddressPartUpperBound - 1;

            if (streetDirections.contains(addressTokens[streetDirectionTokenIndex].toUpperCase())) {
                streetDirection = addressTokens[streetDirectionTokenIndex];
            }

        } else {
            // if we haven't found street type token assume everything is broken and don't care too much about rest of the parsing
            throw new ParseException("Cannot extract street type", streetAddressPartLowerBound);
        }

        if ((unitNumber != null && unitNumber.contains(",")) || streetNumber == null || !streetNumber.matches("(\\d)+\\s*([a-zA-Z]*|\\d+/\\d+)")
                || streetName.startsWith("-")) {
            throw new ParseException("Parsed address validation failed", addressTokens.length);
        }

        return new StreetAddress(unitNumber, streetNumber, streetName.toString(), streetType, streetDirection);
    }
}
