package boardgames_shop.dto.admin;

public class TopGameDto {

    private String name;
    private int totalSold;

    public TopGameDto() {}

    public TopGameDto(String name, int totalSold) {
        this.name = name;
        this.totalSold = totalSold;
    }

    public String getName()                { return name; }
    public void setName(String name)       { this.name = name; }

    public int getTotalSold()              { return totalSold; }
    public void setTotalSold(int totalSold){ this.totalSold = totalSold; }
}