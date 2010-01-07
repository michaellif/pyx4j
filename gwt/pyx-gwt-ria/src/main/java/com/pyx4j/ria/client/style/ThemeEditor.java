/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on May 17, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.ria.client.style;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.ria.client.AbstractView;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.GroupBoxPanel;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.Theme;
import com.pyx4j.widgets.client.style.ThemeColor;

public class ThemeEditor extends AbstractView {

    private Theme theme;

    private final Theme originalTheme;

    private final Map<ThemeColor, ColorLabel> properties = new HashMap<ThemeColor, ColorLabel>();

    private ColorPickerDialog colorPickerDialog;

    private final Map<String, Style> styles = new HashMap<String, Style>();

    private final ListBox styleSelector;

    private Style styleSelected;

    private final TextArea styleEditor;

    public ThemeEditor() {
        super(new SimplePanel(), "Theme Editor", null);
        SimplePanel contentPane = (SimplePanel) getContentPane();
        HorizontalPanel mainPanel = new HorizontalPanel();
        contentPane.add(mainPanel);

        originalTheme = StyleManger.getTheme();
        theme = originalTheme.duplicate();

        GroupBoxPanel colors = new GroupBoxPanel(true);
        colors.setCaption("Colors");
        mainPanel.add(colors);

        //TODO move to component for Colors
        FlexTable table = new FlexTable();
        table.getElement().getStyle().setProperty("padding", "0px");
        colors.setContainer(table);

        int row = 0;
        for (ThemeColor p : EnumSet.allOf(ThemeColor.class)) {
            Label l = new Label(p.name());
            l.getElement().getStyle().setProperty("fontSize", "0.6em");
            table.setWidget(row, 0, l);

            ColorLabel cl = new ColorLabel(p);

            table.setWidget(row, 1, cl);
            properties.put(p, cl);
            row++;
        }

        //TODO move to component for Styles
        GroupBoxPanel stylesBox = new GroupBoxPanel(true);
        stylesBox.setCaption("Styles");
        mainPanel.add(stylesBox);
        FlexTable stable = new FlexTable();
        stable.getElement().getStyle().setProperty("padding", "0px");
        stylesBox.setContainer(stable);

        styleSelector = new ListBox();
        styleSelector.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                int index = styleSelector.getSelectedIndex();
                styleSelected = styles.get(styleSelector.getValue(index));
                if (styleSelected == null) {
                    styleEditor.setText("");
                } else {
                    styleEditor.setText(styleSelected.toEditableString());
                }
            }
        });
        styleEditor = new TextArea();
        styleEditor.setVisibleLines(10);
        styleEditor.setWidth("400px");
        com.google.gwt.dom.client.Style style = styleEditor.getElement().getStyle();
        style.setProperty("fontFamily", "monospace");
        style.setProperty("fontSize", "0.8em");
        style.setProperty("whiteSpace", "nowrap");

        styleEditor.addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(BlurEvent event) {
                if (styleSelected != null) {
                    styleSelected.updateProperties(styleEditor.getText());
                }
            }
        });

        stable.setWidget(0, 0, styleSelector);
        stable.setWidget(1, 0, styleEditor);

        updateThemePresentation();
    }

    private class ColorLabel extends HorizontalPanel {

        final InlineHTML bar;

        final Label label;

        ColorLabel(final ThemeColor p) {
            bar = new InlineHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            bar.setWidth("20px");
            Element cElement = bar.getElement();
            cElement.getStyle().setProperty("whiteSpace", "nowrap");
            cElement.getStyle().setProperty("cursor", "pointer");
            cElement.getStyle().setProperty("cursor", "hand");
            this.add(bar);

            label = new Label();
            com.google.gwt.dom.client.Style style = label.getElement().getStyle();
            style.setProperty("fontFamily", "monospace");
            style.setProperty("fontSize", "0.8em");
            style.setProperty("whiteSpace", "nowrap");
            this.add(label);

            ClickHandler h = new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    editThemeProperty(p, bar);
                }
            };

            bar.addClickHandler(h);
            label.addClickHandler(h);
        }

        private void setColor(String color) {
            bar.getElement().getStyle().setProperty("backgroundColor", color);
            label.setText(color);
        }
    }

    private class ColorPickerDialog extends DialogBox {

        //        private final ColorPicker picker;

        private ThemeColor editThemeProperty;

        private String colorOriginal;

        private boolean previewApplied;

        public ColorPickerDialog() {
            //            setText("Choose a color");
            //
            //            // Define the panels
            //            VerticalPanel panel = new VerticalPanel();
            //            FlowPanel okcancel = new FlowPanel();
            //            picker = new ColorPicker();
            //
            //            // Define the buttons
            //            Button preview = new Button("Preview");
            //            preview.addClickHandler(new ClickHandler() {
            //                public void onClick(ClickEvent sender) {
            //                    colorPreview(picker.getHexColor());
            //                }
            //            });
            //
            //            Button ok = new Button("Ok"); // ok button
            //            ok.addClickHandler(new ClickHandler() {
            //                public void onClick(ClickEvent sender) {
            //                    colorSelected(picker.getHexColor());
            //                    ColorPickerDialog.this.hide();
            //                }
            //            });
            //
            //            Button cancel = new Button("Cancel"); // cancel button
            //            cancel.addClickHandler(new ClickHandler() {
            //                public void onClick(ClickEvent sender) {
            //                    cancel();
            //                    ColorPickerDialog.this.hide();
            //                }
            //            });
            //            okcancel.add(preview);
            //            okcancel.add(ok);
            //            okcancel.add(cancel);
            //
            //            // Put it together
            //            panel.add(picker);
            //            panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
            //            panel.add(okcancel);
            //
            //            setWidget(panel);
        }

        public void showNear(Widget sender) {
            int left = sender.getAbsoluteLeft() + 30;
            int top = sender.getAbsoluteTop() + 10;
            this.setPopupPosition(left, top);
            this.show();
        }

        private void edit(ThemeColor p) {
            editThemeProperty = p;
            previewApplied = false;
            colorOriginal = theme.getThemeColor(p);
            String color = colorOriginal;
            try {
                if (color.startsWith("#")) {
                    color = color.substring(1);
                }
                //TODO picker.setHex(color);
            } catch (Exception e) {
                //TODO Logger.warn(color + " is invalid color", e);
            }
        }

        private void cancel() {
            if (previewApplied) {
                setThemeColor(colorOriginal);
                applyTheme();
            }
        }

        private void colorPreview(String color) {
            previewApplied = true;
            setThemeColor("#" + color);
            applyTheme();
        }

        private void colorSelected(String color) {
            setThemeColor("#" + color);
            if (previewApplied) {
                applyTheme();
            }
        }

        private void setThemeColor(String color) {
            theme.putThemeColor(editThemeProperty, color);
            updateThemePresentation();
        }
    }

    private void editThemeProperty(ThemeColor p, InlineHTML h) {
        if (colorPickerDialog == null) {
            colorPickerDialog = new ColorPickerDialog();
        }
        colorPickerDialog.edit(p);
        colorPickerDialog.showNear(h);
    }

    @Override
    public Widget getToolbarPane() {
        FlowPanel toolbarPane = new FlowPanel();

        toolbarPane.add(new Button("Apply", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                applyTheme();
            }
        }));

        toolbarPane.add(new Button("Reset", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                theme = originalTheme.duplicate();
                updateThemePresentation();
                StyleManger.installTheme(originalTheme);
            }
        }));

        return toolbarPane;
    }

    @Override
    public Widget getFooterPane() {
        return null;
    }

    private void updateThemePresentation() {
        for (Map.Entry<ThemeColor, ColorLabel> pe : properties.entrySet()) {
            String color = theme.getThemeColor(pe.getKey());
            pe.getValue().setColor(color);
        }

        int idx = styleSelector.getSelectedIndex();
        styleSelector.clear();
        styles.clear();
        for (Style style : theme.getAllStyles()) {
            styles.put(style.getSelector(), style);
            styleSelector.addItem(style.getSelector());
        }
        if (idx == -1) {
            idx = 0;
        }
        styleSelector.setSelectedIndex(idx);
        styleSelected = styles.get(styleSelector.getValue(idx));
        if (styleSelected == null) {
            styleEditor.setText("");
        } else {
            styleEditor.setText(styleSelected.toEditableString());
        }
    }

    private void applyTheme() {
        StyleManger.installTheme(theme);
    }

    @Override
    public MenuBar getMenu() {
        return null;
    }
}
