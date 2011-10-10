/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 6, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lead;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.RangeGroup;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.asset.Floorplan;

public interface Lead extends IEntity {

    @I18n
    public enum InformedFrom {

        internet,

        newspaper,

        radio,

        tv,

        mail,

        referal,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @I18n
    public enum Term {

        @Translate("6 months")
        months6,

        @Translate("12 months")
        months12,

        @Translate("18 months")
        months18,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @I18n
    public enum Source {

        toBeProvided;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @I18n
    public enum Status {

        active,

        closed,

        declined,

        rented;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @ToString(index = 0)
    @EmbeddedEntity
    Person person();

    @Caption(name = "How did you hear about us")
    IPrimitive<InformedFrom> informedFrom();

    @Caption(name = "Desired move-in date")
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> moveInDate();

    @Caption(name = "Desired monthly rent range: MIN and MAX")
    @EmbeddedEntity
    RangeGroup rent();

    @ToString(index = 1)
    @Caption(name = "Desired Lease Length")
    IPrimitive<Term> term();

    @Caption(name = "Desired bedrooms")
    IPrimitive<Integer> beds();

    @Caption(name = "Desired Bathrooms")
    IPrimitive<Integer> baths();

    @Caption(name = "Interested in Floorplan")
    Floorplan floorplan();

    @Caption(name = "Questions/Comments")
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> comments();

    // === Internals:
    IPrimitive<Source> source();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> createDate();

    IPrimitive<String> assignedTo();

    IPrimitive<Status> status();

// double reference - currently use just back reference from Appointment itself.
//    IList<Appointment> appointments();

    // === Internals:    
    IPrimitive<Boolean> convertedToLease();
}
