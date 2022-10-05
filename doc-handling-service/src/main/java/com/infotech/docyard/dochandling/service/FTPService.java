package com.infotech.docyard.dochandling.service;

import com.infotech.docyard.dochandling.config.SFTPProperties;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
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

    public boolean uploadFile(String targetPath, String fileName, InputStream inputStream) throws Exception {
        log.info("FTP upload file method called.. " + config.getRoot());

        FTPClient ftpClient = createFtp();
        try {
            ftpClient.changeWorkingDirectory(config.getRoot());
            log.info("Change path to " + config.getRoot());

            int index = targetPath.lastIndexOf("/");
            String fileDir = targetPath.substring(0, index);
            boolean dirs = this.createDirs(fileDir, ftpClient);
            if (!dirs) {
                log.error("Remote path error. path:{}", targetPath);
                throw new Exception("Upload File failure");
            }
            ftpClient.storeFile(fileName, inputStream);
            return true;
        } catch (Exception e) {
            log.error("Upload file failure. TargetPath: {}", targetPath, e);
            throw new Exception("Upload File failure");
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

    private boolean createDirs(String dirPath, FTPClient sftp) {
        if (dirPath != null && !dirPath.isEmpty()
                && sftp != null) {
            String[] dirs = Arrays.stream(dirPath.split("/"))
                    .filter(StringUtils::isNotBlank)
                    .toArray(String[]::new);

            for (String dir : dirs) {
                try {
                    sftp.changeWorkingDirectory(dir);
                    log.info("Change directory {}", dir);
                } catch (Exception e) {
                    try {
                        sftp.makeDirectory(dir);
                        log.info("Create directory {}", dir);
                    } catch (IOException e1) {
                        log.error("Create directory failure, directory:{}", dir, e1);
                        e1.printStackTrace();
                    }
                    try {
                        sftp.makeDirectory(dir);
                        log.info("Change directory {}", dir);
                    } catch (IOException e1) {
                        log.error("Change directory failure, directory:{}", dir, e1);
                        e1.printStackTrace();
                    }
                }
            }
            return true;
        }
        return false;
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
