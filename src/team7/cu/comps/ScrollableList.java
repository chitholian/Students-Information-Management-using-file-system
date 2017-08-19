package team7.cu.comps;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Created by Chitholian on 6/23/2017.
 */
public class ScrollableList extends MyPanel {
    protected MyButton editBtn, deleteBtn, addBtn, detailsBtn, refreshBtn;
    protected JScrollPane scrollPane;
    protected JList<Object> list;
    protected DefaultListModel<Object> listModel;
    protected ListSelectionModel selectionModel;
    protected MyLabel emptyText;

    public ScrollableList() {
        super();
    }

    @Override
    protected void init() {
        super.init();
        emptyText = new MyLabel("Oops! List is empty.");
        emptyText.setVisible(false);
        // Create buttons
        editBtn = new MyButton("Edit", "Modify the contents of this item",
                new ImageIcon(getClass().getResource("/icons/edit.png")));
        editBtn.setEnabled(false);

        deleteBtn = new MyButton("Delete", "Delete this item permanently",
                new ImageIcon(getClass().getResource("/icons/delete.png")));
        deleteBtn.setEnabled(false);

        addBtn = new MyButton("Add", "Add new item",
                new ImageIcon(getClass().getResource("/icons/add.png")));

        detailsBtn = new MyButton("Show Details", "View the item in details",
                new ImageIcon(getClass().getResource("/icons/view.png")));
        detailsBtn.setEnabled(false);

        refreshBtn = new MyButton("Refresh", "Reload Data in the List",
                new ImageIcon(getClass().getResource("/icons/refresh.png")));

        // Create list
        list = new JList<>();
        list.setModel(listModel = new DefaultListModel<>());
        selectionModel = list.getSelectionModel();
        // Add to scroll pane
        scrollPane = new JScrollPane(list);

        // Set button enabling policies
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int[] selectedItems = list.getSelectedIndices();
                if (selectedItems.length == 1) {
                    editBtn.setEnabled(true);
                    detailsBtn.setEnabled(true);
                } else {
                    editBtn.setEnabled(false);
                    detailsBtn.setEnabled(false);
                }
                if (selectedItems.length == 0) deleteBtn.setEnabled(false);
                else deleteBtn.setEnabled(true);
            }
        });
    }

    protected void triggerEmptyList() {
        scrollPane.setVisible(false);
        emptyText.setVisible(true);
        deleteBtn.setEnabled(false);
        editBtn.setEnabled(false);
        detailsBtn.setEnabled(false);
    }

    protected void triggerNonEmptyList() {
        emptyText.setVisible(false);
        scrollPane.setVisible(true);
    }
}
