/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 24, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.tenant.PersonRelationship;

@ToStringFormat("{0} - {1}")
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@DiscriminatorValue("Guarantor")
public interface LeaseTermGuarantor extends LeaseTermParticipant<Guarantor> {

    @NotNull
    @Caption(description = "Relation to the Tenant")
    IPrimitive<PersonRelationship> relationship();

    /**
     * Who invited this Guarantor to lease
     */
    @Caption(name = "Referred by Tenant")
    Tenant tenant();
}
