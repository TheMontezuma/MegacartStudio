package org.montezuma.megacart;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Project
{
	class ItemAddr {
		int start_addr;
		byte start_page;
		int end_addr;
		byte end_page;
	}
	
	static final int MAX_MODULE_TITLE_LEN=20;
	static final int MAX_GAME_TITLE_LEN=33;
	static final int MAX_NO_GAMES=303;
	static final int ROM512K_FILE_SIZE = 0x80000;
	static final int ROM1MB_FILE_SIZE = 0x100000;
    static final int ROM2MB_FILE_SIZE = 0x200000;
	static final int ROM4MB_FILE_SIZE = 0x3FC000;
	static final int TABLE_OF_CONTENTS_SIZE=0x3000;
	static final int FILE_LOADER_SIZE = 0x1000;
	static final int MAX_GAMES_SIZE_512K=ROM512K_FILE_SIZE-TABLE_OF_CONTENTS_SIZE-FILE_LOADER_SIZE;//0x7C000
	static final int MAX_GAMES_SIZE_1MB=ROM1MB_FILE_SIZE-TABLE_OF_CONTENTS_SIZE-FILE_LOADER_SIZE;//0xFC000
    static final int MAX_GAMES_SIZE_2MB=ROM2MB_FILE_SIZE-TABLE_OF_CONTENTS_SIZE-FILE_LOADER_SIZE;//0x1FC000
	static final int MAX_GAMES_SIZE_4MB=ROM4MB_FILE_SIZE-TABLE_OF_CONTENTS_SIZE-FILE_LOADER_SIZE;//0x3F8000
	static final int ATR_512K_SIZE = 0xFFD90;
	static final int ATR_1MB_SIZE = 0x1FFC90;
    static final int ATR_2MB_SIZE = 0x3FFD90;
	static final int ATR_4MB_SIZE = 0x7FFD90;
	static final int MEMORY_BANK_SIZE=0x4000;
	static final int START_ADDR_OF_ROM_IN_1MB_ATR = 0x17290;
	static final int START_ADDR_OF_ROM_IN_2MB_ATR = 0x2d890;
	static final int START_ADDR_OF_ROM_IN_4MB_ATR = 0x27290;
	static final int START_ADDR_OF_ROM_IN_8MB_ATR = 0x27A90;
	static final int START_ADDR_OF_16K_IN_8MB_ATR = 0x5A90;
	static final int APP_BYTES_PER_SECTOR = 253;
	static final int DOS_BYTES_PER_SECTOR = 3;
	static final int START_ADDRESS=0x8000;
	static final String FILE_NAME_512K_LOADER = "bdata512.rom";
	static final String FILE_NAME_4MB_LOADER = "bdata4mb.rom";
	static final String FILE_NAME_TEMPLATE_512K_ATR = "master1.atr";
	static final String FILE_NAME_TEMPLATE_1MB_ATR = "master2.atr";
    static final String FILE_NAME_TEMPLATE_2MB_ATR = "master4.atr";
	static final String FILE_NAME_TEMPLATE_4MB_ATR = "master8.atr";
	static final String FILE_NAME_512K = "FLASH512";
	static final String FILE_NAME_1MB = "FLASH1MB";
    static final String FILE_NAME_2MB = "FLASH2MB";
	static final String FILE_NAME_4MB = "FLASH4MB";
	static final String FILE_SUFFIX_ROM = ".ROM";
	static final String FILE_SUFFIX_CAR = ".CAR";
	static final String FILE_SUFFIX_XEX = ".XEX";
	static final String FILE_SUFFIX_ATR = ".ATR";
	static final String FILE_SUFFIX_TXT = ".TXT";
	static final String ASCII_ENCODING = "ASCII";
	static final byte START_PAGE_512K = (byte)(0x01);
	static final byte START_PAGE_4MB = (byte)0x00;
	static final byte FILL_BYTE = (byte)0xFF;
	static final byte HEADER_SIZE = 7;
	static final byte FOOTER_SIZE = 3;
	static final byte ATASCII_SPACE = (byte)0x00;
	static final byte ATASCII_SUBSTRACTION_FACTOR = (byte)0x20;
	static final byte ATASCII_THRESHOLD = (byte)0x61;
	static final int ROM8KB_FILE_SIZE = 0x2000;
	static final int ROM16KB_FILE_SIZE = 0x4000;
	static final int CAR_HEADER_LENGTH = 16;
	static final int ATR_OFFSET = 0xF0; // 240 bytes
	static final byte START_IN_EMU_MASK = (byte)0x80;
	static final byte DEFAULT_LOADER_TYPE = (byte)0x00;
	static final byte ROM_LOADER_TYPE = (byte)0x08;
	static final byte ATR_LOADER_TYPE = (byte)0x10;
    static final byte CART_512K_TYPE = (byte)0x1F;
    static final byte CART_1MB_TYPE = (byte)0x20;
    static final byte CART_2MB_TYPE = (byte)0x40;
    static final byte CART_4MB_TYPE = (byte)0x3F;
    
	static enum CartType {
	    CART_512KB,
	    CART_1MB,
        CART_2MB,
	    CART_4MB
	}
	
	private List<Item> mItems;
	private boolean mDirty; 
	private int mCurrentGamesBytes;
	private ProjectListener mListener;
	
	private static byte[] s512KLoader = null;
	private static byte[] s4MBLoader = null;
	private static byte[] s512KATR = null;
	private static byte[] s1MBATR = null;
    private static byte[] s2MBATR = null;
	private static byte[] s4MBATR = null;
	
	private byte[] getLoader(CartType type, String module_title) throws IOException
	{
		byte[] result = null;
		switch(type)
		{
			case CART_512KB:
			case CART_1MB:
            case CART_2MB:
				if(null==s512KLoader)
				{
					s512KLoader = new byte[FILE_LOADER_SIZE];
					InputStream is = ClassLoader.getSystemResourceAsStream(FILE_NAME_512K_LOADER);
					if(null==is)
					{
						is = new FileInputStream(FILE_NAME_512K_LOADER);
					}
					else
					{
						is = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(FILE_NAME_512K_LOADER));
					}
					try
					{
						if(FILE_LOADER_SIZE != is.read(s512KLoader,0,FILE_LOADER_SIZE))
						{
							throw new IOException("Loading "+FILE_NAME_512K_LOADER+" failed");
						}
					}
					finally
					{
						if(null!=is)
						{
							is.close();
						}
					}	
				}
				result = s512KLoader;
				break;
				
			case CART_4MB:
				if(null==s4MBLoader)
				{
					s4MBLoader = new byte[FILE_LOADER_SIZE];
					InputStream is = ClassLoader.getSystemResourceAsStream(FILE_NAME_4MB_LOADER);
					if(null==is)
					{
						is = new FileInputStream(FILE_NAME_4MB_LOADER);
					}
					else
					{
						is = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(FILE_NAME_4MB_LOADER));
					}
					try
					{
						if(FILE_LOADER_SIZE != is.read(s4MBLoader,0,FILE_LOADER_SIZE))
						{
							throw new IOException("Loading "+FILE_NAME_4MB_LOADER+" failed");
						}
					}
					finally
					{
						if(null!=is)
						{
							is.close();
						}
					}	
				}
				result = s4MBLoader;
				break;
			}
		
		byte[] title = module_title.toUpperCase().getBytes(ASCII_ENCODING);
		for(int i=0 ; i< title.length ; i++)
		{
			if(title[i] < ATASCII_THRESHOLD)
			{
				title[i] -= ATASCII_SUBSTRACTION_FACTOR;
			}
		}
		if(title.length >= MAX_MODULE_TITLE_LEN)
		{
			System.arraycopy(title, 0, result, 0xEE8, MAX_MODULE_TITLE_LEN);
		}
		else
		{
			byte[] spaces = new byte[MAX_MODULE_TITLE_LEN];
			Arrays.fill(spaces, ATASCII_SPACE);
			int difference = MAX_MODULE_TITLE_LEN - title.length;
			System.arraycopy(title, 0, spaces, difference/2, title.length);
			System.arraycopy(spaces, 0, result, 0xEE8, MAX_MODULE_TITLE_LEN);
		}
		
		return result;
	}

	private byte[] getATR(CartType type) throws IOException
	{
		byte[] result = null;
		switch(type)
		{
			case CART_512KB:
				if(null==s512KATR)
				{
					s512KATR = new byte[ATR_512K_SIZE];
					InputStream is = ClassLoader.getSystemResourceAsStream(FILE_NAME_TEMPLATE_512K_ATR);
					if(null==is)
					{
						is = new FileInputStream(FILE_NAME_TEMPLATE_512K_ATR);
					}
					else
					{
						is = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(FILE_NAME_TEMPLATE_512K_ATR));
					}
					try
					{
						if(ATR_512K_SIZE != is.read(s512KATR,0,ATR_512K_SIZE))
						{
							throw new IOException("Loading "+FILE_NAME_TEMPLATE_512K_ATR+" failed");
						}
					}
					finally
					{
						if(null!=is)
						{
							is.close();
						}
					}	
				}
				result = s512KATR;
				break;
                
			case CART_1MB:
				if(null==s1MBATR)
				{
					s1MBATR = new byte[ATR_1MB_SIZE];
					InputStream is = ClassLoader.getSystemResourceAsStream(FILE_NAME_TEMPLATE_1MB_ATR);
					if(null==is)
					{
						is = new FileInputStream(FILE_NAME_TEMPLATE_1MB_ATR);
					}
					else
					{
						is = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(FILE_NAME_TEMPLATE_1MB_ATR));
					}

					try
					{
						if(ATR_1MB_SIZE != is.read(s1MBATR,0,ATR_1MB_SIZE))
						{
							throw new IOException("Loading "+FILE_NAME_TEMPLATE_1MB_ATR+" failed");
						}
					}
					finally
					{
						if(null!=is)
						{
							is.close();
						}
					}	
				}
				result = s1MBATR; 
				break;


			case CART_2MB:
				if(null==s2MBATR)
				{
					s2MBATR = new byte[ATR_2MB_SIZE];
					InputStream is = ClassLoader.getSystemResourceAsStream(FILE_NAME_TEMPLATE_2MB_ATR);
					if(null==is)
					{
						is = new FileInputStream(FILE_NAME_TEMPLATE_2MB_ATR);
					}
					else
					{
						is = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(FILE_NAME_TEMPLATE_2MB_ATR));
					}

					try
					{
						if(ATR_2MB_SIZE != is.read(s2MBATR,0,ATR_2MB_SIZE))
						{
							throw new IOException("Loading "+FILE_NAME_TEMPLATE_2MB_ATR+" failed");
						}
					}
					finally
					{
						if(null!=is)
						{
							is.close();
						}
					}	
				}
				result = s2MBATR; 
				break;

			case CART_4MB:
				if(null==s4MBATR)
				{
					s4MBATR = new byte[ATR_4MB_SIZE];
					InputStream is = ClassLoader.getSystemResourceAsStream(FILE_NAME_TEMPLATE_4MB_ATR);
					if(null==is)
					{
						is = new FileInputStream(FILE_NAME_TEMPLATE_4MB_ATR);
					}
					else
					{
						is = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(FILE_NAME_TEMPLATE_4MB_ATR));
					}

					try
					{
						if(ATR_4MB_SIZE != is.read(s4MBATR,0,ATR_4MB_SIZE))
						{
							throw new IOException("Loading "+FILE_NAME_TEMPLATE_4MB_ATR+" failed");
						}
					}
					finally
					{
						if(null!=is)
						{
							is.close();
						}
					}	
				}
				result = s4MBATR; 
				break;
			
		}
		return result;
	}
	
	Project(ProjectListener listener)
	{
		mItems = new ArrayList<Item>();
		mDirty = false;
		mCurrentGamesBytes = 0;
		mListener = listener;
	}
	
	public void saveAllItems() throws IOException
	{
		for(int i=0 ; i<mItems.size(); i++)
		{
			saveItem(i);
		}
	}
	
	public void saveItem(int index) throws IOException
	{
		FileOutputStream fos = null;
		Item item = mItems.get(index);
		String suffix;
		byte[] content = item.mContent;
		switch(item.mLoaderType)
		{
			case ROM_LOADER_TYPE:
				suffix = FILE_SUFFIX_ROM;
				boolean extended = true;
				for(int i=0;i<ROM8KB_FILE_SIZE;i++)
				{
					if(content[i] != (byte)0xFF)
					{
						extended = false;
						break;
					}
				}
				if(extended)
				{
					content=Arrays.copyOfRange(content, ROM8KB_FILE_SIZE, ROM16KB_FILE_SIZE);
				}
				break;
			case ATR_LOADER_TYPE:
				suffix = FILE_SUFFIX_ATR;
				break;
			default:	
				suffix = FILE_SUFFIX_XEX;
				break;
		}
		String outputfilename = UserPreferences.sOutputDir+File.separator+item.mGameTitle+suffix;
		try
		{
			fos = new FileOutputStream(outputfilename);
			fos.write(content);
		}
		finally
		{
			if(null!=fos)
			{
				fos.close();
			}
		}
	}
	
	public void testItem(int selectedRow, CartType type, String module_title) throws IOException
	{
		File temp = null;
		FileOutputStream fos = null;
		
		List<Item> items = new ArrayList<Item>();
		Item prev_item = null;
		Item item = mItems.get(selectedRow);
		
		if(ATR_LOADER_TYPE == item.mLoaderType)
		{
			int i = selectedRow;
			while(i>0)
			{
				prev_item = mItems.get(i-1);
				item = mItems.get(i);
				if(itemsBelong2MultiATR(prev_item, item))
				{
					i--;
				}
				else
				{
					break;
				}
			}
			item = mItems.get(i);
			items.add(item);
			while(i<mItems.size()-1)
			{
				prev_item = mItems.get(i);
				item = mItems.get(i+1);
				if(itemsBelong2MultiATR(prev_item, item))
				{
					items.add(item);
					i++;
				}
				else
				{
					break;
				}
			}
		}
		else
		{
			items.add(item);
		}
		
		try
		{
			temp = File.createTempFile("megacart", ".car");
			temp.deleteOnExit();
			
			byte[] car_header = new byte[CAR_HEADER_LENGTH];
			
			// car header
			car_header[0] = 'C';
			car_header[1] = 'A';
			car_header[2] = 'R';
			car_header[3] = 'T';
			car_header[4] = 0;
			car_header[5] = 0;
			car_header[6] = 0;
			
			car_header[12] = 0;
			car_header[13] = 0;
			car_header[14] = 0;
			car_header[15] = 0;
			
			ByteArrayOutputStream bos = null;
			
			switch(type)
			{
				case CART_512KB:
					car_header[7] = CART_512K_TYPE;
					bos = new ByteArrayOutputStream(CAR_HEADER_LENGTH+ROM512K_FILE_SIZE);
					bos.write(car_header);
					tableOfContents2Rom(bos,items,CartType.CART_512KB, true);
					bos.write(getLoader(CartType.CART_512KB, module_title));
					atariFiles2Rom(bos,items,CartType.CART_512KB);
					break;
				case CART_1MB:
					car_header[7] = CART_1MB_TYPE;
					bos = new ByteArrayOutputStream(CAR_HEADER_LENGTH+ROM1MB_FILE_SIZE);
					bos.write(car_header);
					tableOfContents2Rom(bos,items,CartType.CART_1MB, true);
					bos.write(getLoader(CartType.CART_1MB, module_title));
					atariFiles2Rom(bos,items,CartType.CART_1MB);
					break;
				case CART_2MB:
					car_header[7] = CART_2MB_TYPE;
					bos = new ByteArrayOutputStream(CAR_HEADER_LENGTH+ROM2MB_FILE_SIZE);
					bos.write(car_header);
					tableOfContents2Rom(bos,items,CartType.CART_2MB, true);
					bos.write(getLoader(CartType.CART_2MB, module_title));
					atariFiles2Rom(bos,items,CartType.CART_2MB);
					break;
				case CART_4MB:
					car_header[7] = CART_4MB_TYPE;
					bos = new ByteArrayOutputStream(CAR_HEADER_LENGTH+ROM4MB_FILE_SIZE+MEMORY_BANK_SIZE);
					bos.write(car_header);
					atariFiles2Rom(bos,items,CartType.CART_4MB);
					tableOfContents2Rom(bos,items,CartType.CART_4MB, true);
					bos.write(getLoader(CartType.CART_4MB, module_title));
					// add the last page to reach 4MB rom size
					byte[] b = new byte[MEMORY_BANK_SIZE];
					Arrays.fill(b, FILL_BYTE);
					bos.write(b);
					break;
			}

			byte[] car_content = bos.toByteArray();
		
			int checksum = 0;
			for(int i=CAR_HEADER_LENGTH; i<car_content.length; i++)
			{
				checksum += ((int)(car_content[i])) & 0xff;
			}
			
			car_content[8]  = (byte)((checksum >> 24) & 0xff);
			car_content[9]  = (byte)((checksum >> 16) & 0xff);
			car_content[10] = (byte)((checksum >> 8)  & 0xff);
			car_content[11] = (byte)(checksum & 0xff);

			fos = new FileOutputStream(temp);
			fos.write(car_content);
			fos.close();

			String[] cmdarray;
			if(UserPreferences.isMac())
			{
				if(UserPreferences.sEmulatorSwitch.isEmpty())
				{
					cmdarray = new String[3];
					cmdarray[0] = "open";
					cmdarray[1] = UserPreferences.sEmulatorPath;
					cmdarray[2] = temp.getAbsolutePath();
					
				}
				else
				{
					cmdarray = new String[4];
					cmdarray[0] = "open";
					cmdarray[1] = UserPreferences.sEmulatorPath;
					cmdarray[2] = UserPreferences.sEmulatorSwitch;
					cmdarray[3] = temp.getAbsolutePath();
				}
			}
			else
			{
				if(UserPreferences.sEmulatorSwitch.isEmpty())
				{
					cmdarray = new String[2];
					cmdarray[0] = UserPreferences.sEmulatorPath;
					cmdarray[1] = temp.getAbsolutePath();
					
				}
				else
				{
					cmdarray = new String[3];
					cmdarray[0] = UserPreferences.sEmulatorPath;
					cmdarray[1] = UserPreferences.sEmulatorSwitch;
					cmdarray[2] = temp.getAbsolutePath();
				}
			}
			Runtime r = Runtime.getRuntime();
			r.exec(cmdarray);
		}
		finally
		{
			if(null != fos)
			{
				fos.close();
			}
		}
	}

	public void generate(String file_path_without_suffix, CartType type, String module_title) throws IOException
	{
		FileOutputStream fos = null;
		try
		{
			byte[] car_header = new byte[CAR_HEADER_LENGTH];
			
			// car header
			car_header[0] = 'C';
			car_header[1] = 'A';
			car_header[2] = 'R';
			car_header[3] = 'T';
			car_header[4] = 0;
			car_header[5] = 0;
			car_header[6] = 0;
			
			car_header[12] = 0;
			car_header[13] = 0;
			car_header[14] = 0;
			car_header[15] = 0;
			
			ByteArrayOutputStream bos = null;
			ByteArrayInputStream bis = null;
			int atr_index = 0;
			
			switch(type)
			{
				case CART_512KB:
					atr_index = START_ADDR_OF_ROM_IN_1MB_ATR;
					car_header[7] = CART_512K_TYPE;
					bos = new ByteArrayOutputStream(CAR_HEADER_LENGTH+ROM512K_FILE_SIZE);
					bos.write(car_header);
					tableOfContents2Rom(bos,mItems,CartType.CART_512KB, false);
					bos.write(getLoader(CartType.CART_512KB, module_title));
					atariFiles2Rom(bos,mItems,CartType.CART_512KB);
					break;
				case CART_1MB:
					atr_index = START_ADDR_OF_ROM_IN_2MB_ATR;
					car_header[7] = CART_1MB_TYPE;
					bos = new ByteArrayOutputStream(CAR_HEADER_LENGTH+ROM1MB_FILE_SIZE);
					bos.write(car_header);
					tableOfContents2Rom(bos,mItems,CartType.CART_1MB, false);
					bos.write(getLoader(CartType.CART_1MB, module_title));
					atariFiles2Rom(bos,mItems,CartType.CART_1MB);
					break;
                case CART_2MB:
					atr_index = START_ADDR_OF_ROM_IN_4MB_ATR;
					car_header[7] = CART_2MB_TYPE;
					bos = new ByteArrayOutputStream(CAR_HEADER_LENGTH+ROM2MB_FILE_SIZE);
					bos.write(car_header);
					tableOfContents2Rom(bos,mItems,CartType.CART_2MB, false);
					bos.write(getLoader(CartType.CART_2MB, module_title));
					atariFiles2Rom(bos,mItems,CartType.CART_2MB);
					break;
				case CART_4MB:
					atr_index = START_ADDR_OF_ROM_IN_8MB_ATR;
					car_header[7] = CART_4MB_TYPE;
					bos = new ByteArrayOutputStream(CAR_HEADER_LENGTH+ROM4MB_FILE_SIZE+MEMORY_BANK_SIZE);
					bos.write(car_header);
					atariFiles2Rom(bos,mItems,CartType.CART_4MB);
					tableOfContents2Rom(bos,mItems,CartType.CART_4MB, false);
					bos.write(getLoader(CartType.CART_4MB, module_title));
					// add the last page to reach 4MB rom size
					byte[] b = new byte[MEMORY_BANK_SIZE];
					Arrays.fill(b, FILL_BYTE);
					bos.write(b);
					break;
			}

			byte[] car_content = bos.toByteArray();
		
			int checksum = 0;
			for(int i=CAR_HEADER_LENGTH; i<car_content.length; i++)
			{
				checksum += ((int)(car_content[i])) & 0xff;
			}
			
			car_content[8]  = (byte)((checksum >> 24) & 0xff);
			car_content[9]  = (byte)((checksum >> 16) & 0xff);
			car_content[10] = (byte)((checksum >> 8)  & 0xff);
			car_content[11] = (byte)(checksum & 0xff);

			if(UserPreferences.sEnableCarGeneration)
			{
				fos = new FileOutputStream(file_path_without_suffix+FILE_SUFFIX_CAR);
				fos.write(car_content);
				fos.close();
			}
			if(UserPreferences.sEnableRomGeneration)
			{
				fos = new FileOutputStream(file_path_without_suffix+FILE_SUFFIX_ROM);
				fos.write(car_content,CAR_HEADER_LENGTH,car_content.length-CAR_HEADER_LENGTH);
				fos.close();
			}
			if(UserPreferences.sEnableTxtGeneration)
			{
				fos = new FileOutputStream(file_path_without_suffix+FILE_SUFFIX_TXT);
				writeTOC(fos);
				fos.close();
			}

			bis = new ByteArrayInputStream(car_content,CAR_HEADER_LENGTH,car_content.length-CAR_HEADER_LENGTH);
			fos = new FileOutputStream(file_path_without_suffix+FILE_SUFFIX_ATR);
			
			int bytes_read = 0;
			while (bytes_read != -1)
			{
				bytes_read = bis.read(getATR(type),atr_index,APP_BYTES_PER_SECTOR);
				atr_index+=bytes_read;
				atr_index+=DOS_BYTES_PER_SECTOR;
			}
			
			// special header for easy flasher optimization
			if(type==CartType.CART_4MB)
			{
				bytes_read = 0;
				atr_index = START_ADDR_OF_16K_IN_8MB_ATR;
				bis = new ByteArrayInputStream(car_content,CAR_HEADER_LENGTH+MAX_GAMES_SIZE_4MB,MEMORY_BANK_SIZE);
				while (bytes_read != -1)
				{
					bytes_read = bis.read(getATR(type),atr_index,APP_BYTES_PER_SECTOR);
					atr_index+=bytes_read;
					atr_index+=DOS_BYTES_PER_SECTOR;
				}				
			}

			fos.write(getATR(type));

		}
		finally
		{
			if(null != fos)
			{
				fos.close();
			}
		}
	}
	
	private void writeTOC(FileOutputStream fos) throws IOException
	{
		for(Item item: mItems)
		{
			fos.write(item.mGameTitle.getBytes(ASCII_ENCODING));
			fos.write(UserPreferences.sLineSeparator.getBytes(ASCII_ENCODING));
		}		
	}
	
	private void tableOfContents2Rom(OutputStream fos, List<Item> items, CartType typ, boolean automatic_start) throws IOException
	{
		int bytes_written = 0;
		byte start_page = (CartType.CART_4MB == typ)?START_PAGE_4MB:START_PAGE_512K;

		ItemAddr[] addresses = new ItemAddr[items.size()];
		byte[] loader_types = new byte[items.size()];
		byte next_page = start_page;
		int next_address = START_ADDRESS;
		int length = 0;

		int item_index = 0;
		for(Item item: items)
		{
			if(ROM_LOADER_TYPE == item.mLoaderType)
			{
				addresses[item_index] = new ItemAddr();
				addresses[item_index].start_page = next_page;
				addresses[item_index].start_addr = next_address;
				length+=ROM16KB_FILE_SIZE;
				next_page = (byte)((length / MEMORY_BANK_SIZE) + start_page);
				next_address = length - ((next_page-start_page) *  MEMORY_BANK_SIZE) + START_ADDRESS;
				addresses[item_index].end_page = (byte)(next_page-1);
				addresses[item_index].end_addr = 0xBFFF;				
			}
			item_index++;
		}

		item_index = 0;
		for(Item item: items)
		{
			if(ATR_LOADER_TYPE == item.mLoaderType)
			{
				addresses[item_index] = new ItemAddr();
				addresses[item_index].start_page = next_page;
				addresses[item_index].start_addr = next_address+ATR_OFFSET;
				// the content of an ATR file (ATR-header) has to be aligned to $100 address
				// this means 240 bytes at the beginning and filling at the end to the memory page border
				int to_be_filled = (ATR_OFFSET+item.mContent.length)%(0x100);
				length += (ATR_OFFSET + item.mContent.length + to_be_filled);
				next_page = (byte)((length / MEMORY_BANK_SIZE) + start_page);
				next_address = length - ((next_page-start_page) *  MEMORY_BANK_SIZE) + START_ADDRESS;
				addresses[item_index].end_page = (byte)(next_page-1);
				addresses[item_index].end_addr = 0xBFFF;				
			}
			item_index++;
		}
		
		item_index = 0;
		for(Item item: items)
		{
			if((item.mLoaderType != ROM_LOADER_TYPE) && (item.mLoaderType != ATR_LOADER_TYPE))
			{
				addresses[item_index] = new ItemAddr();
				addresses[item_index].start_page = next_page;
				addresses[item_index].start_addr = next_address;
				length+=item.mContent.length;
				next_page = (byte)((length / MEMORY_BANK_SIZE) + start_page);
				next_address = length - ((next_page-start_page) *  MEMORY_BANK_SIZE) + START_ADDRESS;
				if(next_address == START_ADDRESS)
				{
					addresses[item_index].end_page = (byte)(next_page-1);
					addresses[item_index].end_addr = 0xBFFF;
				}
				else
				{
					addresses[item_index].end_page = next_page;
					addresses[item_index].end_addr = next_address-1;					
				}
			}
			item_index++;
		}
			
// Der erste ATR File hat die Kennung (Bootloader) $10 plus die Anzahl der Disketten z.b. zwei Disketten = $11, drei $12 usw
// Alle dazugehörigen ATR Files werden mit $20 (Bootloader) gekennzeichnet.
		
		item_index = 0;
		int first_atr_item_index = 0;
		Item prev_item = null;
		for(Item item: items)
		{
			loader_types[item_index] = item.mLoaderType;
			if(itemsBelong2MultiATR(prev_item, item))
			{
				loader_types[first_atr_item_index]++;
				loader_types[item_index] = (byte)(0x20);
			}
			else if(ATR_LOADER_TYPE == item.mLoaderType)
			{
				first_atr_item_index = item_index;
			}
			item_index++;
			prev_item = item;
		}
		
		item_index = 0;
		for(Item item: items)
		{
			byte[] header = new byte[HEADER_SIZE];
			header[0] = (byte)((addresses[item_index].start_addr) & 0xff);
			header[1] = (byte)(((addresses[item_index].start_addr) >> 8) & 0xff);
			header[2] = addresses[item_index].start_page;
			if(automatic_start)
			{
				header[3] = (byte)((loader_types[item_index]) | START_IN_EMU_MASK);
			}
			else
			{
				header[3] = loader_types[item_index];
			}
			header[4] = (byte)((addresses[item_index].end_addr) & 0xff);
			header[5] = (byte)(((addresses[item_index].end_addr) >> 8) & 0xff);
			header[6] = addresses[item_index].end_page;
			fos.write(header);
			
			byte[] game_title = item.mGameTitle.getBytes(ASCII_ENCODING);
			for(int i=0 ; i< game_title.length ; i++)
			{
				if(game_title[i] < ATASCII_THRESHOLD)
				{
					game_title[i] -= ATASCII_SUBSTRACTION_FACTOR;
				}
			}
			if(game_title.length >= MAX_GAME_TITLE_LEN)
			{
				fos.write(game_title,0,MAX_GAME_TITLE_LEN);
			}
			else
			{
				fos.write(game_title);
				byte[] spaces = new byte[MAX_GAME_TITLE_LEN-game_title.length];
				Arrays.fill(spaces, ATASCII_SPACE);
				fos.write(spaces);
			}
			bytes_written += (HEADER_SIZE + MAX_GAME_TITLE_LEN);
			item_index++;
		}
		
		byte[] footer = new byte[FOOTER_SIZE];
		footer[0] = (byte)(next_address & 0xff);
		footer[1] = (byte)((next_address >> 8) & 0xff);
		footer[2] = next_page;
		fos.write(footer);
		bytes_written += FOOTER_SIZE;
		
		int to_be_filled = TABLE_OF_CONTENTS_SIZE - bytes_written;
		if(to_be_filled>0)
		{
			byte[] b = new byte[to_be_filled];
			Arrays.fill(b, FILL_BYTE);
			fos.write(b);
		}		
	}
	
	private void atariFiles2Rom(OutputStream fos, List<Item> items, CartType typ) throws IOException
	{
		int bytes_written = 0;
		for(Item item: items)
		{
			if(ROM_LOADER_TYPE == item.mLoaderType)
			{
				fos.write(item.mContent);
				bytes_written += item.mContent.length;
			}
		}
		for(Item item: items)
		{
			if(ATR_LOADER_TYPE == item.mLoaderType)
			{
				byte[] b = new byte[ATR_OFFSET];
				Arrays.fill(b, FILL_BYTE);
				fos.write(b);
				fos.write(item.mContent);
				int to_be_filled = (ATR_OFFSET+item.mContent.length)%(0x100);
				if(to_be_filled!=0)
				{
					b = new byte[to_be_filled];
					Arrays.fill(b, FILL_BYTE);
					fos.write(b);
				}
				bytes_written += ATR_OFFSET;
				bytes_written += item.mContent.length;
				bytes_written += to_be_filled;
			}
		}
		for(Item item: items)
		{
			if((item.mLoaderType != ROM_LOADER_TYPE) && (item.mLoaderType != ATR_LOADER_TYPE))
			{
				fos.write(item.mContent);
				bytes_written += item.mContent.length;
			}
		}
		int to_be_filled = 0;
		switch(typ)
		{
		case CART_512KB:
			to_be_filled = MAX_GAMES_SIZE_512K-bytes_written;
			break;
		case CART_1MB:
			to_be_filled = MAX_GAMES_SIZE_1MB-bytes_written;
			break;
        case CART_2MB:
			to_be_filled = MAX_GAMES_SIZE_2MB-bytes_written;
			break;
		case CART_4MB:
			to_be_filled = MAX_GAMES_SIZE_4MB-bytes_written;
			break;
		}
		if(to_be_filled<0)
		{
			throw new IOException("TooManyGames");
		}
		else if(0!=to_be_filled)
		{
			byte[] b = new byte[to_be_filled];
			Arrays.fill(b, FILL_BYTE);
			fos.write(b);
		}
	}
	
	private boolean itemsBelong2MultiATR(Item prev_item, Item item)
	{
		return (	(prev_item!=null) && 
					(ATR_LOADER_TYPE == prev_item.mLoaderType) &&
					(ATR_LOADER_TYPE == item.mLoaderType) &&
					(prev_item.mGameTitle.length()>1) &&
					(prev_item.mGameTitle.length() == item.mGameTitle.length()) &&
					(prev_item.mGameTitle.startsWith(item.mGameTitle.substring(0, item.mGameTitle.length()-1))) &&
					(prev_item.mGameTitle.charAt(prev_item.mGameTitle.length()-2) == 'D') &&
					Character.isDigit(prev_item.mGameTitle.charAt(prev_item.mGameTitle.length()-1)) &&
					Character.isDigit(item.mGameTitle.charAt(item.mGameTitle.length()-1)) &&
					(Character.digit(prev_item.mGameTitle.charAt(prev_item.mGameTitle.length()-1), 10) + 1 == 
					Character.digit(item.mGameTitle.charAt(item.mGameTitle.length()-1), 10))
					);
	}
	
	public int getCount()
	{
		return mItems.size();
	}
	
	public Item getMegacartItem(int index)
	{
		return mItems.get(index);
	}
	
	public boolean addMegacartItem(int index, Item item)
	{
		if(mItems.size() < MAX_NO_GAMES)
		{
			mCurrentGamesBytes += item.mContent.length;
			if(ATR_LOADER_TYPE == item.mLoaderType)
			{
				mCurrentGamesBytes += ATR_OFFSET;
				mCurrentGamesBytes += (ATR_OFFSET+item.mContent.length)%(0x100);
			}
			mDirty = true;
			mItems.add(index, item);
			mListener.onGamesBytes(mCurrentGamesBytes);
			mListener.onGamesCount(mItems.size());
			return true;
		}
		return false;
	}
	
	public Item removeMegacartItem(int index)
	{
		Item item = mItems.remove(index); 
		mCurrentGamesBytes -= item.mContent.length;
		if(ATR_LOADER_TYPE == item.mLoaderType)
		{
			mCurrentGamesBytes -= ATR_OFFSET;
			mCurrentGamesBytes -= (ATR_OFFSET+item.mContent.length)%(0x100);
		}
		mDirty = true;
		mListener.onGamesBytes(mCurrentGamesBytes);
		mListener.onGamesCount(mItems.size());
		return item;
	}
	
	public void touch()
	{
		mDirty = true;
	}
	
	public void sort()
	{
		Collections.sort(mItems, new Comparator<Item>(){
			public int compare(Item o1, Item o2) {
				return o1.mGameTitle.toUpperCase().compareTo(o2.mGameTitle.toUpperCase());
			}});
		mDirty = true;
	}
	
	@SuppressWarnings("unchecked")
	void load(File f) throws Exception
	{
		FileInputStream fis = null;
		try
		{
			fis  = new FileInputStream(f);
			GZIPInputStream is = new GZIPInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(is);
			List<Item> tmp = (ArrayList<Item>)ois.readObject();
			mItems = tmp; 
			ois.close();
			mDirty = false;
			mCurrentGamesBytes = 0;
			for(Item item: mItems)
			{
				mCurrentGamesBytes += item.mContent.length;
			}
			mListener.onGamesBytes(mCurrentGamesBytes);
			mListener.onGamesCount(mItems.size());			
		}
		finally
		{
			if(null != fis)
			{
				fis.close();
			}
		}
	}
	
	void save(File f) throws IOException
	{
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(f);
			GZIPOutputStream os = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(mItems);
			oos.close();
			mDirty = false;
		}
		finally
		{
			if(null != fos)
			{
				fos.close();
			}	
		}
	}
	
	boolean hasChanged()
	{
		return mDirty;
	}
}
