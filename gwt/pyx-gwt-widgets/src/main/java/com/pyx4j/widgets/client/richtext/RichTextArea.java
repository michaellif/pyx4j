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
 * Created on Jan 28, 2010
 * @author michaellif
 */
package com.pyx4j.widgets.client.richtext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;

import com.pyx4j.widgets.client.NotImplementedException;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class RichTextArea extends com.google.gwt.user.client.ui.RichTextArea {

    public enum EditMode {
        text, html
    }

    private EditMode editMode = EditMode.text;

    private boolean ignoreBlur;

    private final boolean isIEUserAgent;

    private JavaScriptObject storedSelection;

    private JavaScriptObject storedRange;

    public RichTextArea() {

        isIEUserAgent = isInternetExplorerUser();

        if (isIEUserAgent) {
            addIEMouseHandlers();
        }

        setStyleName(WidgetsTheme.StyleName.TextBoxContainer.name());

        addFocusHandler(new FocusHandler() {

            @Override
            public void onFocus(FocusEvent event) {
                addStyleDependentName(WidgetsTheme.StyleDependent.focused.name());
            }
        });

        addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(BlurEvent event) {
                removeStyleDependentName(WidgetsTheme.StyleDependent.focused.name());
            }
        });

        ignoreBlur = false;

    }

    private void addIEMouseHandlers() {
        addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                try {
                    JavaScriptObject win = getWindow(RichTextArea.this);
                    saveSelectionAndRange(win);
                } catch (Throwable t) {
                    GWT.log("ERROR", t);
                }
            }

        });
    }

    /**
     * Save current selection and range; useful for fixing IE9 selection/range issues
     * 
     * @param win
     */
    private native void saveSelectionAndRange(JavaScriptObject win) /*-{

		var selection = win.getSelection();
		this.@com.pyx4j.widgets.client.richtext.RichTextArea::storedSelection = selection;

		var range = null;
		if (selection != null) {
			try {
				range = selection.getRangeAt(0);
				this.@com.pyx4j.widgets.client.richtext.RichTextArea::storedRange = range;
			} catch (e) {
				alert("saveSelectionAndRange: " + e);
			}
		}

    }-*/;

    /**
     * Restore previous selection and range; useful for fixing IE9 selection/range issues
     * 
     * @param win
     */
    private native void restoreSelectionAndRange(JavaScriptObject win) /*-{

		try {
			var selection = win.getSelection();
			selection.removeAllRanges();
			selection
					.addRange(this.@com.pyx4j.widgets.client.richtext.RichTextArea::storedRange);
		} catch (e) {
			alert("restoreSelectionAndRange: " + e);
		}

    }-*/;

    public void restoreSelectionAndRange() {

        if (isIEUserAgent) {
            try {
                JavaScriptObject win = getWindow(RichTextArea.this);
                if (win != null) {
                    restoreSelectionAndRange(win);
                }
            } catch (Throwable t) {
                GWT.log("Error restoring selection and range", t);
            }
        }

        // Do selection/range-dependent stuff here ...
    }

    private static JavaScriptObject getWindow(RichTextArea richTextArea) {

        // Sometimes window not available unless we set focus on editor
        richTextArea.setFocus(true);

        IFrameElement frame = richTextArea.getElement().cast();

        if (frame == null) {
            throw new IllegalStateException("Can't get frame for RichTextArea");
        }

        JavaScriptObject win = getWindow(frame);

        if (win == null) {
            throw new IllegalStateException("Can't get window for RichTextArea");
        }

        return win;
    }

    private static native JavaScriptObject getWindow(IFrameElement iFrame) /*-{
		try {

			var iFrameWin = iFrame.contentWindow || iFrame.contentDocument;

			if (!iFrameWin.document) {
				iFrameWin = iFrameWin.getParentNode();
			}
			return iFrameWin;
		} catch (e) {
			return null;
		}
    }-*/;

    private static boolean isInternetExplorerUser() {
        String ua = Window.Navigator.getUserAgent();
        ua = (ua == null) ? "" : ua.toLowerCase();

        return ua.contains("msie") || ua.contains("trident");

    }

    /*
     * This method is used by containing component (ExtendedRichTextArea) to ignore blur events
     * triggered by toolbar buttons. This prevents from loosing text selections due to accessing
     * the text value by onEditiongStop() method via onBlur handler.
     */
    public void ignoreBlur(boolean ignore) {
        ignoreBlur = ignore;
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (!event.getAssociatedType().equals(BlurEvent.getType()) || !ignoreBlur) {
            super.fireEvent(event);
        }
    }

    public void setEditMode(EditMode editMode) {
        this.editMode = editMode;
        switch (editMode) {
        case text:
            super.setHTML(super.getText());
            break;
        case html:
            super.setText(super.getHTML());
            break;
        default:
            break;
        }
    }

    public EditMode getEditMode() {
        return editMode;
    }

    public void setValue(String value) {
        switch (editMode) {
        case text:
            super.setHTML(value);
            break;
        case html:
            super.setText(value);
            break;
        default:
            break;
        }
    }

    public String getValue() {
        switch (editMode) {
        case text:
            return super.getHTML();
        case html:
            return super.getText();
        default:
            return null;
        }
    }

    @Override
    public final void setHTML(String html) {
        //Use setValue()
        throw new NotImplementedException();
    }

    @Override
    public final void setHTML(SafeHtml html) {
        //Use setValue()
        throw new NotImplementedException();
    }

    @Override
    public final String getHTML() {
        //Use getValue()
        throw new NotImplementedException();
    }

    @Override
    public final void setText(String text) {
        //Use setValue()
        throw new NotImplementedException();
    };

    @Override
    public final String getText() {
        //Use setValue()
        throw new NotImplementedException();
    }
}
