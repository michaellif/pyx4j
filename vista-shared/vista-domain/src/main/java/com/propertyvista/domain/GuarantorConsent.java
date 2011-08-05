/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 4, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

public interface GuarantorConsent extends IEntity {

    @Translatable
    public enum Status {

        inactive,

        created,

        submited;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    IPrimitive<Status> status();

}
