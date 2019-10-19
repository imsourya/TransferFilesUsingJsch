
package com.saurabh.grg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader 
{
	volatile static private PropertyReader pr=null;
	
	private String remoteIP;
	private String remoteport;
	private String username;
	private String password;
	private String remoteFolderPath;
	private String fileExtension;
	private String localfolderpath;
	
	private  PropertyReader() throws FileNotFoundException, IOException
	{
		Properties _p = new Properties();
		//System.out.println(System.getProperty("user.dir"));
		String _Path = System.getProperty("user.dir")+"/config/transfer.properties";
		System.out.println("Reading "+_Path);
		_p.load(new FileInputStream(new File(_Path)));
		
		remoteIP = _p.getProperty("Remote.IP");
		remoteport = _p.getProperty("Remote.Port");
		username = _p.getProperty("Remote.User");
		password = _p.getProperty("Remote.Password");
		remoteFolderPath = _p.getProperty("Remote.Path");
		fileExtension = _p.getProperty("Remote.FileExtension");
		localfolderpath=_p.getProperty("Local.Path");
	}
	
	static PropertyReader getPropertyReader() throws FileNotFoundException, IOException
	{
		if(pr==null)
		{
			pr = new PropertyReader();
		}
		return pr;
	}

	public String getRemoteIP() {
		return remoteIP;
	}

	public String getRemoteport() {
		return remoteport;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getRemoteFolderPath() {
		return remoteFolderPath;
	}

	public String getFileExtension() {
		return fileExtension;
	}
	
	public String getLocalFolderPath() {
		return localfolderpath;
	}

}
