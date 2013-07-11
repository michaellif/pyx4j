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

import com.propertyvista.domain.contact.AddressStructured.StreetDirection;
import com.propertyvista.domain.contact.AddressStructured.StreetType;

public class CanadianStreetAddressParser implements StreetAddressParser {

    private static final List<String> streetTypeKeywords = Arrays.asList(//@formatter:off
            "ABBEY",
            "ABBEY",
            "ACRES",
            "ACRES",
            "ALL�E",
            "ALL�E",
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
            "CARR�",
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
            "C�TE",
            "C�TE",
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
            "�CHANGEUR",
            "�CH",
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
            "�LE",
            "�LE",
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
            "MONT�E",
            "MONT�E",
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
    );//@formatter:on 

    private static final Set<String> streetTypeSet = Collections.unmodifiableSet(new HashSet<String>(streetTypeKeywords));

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

    private enum ParserState {
        StreetName, StreetType, StreetDirection, End
    }

    @Override
    public StreetAddress parse(String address1, String address2) throws ParseException {
        String[] addressTokens = address1.trim().replaceAll("\\s+", " ").split(" ");

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
        StreetType streetType = null;
        StreetDirection streetDirection = null;

        ParserState parserState = ParserState.StreetName;

        while (tokenIndex < addressTokens.length) {

            switch (parserState) {
            case StreetName:
                String normalizedToken = addressTokens[tokenIndex].toUpperCase();
                if (streetTypeSet.contains(normalizedToken)) {
                    parserState = ParserState.StreetType;
                } else if (streetDirectionSet.contains(normalizedToken)) {
                    parserState = ParserState.StreetDirection;
                } else {
                    if (streetName.length() != 0) {
                        streetName.append(' ');
                    }
                    streetName.append(addressTokens[tokenIndex]);
                    tokenIndex += 1;
                }
                break;

            case StreetType: {
                String streetTypeCandidate = addressTokens[tokenIndex];
                int i = streetTypeKeywords.indexOf(streetTypeCandidate.toUpperCase());
                try {
                    // (i / 2 * 2) should convert index of abbreviation to index of associated full name of street type
                    streetType = StreetType.valueOf(streetTypeKeywords.get(i / 2 * 2).toLowerCase());
                } catch (Throwable e) {
                    // we don't care
                }

                if (streetType == null) {
                    streetName.append(' ').append(streetTypeCandidate);
                    streetType = StreetType.other;
                }

                parserState = ParserState.StreetDirection;
                tokenIndex += 1;
                break;
            }

            case StreetDirection: {
                String streetDirectionCandidate = addressTokens[tokenIndex];
                int i = streetDirectionKeywords.indexOf(streetDirectionCandidate.toLowerCase());
                try {
                    streetDirection = StreetDirection.valueOf(streetDirectionKeywords.get(i / 2 * 2).toLowerCase());
                } catch (Throwable e) {
                    throw new ParseException("failed to match street direction `" + streetDirectionCandidate + "` to any known value for address + `"
                            + address1 + "`", tokenIndex);
                }

                tokenIndex += 1;
                parserState = ParserState.End;
                break;
            }

            case End:
            default:
                throw new ParseException("Failed to parse address`" + address1 + "` unrecoginzed token `" + addressTokens[tokenIndex] + "`", tokenIndex);
            }

        }

        return new StreetAddress(unitNumber, streetNumber, streetName.toString(), streetType, streetDirection);
    }
}
