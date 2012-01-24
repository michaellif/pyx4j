/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 9, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.person.Name;

@Transient
public interface ApplicationStatusDTO extends IEntity {

    @I18n
    @XmlType(name = "PersonRole")
    public static enum Role implements Serializable {

        Tenant,

        Guarantor;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    /**
     * Tenant/Guarantor
     */
    Name person();

    /**
     * Applicant, Co-Applicant or Guarantor
     */
    IPrimitive<Role> role();

    /**
     * Completed steps/total steps in %
     */
    @Format("#0.00")
    @Caption(name = "Progress (%)")
    IPrimitive<Double> progress();

    IPrimitive<String> description();
}
