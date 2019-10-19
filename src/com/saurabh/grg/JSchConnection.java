
package com.saurabh.grg;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JSchConnection 
{
	private JSch jsch=null; 
	private Session session=null;
	private Channel channel=null;
	private String serverIP;
	private String username;
	private String password;
	private static final int port=22; // SSH
	private java.util.Properties config = new java.util.Properties();
	public JSchConnection(String serverIP,String username,String password)
	{
		this.serverIP = serverIP;
		this.username = username;
		this.password = password;
		jsch = new JSch();
	}
	public Session getSession()
	{
		try
		{
		session = jsch.getSession(this.username, this.serverIP, port);
		session.setPassword(password);
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		System.out.println("Session creating ..");
		}
		catch(JSchException I)
		{
			throw new AssertionError("Problem in getting JSCH Session");
		}
		return session;
	}
	
	public Channel getChannel(String channeltype)
	{
		if(channel.isConnected())
		{
			channel.disconnect();
		}
		try {
			channel = session.openChannel("exec");
		} catch (JSchException e) {
			e.printStackTrace();
		}
		return channel;
	}
	public void closeChannel()
	{
		if(channel.isConnected())
		{
			channel.disconnect();
		}
	}
	public void clossSession()
	{
		if(session.isConnected())
		{
			session.disconnect();
		}
	}

}
