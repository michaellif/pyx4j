/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 2, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.domain.note;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.security.CrmUser;

@ToStringFormat("\"{0}\", by {1}; last modified {2}")
public interface NotesAndAttachments extends IEntity {

    @Indexed(group = { "o,1" })
    @I18n(strategy = I18n.I18nStrategy.IgnoreMember)
    IPrimitive<Key> ownerId();

    @Indexed(group = { "o,2" })
    @I18n(strategy = I18n.I18nStrategy.IgnoreMember)
    @Length(80)
    IPrimitive<String> ownerClass();

    @Detached
    @ReadOnly
    //@MemberColumn(name = "owner2")
    HasNotesAndAttachments owner();

    @NotNull
    @Length(128)
    @ToString(index = 0)
    IPrimitive<String> subject();

    @Length(20845)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> note();

    // TODO Removed for 1.05
    // @see VistaTODO.VISTA_2127_Attachments_For_Notes
    @Transient
    @Owned
    IList<NoteAttachment> attachments();

    @ToString(index = 1)
    @MemberColumn(name = "crmuser")
    @ReadOnly
    CrmUser user();

    @ToString(index = 2)
    @Caption(name = "Last Updated")
    @Timestamp
    IPrimitive<LogicalDate> updated();

    IPrimitive<LogicalDate> created();
}
