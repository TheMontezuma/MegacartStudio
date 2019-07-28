package org.montezuma.megacart;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;

public class TableTransferHandle extends TransferHandler {

	private static final long serialVersionUID = -7531471675526073402L;
	private final DataFlavor localObjectFlavor = new ActivationDataFlavor(Integer.class, DataFlavor.javaJVMLocalObjectMimeType, "Integer Row Index");

	public int getSourceActions(JComponent c) 
	{
		return TransferHandler.MOVE;
	}
	
	protected Transferable createTransferable(JComponent c)
	{
		JTable table = (JTable)c;
		return new DataHandler(new Integer(table.getSelectedRow()), localObjectFlavor.getMimeType());
	}
	
    public boolean canImport(TransferHandler.TransferSupport info)
    {
        if (!info.isDrop()) {
            return false;
        }
        if ( ((COPY & info.getSourceDropActions()) == COPY) && info.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
        {
        	info.setDropAction(COPY);
            return true;
        }
        else if( ((MOVE & info.getSourceDropActions()) == MOVE) && info.isDataFlavorSupported(localObjectFlavor))
        {
        	info.setDropAction(MOVE);
        	return true;
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
	public boolean importData(TransferHandler.TransferSupport info) {
        if (!canImport(info)) {
            return false;
        }
        
        JTable table = (JTable)info.getComponent();
        TableModel tableModel = (TableModel)table.getModel();
        JTable.DropLocation dl = (JTable.DropLocation)info.getDropLocation();
        int index = dl.getRow();
        
        Transferable t = info.getTransferable();
        if(t.isDataFlavorSupported(localObjectFlavor))
        {
            try {
               Integer rowFrom = (Integer) info.getTransferable().getTransferData(localObjectFlavor);
               if (rowFrom != -1 && ( (index > rowFrom+1) || (index < rowFrom)) ) 
               {
            	   if(index > rowFrom+1)
            	   {
            		   index--;
            	   }
                  ((Reorderable)table.getModel()).reorder(rowFrom, index);
                  table.getSelectionModel().setSelectionInterval(0, index);
                  return true;
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
            return false;
        }
        else if(t.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
        {
	        List<File> data;
	        try {
	            data = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
	            java.util.Collections.sort(data);
	            for(File f: data)
	            {
	        		if(tableModel.insertItem(index, f))
	        		{
	        			index++;
	        		}
	            }
	        } 
	        catch (Exception e)
	        {
	        	JOptionPane.showMessageDialog(table.getParent(), Messages.getString("DragAndDropError"), null, JOptionPane.ERROR_MESSAGE);
	        	System.err.println(e);
	        	return false;
	        }
	        return true;
        }
        else
        {
        	return false;
        }
    }   

}
