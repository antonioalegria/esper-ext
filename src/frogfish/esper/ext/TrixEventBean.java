package frogfish.esper.ext;

public class TrixEventBean {
    private double trix;
    private double signal;
    private double ema1;
    private double ema2;
    private double ema3;
    private double value;
    private int period;
    private int speriod;

    public TrixEventBean(double trix, double signal, double ema1, double ema2, double ema3, double value, int period, int speriod) {
        this.trix = trix;
        this.signal = signal;
        this.ema1 = ema1;
        this.ema2 = ema2;
        this.ema3 = ema3;
        this.value = value;
        this.period = period;
        this.speriod = speriod;
    }

    public double getTrix() {
        return trix;
    }

    public double getSignal() {
        return signal;
    }
    
    public double getEma1() {
        return ema1;
    }

    public double getEma2() {
        return ema2;
    }
    
    public double getEma3() {
        return ema3;
    }

    public double getValue() {
        return value;
    }
    
    public double getPeriod() {
        return period;
    }
    
    public double getSperiod() {
        return speriod;
    }
}
