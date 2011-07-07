/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 28, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.common.domain.media;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface File extends IEntity {

    IPrimitive<String> caption();

    IPrimitive<String> accessKey();

    IPrimitive<Key> blobKey();

    IPrimitive<Integer> cacheVersion();

    IPrimitive<String> filename();

    IPrimitive<Integer> fileSize();

    IPrimitive<String> contentType();

}
