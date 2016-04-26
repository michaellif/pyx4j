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
 * Created on Apr 22, 2016
 * @author vlads
 */
package com.pyx4j.entity.ognl;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.googlecode.jatl.Html;

import com.pyx4j.entity.annotations.ognl.OGNLModel;
import com.pyx4j.entity.annotations.ognl.OGNLModelColumn;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.shared.utils.EntityMetaUtils;
import com.pyx4j.entity.shared.utils.EntityMetaUtils.MemberAcceptanceFilter;

public class OGNLModelGraph {

    public static <E extends OGNLModel & IEntity> List<String> listVariables(final Class<E> modelClass) {
        return listVariables(modelClass, new OGNLPathToVariableName());
    }

    public static <E extends OGNLModel & IEntity> List<String> listVariables(final Class<E> modelClass, final PathToVariableName namesFormatter) {
        final List<String> variables = new ArrayList<>();
        apply(modelClass, new OGNLMemberFormatter() {

            @Override
            public void append(IEntity proto, IObject<?> member, Path memberPath, MemberMeta memberMeta) {
                variables.add(namesFormatter.pathToVarname(memberPath));
            }
        });
        return variables;
    }

    public static <E extends OGNLModel & IEntity> String createThymeleafExample(final Class<E> modelClass) {
        StringWriter stringWriter = new StringWriter();
        final Html html = new Html(stringWriter);

        final PathToVariableName namesFormatter = new OGNLPathToVariableName();

        Stack<String> arrays = new Stack<>();

        apply(modelClass, new OGNLMemberFormatter() {

            @Override
            public void append(IEntity proto, IObject<?> member, Path memberPath, MemberMeta memberMeta) {
                String name = namesFormatter.pathToVarname(memberPath);

                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    arrays.push(name.substring(0, name.length() - 1)); // remove last } from name
                    // Start array
                    html.b().text("listing  of " + member.getFieldName()).end();
                    html.table().tr().attr("th:each", "item : " + name);
                    return;
                } else if ((arrays.size() > 0) && !name.startsWith(arrays.peek())) {
                    // End of member of array
                    arrays.pop();
                    html.end().end();
                    // Fallback to default print
                } else if (arrays.size() > 0) {
                    // change the name to start with 'item'
                    name = "${item." + name.replace(arrays.peek() + "[0].", "");
                }

                //TODO Something more creative can be done here.

                html.span().text(name + " = ").end().span().attr("th:text", name).text(name).end().br();
            }
        });

        return stringWriter.getBuffer().toString();
    }

    public static <E extends OGNLModel & IEntity> void apply(final Class<E> modelClass, OGNLMemberFormatter ognlMemebeFormater) {
        EntityMetaUtils.getMembers(modelClass, new OGNLMembersFilter(modelClass, ognlMemebeFormater));
    }

    private static class OGNLMembersFilter implements MemberAcceptanceFilter {

        final Class<? extends OGNLModel> modelClass;

        final OGNLMemberFormatter ognlMemebeFormater;

        public OGNLMembersFilter(Class<? extends OGNLModel> modelClass, OGNLMemberFormatter ognlMemebeFormater) {
            this.modelClass = modelClass;
            this.ognlMemebeFormater = ognlMemebeFormater;
        }

        @Override
        public Acceptance acceptMember(IEntity proto, IObject<?> member, Path memberPath, MemberMeta memberMeta) {
            // TODO this is just magic number today
            if (memberPath.getPathMembers().size() > 11) {
                System.err.println("ODNL Model is unstable path is ignored " + memberPath);
                return Acceptance.Reject;
            }

            Acceptance acceptance = Acceptance.Accept;
            for (OGNLModelColumn ognlModelColumn : memberMeta.getAnnotations(OGNLModelColumn.class)) {
                if ((ognlModelColumn.model() != OGNLModel.class) && (ognlModelColumn.model() != modelClass)) {
                    continue;
                }
                if (ognlModelColumn.hide()) {
                    acceptance = Acceptance.Reject;
                }
            }

            if (acceptance != Acceptance.Reject) {
                if (memberMeta.isOwner()) {
                    acceptance = Acceptance.Reject;
                } else {
                    switch (memberMeta.getObjectClassType()) {
                    case Entity:
                    case EntityList:
                    case EntitySet:
                        switch (memberMeta.getAttachLevel()) {
                        case Attached:
                            acceptance = Acceptance.AcceptRecursively;
                            break;
                        case ToStringMembers:
                            break;
                        default:
                            acceptance = Acceptance.Reject;
                            break;
                        }
                    default:
                        break;
                    }
                }
            }

            if (acceptance != Acceptance.Reject) {
                ognlMemebeFormater.append(proto, member, memberPath, memberMeta);
            }

            return acceptance;
        }

    }
}
