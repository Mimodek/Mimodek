package MimodekV2.imageexport;

/*
This is the code source of Mimodek. When not stated otherwise,
it was written by Jonathan 'Jonsku' Cremieux<jonathan.cremieux@aalto.fi> in 2010. 
Copyright (C) yyyy  name of author

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.util.Debug;

import MimodekV2.Mimodek;
import MimodekV2.MimodekLocation;


import MimodekV2.config.Configurator;
import MimodekV2.debug.Verbose;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UserInfo;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

public class SFTPClient implements Runnable {
	protected static SFTPClient instance;
	protected static int instanceCount = 0;
	/* Connection data */
	private MyUserInfo ui;
	public String hostName;
	public int port = 22;
	private JSch jsch;
	private MyProgressMonitor progressMonitor;
	private Session session;
	private Channel channel;

	/* Image data */
	// Texture latestSnap;
	JpgWithExifTextureWriter textureWriter;
	MimodekLocation location;
	long latestSnapTimeStamp;

	/* File path info */
	String cachePath;
	String remotePath;

	long latestUploadTimeStamp;
	private boolean running = false;
	private Thread runner;
	
	private int imageNum = 0;

	public static SFTPClient createInstance(String user, String pass, String hostName,String cachePath, String remotePath, JpgWithExifTextureWriter textureWriter, MimodekLocation location){
		if(instance == null){
			instance = new SFTPClient(user, pass, hostName, 22, cachePath, remotePath,textureWriter, location);
		}
		return instance;
	
	}
	
	public static SFTPClient getInstance(){
		Verbose.overRule("SFTPClient instance #"+instanceCount);
		return instance;
	}
	
	private SFTPClient(String user, String pass, String hostName, int port,
			String cachePath, String remotePath, JpgWithExifTextureWriter textureWriter, MimodekLocation location) {
		this.hostName = hostName;
		this.port = port;
		// username and password will be given via UserInfo interface.
		ui = new MyUserInfo(user, pass);
		this.location = location;
		
		this.cachePath = cachePath;
		this.remotePath = remotePath;
		
		this.textureWriter = textureWriter;

		jsch = new JSch();
		progressMonitor = new MyProgressMonitor();
		runner = new Thread(this);
		
		//This cause the thread to execute its loop once before actually taking a snapshot.
		//Remove those lines if this beahaviour is not what you want.
		latestUploadTimeStamp = System.currentTimeMillis();
		latestSnapTimeStamp = latestUploadTimeStamp; 
		instanceCount++;

	}

	private SFTPClient(String user, String pass, String hostName,
			String cachePath, String remotePath, JpgWithExifTextureWriter textureWriter,  MimodekLocation location) {
		this(user, pass, hostName, 22, cachePath, remotePath, textureWriter,  location);
	}

	public void cacheSnap(Texture t) throws IOException {
		try {
			latestSnapTimeStamp = System.currentTimeMillis();
			//with the custom writer, the texture won't be written to disk just yet but converted to a BufferedImage 
			TextureIO.write(t, new File(cachePath + "/" + latestSnapTimeStamp + "." + Configurator.getStringSetting("UPLOAD_FORMAT")));
		} finally {
			//the texture can be safely destroyed
			t.dispose();
			t = null;
		}
	}

	public void clearCache() {
		// Declare variables
		File fLogDir = new File(cachePath);

		// Get all BCS files
		File[] fLogs = fLogDir.listFiles(new FilenameFilter() {
			public boolean accept(File fDir, String strName) {
				return (strName.endsWith("." + Configurator.getStringSetting("UPLOAD_FORMAT")));
			}
		});

		// Delete all files
		for (int i = 0; i < fLogs.length; i++) {
			deleteFile(fLogs[i].getAbsolutePath());
		}
	}

	public static void deleteFile(String fileName) {
		try {
			// Construct a File object for the file to be deleted.
			File target = new File(fileName);

			if (!target.exists()) {
				return;
			}

			// Quick, now, delete it immediately:
			if (!target.delete())
				System.err.println("Failed to delete " + fileName);
		} catch (SecurityException e) {
			System.err.println("Unable to delete " + fileName + "("
					+ e.getMessage() + ")");
		}
	}
	
	private void connect() throws JSchException{
		session = jsch.getSession(ui.getUser(), hostName, port);

		session.setUserInfo(ui);

		session.connect();

		channel = session.openChannel("sftp");
		channel.connect();
	}

	// sftp://[ username [: password ]@] hostname [: port ][ absolute-path ]
	private void sendFile(String source, String destination)
			throws JSchException, SftpException {
		if(channel == null || !channel.isConnected())
			 connect();
		
		ChannelSftp c = (ChannelSftp) channel;
		Verbose.debug("Remote home " + c.getHome());
		int mode = ChannelSftp.OVERWRITE;
		c.put(source, destination, progressMonitor, mode);
	}

	public void start() {
		if(running)
			return;
		running = true;
		runner.start();
	}

	public void stop() {
		if(channel != null)
			channel.disconnect();
		if(session != null)
			session.disconnect();
		running = false;
	}
	
	/*
	 * Useful to save a screenshot
	 */
	public void saveSnapShot(String dirPath, String fileName) throws ImageWriteException, ImageReadException, IOException{
		textureWriter.writeToDisk(dirPath,fileName,location,System.currentTimeMillis());
	}

	public void run() {
		Verbose.debug("SFTP Client started");
		while (running) {
			try {
				if ( latestUploadTimeStamp < latestSnapTimeStamp) {
					boolean uploadOk = true;
					try {
						//now the texture is written to disk before being sent
						if(textureWriter.writeToDisk(cachePath,""+location.city+location.country+"_"+imageNum,location,latestSnapTimeStamp)){
							
							sendFile(cachePath + "/" + location.city+location.country+"_"+imageNum + "."
									+ Configurator.getStringSetting("UPLOAD_FORMAT"), remotePath + "/"
									+ latestSnapTimeStamp + "." + Configurator.getStringSetting("UPLOAD_FORMAT"));
							imageNum++;
						}else{
							Verbose.debug("The texture writer didn't write the texture to disk...");
						}
					} catch (JSchException e) {
						uploadOk = false;
						e.printStackTrace();
					} catch (SftpException e) {
						uploadOk = false;
						e.printStackTrace();
					} catch (ImageWriteException e) {
						// TODO Auto-generated catch block
						Debug.dumpStack();
						uploadOk = false;
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						uploadOk = false;
						e.printStackTrace();
					} catch (ImageReadException e) {
						// TODO Auto-generated catch block
						uploadOk = false;
						e.printStackTrace();
					}
					if (uploadOk) {
						latestUploadTimeStamp = System.currentTimeMillis();
						if(Configurator.getBooleanSetting("CLEAR_CACHE_FLAG"))
							clearCache();
					}
				}
				if(Configurator.getBooleanSetting("POST_PICTURES_FLAG"))
					Mimodek.takeSnapShot = true;
				Thread.sleep((long) (Configurator
						.getFloatSetting("UPLOAD_RATE") * 60 * 1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	class MyUserInfo implements UserInfo {
		String passwd;
		String user;

		public MyUserInfo(String user, String pass) {
			this.user = user;
			this.passwd = pass;
		}

		public String getPassword() {
			return passwd;
		}

		public String getUser() {
			return user;
		}

		public boolean promptYesNo(String str) {
			return true;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			{
				return true;
			}
		}

		public void showMessage(String arg0) {
			Verbose.debug(arg0);
		}

	}

	class MyProgressMonitor implements SftpProgressMonitor {
		float total;
		float remaining;

		public boolean count(long count) {

			remaining += count;
			Verbose.debug("Transfer progress: " + (remaining / total)
					* 100f + " %");
			return true;
		}

		public void end() {
			Verbose.debug("Transfer Done");

		}

		public void init(int arg0, String arg1, String arg2, long arg3) {
			Verbose.debug("Transferring " + arg1 + " to " + arg2 + " ("
					+ arg3 + " bytes)");
			total = arg3;
			remaining = 0;
		}

	}
}
