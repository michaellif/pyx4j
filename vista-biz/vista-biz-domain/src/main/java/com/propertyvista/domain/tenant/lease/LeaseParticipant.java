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

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.entity.shared.adapters.index.AlphanumIndexAdapter;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.note.HasNotesAndAttachments;
import com.propertyvista.domain.tenant.Customer;

@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface LeaseParticipant<E extends LeaseTermParticipant<?>> extends IEntity, HasNotesAndAttachments {

    @Owner
    @NotNull
    @ReadOnly
    @Detached
    @JoinColumn
    @MemberColumn(notNull = true)
    @Indexed(uniqueConstraint = true, group = { "discriminator+lc,1" })
    Lease lease();

    @NotNull
    @ReadOnly
    @ToString(index = 0)
    @MemberColumn(notNull = true)
    @Indexed(uniqueConstraint = true, group = { "discriminator+lc,2" })
    Customer customer();

    @NotNull
    @Length(14)
    @Caption(name = "Id")
    @Indexed
    @MemberColumn(sortAdapter = AlphanumIndexAdapter.class)
    IPrimitive<String> participantId();

    @Editor(type = EditorType.label)
    IPrimitive<String> yardiApplicantId();

    //TODO
    @Transient
    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = LeaseTermParticipant.class, mappedBy = LeaseTermParticipant.LeaseParticipantHolderId.class)
    ISet<E> leaseTermParticipants();
}
