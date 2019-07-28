package org.montezuma.megacart;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SettingsDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final int TEXT_FIELD_LENGTH = 30;
	private JTextField mEmulatorPath;
	private JTextField mOutputPath;
	private JTextField mEmulatorSwitch;
	private JCheckBox mGenerateAtr;
	private JCheckBox mGenerateCar;
	private JCheckBox mGenerateRom;
	private JCheckBox mGenerateTxt;
	private JButton mOKButton;
	private JButton mCancelButton;
	private JFrame mFrame;
	private TablePopupMenu mTablePopupMenu;
	
	public SettingsDialog(JFrame f, TablePopupMenu m)
	{
		mFrame = f;
		mTablePopupMenu = m;
	}
	
	public void createDialog() {
		
		setLayout(new BorderLayout());
		
		JPanel north_panel = new JPanel();
		north_panel.setLayout(new BoxLayout(north_panel, BoxLayout.Y_AXIS));

		JPanel out_path_panel = new JPanel();
		mOutputPath = new JTextField(TEXT_FIELD_LENGTH);
		JButton browse_out_path = new JButton(Messages.getString("Browse"));
		browse_out_path.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			JFileChooser fc = new JFileChooser(UserPreferences.sOutputDir);
    			fc.setDialogType(JFileChooser.OPEN_DIALOG);
    			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    			if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog( mFrame ))
    			{
    				mOutputPath.setText(fc.getSelectedFile().getAbsolutePath());
    			}
    		}
    	});
		mOutputPath.setText(UserPreferences.sOutputDir);
		mOutputPath.setEditable(false);
		out_path_panel.add(mOutputPath);
		out_path_panel.add(browse_out_path);
		out_path_panel.setBorder(BorderFactory.createTitledBorder(Messages.getString("OutputDirectory")));
		north_panel.add(out_path_panel);
		
		JPanel emu_path_panel = new JPanel();
		mEmulatorPath = new JTextField(TEXT_FIELD_LENGTH);
		mEmulatorPath.setText(UserPreferences.sEmulatorPath);
		mEmulatorPath.setEditable(false);
		JButton browse_emu_path = new JButton(Messages.getString("Browse"));
		browse_emu_path.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			JFileChooser fc = new JFileChooser(UserPreferences.sEmulatorPath);
    			fc.setDialogType(JFileChooser.OPEN_DIALOG);
    			if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog( mFrame ))
    			{
    				mEmulatorPath.setText(fc.getSelectedFile().getAbsolutePath());
    			}
    		}
    	});
		emu_path_panel.add(mEmulatorPath);
		emu_path_panel.add(browse_emu_path);
		emu_path_panel.setBorder(BorderFactory.createTitledBorder(Messages.getString("EmulatorPath")));
		north_panel.add(emu_path_panel);
		
		JPanel emu_switch_panel = new JPanel();
		mEmulatorSwitch = new JTextField(TEXT_FIELD_LENGTH >> 2);
		mEmulatorSwitch.setText(UserPreferences.sEmulatorSwitch);

		emu_switch_panel.add(mEmulatorSwitch);
		emu_switch_panel.setBorder(BorderFactory.createTitledBorder(Messages.getString("EmulatorSwitch")));
		north_panel.add(emu_switch_panel);

		JPanel generate_panel = new JPanel();
		mGenerateAtr = new JCheckBox(Messages.getString("AtrType"),true);
		mGenerateAtr.setEnabled(false);
		mGenerateCar = new JCheckBox(Messages.getString("CarType"),UserPreferences.sEnableCarGeneration);
		mGenerateRom = new JCheckBox(Messages.getString("RomType"),UserPreferences.sEnableRomGeneration);
		mGenerateTxt = new JCheckBox(Messages.getString("TxtType"),UserPreferences.sEnableTxtGeneration);
		generate_panel.add(mGenerateAtr);
		generate_panel.add(mGenerateCar);
		generate_panel.add(mGenerateRom);
		generate_panel.add(mGenerateTxt);
		generate_panel.setBorder(BorderFactory.createTitledBorder(Messages.getString("Generate")));
		north_panel.add(generate_panel);

		add(north_panel, BorderLayout.PAGE_START);
		
		JPanel button_panel = new JPanel();	
		mOKButton = new JButton("OK");
		mOKButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
   				UserPreferences.sEmulatorPath = mEmulatorPath.getText();
   				UserPreferences.sEmulatorSwitch = mEmulatorSwitch.getText();
   				UserPreferences.sOutputDir = mOutputPath.getText();
   				UserPreferences.sEnableCarGeneration = mGenerateCar.isSelected();
   				UserPreferences.sEnableRomGeneration = mGenerateRom.isSelected();
   				UserPreferences.sEnableTxtGeneration = mGenerateTxt.isSelected();
    			UserPreferences.write();
				mTablePopupMenu.setTestActionEnabled(!(UserPreferences.sEmulatorPath.isEmpty()));
    			setVisible(false);
    			dispose();
    		}
    	});
		button_panel.add(mOKButton);
		mCancelButton = new JButton(Messages.getString("Cancel"));
		mCancelButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			setVisible(false);
    			dispose();
    		}
    	});
		button_panel.add(mCancelButton);
		add(button_panel,BorderLayout.PAGE_END);

		pack();
		setVisible(true);
	}
}

