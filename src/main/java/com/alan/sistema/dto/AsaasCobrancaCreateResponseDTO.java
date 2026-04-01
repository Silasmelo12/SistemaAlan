package com.alan.sistema.dto;

public class AsaasCobrancaCreateResponseDTO {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    private String object;
    private String dateCreated;
    private String checkoutSession;
    private String paymentLink;
    private Double netValue;
    private Double originalValue;
    private Double interestValue;
    private String description;
    private Boolean canBePaidAfterDueDate;
    private String pixTransaction;
    private String originalDueDate;
    private String paymentDate;
    private String clientPaymentDate;
    private Integer installmentNumber;
    private String invoiceUrl;
    private String invoiceNumber;
    private String externalReference;
    private Boolean deleted;
    private Boolean anticipated;
    private Boolean anticipable;
    private String creditDate;
    private String estimatedCreditDate;
    private String transactionReceiptUrl;
    private String nossoNumero;
    private String bankSlipUrl;
    private String lastInvoiceViewedDate;
    private String lastBankSlipViewedDate;
    private Discount discount;
    private Fine fine;
    private Interest interest;
    private Boolean postalService;
    private String escrow;
    private String refunds;

    // Discount inner class
    public static class Discount {
        private Double value;
        private String limitDate;
        private Integer dueDateLimitDays;
        private String type;

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public String getLimitDate() {
            return limitDate;
        }

        public void setLimitDate(String limitDate) {
            this.limitDate = limitDate;
        }

        public Integer getDueDateLimitDays() {
            return dueDateLimitDays;
        }

        public void setDueDateLimitDays(Integer dueDateLimitDays) {
            this.dueDateLimitDays = dueDateLimitDays;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    // Fine inner class
    public static class Fine {
        private Double value;
        private String type;

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    // Interest inner class
    public static class Interest {
        private Double value;
        private String type;

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


    // Getters and setters for new fields
    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCheckoutSession() {
        return checkoutSession;
    }

    public void setCheckoutSession(String checkoutSession) {
        this.checkoutSession = checkoutSession;
    }

    public String getPaymentLink() {
        return paymentLink;
    }

    public void setPaymentLink(String paymentLink) {
        this.paymentLink = paymentLink;
    }

    public Double getNetValue() {
        return netValue;
    }

    public void setNetValue(Double netValue) {
        this.netValue = netValue;
    }

    public Double getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(Double originalValue) {
        this.originalValue = originalValue;
    }

    public Double getInterestValue() {
        return interestValue;
    }

    public void setInterestValue(Double interestValue) {
        this.interestValue = interestValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCanBePaidAfterDueDate() {
        return canBePaidAfterDueDate;
    }

    public void setCanBePaidAfterDueDate(Boolean canBePaidAfterDueDate) {
        this.canBePaidAfterDueDate = canBePaidAfterDueDate;
    }

    public String getPixTransaction() {
        return pixTransaction;
    }

    public void setPixTransaction(String pixTransaction) {
        this.pixTransaction = pixTransaction;
    }

    public String getOriginalDueDate() {
        return originalDueDate;
    }

    public void setOriginalDueDate(String originalDueDate) {
        this.originalDueDate = originalDueDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getClientPaymentDate() {
        return clientPaymentDate;
    }

    public void setClientPaymentDate(String clientPaymentDate) {
        this.clientPaymentDate = clientPaymentDate;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getAnticipated() {
        return anticipated;
    }

    public void setAnticipated(Boolean anticipated) {
        this.anticipated = anticipated;
    }

    public Boolean getAnticipable() {
        return anticipable;
    }

    public void setAnticipable(Boolean anticipable) {
        this.anticipable = anticipable;
    }

    public String getCreditDate() {
        return creditDate;
    }

    public void setCreditDate(String creditDate) {
        this.creditDate = creditDate;
    }

    public String getEstimatedCreditDate() {
        return estimatedCreditDate;
    }

    public void setEstimatedCreditDate(String estimatedCreditDate) {
        this.estimatedCreditDate = estimatedCreditDate;
    }

    public String getTransactionReceiptUrl() {
        return transactionReceiptUrl;
    }

    public void setTransactionReceiptUrl(String transactionReceiptUrl) {
        this.transactionReceiptUrl = transactionReceiptUrl;
    }

    public String getNossoNumero() {
        return nossoNumero;
    }

    public void setNossoNumero(String nossoNumero) {
        this.nossoNumero = nossoNumero;
    }

    public String getBankSlipUrl() {
        return bankSlipUrl;
    }

    public void setBankSlipUrl(String bankSlipUrl) {
        this.bankSlipUrl = bankSlipUrl;
    }

    public String getLastInvoiceViewedDate() {
        return lastInvoiceViewedDate;
    }

    public void setLastInvoiceViewedDate(String lastInvoiceViewedDate) {
        this.lastInvoiceViewedDate = lastInvoiceViewedDate;
    }

    public String getLastBankSlipViewedDate() {
        return lastBankSlipViewedDate;
    }

    public void setLastBankSlipViewedDate(String lastBankSlipViewedDate) {
        this.lastBankSlipViewedDate = lastBankSlipViewedDate;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public Fine getFine() {
        return fine;
    }

    public void setFine(Fine fine) {
        this.fine = fine;
    }

    public Interest getInterest() {
        return interest;
    }

    public void setInterest(Interest interest) {
        this.interest = interest;
    }

    public Boolean getPostalService() {
        return postalService;
    }

    public void setPostalService(Boolean postalService) {
        this.postalService = postalService;
    }

    public String getEscrow() {
        return escrow;
    }

    public void setEscrow(String escrow) {
        this.escrow = escrow;
    }

    public String getRefunds() {
        return refunds;
    }

    public void setRefunds(String refunds) {
        this.refunds = refunds;
    }

}
