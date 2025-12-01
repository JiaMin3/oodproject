package my.com.mckl.oodproject.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Checkout {

    private List<Item> itemList = new ArrayList<>();
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public Checkout() {}

    public List<Item> getItemList() { return itemList; }
    public void setItemList(List<Item> itemList) { this.itemList = itemList; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public void addItem(Item item) {
        itemList.add(item);
        calculateTotal();
    }

    public void calculateTotal() {
        totalAmount = BigDecimal.ZERO;
        for (Item i : itemList) {
            totalAmount = totalAmount.add(i.getSubtotal());
        }
    }

    public String displayCheckoutSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Checkout Summary:\n");

        for (Item item : itemList) {
            sb.append(item.displayItem()).append("\n");
        }

        sb.append("Total: RM").append(totalAmount);
        return sb.toString();
    }
}
