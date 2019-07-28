package org.montezuma.megacart;

import java.io.Serializable;

public class Item implements Serializable
{
	private static final long serialVersionUID = -6326939198836178484L;
	
	String	mGameTitle;
	byte[]	mContent;
	byte	mLoaderType;
}
