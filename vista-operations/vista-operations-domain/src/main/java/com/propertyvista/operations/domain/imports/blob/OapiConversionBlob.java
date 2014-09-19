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
package com.propertyvista.operations.domain.imports.blob;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.blob.IFileBlob;

@Table(namespace = VistaNamespace.operationsNamespace)
public interface OapiConversionBlob extends IFileBlob {
    /**
     * This is actual BLOB of the excel file stored on server
     */
    @Override
    @RpcTransient
    @Length(15 * 1024 * 1024)
    IPrimitive<byte[]> data();
}
