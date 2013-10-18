/*
 * Copyright 2007 Google Inc.
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
 */
package com.pyx4j.widgets.client.richtext;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.ImageFactory.WidgetsImageBundle;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.RichTextArea;
import com.pyx4j.widgets.client.dialog.MessageDialog_v2;

/**
 * A sample toolbar for use with {@link RichTextArea}. It provides a simple UI for all
 * rich text formatting, dynamically displayed only for the available functionality.
 */
public class ExtendedRichTextToolbar extends Composite {
    private static final I18n i18n = I18n.get(ExtendedRichTextToolbar.class);

    /**
     * We use an inner EventListener class to avoid exposing event methods on the
     * RichTextToolbar itself.
     */
    private class EventHandler implements ClickHandler, ChangeHandler, KeyUpHandler, BlurHandler {

        @Override
        public void onChange(ChangeEvent event) {
            Object sender = event.getSource();
            if (sender == backColors) {
                formatter.setBackColor(backColors.getValue(backColors.getSelectedIndex()));
            } else if (sender == foreColors) {
                formatter.setForeColor(foreColors.getValue(foreColors.getSelectedIndex()));
            } else if (sender == fonts) {
                formatter.setFontName(fonts.getValue(fonts.getSelectedIndex()));
            } else if (sender == fontSizes) {
                formatter.setFontSize(fontSizesConstants[fontSizes.getSelectedIndex() - 1]);
            }
        }

        @Override
        public void onClick(ClickEvent event) {
            Object sender = event.getSource();
            if (sender == bold) {
                formatter.toggleBold();
            } else if (sender == italic) {
                formatter.toggleItalic();
            } else if (sender == underline) {
                formatter.toggleUnderline();
            } else if (sender == subscript) {
                formatter.toggleSubscript();
            } else if (sender == superscript) {
                formatter.toggleSuperscript();
            } else if (sender == strikethrough) {
                formatter.toggleStrikethrough();
            } else if (sender == indent) {
                formatter.rightIndent();
            } else if (sender == outdent) {
                formatter.leftIndent();
            } else if (sender == justifyLeft) {
                formatter.setJustification(RichTextArea.Justification.LEFT);
            } else if (sender == justifyCenter) {
                formatter.setJustification(RichTextArea.Justification.CENTER);
            } else if (sender == justifyRight) {
                formatter.setJustification(RichTextArea.Justification.RIGHT);
            } else if (sender == insertImage) {
                inOperation = true;
                if (provider != null) {
                    provider.selectImage(new AsyncCallback<String>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            MessageDialog_v2.error(i18n.tr("Action Failed"), caught.getMessage());
                        }

                        @Override
                        public void onSuccess(String result) {
                            onImageUrl(result);
                        }
                    });
                } else {
                    onImageUrl(Window.prompt("Enter target image URL:", "http://"));
                }
            } else if (sender == createLink) {
                inOperation = true;
                onLinkUrl(Window.prompt("Enter target page URL:", "http://"));
            } else if (sender == removeLink) {
                formatter.removeLink();
            } else if (sender == hr) {
                formatter.insertHorizontalRule();
            } else if (sender == ol) {
                formatter.insertOrderedList();
            } else if (sender == ul) {
                formatter.insertUnorderedList();
            } else if (sender == removeFormat) {
                formatter.removeFormat();
            } else if (sender == richText) {
                // We use the RichTextArea's onKeyUp event to update the toolbar status.
                // This will catch any cases where the user moves the cursur using the
                // keyboard, or uses one of the browser's built-in keyboard shortcuts.
                updateStatus();
            } else if (sender == customButton) {
                RichTextAction action = getCustomAction();
                if (action != null) {
                    inOperation = true;
                    action.perform(formatter, new Command() {
                        @Override
                        public void execute() {
                            inOperation = false;
                        }
                    });
                }
            }
        }

        @Override
        public void onKeyUp(KeyUpEvent event) {
            if (event.getSource() == richText) {
                // We use the RichTextArea's onKeyUp event to update the toolbar status.
                // This will catch any cases where the user moves the cursur using the
                // keyboard, or uses one of the browser's built-in keyboard shortcuts.
                updateStatus();
            }
        }

        @Override
        public void onBlur(BlurEvent event) {
            richText.setFocus(true);
        }
    }

    public interface RichTextAction {
        void perform(RichTextArea.Formatter formatter, Command onComplete);
    }

    private static final RichTextArea.FontSize[] fontSizesConstants = new RichTextArea.FontSize[] { RichTextArea.FontSize.XX_SMALL,
            RichTextArea.FontSize.X_SMALL, RichTextArea.FontSize.SMALL, RichTextArea.FontSize.MEDIUM, RichTextArea.FontSize.LARGE,
            RichTextArea.FontSize.X_LARGE, RichTextArea.FontSize.XX_LARGE };

    private final WidgetsImageBundle images = ImageFactory.getImages();

    private final EventHandler handler = new EventHandler();

    private final RichTextArea richText;

    private final RichTextArea.Formatter formatter;

    private final VerticalPanel toolBar = new VerticalPanel();

    private final HorizontalPanel buttonBarTop = new HorizontalPanel();

    private final HorizontalPanel buttonBarBottom = new HorizontalPanel();

    private ToggleButton bold;

    private ToggleButton italic;

    private ToggleButton underline;

    private final ToggleButton subscript;

    private final ToggleButton superscript;

    private final ToggleButton strikethrough;

    private PushButton indent;

    private PushButton outdent;

    private PushButton justifyLeft;

    private PushButton justifyCenter;

    private PushButton justifyRight;

    private PushButton hr;

    private PushButton ol;

    private PushButton ul;

    private final PushButton insertImage;

    private PushButton createLink;

    private PushButton removeLink;

    private PushButton removeFormat;

    private ListBox backColors;

    private ListBox foreColors;

    private ListBox fonts;

    private ListBox fontSizes;

    private RichTextImageProvider provider;

    private final PushButton customButton = new PushButton();

    private RichTextAction customAction;

    /*
     * This is needed to help handling richTextArea onBlur events. When toolbar is inOperation state
     * it may open other dialogs that may have focusable components. This should not fire onBlur for
     * the editor (see RichTextArea#ignoreBlur()). Note that those dialogs may require some custom
     * handling (see ExtendedRichTextToolbar#onLinkUrl() and ExtendedRichTextToolbar#onImageUrl())
     */
    private boolean inOperation;

    /**
     * Creates a new toolbar that drives the given rich text area.
     * 
     * @param richText
     *            the rich text area to be controlled
     */
    public ExtendedRichTextToolbar(RichTextArea richText) {
        this.richText = richText;
        this.formatter = richText.getFormatter();

        toolBar.add(buttonBarTop);
        toolBar.add(buttonBarBottom);

        initWidget(toolBar);
        final String toolbarStyleName = "gwt-ExtRichTextToolbar";
        setStyleName(toolbarStyleName);

        buttonBarTop.add(bold = createToggleButton(images.bold(), "bold"));
        buttonBarTop.add(italic = createToggleButton(images.italic(), "italic"));
        buttonBarTop.add(underline = createToggleButton(images.underline(), "underline"));

        subscript = createToggleButton(images.subscript(), "subscript");
        //        topPanel.add(subscript);

        superscript = createToggleButton(images.superscript(), "superscript");
        //        topPanel.add(superscript);

        buttonBarTop.add(justifyLeft = createPushButton(images.justifyLeft(), "justifyLeft"));
        buttonBarTop.add(justifyCenter = createPushButton(images.justifyCenter(), "justifyCenter"));
        buttonBarTop.add(justifyRight = createPushButton(images.justifyRight(), "justifyRight"));

        strikethrough = createToggleButton(images.strikeThrough(), "strikeThrough");
        //topPanel.add(strikethrough);
        buttonBarTop.add(indent = createPushButton(images.indent(), "indent"));
        buttonBarTop.add(outdent = createPushButton(images.outdent(), "outdent"));
        buttonBarTop.add(hr = createPushButton(images.hr(), "hr"));
        buttonBarTop.add(ol = createPushButton(images.ol(), "ol"));
        buttonBarTop.add(ul = createPushButton(images.ul(), "ul"));

        buttonBarTop.add(insertImage = createPushButton(images.insertImage(), "insertImage"));
        buttonBarTop.add(createLink = createPushButton(images.createLink(), "createLink"));
        buttonBarTop.add(customButton);
        buttonBarTop.add(removeLink = createPushButton(images.removeLink(), "removeLink"));
        buttonBarTop.add(removeFormat = createPushButton(images.removeFormat(), "removeFormat"));

        buttonBarBottom.add(foreColors = createColorList("Font Color"));
        buttonBarBottom.add(backColors = createColorList("Highlight"));
        buttonBarBottom.add(fonts = createFontList());
        buttonBarBottom.add(fontSizes = createFontSizes());

        customButton.addBlurHandler(handler);
        customButton.addClickHandler(handler);
        customButton.setVisible(false);

        // We only use these listeners for updating status, so don't hook them up
        // unless at least basic editing is supported.
        richText.addKeyUpHandler(handler);
        richText.addClickHandler(handler);

        inOperation = false;
    }

    private ListBox createColorList(String caption) {
        ListBox lb = new ListBox();
        lb.addChangeHandler(handler);
        lb.addBlurHandler(handler);
        lb.setVisibleItemCount(1);

        lb.addItem(caption, "");
        lb.addItem("White", "white");
        lb.addItem("Black", "black");
        lb.addItem("Red", "red");
        lb.addItem("Green", "green");
        lb.addItem("Yellow", "yellow");
        lb.addItem("Blue", "blue");

        lb.getElement().getStyle().setMarginRight(4, Unit.PX);
        return lb;
    }

    private ListBox createFontList() {
        ListBox lb = new ListBox();
        lb.addChangeHandler(handler);
        lb.addBlurHandler(handler);
        lb.setVisibleItemCount(1);

        lb.addItem("Font Family", "");
        lb.addItem("Normal", "");
        lb.addItem("Times New Roman", "Times New Roman");
        lb.addItem("Arial", "Arial");
        lb.addItem("Courier New", "Courier New");
        lb.addItem("Georgia", "Georgia");
        lb.addItem("Trebuchet", "Trebuchet");
        lb.addItem("Verdana", "Verdana");

        lb.getElement().getStyle().setMarginRight(4, Unit.PX);
        return lb;
    }

    private ListBox createFontSizes() {
        ListBox lb = new ListBox();
        lb.addChangeHandler(handler);
        lb.addBlurHandler(handler);
        lb.setVisibleItemCount(1);

        lb.addItem("Font Size");
        lb.addItem("XX-Small");
        lb.addItem("X-Small");
        lb.addItem("Small");
        lb.addItem("Medium");
        lb.addItem("Large");
        lb.addItem("X-Large");
        lb.addItem("XX-Large");

        lb.getElement().getStyle().setMarginRight(4, Unit.PX);
        return lb;
    }

    private PushButton createPushButton(ImageResource img, String tip) {
        PushButton pb = new PushButton(new Image(img));
        pb.addClickHandler(handler);
        pb.addBlurHandler(handler);
        pb.setTitle(tip);
        pb.setStyleName(DefaultRichTextEditorTheme.StyleName.rtePushButton.name());
        return pb;
    }

    private ToggleButton createToggleButton(ImageResource img, String tip) {
        ToggleButton tb = new ToggleButton(new Image(img));
        tb.addClickHandler(handler);
        tb.addBlurHandler(handler);
        tb.setTitle(tip);
        tb.setStyleName(DefaultRichTextEditorTheme.StyleName.rteToggleButton.name());
        return tb;
    }

    /**
     * Updates the status of all the stateful buttons.
     */
    private void updateStatus() {
        bold.setDown(formatter.isBold());
        italic.setDown(formatter.isItalic());
        underline.setDown(formatter.isUnderlined());
        subscript.setDown(formatter.isSubscript());
        superscript.setDown(formatter.isSuperscript());
        strikethrough.setDown(formatter.isStrikethrough());
        // set font properties
        if (foreColors.getSelectedIndex() > 0) {
            formatter.setForeColor(foreColors.getValue(foreColors.getSelectedIndex()));
        }
        if (backColors.getSelectedIndex() > 0) {
            formatter.setBackColor(backColors.getValue(backColors.getSelectedIndex()));
        }
        if (fonts.getSelectedIndex() > 0) {
            formatter.setFontName(fonts.getValue(fonts.getSelectedIndex()));
        }
        if (fontSizes.getSelectedIndex() > 0) {
            formatter.setFontSize(fontSizesConstants[fontSizes.getSelectedIndex() - 1]);
        }
    }

    public void onLinkUrl(String url) {
        formatter.createLink(url);
        // make sure the richTextArea will receive focus and will handle onBlur after this method completes.
        inOperation = false;
        richText.ignoreBlur(false);
        richText.setFocus(true);
    }

    public void onImageUrl(String url) {
        formatter.insertImage(url);
        // make sure the richTextArea will receive focus and will handle onBlur after this method completes.
        inOperation = false;
        richText.ignoreBlur(false);
        richText.setFocus(true);
    }

    public void setImageProvider(RichTextImageProvider provider) {
        this.provider = provider;
    }

    public boolean inOperation() {
        return inOperation;
    }

    public PushButton getCustomButton() {
        // use this to setup button look
        return customButton;
    }

    public void setCustomAction(RichTextAction action) {
        customAction = action;
    }

    private RichTextAction getCustomAction() {
        return customAction;
    }
}
