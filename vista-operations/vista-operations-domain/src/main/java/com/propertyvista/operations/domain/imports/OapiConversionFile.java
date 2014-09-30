/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.operations.domain.imports;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IHasFile;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.operations.domain.imports.blob.OapiConversionBlob;

@Table(namespace = VistaNamespace.operationsNamespace)
public interface OapiConversionFile extends IHasFile<OapiConversionBlob> {

    @I18n
    public enum OapiConversionFileType {

        BuildingIO,

        AnotherIO

        ;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

    }

    @Owner
    @Detached
    @JoinColumn
    @ReadOnly
    OapiConversion oapi();

    @ToString
    @Caption(name = "File type")
    @NotNull
    @MemberColumn(name = "tp")
    IPrimitive<OapiConversionFileType> type(); //TODO define type for possible entity XLS files

    @Override
    @EmbeddedEntity
    @NotNull
    IFile<OapiConversionBlob> file();
}
