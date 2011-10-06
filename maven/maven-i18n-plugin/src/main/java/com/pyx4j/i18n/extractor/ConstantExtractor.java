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
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

public class ConstantExtractor {

    private final Map<String, ConstantEntry> constants = new HashMap<String, ConstantEntry>();

    private class LineNumberAnalyzer extends Analyzer {

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

    public void readClass(InputStream in) throws IOException, AnalyzerException {
        ClassReader classReader = new ClassReader(in);
        final ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        final String className;
        if (classNode.sourceFile != null) {
            className = FilenameUtils.getPath(classNode.name) + classNode.sourceFile;
        } else {
            int nested = classNode.name.indexOf('$');
            if (nested == -1) {
                className = classNode.name + ".java";
            } else {
                className = classNode.name.substring(0, nested) + ".java";
            }
        }

        I18nConstantsInterpreter interpreter = new I18nConstantsInterpreter() {

            @Override
            protected void i18nString(int lineNr, String text) {

                ConstantEntry entry = constants.get(text);
                if (entry == null) {
                    constants.put(text, new ConstantEntry(className, lineNr, text));
                } else {
                    entry.addReference(className, lineNr);
                }
            }
        };

        Analyzer analyzer = new LineNumberAnalyzer(interpreter);

        for (@SuppressWarnings("rawtypes")
        Iterator i = classNode.methods.iterator(); i.hasNext();) {
            MethodNode methodNode = (MethodNode) i.next();
            analyzer.analyze(classNode.name, methodNode);
        }
    }

    public Collection<ConstantEntry> getConstants() {
        return constants.values();
    }
}
