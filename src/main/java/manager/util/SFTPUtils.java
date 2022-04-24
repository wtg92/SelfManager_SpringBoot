package manager.util;

import java.io.InputStream;
import java.util.Properties;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;


public abstract class SFTPUtils {

        // ftp服务器地址
        private final static String host = "";
        // ftp服务器端口号默认为21
        private final static Integer port = null;
        // ftp登录账号
        private final static String username = "";
        // ftp登录密码
        private final static String password = "";
        
        private static Session initSession() throws JSchException {
        	JSch jsch = new JSch();
        	Session sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            //严格主机密钥检查
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            return sshSession;
        }
        
        /**
         */
        public static void uploadFile(String directory, String fileName,InputStream file) {
            
            Session sshSession = null;
            ChannelSftp sftp = null;
            try {
            	sshSession = initSession();
                sftp = (ChannelSftp) sshSession.openChannel("sftp");
                sftp.connect();
                makeDirsIfNotExited(directory,sftp);
                sftp.cd(directory);
                sftp.put(file, fileName);
            }catch (SftpException | JSchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}finally {
            	if(sftp != null) {
            		sftp.disconnect();
            	}
            	if(sshSession != null) {
            		sshSession.disconnect();
            	}
            }
        }

        /**
         * @throws SftpException 可能出现没有创建文件夹目录权限的情况 这时候需要服务器授权
         * @author 王天戈
         */
        private static void makeDirsIfNotExited(String directory, ChannelSftp sftp) throws SftpException {
        	try {
        		sftp.ls(directory);
        	}catch(SftpException e) {
        		if(e.id == 2) {
        			/*没有目录*/
        			/**
        			 * 根据上一层有没有递归
        			 */
        			String parentDirectory = directory.substring(0,directory.lastIndexOf("/"));
        			makeDirsIfNotExited(parentDirectory, sftp);
        			sftp.mkdir(directory);
        			return;
        		}
        		throw e;
        	}
		}
        public static InputStream downloadFile(String fileName) {
        	return downloadFile(null, fileName);
        }

		/**
         * * 下载文件 *
         *
         * @return
         */
        public static InputStream downloadFile(String directory, String fileName) {
        	Session sshSession = null;
            ChannelSftp sftp = null;
            try {
            	sshSession = initSession();
                sftp = (ChannelSftp) sshSession.openChannel("sftp");
                sftp.connect();
                if(directory != null) {
                	sftp.cd(directory);
                }
                return sftp.get(fileName);
            } catch (SftpException | JSchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
            	if(sftp != null) {
            		sftp.disconnect();
            	}
            	if(sshSession != null) {
            		sshSession.disconnect();
            	}
            }
        }

        public static void deleteFile(String fileName) {
        	deleteFile(null, fileName);
        }
        
        public static void deleteFile(String directory, String fileName) {
        	Session sshSession = null;
            ChannelSftp sftp = null;
            try {
            	sshSession = initSession();
                sftp = (ChannelSftp) sshSession.openChannel("sftp");
                sftp.connect();
                if(directory != null) {
                	sftp.cd(directory);
                }
                sftp.rm(fileName);
            } catch (SftpException | JSchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
            	if(sftp != null) {
            		sftp.disconnect();
            	}
            	if(sshSession != null) {
            		sshSession.disconnect();
            	}
            }
        }


}
