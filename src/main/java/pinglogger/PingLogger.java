package pinglogger;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PingLogger {

	public static void main(String[] args) {
		if(args == null || args.length < 2){
			System.out.println("Modo de execu��o: java -jar pinglogger.jar 192.168.1.104 c:\\\\cmd\\\\logger.exe 192.168.1.104");
			System.exit(0);
		}
		try {
			char dirSeparator = args[1].contains("\\") ? '\\' : '/';
			
			String ipAddress = args[0];
			String loggerCmd = args[1];
			String loggerDir = args[1].substring(0, args[1].lastIndexOf(dirSeparator));
			
			
			System.out.println("Execut�vel logger: " + loggerCmd);
			System.out.println("Diret�rio logger: " + loggerDir);
			System.out.println("Endere�o IP: " + ipAddress);

			File fileLoggerDir = new File(loggerDir);
			if(!fileLoggerDir.exists() || !fileLoggerDir.isDirectory()) {
				throw new IllegalArgumentException("Diret�rio " + fileLoggerDir + " n�o encontrado");
			}
			File loggerExe = new File(loggerCmd);
			if(!fileLoggerDir.exists()) {
				throw new IllegalArgumentException("Execut�vel " + loggerExe + " n�o encontrado");
			}

			
			ProcessBuilder pingProcessBuilder = new ProcessBuilder("ping", "-n", "2", ipAddress);
			ProcessBuilder loggerProcessBuilder = new ProcessBuilder(loggerCmd);
			loggerProcessBuilder.directory(fileLoggerDir);
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy - HH:mm"); 
			
			while(true) {
				Process loggerProc = null;
				Process pingProc = null;
				try {
					//Executa ping
					System.out.print(df.format(new Date()));
					System.out.println(" - Executando ping no IP " + ipAddress);
				    pingProc = pingProcessBuilder.start();
				    
					//aguarda 10 segundos 
					Thread.sleep(10 * 1000);
					
					//Executa logger
					System.out.print(df.format(new Date()));
					System.out.println(" - Executando logger");
					loggerProc = loggerProcessBuilder.start();
					
					//aguarda 15 minutos
					Thread.sleep(15 * 60 * 1000 - 10 * 1000);
				} finally {
					if(pingProc != null) {
						try { pingProc.destroy(); } catch (Exception ex) { ex.printStackTrace(); }
					}
					if(loggerProc != null) {
						try { loggerProc.destroy(); } catch (Exception ex) { ex.printStackTrace(); }
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

}
