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
 * Created on 2011-03-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.xml;

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.xml.XMLEntityNamingConvention;

public class XMLEntityNamingConventionDefault implements XMLEntityNamingConvention {

    @Override
    public String getXMLName(@SuppressWarnings("rawtypes") Class<? extends IObject> memberClass) {
        return EnglishGrammar.deCapitalize(memberClass.getSimpleName());
    }

}
