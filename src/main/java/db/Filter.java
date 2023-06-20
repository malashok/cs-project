package db;

public class Filter {
    private String category;
    private Integer toPrice;
    private Integer fromPrice;

    public Filter(String category, Integer toPrice, Integer fromPrice) {
        this.category = category;
        this.toPrice = toPrice;
        this.fromPrice = fromPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getFromPrice() {
        return fromPrice;
    }

    public void setFromPrice(Integer fromPrice) {
        this.fromPrice = fromPrice;
    }

    public Integer getToPrice() {
        return toPrice;
    }

    public void setToPrice(Integer toPrice) {
        this.toPrice = toPrice;
    }

    public boolean isEmpty() {
        if(category == null && toPrice == null && fromPrice == null) return true;
        return false;
    }
}

