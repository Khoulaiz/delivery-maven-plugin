package com.sahlbach.maven.delivery.uploader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;
import java.net.URI;
import java.util.List;

/**
 * User: Andreas Sahlbach
 * Date: 05.08.11
 * Time: 23:02
 */
public class ScpUploader extends Uploader {

    @Override
    public void uploadFiles (List<File> filesToUpload, URI target) throws MojoFailureException {

        Session session = null;
        Channel channel = null;

        try {
            UserInfo userInfo = new UserInfo(target.getUserInfo());
            String host = target.getHost();
            String path = target.getPath();
            int port = target.getPort() == -1 ? 22 : target.getPort();

            JSch jsch = new JSch();
            session = jsch.getSession(userInfo.getUser(), host, port);
            session.setUserInfo(userInfo);

            session.connect();

            // exec 'scp -t path' remotely
            String command = "scp -p -t " + path;
            channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);

            channel.connect();
            if(checkAck(channel.getExtInputStream()) != 0) {
                throw new MojoFailureException("SCP failed.");
            }

            for (File file : filesToUpload) {
                uploadFile(channel, file);
            }

            channel.disconnect();
            channel = null;

            session.disconnect();
            session = null;
        } catch(Exception e){
            throw new MojoFailureException("SCP failed.",e);
        } finally {
            if(channel != null)
                channel.disconnect();
            if(session != null)
                session.disconnect();
        }
    }

    public void uploadFile(Channel channel, File file) throws MojoFailureException {
        FileInputStream fis = null;
        try {
            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            // send "C0644 filesize filename", where filename should not include '/'
            long filesize = file.length();
            String filePath = file.getAbsolutePath();
            String command = "C0644 " + filesize + " ";

            if(filePath.lastIndexOf('/') > 0){
                command += filePath.substring(filePath.lastIndexOf('/') + 1 );
            } else {
                command += filePath;
            }
            command += "\n";

            out.write(command.getBytes());
            out.flush();
            if(checkAck(in) != 0){
                throw new MojoFailureException("SCP failed.");
            }

            // send content of file
            fis = new FileInputStream(file);
            byte[] buf = new byte[1024];
            while(true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0)
                    break;
                out.write(buf, 0, len);
            }
            fis.close();
            fis = null;
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            if(checkAck(in) != 0){
                throw new MojoFailureException("SCP failed.");
            }
            out.close();
        }
        catch(Exception e){
            System.out.println(e);
            try{
                if( fis != null)
                    fis.close();
            } catch(Exception ee) {
                // empty on purpose
            }
        }
    }

    private int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0 ) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2){
            StringBuilder sb = new StringBuilder();
            int c;
            do {
                c = in.read();
                sb.append((char)c);
            }
            while( c != '\n');
            if (b == 1){ // error
                getLogger().error(sb.toString());
            }
            if (b==2) { // fatal error
                getLogger().error(sb.toString());
            }
        }
        return b;
    }

}
