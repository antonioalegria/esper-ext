package com.antonioalegria.frogfish;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.StatementContext;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.view.*;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TrixViewFactory extends ViewFactorySupport {
	
	private static Log sLog = LogFactory.getLog(TrixViewFactory.class);
	
    private ViewFactoryContext mViewFactoryContext;
    private List<ExprNode> mViewParameters;
    private ExprNode mValueExpr;
    
    private int mDataWinSize;
    private int mSignalWinSize;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> viewParameters) throws ViewParameterException {
        this.mViewFactoryContext = viewFactoryContext;
        if (viewParameters.size() != 3) {
            throw new ViewParameterException("View requires three parameters, corresponding to three expressions: 1. window size, 2. signal window size, 3. value data points");
        }
        this.mViewParameters = viewParameters;        
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException {
    	ExprNode[] validatedNodes = ViewFactorySupport.validate("TRIX view", parentEventType, statementContext, mViewParameters, true);

        mValueExpr = validatedNodes[2];
                
        if ((mValueExpr.getExprEvaluator().getType() != double.class) && (mValueExpr.getExprEvaluator().getType() != Double.class)) {
            throw new ViewParameterException("View requires double-typed values in parameter 3");
        }

        Object parameter = ViewFactorySupport.evaluateAssertNoProperties("TRIX View", validatedNodes[0], 0, statementContext);
        if (!(parameter instanceof Integer)) {
            throw new ViewParameterException("View requires int-typed values in parameter 0");
        } else {
            Number param = (Number) parameter;
            mDataWinSize = param.intValue();
        }

        if (mDataWinSize < 2) {
            throw new ViewParameterException("TRIX view requires a data window size of at least 2");
        }
        
        parameter = ViewFactorySupport.evaluateAssertNoProperties("TRIX View", validatedNodes[1], 1, statementContext);
        if (!(parameter instanceof Integer)) {
            throw new ViewParameterException("View requires int-typed values in parameter 1");
        } else {
            Number param = (Number) parameter;
            mSignalWinSize = param.intValue();
        }

        if (mSignalWinSize < 1) {
            throw new ViewParameterException("TRIX view requires a data window size of at least 1");
        }
    }

    public View makeView(StatementContext statementContext)
    {
        return new TrixView(statementContext, mDataWinSize, mSignalWinSize, mValueExpr);
    }

    public EventType getEventType() {
        return TrixView.getEventType(mViewFactoryContext.getEventAdapterService());
    }
}
