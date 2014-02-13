/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 12, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.prospect.dto;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;

@Transient
public interface UnitSelectionDTO extends IEntity {

    Building building();

    Floorplan floorplan();

    @Caption(name = "Move-in Date")
    IPrimitive<LogicalDate> moveIn();

    //---------------------------------------------

    @I18n(context = "Bedroom Number")
    @XmlType(name = "BedroomNumber")
    public enum BedroomNumber {

        Any,

        @Translate("1")
        One,

        @Translate("1 + den")
        OneAndHalf,

        @Translate("2")
        Two,

        @Translate("2 + den")
        TwoAndHalf,

        @Translate("3")
        Three,

        @Translate("3 + den")
        ThreeAndHalf,

        @Translate("4")
        Four,

        @Translate("4 + den")
        FourAndHalf,

        @Translate("5")
        Five,

        @Translate("5 + den")
        FiveAndHalf,

        @Translate("More then 5")
        More;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n(context = "Bathroom Number")
    @XmlType(name = "BathroomNumber")
    public enum BathroomNumber {

        Any,

        @Translate("1")
        One,

        @Translate("2")
        Two,

        @Translate("3")
        Three,

        @Translate("More then 3")
        More;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    IPrimitive<BedroomNumber> bedrooms();

    IPrimitive<BathroomNumber> bathrooms();

    @Transient
    @ToStringFormat("{0} ({1} beds, {2} dens, {3} baths, available {4}, price ${5})")
    public interface UnitTO extends IEntity {

        @Editor(type = Editor.EditorType.label)
        IPrimitive<String> display();

        @ToString(index = 0)
        @Editor(type = Editor.EditorType.label)
        IPrimitive<String> number();

        @Editor(type = Editor.EditorType.label)
        IPrimitive<String> floorplan();

        @Editor(type = Editor.EditorType.label)
        IPrimitive<Integer> floor();

        @ToString(index = 1)
        @Editor(type = Editor.EditorType.label)
        IPrimitive<Integer> bedrooms();

        @ToString(index = 2)
        @Editor(type = Editor.EditorType.label)
        IPrimitive<Integer> dens();

        @ToString(index = 3)
        @Editor(type = Editor.EditorType.label)
        IPrimitive<Integer> bathrooms();

        @ToString(index = 4)
        @Editor(type = Editor.EditorType.label)
        IPrimitive<LogicalDate> available();

        @ToString(index = 5)
        @Format("#,##0.00")
        @Editor(type = EditorType.label)
        IPrimitive<BigDecimal> price();
    }

    @Editor(type = Editor.EditorType.label)
    UnitTO selectedUnit();

    @Caption(name = "Exact match:")
    IList<UnitTO> availableUnits();

    @Caption(name = "Partial match:")
    IList<UnitTO> potentialUnits();
}
