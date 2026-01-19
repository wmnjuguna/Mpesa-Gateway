package io.github.wmjuguna.daraja.utils;

public enum TransactionTypeEnum {
    CustomerPaybillOnline("CustomerPayBillOnline"),
    CustomerBuyGoodsOnline("CustomerBuyGoodsOnline");

    private final String transactioType;

    TransactionTypeEnum(String transactionTYpe){
        this.transactioType = transactionTYpe;
    }

    public String getTransactioType() {
        return transactioType;
    }
}
