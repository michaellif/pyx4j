/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Oct 27, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.datatable;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public class MemberPrimitiveDateColumnDescriptor extends MemberColumnDescriptor {

    private String format;

    protected MemberPrimitiveDateColumnDescriptor(Builder builder, String format) {
        super(builder);
        this.format = format;
    }

    @Override
    public String convert(IEntity entity) {
        Date value = (Date) ((IPrimitive<?>) entity.getMember(getColumnPath())).getValue();
        if (value == null) {
            return "";
        } else {
            return DateTimeFormat.getFormat(format).format(value);
        }
    }

}
