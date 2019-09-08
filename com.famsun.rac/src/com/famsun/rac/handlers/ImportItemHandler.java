package com.famsun.rac.handlers;

import java.awt.Frame;

import javax.swing.SwingUtilities;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.famsun.rac.dialog.ImportItemDialog;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.Activator;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ImportItemHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		 final InterfaceAIFComponent[] selobjs = Activator.getDefault()
					.getSelectionMediatorService().getTargetComponents();
		    AIFDesktop aifdesktop =AIFUtility.getActiveDesktop();
		    final Frame frame=aifdesktop.getFrame();

		    SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					
					ImportItemDialog importDialog = new ImportItemDialog(frame, selobjs);
					importDialog.setVisible(true);
				}
			});
		return null;
	}
}
