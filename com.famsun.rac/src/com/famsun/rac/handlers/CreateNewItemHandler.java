package com.famsun.rac.handlers;

import java.awt.Frame;

import javax.swing.SwingUtilities;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.famsun.rac.dialog.NewItemDialog;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.Activator;

public class CreateNewItemHandler extends AbstractHandler {

	public CreateNewItemHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		// TODO Auto-generated method stub
		 final InterfaceAIFComponent[] selobjs = Activator.getDefault()
					.getSelectionMediatorService().getTargetComponents();
		    AIFDesktop aifdesktop =AIFUtility.getActiveDesktop();
		    final Frame frame=aifdesktop.getFrame();

		    SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					
					NewItemDialog createDialog = new NewItemDialog(frame, selobjs);
					createDialog.setVisible(true);
				}
			});
		    
		return null;
	}

}
