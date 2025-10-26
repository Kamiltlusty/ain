package pl.kamil.domain.model;

public class UInt16 {
    private Integer val;
    private final static Integer MAX = 65535;

    public UInt16(Integer val) {
        this.val = val;
    }

    public UInt16 copy() {
        return new UInt16(this.val);
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        if (val > MAX || val < 0) { throw new IllegalArgumentException("Value must be between 0 and 65535"); }
        this.val = val;
    }

    @Override
    public String toString() {
        return "UInt16{" +
                "val=" + val +
                '}';
    }

}
