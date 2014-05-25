/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 19, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.domain.communication;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@Inheritance
@AbstractEntity
public interface CommunicationEndpoint extends IEntity {

    @I18n(context = "CommunicationEndpoint")
    @XmlType(name = "Contact Type")
    public enum ContactType {
        @Translate("SystemEndpoint")
        System,

        @Translate("Tenant")
        Tenants,

        @Translate("Corporate")
        Employee;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

}
