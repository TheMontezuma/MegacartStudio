package org.montezuma.megacart;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

public class TablePopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	private JTable mTable;
	private ProjectListener mListener;
	private TestAction512K mTestAction512K;
	private TestAction1MB mTestAction1MB;
    private TestAction2MB mTestAction2MB;
	private TestAction4MB mTestAction4MB;
	private RemoveAction mRemoveAction;
	private SaveAction mSaveAction;

	public TablePopupMenu(JTable table, ProjectListener listener) {
        super();
        mTestAction512K = new TestAction512K(Messages.getString("TablePopupMenuTest512K"));
        mTestAction512K.setEnabled(!(UserPreferences.sEmulatorPath.isEmpty()));
        add(mTestAction512K);
        mTestAction1MB = new TestAction1MB(Messages.getString("TablePopupMenuTest1MB"));
        mTestAction1MB.setEnabled(!(UserPreferences.sEmulatorPath.isEmpty()));
        add(mTestAction1MB);
        mTestAction2MB = new TestAction2MB(Messages.getString("TablePopupMenuTest2MB"));
        mTestAction2MB.setEnabled(!(UserPreferences.sEmulatorPath.isEmpty()));
        add(mTestAction2MB);
        mTestAction4MB = new TestAction4MB(Messages.getString("TablePopupMenuTest4MB"));
        mTestAction4MB.setEnabled(!(UserPreferences.sEmulatorPath.isEmpty()));
        add(mTestAction4MB);
        addSeparator();
        mRemoveAction = new RemoveAction(Messages.getString("TablePopupMenuRemove")); 
        add(mRemoveAction);
        addSeparator();
        mSaveAction = new SaveAction(Messages.getString("TablePopupMenuSave")); 
        add(mSaveAction);
        mTable = table;
        mListener = listener;
    }
	
	public void setTestActionEnabled(boolean enabled)
	{
		mTestAction512K.setEnabled(enabled);
		mTestAction1MB.setEnabled(enabled);
        mTestAction2MB.setEnabled(enabled);
		mTestAction4MB.setEnabled(enabled);
	}

    public void show(Component c, int x, int y) {
        int r = mTable.rowAtPoint(new Point(x,y));
        if (r >= 0 && r < mTable.getRowCount()) {
        	mTable.setRowSelectionInterval(r, r);
        	super.show(c, x, y);
        } else {
        	mTable.clearSelection();
        }
    }

    private class TestAction512K extends AbstractAction{
		private static final long serialVersionUID = 1L;
		public TestAction512K(String label) {
            super(label);
        }
        public void actionPerformed(ActionEvent e)
        {
        	int selectedRow = mTable.getSelectedRow();
        	mListener.onTestItem512K(selectedRow);
        }
    }

    private class TestAction1MB extends AbstractAction{
		private static final long serialVersionUID = 1L;
		public TestAction1MB(String label) {
            super(label);
        }
        public void actionPerformed(ActionEvent e)
        {
        	int selectedRow = mTable.getSelectedRow();
        	mListener.onTestItem1MB(selectedRow);
        }
    }
    
    private class TestAction2MB extends AbstractAction{
		private static final long serialVersionUID = 1L;
		public TestAction2MB(String label) {
            super(label);
        }
        public void actionPerformed(ActionEvent e)
        {
        	int selectedRow = mTable.getSelectedRow();
        	mListener.onTestItem2MB(selectedRow);
        }
    }   
    
    private class TestAction4MB extends AbstractAction{
		private static final long serialVersionUID = 1L;
		public TestAction4MB(String label) {
            super(label);
        }
        public void actionPerformed(ActionEvent e)
        {
        	int selectedRow = mTable.getSelectedRow();
        	mListener.onTestItem4MB(selectedRow);
        }
    }   
    
    private class RemoveAction extends AbstractAction{
		private static final long serialVersionUID = 1L;
		public RemoveAction(String label) {
            super(label);
        }
        public void actionPerformed(ActionEvent e)
        {
        	TableModel mTableModel = (TableModel)mTable.getModel();
        	int selectedRow = mTable.getSelectedRow();
        	mTableModel.removeRow(selectedRow);
			if(selectedRow >= mTableModel.getRowCount())
			{
				selectedRow--;
			}
			mTable.getSelectionModel().setSelectionInterval(0, selectedRow);
        }
    }
    
    private class SaveAction extends AbstractAction{
		private static final long serialVersionUID = 1L;
		public SaveAction(String label) {
            super(label);
        }
        public void actionPerformed(ActionEvent e)
        {
        	int selectedRow = mTable.getSelectedRow();
        	mListener.onSaveItem(selectedRow);
        }
    }

}
