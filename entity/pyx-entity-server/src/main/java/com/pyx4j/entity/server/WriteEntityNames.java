/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-10-19
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.gwt.server.IOUtils;

public class WriteEntityNames {

    enum WriteParam {

        persistable,

        all;

    }

    public static void main(String[] args) throws IOException {

        WriteParam param = WriteParam.persistable;

        Collection<String> words = new HashSet<String>();

        for (Class<? extends IEntity> entityClass : ServerEntityFactory.getAllEntityClasses()) {
            if (accept(entityClass, param)) {
                extract(entityClass, param, words);
            }
        }

        write(new File(param.name() + ".txt"), words);
    }

    private static void write(File file, Collection<String> words) throws IOException {
        PrintWriter writer = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            writer = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
            for (String word : words) {
                writer.write(word);
                writer.println();
            }
            writer.flush();
            writer.close();
            System.out.println("File " + file.getAbsolutePath() + " created");
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private static boolean accept(Class<? extends IEntity> entityClass, WriteParam param) {
        switch (param) {
        case persistable:
            EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
            return (!meta.isTransient() && (entityClass.getAnnotation(AbstractEntity.class) == null) && (entityClass.getAnnotation(EmbeddedEntity.class) == null));
        case all:
            return true;
        default:
            return false;
        }
    }

    private static boolean accept(MemberMeta memberMeta, WriteParam param) {
        switch (param) {
        case persistable:
            return !memberMeta.isTransient();
        case all:
            return true;
        default:
            return false;
        }
    }

    private static void extract(Class<? extends IEntity> entityClass, WriteParam param, Collection<String> words) {
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        extract(entityMeta.getPersistenceName(), param, words);

        for (String memberName : entityMeta.getMemberNames()) {
            MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
            if (accept(memberMeta, param)) {
                extract(memberMeta.getFieldName(), param, words);

                if (memberMeta.isEmbedded()) {
                    if (memberMeta.getObjectClassType() == ObjectClassType.Entity) {
                        @SuppressWarnings("unchecked")
                        Class<? extends IEntity> entityClass2 = (Class<IEntity>) memberMeta.getObjectClass();
                        extract(entityClass2, param, words);
                    }
                }
            }
        }
    }

    private static void extract(String name, WriteParam param, Collection<String> words) {
        StringBuilder currentWord = new StringBuilder();
        boolean wordStart = true;
        for (char c : name.toCharArray()) {
            if (c == '_' || c == '$') {
                if (currentWord.length() > 0) {
                    words.add(currentWord.toString());
                    currentWord = new StringBuilder();
                    wordStart = true;
                }
            } else if (Character.isUpperCase(c)) {
                if (!wordStart) {
                    words.add(currentWord.toString());
                    currentWord = new StringBuilder();
                    wordStart = true;
                }
                currentWord.append(Character.toLowerCase(c));
            } else {
                wordStart = false;
                currentWord.append(c);
            }
        }

        if (currentWord.length() > 0) {
            words.add(currentWord.toString());
        }
    }
}
