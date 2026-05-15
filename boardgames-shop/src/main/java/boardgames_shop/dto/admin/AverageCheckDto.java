package boardgames_shop.dto.admin;

import java.math.BigDecimal;

public class AverageCheckDto {

    private BigDecimal averageCheck;

    public AverageCheckDto() {}

    public AverageCheckDto(BigDecimal averageCheck) {
        this.averageCheck = averageCheck;
    }

    public BigDecimal getAverageCheck()                  { return averageCheck; }
    public void setAverageCheck(BigDecimal averageCheck) { this.averageCheck = averageCheck; }
}