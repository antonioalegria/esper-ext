package com.antonioalegria.esper-ext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.SingleEventIterator;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.view.CloneableView;
import com.espertech.esper.view.View;
import com.espertech.esper.view.ViewSupport;

public class TrixView extends ViewSupport implements CloneableView {
    
	private static Log sLog = LogFactory.getLog(TrixView.class);

    private final EventBean[] mEventsPerStream = new EventBean[1];
    
    private double mPrevEMA1 = 0D;
    private double mPrevEMA2 = 0D;
    private double mPrevEMA3 = 0D;
    private double mPrevSignal = 0D;
    
    private double mEMA1 = 0D;
    private double mEMA2 = 0D;
    private double mEMA3 = 0D;
    private double mTrix = 0D;
    private double mSignal = 0D;
    private double mValue = 0D;
    
    private long mDatapoints = 0L;
    
	private AgentInstanceViewFactoryChainContext mStatementContext;
	private int mDataWinSize;
	private int mSignalWinSize;
	private ExprNode mValueExpr;
	private ExprEvaluator mValueExprEval;
	
    private EventBean mLastEvent = null;

	private EventType mEventType;
	
    public TrixView(AgentInstanceViewFactoryChainContext statementContext, int dataWinSize, int signalWinSize, ExprNode valueExpr) {
        this.mStatementContext = statementContext;
        this.mDataWinSize = dataWinSize;
        this.mSignalWinSize = signalWinSize;
        this.mValueExpr = valueExpr;
        this.mValueExprEval = valueExpr.getExprEvaluator();
        this.mEventType = createEventType(mStatementContext.getStatementContext().getEventAdapterService());
    }

    @Override
    public void update(EventBean[] newData, EventBean[] oldData) { 	
    	if (newData != null) {
    		for (int i = 0; i < newData.length; i++) {
    			mDatapoints++;
    		 	
    			mPrevEMA1 = mEMA1;
        		mPrevEMA2 = mEMA2;
        		mPrevEMA3 = mEMA3;
        		mPrevSignal = mSignal;
    			
                mEventsPerStream[0] = newData[i];
                Number valueExpr = (Number) mValueExprEval.evaluate(mEventsPerStream, true, mStatementContext);

                if (valueExpr != null) {
                    mValue  = valueExpr.doubleValue();
                    mEMA1   = ema(mValue, mPrevEMA1, mDataWinSize);
                    mEMA2   = ema(mEMA1, mPrevEMA2, mDataWinSize);
                    mEMA3   = ema(mEMA2, mPrevEMA3, mDataWinSize);
                    mTrix   = trix(mEMA3, mPrevEMA3);
                    mSignal = ema(mTrix, mPrevSignal, mSignalWinSize);
                }
            }
    	}
    	
    	// If there are child views, call update method
        if (hasViews()) postData();
    }
    
    private double ema(double latestpoint, double prevEma, int p) {
    	double result = prevEma;
    	
    	result += (latestpoint - prevEma) * 2/(1+p);
    	
    	return result;
    }
    
    private double trix(double ema3, double prevEma3) {
    	if (prevEma3 == 0) return 0;
    	
    	return 100 * (ema3 - prevEma3)/prevEma3;
    }

    @Override
    public EventType getEventType() {
    	  return mEventType;
    }
    
    public static EventType getEventType(EventAdapterService eventAdapterService) {
    	return createEventType(eventAdapterService);
    }

    @Override
    public Iterator<EventBean> iterator() {
    	return new SingleEventIterator(mLastEvent);
    }

    @Override
    public View cloneView() {
        return new TrixView(mStatementContext, mDataWinSize, mSignalWinSize, mValueExpr);
    }
    
    protected static EventType createEventType(EventAdapterService eventAdapterService) {
    	Map<String, Object> schemaMap = new HashMap<String, Object>();
        schemaMap.put("trix", Double.TYPE);
        schemaMap.put("signal", Double.TYPE);
        schemaMap.put("ema1", Double.TYPE);
        schemaMap.put("ema2", Double.TYPE);
        schemaMap.put("ema3", Double.TYPE);
        schemaMap.put("value", Double.TYPE);
        schemaMap.put("period", Integer.TYPE);
        schemaMap.put("speriod", Integer.TYPE);
        schemaMap.put("datapoints", Long.TYPE);
        
        return eventAdapterService.createAnonymousMapType("TrixValue", schemaMap); // TODO: before this was really anonymous
    }
    
    private void postData() {    	
		HashMap<String,Object> trixValue = createEvent(mTrix, mSignal, mEMA1, mEMA2, mEMA3, mValue, mDataWinSize, mSignalWinSize, mDatapoints);
		EventBean outgoing = mStatementContext.getStatementContext().getEventAdapterService().adapterForTypedMap(trixValue, mEventType);
        this.updateChildren(new EventBean[] {outgoing}, null);            
        mLastEvent = outgoing;
    }
    
    private static HashMap<String, Object> createEvent(double trix, double signal, double ema1, double ema2, double ema3, double value, int period, int speriod, long datapoints) {
    	HashMap<String, Object> evt = new HashMap<String,Object>();
    	evt.put("trix", trix);
    	evt.put("signal", signal);
    	evt.put("ema1", ema1);
    	evt.put("ema2", ema2);
    	evt.put("ema3", ema3);
    	evt.put("value", value);
    	evt.put("period", period);
    	evt.put("speriod", speriod);
    	evt.put("datapoints", datapoints);
    	
    	return evt;
    }
}
