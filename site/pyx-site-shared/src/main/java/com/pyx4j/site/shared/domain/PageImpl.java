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

public class PageImpl implements Page {

    private final String uri;

    protected String tabName;

    private final String caption;

    private final String discriminator;

    private boolean navigHidden;

    private final PageData pageData;

    public PageImpl(String caption, String uri, String discriminator, PageData pageData) {
        this.caption = caption;
        this.uri = uri;
        this.discriminator = discriminator;
        this.pageData = pageData;
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public String tabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    @Override
    public String caption() {
        return caption;
    }

    @Override
    public String discriminator() {
        return discriminator;
    }

    @Override
    public boolean navigHidden() {
        return navigHidden;
    }

    public void setNavigHidden() {
        navigHidden = true;
    }

    @Override
    public PageData data() {
        return pageData;
    }

}
