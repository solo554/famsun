package com.famsun.rac.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

import com.famsun.rac.operations.CheckExistsOperation;
import com.famsun.rac.operations.NewDatasetOperation;
import com.famsun.rac.operations.NewItemOperation;
import com.famsun.rac.util.ConnectionPool;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.commands.namedreferences.ImportFilesFileChooser;
import com.teamcenter.rac.common.lov.LOVComboBox;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinition;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinitionType;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.VerticalLayout;

public class NewItemDialog extends AbstractAIFDialog
		implements InterfaceAIFOperationListener, PropertyChangeListener {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected TCSession tcsession;
	protected Frame parent;
	protected InterfaceAIFComponent[] pasteTargets;
	protected AIFDesktop desktop;
    protected NewItemOperation newItemOp;
	
	protected JTextField itemId_Field;
	protected JTextField itemName_Field;
	private JTextField jText1;
	private JTextField jText2;
	private JButton button_OK;
	private AbstractButton button_Cancel;
	private Component itemRev_Field;
	
    
	private Registry appReg;
    
	private short okOrApply;

	private String item_ID;
	private String item_Name;
	private JTextField txt_FileName;
	private JButton button_Browser;

	private String datasetType;
	private ImportFilesFileChooser fc;
    private String importFileType;
    private String importRefType;
	private JCheckBox jSE_Box1;
	private JCheckBox jSE_Box3;
	private JCheckBox jSE_Box2;
	private JCheckBox jSE_Box4;
	private String mydataset;
	private NewDatasetOperation datasetOp;
	private JButton button_MDM;
	private JTextField itemRev_DrawingNo;
	private JTextField jText3;
	private JLabel jLable4;
	private LOVComboBox pLOV_Type;
	private JButton button_SC;


	public NewItemDialog(Frame frame, InterfaceAIFComponent ainterfaceaifcomponent[]){
	     super(frame, true);
	     tcsession = null;
	     desktop = null;
	     fc=null;
	     parent = null;
	     datasetOp=null;
 	     tcsession = (TCSession)ainterfaceaifcomponent[0].getSession();
	     pasteTargets = ainterfaceaifcomponent;
	     parent = frame;
	     if(frame instanceof AIFDesktop)
	    	 desktop = (AIFDesktop)frame;
	     initializeDialog();
	     
	     }

	private void initializeDialog() {
		// TODO Auto-generated method stub
				setTitle("创建零组件");//窗口名称
				appReg = Registry.getRegistry(this);
				
				//选择excel文件的面板

				JPanel jPanel1 = new JPanel(new PropertyLayout(10,15,5,5,5,10));

				JPanel jPanel2 = new JPanel(new GridLayout(1,4));
				JPanel jPanel3 = new JPanel(new PropertyLayout(5,15,5,5,5,10));
				JPanel jPanel4 = new JPanel(new FlowLayout());
				
				JLabel jlabel1 = new JLabel("零组件ID:");		
				jPanel1.add("1.1.left", jlabel1);
				itemId_Field = new JTextField(15);
				itemId_Field.setBorder(new EtchedBorder());
				itemId_Field.addKeyListener(new KeyAdapter() {

			            public void keyReleased(KeyEvent keyevent)
			            {
			            	validateEntries();
			            }

			        });
				jPanel1.add("1.2.left", itemId_Field);
				
				button_SC = new JButton("智能编码器");	 
				button_SC.setEnabled(true);				
				jPanel1.add("1.3.left", button_SC);
				button_SC.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent actionevent) {
						SmartCodeDialog scconfig= new SmartCodeDialog(parent,tcsession,NewItemDialog.this,"编码申请",true);
						scconfig.setVisible(true);
					}
		    	});
				
				
				JLabel jlabel21 = new JLabel("零组件名称");
				jPanel1.add("2.1.left", jlabel21);
				itemName_Field = new JTextField(25);
				itemName_Field.setBorder(new EtchedBorder());
				itemName_Field.addKeyListener(new KeyAdapter() {
		            public void keyReleased(KeyEvent keyevent){
		            	validateEntries();
		            }

			    });
				
				jPanel1.add("2.2.left",itemName_Field);				
				
				button_MDM = new JButton("查询MDM");	 
				button_MDM.setEnabled(false);				
				jPanel1.add("2.3.left", button_MDM);
				button_MDM.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent actionevent) {
						
						queryMdm(itemId_Field.getText());
							
					}
		    	});
				

				JLabel jlabel31 = new JLabel("零组件版本");
				jPanel1.add("3.1.left", jlabel31);
				itemRev_Field = new JTextField("A",10);
				itemRev_Field.setEnabled(false);
				
				JLabel jlabel33 = new JLabel("类别");
				pLOV_Type = new LOVComboBox(tcsession,"M2MY_Type");
				pLOV_Type.setTextFieldLength(11);
				pLOV_Type.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						validateEntries();
					}

			    });
				pLOV_Type.addKeyListener(new KeyAdapter(){

					@Override
					public void keyPressed(KeyEvent e) {
						// TODO Auto-generated method stub
						if(e.getKeyCode()==KeyEvent.VK_BACK_SPACE)
						validateEntries();
					}
					
				});
				
				jPanel1.add("3.1.left", jlabel31);
				jPanel1.add("3.2.left",itemRev_Field);
				jPanel1.add("3.3.left",jlabel33);
				jPanel1.add("3.4.left",pLOV_Type);
				
				JLabel jlabel41 = new JLabel("零件图号");
				jPanel1.add("4.1.left", jlabel41);
				itemRev_DrawingNo = new JTextField(25);
				itemRev_DrawingNo.setBorder(new EtchedBorder());
				itemRev_DrawingNo.setEnabled(true);
				jPanel1.add("4.2.left",itemRev_DrawingNo);

				
				JLabel jlabel51 = new JLabel("选择文件:");		
				jPanel1.add("5.1.left", jlabel51);
				
				txt_FileName = new JTextField(25);	
				txt_FileName.setBorder(new EtchedBorder());
				jPanel1.add("5.2.left", txt_FileName);
				
				button_Browser = new JButton("选择 SE 文件");	 

				button_Browser.setToolTipText("importButton.TIP");
				button_Browser.setFocusPainted(false);
				button_Browser.setEnabled(true);				
				jPanel1.add("5.3.left", button_Browser);
				button_Browser.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent actionevent) {
						if(jSE_Box1.isSelected()||jSE_Box2.isSelected()||jSE_Box3.isSelected()||jSE_Box4.isSelected()) {
							startImportFile(datasetType);	
						}
						else {
							MessageBox  msgBox = new MessageBox("请先选择文件类型!","提示",MessageBox.INFORMATION);
		    		        msgBox.setModal(true);
		    		        msgBox.setVisible(true);
						}
							
					}
			    });
				
				jSE_Box1 = new JCheckBox("Part",false);
				jSE_Box2 = new JCheckBox("SheetMetal",false);
				jSE_Box3 = new JCheckBox("Assembly",false);
				jSE_Box4 = new JCheckBox("Draft",false);
				jPanel2.setBorder(BorderFactory.createEtchedBorder());
				jPanel2.add(jSE_Box1);
				jPanel2.add(jSE_Box2);
				jPanel2.add(jSE_Box3);
				jPanel2.add(jSE_Box4);

				jSE_Box1.addActionListener(new ActionListener() {

		            public void actionPerformed(ActionEvent actionevent)
		            {
		             if (jSE_Box1.isSelected())
		             {

						 datasetType="SE Part";
						 jSE_Box2.setSelected(false);
						 jSE_Box3.setSelected(false);
						 jSE_Box4.setSelected(false);
						 
		             }
					}
		          });
				jSE_Box2.addActionListener(new ActionListener() {

		            public void actionPerformed(ActionEvent actionevent)
		            {
		             if (jSE_Box2.isSelected())
		             {
						 //datasetType=jAutoCadBox.getText();
						 datasetType="SE SheetMetal";
						 jSE_Box1.setSelected(false);
						 jSE_Box3.setSelected(false);
						 jSE_Box4.setSelected(false);
						 
		             }
					}
		          });				
				jSE_Box3.addActionListener(new ActionListener() {

			            public void actionPerformed(ActionEvent actionevent)
			            {
			             if (jSE_Box3.isSelected())
			             {
							 //datasetType=jAutoCadBox.getText();
							 datasetType="SE Assembly";
							 jSE_Box1.setSelected(false);
							 jSE_Box2.setSelected(false);
							 jSE_Box4.setSelected(false);
							 
			             }
						}
			          });
				jSE_Box4.addActionListener(new ActionListener() {

		            public void actionPerformed(ActionEvent actionevent)
		            {
		             if (jSE_Box4.isSelected())
		             {
						 //datasetType=jAutoCadBox.getText();
						 datasetType="SE Draft";
						 jSE_Box1.setSelected(false);
						 jSE_Box2.setSelected(false);
						 jSE_Box3.setSelected(false);
						 
		             }
					}
		          });
				
				jLable4= new JLabel("注意：创建完毕后需使用SE打开保存！");
				jLable4.setForeground(Color.red);
				jPanel3.add("1.1.left",jLable4);
				
				button_OK = new JButton("创建");	   
			    button_OK.setEnabled(false);			    
			    button_OK.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent actionevent)
		            {
		            	button_OK.setEnabled(false);
		            	try{
			            	item_ID=itemId_Field.getText();
		            		CheckExistsOperation op = new CheckExistsOperation(tcsession,item_ID);
			            	op.executeOperation();
			            	
			            	if(!op.getSuccessFlag()) {
			                	okOrApply=0;
				            	startCreateItemOperation();
			                }
			            	else{
			        		    MessageBox  msgBox = new MessageBox("编码已存在!","提示",MessageBox.INFORMATION);
			    		        msgBox.setModal(true);
			    		        msgBox.setVisible(true);
								setVisible(false);
								dispose();
			                }            
		            	}
		            	catch(Exception e){
		     	    	   System.out.println("error");
		    	    	   e.printStackTrace();
		            	}		            		            	 
		                
		             }

		        });
				
			    button_Cancel = new JButton("取消");
				button_Cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent actionevent) {
						setVisible(false);
						dispose();
					}
				});
	
					
				// 按钮面板
				jPanel4.add(button_OK);
				jPanel4.add(new JLabel(" "));
				jPanel4.add(button_Cancel);	
				
				// 总面板
				VerticalLayout sumLayout = new VerticalLayout();
				sumLayout.setVerticalGap(10);
				sumLayout.setTopMargin(10);
				sumLayout.setBottomMargin(10);
				sumLayout.setLeftMargin(7);
				sumLayout.setRightMargin(7);
				JPanel pane = new JPanel(sumLayout);
				pane.add("top.bind", jPanel1);
				pane.add("top2.bind",jPanel2);
				pane.add("top3.bind",jPanel3);
				pane.add("bottom.nobind", jPanel4);
				
				getContentPane().add(pane);
				setSize(getWidth(), getHeight() + 50);
			
				pack();
				centerToScreen(1.0D, 1.0D);
	}
		
	private void validateEntries(){
	        if(itemId_Field.getText().length() > 5 && itemName_Field.getText().length()>0 && pLOV_Type.getSelectedString().length()>1 ){
	        	button_OK.setEnabled(true);      
	        } 
	        else{
	        	button_OK.setEnabled(false);
	        }
	        
	        if(itemId_Field.getText().length() > 5 && itemName_Field.getText().length()==0 ) {
	        	button_MDM.setEnabled(true);
	        } 
	        else{
	        	button_MDM.setEnabled(false);
	        }
	        
	}

	private void startCreateItemOperation() {
			// TODO Auto-generated method stub

	       item_ID = null;
	       item_Name=null;
	   
	       try{
	            item_ID = itemId_Field.getText();
	            item_Name=itemName_Field.getText();
	            
	            System.out.println(item_ID + "/A-"+item_Name);			
	            newItemOp = new NewItemOperation(tcsession,desktop,item_Name,item_ID,"A",null,"Item",null,pasteTargets);
				newItemOp.addOperationListener(this);
	     	    newItemOp.addPropertyChangeListener(this);
	     	    tcsession.queueOperation(newItemOp);

	       }
	       catch(Exception e){    	   
	    	   System.out.println("error");
	    	   MessageBox.post(e);
	       }
	       
	      
			
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startOperation(String arg0) {
		// TODO Auto-generated method stub

		
	}
	
	@Override
	public void endOperation() {
		// TODO Auto-generated method stub
		 newItemOp.removeOperationListener(this);
         if(!newItemOp.isAbortRequested()){
        	 if(okOrApply == 0 && newItemOp.getSuccessFlag()){
   	        	 MessageBox  msgBox = new MessageBox("创建成功！","提示",MessageBox.INFORMATION);
  	    	     msgBox.setModal(true);
  	    	     msgBox.setVisible(true); 
 			     setVisible(false);
				 dispose(); 
				 
				 if(pLOV_Type.getSelectedString().length()>0){
					 try{
						//属性表赋值	     	    
						TCComponentItemRevision newItemRevision=newItemOp.getNewItem().getLatestItemRevision();	        	
						TCComponentForm itemRevForm=(TCComponentForm)newItemRevision.getRelatedComponent("IMAN_master_form_rev");
						String drawingNo=itemRev_DrawingNo.getText();
						String partType=pLOV_Type.getSelectedString();
						String[] itemRevFormNames=new String[] {"m2MY_DrawingNo","m2MY_Mass","m2MY_Material","m2MY_Memo","m2MY_Stock","m2MY_Type"};	    
						String[] itemRevFormValues=new String[] {drawingNo,"","","","",partType};

				        TCProperty[]  formProperties = itemRevForm.getFormTCProperties(itemRevFormNames);
				 
				        for(int k=0;k<formProperties.length;k++){
						         formProperties[k].setStringValueData(itemRevFormValues[k]);    			
				        }
				        itemRevForm.setTCProperties(formProperties);
						}catch(Exception e){   
				    	    System.out.println("error");
				    	    MessageBox.post(e);
						}
				 }
				 
				 
        		 if(txt_FileName.getText().length()>5) {
                	 mydataset=item_ID+"/A";              	 
          			try{			   		     
     				    TCComponentItem newItem=newItemOp.getNewItem();
     				    TCComponentItemRevision tcNewItemRevision=newItem.getLatestItemRevision();	        	
      					InterfaceAIFComponent[] pasteToItemRev =new InterfaceAIFComponent[1];
      					pasteToItemRev[0]=(InterfaceAIFComponent)tcNewItemRevision;				
      		    	    datasetOp = new NewDatasetOperation(tcsession, desktop, mydataset, "", datasetType, getToolType(), fc.getSelectedFile().getAbsolutePath(), importFileType, importRefType, true, pasteToItemRev);		       						      				
      					tcsession.queueOperation(datasetOp);  
      					
      					
          			}
          	        catch(Exception e){
          	        	MessageBox.post(e);
          	        }          	        
          	        
                 } 
        	 }
        	 
         }
		 
	}


	 
	 private void startImportFile(String str){
	        fc=null;
		    TCComponentDatasetDefinition imancomponentdatasetdefinition = null;
	        com.teamcenter.rac.kernel.NamedReferenceContext anamedreferencecontext[] = null;
	        try
	         {
	            TCComponentDatasetDefinitionType imancomponentdatasetdefinitiontype = (TCComponentDatasetDefinitionType)tcsession.getTypeComponent("DatasetType");
	            imancomponentdatasetdefinition = imancomponentdatasetdefinitiontype.find(str);
	            anamedreferencecontext = imancomponentdatasetdefinition.getNamedReferenceContexts();
	         }
	        catch(TCException imanexception){MessageBox.post(parent, imanexception);}
	         
	        if(anamedreferencecontext == null || anamedreferencecontext.length == 0)
	         {
	            MessageBox.post(parent, appReg.getString("noRefTypeFound"), appReg.getString("noRefTypeFound.TITLE"), 2);
	            return;
	         }
	        if(fc == null)
	            fc = new ImportFilesFileChooser(imancomponentdatasetdefinition, parent);
	        else
	        if(imancomponentdatasetdefinition != fc.getDatasetDefinition())
	            fc.setDatasetDefinition(imancomponentdatasetdefinition);
	        int i = fc.showDialog(parent, null);
	        if(i == 0)
	        {
	            File file = fc.getSelectedFile();
	            importFileType = fc.getType();
	            importRefType = fc.getReferenceType();
	            if(file != null)
	            {
	            	txt_FileName.setText(file.getAbsolutePath());
	            } else
	            {
	                MessageBox messagebox = new MessageBox(parent, appReg.getString("noImportFileSelection"), "", appReg.getString("warning.TITLE"), 2, true);
	                messagebox.setVisible(true);
	            }
	        }
	    }


	 private String getToolType(){
        String flag="";
        
			if (datasetType.equals("SE Part")){
				flag="SEpart";
			}
			else if(datasetType.equals("SE Draft")){
				flag="SEdraft";
			}
			else if(datasetType.equals("SE Assembly")){
				flag="SEassembly";
			}
			else if(datasetType.equals("SE SheetMetal")){
				flag="SEsheetMetal";
			}
		     		
		return flag;
		
    }
	 
	 protected void queryMdm(String str){

		String Ssql="select DESCLONG,FREEZEFLAG from MYMDM.MDM_WLBM_CODE where CODE='"+str+"'";
		String desclong="";
		ConnectionPool pool =ConnectionPool.getInstance();
   		Connection con = pool.getConnection();
   		Statement  myStmt=null; 	
		ResultSet myRs=null;
		try{
		 	
	   		myStmt=con.createStatement(); 	
			myRs=myStmt.executeQuery(Ssql);	
			 
			while(myRs.next()){
		    	desclong=myRs.getString("DESCLONG"); 			
			 }
			
			if (desclong==null || desclong.length()==0){
				MessageBox  msgBox1 = new MessageBox("MDM中不存在该编码！","提示",MessageBox.INFORMATION);
		        msgBox1.setModal(true);
		        msgBox1.setVisible(true);
		        return;
			}
			
	     }catch (Exception ex){
	    	 ex.printStackTrace();
		 }finally{
			 try{
				 if(myRs!=null){
					 myRs.close();
				 }if(myStmt!=null){
					 myStmt.close();
				 }if(con!=null){
					 con.close();
				 }
				 
			 }catch(Exception ex){
	    	 ex.printStackTrace();

			 }
		 }
		 
		 
		 
		 if( desclong!=null ){
	
				final JDialog dialog=new JDialog(parent,"查询MDM",true);
				dialog.setSize(350, 250);
				dialog.setResizable(false);
					
				PropertyLayout propertyLayout = new PropertyLayout();
				propertyLayout.setVerticalGap(15);
				propertyLayout.setTopMargin(5);
				propertyLayout.setBottomMargin(10);
				
				JLabel lable1= new JLabel("MDM物料描述");
				jText1 = new JTextField(desclong,30);
				jText1.setEditable(false);
				jText1.setBackground(Color.gray);
				
				JLabel lable2= new JLabel("名称");
				jText2 = new JTextField(desclong,30);
				jText2.setBorder(new EtchedBorder());
				
				JLabel lable3= new JLabel("图号");
				jText3 = new JTextField(desclong,30);
				jText3.setBorder(new EtchedBorder());
		
				JPanel panel1= new JPanel(propertyLayout);
				panel1.add("1.1.left",lable1);
				panel1.add("1.2.left",jText1);
				panel1.add("2.1.left",lable2);
				panel1.add("2.2.left",jText2);
				panel1.add("3.1.left",lable3);
				panel1.add("3.2.left",jText3);
	
				JPanel panel2= new JPanel(new PropertyLayout());
				JLabel lable4= new JLabel("注意：请按照规范填写零件名称、图号属性！");
				lable4.setForeground(Color.red);
				panel2.add("1.1.left",lable4);
				
				JButton okBtn=new JButton("确定");				
				okBtn.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent actionevent) {
						if(jText2.getText().length()>1 && jText3.getText().length()>1){
							itemName_Field.setText(jText2.getText());
							itemRev_DrawingNo.setText(jText3.getText());
							validateEntries();
						}
						
						dialog.dispose();
					}
		 		});
				
				JButton canBtn=new JButton("取消");				
				canBtn.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent actionevent) {
						dialog.dispose();
					}
		 		});
				
				JPanel panel3= new JPanel(new FlowLayout());
				panel3.add(okBtn);
				panel3.add(canBtn);
				
				VerticalLayout sumLayout = new VerticalLayout(10,7,7,10,10);
				
				JPanel pane = new JPanel(sumLayout);
				pane.add("top.bind", panel1);
				pane.add("top1.bind", panel2);
				pane.add("top2.bind",panel3);				
				dialog.setContentPane(pane);
				
				if (JDialog.isDefaultLookAndFeelDecorated()) {
		            boolean supportsWindowDecorations =
		            UIManager.getLookAndFeel().getSupportsWindowDecorations();
		            if (supportsWindowDecorations) {
		                dialog.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		            }
		        }
			
				dialog.pack();
				dialog.setLocationRelativeTo(this);
				dialog.setVisible(true);			
		 }
		 
		 return ;
		
	 }

}
