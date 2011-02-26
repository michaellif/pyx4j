/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jun 4, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;


public class CRichTextArea extends CRichTextAreaBase<NativeRichTextArea> {

    @Override
    protected NativeRichTextArea createWidget() {
        return new NativeRichTextArea(this);
    }

    public void scrollToBottom() {
        if (isWidgetCreated()) {
            asWidget().scrollToBottom();
        }
    }

}