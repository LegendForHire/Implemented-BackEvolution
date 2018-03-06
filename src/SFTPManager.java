/**
 * SFTPManager.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SFTPManager {

	public static void start() {
		
		Thread thread5 = new Thread(){
        	public void run(){
        		Singleton s = Singleton.getInstance();
        		while(true) {
    				Thread thread = new Thread() {
    					public void run() {
        		
        		while(true){       			
        			try {       			
        			JSch jsch = new JSch();
        			Session session = jsch.getSession("LegendForHire", "71.71.87.235");
        			session.setPassword("tFA45w&5f");
        			session.setConfig("StrictHostKeyChecking", "no");
        	        session.connect();
        	        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        	        sftp.connect();
        	        s.setChannel(sftp);
        	        long t1 = System.currentTimeMillis();
        			while(System.currentTimeMillis()-t1 < 3600000);
        			sftp.disconnect();
        			session.disconnect();
        			} 
        			catch (JSchException e) {
				}
        		}
    					}
    				};
    			thread.start();
    			try {
					thread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					s.getWriter().println(e.getMessage());
				}
        		}
        	}
        };
        thread5.start();
		
	}

}
