/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-13
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.contact;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.domain.ref.ISOProvince;

@EmbeddedEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@ToStringFormat("{0,choice,null#|!null#{0}-}{1} {2} {3}{4,choice,other#|null#|!null# {4}}{5,choice,null#|!null# {5}}, {6}, {7} {8}, {9}")
public interface AddressStructured extends IEntity {

    @I18n
    @I18nComment("Street Type")
    public enum StreetType {//@formatter:off
        // reference http://www.canadapost.ca/tools/pg/manual/PGaddress-e.asp#1423617
        alley("ALLEY"),        
        approach("APPROACH"), // FIXME: not in list of recognized by Canada Post street types
        arcade("ARCADE"), // FIXME: not in list of recognized by Canada Post street types
        avenue("AVE"),
        boulevard("BLVD"), 
        brow("BROW"), // FIXME: not in list of recognized by Canada Post street types
        bypass("BYPASS"),
        causeway("CAUSEWAY"), // FIXME: not in list of recognized by Canada Post street types
        circuit("CIRCT"),
        circle("CIR"),
        circus("CIRCUS"), // FIXME: not in list of recognized by Canada Post street types
        close("CLOSE"),
        copse("COPSE"), // FIXME: not in list of recognized by Canada Post street types
        corner("CORNER"), // FIXME: not in list of recognized by Canada Post street types
        court("CRT"),
        cove("COVE"),
        crescent("CRES"),
        drive("DR"),
        end("END"),
        esplanande("ESPL"),
        flat("FLAT"), // FIXME: not in list of recognized by Canada Post street types
        freeway("FWY"),
        frontage("FRONTAGE"), // FIXME: not in list of recognized by Canada Post street types
        gardens("GDNS"),
        glade("GLADE"),
        glen("GLEN"),
        green("GREEN"),
        grove("GROVE"),
        heights("HARBR"),
        highway("HWY"),
        lane("LANE"),
        line("LINE"),
        link("LINK"),
        loop("LOOP"),
        mall("MALL"),
        mews("MEWS"),
        packet("PACKET"), // FIXME: not in list of recognized by Canada Post street types
        parade("PARADE"),
        park("PK"),
        parkway("PKY"),
        place("PL"),
        promenade("PROM"),
        reserve("RESERVE"), // FIXME: not in list of recognized by Canada Post street types
        ridge("RIDGE"),
        rise("RISE"),
        road("RD"),
        row("ROW"),    
        square("SQ"),
        street("ST"),
        strip("STRIP"), // FIXME: not in list of recognized by Canada Post street types
        tarn("TARN"), // FIXME: not in list of recognized by Canada Post street types
        terrace("TERR"),
        thoroughfaree("THOROUGHFAREE"), // FIXME: not in list of recognized by Canada Post street types
        track("TRACK"), // FIXME: not in list of recognized by Canada Post street types
        trunkway("TRUNKWAY"), // FIXME: not in list of recognized by Canada Post street types
        view("VIEW"),
        vista("VISTA"),
        walk("WALK"),
        way("WAY"),
        walkway("WALKWAY"), // FIXME: not in list of recognized by Canada Post street types
        yard("YARD"), // FIXME: not in list of recognized by Canada Post street types

        other("OTHER");
        
        private final String abbr;

        private StreetType(String abbr) {
            this.abbr = abbr;
        }


        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
        
        public String toAbbr() {
            return this.abbr;
        }
    }// @formatter:on

    @I18n
    public enum StreetDirection {

        east("E"), north("N"), south("S"), west("W"),

        southEast("SE"), southWest("SW"),

        northEast("NE"), northWest("NW");

        private final String abbr;

        private StreetDirection(String abbr) {
            this.abbr = abbr;
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        public String toAbbr() {
            return this.abbr;
        }
    }

    @ToString(index = 0)
    IPrimitive<String> suiteNumber();

    @NotNull
    @ToString(index = 1)
    IPrimitive<String> streetNumber();

    @ToString(index = 2)
    IPrimitive<String> streetNumberSuffix();

    @NotNull
    @ToString(index = 3)
    IPrimitive<String> streetName();

    @NotNull
    @ToString(index = 4)
    IPrimitive<StreetType> streetType();

    @ToString(index = 5)
    IPrimitive<StreetDirection> streetDirection();

    @NotNull
    @ToString(index = 6)
    IPrimitive<String> city();

    IPrimitive<String> county();

    @NotNull
    @ToString(index = 7)
    @Caption(name = "Province")
    IPrimitive<ISOProvince> province();

    @NotNull
    @ToString(index = 9)
    @Caption(name = "Country")
    IPrimitive<ISOCountry> country();

    @NotNull
    @ToString(index = 8)
    @Caption(name = "Postal Code")
    IPrimitive<String> postalCode();
}
