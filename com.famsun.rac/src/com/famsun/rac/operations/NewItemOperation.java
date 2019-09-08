package com.famsun.rac.operations;
import java.beans.PropertyChangeListener;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.commands.paste.PasteCommand;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;


public class NewItemOperation extends AbstractAIFOperation
		implements PropertyChangeListener{
	protected String itemName;
	protected String itemId;
	protected String revId;
	protected String description;
	protected String itemType;
	protected TCComponent unitOfMeasure;
	protected boolean openOnCreateFlag;
	protected TCComponentItem oldItem;
	protected InterfaceAIFComponent pasteTargets[];
	protected TCComponentItem newItem;
	protected TCSession session;
	protected AIFDesktop desktop;
	protected boolean successFlag;
	protected InterfaceAIFComponent pasteToNewstuff[];
	protected CheckExistsOperation op;
	
	public NewItemOperation(TCSession imansession, AIFDesktop aifdesktop, String s, String s1, String s2, String s3, 
	        String s4, TCComponent imancomponent,InterfaceAIFComponent ainterfaceaifcomponent[]){
	    itemName = null;
	    itemId = null;
	    revId = null;
	    description = null;
	    itemType = null;
	    unitOfMeasure = null;
	    newItem = null;
	    session = null;
	    desktop = null;
	    successFlag = true;
	    pasteTargets = null;
	    session = imansession;
	    desktop = aifdesktop;
	    itemName = s;
	    itemId = s1;
	    revId = s2;
	    description = s3;
	    itemType = s4;
	    unitOfMeasure = imancomponent;
	    pasteTargets=ainterfaceaifcomponent;
	
	 }
	
	public boolean getSuccessFlag(){
	    return successFlag;
	}
	
	public TCComponentItem getNewItem(){
	    return newItem;
	}
	
	public void executeOperation() throws Exception{
		
	   Registry registry = Registry.getRegistry(this);
	   String qitemId=itemId;
	
	   op=new CheckExistsOperation(session,qitemId);
	   op.executeOperation();
	   if(!op.getSuccessFlag()){
	     
		    try
		    {  
		        TCComponentItemType imancomponentitemtype = (TCComponentItemType)session.getTypeComponent(itemType);
		                           
		        newItem = imancomponentitemtype.create(itemId, revId, itemType, itemName, description, unitOfMeasure);
		      	        
		    }
		    catch(TCException imanexception)
		    {
		        String s = registry.getString("failToCreate") + " " + itemId + "/" + itemName + " - " + imanexception.toString();
		        String s1 = imanexception.getDetailsMessage();
		        if(newItem != null)
		        	System.out.println(s);
		 
		        if(desktop != null){
		            MessageBox messagebox = new MessageBox(desktop, s, s1, registry.getString("error.TITLE"), 3, true);
		            messagebox.setVisible(true);
	
		        } 
		        else{
		            System.out.println(s);
		        }
		        successFlag = false;
		        throw imanexception;
		    }
		
		    if(newItem instanceof TCComponentItem && newItem != null){
		    	newItem.refresh();
		    	
					if (pasteTargets[0] instanceof TCComponentFolder){
						pasteToNewstuff= new InterfaceAIFComponent[1]; 		             						
						pasteToNewstuff[0]=pasteTargets[0];
					}
					else {
						 TCComponentUser currentuser = session.getUser();
						 System.out.println("currentuser:::::"+currentuser.toString());
						 TCComponent imancomponentNewstuff = (TCComponentFolder)currentuser.getNewStuffFolder()	;	 
						 pasteToNewstuff= new InterfaceAIFComponent[1];
						 pasteToNewstuff[0]=  imancomponentNewstuff;
				 	}
	
				
		        TCComponent aimancomponent[] = {newItem};
	            PasteCommand pastecommand;	 		
				pastecommand = new PasteCommand(aimancomponent,pasteToNewstuff, desktop);      	            	
	            pastecommand.setFailBackFlag(true);
	            pastecommand.addPropertyChangeListener(this);
	            pastecommand.executeModal(); 
	
			}       	
		 
		}	
	}



}
