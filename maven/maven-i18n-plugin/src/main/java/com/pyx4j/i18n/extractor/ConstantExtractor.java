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
 * Created on Oct 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.i18n.shared.Translatable;
import com.pyx4j.i18n.shared.Translation;

public class ConstantExtractor {

    static String ENUM_CLASS = AsmUtils.codeName(Enum.class);

    static String TRANSLATABLE_CLASS = AsmUtils.annotationCodeName(Translatable.class);

    static String TRANSLATION_CLASS = AsmUtils.annotationCodeName(Translation.class);

    private final Map<String, ConstantEntry> constants = new HashMap<String, ConstantEntry>();

    private static class LineNumberAnalyzer extends Analyzer {

        private final I18nConstantsInterpreter interpreter;

        public LineNumberAnalyzer(I18nConstantsInterpreter interpreter) {
            super(interpreter);
            this.interpreter = interpreter;
        }

        protected MethodNode currentMethodNode;

        @Override
        public Frame[] analyze(final String owner, final MethodNode m) throws AnalyzerException {
            currentMethodNode = m;
            return super.analyze(owner, m);
        }

        @Override
        protected void newControlFlowEdge(int instructionIndex, int nextInstructionIndex) {
            AbstractInsnNode insnNode = currentMethodNode.instructions.get(instructionIndex);
            if (insnNode instanceof LineNumberNode) {
                interpreter.setCurrentLineNr(((LineNumberNode) insnNode).line);
            }
        }
    }

    public ConstantExtractor() {

    }

    public Collection<ConstantEntry> getConstants() {
        return constants.values();
    }

    public void readClass(InputStream in) throws IOException, AnalyzerException {
        ClassReader classReader = new ClassReader(in);
        final ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        final String classSourceFileName;
        if (classNode.sourceFile != null) {
            classSourceFileName = FilenameUtils.getPath(classNode.name) + classNode.sourceFile;
        } else {
            int nested = classNode.name.indexOf('$');
            if (nested == -1) {
                classSourceFileName = classNode.name + ".java";
            } else {
                classSourceFileName = classNode.name.substring(0, nested) + ".java";
            }
        }

        I18nConstantsInterpreter interpreter = new I18nConstantsInterpreter() {

            @Override
            protected void i18nString(int lineNr, String text) {
                addEntry(classSourceFileName, lineNr, text);
            }
        };

        Analyzer analyzer = new LineNumberAnalyzer(interpreter);

        for (@SuppressWarnings("rawtypes")
        Iterator i = classNode.methods.iterator(); i.hasNext();) {
            MethodNode methodNode = (MethodNode) i.next();
            analyzer.analyze(classNode.name, methodNode);
        }

        if (ENUM_CLASS.equals(classNode.superName)) {
            translateEnumClass(classSourceFileName, classNode);
        }
    }

    public void addEntry(String classSourceFileName, int lineNr, String text) {
        ConstantEntry entry = constants.get(text);
        if (entry == null) {
            constants.put(text, new ConstantEntry(classSourceFileName, lineNr, text));
        } else {
            entry.addReference(classSourceFileName, lineNr);
        }
    }

    private void translateEnumClass(String classSourceFileName, ClassNode classNode) {
        boolean translationPresent = AsmUtils.hasAnnotation(TRANSLATABLE_CLASS, classNode.visibleAnnotations);

        if (!translationPresent) {
            for (@SuppressWarnings("rawtypes")
            Iterator i = classNode.fields.iterator(); i.hasNext();) {
                FieldNode fieldNode = (FieldNode) i.next();
                translationPresent |= AsmUtils.hasAnnotation(TRANSLATION_CLASS, fieldNode.visibleAnnotations);
            }
        }

        if (!translationPresent) {
            return;
        }

        boolean capitalize = true;
        if (Boolean.FALSE.equals(AsmUtils.getAnnotationValue(TRANSLATABLE_CLASS, "capitalize", classNode.visibleAnnotations))) {
            capitalize = false;
        }

        for (@SuppressWarnings("rawtypes")
        Iterator i = classNode.fields.iterator(); i.hasNext();) {
            FieldNode fieldNode = (FieldNode) i.next();
            if ("ENUM$VALUES".equals(fieldNode.name)) {
                continue;
            }

            Object translationValue = AsmUtils.getAnnotationValue(TRANSLATION_CLASS, "value", fieldNode.visibleAnnotations);
            if (translationValue != null) {
                addEntry(classSourceFileName, 0, translationValue.toString());
            } else {
                if (capitalize) {
                    addEntry(classSourceFileName, 0, EnglishGrammar.capitalize(fieldNode.name));
                } else {
                    addEntry(classSourceFileName, 0, fieldNode.name);
                }
            }

        }
    }

    public void analyzeTranslatableHierarchy() {
        // TODO Auto-generated method stub
    }
}
