package com.utilities;

import java.io.*;

public class RegQuery {

  private static final String REGQUERY_UTIL = "reg query ";
  private static final String REGSTR_TOKEN = "REG_SZ";
  private static final String ROBOT_CARTES = REGQUERY_UTIL +
		    "\"HKLM\\SOFTWARE\\WOW6432Node\\Cartes\""
		     + " /v \"Cartes Client\" ";

  //Para validar todas las posibles variantes y opciones de consultas, colocar lo siguiente en la CMD:
  //REG QUERY /?

  public static String getPathInstalationRobotCartes() 
  {
    try 
    {
    	Process process = Runtime.getRuntime().exec(ROBOT_CARTES);
    	StreamReader reader = new StreamReader(process.getInputStream());
    	reader.start();
    	process.waitFor();
    	reader.join();

    	String result = reader.getResult();
    	int p = result.indexOf(REGSTR_TOKEN);

    	if (p == -1)
    		return null;

      return result.substring(p + REGSTR_TOKEN.length()).trim();
    }
    catch (Exception e) {
      return null;
    }
  }

  static class StreamReader extends Thread {
    private InputStream is;
    private StringWriter sw;

    StreamReader(InputStream is) {
      this.is = is;
      sw = new StringWriter();
    }

    public void run() {
      try {
        int c;
        while ((c = is.read()) != -1)
          sw.write(c);
        }
        catch (IOException e) { ; }
      }

    String getResult() {
      return sw.toString();
    }
  }
}