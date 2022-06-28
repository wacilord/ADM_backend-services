package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SummaryCamReportResponse {
    private List<CamRecord> records = new ArrayList<>();
    private CamTotal total;
    private CamTotal subTotal;
    private int pageIndex;
    private int pageSize;
    private int pageCount;
    private int itemCount;
    private int agentLevel;

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

    public int getAgentLevel() {
        return agentLevel;
    }

    public void setAgentLevel(int agentLevel) {
        this.agentLevel = agentLevel;
    }

    @JsonIgnore
    public BigDecimal getRecordsSumCamWin() {
        return BigDecimal.valueOf(this.getRecords().stream().map(CamRecord::getSumCamWin).mapToDouble(BigDecimal::doubleValue).sum());
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

        public RecordBuilder currency(String currency) {
            this.camRecord.setCurrency(currency);
            return this;
        }

        public RecordBuilder sumCamWin(BigDecimal sumCamWin) {
            this.camRecord.setSumCamWin(sumCamWin);
            return this;
        }

        public RecordBuilder items(int items) {
            this.camRecord.setItems(items);
            return this;
        }


    }

    public class CamRecord {
        private String accountID;
        private String currency;
        private BigDecimal sumCamWin;
        private int items;

        public String getAccountID() {
            return accountID;
        }

        public void setAccountID(String accountID) {
            this.accountID = accountID;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public BigDecimal getSumCamWin() {
            return sumCamWin;
        }

        public void setSumCamWin(BigDecimal sumCamWin) {
            this.sumCamWin = sumCamWin;
        }

        public int getItems() {
            return items;
        }

        public void setItems(int items) {
            this.items = items;
        }

        @Override
        public String toString() {
            return "CamRecord{" +
                    "accountID='" + accountID + '\'' +
                    ", currency='" + currency + '\'' +
                    ", sumCamWin=" + sumCamWin +
                    ", items=" + items +
                    '}';
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
        return "SummaryCamReportResponse{" +
                "records=" + records +
                ", total=" + total +
                ", subTotal=" + subTotal +
                ", pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", pageCount=" + pageCount +
                ", itemCount=" + itemCount +
                ", agentLevel=" + agentLevel +
                '}';
    }
}
