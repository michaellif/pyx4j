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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.CheckBox;
import com.pyx4j.widgets.client.GroupFocusHandler;
import com.pyx4j.widgets.client.IFocusGroup;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.ImageFactory.WidgetsImageBundle;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.StringBox;
import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;
import com.pyx4j.widgets.client.richtext.RichTextArea.EditMode;

/**
 * A sample toolbar for use with {@link RichTextArea}. It provides a simple UI for all
 * rich text formatting, dynamically displayed only for the available functionality.
 */
public class RichTextToolbar extends FlowPanel implements IFocusGroup {
    private static final I18n i18n = I18n.get(RichTextToolbar.class);

    private static final RichTextArea.FontSize[] fontSizesConstants = new RichTextArea.FontSize[] { RichTextArea.FontSize.XX_SMALL,
            RichTextArea.FontSize.X_SMALL, RichTextArea.FontSize.SMALL, RichTextArea.FontSize.MEDIUM, RichTextArea.FontSize.LARGE,
            RichTextArea.FontSize.X_LARGE, RichTextArea.FontSize.XX_LARGE };

    protected final WidgetsImageBundle images = ImageFactory.getImages();

    protected final RichTextEditor richTextEditor;

    protected final RichTextArea.Formatter formatter;

    private FlowPanel topToolbar;

    protected Toolbar topButtonBar;

    private FlowPanel fontToolbar;

    private FlowPanel insertToolbar;

    protected FlowPanel formatToolbar;

    private ListBox backColors;

    private ListBox foreColors;

    protected ListBox fonts;

    private ListBox fontSizes;

    private RichTextImageProvider provider;

    private Button templateActionButton;

    private RichTextTemplateAction templateAction;

    protected Button fontButton;

    protected Button formatButton;

    protected Button insertButton;

    private CheckBox editModeSwitch;

    protected Button boldButton;

    protected Button italicButton;

    protected Button underlineButton;

    protected final GroupFocusHandler groupFocusHandler;

    /**
     * Creates a new toolbar that drives the given rich text area.
     *
     * @param richText
     *            the rich text area to be controlled
     */
    public RichTextToolbar(final RichTextEditor richTextEditor) {
        this.richTextEditor = richTextEditor;
        this.formatter = richTextEditor.getRichTextArea().getFormatter();
        groupFocusHandler = new GroupFocusHandler(this);

        initTopToolbar();
        initFormatToolbar();
        initFontToolbar();
        initInsertToolbar();

        formatButton.toggleActive();

        // We only use these listeners for updating status, so don't hook them up
        // unless at least basic editing is supported.
        richTextEditor.getRichTextArea().addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                // We use the RichTextArea's onKeyUp event to update the toolbar status.
                // This will catch any cases where the user moves the cursur using the
                // keyboard, or uses one of the browser's built-in keyboard shortcuts.
                updateStatus();
            }
        });
        richTextEditor.getRichTextArea().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                // We use the RichTextArea's onKeyUp event to update the toolbar status.
                // This will catch any cases where the user moves the cursur using the
                // keyboard, or uses one of the browser's built-in keyboard shortcuts.
                updateStatus();
            }
        });

    }

    private void initTopToolbar() {
        topToolbar = new FlowPanel();
        topToolbar.setStyleName(RichTextTheme.StyleName.RteToolbarTop.name());
        topToolbar.setWidth("100%");

        topButtonBar = new Toolbar();
        topButtonBar.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        topButtonBar.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        topToolbar.add(topButtonBar);

        editModeSwitch = new CheckBox(i18n.tr("HTML"));
        groupFocusHandler.addFocusable(editModeSwitch);
        editModeSwitch.addStyleName(RichTextTheme.StyleName.RteCheckBox.name());
        editModeSwitch.setTitle(i18n.tr("Toggle HTML or Text mode"));
        editModeSwitch.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                richTextEditor.setEditMode(((CheckBox) event.getSource()).getValue() ? EditMode.html : EditMode.text);
            }
        });

        topToolbar.add(editModeSwitch);
        add(topToolbar);
    }

    private void initInsertToolbar() {
        insertToolbar = new FlowPanel();
        insertToolbar.setStyleName(RichTextTheme.StyleName.RteToolbarBottom.name());

        insertToolbar.setVisible(false);

        topButtonBar.addItem(insertButton = createButton(i18n.tr("Insert"), i18n.tr("Insert"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                if (insertButton.isActive()) {
                    if (fontButton.isActive()) {
                        fontButton.toggleActive();
                    }
                    if (formatButton.isActive()) {
                        formatButton.toggleActive();
                    }
                }
                insertToolbar.setVisible(insertButton.isActive());
            }
        }, true));
        insertButton.addStyleName(RichTextTheme.StyleName.RteToolbarButton.name());
        groupFocusHandler.addFocusable(insertButton);

        Toolbar linkPanel = new Toolbar();
        linkPanel.addItem(createButton(images.createLink(), i18n.tr("Create Link"), new Command() {

            @Override
            public void execute() {
                groupFocusHandler.setGroupFocusLocked(true);
                new EditUrlDialog(i18n.tr("Enter target resource URL:")) {

                    @Override
                    public boolean onClickOk() {
                        if (!getInput().isEmpty()) {
                            onLinkUrl(getInput());
                            return true;
                        } else {
                            return false;
                        }
                    }
                }.show();
            }
        }, false));
        linkPanel.addItem(createButton(images.removeLink(), i18n.tr("Remove Link"), new Command() {

            @Override
            public void execute() {
                formatter.removeLink();
            }
        }, false));
        linkPanel.addItem(new HTML("&emsp;"));
        linkPanel.addItem(createButton(images.insertImage(), i18n.tr("Insert Image"), new Command() {

            @Override
            public void execute() {

                groupFocusHandler.setGroupFocusLocked(true);
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
                    new EditUrlDialog(i18n.tr("Enter target image URL:")) {

                        @Override
                        public boolean onClickOk() {
                            if (!getInput().isEmpty()) {
                                onImageUrl(getInput());
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }.show();
                }
            }
        }, false));

        linkPanel.addItem(new HTML("&emsp;"));
        linkPanel.addItem(templateActionButton = createButton(images.mergeImage(), i18n.tr("Merge"), new Command() {

            @Override
            public void execute() {
                RichTextTemplateAction action = getTemplateAction();
                if (action != null) {
                    groupFocusHandler.setGroupFocusLocked(true);
                    action.perform(formatter, new Command() {
                        @Override
                        public void execute() {
                            groupFocusHandler.setGroupFocusLocked(false);
                        }
                    }, templateActionButton);
                }
            }
        }, false));
        templateActionButton.setVisible(false);
        insertToolbar.add(linkPanel);
        add(insertToolbar);
    }

    private void initFontToolbar() {
        fontToolbar = new FlowPanel();
        fontToolbar.setStyleName(RichTextTheme.StyleName.RteToolbarBottom.name());

        fontToolbar.setVisible(false);

        topButtonBar.addItem(fontButton = createButton(i18n.tr("Font"), i18n.tr("Font"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                if (fontButton.isActive()) {
                    if (insertButton.isActive()) {
                        insertButton.toggleActive();
                    }
                    if (formatButton.isActive()) {
                        formatButton.toggleActive();
                    }
                }
                fontToolbar.setVisible(fontButton.isActive());
            }
        }, true));
        fontButton.addStyleName(RichTextTheme.StyleName.RteToolbarButton.name());

        fontToolbar.add(foreColors = createColorList(i18n.tr("Font Color"), new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                formatter.setForeColor(foreColors.getValue(foreColors.getSelectedIndex()));
            }
        }));
        fontToolbar.add(backColors = createColorList(i18n.tr("Highlight"), new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                formatter.setBackColor(backColors.getValue(backColors.getSelectedIndex()));
            }
        }));
        fontToolbar.add(fonts = createFontList());
        fontToolbar.add(fontSizes = createFontSizes());
        add(fontToolbar);

    }

    protected void initFormatToolbar() {
        formatToolbar = new FlowPanel();
        formatToolbar.setStyleName(RichTextTheme.StyleName.RteToolbarBottom.name());
        formatToolbar.setVisible(false);

        topButtonBar.addItem(formatButton = createButton(i18n.tr("Format"), i18n.tr("Format"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                if (formatButton.isActive()) {
                    if (fontButton.isActive()) {
                        fontButton.toggleActive();
                    }
                    if (insertButton.isActive()) {
                        insertButton.toggleActive();
                    }
                }
                formatToolbar.setVisible(formatButton.isActive());
            }
        }, true));
        formatButton.addStyleName(RichTextTheme.StyleName.RteToolbarButton.name());
        groupFocusHandler.addFocusable(formatButton);

        Toolbar formatPanel = new Toolbar();

        formatPanel.addItem(boldButton = createButton(images.bold(), i18n.tr("Bold"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.toggleBold();
            }
        }, true));
        formatPanel.addItem(italicButton = createButton(images.italic(), i18n.tr("Italic"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.toggleItalic();
            }
        }, true));
        formatPanel.addItem(underlineButton = createButton(images.underline(), i18n.tr("Underline"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.toggleUnderline();
            }
        }, true));
        formatPanel.addItem(new HTML("&emsp;"));
        formatPanel.addItem(createButton(images.justifyLeft(), i18n.tr("Justify Left"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.setJustification(RichTextArea.Justification.LEFT);
            }
        }, false));
        formatPanel.addItem(createButton(images.justifyCenter(), i18n.tr("Justify Center"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.setJustification(RichTextArea.Justification.CENTER);
            }
        }, false));
        formatPanel.addItem(createButton(images.justifyRight(), i18n.tr("Justify Right"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.setJustification(RichTextArea.Justification.RIGHT);
            }
        }, false));
        formatPanel.addItem(new HTML("&emsp;"));

        formatToolbar.add(formatPanel);

        Toolbar indentPanel = new Toolbar();
        indentPanel.addItem(createButton(images.outdent(), i18n.tr("Indent Less"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.leftIndent();
            }
        }, false));
        indentPanel.addItem(createButton(images.indent(), i18n.tr("Indent More"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.rightIndent();
            }
        }, false));
        indentPanel.addItem(new HTML("&emsp;"));
        indentPanel.addItem(createButton(images.hr(), "Horizontal Rule", new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.insertHorizontalRule();
            }
        }, false));
        indentPanel.addItem(new HTML("&emsp;"));
        indentPanel.addItem(createButton(images.ol(), i18n.tr("Numbered List"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.insertOrderedList();
            }
        }, false));
        indentPanel.addItem(createButton(images.ul(), i18n.tr("Bulleted List"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.insertUnorderedList();
            }
        }, false));
        indentPanel.addItem(new HTML("&emsp;"));
        indentPanel.addItem(createButton(images.removeFormat(), i18n.tr("Remove Format"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.removeFormat();
            }
        }, false));
        formatToolbar.add(indentPanel);
        add(formatToolbar);

    }

    private ListBox createColorList(String caption, ChangeHandler handler) {
        ListBox lb = new ListBox();
        lb.addChangeHandler(handler);
        lb.setVisibleItemCount(1);

        lb.addItem(caption, "");
        lb.addItem(i18n.tr("White"), "white");
        lb.addItem(i18n.tr("Black"), "black");
        lb.addItem(i18n.tr("Red"), "red");
        lb.addItem(i18n.tr("Green"), "green");
        lb.addItem(i18n.tr("Yellow"), "yellow");
        lb.addItem(i18n.tr("Blue"), "blue");

        lb.getElement().getStyle().setMarginRight(4, Unit.PX);

        groupFocusHandler.addFocusable(lb);

        return lb;
    }

    protected ListBox createFontList() {
        ListBox lb = new ListBox();
        lb.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.setFontName(fonts.getValue(fonts.getSelectedIndex()));
            }
        });
        lb.setVisibleItemCount(1);

        lb.addItem(i18n.tr("Font Family"), "");
        lb.addItem(i18n.tr("Normal"), "");
        lb.addItem(i18n.tr("Times New Roman"), "Times New Roman");
        lb.addItem(i18n.tr("Arial"), "Arial");
        lb.addItem(i18n.tr("Courier New"), "Courier New");
        lb.addItem(i18n.tr("Georgia"), "Georgia");
        lb.addItem(i18n.tr("Trebuchet"), "Trebuchet");
        lb.addItem(i18n.tr("Verdana"), "Verdana");

        lb.getElement().getStyle().setMarginRight(4, Unit.PX);

        groupFocusHandler.addFocusable(lb);

        return lb;
    }

    private ListBox createFontSizes() {
        ListBox lb = new ListBox();
        lb.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                if (fontSizes.getSelectedIndex() > 0) {
                    formatter.setFontSize(fontSizesConstants[fontSizes.getSelectedIndex() - 1]);
                }
            }
        });
        lb.setVisibleItemCount(1);

        lb.addItem(i18n.tr("Font Size"));
        lb.addItem(i18n.tr("XX-Small"));
        lb.addItem(i18n.tr("X-Small"));
        lb.addItem(i18n.tr("Small"));
        lb.addItem(i18n.tr("Medium"));
        lb.addItem(i18n.tr("Large"));
        lb.addItem(i18n.tr("X-Large"));
        lb.addItem(i18n.tr("XX-Large"));

        lb.getElement().getStyle().setMarginRight(4, Unit.PX);

        groupFocusHandler.addFocusable(lb);

        return lb;
    }

    protected Button createButton(String text, String tip, Command command, boolean toggleable) {
        return createButton(text, null, tip, command, toggleable);
    }

    protected Button createButton(ImageResource img, String tip, Command command, boolean toggleable) {
        return createButton(null, img, tip, command, toggleable);
    }

    private Button createButton(String text, ImageResource img, String tip, Command command, final boolean toggleable) {
        Button button = text == null ? new Button(img, command) : new Button(text, command) {
            @Override
            public boolean isActive() {
                if (toggleable) {
                    return super.isActive();
                }
                return false;
            }
        };

        button.addStyleName(toggleable ? RichTextTheme.StyleName.RteToolbarButton.name() : RichTextTheme.StyleName.RteToolbarButtonNoToggle.name());
        button.setTitle(tip);
        groupFocusHandler.addFocusable(button);

        return button;
    }

    public void onEditModeChange(EditMode editMode) {
        editModeSwitch.setValue(editMode == EditMode.html);
        switch (editMode) {
        case text:
            topButtonBar.asWidget().setVisible(true);
            break;
        case html:
            topButtonBar.asWidget().setVisible(false);
            if (fontButton.isActive()) {
                fontButton.toggleActive();
            }
            if (insertButton.isActive()) {
                insertButton.toggleActive();
            }
            if (formatButton.isActive()) {
                formatButton.toggleActive();
            }
            break;
        default:
            break;
        }

    }

    /**
     * Updates the status of all the stateful buttons.
     */
    private void updateStatus() {
        groupFocusHandler.setGroupFocusLocked(true);
        if (formatter.isBold() != boldButton.isActive()) {
            boldButton.toggleActive();
        }

        if (formatter.isItalic() != italicButton.isActive()) {
            italicButton.toggleActive();
        }

        if (formatter.isUnderlined() != underlineButton.isActive()) {
            underlineButton.toggleActive();
        }
        groupFocusHandler.setGroupFocusLocked(false);
    }

    public void onLinkUrl(String url) {

        richTextEditor.getRichTextArea().restoreSelectionAndRange();
        formatter.createLink(url);
        // make sure the richTextArea will receive focus and will handle onBlur after this method completes.
        groupFocusHandler.setGroupFocusLocked(false);
        richTextEditor.getRichTextArea().setFocus(true);
    }

    public void onImageUrl(String url) {

        richTextEditor.getRichTextArea().restoreSelectionAndRange();
        formatter.insertImage(url);
        groupFocusHandler.setGroupFocusLocked(false);
        richTextEditor.getRichTextArea().setFocus(true);
    }

    public void setImageProvider(RichTextImageProvider provider) {
        this.provider = provider;
    }

    public void setTemplateAction(RichTextTemplateAction action) {
        templateAction = action;
        if (templateAction != null) {
            templateActionButton.setVisible(true);
        }

    }

    private RichTextTemplateAction getTemplateAction() {
        return templateAction;
    }

    private abstract class EditUrlDialog extends OkCancelDialog {
        private TextBox<String> inputTextBox;

        public EditUrlDialog(String labelText) {
            super(i18n.tr("Edit Link"));
            setBody(initBody(labelText));
        }

        private IsWidget initBody(String labelText) {
            FlowPanel body = new FlowPanel();
            body.getElement().getStyle().setProperty("padding", "20px 10px");
            body.add(new HTML(labelText));
            body.add(new HTML("<br/>"));
            inputTextBox = new StringBox();
            inputTextBox.setValue("http://");
            body.add(inputTextBox);

            return body;
        }

        public String getInput() {
            return inputTextBox.getValue();
        }
    }

    @Override
    public GroupFocusHandler getGroupFocusHandler() {
        return groupFocusHandler;
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return groupFocusHandler.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return groupFocusHandler.addBlurHandler(handler);
    }

}
