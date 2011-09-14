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
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;
import com.pyx4j.i18n.shared.Translation;

import com.propertyvista.domain.contact.Address;

@ToStringFormat("{0} {1}")
public interface BuildingInfo extends IEntity {

    @Translatable
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

    @Translatable
    public enum Shape {

        regular,

        @Translation("L-shape")
        lShape,

        @Translation("T-shape")
        tShape,

        @Translation("U-shape")
        uShape,

        irregular;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
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

    @Translatable
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

    @Translatable
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

    @Translatable
    public enum StructureType {

        @Translation("Low-Rise")
        lowRise,

        @Translation("High-Rise")
        highRise,

        @Translation("Mid-Rise")
        midRise,

        @Translation("Walk-up")
        walkUp,

        townhouse,

        condo,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
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

    Address address();

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
