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
import java.util.Set;

public class CanadianStreetAddressParser implements StreetAddressParser {

    private static final Set<String> streetTypeKeywords = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(//@formatter:off
            "ABBEY",
            "ABBEY",
            "ACRES",
            "ACRES",
            "ALLÉE",
            "ALLÉE",
            "ALLEY",
            "ALLEY",
            "AUTOROUTE",
            "AUT",
            "AVENUE",
            "AVE",
            "AVENUE",
            "AV",
            "BAY",
            "BAY",
            "BEACH",
            "BEACH",
            "BEND",
            "BEND",
            "BOULEVARD",
            "BLVD",
            "BOULEVARD",
            "BOUL",
            "BY-PASS",
            "BYPASS",
            "BYWAY",
            "BYWAY",
            "CAMPUS",
            "CAMPUS",
            "CAPE",
            "CAPE",
            "CARRÉ",
            "CAR",
            "CARREFOUR",
            "CARREF",
            "CENTRE",
            "CTR",
            "CENTRE",
            "C",
            "CERCLE",
            "CERCLE",
            "CHASE",
            "CHASE",
            "CHEMIN",
            "CH",
            "CIRCLE",
            "CIR",
            "CIRCUIT",
            "CIRCT",
            "CLOSE",
            "CLOSE",
            "COMMON",
            "COMMON",
            "CONCESSION",
            "CONC",
            "CORNERS",
            "CRNRS",
            "CÔTE",
            "CÔTE",
            "COUR",
            "COUR",
            "COURS",
            "COURS",
            "COURT",
            "CRT",
            "COVE",
            "COVE",
            "CRESCENT",
            "CRES",
            "CROISSANT",
            "CROIS",
            "CROSSING",
            "CROSS",
            "CUL-DE-SAC",
            "CDS",
            "DALE",
            "DALE",
            "DELL",
            "DELL",
            "DIVERSION",
            "DIVERS",
            "DOWNS",
            "DOWNS",
            "DRIVE",
            "DR",
            "ÉCHANGEUR",
            "ÉCH",
            "END",
            "END",
            "ESPLANADE",
            "ESPL",
            "ESTATES",
            "ESTATE",
            "EXPRESSWAY",
            "EXPY",
            "EXTENSION",
            "EXTEN",
            "FARM",
            "FARM",
            "FIELD",
            "FIELD",
            "FOREST",
            "FOREST",
            "FREEWAY",
            "FWY",
            "FRONT",
            "FRONT",
            "GARDENS",
            "GDNS",
            "GATE",
            "GATE",
            "GLADE",
            "GLADE",
            "GLEN",
            "GLEN",
            "GREEN",
            "GREEN",
            "GROUNDS",
            "GRNDS",
            "GROVE",
            "GROVE",
            "HARBOUR",
            "HARBR",
            "HEATH",
            "HEATH",
            "HEIGHTS",
            "HTS",
            "HIGHLANDS",
            "HGHLDS",
            "HIGHWAY",
            "HWY",
            "HILL",
            "HILL",
            "HOLLOW",
            "HOLLOW",
            "ÎLE",
            "ÎLE",
            "IMPASSE",
            "IMP",
            "INLET",
            "INLET",
            "ISLAND",
            "ISLAND",
            "KEY",
            "KEY",
            "KNOLL",
            "KNOLL",
            "LANDING",
            "LANDNG",
            "LANE",
            "LANE",
            "LIMITS",
            "LMTS",
            "LINE",
            "LINE",
            "LINK",
            "LINK",
            "LOOKOUT",
            "LKOUT",
            "LOOP",
            "LOOP",
            "MALL",
            "MALL",
            "MANOR",
            "MANOR",
            "MAZE",
            "MAZE",
            "MEADOW",
            "MEADOW",
            "MEWS",
            "MEWS",
            "MONTÉE",
            "MONTÉE",
            "MOOR",
            "MOOR",
            "MOUNT",
            "MOUNT",
            "MOUNTAIN",
            "MTN",
            "ORCHARD",
            "ORCH",
            "PARADE",
            "PARADE",
            "PARC",
            "PARC",
            "PARK",
            "PK",
            "PARKWAY",
            "PKY",
            "PASSAGE",
            "PASS",
            "PATH",
            "PATH",
            "PATHWAY",
            "PTWAY",
            "PINES",
            "PINES",
            "PLACE",
            "PL",
            "PLACE",
            "PLACE",
            "PLATEAU",
            "PLAT",
            "PLAZA",
            "PLAZA",
            "POINT",
            "PT",
            "POINTE",
            "POINTE",
            "PORT",
            "PORT",
            "PRIVATE",
            "PVT",
            "PROMENADE",
            "PROM",
            "QUAI",
            "QUAI",
            "QUAY",
            "QUAY",
            "RAMP",
            "RAMP",
            "RANG",
            "RANG",
            "RANGE",
            "RG",
            "RIDGE",
            "RIDGE",
            "RISE",
            "RISE",
            "ROAD",
            "RD",
            "ROND-POINT",
            "RDPT",
            "ROUTE",
            "RTE",
            "ROW",
            "ROW",
            "RUE",
            "RUE",
            "RUELLE",
            "RLE",
            "RUN",
            "RUN",
            "SENTIER",
            "SENT",
            "SQUARE",
            "SQ",
            "STREET",
            "ST",
            "SUBDIVISION",
            "SUBDIV",
            "TERRACE",
            "TERR",
            "TERRASSE",
            "TSSE",
            "THICKET",
            "THICK",
            "TOWERS",
            "TOWERS",
            "TOWNLINE",
            "TLINE",
            "TRAIL",
            "TRAIL",
            "TURNABOUT",
            "TRNABT",
            "VALE",
            "VALE",
            "VIA",
            "VIA",
            "VIEW",
            "VIEW",
            "VILLAGE",
            "VILLGE",
            "VILLAS",
            "VILLAS",
            "VISTA",
            "VISTA",
            "VOIE",
            "VOIE",
            "WALK",
            "WALK",
            "WAY",
            "WAY",
            "WHARF",
            "WHARF",
            "WOOD",
            "WOOD",
            "WYND",
            "WYND"
    )));//@formatter:on 

    private static final Set<String> streetDirectionKeywords = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(//@formatter:off
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
    )));//@formatter:on

    private enum ParserState {
        PARSE_STREET_NAME, PARSE_STREET_TYPE, PARSE_STREET_DIRECTION, PARSE_END
    }

    @Override
    public StreetAddress parse(String address1, String address2) throws ParseException {
        String[] addressTokens = address1.trim().split("\\s");

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
        if (unitNumber == null) {
            unitNumber = address2;
        }

        int tokenIndex = 1;
        StringBuilder streetName = new StringBuilder();
        String streetDirection = null;
        String streetType = null;

        ParserState parserState = ParserState.PARSE_STREET_NAME;

        while (tokenIndex < addressTokens.length) {
            String token = addressTokens[tokenIndex].toUpperCase();
            switch (parserState) {
            case PARSE_STREET_NAME:
                if (streetTypeKeywords.contains(token)) {
                    parserState = ParserState.PARSE_STREET_TYPE;
                } else if (streetDirectionKeywords.contains(token)) {
                    parserState = ParserState.PARSE_STREET_DIRECTION;
                } else {
                    streetName.append(addressTokens[tokenIndex]);
                    tokenIndex += 1;
                }
                break;

            case PARSE_STREET_TYPE:
                streetType = addressTokens[tokenIndex];
                parserState = ParserState.PARSE_STREET_DIRECTION;
                tokenIndex += 1;
                break;

            case PARSE_STREET_DIRECTION:
                streetDirection = addressTokens[tokenIndex];
                tokenIndex += 1;
                parserState = ParserState.PARSE_END;
                break;

            case PARSE_END:
            default:
                throw new ParseException("Failed to parse address`" + address1 + "` unrecoginzed token `" + addressTokens[tokenIndex] + "`", tokenIndex);
            }
        }

        return new StreetAddress(unitNumber, streetNumber, streetName.toString(), null, null);
    }
}
