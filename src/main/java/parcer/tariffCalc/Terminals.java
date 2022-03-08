package parcer.tariffCalc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Terminals {
    private String To;
    private Integer TimeOnWay;
    private Integer MinAmount;
    private Double WeightIndex1;
    private Double WeightIndex3;
    private Double WeightIndex5;
    private Double WeightIndex10;
    private Double WeightIndex20;
    private Double WeightIndex40;
    private Integer SizeIndex1;
    private Integer SizeIndex3;
    private Integer SizeIndex5;
    private Integer SizeIndex10;
    private Integer SizeIndex20;
    private Integer SizeIndex40;


    public Terminals(String to, Integer timeOnWay, Integer minAmount, Double weightIndex1, Double weightIndex3,
            Double weightIndex5, Double weightIndex10, Double weightIndex20, Double weightIndex40, Integer sizeIndex1,
            Integer sizeIndex3, Integer sizeIndex5, Integer sizeIndex10, Integer sizeIndex20, Integer sizeIndex40) {
        this.To = to;
        this.TimeOnWay = timeOnWay;
        this.MinAmount = minAmount;
        this.WeightIndex1 = weightIndex1;
        this.WeightIndex3 = weightIndex3;
        this.WeightIndex5 = weightIndex5;
        this.WeightIndex10 = weightIndex10;
        this.WeightIndex20 = weightIndex20;
        this.WeightIndex40 = weightIndex40;
        this.SizeIndex1 = sizeIndex1;
        this.SizeIndex3 = sizeIndex3;
        this.SizeIndex5 = sizeIndex5;
        this.SizeIndex10 = sizeIndex10;
        this.SizeIndex20 = sizeIndex20;
        this.SizeIndex40 = sizeIndex40;
    }

    public Terminals() {

    }

    @Override
    public String toString() {
        return "Terminals{" + "To='" + To + ", TimeOnWay=" + TimeOnWay + ", MinAmount=" + MinAmount +
               ", WeightIndex1=" + WeightIndex1 + ", WeightIndex3=" + WeightIndex3 + ", WeightIndex5=" + WeightIndex5 +
               ", WeightIndex10=" + WeightIndex10 + ", WeightIndex20=" + WeightIndex20 + ", WeightIndex40=" +
               WeightIndex40 + ", SizeIndex1=" + SizeIndex1 + ", SizeIndex3=" + SizeIndex3 + ", SizeIndex5=" +
               SizeIndex5 + ", SizeIndex10=" + SizeIndex10 + ", SizeIndex20=" + SizeIndex20 + ", SizeIndex40=" +
               SizeIndex40 + '}';
    }
}
