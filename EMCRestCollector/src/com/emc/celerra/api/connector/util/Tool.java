/**
*
* This source file is provided as a sample to show how to write a client application that communicates with the Celerra Management API.
* It should not be used as or considered as a full-fledged application.
**/


package com.emc.celerra.api.connector.util;

import  java.util.Date;
import  java.text.SimpleDateFormat;
import  java.util.Vector;
import  java.util.StringTokenizer;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Tool
{

    private static Date convDate = new Date();
    private static SimpleDateFormat shortForm = new SimpleDateFormat("MMM d, hh:mm:ss a");
    public static String formatShortCurrentLocalDate()
    {
        convDate.setTime(System.currentTimeMillis());
        return shortForm.format(convDate);
    }
    public static String formatShortLocalDate(long time)
    {
        convDate.setTime(time);
        return shortForm.format(convDate);
    }

   /**
    * Break string into an array of tokens. The delimiter is passed
    * to StringTokenizer for tokenizing the string.
    * @param str string to break up.
    * @param delim delimiter string.
    * @return token array.
    */
   public static String[] tokenize(String str, String delim) {
      Vector toks = new Vector();

      try {
	    StringTokenizer token = new StringTokenizer(str, delim);
	    while(token.hasMoreTokens()) {
	        toks.addElement(token.nextToken());
	    }
      }
      catch(Exception e) {
	    e.printStackTrace();
      }

      String[] ret = new String[toks.size()];
      toks.copyInto(ret);
      return ret;
   }

   public static boolean notEqual(String a, String b)
   {
    if( (a==null && b!=null) || (a!=null && b==null)) return true;
    return (!a.equals(b));
   }
    public static String stackTraceToString(Throwable exe)
    {
        try {
            ByteArrayOutputStream temp;
            PrintStream ps = new PrintStream(temp = new ByteArrayOutputStream(512));
            exe.printStackTrace(ps);
            ps.flush();
            return temp.toString("UTF-8");
        }
        catch(Exception ex) {
            ex.printStackTrace(System.out);
        }
        return "";
    }
    public static void threadSleep(int num)
    {
        try {
            Thread.sleep(num);
        }
        catch(InterruptedException ie) {}
    }
}

