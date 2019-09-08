package com.famsun.rac.operations;

import java.beans.PropertyChangeListener;


import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.commands.paste.PasteCommand;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentFolderType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;


public class NewFolderOp extends AbstractAIFOperation
    implements PropertyChangeListener
{
	
    public NewFolderOp(TCSession imansession, AIFDesktop aifdesktop, String s, String s1, String s2, InterfaceAIFComponent ainterfaceaifcomponent[])
    {
        folderName = null;
        folderDescription = null;
        folderType = null;
        pasteTargets = null;
        newFolder = null;
        session = null;
        desktop = null;
        successFlag = true;
        session = imansession;
        desktop = aifdesktop;
        folderName = s;
        folderDescription = s1;
        folderType = s2;
        pasteTargets = ainterfaceaifcomponent;

    }

    public TCComponent getNewFolder()
    {
        return newFolder;
    }

    public boolean getSuccessFlag()
    {
        return successFlag;
    }

    public void executeOperation()
        throws Exception
    {
        registry = Registry.getRegistry(this);
        session.setStatus(registry.getString("creatingNewFolder") + " " + folderName + "  ...");
        try
        {
            TCComponentFolderType imancomponentfoldertype = (TCComponentFolderType)session.getTypeComponent(folderType);
            newFolder = imancomponentfoldertype.create(folderName, folderDescription, folderType);
        }
        catch(TCException imanexception)
        {
            String s = registry.getString("failToCreate") + " " + folderName + " - " + imanexception.toString();
            if(desktop != null)
            {
                MessageBox messagebox = new MessageBox(desktop, s, null, registry.getString("error.TITLE"), 3, true);
                messagebox.setVisible(true);
            	//System.out.println(desktop);
            } else
            {
                System.out.println(s);
            }
            successFlag = false;
            throw imanexception;
        }
        TCComponent aimancomponent[] = {
            newFolder
        };

			////////如果直接在PSE中创建文件夹Folder，则将Folder粘贴到newstuff中
			 
					if (pasteTargets[0] instanceof TCComponentBOMLine)
					{
						TCComponentUser currentuser = session.getUser();		
		                TCComponent imancomponentNewstuff = (TCComponentFolder)currentuser.getNewStuffFolder()	; 			     
					 
					     pasteToNewstuff= new InterfaceAIFComponent[1];
		                 pasteToNewstuff[0]=  imancomponentNewstuff;
		 
					   
					   }
					   else
					   {
					       pasteToNewstuff= new InterfaceAIFComponent[1]; 		             	 

					           pasteToNewstuff[0]=pasteTargets[0];

					    }
				  //////////////////////////////////////////


		PasteCommand	pastecommand = new PasteCommand(aimancomponent,pasteToNewstuff, desktop);			
        pastecommand.setFailBackFlag(true);
        pastecommand.addPropertyChangeListener(this);
        //session.suspendOperation(this);
        pastecommand.executeModal();
        //session.resumeOperation(this);
        if(isAbortRequested())
            session.setStatus(registry.getString("abort"));
        else
            session.setReadyStatus();

    }



    protected String folderName;
    protected String folderDescription;
    protected String folderType;
    protected boolean openOnCreateFlag;
    protected InterfaceAIFComponent pasteTargets[];
    protected TCComponent newFolder;
    protected TCSession session;
    protected AIFDesktop desktop;
    private Registry registry;
    private boolean successFlag;
	private InterfaceAIFComponent pasteToNewstuff[];
}

