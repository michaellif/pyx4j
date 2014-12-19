/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2012
 * @author vlads
 */
package com.propertyvista.domain.site;

import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.IHasFile;

import com.propertyvista.domain.blob.MediaFileBlob;

public interface SiteImageResource extends IHasFile<MediaFileBlob> {

    IPrimitive<String> caption();

    IPrimitive<String> description();

}
