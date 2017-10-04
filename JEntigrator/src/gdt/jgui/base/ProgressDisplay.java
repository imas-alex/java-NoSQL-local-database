package gdt.jgui.base;

import javax.swing.*;

import gdt.jgui.console.JMainConsole;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
    Dialog which displays  progress.

  */
public class ProgressDisplay extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int PROGRESS_BAR_WIDTH = 200;

    private Runnable runnable;

    private JProgressBar progressBar;
    private JLabel lblMessage;
    private JMainConsole console;
    int takt=1000;
    boolean stop=false;
    private JButton cancel;
    Thread task;
    /**
     * Constructor.
     * @param console the main console.
     * @param runnable the <tt>Runnable</tt> to be started on <tt>setVisible</tt>.
     * @param message the initial status message.
     */
    public ProgressDisplay(JMainConsole console, Runnable runnable, String message) {
        super(console.getFrame());
        this.console=console;
        init(runnable, message);
    }

   
    /**
     * Set the current status message.
     * @param message the message.
     */
    public void setMessage(String message) {
        lblMessage.setText(message);
    }

    /**
     * Set the  <tt>Runnable</tt> to be started on <tt>setVisible</tt>.
     * @param runnable the <tt>Runnable</tt>.
     */
    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    private void init(Runnable runnable, String message) {
        setupControls();
        setupComponent();
        setupEventHandlers();
        setMessage(message);
        setRunnable(runnable);
        addWindowListener(new WindowAdapter(){
        	 @Override
             public void windowClosing(WindowEvent e) {
               cancel();
        	 }
        });
       
       
    }

    private void setupControls() {

        progressBar = new JProgressBar(0,100);
        Dimension preferredSize = progressBar.getPreferredSize();
        preferredSize.width = PROGRESS_BAR_WIDTH;
        progressBar.setPreferredSize(preferredSize);
        lblMessage = new JLabel(" ");
        cancel=new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
				
			}
		} );
    }
    private void cancel() {
		//System.out.println("ProgressDisplay:cancel");
		stop=true;
		task.interrupt();
		console.clipboard.setProgressStop(true);
		setVisible(false);
	}
    private void setupComponent() {

        JPanel contentPane = (JPanel)getContentPane();
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.gridx = 0;
        gc.gridy = GridBagConstraints.RELATIVE;
        gc.anchor = GridBagConstraints.NORTHWEST;
        contentPane.add(lblMessage, gc);
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(progressBar, gc);
       // gc.weightx = 0;
        gc.fill = GridBagConstraints.CENTER;
        gc.anchor = GridBagConstraints.CENTER;
        contentPane.add(cancel, gc);
        setTitle("");
        setModal(true);
        pack();

    }

    private void setupEventHandlers() {

        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent event) {
            	//System.out.println("ProgressDialog:componentShown");
            	progressBar.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            	progressBar.setStringPainted(true);
            	console.clipboard.setProgressStop(false);
            	//final Thread
            	task = new Thread(runnable);
            	//takt=(int)console.clipboard.getProgressLength();
                task.start();
                new DisplayThread().start();
               
              
            }
        });
    }
    public class DisplayThread extends Thread{ 
    public void run(){
		 try {
             
             long max=console.clipboard.getProgressLength(); 
            takt=(int)max;
             long progress=console.clipboard.getProgress();
             //System.out.println("DisplayThread:stop="+stop);
             if(stop||!task.isAlive()){
            	 console.clipboard.resetProgress(0);
            	 ProgressDisplay.this.setVisible(false);
            	return;
             }
             double i=((double)progress/max)*100;
           //  if(console.clipboard.getProgressMessage()!=null)
            // 	setMessage(console.clipboard.getProgressMessage());
          
             Thread.sleep(takt);
             int p=(int)i;
            //System.out.println("DisplayThread:p="+progress+" m="+max);
            //System.out.println("DisplayThread:task="+task.isAlive());
            progressBar.setValue(p);
           
              new DisplayThread().start();
             
		 }catch(Exception e){}
	 }
 };


}