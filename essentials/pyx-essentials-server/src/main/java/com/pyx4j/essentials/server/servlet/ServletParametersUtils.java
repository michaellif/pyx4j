/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Sep 12, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server.servlet;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;

public class ServletParametersUtils {

    public static void help(ServletTextOutput out, IEntity parametersDefaults) throws IOException {
        out.html("<table>");

        EntityMeta em = parametersDefaults.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);

            out.html("<tr><td><a href=\"");
            out.html("?", memberMeta.getFieldName(), "=", String.valueOf(parametersDefaults.getMember(memberName).getValue()), "\">");
            out.html(memberMeta.getFieldName(), "=", String.valueOf(parametersDefaults.getMember(memberName).getValue()));
            out.html("</a></td><td>", memberMeta.getDescription());
            out.html("</td></tr>");
        }

        out.html("</table>");
    }

    public static void get(HttpServletRequest request, IEntity parameters) {
        EntityMeta em = parameters.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (ObjectClassType.Primitive.equals(memberMeta.getObjectClassType())) {
                @SuppressWarnings("unchecked")
                IPrimitive<Serializable> member = (IPrimitive<Serializable>) parameters.getMember(memberName);
                String paramValue = request.getParameter(memberName);
                if (CommonsStringUtils.isStringSet(paramValue)) {
                    member.setValue(member.parse(paramValue));
                }
            }
        }

    }
}
