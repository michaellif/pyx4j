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
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

public interface IPrimitive<TYPE> extends IObject<IPrimitive<TYPE>, TYPE> {

    public Class<TYPE> getValueClass();

    //TODO
    //public void set(TYPE primitiveValue);

    /**
     * TODO Business toString() presentation. @see com.pyx4j.entity.annotations.Format
     * 
     * @return String value of member formated using annotation @Format
     */
    //public String getStringView();
}
