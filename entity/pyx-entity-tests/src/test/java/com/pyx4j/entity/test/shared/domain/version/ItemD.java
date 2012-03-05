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
 * Created on Feb 6, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain.version;

import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.entity.test.shared.domain.version.ItemA.ItemAVersion;
import com.pyx4j.entity.test.shared.domain.version.ItemD.ItemDVersion;

@Table(prefix = "test")
public interface ItemD extends IVersionedEntity<ItemDVersion> {

    IPrimitive<String> testId();

    IPrimitive<String> name();

    public interface ItemDVersion extends ItemAVersion {

        @Override
        IPrimitive<String> testId();

        @Override
        IPrimitive<String> name();

        @Owned
        IList<ItemC> itemCOwned();
    }

}
