package org.montezuma.megacart;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import javax.swing.JOptionPane;

public class Messages
{
	private static final String BUNDLE_NAME = "org.montezuma.megacart.messages";
	private static Control sControl = new UTF8Control();
	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME,Locale.ENGLISH,sControl);
	
	private Messages() {}

	public static void setLocale(Locale locale)
	{
		try
		{
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME,locale,sControl);
			JOptionPane.setDefaultLocale(locale);
		}
		catch (Exception e)
		{
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME,Locale.ENGLISH,sControl);
			JOptionPane.setDefaultLocale(Locale.ENGLISH);
		}
	}
	
	public static String getString(String key)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e)
		{
			return '!' + key + '!';
		}
	}
}
