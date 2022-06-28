package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayerSummaryCamReportResponse {
    private List<CamRecord> records = new ArrayList<>();
    private CamTotal total;
    private CamTotal subTotal;
    private int pageIndex;
    private int pageSize;
    private int pageCount;
    private int itemCount;
    private List<String> agentTree;

    public List<CamRecord> getRecords() {
        return records;
    }

    public void setRecords(List<CamRecord> records) {
        this.records = records;
    }

    public CamTotal getTotal() {
        return total;
    }

    public void setTotal(CamTotal total) {
        this.total = total;
    }

    public CamTotal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(CamTotal subTotal) {
        this.subTotal = subTotal;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public RecordBuilder newRecordBuilder() {
        return new RecordBuilder(new CamRecord());
    }

    public void addRecord(CamRecord record) {
        this.records.add(record);
    }

    public List<String> getAgentTree() {
        return agentTree;
    }

    public void setAgentTree(List<String> agentTree) {
        this.agentTree = agentTree;
    }

    @JsonIgnore
    public BigDecimal getRecordsSumCamWin() {
        return BigDecimal.valueOf(this.getRecords().stream().map(CamRecord::getCamWin).mapToDouble(BigDecimal::doubleValue).sum());
    }

    public static class RecordBuilder {
        private CamRecord camRecord;

        public RecordBuilder(CamRecord camRecord) {
            this.camRecord = camRecord;
        }

        public CamRecord build() {
            return this.camRecord;
        }

        public RecordBuilder accountID(String accountID) {
            this.camRecord.setAccountID(accountID);
            return this;
        }
        public RecordBuilder gameTurn(Long gameTurn) {
            this.camRecord.setGameTurn(gameTurn);
            return this;
        }
        public RecordBuilder camAwardTypeID(int camAwardTypeID) {
            this.camRecord.setCamAwardTypeID(camAwardTypeID);
            return this;
        }
        public RecordBuilder camAwardTypeName(String camAwardTypeName) {
            this.camRecord.setCamAwardTypeName(camAwardTypeName);
            return this;
        }
        public RecordBuilder camAwardCreateDateTime(Date camAwardCreateDateTime) {
            this.camRecord.setCamAwardCreateDateTime(camAwardCreateDateTime);
            return this;
        }
        public RecordBuilder camWin(BigDecimal camWin) {
            this.camRecord.setCamWin(camWin);
            return this;
        }
        public RecordBuilder currency(String currency) {
            this.camRecord.setCurrency(currency);
            return this;
        }


    }

    public class CamRecord {
        private String accountID;
        private long gameTurn;
        private int camAwardTypeID;
        private String camAwardTypeName;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private Date camAwardCreateDateTime;
        private BigDecimal camWin;
        private String currency;

        public String getAccountID() {
            return accountID;
        }

        public void setAccountID(String accountID) {
            this.accountID = accountID;
        }

        public long getGameTurn() {
            return gameTurn;
        }

        public void setGameTurn(long gameTurn) {
            this.gameTurn = gameTurn;
        }

        public int getCamAwardTypeID() {
            return camAwardTypeID;
        }

        public void setCamAwardTypeID(int camAwardTypeID) {
            this.camAwardTypeID = camAwardTypeID;
        }

        public String getCamAwardTypeName() {
            return camAwardTypeName;
        }

        public void setCamAwardTypeName(String camAwardTypeName) {
            this.camAwardTypeName = camAwardTypeName;
        }

        public Date getCamAwardCreateDateTime() {
            return camAwardCreateDateTime;
        }

        public void setCamAwardCreateDateTime(Date camAwardCreateDateTime) {
            this.camAwardCreateDateTime = camAwardCreateDateTime;
        }

        public BigDecimal getCamWin() {
            return camWin;
        }

        public void setCamWin(BigDecimal camWin) {
            this.camWin = camWin;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }

    public CamTotalBuilder newCamTotalBuilder() {
        return new CamTotalBuilder(new CamTotal());
    }

    public static class CamTotalBuilder{
        private CamTotal camTotal;

        public CamTotalBuilder(CamTotal camTotal) {
            this.camTotal = camTotal;
        }

        public CamTotal build() {
            return this.camTotal;
        }

        public CamTotalBuilder currency(String currency) {
            this.camTotal.setCurrency(currency);
            return this;
        }

        public CamTotalBuilder totalCamWin(BigDecimal totalCamWin) {
            this.camTotal.setTotalCamWin(totalCamWin);
            return this;
        }

        public CamTotalBuilder items(int items) {
            this.camTotal.setItems(items);
            return this;
        }
    }

    class CamTotal {
        private String currency;
        private BigDecimal totalCamWin = BigDecimal.ZERO;
        private int items = 0;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public BigDecimal getTotalCamWin() {
            return totalCamWin;
        }

        public void setTotalCamWin(BigDecimal totalCamWin) {
            this.totalCamWin = totalCamWin;
        }

        public int getItems() {
            return items;
        }

        public void setItems(int items) {
            this.items = items;
        }

        @Override
        public String toString() {
            return "CamTotal{" +
                    "currency='" + currency + '\'' +
                    ", totalCamWin='" + totalCamWin + '\'' +
                    ", items=" + items +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PlayerSummaryCamReportResponse{" +
                "records=" + records +
                ", total=" + total +
                ", subTotal=" + subTotal +
                ", pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", pageCount=" + pageCount +
                ", itemCount=" + itemCount +
                ", agentTree=" + agentTree +
                '}';
    }
}
