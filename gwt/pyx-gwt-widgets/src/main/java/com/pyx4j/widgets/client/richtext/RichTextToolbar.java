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

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextDecoration;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.CheckBox;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.ImageFactory.WidgetsImageBundle;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

/**
 * A sample toolbar for use with {@link RichTextArea}. It provides a simple UI for all
 * rich text formatting, dynamically displayed only for the available functionality.
 */
public class RichTextToolbar extends FlowPanel {

    private static final I18n i18n = I18n.get(RichTextToolbar.class);

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
                            MessageDialog.error(i18n.tr("Action Failed"), caught.getMessage());
                        }

                        @Override
                        public void onSuccess(String result) {
                            onImageUrl(result);
                        }
                    });
                } else {
                    onImageUrl(Window.prompt(i18n.tr("Enter target image URL:"), "http://"));
                }
            } else if (sender == createLink) {
                inOperation = true;
                onLinkUrl(Window.prompt(i18n.tr("Enter target page URL:"), "http://"));
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
//            richText.setFocus(true);
        }
    }

    private static final RichTextArea.FontSize[] fontSizesConstants = new RichTextArea.FontSize[] { RichTextArea.FontSize.XX_SMALL,
            RichTextArea.FontSize.X_SMALL, RichTextArea.FontSize.SMALL, RichTextArea.FontSize.MEDIUM, RichTextArea.FontSize.LARGE,
            RichTextArea.FontSize.X_LARGE, RichTextArea.FontSize.XX_LARGE };

    private final WidgetsImageBundle images = ImageFactory.getImages();

    private final EventHandler handler = new EventHandler();

    private final RichTextArea richText;

    private final RichTextArea.Formatter formatter;

    private FlowPanel topToolbar;

    private Toolbar topButtonBar;

    private FlowPanel fontToolbar;

    private FlowPanel insertToolbar;

    private FlowPanel formatToolbar;

    private Button bold;

    private Button italic;

    private Button underline;

    private Button indent;

    private Button outdent;

    private Button justifyLeft;

    private Button justifyCenter;

    private Button justifyRight;

    private Button hr;

    private Button ol;

    private Button ul;

    private Button insertImage;

    private Button createLink;

    private Button removeLink;

    private Button removeFormat;

    private ListBox backColors;

    private ListBox foreColors;

    private ListBox fonts;

    private ListBox fontSizes;

    private RichTextImageProvider provider;

    private final Button customButton = new Button(i18n.tr("MERGE"));

    private RichTextAction customAction;

    private Button fontButton;

    private Button formatButton;

    private Button insertButton;

    private CheckBox textHtmlSwitch;

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
    public RichTextToolbar(final RichTextArea richText) {
        this.richText = richText;
        this.formatter = richText.getFormatter();

//TODO change to PYX Theme
        setStyleName("gwt-ExtRichTextToolbar");

        initTopToolbar();
        initFormatToolbar();
        initFontToolbar();
        initInsertToolbar();

//TODO move to initInsertToolbar
        customButton.setVisible(false);
        customButton.addBlurHandler(handler);
        customButton.addClickHandler(handler);
        customButton.setVisible(false);

        // We only use these listeners for updating status, so don't hook them up
        // unless at least basic editing is supported.
        richText.addKeyUpHandler(handler);
        richText.addClickHandler(handler);

        inOperation = false;
    }

    private void initTopToolbar() {
        topToolbar = new FlowPanel();
        topToolbar.setWidth("100%");

        topButtonBar = new Toolbar();
        topButtonBar.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        topButtonBar.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        topToolbar.add(topButtonBar);

        textHtmlSwitch = new CheckBox("HTML");
        textHtmlSwitch.getElement().getStyle().setFloat(Float.RIGHT);
        textHtmlSwitch.setTitle(i18n.tr("Toggle HTML or Text mode"));
        textHtmlSwitch.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (((CheckBox) event.getSource()).getValue()) {
                    richText.setText(richText.getHTML());
                    formatToolbar.setVisible(false);
                    fontToolbar.setVisible(false);
                    insertToolbar.setVisible(false);

                    formatButton.setVisible(false);
                    fontButton.setVisible(false);
                    insertButton.setVisible(false);
                } else {
                    richText.setHTML(richText.getText());
                    formatToolbar.setVisible(false);
                    fontToolbar.setVisible(false);
                    insertToolbar.setVisible(false);

                    formatButton.setVisible(true);
                    fontButton.setVisible(true);
                    insertButton.setVisible(true);
                    formatButton.toggleActive();
                }
            }
        });
        textHtmlSwitch.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                richText.fireEvent(event);
            }
        });
        topToolbar.add(textHtmlSwitch);
        add(topToolbar);
    }

    private void initInsertToolbar() {
        insertToolbar = new FlowPanel();
        insertToolbar.setVisible(false);

        topButtonBar.addItem(insertButton = new Button(i18n.tr("Insert"), new Command() {

            @Override
            public void execute() {
                insertToolbar.setVisible(!insertToolbar.isVisible());
                fontToolbar.setVisible(false);
                formatToolbar.setVisible(false);
                insertButton.getElement().getStyle().setTextDecoration(insertToolbar.isVisible() ? TextDecoration.UNDERLINE : TextDecoration.NONE);
                if (formatButton != null)
                    formatButton.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
                if (fontButton != null)
                    fontButton.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
            }
        }));
        insertButton.addStyleName(RichTextEditorTheme.StyleName.rteTopBarButton.name());

        Toolbar linkPanel = new Toolbar();
        linkPanel.addItem(createLink = createButton(images.createLink(), "createLink"));
        linkPanel.addItem(removeLink = createButton(images.removeLink(), "removeLink"));
        linkPanel.addItem(new HTML("&emsp;"));
        linkPanel.addItem(insertImage = createButton(images.insertImage(), "insertImage"));
        insertToolbar.add(linkPanel);
        insertToolbar.add(customButton);
        add(insertToolbar);
    }

    private void initFontToolbar() {
        fontToolbar = new FlowPanel();
        fontToolbar.setVisible(false);

        topButtonBar.addItem(fontButton = new Button(i18n.tr("Font"), new Command() {

            @Override
            public void execute() {
                fontToolbar.setVisible(!fontToolbar.isVisible());
                formatToolbar.setVisible(false);
                insertToolbar.setVisible(false);
                fontButton.getElement().getStyle().setTextDecoration(fontToolbar.isVisible() ? TextDecoration.UNDERLINE : TextDecoration.NONE);
                if (formatButton != null)
                    formatButton.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
                if (insertButton != null)
                    insertButton.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
            }
        }));
        fontButton.addStyleName(RichTextEditorTheme.StyleName.rteTopBarButton.name());
        fontToolbar.add(foreColors = createColorList("Font Color"));
        fontToolbar.add(backColors = createColorList("Highlight"));
        fontToolbar.add(fonts = createFontList());
        fontToolbar.add(fontSizes = createFontSizes());
        add(fontToolbar);

    }

    private void initFormatToolbar() {
        formatToolbar = new FlowPanel();
        formatToolbar.setVisible(false);

        topButtonBar.addItem(formatButton = new Button(i18n.tr("Format"), new Command() {

            @Override
            public void execute() {
                formatToolbar.setVisible(!formatToolbar.isVisible());
                fontToolbar.setVisible(false);
                insertToolbar.setVisible(false);
                formatButton.getElement().getStyle().setTextDecoration(formatToolbar.isVisible() ? TextDecoration.UNDERLINE : TextDecoration.NONE);
                if (fontButton != null)
                    fontButton.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
                if (insertButton != null)
                    insertButton.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
            }
        }));
        formatButton.addStyleName(RichTextEditorTheme.StyleName.rteTopBarButton.name());

        formatButton.toggleActive();

        Toolbar formatPanel = new Toolbar();
        formatPanel.addItem(bold = createButton(images.bold(), "bold"));
        formatPanel.addItem(italic = createButton(images.italic(), "italic"));
        formatPanel.addItem(underline = createButton(images.underline(), "underline"));
        formatPanel.addItem(new HTML("&emsp;"));
        formatPanel.addItem(justifyLeft = createButton(images.justifyLeft(), "justifyLeft"));
        formatPanel.addItem(justifyCenter = createButton(images.justifyCenter(), "justifyCenter"));
        formatPanel.addItem(justifyRight = createButton(images.justifyRight(), "justifyRight"));
        formatPanel.addItem(new HTML("&emsp;"));

        formatToolbar.add(formatPanel);

        Toolbar indentPanel = new Toolbar();
        indentPanel.addItem(indent = createButton(images.indent(), "indent"));
        indentPanel.addItem(outdent = createButton(images.outdent(), "outdent"));
        indentPanel.addItem(new HTML("&emsp;"));
        indentPanel.addItem(hr = createButton(images.hr(), "hr"));
        indentPanel.addItem(ol = createButton(images.ol(), "ol"));
        indentPanel.addItem(new HTML("&emsp;"));
        indentPanel.addItem(ul = createButton(images.ul(), "ul"));
        indentPanel.addItem(new HTML("&emsp;"));
        indentPanel.addItem(removeFormat = createButton(images.removeFormat(), "removeFormat"));
        formatToolbar.add(indentPanel);
        add(formatToolbar);

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

    private Button createButton(ImageResource img, String tip) {
        Button tb = new Button(img);
        tb.addClickHandler(handler);
        tb.addBlurHandler(handler);
        tb.setTitle(tip);
        tb.setStyleName(WidgetTheme.StyleName.Button.name());
        Style s = tb.getElement().getStyle();
        s.setPaddingLeft(3, Unit.PX);
        s.setPaddingRight(5, Unit.PX);

        return tb;
    }

    /**
     * Updates the status of all the stateful buttons.
     */
    private void updateStatus() {
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

    public Button getCustomButton() {
        // use this to setup button look
        return customButton;
    }

    public void setCustomAction(RichTextAction action) {
        customAction = action;
    }

    private RichTextAction getCustomAction() {
        return customAction;
    }

    public boolean isHtmlMode() {
        return !textHtmlSwitch.getValue();
    }
}
