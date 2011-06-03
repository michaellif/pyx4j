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
 * Created on Jun 3, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityArgsConverter {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");

    public static Map<String, String> convertToArgs(IEntity entity) {
        Map<String, String> map = new HashMap<String, String>();
        entity.getValue().keySet();
        for (String memberName : entity.getValue().keySet()) {
            MemberMeta memberMeta = entity.getEntityMeta().getMemberMeta(memberName);
            if (ObjectClassType.Primitive.equals(memberMeta.getObjectClassType())) {
                if (memberMeta.getValueClass().equals(Date.class) || (memberMeta.getValueClass().equals(java.sql.Date.class))
                        || (memberMeta.getValueClass().equals(LogicalDate.class))) {
                    map.put(memberName, DATE_FORMAT.format((Date) entity.getMember(memberName).getValue()));
                } else {
                    map.put(memberName, entity.getMember(memberName).getValue().toString());
                }
            }

        }
        return map;
    }

    public static <E extends IEntity> E createFromArgs(Class<E> clazz, Map<String, String> args) {

        E entity = EntityFactory.create(clazz);

        List<String> memberNames = entity.getEntityMeta().getMemberNames();

        if (memberNames == null) {
            return entity;
        }

        for (String memberName : args.keySet()) {
            if (memberNames.contains(memberName)) {
                MemberMeta memberMeta = entity.getEntityMeta().getMemberMeta(memberName);
                IObject<?> member = entity.getMember(memberName);
                if (ObjectClassType.Primitive.equals(memberMeta.getObjectClassType())) {
                    if (memberMeta.getValueClass().equals(Date.class) || (memberMeta.getValueClass().equals(java.sql.Date.class))
                            || (memberMeta.getValueClass().equals(LogicalDate.class))) {

                        try {
                            ((IPrimitive<Date>) member).setValue(DATE_FORMAT.parse(args.get(memberName)));
                        } catch (ClassCastException e) {
                            throw new Error(e);
                        } catch (ParseException e) {
                            throw new Error(e);
                        }
                    } else {
                        ((IPrimitive) member).setValue(((IPrimitive) member).parse(args.get(memberName)));
                    }
                }
            }
        }

        return entity;
    }
}
