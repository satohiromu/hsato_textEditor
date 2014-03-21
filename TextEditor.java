//import java.awt.*;
//import javax.swing.*;
//import java.awt.event.*;
//import java util.*;

import java.io.*;
import java.util.List;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.InputEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.*;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;
import javax.swing.JComponent;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.mozilla.universalchardet.UniversalDetector;

public class TextEditor extends JFrame implements ActionListener{
	
	private JTextArea textArea;
	private String filePath = new File("").getAbsolutePath();
	private String textTitle = "No title";
	private String textData = "";
	private String saveMode = "save";
	//final String saveCheckMsg = "The contents of the file has changed.\nDo you want to save it?";
	private final String saveCheckMsg = "ファイルの内容が変更されています。\n保存しますか？";
	private boolean fileSavedCheck = true;
	private boolean fileOpendCheck = false;

	// Constructor
	TextEditor(){
//		setTitle(textTitle + " - TextEditor");
//		setFrameTitle(textTitle);
		setSize(640, 480);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new MyWindowEvent());
	}

	public void setTextArea(){
		JPanel textFieldPanel = new JPanel();
		textArea = new JTextArea(25, 50);

		// border setting
		textArea.setBorder(new BevelBorder(BevelBorder.LOWERED));

		// carret setting
		textArea.setCaretPosition(0);

		// add key event
		textArea.addKeyListener(new MyKeyEvent());

		// D&D event
		textArea.setTransferHandler(new MyDropEvent());


		JScrollPane scrollPane = new JScrollPane(textArea);
		textFieldPanel.add(scrollPane);
		
		Container contentPane = getContentPane();
		contentPane.add(textFieldPanel, BorderLayout.CENTER);
	}

	public void setMenu(){
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		// new
		JMenuItem fileMenuItemNew = new JMenuItem("New");
		fileMenuItemNew.setMnemonic(KeyEvent.VK_N);
		fileMenuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		fileMenuItemNew.setActionCommand("New");
		fileMenuItemNew.addActionListener(this);
		
		// open
		JMenuItem fileMenuItemOpen = new JMenuItem("Open");
		fileMenuItemOpen.setMnemonic(KeyEvent.VK_O);
		fileMenuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		fileMenuItemOpen.setActionCommand("Open");
		fileMenuItemOpen.addActionListener(this);
		
		// save
		JMenuItem fileMenuItemSave = new JMenuItem("Save");
		fileMenuItemSave.setMnemonic(KeyEvent.VK_S);
		fileMenuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		fileMenuItemSave.setActionCommand("Save");
		fileMenuItemSave.addActionListener(this);
		
		// save as
		JMenuItem fileMenuItemSaveas = new JMenuItem("Save as");
		fileMenuItemSaveas.setMnemonic(KeyEvent.VK_A);
		fileMenuItemSaveas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		fileMenuItemSaveas.setActionCommand("Save_as");
		fileMenuItemSaveas.addActionListener(this);

		// close
		JMenuItem fileMenuItemClose = new JMenuItem("Close");
		fileMenuItemClose.setMnemonic(KeyEvent.VK_C);
		fileMenuItemClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		fileMenuItemClose.setActionCommand("Close");
		fileMenuItemClose.addActionListener(this);


		// add menuItem
		fileMenu.add(fileMenuItemNew);
		fileMenu.add(fileMenuItemOpen);
		fileMenu.add(fileMenuItemSave);
		fileMenu.add(fileMenuItemSaveas);
		fileMenu.add(fileMenuItemClose);
		menuBar.add(fileMenu);
	
		this.setJMenuBar(menuBar);
	}

	// menuItem event
	public void actionPerformed(ActionEvent event){
		String cmd = event.getActionCommand();
		switch(cmd){
			case "New" :
				newFile();
				break;
			case "Open" :
				openFile("");
				break;
			case "Save" :
				saveFile(saveMode);
				break;
			case "Save_as" :
			//	saveAsFile();
				saveMode = "saveAs";
				saveFile(saveMode);
				break;
			case "Close" :
				closeFrame();
				break;
		}
	}

	// set fileName
	public void setFrameTitle(String title){
		this.setTitle(title + " - TextEditor");
	}

/*
	// set fileChooser FileFilter
	public void setFileChooserFileFilter(){
		//JFileChooser fileChooser = new JFileChooser(filePath);
		fileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
		fileChooser.setFileFilter(new FileNameExtensionFilter("*.java", "java"));
		fileChooser.setFileFilter(new FileNameExtensionFilter("*.html", "html"));
	}
*/

	// file create
	public void newFile(){

		if(!fileSavedCheck){
			int closeCheck = JOptionPane.showConfirmDialog(this, saveCheckMsg, 
				"caution", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

			if(closeCheck == JOptionPane.YES_OPTION){
				saveFile(saveMode);
			}else if(closeCheck == JOptionPane.CANCEL_OPTION){
				return;
			}
			fileSavedCheck = true;
		}
		
		textTitle = "No title";
		setFrameTitle(textTitle);
		textArea.setText("");
		filePath = null;
		fileOpendCheck = false;
	}

	// file open
	public void openFile(String fileName){
		String textLine;
		File file = null;
		boolean fileCheck = false;
		String encodingCheck;
		//BufferedReader br = null;

		if(fileName.equals("")){
			JFileChooser fileChooser = new JFileChooser(filePath);
			fileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
			fileChooser.setFileFilter(new FileNameExtensionFilter("*.java", "java"));
			fileChooser.setFileFilter(new FileNameExtensionFilter("*.html", "html"));
			
			int selected = fileChooser.showOpenDialog(this);
			if(selected == fileChooser.APPROVE_OPTION){
				file = fileChooser.getSelectedFile();
				fileCheck = true;
			}else{
				return;
			}
		}else{
			file = new File(fileName);
			String[] checkDir = file.list();
			if(checkDir == null){ // file or directory check
				fileCheck = true;
			}
		}
		
		try{
			if(fileCheck){
				BufferedReader br = null;
				textArea.setText("");
				textTitle = file.getName();
                                setFrameTitle(textTitle);
                                filePath = file.getAbsolutePath();

				encodingCheck = getEncoding(file);
				//System.out.println("encode : " + encodingCheck);

				String[] encodeList = {"UTF-8", "UTF-16LE", "EUC-JP","SHIFT_JIS"};
				
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				for(int i=0;i<encodeList.length;i++){
                                	if(encodingCheck != null && encodingCheck.equals(encodeList[i])){
                                        	br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encodeList[i]));
						break;
					}
				}	
				while((textLine = br.readLine()) != null){
					textArea.append(textLine);
					textArea.append("\n");
				}
				br.close();

				int lastNewLineCount = textArea.getText().length();
				textArea.replaceRange("", lastNewLineCount-1, lastNewLineCount);
				
				fileOpendCheck = true;
			}else{
				System.out.println("ファイルではないため、開くことができません。");
			}
		}catch(FileNotFoundException error){
			System.out.println("FileNotFoundException");
		}catch(IOException error){
			System.out.println("IOException");
		}catch(NullPointerException error){
			System.out.println("NullPointer");
		}
		textData = textArea.getText();
	}

	// save or saveAs
	public void saveFile(String saveMode){
		JFileChooser fileChooser = new JFileChooser(filePath);
		fileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
		fileChooser.setFileFilter(new FileNameExtensionFilter("*.java", "java"));
		fileChooser.setFileFilter(new FileNameExtensionFilter("*.html", "html"));

		File file = null;
		String saveCommand = "save";
		//String saveAs = "saveAs";

		try{
			BufferedWriter bw;
			if(fileOpendCheck && saveMode.equals(saveCommand)){ // update
                                file = new File(filePath);
                                bw = new BufferedWriter(new FileWriter(file));
                                bw.write(textArea.getText());
                                bw.close();
                                fileSavedCheck = true;
			}else{ // save or saveAs
				int selected = fileChooser.showSaveDialog(this);
				if(selected == fileChooser.APPROVE_OPTION){
					file = fileChooser.getSelectedFile();
					file.createNewFile();
					textTitle = file.getName();
					setFrameTitle(textTitle);
					filePath = file.getAbsolutePath();

					bw = new BufferedWriter(new FileWriter(file));
					bw.write(textArea.getText());
					bw.close();
					fileSavedCheck = true;
				}
			}
			textData = textArea.getText();
			fileOpendCheck = true;
		}catch(FileNotFoundException error){
			System.out.println("FileNotFoundException");
		}catch(IOException error){
			System.out.println("IOException");
		}
	}
	
	// file close
	public void closeFrame(){
		if(fileSavedCheck){
			System.exit(0);
		}else{

			int closeCheck = JOptionPane.showConfirmDialog(this, saveCheckMsg, 
				"caution", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

			if(closeCheck == JOptionPane.YES_OPTION){
				saveFile(saveMode);
				System.exit(0);
			}else if(closeCheck == JOptionPane.NO_OPTION){
				System.exit(0);
			}else{
				System.out.println("cancel");
				return;
			}
		}
	}

       public String getEncoding(File file){
		String encoding = "";
		try{
                	UniversalDetector detector = new UniversalDetector(null);
                	FileInputStream fis = new FileInputStream(file);
                	byte[] byteArray = new byte[4096];
                	int read;

                	while((read = fis.read(byteArray)) > 0 && !detector.isDone()){
                	        detector.handleData(byteArray, 0, read);
                	}
                	detector.dataEnd();
                	encoding = detector.getDetectedCharset();

		}catch(FileNotFoundException error){
			System.out.println("file not found");	
		}catch(IOException error){
			System.out.println("IO error");
		}
		return encoding;
        }

	
	public static void main(String[] args){
		TextEditor textEditor = new TextEditor();
		textEditor.setMenu();
		textEditor.setTextArea();
		textEditor.newFile();
		textEditor.setVisible(true);
	}

	// frame close
	public class MyWindowEvent extends WindowAdapter{
		public void windowClosing(WindowEvent event){
			closeFrame();
		}
	}

	public class MyKeyEvent extends KeyAdapter{
		public void keyReleased(KeyEvent event){
			String text = textArea.getText();

			if(textData.equals(text)){
				fileSavedCheck = true;
				textData = text;
			}else{
				fileSavedCheck = false;
			}
			
		}
	}

	public class MyDropEvent extends TransferHandler{

		@Override
		public boolean canImport(TransferSupport support){
			
			if(!support.isDrop()){
				return false;
			}
			return true;
		}

		@Override
		public boolean importData(TransferSupport support) {
			try{
				Transferable transferble = support.getTransferable();

				Object obj = transferble.getTransferData(DataFlavor.javaFileListFlavor);
				List list = (List)obj;
				openFile(list.get(0).toString());

		//		for(Object file : list){
		//			System.out.println(list.get(0));
		//		}
		
				return true;
				
			}catch(UnsupportedFlavorException error){
				return false;
			}catch(IOException error){
				return false;
			}
		}

		@Override
		protected void exportDone(JComponent c, Transferable data, int action) {

		}
	}
}
