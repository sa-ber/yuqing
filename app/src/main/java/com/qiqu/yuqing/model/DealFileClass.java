package com.qiqu.yuqing.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;


public final class DealFileClass 
{
	//��һ�ļ�����
	public static boolean copyFile(String szSrcFile, String szDestFile)
	{
		File file = new File(szSrcFile);
		if (!file.exists()) 
		{
			return false;
		}
		try 
		{
			RandomAccessFile fSrcFile = new RandomAccessFile(szSrcFile, "rw");
			RandomAccessFile fDestFile = new RandomAccessFile(szDestFile, "rw"); 
			int nLen = (int)fSrcFile.length();
			byte[] bData = new byte[nLen];
			fSrcFile.readFully(bData);
			fDestFile.write(bData);
			fSrcFile.close();
			fDestFile.close();
			
			Runtime.getRuntime().exec("sync");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return true;
	}

    //�ļ�������
    public static boolean renameFile(String srcPath, String desPath) 
	{
		if (srcPath == null) 
		{
			return false;
		}
		try 
		{
			File srcFile = new File(srcPath);
			if (!srcFile.exists()) 
			{
				return false;
			}
			File desFile = new File(desPath);
			if (srcFile.renameTo(desFile))
			{
				Runtime.getRuntime().exec("sync");
				return true;
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
	}
	
	//�ж��ļ��Ƿ����
	public static boolean isFileExist(String path) 
	{
		if (path == null) 
		{
			return false;
		}
		try 
		{
			File f = new File(path);
			if (!f.exists()) 
			{
				return false;
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return true;
	}
	
    //ɾ����һ�ļ�
	public static void deleteFile(String filename)
	{
		if ( filename == null || filename.equals(""))
		{
			return;
		}
		File file = new File(filename);
		if ( !file.exists() )
		{
		    return;
		}
		
		if ( file.isFile() )
		{
			file.delete();
		}
		try {
			Runtime.getRuntime().exec("sync");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//����ļ�������
	public static void cleanDirectory( String filePath )
	{
		if ( filePath == null || filePath.equals(""))
		{
			return;
		}
		File f = new File(filePath);
		String[] list = f.list();
		
		for ( int i = 0;list!=null&&i < list.length; i++ )
		{
			String name = list[i];
			File file = new File(filePath + "/" + name);
			if ( file.isFile() )	//�����ļ���ֱ��ɾ��
			{
				file.delete();
			}
			else if ( file.isDirectory() )	//�����ļ��У������������
			{
				deleteDirectory(filePath + "/" + name);
			}
		}
		try {
			Runtime.getRuntime().exec("sync");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//�ݹ�ɾ���ļ���
	public static void deleteDirectory( String filePath )
	{
		if ( filePath == null || filePath.equals(""))
		{
			return;
		}
		File f = new File(filePath);
		String[] list = f.list();
		for ( int i = 0; list != null && i < list.length; i++ )
		{
			String name = list[i];
			File file = new File(filePath + "/" + name);
			if ( file.isFile() )	//�����ļ���ֱ��ɾ��
			{
				file.delete();
			}
			else if ( file.isDirectory() )	//�����ļ��У������������
			{
				deleteDirectory(filePath + "/" + name);
			}
		}
		f.delete();	//��󽫿��ļ���ɾ��
	}

	
	//��ȡϵͳ������Ϣ
	public static String getSystemInfo( String key, String defValue )
	{
		File file = new File( "/mnt/internal_sd/config/sysconfig.properties" );
		
		if( file.exists() == false )
		{
			return "";
		}
		try
		{
			//���������ļ���������
			FileInputStream fileStream = new  FileInputStream( file );			
			Properties systemInfo = new Properties();
			systemInfo.load(fileStream);
			fileStream.close();
			return systemInfo.getProperty( key );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return defValue;
	}
	
	
	//����ϵͳ������Ϣ
	public static boolean setSystemInfo( String key, String value )
	{
		if ( (key == null || key.equals("")) || (value == null || value.equals("")) )
		{
			return false;
		}
		
		//��Ϣ��ͬ��������
		if ( getSystemInfo( key, "ERROR" ) != null )
		{
			if ( getSystemInfo( key, "ERROR" ).equals(value) == true )
			{
				return true;
			}
		}
		
		try
		{
			boolean hasFile = true;
			Properties systemInfo = new Properties();
			File file = new File( "/mnt/internal_sd/config/sysconfig.properties" );
			if( file.exists() == true )//���ļ����ڣ�����ļ��еõ���������ֵ
			{
				FileInputStream inStream = new  FileInputStream( file );
				systemInfo.load(inStream);
				inStream.close();
			}
			else
			{
				hasFile = false;
			}
			
			//��ʼ��������Ϣ
			systemInfo.setProperty( key, value );
			FileOutputStream outStream = new FileOutputStream( file );
			systemInfo.store(outStream, null);
			
			//����ļ�Ϊ�´����ļ���������Ȩ��
			if (hasFile == false)
			{
				Runtime.getRuntime().exec("chmod 0777 /mnt/internal_sd/config/sysconfig.properties");
			}
			Runtime.getRuntime().exec("sync");
		}
		catch( Exception e )
		{
			e.printStackTrace(); 
		}
		
		return true;
	}

}
