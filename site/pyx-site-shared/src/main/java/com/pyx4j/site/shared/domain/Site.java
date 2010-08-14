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
 * Created on Dec 28, 2009
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.shared.domain;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

public interface Site extends IEntity {

    IPrimitive<Long> updateTimestamp();

    @NotNull
    @Caption(name = "Site Id")
    IPrimitive<String> siteId();

    @NotNull
    @Caption(name = "Caption")
    IPrimitive<String> siteCaption();

    @NotNull
    @Caption(name = "Logo Url")
    IPrimitive<String> logoUrl();

    @NotNull
    @Caption(name = "Skin Type")
    IPrimitive<String> skinType();

    @Owned
    IList<Link> headerLinks();

    @Owned
    IList<Link> footerLinks();

    @Caption(name = "Footer Copyright")
    IPrimitive<String> footerCopyright();

    @Owned
    IList<Page> pages();

}