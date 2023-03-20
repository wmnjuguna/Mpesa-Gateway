package app.fortuneconnect.payments.Utils.Enums;

public enum TransactionTypeEnum {
    CustomerPaybillOnline("CustomerPaybillOnline"),
    CustomerBuyGoodsOnline("CustomerBuyGoodsOnline");

    private final String transactioType;

    TransactionTypeEnum(String transactionTYpe){
        this.transactioType = transactionTYpe;
    }

    public String getTransactioType() {
        return transactioType;
    }
}
