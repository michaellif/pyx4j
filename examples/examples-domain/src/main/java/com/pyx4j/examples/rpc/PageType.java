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
 * Created on Feb 9, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.rpc;

import com.pyx4j.site.shared.domain.ResourceUri;
import com.pyx4j.site.shared.util.ResourceUriUtil;

public enum PageType {

    //----pub-----//

    pub$home(ResourceUriUtil.createResourceUri(Sites.pub.name(), "home")),

    pub$home$results(ResourceUriUtil.createResourceUri(Sites.pub.name(), "home", "results")),

    pub$home$contactUs(ResourceUriUtil.createResourceUri(Sites.pub.name(), "contactUs")),

    pub$home$technicalSupport(ResourceUriUtil.createResourceUri(Sites.pub.name(), "home", "technicalSupport")),

    pub$home$privacyPolicy(ResourceUriUtil.createResourceUri(Sites.pub.name(), "home", "privacyPolicy")),

    pub$home$termsOfUse(ResourceUriUtil.createResourceUri(Sites.pub.name(), "home", "termsOfUse")),

    //----crm-----//

    employer$organizationProfile(ResourceUriUtil.createResourceUri(Sites.crm.name(), "organizationProfile")),

    employer$personalSettings(ResourceUriUtil.createResourceUri(Sites.crm.name(), "personalSettings")),

    employer$jobs(ResourceUriUtil.createResourceUri(Sites.crm.name(), "jobs")),

    employer$home$contactUs(ResourceUriUtil.createResourceUri(Sites.crm.name(), "home", "contactUs")),

    employer$home$technicalSupport(ResourceUriUtil.createResourceUri(Sites.crm.name(), "home", "technicalSupport")),

    employer$home$privacyPolicy(ResourceUriUtil.createResourceUri(Sites.crm.name(), "home", "privacyPolicy")),

    employer$home$termsOfUse(ResourceUriUtil.createResourceUri(Sites.crm.name(), "home", "termsOfUse")),

    //----headless-----//

    headless$headless$pageNotFound(ResourceUriUtil.createResourceUri(Sites.headless.name(), "headless", "pageNotFound")),

    headless$headless$password(ResourceUriUtil.createResourceUri(Sites.headless.name(), "headless", "password")),

    headless$headless$activation(ResourceUriUtil.createResourceUri(Sites.headless.name(), "headless", "activation")),

    headless$headless$contactUs(ResourceUriUtil.createResourceUri(Sites.headless.name(), "headless", "contactUs")),

    headless$headless$technicalSupport(ResourceUriUtil.createResourceUri(Sites.headless.name(), "headless", "technicalSupport")),

    headless$headless$privacyPolicy(ResourceUriUtil.createResourceUri(Sites.headless.name(), "headless", "privacyPolicy")),

    headless$headless$termsOfUse(ResourceUriUtil.createResourceUri(Sites.headless.name(), "headless", "termsOfUse")),

    ;

    private ResourceUri uri;

    public ResourceUri getUri() {
        return uri;
    }

    private PageType(ResourceUri uri) {
        this.uri = uri;
    }

}
