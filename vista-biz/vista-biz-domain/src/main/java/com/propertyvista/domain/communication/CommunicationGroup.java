/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 24, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.domain.communication;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.CrmRole;

@DiscriminatorValue("CommunicationGroup")
public interface CommunicationGroup extends CommunicationEndpoint {

    @I18n(context = "Endpoint Group")
    @XmlType(name = "Endpoint Group")
    public enum EndpointGroup {
        Commandant,

        Custom;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n(context = "Contact Type")
    @XmlType(name = "Contact Type")
    public enum ContactType {
        @Translate("Predefined Group")
        Group,

        @Translate("Tenant")
        Tenants,

        @Translate("Corporate")
        Employee;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    IPrimitive<String> name();

    @NotNull
    IPrimitive<EndpointGroup> type();

    @NotNull
    IPrimitive<Boolean> isPredefined();

    @NotNull
    @Detached
    @MemberColumn(name = "rls")
    IList<CrmRole> roles();

    @Detached
    IList<Building> buildings();

    @Detached
    IList<Portfolio> portfolios();

}
