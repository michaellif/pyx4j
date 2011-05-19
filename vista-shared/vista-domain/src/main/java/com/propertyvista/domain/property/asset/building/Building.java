/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.building;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;
import com.pyx4j.i18n.shared.Translation;

import com.propertyvista.domain.Medium;
import com.propertyvista.domain.property.asset.BuildingAmenity;
import com.propertyvista.domain.property.asset.Complex;

@ToStringFormat("{0} {1}")
//TODO rename to Property
public interface Building extends IEntity {

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

    @EmbeddedEntity
    BuildingInfo info();

    IList<BuildingAmenity> amenities();

    @EmbeddedEntity
    BuildingFinancial financial();

    @EmbeddedEntity
    BuildingContactInfo contactInfo();

    // -----------------------Marketing---------------------------------------------------------------
    /**
     * Property name used for marketing purposes (max 120 char)
     */
    IPrimitive<String> marketingName();

    /**
     * Property description used for marketing purposes
     */
    IPrimitive<String> marketingDescription();

    // TODO - add list of property owners here...

    @Detached
    IList<Medium> media();

// --------------------------------------------------------------------------------------

    // there is a drop-down box with create new complex  
    Complex complex();
}
