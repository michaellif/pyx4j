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
 * Created on Nov 3, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client.domain;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface CComponentProperties extends IEntity {

    @Editor(type = Editor.EditorType.label)
    IPrimitive<String> title();

    @Editor(type = Editor.EditorType.label)
    IPrimitive<String> componentValue();

    IPrimitive<Boolean> mandatory();

    IPrimitive<Boolean> enabled();

    IPrimitive<Boolean> editable();

    IPrimitive<Boolean> visible();

    IPrimitive<Boolean> valid();

    IPrimitive<String> toolTip();

    @Editor(type = Editor.EditorType.label)
    IPrimitive<String> adapters();

}
