/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.pmc.info;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.VistaNamespace;

@Table(namespace = VistaNamespace.operationsNamespace)
public interface BusinessInformation extends IEntity {

    @I18n(context = "Company Type")
    @XmlType(name = "CompanyType")
    public enum CompanyType {

        SoleProprietorship, Partnership, Corporation, Cooperative;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };

    }

    @NotNull
    IPrimitive<String> companyName();

    // TODO not sure that this is actually what was the poet's intention in the specs...
    // TODO also I'm suspecting that the set acceptable values can change based on the country
    @NotNull
    IPrimitive<CompanyType> companyType();

    @NotNull
    @EmbeddedEntity
    PmcAddressSimple businessAddress();

    @Caption(name = "Business Number / Employer Identification")
    IPrimitive<String> businessNumber();

    IPrimitive<LogicalDate> businessEstablishedDate();

    @Owned
    IList<PmcBusinessInfoDocument> documents();

}
