/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.domain;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

// TODO define basic phone + contactPhone + BuildingPhone etc. which has different types enum...
public interface Phone extends IEntity {

    @Translatable
    public enum PhoneType {

        mobile,

        work,

        home,

        work_fax,

        home_fax,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    IPrimitive<PhoneType> phoneType();

    /**
     * (max 20 char)
     */
    IPrimitive<String> phoneNumber();

    /**
     * (max 20 char)
     */
    IPrimitive<String> extension();
}
