package com.famsun.rac.handlers;
import javax.swing.SwingUtilities;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.famsun.rac.dialog.CreateFolderDialog;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.Activator;

public class NewFolderHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent paramExecutionEvent)
		    throws ExecutionException
		  {

		
			final InterfaceAIFComponent[] selobjs = Activator.getDefault().getSelectionMediatorService().getTargetComponents();

		    SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
				CreateFolderDialog dlog = new CreateFolderDialog(null, selobjs);
					dlog.setVisible(true);
				}
			});
		    
		    return null;
		  }


	

}
