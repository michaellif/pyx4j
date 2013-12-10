/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Oct 21, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.adapters.FileUploadBlobSecurityAdapter;
import com.pyx4j.i18n.annotations.I18n;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@EmbeddedEntity
public interface IFile extends IEntity {

    @ToString(index = 0)
    IPrimitive<String> fileName();

    @MemberColumn(name = "updated_timestamp")
    IPrimitive<Long> timestamp();

    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    IPrimitive<Integer> cacheVersion();

    IPrimitive<Integer> fileSize();

    IPrimitive<String> contentMimeType();

    IPrimitive<String> caption();

    IPrimitive<String> description();

    /**
     * Used to access just upload files
     */
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    @Transient
    IPrimitive<String> accessKey();

    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    @MemberColumn(modificationAdapters = { FileUploadBlobSecurityAdapter.class })
    IPrimitive<Key> blobKey();
}
