package com.infotech.docyard.dochandling.service;

import com.infotech.docyard.dochandling.config.SFTPProperties;
import com.infotech.docyard.dochandling.util.AppUtility;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

@Service
@Log4j2
public class FTPService {

    @Autowired
    private SFTPProperties config;

//    public InputStream downloadInputStream(String targetPath) throws Exception {
//        FTPClient ftpClient = this.createFtp();
//        OutputStream outputStream = null;
//        try {
//            ftpClient.listFiles();
//            ftpClient.listDirectories();
//            String root = config.getRoot();
//            ftpClient.changeWorkingDirectory(root);
//            log.info("Change path to {}", root);
//            File file = new File(targetPath.substring(targetPath.lastIndexOf("/") + 1));
//            outputStream = Files.newOutputStream(file.toPath());
//            ftpClient.listFiles();
//            ftpClient.listDirectories();
//            boolean downloaded = ftpClient.retrieveFile("/", outputStream);
//            downloaded = ftpClient.retrieveFile(root, outputStream);
//            if(!downloaded){
//                log.error("Download file failure. TargetPath: {}", targetPath);
//                throw new Exception("Download File failure");
//            }
//            log.info("Download file success. TargetPath: {}", targetPath);
//            return new FileInputStream(file);
//        } catch (Exception e) {
//            log.error("Download file failure. TargetPath: {}", targetPath, e);
//            throw new Exception("Download File failure");
//        } finally {
//            if (outputStream != null) {
//                outputStream.close();
//            }
//            this.disconnect(ftpClient);
//        }
//    }

    public File downloadFile(String targetPath) throws Exception {
        FTPClient ftpClient = this.createFtp();
        OutputStream outputStream = null;
        try {
            ftpClient.changeWorkingDirectory(config.getRoot());
            log.info("Change path to {}", config.getRoot());

            File file = new File(targetPath.substring(targetPath.lastIndexOf("/") + 1));

            outputStream = Files.newOutputStream(file.toPath());
            ftpClient.retrieveFile(targetPath, outputStream);
            log.info("Download file success. TargetPath: {}", targetPath);
            return file;
        } catch (Exception e) {
            log.error("Download file failure. TargetPath: {}", targetPath, e);
            throw new Exception("Download File failure");
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            this.disconnect(ftpClient);
        }
    }

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

    public boolean deleteDirectory(String targetPath, String fileName) throws Exception {
        log.info("FTP deleteDirectory method called.. " + config.getRoot());

        FTPClient ftpClient = createFtp();
        try {
            ftpClient.printWorkingDirectory();
            FTPFile[] subFiles = ftpClient.listFiles(targetPath);
            FTPFile[] subDirs = ftpClient.listDirectories(targetPath);

            if (subFiles != null && subFiles.length > 0) {
                for (FTPFile aFile : subFiles) {
                    String currentFileName = aFile.getName();
                    if (currentFileName.equals(".") || currentFileName.equals("..")) {
                        // skip parent directory and the directory itself
                        continue;
                    }
                    String filePath = targetPath + currentFileName;
                    if (aFile.isDirectory()) {
                        // remove the subdirectory
                        deleteDirectory(filePath, currentFileName);
                    } else {
                        // delete the file
                        boolean deleted = ftpClient.deleteFile(currentFileName);
                        if (deleted) {
                            log.info("DELETED the file: " + currentFileName);
                        } else {
                            log.info("CANNOT delete the file: " + currentFileName);
                        }
                    }
                }
                // finally, remove the directory itself
                boolean removed = ftpClient.removeDirectory(targetPath);
                if (removed) {
                    System.out.println("REMOVED the directory: " + targetPath);
                } else {
                    System.out.println("CANNOT remove the directory: " + targetPath);
                }
            }
            boolean exist = ftpClient.deleteFile(fileName);
            if (!exist) {
                log.error("Remote path error. path:{}", targetPath);
                throw new Exception("Delete File failure");
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
