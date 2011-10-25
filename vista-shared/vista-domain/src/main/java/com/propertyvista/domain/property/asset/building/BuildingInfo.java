/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.building;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.contact.AddressStructured;

@ToStringFormat("{0} {1}")
public interface BuildingInfo extends IEntity {

    @I18n
    @XmlType(name = "BuildingInfoType")
    public enum Type {

        agricultural,

        commercial,

        mixed_residential,

        residential,

        industrial,

        military,

        parking_storage,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @I18n
    public enum Shape {

        regular,

        @Translate("L-shape")
        lShape,

        @Translate("T-shape")
        tShape,

        @Translate("U-shape")
        uShape,

        irregular;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @I18n
    public enum ConstructionType {

        brick,

        wood,

        block,

        panel,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @I18n
    public enum FoundationType {

        pile,

        continuousFooting,

        spreadFooting,

        foundationWalls,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @I18n
    public enum FloorType {

        hardwood,

        tile,

        laminate,

        carpet,

        mixed,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @I18n
    public enum StructureType {

        @Translate("Low-Rise")
        lowRise,

        @Translate("Highrise")
        highRise,

        @Translate("Mid-Rise")
        midRise,

        @Translate("Walk-up")
        walkUp,

        townhouse,

        condo,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @I18n
    public enum WaterSupply {

        municipal,

        privateWell,

        privateCommunityWell,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    /**
     * Legal name of the property (max 120 char)
     */
    @ToString(index = 0)
    IPrimitive<String> name();

    AddressStructured address();

    @ToString(index = 1)
    @MemberColumn(name = "buildingType")
    IPrimitive<Type> type();

    // ---- Physical: ----------------

    IPrimitive<Shape> shape();

    IPrimitive<String> totalStoreys();

    IPrimitive<String> residentialStoreys();

    IPrimitive<StructureType> structureType();

    @Format("yyyy")
    @Editor(type = EditorType.yearpicker)
    IPrimitive<LogicalDate> structureBuildYear();

    IPrimitive<ConstructionType> constructionType();

    IPrimitive<FoundationType> foundationType();

    IPrimitive<FloorType> floorType();

    IPrimitive<String> landArea();

    IPrimitive<WaterSupply> waterSupply();

    IPrimitive<Boolean> centralAir();

    IPrimitive<Boolean> centralHeat();
}
