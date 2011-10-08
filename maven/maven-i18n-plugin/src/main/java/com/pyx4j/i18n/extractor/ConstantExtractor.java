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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MemberNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.i18n.annotations.I18nAnnotation;
import com.pyx4j.i18n.shared.Translatable;
import com.pyx4j.i18n.shared.Translatable.I18nStrategy;
import com.pyx4j.i18n.shared.Translation;

public class ConstantExtractor {

    static String ENUM_CLASS = AsmUtils.codeName(Enum.class);

    static String TRANSLATABLE_CLASS = AsmUtils.annotationCodeName(Translatable.class);

    static String TRANSLATION_CLASS = AsmUtils.annotationCodeName(Translation.class);

    static String TRANSLATABLE_ANNOTATION_CLASS = AsmUtils.annotationCodeName(I18nAnnotation.class);

    private final Map<String, ConstantEntry> constants = new HashMap<String, ConstantEntry>();

    private final Collection<ClassNode> translatableSuper = new Vector<ClassNode>();

    private final Map<String, I18nAnnotationDefintition> isTranslations = new HashMap<String, I18nAnnotationDefintition>();

    private final Collection<ClassNode> allClasses = new Vector<ClassNode>();

    private static class I18nAnnotationDefintition {

        String mainElement;

        List<String> elements;

        I18nAnnotationDefintition(AnnotationNode anode, ClassNode classNode) {
            mainElement = (String) AsmUtils.getAnnotationValue(anode, "element");
            elements = new Vector<String>();
            for (@SuppressWarnings("rawtypes")
            Iterator i = classNode.methods.iterator(); i.hasNext();) {
                MethodNode methodNode = (MethodNode) i.next();
                if (!methodNode.name.equals(mainElement)) {
                    if (AsmUtils.hasAnnotation(TRANSLATABLE_CLASS, methodNode)) {
                        elements.add(methodNode.name);
                    }
                }
            }
        }
    }

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

    public Collection<String> getConstantsText() {
        Collection<String> r = new HashSet<String>();
        for (ConstantEntry entry : this.getConstants()) {
            r.add(entry.text);
        }
        return r;
    }

    public void readClass(Class<?> in) throws IOException, AnalyzerException {
        InputStream is;
        is = this.getClass().getResourceAsStream("/" + AsmUtils.codeName(in) + ".class");
        try {
            this.readClass(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public void readClass(InputStream in) throws IOException, AnalyzerException {
        ClassReader classReader = new ClassReader(in);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        final String classSourceFileName = AsmUtils.classSourceFileName(classNode);

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
        } else {
            I18nStrategy strategy = getStrategyAnnotationValue(classNode);
            if (I18nStrategy.DerivedOnly == strategy) {
                translatableSuper.add(classNode);
            } else {
                AnnotationNode anode = AsmUtils.getAnnotation(TRANSLATABLE_ANNOTATION_CLASS, classNode);
                if (anode != null) {
                    isTranslations.put("L" + classNode.name + ";", new I18nAnnotationDefintition(anode, classNode));
                } else {
                    if (allClasses.contains(classNode)) {
                        throw new Error(classNode.name + " " + System.identityHashCode(classNode));
                    }
                    allClasses.add(classNode);
                }
            }
        }
    }

    public void addEntry(String classSourceFileName, int lineNr, String text) {
        if (text.length() != 0) {
            ConstantEntry entry = constants.get(text);
            if (entry == null) {
                constants.put(text, new ConstantEntry(classSourceFileName, lineNr, text));
            } else {
                entry.addReference(classSourceFileName, lineNr);
            }
        }
    }

    I18nStrategy getStrategyAnnotationValue(MemberNode memberNode) {
        Object strategy = AsmUtils.getAnnotationValue(TRANSLATABLE_CLASS, "strategy", memberNode);
        if (strategy != null) {
            return I18nStrategy.valueOf(((String[]) strategy)[1]);
        } else {
            return null;
        }
    }

    private void translateEnumClass(String classSourceFileName, ClassNode classNode) {
        boolean translationPresent = AsmUtils.hasAnnotation(TRANSLATABLE_CLASS, classNode);

        if (!translationPresent) {
            for (@SuppressWarnings("rawtypes")
            Iterator i = classNode.fields.iterator(); i.hasNext();) {
                FieldNode fieldNode = (FieldNode) i.next();
                translationPresent |= AsmUtils.hasAnnotation(TRANSLATION_CLASS, fieldNode);
            }
        }

        if (!translationPresent) {
            return;
        }

        boolean capitalize = true;
        if (Boolean.FALSE.equals(AsmUtils.getAnnotationValue(TRANSLATABLE_CLASS, "capitalize", classNode))) {
            capitalize = false;
        }

        for (@SuppressWarnings("rawtypes")
        Iterator i = classNode.fields.iterator(); i.hasNext();) {
            FieldNode fieldNode = (FieldNode) i.next();
            if ((Opcodes.ACC_ENUM & fieldNode.access) == 0) {
                continue;
            }

            Object translationValue = AsmUtils.getAnnotationValue(TRANSLATION_CLASS, "value", fieldNode);
            if (translationValue != null) {
                addEntry(classSourceFileName, -10, translationValue.toString());
            } else {
                if (capitalize) {
                    addEntry(classSourceFileName, -11, EnglishGrammar.capitalize(fieldNode.name));
                } else {
                    addEntry(classSourceFileName, -12, fieldNode.name);
                }
            }

        }
    }

    /**
     * This is the second pass for Translatable
     */
    public void analyzeTranslatableHierarchy() {
        analyzeHierarchy(allClasses, translatableSuper);
    }

    private void analyzeHierarchy(Collection<ClassNode> classesToProcess, Collection<ClassNode> translatable) {
        Collection<ClassNode> newTranslatable = new Vector<ClassNode>();
        Collection<ClassNode> unprocessed = new Vector<ClassNode>();
        nextClassNode: for (ClassNode classNode : classesToProcess) {
            for (ClassNode itf : translatable) {
                if (itf.name.equals(classNode.superName) || (classNode.interfaces != null && classNode.interfaces.contains(itf.name))) {
                    extractTranslatableMembers(classNode);
                    newTranslatable.add(classNode);
                    continue nextClassNode;
                }
            }
            unprocessed.add(classNode);
        }
        if (!newTranslatable.isEmpty()) {
            analyzeHierarchy(unprocessed, newTranslatable);
        }
    }

    private void extractTranslatableMembers(ClassNode classNode) {
        I18nStrategy strategy = getStrategyAnnotationValue(classNode);
        if ((strategy == I18nStrategy.IgnoreAll) || (strategy == I18nStrategy.DerivedOnly)) {
            return;
        }

        final String classSourceFileName = AsmUtils.classSourceFileName(classNode);
        boolean classNameFoound = false;
        for (Map.Entry<String, I18nAnnotationDefintition> ta : isTranslations.entrySet()) {
            AnnotationNode anode = AsmUtils.getAnnotation(ta.getKey(), classNode);

            if ((anode != null) && (anode.values != null)) {
                @SuppressWarnings("unchecked")
                Iterator<Object> it = anode.values.iterator();
                while (it.hasNext()) {
                    Object name = it.next();
                    if (ta.getValue().mainElement.equals(name)) {
                        String mainValue = it.next().toString();
                        if (!I18nAnnotation.DEFAULT_VALUE.equals(mainValue)) {
                            addEntry(classSourceFileName, -1, mainValue);
                            classNameFoound = true;
                        }
                    } else if (ta.getValue().elements.contains(name)) {
                        addEntry(classSourceFileName, -2, it.next().toString());
                    } else {
                        if (it.hasNext()) {
                            it.next();
                        }
                    }
                }
            }
        }
        if (!classNameFoound) {
            boolean capitalize = true;
            if (Boolean.FALSE.equals(AsmUtils.getAnnotationValue(TRANSLATABLE_CLASS, "capitalize", classNode))) {
                capitalize = false;
            }
            if (capitalize) {
                addEntry(classSourceFileName, -3, EnglishGrammar.capitalize(AsmUtils.getSimpleName(classNode)));
            } else {
                addEntry(classSourceFileName, -4, AsmUtils.getSimpleName(classNode));
            }
        }

        if (strategy != I18nStrategy.IgnoreMemeber) {
            for (@SuppressWarnings("rawtypes")
            Iterator i = classNode.methods.iterator(); i.hasNext();) {
                MethodNode methodNode = (MethodNode) i.next();
                if ("<init>".equals(methodNode.name)) {
                    continue;
                }
                I18nStrategy methodStrategy = getStrategyAnnotationValue(methodNode);
                if ((methodStrategy != null) && (methodStrategy != I18nStrategy.TranslateAll)) {
                    continue;
                }

                boolean methodNameFoound = false;
                for (Map.Entry<String, I18nAnnotationDefintition> ta : isTranslations.entrySet()) {
                    AnnotationNode anode = AsmUtils.getAnnotation(ta.getKey(), methodNode);
                    if ((anode != null) && (anode.values != null)) {
                        @SuppressWarnings("unchecked")
                        Iterator<Object> it = anode.values.iterator();
                        while (it.hasNext()) {
                            Object name = it.next();
                            if (ta.getValue().mainElement.equals(name)) {
                                String mainValue = it.next().toString();
                                if (!I18nAnnotation.DEFAULT_VALUE.equals(mainValue)) {
                                    addEntry(classSourceFileName, -5, mainValue);
                                    methodNameFoound = true;
                                }
                            } else if (ta.getValue().elements.contains(name)) {
                                addEntry(classSourceFileName, -6, it.next().toString());
                            } else {
                                if (it.hasNext()) {
                                    it.next();
                                }
                            }
                        }
                    }
                }
                if (!methodNameFoound) {
                    boolean capitalize = true;
                    if (Boolean.FALSE.equals(AsmUtils.getAnnotationValue(TRANSLATABLE_CLASS, "capitalize", methodNode))) {
                        capitalize = false;
                    }
                    if (capitalize) {
                        addEntry(classSourceFileName, -7, EnglishGrammar.capitalize(methodNode.name));
                    } else {
                        addEntry(classSourceFileName, -8, methodNode.name);
                    }
                }
            }
        }
    }
}
