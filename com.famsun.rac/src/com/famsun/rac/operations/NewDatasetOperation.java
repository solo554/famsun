package com.famsun.rac.operations;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.commands.paste.PasteCommand;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.services.rac.core.DataManagementService;



public class NewDatasetOperation extends AbstractAIFOperation
 implements PropertyChangeListener
{

 public NewDatasetOperation()
 {

     successFlag = true;
     dataModelSupported = false;
 }


 public NewDatasetOperation(TCSession tcsession, AIFDesktop aifdesktop, String s, String s1,String s4, 
         String s5, String s6, String s7, String s8, boolean flag, InterfaceAIFComponent ainterfaceaifcomponent[])
 {
     successFlag = false;
     dataModelSupported = false;
     session = tcsession;
     desktop = aifdesktop;
     dsName = s;
     dsDescription = s1;
     dsType = s4;
     toolUsed = s5;
     importFileName = s6;
     importFileType = s7;
     importRefType = s8;
     pasteTargets = ainterfaceaifcomponent;
     dataModelSupported = flag;
     propertySupport = new PropertyChangeSupport(this);
 }

 public TCComponent getNewDataset()
 {
     return newDataset;
 }

 public boolean getSuccessFlag()
 {
     return successFlag;
 }
 


 public void executeOperation()
     throws Exception
 {
	 registry = Registry.getRegistry(this);
     session.setStatus((new StringBuilder()).append(registry.getString("creatingNewDataset")).append(" ").append(dsName).append(" ...").toString());
     com.teamcenter.services.rac.core._2006_03.DataManagement.CreateDatasetsResponse createdatasetsresponse = null;
     DataManagementService datamanagementservice = DataManagementService.getService(session);
     pasteTargets = PasteCommand.getParentsFromPreference(pasteTargets, session);
     TCComponent tcComponent=(TCComponent)pasteTargets[0];
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
     if(createdatasetsresponse.serviceData.sizeOfPartialErrors() == 0){
         newDataset = createdatasetsresponse.output[0].dataset;
         if(adatasetproperties[0].container != null)
         successFlag =true; 
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
         } else{
             System.out.println(s);
         }
         successFlag = false;


         throw new TCException(createdatasetsresponse.serviceData.getPartialError(0).getLevels(), createdatasetsresponse.serviceData.getPartialError(0).getCodes(), createdatasetsresponse.serviceData.getPartialError(0).getMessages());
     }

    
     if(importFileName != null && importFileName.length() > 0)
     {
   
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
         try
         {
             session.setStatus(registry.getString("importingFile"));
             newDataset.setFiles(as1, as3, as4, as2);
    
         }
         catch(TCException tcexception)
         {
             newDataset.delete();
             MessageBox  msgBox4 = new MessageBox("导入失败！","提示",MessageBox.INFORMATION);
    	     msgBox4.setModal(true);
    	     msgBox4.setVisible(true); 
             successFlag = false;
             return;
         }
     }  

    
 }


 protected String dsName;
 protected String dsDescription;
 protected String dsType,mycodename="";
 protected String toolUsed;
 protected InterfaceAIFComponent pasteTargets[];
 protected TCComponentDataset newDataset;
 protected TCSession session;
 protected AIFDesktop desktop;
 protected Registry registry;
 protected PropertyChangeSupport propertySupport;
 protected boolean successFlag;
 protected String importFileName;
 protected String importFileType;
 protected String importRefType,subGroup="";
 protected boolean dataModelSupported;
}

