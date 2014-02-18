/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-02-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.dto;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

@Transient
public interface LeaseAgreementStakeholderSigningProgressDTO extends IEntity {

    @I18n(context = "Signature Type")
    @XmlType(name = "SignatureType")
    public enum SignatureType {

        Digital, Ink;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };
    }

    CrmUser stakeholderUser();

    LeaseTermParticipant<?> stakeholderLeaseParticipant();

    IPrimitive<String> name();

    IPrimitive<String> role();

    IPrimitive<Boolean> hasSigned();

    IPrimitive<SignatureType> singatureType();

}
