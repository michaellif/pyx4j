/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-23
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.pmc.info;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.VistaNamespace;

@Table(namespace = VistaNamespace.operationsNamespace)
public interface PmcPersonalInformationDocument extends PmcDocument {

    public enum Type {

        DriversLicense,

        Passport; // TODO get the rest from Leonard

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };

    }

    @MemberColumn(name = "personal_info_doc_type")
    IPrimitive<Type> type();

    @Owner
    @Detached
    @ReadOnly
    @JoinColumn
    PersonalInformation owner();

    @OrderColumn
    IPrimitive<Integer> ownersOrder();
}
