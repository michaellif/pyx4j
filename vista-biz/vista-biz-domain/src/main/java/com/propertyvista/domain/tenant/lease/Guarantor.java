/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;


@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@DiscriminatorValue("Guarantor")
public interface Guarantor extends LeaseParticipant<LeaseTermGuarantor> {

    //TODO move
    @Override
    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = LeaseTermGuarantor.class, mappedBy = LeaseTermParticipant.LeaseParticipantHolderId.class)
    ISet<LeaseTermGuarantor> leaseTermParticipants();
}
