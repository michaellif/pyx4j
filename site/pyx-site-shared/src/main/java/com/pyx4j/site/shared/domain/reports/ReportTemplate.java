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
 * Created on Jul 27, 2012
 * @author ArtyomB
 */
package com.pyx4j.site.shared.domain.reports;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@AbstractEntity
@ToStringFormat(value = "{0,choice,null#Untitled|!null#{0}}", nil = "Report")
public interface ReportTemplate extends IEntity {

    @ToString(index = 0)
    IPrimitive<String> reportTemplateName();
}
