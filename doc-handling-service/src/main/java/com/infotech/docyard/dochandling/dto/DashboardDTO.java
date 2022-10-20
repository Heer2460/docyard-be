package com.infotech.docyard.dochandling.dto;

import com.infotech.docyard.dochandling.util.AppUtility;
import lombok.Data;

import java.io.Serializable;

@Data
public class DashboardDTO {
    private ImageProps imageProps;
    private VideosProps videosProps;
    private DocsProps docsProps;
    private OthersProps othersProps;

    public DashboardDTO () {

    }

    private static class Properties implements Serializable {
        private Integer count;
        private Double occupiedSize;
        private Double totalSize = 1048576D;
        private Double occupiedPercentage;

        /*public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Double getOccupiedSize() {
            return occupiedSize;
        }

        public void setOccupiedSize(Double occupiedSize) {
            this.occupiedSize = occupiedSize;
        }

        public Double getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(Double totalSize) {
            this.totalSize = totalSize;
        }

        public Double getOccupiedPercentage() {
            return occupiedPercentage;
        }

        public void setOccupiedPercentage(Double occupiedPercentage) {
            this.occupiedPercentage = occupiedPercentage;
        }*/

        private Properties(Integer count, Double occupiedSize, Double totalSize) {
            this.count = count;
            this.occupiedSize = occupiedSize;
            this.totalSize = !AppUtility.isEmpty(totalSize) ? totalSize : 1048576D;
            this.occupiedPercentage = (occupiedSize/totalSize)*100;
        }
    }
    public static class ImageProps extends Properties  implements Serializable {
        public ImageProps(Integer count, Double occupiedSize, Double totalSize) {
            super(count, occupiedSize, totalSize);
        }
    }

    public static class VideosProps extends Properties  implements Serializable {
        public VideosProps(Integer count, Double occupiedSize, Double totalSize) {
            super(count, occupiedSize, totalSize);
        }
    }

    public static class DocsProps extends Properties  implements Serializable {
        public DocsProps(Integer count, Double occupiedSize, Double totalSize) {
            super(count, occupiedSize, totalSize);
        }
    }

    public static class OthersProps extends Properties  implements Serializable {
        public OthersProps(Integer count, Double occupiedSize, Double totalSize) {
            super(count, occupiedSize, totalSize);
        }
    }
    public DashboardDTO (ImageProps i, VideosProps v, DocsProps d, OthersProps o) {
        this.imageProps = i;
        this.videosProps = v;
        this.docsProps = d;
        this.othersProps = o;
    }
}
