package org.montezuma.megacart;

import java.util.prefs.Preferences;


public class UserPreferences {

	private static final String EMULATOR_PATH = "emulator_path";
	private static final String OUTPUT_DIR = "output_path";
	private static final String PROJECT_DIR = "project_path";
	private static final String INPUT_DIR = "input_dir";
	private static final String LANGUAGE = "language";
	private static final String EMULATOR_SWITCH = "emulator_switch";
	private static final String ENABLE_CAR_GENERATION = "generate_car";
	private static final String ENABLE_ROM_GENERATION = "generate_rom";
	private static final String ENABLE_TXT_GENERATION = "generate_txt";
	
	static final String DEFAULT_EMULATOR_SWITCH = "/cart";

	static String sEmulatorPath = null;
	static String sOutputDir = null;
	static String sEmulatorSwitch = null;
	static String sLanguage = null;
	static String sLineSeparator = System.getProperty("line.separator");
	
	static boolean sEnableCarGeneration = true;
	static boolean sEnableRomGeneration = true;
	static boolean sEnableTxtGeneration = true;

	private static String OS = System.getProperty("os.name").toLowerCase();
	private static String sInputDir = "";
	private static String sProjectDir = "";
	private static boolean sInputDirChanged = false;
	private static boolean sProjectDirChanged = false;
	
	static boolean isMac()
	{
		return (OS.indexOf("mac") != -1);
	}
	
	static void read()
	{
		if(null==sInputDir)
		{
			sInputDir=".";
		}
		if(null==sLineSeparator)
		{
			sLineSeparator="\n";
		}
		Preferences prefs = Preferences.userNodeForPackage(UserPreferences.class);
		sEmulatorPath = prefs.get(EMULATOR_PATH, "");
		sOutputDir = prefs.get(OUTPUT_DIR, sInputDir);
		sProjectDir = prefs.get(PROJECT_DIR, sProjectDir);
		sInputDir = prefs.get(INPUT_DIR, sInputDir);
		sLanguage = prefs.get(LANGUAGE, "en");
		sEmulatorSwitch = prefs.get(EMULATOR_SWITCH, DEFAULT_EMULATOR_SWITCH);
		sEnableCarGeneration = prefs.getBoolean(ENABLE_CAR_GENERATION, sEnableCarGeneration);
		sEnableRomGeneration = prefs.getBoolean(ENABLE_ROM_GENERATION, sEnableRomGeneration);
		sEnableTxtGeneration = prefs.getBoolean(ENABLE_TXT_GENERATION, sEnableTxtGeneration);
	}
	
	static void write()
	{
		Preferences prefs = Preferences.userNodeForPackage(UserPreferences.class);
		prefs.put(EMULATOR_PATH, sEmulatorPath);
		prefs.put(OUTPUT_DIR, sOutputDir);
		prefs.put(LANGUAGE, sLanguage);
		prefs.put(EMULATOR_SWITCH, sEmulatorSwitch);
		prefs.putBoolean(ENABLE_CAR_GENERATION, sEnableCarGeneration);
		prefs.putBoolean(ENABLE_ROM_GENERATION, sEnableRomGeneration);
		prefs.putBoolean(ENABLE_TXT_GENERATION, sEnableTxtGeneration);
	}
	
	static public void setInputDir(String dir)
	{
		if(0 != sInputDir.compareTo(dir))
		{
			sInputDirChanged = true;
			sInputDir = dir;
		}
	}

	static public void setProjectDir(String dir)
	{
		if(0 != sProjectDir.compareTo(dir))
		{
			sProjectDirChanged = true;
			sProjectDir = dir;
		}
	}	
	
	static String getInputDir()
	{
		return sInputDir;
	}
	
	static String getProjectDir()
	{
		return sProjectDir;
	}
	
	static void writeOnExit()
	{
		if(sInputDirChanged)
		{
			Preferences prefs = Preferences.userNodeForPackage(UserPreferences.class);
			prefs.put(INPUT_DIR, sInputDir);
		}
		if(sProjectDirChanged)
		{
			Preferences prefs = Preferences.userNodeForPackage(UserPreferences.class);
			prefs.put(PROJECT_DIR, sProjectDir);
		}
	}
}