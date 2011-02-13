package com.pyx4j.dashboard.client;

import java.util.Vector;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.dashboard.client.DashboardPanel.IGadget.ISetup;

/**
 * Dashboard panel.
 */
public class DashboardPanel extends SimplePanel {
    /**
     * Dashboard Gadget interface. User-defined dashboard gadgets should extend GWT Widget
     * and implement this interface.
     */
    public interface IGadget {
        // info:
        Widget getWidget(); // should be implemented meaningful!

        String getName();

        // flags:	
        boolean isMaximizable();

        boolean isMinimizable();

        boolean isSetupable();

        /**
         * Dashboard Gadget Setup interface. User-defined gadgets may implement this
         * interface in order to get gadget setup functionality.
         */
        interface ISetup {
            Widget getWidget(); // should be implemented meaningful!

            // notifications:
            boolean onOk();

            void onCancel();
        }

        // setup:
        ISetup getSetup(); // should be implemented meaningful if isSetupable!

        // notifications:
        void onMaximize(boolean maximized_restored); // true for max-ed, false - restored

        void onMinimize(boolean minimized_restored); // true for min-ed, false - restored

        void onDelete();
    } // Interface IWidget

    /**
     * Dashboard layout data type. Represents desirable layout for dashboard
     */
    public static class Layout {
        private int columns = 1; // at least one column exists by default...

        // geometry:
        private double horizontalSpacing = 0; // horizontal (%-values!) and

        private int verticalSpacing = 0; // vertical (pixels) cell spacing value...

        // column relative widths (in per-cents):
        private byte[] columnWidths = new byte[0]; // could be filled with widths...

        // decoration:
        private String[] columnNames = new String[0]; // could be filled with names...

        public Layout() {
        }

        public Layout(int columns) throws IllegalArgumentException {
            if (columns > 0)
                this.columns = columns;
            else
                throw new IllegalArgumentException();
        }

        public Layout(int columns, double spacingH_PCT, int spacingV_PX) throws IllegalArgumentException {
            if (columns > 0)
                this.columns = columns;
            else
                throw new IllegalArgumentException();

            if (!setHorizontalSpacing(spacingH_PCT))
                throw new IllegalArgumentException();

            this.verticalSpacing = spacingV_PX;
        }

        public int getColumns() {
            return columns;
        }

        public double getHorizontalSpacing() {
            return horizontalSpacing;
        }

        /*
         * Horizontal spacing set by %, so their sum (doubled value multiplied by column
         * number) may not exceed 100%, at least (in reality we want to leave space for
         * the columns itself!). Then, the spacing is formed by means of column padding,
         * so its doubled value may not exceed the size of the smallest column also...
         */
        public boolean setHorizontalSpacing(double spacingH_PCT) {
            if (getColumns() * spacingH_PCT * 2 >= 100.0)
                return false; // percentage looks strange!?.

            double pcMin = 100.0 / getColumns();
            for (int i = 0; i < columnWidths.length; ++i)
                pcMin = Math.min(pcMin, columnWidths[i]);

            if (pcMin <= spacingH_PCT * 2)
                return false; // ok, smallest column should be wider than spacing...

            horizontalSpacing = spacingH_PCT;
            return true;
        }

        public int getVerticalSpacing() {
            return verticalSpacing;
        }

        public void setverticalSpacing(int spacingV_PX) {
            verticalSpacing = spacingV_PX;
        }

        public boolean isColumnWidths() {
            return (columnWidths.length != 0);
        }

        /*
         * The column widths set by %, so their sum shouldn't exceed 100%. But there is
         * horizontal spacing also, and spacing formed by column padding, so the smallest
         * column width should be greater that doubled spacing value...
         */
        public boolean setColumnWidths(byte[] columnWidths) throws IllegalArgumentException {
            if (columnWidths.length > 0) { // note: zero lenth array is 'reset to default' case!..
                if (columnWidths.length < getColumns())
                    throw new IllegalArgumentException();

                byte pcSum = 0;
                double pcMin = 100.0 / getColumns();
                for (int i = 0; i < columnWidths.length; ++i) {
                    pcSum += columnWidths[i];
                    pcMin = Math.min(pcMin, columnWidths[i]);
                }

                if (pcSum > 100)
                    return false; // mmm, the widths percentage looks strange!?.

                if (pcMin <= getHorizontalSpacing() * 2)
                    return false; // ok, smallest column should be wider than spacing...
            }

            this.columnWidths = columnWidths;
            return true;
        }

        public float getCoumnWidth(int column) throws ArrayIndexOutOfBoundsException {
            return columnWidths[column];
        }

        public boolean isColumnNames() {
            return (columnNames.length != 0);
        }

        public void setColumnNames(String[] columnNames) throws IllegalArgumentException {
            if (columnNames.length < getColumns())
                throw new IllegalArgumentException();

            this.columnNames = columnNames;
        }

        public String getCoumnName(int column) throws ArrayIndexOutOfBoundsException {
            return columnNames[column];
        }
    } // class Layout

    // CSS style names used: 
    private static final String CSS_DASHBOARD_PANEL = "DashboardPanel";

    private static final String CSS_DASHBOARD_PANEL_COLUMN_COMPOSITE = "DashboardPanel-column-composite";

    private static final String CSS_DASHBOARD_PANEL_COLUMN_HEADING = "DashboardPanel-column-heading";

    private static final String CSS_DASHBOARD_PANEL_COLUMN_CONTAINER = "DashboardPanel-column-container";

    private static final String CSS_DASHBOARD_PANEL_HOLDER = "DashboardPanel-holder";

    private static final String CSS_DASHBOARD_PANEL_HOLDER_SETUP = "DashboardPanel-holder-setup";

    private static final String CSS_DASHBOARD_PANEL_HOLDER_CAPTION = "DashboardPanel-holder-caption";

    private static final String CSS_DASHBOARD_PANEL_HOLDER_MENU = "DashboardPanel-holder-menu";

    private static final String CSS_DASHBOARD_PANEL_HOLDER_MENU_BUTTON = "DashboardPanel-holder-menu-button";

    // internal data:	
    protected Layout layout;

    //    // VladLL : column drag-n-drop functionality is commented till now!..
    //    protected PickupDragController columnDragController;

    protected PickupDragController widgetDragController;

    protected FlowPanel columnsContainerPanel; // holds columns (as vertical panels).

    // construction:
    public DashboardPanel() {
        this.layout = new Layout();
        init();
    }

    public DashboardPanel(Layout layout) {
        this.layout = layout;
        init();
    }

    // Layout manipulation:
    public Layout getLayout() {
        return this.layout;
    }

    public boolean setLayout(Layout layout) {
        this.layout = layout; // accept new layout
        return refresh();
    }

    public boolean refresh() {
        int gadgetsCount = 0;
        // hold the current widgets for a while:
        Vector<VerticalPanelWithSpacer> columnWidgetsPanels = new Vector<VerticalPanelWithSpacer>(columnsContainerPanel.getWidgetCount());
        for (int i = 0; i < columnsContainerPanel.getWidgetCount(); ++i) {
            VerticalPanelWithSpacer cwp = getColumnWidgetsPanel(i);
            gadgetsCount += cwp.getWidgetCount();
            columnWidgetsPanels.add(cwp);
        }

        initColumns(); // initialize new columns according to the (new) layout

        // if new columns count the same as previous one - just move gadgets one to one:
        if (columnsContainerPanel.getWidgetCount() == columnWidgetsPanels.size()) {
            for (int i = 0; i < columnWidgetsPanels.size(); ++i) {
                while (columnWidgetsPanels.get(i).getWidgetCount() > 0)
                    getColumnWidgetsPanel(i).add(columnWidgetsPanels.get(i).getWidget(0));
            }
        } else { // 'equalize' gadgets per columns:
            Vector<Widget> allGadgets = new Vector<Widget>(gadgetsCount);
            for (int i = 0; i < columnWidgetsPanels.size(); ++i)
                for (int j = 0; j < columnWidgetsPanels.get(i).getWidgetCount(); ++j)
                    allGadgets.add(columnWidgetsPanels.get(i).getWidget(j));

            int gadgetsPerColumn = gadgetsCount / columnsContainerPanel.getWidgetCount();
            for (int i = 0; i < columnsContainerPanel.getWidgetCount(); ++i) {
                int size = (i == columnsContainerPanel.getWidgetCount() - 1 ? allGadgets.size() : Math.min(gadgetsPerColumn, allGadgets.size()));
                for (int j = 0; j < size; ++j) {
                    getColumnWidgetsPanel(i).add(allGadgets.firstElement());
                    allGadgets.remove(0);
                }
            }
        }

        return true;
    }

    // Widget manipulation:	
    public boolean addGadget(IGadget widget) {
        return insertGadget(widget, 0, 0);
    }

    public boolean addGadget(IGadget widget, int column) {
        return insertGadget(widget, column, -1);
    }

    public boolean insertGadget(IGadget widget, int column, int row) {
        if (checkIndexes(column, row, true)) {
            // create holder for supplied widget and insert it into specified column,row:
            GadgetHolder gh = new GadgetHolder(widget, this);

            if (row > 0)
                getColumnWidgetsPanel(column).insert(gh, row);
            else
                // if row is negative - just add at the end:
                getColumnWidgetsPanel(column).add(gh);

            return true;
        }

        return false;
    }

    public boolean removeGadget(int column, int row) {
        return (checkIndexes(column, row, false) && getColumnWidgetsPanel(column).remove(row));
    }

    public void removeAllGadgets() {
        for (int i = 0; i < columnsContainerPanel.getWidgetCount(); ++i)
            getColumnWidgetsPanel(i).clear();
    }

    // initializing:
    protected boolean init() {
        addStyleName(CSS_DASHBOARD_PANEL);

        // use the boundary panel as this composite's widget:
        AbsolutePanel boundaryPanel = new AbsolutePanel();
        boundaryPanel.setSize("100%", "100%");
        setWidget(boundaryPanel);

        // initialize horizontal panel to hold our columns:
        columnsContainerPanel = new FlowPanel();
        columnsContainerPanel.addStyleName(CSS_DASHBOARD_PANEL_COLUMN_CONTAINER);
        columnsContainerPanel.setWidth("100%");

        //        // VladLL : column drag-n-drop functionality is commented till now!..
        //        //initialize our column drag controller: 
        //        columnDragController = new PickupDragController(boundaryPanel, false);
        //        columnDragController.setBehaviorMultipleSelection(false);
        //
        //        // initialize our column drop controller:
        //        CustomFlowPanelDropController columnDropController = new CustomFlowPanelDropController(columnsContainerPanel);
        //        columnDragController.registerDropController(columnDropController);

        boundaryPanel.add(columnsContainerPanel);

        // initialize our widget drag controller:
        widgetDragController = new PickupDragController(boundaryPanel, false);
        widgetDragController.setBehaviorMultipleSelection(false);

        return initColumns();
    }

    protected boolean initColumns() {
        columnsContainerPanel.clear();
        widgetDragController.unregisterDropControllers();

        for (int col = 0; col < layout.getColumns(); ++col) {
            // vertical panel to hold the heading and a second vertical panel for widgets:
            FlowPanel columnCompositePanel = new FlowPanel();
            columnCompositePanel.addStyleName(CSS_DASHBOARD_PANEL_COLUMN_COMPOSITE);
            columnCompositePanel
                    .setWidth(((layout.isColumnWidths() ? layout.getCoumnWidth(col) : 100.0 / layout.getColumns()) - layout.getHorizontalSpacing() * 2) * 0.995
                            + "%"); // note that nasty .99x multiplier - it seems that IE calculates % widths less precisely tham Mozilla, that leads to last  
                                    //  column is being dropped to the left-bottom corner of the panel, so we leave an additional space (make columns narrower)...

            // set specific formatting styles:
            columnCompositePanel.getElement().getStyle().setMarginLeft(layout.getHorizontalSpacing(), Unit.PCT);
            columnCompositePanel.getElement().getStyle().setMarginRight(layout.getHorizontalSpacing(), Unit.PCT);
            columnCompositePanel.getElement().getStyle().setFloat(Float.LEFT);

            // put column name if necessary:
            if (layout.isColumnNames()) {
                Label heading = new Label(layout.getCoumnName(col));
                heading.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
                heading.addStyleName(CSS_DASHBOARD_PANEL_COLUMN_HEADING);
                heading.setWidth("100%");

                columnCompositePanel.add(heading);

                //                // VladLL : column drag-n-drop functionality is commented till now!..
                //                // make the column draggable by its heading:
                //                columnDragController.makeDraggable(columnCompositePanel, heading);
            }

            // inner vertical panel to hold individual widgets:
            VerticalPanelWithSpacer columnPanel = new VerticalPanelWithSpacer();
            columnPanel.addStyleName(CSS_DASHBOARD_PANEL_COLUMN_CONTAINER);
            columnPanel.setWidth("100%");

            // widget drop controller for the current column:
            CustomFlowPanelDropController widgetDropController = new CustomFlowPanelDropController(columnPanel, layout.getVerticalSpacing());
            widgetDragController.registerDropController(widgetDropController);

            columnCompositePanel.add(columnPanel);
            columnsContainerPanel.add(columnCompositePanel);
        }

        return true;
    }

    // internals:	
    protected VerticalPanelWithSpacer getColumnWidgetsPanel(int column) {
        ComplexPanel columnCompositePanel = (ComplexPanel) columnsContainerPanel.getWidget(column);
        return (VerticalPanelWithSpacer) columnCompositePanel.getWidget(columnCompositePanel.getWidgetCount() - 1);
        // note, that first element could be label with column name, so always get last one!..
    }

    protected boolean checkIndexes(int column, int row, boolean insert) {
        if (column >= columnsContainerPanel.getWidgetCount())
            return false;

        if (insert) {
            if (row > getColumnWidgetsPanel(column).getWidgetCount())
                return false;
        } else if (row >= getColumnWidgetsPanel(column).getWidgetCount())
            return false;

        return true;
    }

    protected final class GadgetHolder extends SimplePanel {
        private final IGadget holdedGadget;

        private final DashboardPanel dashboardPanel;

        private final VerticalPanel holder = new VerticalPanel();

        private final Label title = new Label();

        // public interface:
        public IGadget getIWidget() {
            return holdedGadget;
        }

        // internals:
        public GadgetHolder(IGadget widget, DashboardPanel mainPanel) {
            this.holdedGadget = widget;
            this.dashboardPanel = mainPanel;

            // create caption with title and menu:
            title.setText(holdedGadget.getName());
            title.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            HorizontalPanel widgetHolderCaption = new HorizontalPanel();
            widgetHolderCaption.add(title);
            widgetHolderCaption.setCellWidth(widgetHolderCaption.getWidget(widgetHolderCaption.getWidgetCount() - 1), "90%");
            widgetHolderCaption.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
            widgetHolderCaption.add(createWidgetMenu());
            widgetHolderCaption.setCellWidth(widgetHolderCaption.getWidget(widgetHolderCaption.getWidgetCount() - 1), "10%");
            widgetHolderCaption.addStyleName(CSS_DASHBOARD_PANEL_HOLDER_CAPTION);
            widgetHolderCaption.setWidth("100%");

            // put it together:
            holder.add(widgetHolderCaption);
            holder.add(holdedGadget.getWidget());
            holder.addStyleName(CSS_DASHBOARD_PANEL_HOLDER);
            holder.setWidth("100%");

            this.setWidget(holder);
            this.setWidth("100%");

            // don't forget about vertical spacing:
            this.getElement().getStyle().setMarginTop(layout.getVerticalSpacing(), Unit.PX);
            this.getElement().getStyle().setMarginBottom(layout.getVerticalSpacing(), Unit.PX);

            // make the widget place holder draggable by its title:
            widgetDragController.makeDraggable(this, title);
        }

        private Widget createWidgetMenu() {
            final Button btn = new Button("^");
            btn.addClickHandler(new ClickHandler() {
                private final PopupPanel pp = new PopupPanel(true);

                @Override
                public void onClick(ClickEvent event) {
                    // menu items command processors go here:
                    Command cmdMinimize = new Command() {
                        @Override
                        public void execute() {
                            pp.hide();
                            minimize();
                        }
                    };

                    Command cmdMaximize = new Command() {
                        @Override
                        public void execute() {
                            pp.hide();
                            maximize();
                        }
                    };

                    Command cmdSetup = new Command() {
                        @Override
                        public void execute() {
                            pp.hide();
                            setup();
                        }
                    };

                    Command cmdDelete = new Command() {
                        @Override
                        public void execute() {
                            pp.hide();
                            delete();
                        }
                    };

                    // create the menu:
                    MenuBar menu = new MenuBar(true);
                    if (holdedGadget.isMinimizable())
                        menu.addItem((isMinimized() ? "Expand" : "Minimize"), cmdMinimize);

                    if (holdedGadget.isMaximizable())
                        menu.addItem((isMaximized() ? "Restore" : "Maximize"), cmdMaximize);

                    menu.addItem("Delete", cmdDelete);

                    if (holdedGadget.isSetupable()) {
                        menu.addSeparator();
                        menu.addItem("Setup", cmdSetup);
                    }

                    menu.addStyleName(CSS_DASHBOARD_PANEL_HOLDER_MENU);

                    pp.setWidget(menu);
                    pp.setPopupPosition(btn.getAbsoluteLeft(), btn.getAbsoluteTop() + btn.getOffsetHeight());
                    pp.show();
                } // onClick button event handler...
            }); // ClickHandler class...

            btn.addStyleName(CSS_DASHBOARD_PANEL_HOLDER_MENU_BUTTON);
            return btn;
        }

        // --------------------------------------------------------------

        private void minimize() {
            if (isMinimized()) {
                holder.add(minimizedWidget);
                minimizedWidget = null;
                holdedGadget.onMinimize(false);
            } else { // minimize:
                minimizedWidget = holder.getWidget(holder.getWidgetCount() - 1);
                holder.remove(minimizedWidget);
                holdedGadget.onMinimize(true);
            }
        }

        private boolean isMinimized() {
            return (minimizedWidget != null);
        }

        private Widget minimizedWidget;

        // --------------------------------------------------------------

        private void maximize() {
            if (isMaximized()) {
                maximizeData.restoreWidgetPosition(this);
                dashboardPanel.setWidget(maximizeData.boundaryPanel);
                maximizeData.clear();

                widgetDragController.makeDraggable(this, title);
                holdedGadget.onMaximize(false);
            } else { // maximize:
                maximizeData.saveWidgetPosition(this);
                maximizeData.boundaryPanel = dashboardPanel.getWidget();
                dashboardPanel.setWidget(this);

                widgetDragController.makeNotDraggable(this);
                holdedGadget.onMaximize(true);
            }
        }

        private boolean isMaximized() {
            return (maximizeData.boundaryPanel != null);
        }

        private class MaximizeData {
            private FlowPanel columnPanel;

            private int widgetIndex;

            public Widget boundaryPanel;

            public void saveWidgetPosition(GadgetHolder widget) {
                columnPanel = (FlowPanel) getParent();
                widgetIndex = maximizeData.columnPanel.getWidgetIndex(widget);
            }

            public void restoreWidgetPosition(GadgetHolder widget) {
                columnPanel.insert(widget, widgetIndex);
            }

            public void clear() {
                boundaryPanel = null;
                columnPanel = null;
            }
        }

        private final MaximizeData maximizeData = new MaximizeData();

        // --------------------------------------------------------------

        private void delete() {
            holdedGadget.onDelete();
            ((FlowPanel) getParent()).remove(this);
        }

        // --------------------------------------------------------------

        private void setup() {
            final ISetup gadgetSetup = holdedGadget.getSetup();

            // create main gadget setup panel: 
            final FlowPanel setupPanel = new FlowPanel();
            setupPanel.add(gadgetSetup.getWidget());

            // create panel with Ok/Cancel buttons:
            HorizontalPanel bp = new HorizontalPanel();
            bp.add(new Button("Ok", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    gadgetSetup.onOk();
                    switchViewToNormal();
                }
            }));
            bp.add(new Button("Cancel", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    gadgetSetup.onCancel();
                    switchViewToNormal();
                }
            }));

            setupPanel.add(bp);

            // switch displayed widget with setup one:
            switchViewToSetup(setupPanel);
        }

        private void switchViewToSetup(Widget setup) {
            holder.remove(holder.getWidgetCount() - 1);
            setup.addStyleName(CSS_DASHBOARD_PANEL_HOLDER_SETUP);
            holder.add(setup);
        }

        private void switchViewToNormal() {
            holder.remove(holder.getWidgetCount() - 1);
            holder.add(holdedGadget.getWidget());
        }
    } // WidgetHolder
} // DashboardPanel class...
