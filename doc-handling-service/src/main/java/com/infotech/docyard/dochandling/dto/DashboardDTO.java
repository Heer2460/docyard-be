package com.infotech.docyard.dochandling.dto;

import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.DocumentUtil;
import lombok.Data;

@Data
public class DashboardDTO {
    private ImageProps imageProps;
    private VideosProps videosProps;
    private DocsProps docsProps;
    private OthersProps othersProps;

    public DashboardDTO() {

    }

    @Data
    public static class Properties {
        private Integer count;
        private Double occupiedSize;
        private String formattedOccupiedSize;
        private Double totalSize = 52428800000D;
        private String formattedTotalSize;
        private Double occupiedPercentage;

        public Properties() {

        }

        public Properties(Integer count, Double occupiedSize, Double totalSize) {
            this.count = count;
            this.occupiedSize = occupiedSize;
            this.formattedOccupiedSize = DocumentUtil.getFileSize(this.occupiedSize);
            this.totalSize = !AppUtility.isEmpty(totalSize) ? totalSize : 52428800000D;
            this.formattedTotalSize = DocumentUtil.getFileSize(this.totalSize);
            this.occupiedPercentage = (this.occupiedSize / this.totalSize) * 100;
        }
    }

    @Data
    public static class ImageProps extends Properties {
        public ImageProps() {

        }

        public ImageProps(Integer count, Double occupiedSize, Double totalSize) {
            super(count, occupiedSize, totalSize);
        }
    }

    @Data
    public static class VideosProps extends Properties {
        public VideosProps() {

        }

        public VideosProps(Integer count, Double occupiedSize, Double totalSize) {
            super(count, occupiedSize, totalSize);
        }
    }

    @Data
    public static class DocsProps extends Properties {
        public DocsProps() {

        }

        public DocsProps(Integer count, Double occupiedSize, Double totalSize) {
            super(count, occupiedSize, totalSize);
        }
    }

    @Data
    public static class OthersProps extends Properties {
        public OthersProps() {

        }

        public OthersProps(Integer count, Double occupiedSize, Double totalSize) {
            super(count, occupiedSize, totalSize);
        }
    }

    public DashboardDTO(ImageProps i, VideosProps v, DocsProps d, OthersProps o) {
        this.imageProps = i;
        this.videosProps = v;
        this.docsProps = d;
        this.othersProps = o;
    }
}
