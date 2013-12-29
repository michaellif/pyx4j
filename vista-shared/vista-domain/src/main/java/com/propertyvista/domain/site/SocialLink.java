/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 28, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.site;

import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface SocialLink extends IEntity {
    public enum SocialSite {
        Facebook, Twitter, Youtube, Flickr
    }

    IPrimitive<SocialSite> socialSite();

    @ToString
    IPrimitive<String> siteUrl();
}
