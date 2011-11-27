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
 * 
 * The idea is taken from https://gist.github.com/1201922
 * 
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.extractor;

import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Value;

import com.pyx4j.i18n.shared.I18n;

abstract class I18nConstantsInterpreter extends BasicInterpreter {

    static String I18N_CLASS = AsmUtils.codeName(I18n.class);

    static final class StringConstantValue extends BasicValue {

        final String string;

        public StringConstantValue(String string) {
            super(Type.getObjectType("java/lang/String"));
            this.string = string;
        }

        @Override
        public int getSize() {
            return 1;
        }
    }

    private String currentComment;

    private int currentLineNr;

    public void setCurrentLineNr(int currentLineNr) {
        this.currentLineNr = currentLineNr;
    }

    public void setCurrentComment(String currentComment) {
        this.currentComment = currentComment;
    }

    protected abstract void i18nString(int lineNr, String text, boolean javaFormatFlag, String currentComment);

    protected abstract void reportError(int lineNr, Value arg);

    @Override
    public BasicValue naryOperation(AbstractInsnNode insn, @SuppressWarnings("rawtypes") List values) throws AnalyzerException {
        if (insn.getOpcode() == INVOKEVIRTUAL) {
            MethodInsnNode methodInsn = (MethodInsnNode) insn;
            if (I18N_CLASS.equals(methodInsn.owner) && "tr".equals(methodInsn.name)) {
                Value arg = (Value) values.get(1);
                if (arg instanceof StringConstantValue) {
                    boolean javaFormatFlag = (values.size() > 2);
                    i18nString(currentLineNr, ((StringConstantValue) arg).string, javaFormatFlag, currentComment);
                } else {
                    reportError(currentLineNr, arg);
                }
            }
        }
        return super.naryOperation(insn, values);
    }

    @Override
    public BasicValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
        if (insn.getOpcode() == LDC) {
            Object cst = ((LdcInsnNode) insn).cst;
            if (cst instanceof String) {
                return new StringConstantValue((String) cst);
            }
        }
        return super.newOperation(insn);
    }

}
