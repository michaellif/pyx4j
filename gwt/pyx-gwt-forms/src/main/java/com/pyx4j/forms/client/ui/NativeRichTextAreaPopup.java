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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RequiresResize;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.HtmlArea;
import com.pyx4j.widgets.client.RichTextArea;
import com.pyx4j.widgets.client.WatermarkComponent;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkOption;
import com.pyx4j.widgets.client.richtext.BasikRichTextToolbar;

public class NativeRichTextAreaPopup extends DockPanel implements INativeRichTextComponent<String>, WatermarkComponent {

    private final HtmlArea viewer;

    private final Anchor editAction;

    private final CRichTextAreaPopup textArea;

    public NativeRichTextAreaPopup(CRichTextAreaPopup textArea) {
        super();
        this.textArea = textArea;

        ClickHandler popupHandler = new ClickHandler() {

            boolean shown = false;

            @Override
            public void onClick(ClickEvent event) {
                event.preventDefault();
                if (shown) {
                    return;
                }
                final RichTextAreaPopupPanel editor = new RichTextAreaPopupPanel();
                editor.setSize("600px", "200px");
                Dialog dialog = new Dialog(NativeRichTextAreaPopup.this.textArea.getTitle() + " Editor", editor) {
                    @Override
                    protected void setupFocusManager() {
                        //no impl
                    }

                    @Override
                    public void hide() {
                        super.hide();
                        shown = false;
                    }
                };
                dialog.setBody(editor);
                shown = true;
                dialog.show();

            }
        };

        viewer = new HtmlArea();
        viewer.setHeight("120px");
        viewer.addClickHandler(popupHandler);

        editAction = new Anchor("edit");

        editAction.getElement().getStyle().setColor("#6A97C4");
        editAction.getElement().getStyle().setFontSize(0.8, Unit.EM);
        editAction.getElement().getStyle().setFontStyle(FontStyle.ITALIC);

        editAction.addClickHandler(popupHandler);

        add(editAction, SOUTH);
        setCellHeight(editAction, "100%");
        add(viewer, CENTER);
        setCellWidth(viewer, "100%");

        getElement().getStyle().setProperty("resize", "none");

        setTabIndex(editAction.getTabIndex());

        setWidth(textArea.getWidth());
        setHeight(textArea.getHeight());

        sinkEvents(Event.ONMOUSEOVER);
        sinkEvents(Event.ONMOUSEOUT);

    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEditable(boolean editable) {
        editAction.setEnabled(editable);
    }

    @Override
    public boolean isEditable() {
        return editAction.isEnabled();
    }

    public void scrollToBottom() {
        //Workaround for initiation of "scrollHeight" - keep next line!!!
        DOM.getElementPropertyInt(getElement(), "scrollHeight");
        DOM.setElementPropertyInt(getElement(), "scrollTop", Integer.MAX_VALUE);
    }

    @Override
    public void setNativeValue(String value) {
        String newValue = value == null ? "" : value;
        if (!newValue.equals(viewer.getHTML())) {
            viewer.setHTML(newValue);
        }
    }

    @Override
    public String getNativeValue() {
        return viewer.getHTML();
    }

    @Override
    public CRichTextAreaPopup getCComponent() {
        return textArea;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        DomDebug.attachedWidget();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        DomDebug.detachWidget();
    }

    @Override
    public void setFocus(boolean focused) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTabIndex(int tabIndex) {
        // TODO Auto-generated method stub
    }

    class RichTextAreaPopupPanel extends DockPanel implements OkOption, CancelOption, RequiresResize {

        private final RichTextArea richTextArea;

        private final BasikRichTextToolbar toolbar;

        public RichTextAreaPopupPanel() {
            getElement().getStyle().setPadding(30, Unit.PX);
            getElement().getStyle().setPaddingRight(10, Unit.PX);

            richTextArea = new RichTextArea();
            richTextArea.setWidth("100%");
            richTextArea.setHeight("100%");

            toolbar = new BasikRichTextToolbar(richTextArea, true);
            toolbar.getElement().getStyle().setMarginLeft(2, Unit.PX);

            add(toolbar, NORTH);
            add(richTextArea, CENTER);
            setCellWidth(richTextArea, "100%");
            setCellHeight(richTextArea, "100%");

            getElement().getStyle().setProperty("resize", "none");

            richTextArea.setHTML(viewer.getHTML());

        }

        @Override
        public boolean onClickOk() {
            if (textArea.getTidy() != null) {
                textArea.getTidy().tidy(NativeRichTextArea.trimHtml(richTextArea.getHTML()), new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        throw new UnrecoverableClientError(caught);
                    }

                    @Override
                    public void onSuccess(String result) {
                        setNativeValue(result);
                        textArea.onEditingStop();
                    }
                });
            } else {
                setNativeValue(NativeRichTextArea.trimHtml(richTextArea.getHTML()));
                textArea.onEditingStop();
            }
            richTextArea.removeFromParent();
            return true;
        }

        @Override
        public boolean onClickCancel() {
            richTextArea.removeFromParent();
            return true;
        }

        @Override
        public void onResize() {
            setWidth("100%");
            setHeight("100%");
            richTextArea.setWidth("100%");
            richTextArea.setHeight("100%");
            setCellWidth(richTextArea, "100%");
            setCellHeight(richTextArea, "100%");
        }

    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        return viewer.addFocusHandler(focusHandler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        return viewer.addBlurHandler(blurHandler);
    }

    @Override
    public void setWatermark(String text) {
        viewer.setWatermark(text);
    }

    @Override
    public void setValid(boolean valid) {
    }

}