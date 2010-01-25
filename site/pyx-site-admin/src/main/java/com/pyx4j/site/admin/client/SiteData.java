/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 25, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.admin.client;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.site.shared.domain.Site;

public class SiteData {

    private static Logger log = LoggerFactory.getLogger(SiteData.class);

    private Vector<Site> sites;

    public SiteData() {
    }

    public void setSites(Vector<Site> sites) {
        this.sites = sites;
        log.info(sites.toString());
    }

}
