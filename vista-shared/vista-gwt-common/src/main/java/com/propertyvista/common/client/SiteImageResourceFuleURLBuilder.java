/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 17, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.shared.IFileURLBuilder;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.domain.site.SiteImageResource;

public class SiteImageResourceFuleURLBuilder implements IFileURLBuilder {

    @Override
    public String getUrl(IFile<?> file) {
        return MediaUtils.createSiteImageResourceUrl((SiteImageResource) file.getOwner());
    }

}
