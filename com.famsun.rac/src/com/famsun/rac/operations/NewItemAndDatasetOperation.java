package com.famsun.rac.operations;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.famsun.rac.util.CommonUtils;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.commands.paste.PasteCommand;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.services.rac.core.DataManagementService;


public class NewItemAndDatasetOperation extends AbstractAIFOperation
		implements PropertyChangeListener{

	protected InterfaceAIFComponent pasteTargets[];
	protected TCSession session;
	protected AIFDesktop desktop;
	protected boolean successFlag;
	protected InterfaceAIFComponent pasteToNewstuff[];
	protected CheckExistsOperation op;
	protected Map<String, List<String>> partsMap;
	protected Registry registry;
	
	public NewItemAndDatasetOperation(TCSession imansession, AIFDesktop aifdesktop, Map<String, List<String>> map, InterfaceAIFComponent ainterfaceaifcomponent[]){
	    
	    session = null;
	    desktop = null;
	    successFlag = true;
	    pasteTargets = null;
	    session = imansession;
	    desktop = aifdesktop;
	    partsMap=map;
	    pasteTargets=ainterfaceaifcomponent;
		registry = Registry.getRegistry(this);
	 }
	
	public boolean getSuccessFlag(){
	    return successFlag;
	}

	
	public void executeOperation() throws Exception{
		Set<Entry<String,List<String>>> set = partsMap.entrySet();
		Iterator<Map.Entry<String,List<String>>> itera_Entry = set.iterator();
		
		//迭代器
		while(itera_Entry.hasNext()) {
			Map.Entry<String, List<String>> mapEntry = itera_Entry.next();
			String itemId = mapEntry.getKey();
			List<String> itemInfo= mapEntry.getValue();
			if(CommonUtils.hasItem(itemId)) {
				continue;				
			} else {
				createItemAndDataset(itemId,itemInfo);
			}
		}	
		
	}

	private void createItemAndDataset(String id, List<String> info) throws Exception {
		// TODO Auto-generated method stub
		TCComponentItem newItem;
		String itemId =id;
		List<String> itemInfo = info;
		String itemName=itemInfo.get(0);
		String revId=itemInfo.get(1);
		String description="";
		String itemType="Item";
		TCComponent unitOfMeasure=null;
		session.setStatus((new StringBuilder()).append("新建零组件").append(" ").append(itemId).append("-").append(itemName).
			append(" ...").toString());
	     
		try {  
	        TCComponentItemType imancomponentitemtype = (TCComponentItemType)session.getTypeComponent(itemType);
	                           
	        newItem = imancomponentitemtype.create(itemId, revId, itemType, itemName, description, unitOfMeasure);
		      	        
		}
		catch(TCException imanexception) {
	        String s = registry.getString("failToCreate") + " " + itemId + "/" + itemName + " - " + imanexception.toString();
	        System.out.println(s);
	        successFlag = false;
	        throw imanexception;
		}
		 if(newItem != null){
			 try{
				//属性表赋值	     	    
				TCComponentItemRevision newItemRevision=newItem.getLatestItemRevision();	        	
				TCComponentForm itemRevForm=(TCComponentForm)newItemRevision.getRelatedComponent("IMAN_master_form_rev");
				String drawingNo=info.get(2);
				String partType=info.get(3);
				String[] itemRevFormNames=new String[] {"m2MY_DrawingNo","m2MY_Mass","m2MY_Material","m2MY_Memo","m2MY_Stock","m2MY_Type"};	    
				String[] itemRevFormValues=new String[] {drawingNo,"","","","",partType};

		        TCProperty[]  formProperties = itemRevForm.getFormTCProperties(itemRevFormNames);//获得属性表
		 
		        for(int k=0;k<formProperties.length;k++){
				         formProperties[k].setStringValueData(itemRevFormValues[k]);    //赋值			
		        }
		        itemRevForm.setTCProperties(formProperties); //回写
				}catch(Exception e){   
		    	    System.out.println("error");
		    	    MessageBox.post(e);
				}
		 }
		
		if(newItem instanceof TCComponentItem && newItem != null){
			//粘贴到文件夹
	    	newItem.refresh();
			try {
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
			catch(TCException imanexception) {
				throw imanexception;
			}
		}
		
		importDatasetFile(newItem,itemInfo.get(4));


	}

	private void importDatasetFile(TCComponentItem newItem, String fullName) throws Exception {
		// TODO Auto-generated method stub
		if (newItem != null){
			 TCComponentDataset newDataset;
			 String importFileName=fullName;
			 String dsName=newItem.getNewID()+"/A";
			 String dsDescription ="";
			 String dsType= CommonUtils.getDatasetType(importFileName);
			 String toolUsed = CommonUtils.getSeToolType(importFileName);

	         try {
	        	 TCComponentItemRevision tcNewItemRevision=newItem.getLatestItemRevision();
	        	 InterfaceAIFComponent[] pasteToItemRev =new InterfaceAIFComponent[1];
 				 pasteToItemRev[0]=(InterfaceAIFComponent)tcNewItemRevision;	
 				 com.teamcenter.services.rac.core._2006_03.DataManagement.CreateDatasetsResponse createdatasetsresponse = null;
 			     DataManagementService datamanagementservice = DataManagementService.getService(session);
 			     TCComponent tcComponent=(TCComponent)pasteToItemRev[0];
 			     com.teamcenter.services.rac.core._2008_06.DataManagement.DatasetProperties2 adatasetproperties[] = new com.teamcenter.services.rac.core._2008_06.DataManagement.DatasetProperties2[1];
 			     adatasetproperties[0] = new com.teamcenter.services.rac.core._2008_06.DataManagement.DatasetProperties2();
 			     adatasetproperties[0].clientId = "1";
 			     adatasetproperties[0].description = dsDescription;
 			     adatasetproperties[0].name = dsName;
 			     adatasetproperties[0].type = dsType;
 			     adatasetproperties[0].datasetId = "";
 			     adatasetproperties[0].datasetRev = "";
 			     adatasetproperties[0].toolUsed = toolUsed;
 			     adatasetproperties[0].container = tcComponent;
 			     adatasetproperties[0].relationType = "IMAN_specification";
 			     createdatasetsresponse = datamanagementservice.createDatasets2(adatasetproperties);
 			     if(createdatasetsresponse.serviceData.sizeOfPartialErrors() != 0){
 			         newDataset = createdatasetsresponse.output[0].dataset;
 			     } 
 			     else{
 			         String as[] = createdatasetsresponse.serviceData.getPartialError(0).getMessages();
 			         String s = as[0];
 			         StringBuffer stringbuffer = new StringBuffer();
 			         for(int i = 0; i < as.length; i++)
 			         {
 			             if(i > 0)
 			                 stringbuffer.append('\n');
 			             stringbuffer.append(as[i]);
 			         }

 			         if(desktop != null){
 			 
 			        	 System.out.println(desktop);
 			         } else {
 			             System.out.println(s);
 			         }
 			         throw new TCException(createdatasetsresponse.serviceData.getPartialError(0).getLevels(), createdatasetsresponse.serviceData.getPartialError(0).getCodes(), createdatasetsresponse.serviceData.getPartialError(0).getMessages());
 			     }
	         }
	         catch(TCException imanexception) {
	        	 successFlag = false;
	        	 throw imanexception;
	         }
	         
	         String importFileType = CommonUtils.getRefType(importFileName);
	         String importRefType = CommonUtils.getRefType(importFileName);
	    	 String as1[] = {
	             importFileName
             };
             String as2[] = {
        		 importRefType
             };
             String as3[] = {
        		 importFileType
             };
             String as4[] = {
        		 "Plain"
             };
	         try {
	              session.setStatus(registry.getString("importingFile"));
	              newDataset.setFiles(as1, as3, as4, as2);	     
	          }
	         catch (TCException imanexception) {
	              newDataset.delete();
	              throw imanexception;
	         }
	         
	    }
	   
	}
}
