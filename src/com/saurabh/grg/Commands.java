
package com.saurabh.grg;

public class Commands
{
	/////////////////// Shell Commands ///////////////
	
	public static String ChangeDir="cd";
	public static String FindInPath(String extensions)
	{
		//return "find . -name \"*."+extension+"\"";
		String extensionList[] = extensions.split(",");
		StringBuilder sbul = new StringBuilder("find -iname ");
		for(int i=0;i<extensionList.length;i++)
		{
			if(i!=0)
			{
				sbul.append(" -o -iname \"*."+extensionList[i].toLowerCase()+"\"");
			}
			else
			{
				sbul.append("\"*."+extensionList[i].toLowerCase()+"\"");
			}
		}
		return sbul.toString();
	}
}
