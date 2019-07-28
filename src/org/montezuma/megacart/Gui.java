package org.montezuma.megacart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.montezuma.megacart.Project.CartType;

public class Gui implements ProjectListener {
	
	static final private String MEGACART_STUDIO_VERSION = "6.0";
	static final private String FILE_LOADER_VERSION = "4.4";
	static final private String FLASHER_VERSION = "3.25";
	static final private String EASY_SHORT_512K_FLASHER_VERSION = "2.0";
	static final private String EASY_SHORT_4MB_FLASHER_VERSION = "2.0";
	static final private String FILE_TRANSFER = "3.0";
	static final private String MEGACART_FILE_TYPE = "MegaCart";
	static final private String MEGACART_FILE_SUFFIX = "mcp";
	static final private String ATARI_FILE_TYPE = "Atari";
    static final private String DEFAULT_4MB_MODULE_TITLE =  "4MB Flash Megacart";
    static final private String DEFAULT_2MB_MODULE_TITLE =  "2MB Flash Megacart";
    static final private String DEFAULT_1MB_MODULE_TITLE =  "1MB Flash Megacart";
    static final private String DEFAULT_512K_MODULE_TITLE = "512K Flash Megacart";
	static final boolean SI_UNIT = false;
	static final String ATARI_XEX_FILE_SUFFIX = "xex";
	static final String ATARI_COM_FILE_SUFFIX = "com";
	static final String ATARI_EXE_FILE_SUFFIX = "exe";
	static final String ATARI_ROM_FILE_SUFFIX = "rom";
	static final String ATARI_BIN_FILE_SUFFIX = "bin";
	static final String ATARI_ATR_FILE_SUFFIX = "atr";
	static final private String FOR_512K = " (512K) ";
	static final private String FOR_1MB = " (1MB) ";
    static final private String FOR_2MB = " (2MB) ";
	static final private String FOR_4MB = " (4MB) ";
	static final private String LANG_EN = "en";
	static final private String LANG_DE = "de";
	static final private String LANG_EL = "el";
	static final private String LANG_PL = "pl";
	static final private String LANG_EN_MENU = "English";
	static final private String LANG_DE_MENU = "Deutsch";
	static final private String LANG_EL_MENU = "Ελληνικά";
	static final private String LANG_PL_MENU = "Polski";
	static final private Color DARK_GREEN  = new Color(0x00, 0x80, 0x00);
	static final private Color DARK_RED    = new Color(0x80, 0x00, 0x00);
	static final private int WINDOW_WIDTH = 600;
	static final private int WINDOW_HEIGHT = 300;
	
	private JFrame mFrame;
	private JMenuBar mMenuBar;
	private JMenu mMenuFile;
	private JMenu mMenuProject;
	private JMenu mMenuEdit;
	private JMenu mMenuLanguage;
	private JMenu mMenuHelp;
	private JMenuItem mMenuItemProjectNew;
	private JMenuItem mMenuItemProjectOpen;
	private JMenuItem mMenuItemProjectSave;
	private JMenuItem mMenuItemProjectSaveAs;
	private JMenuItem mMenuItemAddItems;
	private JMenuItem mMenuItemSaveItems;
	private JMenuItem mMenuItemGenerate512K;
	private JMenuItem mMenuItemGenerate1MB;
    private JMenuItem mMenuItemGenerate2MB;
	private JMenuItem mMenuItemGenerate4MB;
	private JMenuItem mMenuItemExit;
	private JMenuItem mMenuItemEditSort;
	private JMenuItem mMenuItemEditSettings;
	private JMenuItem mMenuItemHelp;
	private JMenuItem mMenuItemHelpAbout;
	private JScrollPane mPane;
	private TableModel mTableModel;
	private JTable mTable;
	private String mAppName;
	private TablePopupMenu mTablePopupMenu;
	private JLabel mAvailableSpace;
	private JLabel mMemorySatus512K;
	private JLabel mMemorySatus1MB;
    private JLabel mMemorySatus2MB;
	private JLabel mMemorySatus4MB;
	
	Project mProject;
	File mProjectFile;

	Gui(String appName) {
		mAppName = appName;
		Locale locale = new Locale(UserPreferences.sLanguage);
		Messages.setLocale(locale);
	}
	
	Project getProject()
	{
		return mProject;
	}
	
	private void setLanguage(String lang)
	{
		Locale l = new Locale(lang);
		Messages.setLocale(l);
		setWidgetTexts();
		UserPreferences.sLanguage = lang;
		UserPreferences.write();
	}
	
	private void setWidgetTexts()
	{
		mMenuFile.setText(Messages.getString("File"));
		mMenuProject.setText(Messages.getString("Project"));
		mMenuItemProjectNew.setText(Messages.getString("New"));
		mMenuItemProjectOpen.setText(Messages.getString("Open"));
		mMenuItemProjectSave.setText(Messages.getString("Save"));
		mMenuItemProjectSaveAs.setText(Messages.getString("SaveAs"));
		mMenuItemAddItems.setText(Messages.getString("AddItems"));
		mMenuItemSaveItems.setText(Messages.getString("SaveItems"));
		mMenuItemExit.setText(Messages.getString("Exit"));
		mMenuEdit.setText(Messages.getString("Edit"));
		mMenuItemEditSort.setText(Messages.getString("Sort"));
		mMenuItemEditSettings.setText(Messages.getString("Settings"));
		mMenuLanguage.setText(Messages.getString("Language"));
		mMenuHelp.setText(Messages.getString("Help"));
		mMenuItemHelp.setText(Messages.getString("Manual"));
		mMenuItemHelpAbout.setText(Messages.getString("About"));
		mTableModel.setUpColumns(mTable);
		mMenuItemGenerate512K.setText(Messages.getString("Generate512ROM"));
		mMenuItemGenerate1MB.setText(Messages.getString("Generate1MBROM"));
        mMenuItemGenerate2MB.setText(Messages.getString("Generate2MBROM"));
		mMenuItemGenerate4MB.setText(Messages.getString("Generate4MBROM"));
		mAvailableSpace.setText(Messages.getString("Gui.AvailableSpace"));
		mTable.setToolTipText(Messages.getString("Gui.DragAndDropFilesHere"));
		mTablePopupMenu = new TablePopupMenu(mTable, this);
		mTable.setComponentPopupMenu(mTablePopupMenu);
		mFrame.repaint();
	}
	
	private String setWindowsTitle()
	{
		StringBuffer sb = new StringBuffer(mAppName);
		if(mProjectFile != null)
		{
			sb.append(" - ");
			sb.append(mProjectFile.getName());
		}
		return sb.toString();
	}
	
	private String getModuleTitle(CartType type)
	{
		String module_title;
		if(mProjectFile != null)
		{
			String file_name = mProjectFile.getName(); 
			module_title = file_name.substring(0, file_name.length()-4);
		}
		else
		{
			switch(type)
			{
				case CART_512KB:
					module_title = DEFAULT_512K_MODULE_TITLE;
					break;
				case CART_1MB:
					module_title = DEFAULT_1MB_MODULE_TITLE;
					break;
				case CART_2MB:
					module_title = DEFAULT_2MB_MODULE_TITLE;
					break;
				default:
					module_title = DEFAULT_4MB_MODULE_TITLE;
					break;
			}
		}
		return module_title;
	}

	private boolean OpenProject()
	{
		boolean result = false;
		JFileChooser fc = new JFileChooser(UserPreferences.getProjectDir());
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		FileFilter filter = new FileNameExtensionFilter(MEGACART_FILE_TYPE, MEGACART_FILE_SUFFIX);
		fc.setFileFilter(filter);
		if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog(mFrame))
		{
			UserPreferences.setProjectDir(fc.getCurrentDirectory().getPath());
			mProjectFile = fc.getSelectedFile();
			try
			{
				mProject.load(mProjectFile);
				result = true;
			}
			catch(Exception e)
			{
				System.out.println(e);
				JOptionPane.showMessageDialog(mFrame, Messages.getString("LoadFail"), null, JOptionPane.ERROR_MESSAGE);
			}
		}
		return result;
	}
	
	private boolean SaveProject(boolean saveas)
	{
		boolean result = false;
		boolean shouldBeSaved = false;
		File oldfile = mProjectFile;
		if(saveas || (null == mProjectFile))
		{
			JFileChooser fc = new JFileChooser(UserPreferences.getProjectDir());
			FileFilter filter = new FileNameExtensionFilter(MEGACART_FILE_TYPE, MEGACART_FILE_SUFFIX);
			fc.setFileFilter(filter);
			if(JFileChooser.APPROVE_OPTION == fc.showSaveDialog(mFrame))
			{
				UserPreferences.setProjectDir(fc.getCurrentDirectory().getPath());
				mProjectFile = fc.getSelectedFile();
				String path = mProjectFile.getPath();
				if(!path.toLowerCase().endsWith(MEGACART_FILE_SUFFIX))
				{
					mProjectFile = new File(path+"."+MEGACART_FILE_SUFFIX);
					shouldBeSaved = true;
				}
			}
		}
		if( (!saveas && (null != mProjectFile)) || shouldBeSaved)
		{
			try
			{
				mProject.save(mProjectFile);
				result = true;
			}
			catch(IOException ioe)
			{
				System.out.println(ioe);
				JOptionPane.showMessageDialog(mFrame, Messages.getString("SaveFail"), null, JOptionPane.ERROR_MESSAGE);
				mProjectFile = oldfile;
			}
		}
		return result;
	}
	
	boolean checkProjectStatus()
	{
		boolean result = true;
		if(mProject.hasChanged())
		{
			int n = JOptionPane.showConfirmDialog(
					mFrame,
					Messages.getString("SaveProjectQuestion"),
					null,
					JOptionPane.YES_NO_OPTION
					);
			if (n == JOptionPane.YES_OPTION) {
                result = SaveProject(false);
            }
		}
		return result;
	}
	
    void createAndShow() {
    	mProject = new Project(this);
    	mFrame = new JFrame(setWindowsTitle());
    	mMenuBar = new JMenuBar();
    	mMenuFile = new JMenu();
    	mMenuItemAddItems = new JMenuItem();
    	mMenuItemAddItems.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			JFileChooser fc = new JFileChooser(UserPreferences.getInputDir());
    			fc.setDialogType(JFileChooser.OPEN_DIALOG);
    			FileFilter filter = new FileNameExtensionFilter(	ATARI_FILE_TYPE, 
    																ATARI_EXE_FILE_SUFFIX,
    																ATARI_XEX_FILE_SUFFIX,
    																ATARI_COM_FILE_SUFFIX,
    																ATARI_ROM_FILE_SUFFIX,
    																ATARI_BIN_FILE_SUFFIX,
    																ATARI_ATR_FILE_SUFFIX);
    			fc.setFileFilter(filter);
    			fc.setMultiSelectionEnabled(true);
    			if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog(mFrame))
    			{
    				UserPreferences.setInputDir(fc.getCurrentDirectory().getPath());
    				File[] files = fc.getSelectedFiles();
    				Arrays.sort(files);
    				try
    				{
    					int selected = mTable.getSelectedRow();
    					int index = selected+1;
    		            for(File f: files)
    		            {
    		        		if(mTableModel.insertItem(index, f))
    		        		{
    		        			index++;
    		        		}
    		            }
    		            mTableModel.fireTableDataChanged();
    					mTable.getSelectionModel().setSelectionInterval(0, selected);
    				}
    				catch(Exception exc)
    				{
    					System.out.println(exc);
    					JOptionPane.showMessageDialog(mFrame, Messages.getString("LoadFail"), null, JOptionPane.ERROR_MESSAGE);
    				}
    			}
    		}
    	});
    	
    	mMenuItemSaveItems = new JMenuItem();
    	mMenuItemSaveItems.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
				try
				{
					mProject.saveAllItems();
				}
				catch(Exception exc)
				{
					System.out.println(exc);
					JOptionPane.showMessageDialog(mFrame, Messages.getString("SaveFail"), null, JOptionPane.ERROR_MESSAGE);
				}
    		}
    	});
    	
    	mMenuItemGenerate512K = new JMenuItem();
    	mMenuItemGenerate512K.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			JFileChooser fc = new JFileChooser(UserPreferences.sOutputDir);
    			String proposed_file_name;
    			if(mProjectFile != null)
    			{
    				String project_file_name = mProjectFile.getName();
    				int last_char = project_file_name.length()-4;
    				proposed_file_name = project_file_name.substring(0,last_char)+"_"+Project.FILE_NAME_512K;
    			}
    			else
    			{
    				proposed_file_name = Project.FILE_NAME_512K;	
    			}
    			fc.setSelectedFile(new File(proposed_file_name));
    			if(JFileChooser.APPROVE_OPTION == fc.showSaveDialog(mFrame))
    			{
    				String file_path_without_suffix = fc.getSelectedFile().getPath();
    				int last_dot_index = file_path_without_suffix.lastIndexOf("."); 
    				if(-1 != last_dot_index)
    				{
    					file_path_without_suffix = file_path_without_suffix.substring(0, last_dot_index);
    				}
    				try{
    					mProject.generate(file_path_without_suffix,CartType.CART_512KB, getModuleTitle(CartType.CART_512KB));
    					JOptionPane.showMessageDialog(
    							mFrame,
    							file_path_without_suffix+Project.FILE_SUFFIX_ATR+UserPreferences.sLineSeparator+
    							(UserPreferences.sEnableCarGeneration?file_path_without_suffix+Project.FILE_SUFFIX_CAR+UserPreferences.sLineSeparator:"")+
    							(UserPreferences.sEnableRomGeneration?file_path_without_suffix+Project.FILE_SUFFIX_ROM+UserPreferences.sLineSeparator:"")+
    							(UserPreferences.sEnableTxtGeneration?file_path_without_suffix+Project.FILE_SUFFIX_TXT+UserPreferences.sLineSeparator:"")+
    							Messages.getString("Generated"),
    							null,
    	    	    			JOptionPane.INFORMATION_MESSAGE);
    				}
    				catch(IOException ioe)
    				{
    					System.out.println(ioe);
    					JOptionPane.showMessageDialog(mFrame, Messages.getString("Rom512FAIL"), null, JOptionPane.ERROR_MESSAGE);
    				}
    			}    			
    		}
    		});

    	mMenuItemGenerate1MB = new JMenuItem();
    	mMenuItemGenerate1MB.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			JFileChooser fc = new JFileChooser(UserPreferences.sOutputDir);
    			String proposed_file_name;
    			if(mProjectFile != null)
    			{
    				String project_file_name = mProjectFile.getName();
    				int last_char = project_file_name.length()-4;
    				proposed_file_name = project_file_name.substring(0,last_char)+"_"+Project.FILE_NAME_1MB;
    			}
    			else
    			{
    				proposed_file_name = Project.FILE_NAME_1MB;
    			}
    			fc.setSelectedFile(new File(proposed_file_name));
    			if(JFileChooser.APPROVE_OPTION == fc.showSaveDialog(mFrame))
    			{
    				String file_path_without_suffix = fc.getSelectedFile().getPath();
    				int last_dot_index = file_path_without_suffix.lastIndexOf("."); 
    				if(-1 != last_dot_index)
    				{
    					file_path_without_suffix = file_path_without_suffix.substring(0, last_dot_index);
    				}
    				try{
    					mProject.generate(file_path_without_suffix,CartType.CART_1MB, getModuleTitle(CartType.CART_1MB));
    					JOptionPane.showMessageDialog(
    							mFrame,
    							file_path_without_suffix+Project.FILE_SUFFIX_ATR+UserPreferences.sLineSeparator+
    							(UserPreferences.sEnableCarGeneration?file_path_without_suffix+Project.FILE_SUFFIX_CAR+UserPreferences.sLineSeparator:"")+
    							(UserPreferences.sEnableRomGeneration?file_path_without_suffix+Project.FILE_SUFFIX_ROM+UserPreferences.sLineSeparator:"")+
    							(UserPreferences.sEnableTxtGeneration?file_path_without_suffix+Project.FILE_SUFFIX_TXT+UserPreferences.sLineSeparator:"")+

    							Messages.getString("Generated"),
    							null,
    	    	    			JOptionPane.INFORMATION_MESSAGE);
    				}
    				catch(IOException ioe)
    				{
    					System.out.println(ioe);
    					JOptionPane.showMessageDialog(mFrame, Messages.getString("Rom2FAIL"), null, JOptionPane.ERROR_MESSAGE);
    				}
    			}    			
    		}
    		});

    	mMenuItemGenerate2MB = new JMenuItem();
    	mMenuItemGenerate2MB.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			JFileChooser fc = new JFileChooser(UserPreferences.sOutputDir);
    			String proposed_file_name;
    			if(mProjectFile != null)
    			{
    				String project_file_name = mProjectFile.getName();
    				int last_char = project_file_name.length()-4;
    				proposed_file_name = project_file_name.substring(0,last_char)+"_"+Project.FILE_NAME_2MB;
    			}
    			else
    			{
    				proposed_file_name = Project.FILE_NAME_2MB;
    			}
    			fc.setSelectedFile(new File(proposed_file_name));
    			if(JFileChooser.APPROVE_OPTION == fc.showSaveDialog(mFrame))
    			{
    				String file_path_without_suffix = fc.getSelectedFile().getPath();
    				int last_dot_index = file_path_without_suffix.lastIndexOf("."); 
    				if(-1 != last_dot_index)
    				{
    					file_path_without_suffix = file_path_without_suffix.substring(0, last_dot_index);
    				}
    				try{
    					mProject.generate(file_path_without_suffix,CartType.CART_2MB, getModuleTitle(CartType.CART_2MB));
    					JOptionPane.showMessageDialog(
    							mFrame,
    							file_path_without_suffix+Project.FILE_SUFFIX_ATR+UserPreferences.sLineSeparator+
    							(UserPreferences.sEnableCarGeneration?file_path_without_suffix+Project.FILE_SUFFIX_CAR+UserPreferences.sLineSeparator:"")+
    							(UserPreferences.sEnableRomGeneration?file_path_without_suffix+Project.FILE_SUFFIX_ROM+UserPreferences.sLineSeparator:"")+
    							(UserPreferences.sEnableTxtGeneration?file_path_without_suffix+Project.FILE_SUFFIX_TXT+UserPreferences.sLineSeparator:"")+

    							Messages.getString("Generated"),
    							null,
    	    	    			JOptionPane.INFORMATION_MESSAGE);
    				}
    				catch(IOException ioe)
    				{
    					System.out.println(ioe);
    					JOptionPane.showMessageDialog(mFrame, Messages.getString("Rom2FAIL"), null, JOptionPane.ERROR_MESSAGE);
    				}
    			}    			
    		}
    		});

    	mMenuItemGenerate4MB = new JMenuItem();
    	mMenuItemGenerate4MB.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			JFileChooser fc = new JFileChooser(UserPreferences.sOutputDir);
    			String proposed_file_name;
    			if(mProjectFile != null)
    			{
    				String project_file_name = mProjectFile.getName();
    				int last_char = project_file_name.length()-4;
    				proposed_file_name = project_file_name.substring(0,last_char)+"_"+Project.FILE_NAME_4MB;
    			}
    			else
    			{
    				proposed_file_name = Project.FILE_NAME_4MB;
    			}
    			fc.setSelectedFile(new File(proposed_file_name));
    			if(JFileChooser.APPROVE_OPTION == fc.showSaveDialog(mFrame))
    			{
    				String file_path_without_suffix = fc.getSelectedFile().getPath();
    				int last_dot_index = file_path_without_suffix.lastIndexOf("."); 
    				if(-1 != last_dot_index)
    				{
    					file_path_without_suffix = file_path_without_suffix.substring(0, last_dot_index);
    				}
    				try{
    					mProject.generate(file_path_without_suffix,CartType.CART_4MB, getModuleTitle(CartType.CART_4MB));
    					JOptionPane.showMessageDialog(
    							mFrame,
    							file_path_without_suffix+Project.FILE_SUFFIX_ATR+UserPreferences.sLineSeparator+
    							(UserPreferences.sEnableCarGeneration?file_path_without_suffix+Project.FILE_SUFFIX_CAR+UserPreferences.sLineSeparator:"")+
    							(UserPreferences.sEnableRomGeneration?file_path_without_suffix+Project.FILE_SUFFIX_ROM+UserPreferences.sLineSeparator:"")+
    							(UserPreferences.sEnableTxtGeneration?file_path_without_suffix+Project.FILE_SUFFIX_TXT+UserPreferences.sLineSeparator:"")+
    							Messages.getString("Generated"),
    							null,
    	    	    			JOptionPane.INFORMATION_MESSAGE);
    				}
    				catch(IOException ioe)
    				{
    					System.out.println(ioe);
    					JOptionPane.showMessageDialog(mFrame, Messages.getString("Rom4FAIL"), null, JOptionPane.ERROR_MESSAGE);
    				}
    			}    			
    		}
    		});
    	mMenuItemExit = new JMenuItem();
    	mMenuItemExit.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			WindowEvent wev = new WindowEvent(mFrame, WindowEvent.WINDOW_CLOSING);
    			Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
    		}
        	});
    	mMenuFile.add(mMenuItemAddItems);
    	mMenuFile.add(mMenuItemSaveItems);
    	mMenuFile.addSeparator();
    	mMenuFile.add(mMenuItemGenerate512K);
    	mMenuFile.add(mMenuItemGenerate1MB);
        mMenuFile.add(mMenuItemGenerate2MB);
    	mMenuFile.add(mMenuItemGenerate4MB);
    	mMenuFile.addSeparator();
    	mMenuFile.add(mMenuItemExit);
    	mMenuBar.add(mMenuFile);
    	
    	mMenuProject = new JMenu();
    	mMenuItemProjectNew = new JMenuItem();
    	mMenuItemProjectNew.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			if(checkProjectStatus())
    			{
					mProject = new Project(Gui.this);
					mProjectFile = null;
					mFrame.setTitle(setWindowsTitle());
					mTableModel.fireTableDataChanged();
			    	mMenuItemEditSort.setEnabled(false);
			    	mMenuItemSaveItems.setEnabled(false);
					mMenuItemGenerate512K.setEnabled(false);
					mMenuItemGenerate1MB.setEnabled(false);
                    mMenuItemGenerate2MB.setEnabled(false);
					mMenuItemGenerate4MB.setEnabled(false);
					mMemorySatus512K.setText(Utilities.humanReadableByteCount(Project.MAX_GAMES_SIZE_512K,SI_UNIT)+FOR_512K);
					mMemorySatus512K.setForeground(DARK_GREEN);
					mMemorySatus1MB.setText(Utilities.humanReadableByteCount(Project.MAX_GAMES_SIZE_1MB,SI_UNIT)+FOR_1MB);
					mMemorySatus1MB.setForeground(DARK_GREEN);
                    mMemorySatus2MB.setText(Utilities.humanReadableByteCount(Project.MAX_GAMES_SIZE_2MB,SI_UNIT)+FOR_2MB);
					mMemorySatus2MB.setForeground(DARK_GREEN);			    	
					mMemorySatus4MB.setText(Utilities.humanReadableByteCount(Project.MAX_GAMES_SIZE_4MB,SI_UNIT)+FOR_4MB);
					mMemorySatus4MB.setForeground(DARK_GREEN);			    	
					mFrame.repaint();
    			}
    		}
    	});
    	mMenuItemProjectOpen = new JMenuItem();
    	mMenuItemProjectOpen.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			if(checkProjectStatus())
    			{
    				if(OpenProject())
    				{
	    				mFrame.setTitle(setWindowsTitle());
						mTableModel.fireTableDataChanged();
						mTable.getSelectionModel().setSelectionInterval(0, 0);
						mFrame.repaint();
    				}
    			}
    		}
    	});
    	mMenuItemProjectSave = new JMenuItem();
    	mMenuItemProjectSave.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			SaveProject(false);
   				mFrame.setTitle(setWindowsTitle());
    		}
    	});
    	mMenuItemProjectSaveAs = new JMenuItem();
    	mMenuItemProjectSaveAs.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			SaveProject(true);
   				mFrame.setTitle(setWindowsTitle());
    		}
    	});
    	
    	mMenuProject.add(mMenuItemProjectNew);
    	mMenuProject.add(mMenuItemProjectOpen);
    	mMenuProject.add(mMenuItemProjectSave);
    	mMenuProject.add(mMenuItemProjectSaveAs);
    	mMenuBar.add(mMenuProject);
    	
    	mMenuEdit = new JMenu();
    	mMenuItemEditSort = new JMenuItem();
    	mMenuItemEditSort.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			mProject.sort();
				mTableModel.fireTableDataChanged();
				mTable.setColumnSelectionInterval(1, 1);
				mTable.setRowSelectionInterval(0, 0);
    		}
    	});
    	
    	mMenuItemEditSettings = new JMenuItem();
    	mMenuItemEditSettings.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			SettingsDialog sd = new SettingsDialog(mFrame, mTablePopupMenu);
    			sd.setModal(true);
    			sd.setLocationRelativeTo(mFrame);
    			sd.createDialog();
    		}
    	});
    	
    	mMenuLanguage = new JMenu();
    	JMenuItem de = new JMenuItem(LANG_DE_MENU);
    	de.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			setLanguage(LANG_DE);
    		}
    	});
    	JMenuItem en = new JMenuItem(LANG_EN_MENU);
    	en.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			setLanguage(LANG_EN);
    		}
    	});
    	JMenuItem el = new JMenuItem(LANG_EL_MENU);
    	el.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			setLanguage(LANG_EL);
    		}
    	});
    	JMenuItem pl = new JMenuItem(LANG_PL_MENU);
    	pl.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			setLanguage(LANG_PL);
    		}
    	});
    	mMenuLanguage.add(de);
    	mMenuLanguage.add(en);
    	mMenuLanguage.add(el);
    	mMenuLanguage.add(pl);
    	mMenuEdit.add(mMenuItemEditSort);
    	mMenuEdit.add(mMenuItemEditSettings);
    	mMenuEdit.add(mMenuLanguage);
    	mMenuBar.add(mMenuEdit);
    	
    	mMenuHelp = new JMenu();
    	mMenuItemHelpAbout = new JMenuItem();
    	mMenuItemHelpAbout.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    	    	JOptionPane.showMessageDialog(mFrame,
    	    			"ATARI Software (c) 2k13 Bernd Herale"+UserPreferences.sLineSeparator+
    	    			"File Loader V"+FILE_LOADER_VERSION+UserPreferences.sLineSeparator+
    	    			"512K easy short Megacart Programer V"+EASY_SHORT_512K_FLASHER_VERSION+UserPreferences.sLineSeparator+
    	    			"4MB easy short Megacart Programer V"+EASY_SHORT_4MB_FLASHER_VERSION+UserPreferences.sLineSeparator+
    	    			"Megacart Programer V"+FLASHER_VERSION+UserPreferences.sLineSeparator+
    	    			"File Transfer V"+FILE_TRANSFER+UserPreferences.sLineSeparator+
    	    			UserPreferences.sLineSeparator+
    	    			"PC Software (c) 2k13 Marcin Sochacki"+UserPreferences.sLineSeparator+
    	    			"Megacart Studio V"+MEGACART_STUDIO_VERSION+UserPreferences.sLineSeparator+UserPreferences.sLineSeparator,
    	    			null,
    	    			JOptionPane.INFORMATION_MESSAGE
    	    	);
    		}
    	});
    	mMenuItemHelp = new JMenuItem();
    	mMenuItemHelp.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			Manual manual = new Manual();
    			manual.setModal(true);
    			manual.setLocationRelativeTo(mFrame);
    			manual.createDialog();
    		}
    	});    	
    	mMenuHelp.add(mMenuItemHelp);
    	mMenuHelp.add(mMenuItemHelpAbout);
    	mMenuBar.add(mMenuHelp);

    	mFrame.setJMenuBar(mMenuBar);
    	
		mTableModel = new TableModel(this);
		mTable = new JTable(mTableModel);
		mTableModel.setUpColumns(mTable);
		mTable.setPreferredScrollableViewportSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
		mTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mTable.setDragEnabled(true);
		mTable.setDropMode(DropMode.INSERT_ROWS);
		mTable.setFillsViewportHeight(true);
		mTable.setTransferHandler(new TableTransferHandle());
		mTable.getTableHeader().setReorderingAllowed(false);
		mTablePopupMenu = new TablePopupMenu(mTable, this);
		mTable.setComponentPopupMenu(mTablePopupMenu);
		mTable.setIntercellSpacing(new Dimension(5, 5));
		mTable.setRowHeight(25);

		int condition = JComponent.WHEN_FOCUSED;
		InputMap inputMap = mTable.getInputMap(condition);
		ActionMap actionMap = mTable.getActionMap();
		String DELETE = Messages.getString("Delete");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE);
		actionMap.put(DELETE, new AbstractAction() {
			  private static final long serialVersionUID = 1L;
			  public void actionPerformed(ActionEvent e) {
				  if(mTableModel.getRowCount()!=0)
				  {
					  int selectedRow = mTable.getSelectedRow();
					  mTableModel.removeRow(selectedRow);
					  if(selectedRow >= mTableModel.getRowCount())
					  {
						  selectedRow--;
					  }
					  mTable.getSelectionModel().setSelectionInterval(0, selectedRow);
				  }
		     }
		  });
		
		mPane = new JScrollPane(mTable);
		mFrame.add(mPane,BorderLayout.CENTER);
		
		JPanel label_panel = new JPanel();
		label_panel.setBorder(BorderFactory.createEtchedBorder());
		mAvailableSpace = new JLabel();
		label_panel.add(mAvailableSpace);
		mMemorySatus512K = new JLabel(Utilities.humanReadableByteCount(Project.MAX_GAMES_SIZE_512K,SI_UNIT)+FOR_512K);
		mMemorySatus512K.setForeground(DARK_GREEN);
		label_panel.add(mMemorySatus512K);
		mMemorySatus1MB = new JLabel(Utilities.humanReadableByteCount(Project.MAX_GAMES_SIZE_1MB,SI_UNIT)+FOR_1MB);
		mMemorySatus1MB.setForeground(DARK_GREEN);
		label_panel.add(mMemorySatus1MB);
		mMemorySatus2MB = new JLabel(Utilities.humanReadableByteCount(Project.MAX_GAMES_SIZE_2MB,SI_UNIT)+FOR_2MB);
		mMemorySatus2MB.setForeground(DARK_GREEN);
		label_panel.add(mMemorySatus2MB);
		mMemorySatus4MB = new JLabel(Utilities.humanReadableByteCount(Project.MAX_GAMES_SIZE_4MB,SI_UNIT)+FOR_4MB);
		mMemorySatus4MB.setForeground(DARK_GREEN);
		label_panel.add(mMemorySatus4MB);
		
		mFrame.add(label_panel,BorderLayout.PAGE_END);

		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				checkProjectStatus();
				UserPreferences.writeOnExit();
			}
		});

    	mMenuItemEditSort.setEnabled(false);
    	mMenuItemSaveItems.setEnabled(false);
		mMenuItemGenerate512K.setEnabled(false);
		mMenuItemGenerate1MB.setEnabled(false);
        mMenuItemGenerate2MB.setEnabled(false);
		mMenuItemGenerate4MB.setEnabled(false);
    	
    	setWidgetTexts();
		
		mFrame.pack();
		mFrame.setVisible(true);
	}
    
	public void onGamesBytes(int currentGamesBytes)
	{
		boolean status512k = (currentGamesBytes!=0) && (currentGamesBytes <= Project.MAX_GAMES_SIZE_512K);
		boolean status1mb = (currentGamesBytes!=0) && (currentGamesBytes <= Project.MAX_GAMES_SIZE_1MB);
        boolean status2mb = (currentGamesBytes!=0) && (currentGamesBytes <= Project.MAX_GAMES_SIZE_2MB);
		boolean status4mb = (currentGamesBytes!=0) && (currentGamesBytes <= Project.MAX_GAMES_SIZE_4MB);
		mMenuItemGenerate512K.setEnabled(status512k);
		mMenuItemGenerate1MB.setEnabled(status1mb);
        mMenuItemGenerate2MB.setEnabled(status2mb);
		mMenuItemGenerate4MB.setEnabled(status4mb);
		
		int available512kbytes = Project.MAX_GAMES_SIZE_512K-currentGamesBytes;
		int available1mbbytes = Project.MAX_GAMES_SIZE_1MB-currentGamesBytes;
        int available2mbbytes = Project.MAX_GAMES_SIZE_2MB-currentGamesBytes;
		int available4mbbytes = Project.MAX_GAMES_SIZE_4MB-currentGamesBytes;
		
		mMemorySatus512K.setText(((available512kbytes<0)?"-":"")+Utilities.humanReadableByteCount(Math.abs(available512kbytes),SI_UNIT)+FOR_512K);
		mMemorySatus1MB.setText(((available1mbbytes<0)?"-":"")+Utilities.humanReadableByteCount(Math.abs(available1mbbytes),SI_UNIT)+FOR_1MB);
        mMemorySatus2MB.setText(((available2mbbytes<0)?"-":"")+Utilities.humanReadableByteCount(Math.abs(available2mbbytes),SI_UNIT)+FOR_2MB);
		mMemorySatus4MB.setText(((available4mbbytes<0)?"-":"")+Utilities.humanReadableByteCount(Math.abs(available4mbbytes),SI_UNIT)+FOR_4MB);

		Color c512k = null;
		Color c1mb = null;
		Color c2mb = null;
		Color c4mb = null;

		if(available512kbytes > 0)
		{
			c512k = DARK_GREEN;
		}
		else
		{
			c512k = DARK_RED;
		}

		if(available1mbbytes > 0)
		{
			c1mb = DARK_GREEN;
		}
		else
		{
			c1mb = DARK_RED;
		}
        
        if(available2mbbytes > 0)
		{
			c2mb = DARK_GREEN;
		}
		else
		{
			c2mb = DARK_RED;
		}		

		if(available4mbbytes > 0)
		{
			c4mb = DARK_GREEN;
		}
		else
		{
			c4mb = DARK_RED;
		}		
		mMemorySatus512K.setForeground(c512k);
		mMemorySatus1MB.setForeground(c1mb);
        mMemorySatus2MB.setForeground(c2mb);
		mMemorySatus4MB.setForeground(c4mb);
	}

	public void onGamesCount(int currentGamesCount)
	{
		boolean enable = currentGamesCount>1;
		mMenuItemEditSort.setEnabled(enable);
		mMenuItemSaveItems.setEnabled(enable);
	}

	public void onSaveItem(int selectedRow)
	{
		try
		{
			mProject.saveItem(selectedRow);
		}
		catch(IOException ioe)
		{
			JOptionPane.showMessageDialog(mFrame, Messages.getString("SaveError"), null, JOptionPane.ERROR_MESSAGE);
		}
	}

	public void onTestItem512K(int selectedRow)
	{
		try
		{
			mProject.testItem(selectedRow, CartType.CART_512KB, getModuleTitle(CartType.CART_512KB));
		}
		catch(IOException ioe)
		{
			JOptionPane.showMessageDialog(mFrame, Messages.getString("TestError"), null, JOptionPane.ERROR_MESSAGE);
		}
	}

	public void onTestItem1MB(int selectedRow)
	{
		try
		{
			mProject.testItem(selectedRow, CartType.CART_1MB, getModuleTitle(CartType.CART_1MB));
		}
		catch(IOException ioe)
		{
			JOptionPane.showMessageDialog(mFrame, Messages.getString("TestError"), null, JOptionPane.ERROR_MESSAGE);
		}
	}
    
	public void onTestItem2MB(int selectedRow)
	{
		try
		{
			mProject.testItem(selectedRow, CartType.CART_2MB, getModuleTitle(CartType.CART_2MB));
		}
		catch(IOException ioe)
		{
			JOptionPane.showMessageDialog(mFrame, Messages.getString("TestError"), null, JOptionPane.ERROR_MESSAGE);
		}
	}

	public void onTestItem4MB(int selectedRow)
	{
		try
		{
			mProject.testItem(selectedRow, CartType.CART_4MB, getModuleTitle(CartType.CART_4MB));
		}
		catch(IOException ioe)
		{
			JOptionPane.showMessageDialog(mFrame, Messages.getString("TestError"), null, JOptionPane.ERROR_MESSAGE);
		}
	}
}
