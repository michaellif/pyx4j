/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal;

import java.util.Date;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Inheritance.InheritanceStrategy;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.IHasFile;

import com.propertyvista.domain.blob.LegalLetterBlob;
import com.propertyvista.domain.tenant.lease.Lease;

@AbstractEntity
@Inheritance(strategy = InheritanceStrategy.SINGLE_TABLE)
public interface LegalLetter extends IHasFile<LegalLetterBlob> {

    @Detached
    @MemberColumn(notNull = true)
    Lease lease();

    @Detached
    LegalStatus status();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> notes();

    @MemberColumn(notNull = true)
    @ReadOnly
    IPrimitive<Date> generatedOn();

}
