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
 * Created on 2010-11-12
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.shared.domain;

import java.util.List;
import java.util.Vector;

public class SiteImpl implements Site {

    protected String siteId;

    protected String siteCaption;

    protected String logoUrl;

    protected String skinType;

    //protected List<Link> headerLinks;

    //protected List<Link> footerLinks;

    protected String footerCopyright;

    protected List<Page> pages = new Vector<Page>();

    public SiteImpl(String siteId, String caption, String footerCopyright) {
        this.siteId = siteId;
        this.siteCaption = caption;
        this.footerCopyright = footerCopyright;
    }

    @Override
    public String siteId() {
        return siteId;
    }

    @Override
    public String siteCaption() {
        return siteCaption;
    }

    @Override
    public String logoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    @Override
    public String skinType() {
        return skinType;
    }

    @Override
    public List<Link> headerLinks() {
        return null;
    }

    @Override
    public List<Link> footerLinks() {
        return null;
    }

    @Override
    public String footerCopyright() {
        return footerCopyright;
    }

    @Override
    public List<Page> pages() {
        return pages;
    }

}
