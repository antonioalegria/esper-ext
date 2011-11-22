package com.antonioalegria.esper-ext;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.espertech.esper.epl.agg.AggregationSupport;
import com.espertech.esper.epl.agg.AggregationValidationContext;

public class TrixAggregationFunction extends AggregationSupport {

	private int mDataWindowSize;
	private int mSignalWindowSize;

	private LinkedList<Double> mDataWindow;
	private LinkedList<Double> mSignalWindow;
	
	private double mEMA1;
	private double mEMA2;
	private double mEMA3;
	private double mSignal;
	
	public TrixAggregationFunction() { }
	
	/* (non-Javadoc)
	 * @see com.espertech.esper.epl.agg.AggregationSupport#validate(com.espertech.esper.epl.agg.AggregationValidationContext)
	 */
	@Override
	public void validate(AggregationValidationContext validationContext) {
		if ((validationContext.getParameterTypes().length != 1) || (validationContext.getParameterTypes()[0] != String.class)) {
			
			throw new IllegalArgumentException("Concat aggregation requires a single parameter of type String");
		}
	}

	/* (non-Javadoc)
	 * @see com.espertech.esper.epl.agg.AggregationMethod#clear()
	 */
	@Override
	public void clear() {
		mDataWindow.clear();
		mSignalWindow.clear();
	}

	@Override
	public void enter(Object value) {
		if (value != null) {
			mDataWindow.addLast((Double) value);
			mSignalWindow.addLast((Double) value);
		}
		
		if (mDataWindow.size() > mDataWindowSize) {
			mDataWindow.removeFirst();
		}
		
		if (mSignalWindow.size() > mSignalWindowSize) {
			mSignalWindow.removeFirst();
		}
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class getValueType() {
		return Double.TYPE;
	}

	@Override
	public void leave(Object arg0) {
		// TODO Auto-generated method stub
		
	}
}
