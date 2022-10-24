package com.infotech.docyard.cjs.service;

import com.infotech.docyard.cjs.config.SFTPProperties;
import com.infotech.docyard.cjs.util.AppUtility;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Service
@Log4j2
public class FTPService {

    @Autowired
    private SFTPProperties config;

    public InputStream downloadInputStream(String targetPath) throws Exception {
        FTPClient ftpClient = createFtp();
        try {
            ftpClient.changeWorkingDirectory(config.getRoot());
            log.info("Download file success. TargetPath: {}", targetPath);
            return ftpClient.retrieveFileStream(targetPath);
        } catch (Exception e) {
            log.error("Download file failure. TargetPath: {}", targetPath, e);
            throw new Exception("Download File failure");
        } finally {
            this.disconnect(ftpClient);
        }
    }

    public boolean deleteFile(String targetPath, String fileName) throws Exception {
        log.info("FTP deleteFile method called.. " + config.getRoot());

        FTPClient ftpClient = createFtp();
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles();
            ftpClient.changeWorkingDirectory(config.getRoot());
            ftpFiles = ftpClient.listFiles();
            ftpFiles = Arrays.stream(ftpFiles).filter(file -> file.getName().equals(fileName)).toArray(FTPFile[]::new);
            if ((!AppUtility.isEmpty(ftpFiles)) && (ftpFiles.length != 0)) {
                boolean deleted = ftpClient.deleteFile(fileName);
                if (!deleted) {
                    log.error("Remote path error. path:{}", targetPath);
                    throw new Exception("Delete File failure");
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Delete file failure. TargetPath: {}", targetPath, e);
            throw new Exception("Delete File failure");
        } finally {
            this.disconnect(ftpClient);
        }
    }

    private FTPClient createFtp() {
        FTPClient ftpClient = new FTPClient();
        try {
            log.info("Try to connect ftp[" + config.getUsername() + "@" + config.getHost() + "], " +
                    "use password[" + config.getPassword() + "]");
            ftpClient.connect(config.getHost(), config.getPort());
            ftpClient.login(config.getUsername(), config.getPassword());
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            System.err.println("ERROR :: FTP Server Unreachable");
        }
        return ftpClient;
    }

    private void disconnect(FTPClient ftpClient) {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
