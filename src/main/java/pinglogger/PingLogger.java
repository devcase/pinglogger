package pinglogger;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PingLogger {

	public static void main(String[] args) {
		if(args == null || args.length < 2){
			System.out.println("Modo de execução: java -jar pinglogger.jar 192.168.1.104 c:\\\\cmd\\\\logger.exe 192.168.1.104");
			System.exit(0);
		}
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
		try {
			char dirSeparator = args[1].contains("\\") ? '\\' : '/';
			
			String ipAddress = args[0];
			String loggerCmd = args[1];
			String loggerDir = args[1].substring(0, args[1].lastIndexOf(dirSeparator));
			
			
			System.out.print(df.format(new Date()) + " : ");
			System.out.println("Executável logger: " + loggerCmd);
			System.out.println("Diretório logger: " + loggerDir);
			System.out.println("Endereço IP: " + ipAddress);

			File fileLoggerDir = new File(loggerDir);
			if(!fileLoggerDir.exists() || !fileLoggerDir.isDirectory()) {
				throw new IllegalArgumentException("Diretório " + fileLoggerDir + " não encontrado");
			}
			File loggerExe = new File(loggerCmd);
			if(!fileLoggerDir.exists()) {
				throw new IllegalArgumentException("Executável " + loggerExe + " não encontrado");
			}

			
			ProcessBuilder pingProcessBuilder = new ProcessBuilder("ping", "-n", "2", ipAddress); //executa 2 pings
			ProcessBuilder loggerProcessBuilder = new ProcessBuilder(loggerCmd);
			loggerProcessBuilder.directory(fileLoggerDir);
			
			
			//calculando tempo para iniciar exatamente às XX:00, XX:15, XX:30 ou XX:45
			long millisParaIniciar = (15 * 60 * 1000)- System.currentTimeMillis() % (15 * 60 * 1000);
			
			System.out.print(df.format(new Date()) + " : ");
			System.out.println("Aguardando " + ((double) millisParaIniciar / (60 * 1000)) + " minutos");
			//aguarda 15 minutos
			Thread.sleep(millisParaIniciar);
			
			while(true) {
				Process loggerProc = null;
				Process pingProc = null;
				try {
					
					//Executa ping
					System.out.print(df.format(new Date()) + " : ");
					System.out.println("Executando ping no IP " + ipAddress);
				    pingProc = pingProcessBuilder.start();
				    
					//aguarda 10 segundos 
					Thread.sleep(10 * 1000);
					
					//Executa logger
					System.out.print(df.format(new Date()) + " : ");
					System.out.println("Executando logger");
					loggerProc = loggerProcessBuilder.start();
					
					//calculando tempo para iniciar exatamente às XX:00, XX:15, XX:30 ou XX:45
					long millisParaRepetir = (15 * 60 * 1000)- System.currentTimeMillis() % (15 * 60 * 1000);
					
					System.out.print(df.format(new Date()) + " : ");
					System.out.println("Aguardando " + ((double) millisParaRepetir / (60 * 1000)) + " minutos");
					//aguarda 15 minutos
					Thread.sleep(millisParaRepetir);
					
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
