package org.vaadin.kanban;

import java.util.List;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class KanbanBoard extends CustomComponent {

    private GridLayout grid;
    private BoardModel model;

    public KanbanBoard(BoardModel model) {
        this.model = model;

        grid = new GridLayout();
        grid.setStyleName("board");
        grid.setImmediate(true);

        // board should use all available space
        grid.setMargin(false);
        grid.setSizeFull();

        grid.setSpacing(true);

        setCompositionRoot(grid);
        addStyleName("no-horizontal-drag-hints");
        setSizeFull();
    }

    @Override
    public void requestRepaint() {
        refresh();
        super.requestRepaint();
    }

    void refresh() {
        if (model == null || grid == null) {
            return;
        }
        List<? extends ColumnModel> columns = model.getColumns();

        grid.removeAllComponents();
        if (columns.size() < 1) {
            return;
        }
        grid.setColumns(columns.size());
        grid.setRows(3);
        grid.setRowExpandRatio(0, 0);
        grid.setRowExpandRatio(1, 2);
        grid.setRowExpandRatio(2, 0);
        for (int i = 0; i < columns.size(); i++) {
            ColumnModel column = columns.get(i);
            Label name = new Label("<h2>" + column.getName() + "</h2>",
                    Label.CONTENT_XHTML);
            name.setStyleName("header");
            name.setSizeUndefined();
            name.setWidth(100, UNITS_PERCENTAGE);

            int wipLimit = column.getWorkInProgressLimit();
            Label wip = new Label("" + (wipLimit > 0 ? wipLimit : ""),
                    Label.CONTENT_XHTML);
            wip.setStyleName("wip");
            wip.setSizeUndefined();
            wip.setWidth(100, UNITS_PERCENTAGE);

            VerticalLayout header = new VerticalLayout();
            header.setSizeUndefined();
            header.setWidth(100, UNITS_PERCENTAGE);
            header.addComponent(name);
            header.addComponent(wip);

            KanbanColumn columnView = new KanbanColumn(this, column);
            for (CardModel card : column.getCards()) {
                columnView.addComponent(new KanbanCard(card));
            }

            int row = 0;
            grid.addComponent(header, i, row++);

            if (i > 0 && i < columns.size() - 1) {
                grid.addComponent(columnView, i, row++);

                Label dod = new Label("<h3>Definition of done</h3>"
                        + column.getDefinitionOfDone(), Label.CONTENT_XHTML);
                dod.setStyleName("dod");
                dod.setSizeUndefined();
                dod.setWidth(100, UNITS_PERCENTAGE);

                grid.addComponent(dod, i, row++);
            } else {
                grid.addComponent(columnView, i, row, i, ++row);
            }
            grid.setColumnExpandRatio(i, 1);
        }
    }

    public BoardModel getModel() {
        return model;
    }
}
