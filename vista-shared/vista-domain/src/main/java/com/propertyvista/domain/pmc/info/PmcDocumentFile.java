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

import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IFile;

import com.propertyvista.domain.VistaNamespace;

/** Holds the metadata of a file and the key of Blob */
@Table(namespace = VistaNamespace.operationsNamespace)
public interface PmcDocumentFile extends IFile {

}
