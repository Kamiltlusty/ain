package pl.kamil.domain.model;

public class UInt16 {
    private Integer val;
    private String bitString;

    private final static Integer MAX = 65535;

    public UInt16(Integer val) {
        this.val = val;
        this.bitString = toBitString(val);
    }

    public UInt16(String bitString) {
        this.bitString = bitString;
        this.val = Integer.parseInt(bitString, 2);
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

    public String getBitString() {
        return bitString;
    }

    private String toBitString(Integer val) {
        if (val == null) { throw new IllegalArgumentException("Value must not be null"); }
        return String.format("%16s", Integer.toBinaryString(val)).replace(' ', '0');
    }

    public void setBitString(String bitString) {
        this.bitString = bitString;
    }

    public void toIntVal(String bitString) {

    }

    @Override
    public String toString() {
        return "UInt16{" +
                "val=" + val +
                '}';
    }

}
