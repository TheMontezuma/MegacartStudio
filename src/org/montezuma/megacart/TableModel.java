package org.montezuma.megacart;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

public class TableModel extends AbstractTableModel implements Reorderable {

	private static final long serialVersionUID = 6974671625924030220L;
	private static final String[] LOADER_TYP = {"$700","$400","","","","","","","ROM","","","","","","","","ATR"};
	private Gui mGui;
	
	TableModel(Gui gui)
	{
		mGui = gui;
	}
	
    public void setUpColumns(JTable table)
    {
    	TableColumn indexColumn = table.getColumnModel().getColumn(0);
    	indexColumn.setPreferredWidth(40);
    	TableColumn titleColumn = table.getColumnModel().getColumn(1);
    	titleColumn.setHeaderValue(Messages.getString("Title"));
    	titleColumn.setPreferredWidth(300);
    	TableColumn sizeColumn = table.getColumnModel().getColumn(2);
    	sizeColumn.setHeaderValue(Messages.getString("Size"));
    	sizeColumn.setPreferredWidth(80);
    	TableColumn loaderColumn = table.getColumnModel().getColumn(3);
    	loaderColumn.setHeaderValue(Messages.getString("Loader"));
    	loaderColumn.setPreferredWidth(80);
    	JComboBox<String> comboBox = new JComboBox<String>();
    	comboBox.addItem(LOADER_TYP[0]);
    	comboBox.addItem(LOADER_TYP[1]);
    	loaderColumn.setCellEditor(new DefaultCellEditor(comboBox));
    	DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    	renderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
    	sizeColumn.setCellRenderer(renderer);
    	loaderColumn.setCellRenderer(renderer);
    }
    
    public Class<?> getColumnClass(int columnIndex)
    {
    	return getValueAt(0, columnIndex).getClass();
    }
	
	public int getRowCount() {
		return mGui.getProject().getCount();
	}

	public int getColumnCount() {
		return 4;
	}
	
	public String getColumnName(int column)
	{
		String result = null;
		switch(column)
		{
		case 1:
			result = Messages.getString("Title");
			break;
		case 2:
			result = Messages.getString("Size");
			break;
		case 3:
			result = Messages.getString("Loader");
			break;
		default:
			break;
		}
		return result;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		Item item = mGui.getProject().getMegacartItem(rowIndex);
		if(null != item)
		{
			switch(columnIndex)
			{
				case 0:
					result = rowIndex+1;
					break;
				case 1:
					result = item.mGameTitle;
					break;
				case 2:
					result = Utilities.humanReadableByteCount(item.mContent.length,Gui.SI_UNIT);
					break;
				case 3:
					result = LOADER_TYP[item.mLoaderType];
					break;
				default:
					break;					
			}
		}
		return result;
	}
	
    public void setValueAt(Object value, int row, int col)
    {
    	Item item = mGui.getProject().getMegacartItem(row);
		switch(col)
		{
			case 1:
				item.mGameTitle = value.toString().trim();
				if(item.mGameTitle.length()>Project.MAX_GAME_TITLE_LEN)
				{
					item.mGameTitle = item.mGameTitle.substring(0, Project.MAX_GAME_TITLE_LEN).trim();
				}
				break;
			case 3:
				if(LOADER_TYP[0].equals(value))
				{
					item.mLoaderType = 0;
				}
				else
				{
					item.mLoaderType = 1;
				}
				break;
			default:
				break;					
		}
		mGui.getProject().touch();
        fireTableCellUpdated(row, col);
    }
	
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		
		if(1 == columnIndex)
		{
			return true;
		}
		else if(3 == columnIndex)
		{
			Item item = mGui.getProject().getMegacartItem(rowIndex);
			if((item.mLoaderType != Project.ROM_LOADER_TYPE) && ((item.mLoaderType != Project.ATR_LOADER_TYPE)))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean insertItem(int index, File f) throws IOException
	{
		boolean result = false;
		if(		f.isFile() && 
				f.canRead() &&
				(f.getName().toLowerCase().endsWith(Gui.ATARI_XEX_FILE_SUFFIX) ||
				f.getName().toLowerCase().endsWith(Gui.ATARI_EXE_FILE_SUFFIX) ||
				f.getName().toLowerCase().endsWith(Gui.ATARI_COM_FILE_SUFFIX) ||
				f.getName().toLowerCase().endsWith(Gui.ATARI_ROM_FILE_SUFFIX) ||
				f.getName().toLowerCase().endsWith(Gui.ATARI_BIN_FILE_SUFFIX) ||
				f.getName().toLowerCase().endsWith(Gui.ATARI_ATR_FILE_SUFFIX)
				))
		{
			FileInputStream fis = null;
			long fileSize = f.length();
			String fileName = f.getName();
			if (fileSize > Integer.MAX_VALUE)
			{
			    return false;
			}
			try
			{
				if(		(fileName.toLowerCase().endsWith(Gui.ATARI_BIN_FILE_SUFFIX) ||
						fileName.toLowerCase().endsWith(Gui.ATARI_ROM_FILE_SUFFIX)) &&
						fileSize!=Project.ROM8KB_FILE_SIZE &&
						fileSize!=Project.ROM16KB_FILE_SIZE
				)
				{
					// ROM file with incompatible size (allowed are only 8KB and 16KB)
					return false;
				}
				fis = new FileInputStream(f);
				byte[] data = new byte[(int)fileSize];
				fis.read(data);
				fis.close();			
				if(		fileName.toLowerCase().endsWith(Gui.ATARI_ROM_FILE_SUFFIX) ||
						fileName.toLowerCase().endsWith(Gui.ATARI_BIN_FILE_SUFFIX) ||
						(fileName.toLowerCase().endsWith(Gui.ATARI_ATR_FILE_SUFFIX) && (data[0]==(byte)0x96 && data[1]==(byte)0x02)) || 
						(data[0]==(byte)0xFF && data[1]==(byte)0xFF))
				{
					Item item = new Item();
					int last_char = fileName.length()-4;
					if(last_char > Project.MAX_GAME_TITLE_LEN)
					{
						last_char = Project.MAX_GAME_TITLE_LEN;
					}
					item.mGameTitle = fileName.substring(0,last_char).trim();
					item.mContent = data;
					if(fileName.toLowerCase().endsWith(Gui.ATARI_ROM_FILE_SUFFIX) || fileName.toLowerCase().endsWith(Gui.ATARI_BIN_FILE_SUFFIX))
					{
						item.mLoaderType = Project.ROM_LOADER_TYPE;
						if(fileSize==Project.ROM8KB_FILE_SIZE)
						{
							item.mContent = new byte[Project.ROM16KB_FILE_SIZE];
							Arrays.fill(item.mContent, (byte)0xFF);
							System.arraycopy(data, 0, item.mContent, Project.ROM8KB_FILE_SIZE, Project.ROM8KB_FILE_SIZE);
						}
					}
					else if(fileName.toLowerCase().endsWith(Gui.ATARI_ATR_FILE_SUFFIX))
					{
						item.mLoaderType = Project.ATR_LOADER_TYPE;
					}
					else
					{
						item.mLoaderType = Project.DEFAULT_LOADER_TYPE;
					}
					if(mGui.getProject().addMegacartItem(index, item))
					{
						fireTableRowsInserted(index, index);
						result = true;
					}
				}
			}
			finally
			{
				if(null != fis)
				{
					fis.close();
				}
			}
		}
		return result;
	}
	
	public void removeRow(int rowIndex)
	{
		mGui.getProject().removeMegacartItem(rowIndex);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

	public void reorder(int fromIndex, int toIndex)
	{
		Item tmpitem = mGui.getProject().removeMegacartItem(fromIndex);
		mGui.getProject().addMegacartItem(toIndex, tmpitem);
		fireTableDataChanged();
	}

}
