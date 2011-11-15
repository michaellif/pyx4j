/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import java.util.Date;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.contact.Phone;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;

public interface Inquiry extends IEntity {

    @I18n
    public enum Title {
        Mr, Mrs, Ms, Miss;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    public enum LeaseTerm {
        mon6(6), mon12(12), mon18(18);

        private static final com.pyx4j.i18n.shared.I18n i18n = com.pyx4j.i18n.shared.I18n.get(Inquiry.LeaseTerm.class);

        private final int term;

        private LeaseTerm(int months) {
            term = months;
        }

        private int rightOrdinal() {
            return (LeaseTerm.values().length - this.ordinal() - 1);
        }

        public int getTerm() {
            return term;
        }

        @Override
        public String toString() {
            String format = "{0} months{1,choice,0# or longer|0<}";
            return i18n.tr(format, term, rightOrdinal());
        }
    }

    @I18n
    public enum DayPart {
        Morning, Afternoon, Evening;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    public enum RefSource {
        Internet, Newspaper, Radio, Referral, Import, Locator_Services, Direct_Mail;

        private static final com.pyx4j.i18n.shared.I18n i18n = com.pyx4j.i18n.shared.I18n.get(Inquiry.RefSource.class);

        @Override
        public String toString() {
            return i18n.tr(name().replace("_", " "));
        }
    }

    @EmbeddedEntity
    @ToString(index = 0)
    Name name();

    @Owned
    @Detached
    IList<Phone> phones();

    @EmbeddedEntity
    Email email();

    IPrimitive<LeaseTerm> leaseTerm();

    IPrimitive<Date> movingDate();

    IPrimitive<Date> appointmentDate1();

    IPrimitive<DayPart> appointmentTime1();

    IPrimitive<Date> appointmentDate2();

    IPrimitive<DayPart> appointmentTime2();

    IPrimitive<String> refSource();

    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> comments();

    Building building();

    Floorplan floorplan();

    IPrimitive<Date> created();
}
