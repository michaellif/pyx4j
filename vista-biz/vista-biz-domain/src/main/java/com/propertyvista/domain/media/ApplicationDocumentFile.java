/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 3, 2014
 * @author VladL
 */
package com.propertyvista.domain.media;

import java.util.Date;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IHasFile;

import com.propertyvista.domain.blob.IFileBlob;
import com.propertyvista.domain.company.Employee;

@AbstractEntity
@ToStringFormat("{0} {1,choice,null#|!null#({1})}")
public interface ApplicationDocumentFile<D extends IFileBlob> extends IHasFile<D> {

    @OrderColumn
    IPrimitive<Integer> orderInOwner();

    @Override
    @EmbeddedEntity
    @NotNull
    @ToString(index = 0)
    IFile<D> file();

    @ToString(index = 1)
    IPrimitive<String> description();

// ----------------------------------

    @ToString(index = 2)
    IPrimitive<Boolean> verified();

    @Editor(type = EditorType.label)
    Employee verifiedBy();

    @Editor(type = EditorType.label)
    IPrimitive<Date> verifiedOn();

    IPrimitive<String> notes();
}
