package com.karapeno.Jserver.configLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.karapeno.Jserver.Server;

/**
 * this class implment a configuration reader
 * 
 * @author karapeno
 */

public class configLoader{
	
	/**
	 * read INI format configuration file
	 * be cautious that this module don't
	 * process the config
	 */
	
	public static int readConfig(){
		String path = Server.configPath;
		int configState = 0;
		try (FileReader configHandle = new FileReader(path)) {
			BufferedReader config = new BufferedReader(configHandle);
			try {
				String content = null;
				while ( 
				(content = config.readLine()) != null
				) {
					content = content.trim();
		            if (!content.isEmpty()) {
						//pass content to parser
		                configState = configParser.parse(content);
		            };
				};
				config.close();
				return configState;
			} catch ( IOException e ) {
				System.err.println(
				"[ Error ] Failed to read config file:"+e.getMessage()
				);
			}
		} catch (IOException e) {
			System.err.println(
				"[ Error ] Configuratoin File Not Found"
				);
		}
		
		return configState;
	}
}