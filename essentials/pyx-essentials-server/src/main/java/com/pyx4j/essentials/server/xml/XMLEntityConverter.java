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
 * Created on 2010-12-29
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.xml;

import org.w3c.dom.Element;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.report.XMLStringWriter;

public class XMLEntityConverter {

    public static void write(XMLStringWriter xml, IEntity entity) {
        new XMLEntityWriter(xml).write(entity, entity.getEntityMeta().getEntityClass().getName());
    }

    public static <T extends IEntity> T pars(Element node) {
        return new XMLEntityParser().pars(node);
    }

}
