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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityArgsConverter {

    public static final String DATE_TIME_FORMAT = "yyyyMMddHHmm";

    public static final String DATE_FORMAT = "yyyyMMdd";

    public static Map<String, String> convertToArgs(IEntity entity) {
        Map<String, String> map = new HashMap<String, String>();

        for (String memberName : entity.getEntityMeta().getMemberNames()) {
            IObject<?> member = entity.getMember(memberName);
            MemberMeta memberMeta = entity.getEntityMeta().getMemberMeta(memberName);
            if (!member.isNull()) {
                if (ObjectClassType.Primitive.equals(memberMeta.getObjectClassType())) {
                    if (memberMeta.getValueClass().equals(Date.class)) {
                        map.put(memberName, TimeUtils.simpleFormat((Date) entity.getMember(memberName).getValue(), DATE_TIME_FORMAT));
                    } else if (memberMeta.getValueClass().equals(LogicalDate.class)) {
                        map.put(memberName, TimeUtils.simpleFormat((Date) entity.getMember(memberName).getValue(), DATE_FORMAT));
                    } else if (memberMeta.getValueClass().isEnum()) {
                        map.put(memberName, ((Enum<?>) entity.getMember(memberName).getValue()).name());
                    } else {
                        map.put(memberName, entity.getMember(memberName).getValue().toString());
                    }
                } else if (ObjectClassType.Entity.equals(memberMeta.getObjectClassType())) {
                    IEntity nested = (IEntity) entity.getMember(memberName);
                    Map<String, String> nestedMap = convertToArgs(nested);
                    for (String nestedKey : nestedMap.keySet()) {
                        map.put(memberMeta.getFieldName() + "." + nestedKey, nestedMap.get(nestedKey));
                    }
                }
            }
        }
        return map;
    }

    public static <E extends IEntity> E createFromArgs(Class<E> clazz, Map<String, String> args) {

        E entity = EntityFactory.create(clazz);
        if (args != null) {
            for (String memberName : args.keySet()) {
                Path path = convertDotNotationToPath(clazz, memberName);
                MemberMeta memberMeta = entity.getEntityMeta().getMemberMeta(path);

                if (memberMeta != null) {
                    if (ObjectClassType.Primitive.equals(memberMeta.getObjectClassType())) {
                        IPrimitive<?> member = (IPrimitive<?>) entity.getMember(path);
                        if (memberMeta.getValueClass().equals(Date.class)) {
                            entity.setValue(path, TimeUtils.simpleParse(args.get(memberName), DATE_TIME_FORMAT));
                        } else if (memberMeta.getValueClass().equals(LogicalDate.class)) {
                            entity.setValue(path, new LogicalDate(TimeUtils.simpleParse(args.get(memberName), DATE_FORMAT)));
                        } else {
                            entity.setValue(path, member.parse(args.get(memberName)));
                        }
                    }
                }

            }
        }

        return entity;
    }

    public static String convertPathToDotNotation(Path path) {
        StringBuilder builder = new StringBuilder();
        for (String segment : path.getPathMembers()) {
            builder.append(segment).append('.');
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    public static Path convertDotNotationToPath(Class<? extends IObject<?>> root, String string) {
        string = GWTJava5Helper.getSimpleName(root) + Path.PATH_SEPARATOR + string.replace('.', Path.PATH_SEPARATOR);
        return new Path(string);
    }
}
