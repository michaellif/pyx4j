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
 * Created on Dec 2, 2010
 * @author Misha
 * @version $Id: code-templates.xml 4670 2010-01-10 07:33:42Z vlads $
 */
package com.pyx4j.entity.client.ui.crud;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class Decorator implements IPrimitive<String> {

    public Decorator() {

    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public void setValue(String value) throws ClassCastException {
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public Path getPath() {
        return null;
    }

    @Override
    public Class<? extends IObject> getObjectClass() {
        return null;
    }

    @Override
    public IEntity getOwner() {
        return null;
    }

    @Override
    public IObject<?> getParent() {
        return null;
    }

    @Override
    public String getFieldName() {
        return null;
    }

    @Override
    public MemberMeta getMeta() {
        return null;
    }

    @Override
    public boolean metaEquals(IObject<?> other) {
        return false;
    }

    @Override
    public String getStringView() {
        return null;
    }

    @Override
    public Class<String> getValueClass() {
        return null;
    }

    @Override
    public void set(IPrimitive<String> primitiveValue) {
    }

    @Override
    public String parse(String value) {
        return null;
    }

    @Override
    public boolean isBooleanTrue() {
        return false;
    }

    @Override
    public int compareTo(IPrimitive<String> o) {
        return 0;
    }

}