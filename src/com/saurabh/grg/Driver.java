
package com.saurabh.grg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;


public class Driver 
{
	static JSchConnection jcon;
	
	public static void main(String[] args)
	{
		PropertyReader prop=null;
		try{
			prop = PropertyReader.getPropertyReader();
		}		
		catch(FileNotFoundException e)
		{
			System.out.println("Unable to locate file transfer.properties");
			throw new AssertionError("Unable to locate file transfer.properties");
		}
		catch(IOException e)
		{
			System.out.println("IO exception while reading transfer.properties");
			throw new AssertionError("IO exception while reading transfer.properties");
		}

		jcon = new JSchConnection(prop.getRemoteIP(), prop.getUsername(), prop.getPassword());
		Session session = jcon.getSession();
		try {
			session.connect();
		} catch (JSchException e) {
			e.printStackTrace();
			throw new AssertionError("Unable to make Session Try again after verifying Properties details");
		}
		String[] paths = getTargetFilePaths(session,prop);
		System.out.println("total Found Files : "+paths.length);
		System.out.println("\nDownloading ....");
		getTargetFiles(session,prop,paths);
        session.disconnect();
        System.out.println("\nDownload Completed !");
	}

	private static void getTargetFiles(Session session, PropertyReader prop,String[] paths) 
	{
		try
		{
			Channel channel=session.openChannel("sftp");
			channel.connect();
			ChannelSftp c=(ChannelSftp)channel;

			HashMap<String,List<String>> map = getMap(paths);
			
			//System.out.println("Lel 1    "+prop.getLocalFolderPath());
			try{
			c.lcd(prop.getLocalFolderPath());
            c.cd(prop.getRemoteFolderPath());
			}
			catch(SftpException e)
			{
				System.out.println("Not able to make FTP connection : SFTP exception");
				throw new AssertionError(" Assert : check Folder path in properties file");
			}
            //System.out.println("Local Directory :  "+c.lpwd());
            //System.out.println("Remote Directory :  "+c.pwd());

			for(String path : map.keySet())
			{
				for(String filename : map.get(path)){
					//System.out.println("Changing to path "+path.trim()+"  file name :"+filename);
					if(path.trim().length()!=0) c.cd(prop.getRemoteFolderPath()+path.trim());
					//System.out.println("After Change Remote Directory :  "+c.pwd());
					String dest = getReqFormat(path,filename);
					c.get(filename,dest);
				}
			}
			channel.disconnect();
		}
		catch(JSchException e)
		{
			System.out.println("given Remote/Local path in properties file might be wrong ");
			throw new AssertionError("given Remote/Local path in properties file might be wrong ");
		}
		catch(SftpException e)
		{
			System.out.println("Not able to make FTP connection : SFTP exception");
			throw new AssertionError("Not able to make FTP connection : SFTP exception");
		}
	}	

	private static String[] getTargetFilePaths(Session session,PropertyReader prop) 
	{
		String listOfPath=null;
		Channel channel=null;
		try{
			channel = session.openChannel("exec");
			String Command = Commands.ChangeDir+" "+prop.getRemoteFolderPath()+" && "+ Commands.FindInPath(prop.getFileExtension());
			//System.out.println("Command : "+Command);
			((ChannelExec)channel).setCommand(Command);
			InputStream in=channel.getInputStream();
			byte[] tmp=new byte[1024];
			channel.connect();
			if(channel.isConnected())
			{
				//System.out.println("Channel connected");
			}
			while(true)
			{
				while(in.available()>0){
					int i=in.read(tmp, 0, 1024);
					if(i<0)break;
					listOfPath+=new String(tmp, 0, i);
				}
				if(channel.isClosed())
				{
					break;
				}
				else
				{
					try{Thread.sleep(1000);}catch(Exception ee){}
				}
			}
		}
		catch(JSchException e)
		{
			System.out.println("Authentication failure check username/password");
			throw new AssertionError("Authentication failure check username/password");
			
		}
		catch(IOException e)
		{
			System.out.println(e);
			throw new AssertionError("Authentication failure check username/password");
		}
		channel.disconnect();
        String temp = listOfPath.substring(listOfPath.indexOf(".")!=-1?listOfPath.indexOf("."):0,listOfPath.length());
		System.out.println();
		return temp.split("\n");
	}
	
	private static HashMap<String, List<String>> getMap(String[] paths) {
		HashMap<String, List<String>> hmap = new HashMap<String,List<String>>();
		
		for(String path : paths)
		{
			if(path.charAt(0)=='.')
			{
				path = path.substring(1, path.length());
			}
			String temp[]=path.split("/");
			String temp2=null;
			int length=0;
			if(temp.length>0)
			{
				temp2 = temp[temp.length-1];
				length=temp2.length();
			}
			String var=path.substring(0,path.length()-length);
			if(hmap.containsKey(var))
			{
				hmap.get(var).add(temp2);
			}
			else
			{
				List<String> var2 = new LinkedList<String>();
				var2.add(temp2);
				hmap.put(var, var2);
			}
		}
		
		/*System.out.println("PATH                file name");
		for(String path : hmap.keySet())
		{
			System.out.println(path+"              "+hmap.get(path));
		}
		*/
		return hmap;
	}
	
	private static String getReqFormat(String path,String name) {
		if(path.contains("\\"))
		{
		   path = path.replace('\\', '_');	
		}
		if(path.contains("/"))
		{
			path = path.replace('/', '_');
		}
		if(path.charAt(0)=='_')
		{
			path=path.substring(1, path.length());
		}
		
		return path+name;
	}

}
