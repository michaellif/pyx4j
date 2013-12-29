/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.resident.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.site.HtmlContent;
import com.propertyvista.domain.site.PortalBannerImage;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.domain.site.SitePalette;
import com.propertyvista.domain.site.SocialLink;

@Transient
public interface PortalContentDTO extends IEntity {

    IPrimitive<Skin> skin();

    SitePalette sitePalette();

    SiteImageResource logoSmall();

    SiteImageResource logoLarge();

    HtmlContent pmcInfo();

    PortalBannerImage portalBanner();

    IList<SocialLink> socialLinks();
}
