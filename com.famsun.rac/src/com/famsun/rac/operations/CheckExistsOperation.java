package com.famsun.rac.operations;


import java.beans.PropertyChangeListener;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class CheckExistsOperation extends AbstractAIFOperation
		implements PropertyChangeListener{
	public CheckExistsOperation(TCSession tcsession, String itemid)
    {
       
        session = null;
        successFlag = false;
        session = tcsession;
 		value = itemid;
 
    }
   

    public boolean getSuccessFlag(){
        return successFlag;
    }


	public void executeOperation()
        throws Exception{
		TCComponentQuery tccomponentquery;
		TCComponentQueryType tccomponentquerytype;

    	String[] item_name = {"零组件 ID"};
    	String[] item_name_value= {value};

		if (value!=null && value!="")
		{
		
			try
	        {
	            tccomponentquerytype =(TCComponentQueryType)session.getTypeComponent("ImanQuery");         
		        tccomponentquery =(TCComponentQuery)tccomponentquerytype.find("Item...");	        		       
		        if ((tccomponentquery == null) || (!tccomponentquery.isValid()))
		        {
		          tccomponentquery = ((TCComponentQuery)tccomponentquerytype.find("零组件 ID"));
		          item_name[0] = "零组件ID";
		        }
		        TCComponent[] imancomponents =tccomponentquery.execute(item_name, item_name_value);		

			
				int queryq = imancomponents.length;	
				  System.out.println("qq::::::::"+queryq);
					 
					 if (queryq>0)
					 {
						 successFlag = true;
											
					 }
					 else
					 {
						 System.out.println("无重码");
						 successFlag = false;
					 }
				
			}
			catch(Exception ex){
    		    MessageBox  msgBox = new MessageBox(ex.toString(),"提示",MessageBox.INFORMATION);
		        msgBox.setModal(true);
		        msgBox.setVisible(true);
				successFlag = true;
				ex.printStackTrace();
			}
		}
	
    }       
   
	private TCSession session;
    private boolean successFlag;
	private String value;

}
