package com.famsun.rac.dialog;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.famsun.rac.operations.NewFolderOp;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AifrcpPlugin;
import com.teamcenter.rac.kernel.TCAccessControlService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.services.ISessionService;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.FilterDocument;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.Painter;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.VerticalLayout;


public class CreateFolderDialog extends AbstractAIFDialog
    implements InterfaceAIFOperationListener, PropertyChangeListener{
	private static final long serialVersionUID = 1L;
    public CreateFolderDialog(Frame frame, InterfaceAIFComponent ainterfaceaifcomponent[]){
        super(frame, true);
        okOrApply = 0;
        session = null;
        pasteTargets = null;
        desktop = null;
        parent = null;
        createFolderOp = null;
        openOnCreatePref = null;
        pasteTargets = ainterfaceaifcomponent;
        parent = frame;
        if(frame instanceof AIFDesktop)
            desktop = (AIFDesktop)frame;
        initializeDialog();
    }



    private void initializeDialog(){
    	try
    	{
          ISessionService iss = AifrcpPlugin.getSessionService();
          session = (TCSession)iss.getSession("com.teamcenter.rac.kernel.TCSession"); 
    	}
    	catch (Exception ex){}
        propertySupport = new PropertyChangeSupport(this);
        appReg = Registry.getRegistry(this);
        setTitle(appReg.getString("新建文件夹"));
        JPanel jpanel = new JPanel(new HorizontalLayout());
        JPanel jpanel1 = new JPanel(new VerticalLayout(5, 2, 2, 2, 2));
        getContentPane().add(jpanel1);
        JPanel jpanel2 = new JPanel(new PropertyLayout());
        JPanel jpanel3 = new JPanel(new ButtonLayout());
        JPanel jpanel4 = new JPanel(new ButtonLayout());
        JLabel jlabel = new JLabel(appReg.getImageIcon("createFolder.ICON"), 0);
        JLabel jlabel1 = new JLabel(appReg.getString("名称"));
		
        String s = TCSession.getServerEncodingName(session);
        FilterDocument filterdocument = new FilterDocument(128, s);
        folderName = new JTextField(filterdocument, "", 20) {

            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g)
            {
                super.paint(g);
                Painter.paintIsRequired(this, g);
            }

        };
        folderName.setBorder(new EtchedBorder());
        folderName.addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent keyevent)
            {
                validateEntries();
            }

        });
        JLabel jlabel2 = new JLabel(appReg.getString("描述"));
        filterdocument = new FilterDocument(240, s);
        folderDescription = new JTextArea(filterdocument, "", 3, 10);
        folderDescription.setLineWrap(true);
        folderDescription.setWrapStyleWord(true);
        
 
        
        okButton = new JButton(appReg.getString("ok"));
        okButton.setMnemonic(appReg.getString("ok.MNEMONIC").charAt(0));
        okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                okOrApply = 0;
                startCreateOperation();
            }

        });
        
        cancelButton = new JButton(appReg.getString("cancel"));
        cancelButton.setMnemonic(appReg.getString("cancel.MNEMONIC").charAt(0));
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                setVisible(false);
                dispose();
            }

        });   

		 isSetPrivilegeBox = new JCheckBox(appReg.getString("对本组成员赋写权限"),false);
		 isSetPrivilegeBox.setForeground(Color.red);
		 	 

	     jpanel4.add("1.1.center.center.preferred.preferred",isSetPrivilegeBox);	        
	    

        jpanel1.add("top.nobind.left", jlabel);
        jpanel1.add("top.bind", new Separator());
        jpanel2.add("1.1.right.center.preferred.preferred", jlabel1);
        jpanel2.add("1.2.right.center.preferred.preferred", folderName);
        jpanel2.add("2.1.right.center.preferred.preferred", jlabel2);
        jpanel2.add("2.2.right.center.preferred.preferred", folderDescription);
       
        jpanel3.add(okButton);     
        jpanel3.add(cancelButton);
        
        //JPanel jpanel4 = new JPanel(new VerticalLayout());
        //jpanel4.add("unbound.bind.center.top", jpanel2);
        jpanel.add("unbound.bind.left.top", jpanel2);
        jpanel1.add("unbound.bind.left.top", jpanel);
        jpanel1.add("bottom.bind.center.top", jpanel3);
        jpanel1.add("bottom.nobind.right", jpanel4);
		//jpanel1.add("bottom.nobind.right", isSetPrivilegeBox);
        jpanel1.add("bottom.bind", new Separator());
        pack();
        centerToScreen(1.0D, 0.75D);
        folderName.requestFocus();
    }

    private void startCreateOperation(){
    	String FolderType="";

    	FolderType="Folder";

    	
    	
    	createFolderOp = new NewFolderOp(session, desktop, getFolderName(), getFolderDescription(), FolderType, pasteTargets);
    	createFolderOp.addOperationListener(this);
    	createFolderOp.addPropertyChangeListener(this);
    	session.queueOperation(createFolderOp);
    	
    	
    }

    public void startOperation(String s){
        okButton.setVisible(false);
        cancelButton.setVisible(false);    
        folderName.setEnabled(false);
        folderDescription.setEnabled(false);
        //folderTypes.setEnabled(false);
        //openOnCreate.setEnabled(false);
        validate();
    }

    public void endOperation(){
       createFolderOp.removeOperationListener(this);
        if(!createFolderOp.isAbortRequested())
        {
            okButton.setVisible(true);
            cancelButton.setVisible(true);
            folderName.setEnabled(true);
            folderDescription.setEnabled(true);
            //folderTypes.setEnabled(true);
            //openOnCreate.setEnabled(true);
            folderName.requestFocus();
            folderName.selectAll();
            validate();
          
			   if (isSetPrivilegeBox.isSelected())
			   {
				try
                {
				   TCComponent  NewFolder = createFolderOp.getNewFolder();
			       TCAccessControlService  accessControlService = session.getTCAccessControlService();
			       if(accessControlService != null)
                   {  
			    	   TCProperty imanproperty1 = NewFolder.getTCProperty("owning_group");
					   String ss = 	imanproperty1.toString();
					   System.out.println("拥有组：：：：：：：：："+ss);
					
                      TCComponentGroup imancomponentgroup = (TCComponentGroup)imanproperty1.getReferenceValue();
					  accessControlService.grantPrivilege(NewFolder, imancomponentgroup, "WRITE"); 
			       }
				}
                 catch (Exception ex){}
			   }
 
            if(okOrApply == 0 && createFolderOp.getSuccessFlag())
            {
                setVisible(false);
                dispose();
            }

        }
    }


   

    public void propertyChange(PropertyChangeEvent propertychangeevent){
        String s = propertychangeevent.getPropertyName();
        if(s.equals("iMAN Stop Operation"))
        {
            Thread thread = new Thread() {

                public void run(){ } 
            };
            thread.start();
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener propertychangelistener)
    {
        propertySupport.addPropertyChangeListener(propertychangelistener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertychangelistener)
    {
        propertySupport.removePropertyChangeListener(propertychangelistener);
    }

    public String getFolderName()
    {
        return folderName.getText();
    }

    public String getFolderDescription()
    {
        return folderDescription.getText();
    }

  

    public boolean getOpenOnCreateFlag()
    {
        return openOnCreate.isSelected();
    }

    protected void validateEntries()
    {
        if(folderName.getText().length() > 0 )
        {
            okButton.setEnabled(true);
        } else
        {
            okButton.setEnabled(false);       
        }
    }

    public void run(){
        setVisible(true);
    }

    private JTextField folderName;
    private JTextArea folderDescription;
    private JCheckBox openOnCreate;
    private JButton okButton;
    private JButton cancelButton;
    protected short okOrApply;
    protected TCSession session;
    protected InterfaceAIFComponent pasteTargets[];
    protected AIFDesktop desktop;
    protected Frame parent;
    protected NewFolderOp createFolderOp;
    protected String openOnCreatePref;
    protected TCPreferenceService prefService;
    protected Registry appReg;
    private PropertyChangeSupport propertySupport;	
	private JCheckBox isSetPrivilegeBox;

}
