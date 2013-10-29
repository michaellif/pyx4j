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

import com.pyx4j.entity.annotations.RequireFeature;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IFile;

import com.propertyvista.misc.ApplicationFeaturesAttachment;

//TODO implement Attachments
@RequireFeature(ApplicationFeaturesAttachment.class)
@Transient
public interface NoteAttachment extends IFile {

}
