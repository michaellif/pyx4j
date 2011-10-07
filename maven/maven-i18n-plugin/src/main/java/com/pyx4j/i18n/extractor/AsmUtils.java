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
 * Created on Oct 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.extractor;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

class AsmUtils {

    static String codeName(Class<?> klass) {
        return klass.getName().replace('.', '/');
    }

    static String annotationCodeName(Class<? extends Annotation> klass) {
        return "L" + klass.getName().replace('.', '/') + ";";
    }

    static String classSourceFileName(ClassNode classNode) {
        if (classNode.sourceFile != null) {
            return FilenameUtils.getPath(classNode.name) + classNode.sourceFile;
        } else {
            int nested = classNode.name.indexOf('$');
            if (nested == -1) {
                return classNode.name + ".java";
            } else {
                return classNode.name.substring(0, nested) + ".java";
            }
        }
    }

    static String getSimpleName(ClassNode classNode) {
        int nested = classNode.name.lastIndexOf('$');
        if (nested != -1) {
            return classNode.name.substring(nested + 1);
        } else {
            int pkg = classNode.name.lastIndexOf('/');
            return classNode.name.substring(pkg + 1);
        }
    }

    static boolean hasAnnotation(String annotationClassName, List<?> visibleAnnotations) {
        if (visibleAnnotations == null) {
            return false;
        }
        for (Object node : visibleAnnotations) {
            if ((node instanceof AnnotationNode) && (annotationClassName.equals(((AnnotationNode) node).desc))) {
                return true;
            }
        }
        return false;
    }

    static Object getAnnotationValue(String annotationClassName, String valueName, List<?> visibleAnnotations) {
        if (visibleAnnotations == null) {
            return null;
        }
        for (Object node : visibleAnnotations) {
            if (node instanceof AnnotationNode) {
                AnnotationNode anode = (AnnotationNode) node;
                if (annotationClassName.equals(anode.desc)) {
                    if (anode.values == null) {
                        return null;
                    } else {
                        @SuppressWarnings("unchecked")
                        Iterator<Object> it = anode.values.iterator();
                        while (it.hasNext()) {
                            Object name = it.next();
                            if (valueName.equals(name)) {
                                return it.next();
                            } else {
                                if (it.hasNext()) {
                                    it.next();
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
