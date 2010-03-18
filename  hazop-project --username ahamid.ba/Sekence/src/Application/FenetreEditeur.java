package Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.PropertyVetoException;
import java.beans.Statement;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ResourceBundle;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Outils.FiltreExtension;
import Vue.NoeudAbstrait;
import Vue.ArrowHead;
import Vue.BentStyle;
import Vue.DiagrammeSequence;
import Vue.Graph;
import Vue.LineStyle;

public class FenetreEditeur extends JFrame
{

	public FenetreEditeur(Class appClass)
	{  

		String appClassName = appClass.getName();

		changeLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		fileService = FileService.getInstance(new File("."));      

		setTitle("Sekence");
		Dimension screenSize 
		= Toolkit.getDefaultToolkit().getScreenSize();

		int screenWidth = (int)screenSize.getWidth();
		int screenHeight = (int)screenSize.getHeight();

		setBounds(screenWidth / 16, screenHeight / 16,
				screenWidth * 7 / 8, screenHeight * 7 / 8);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new
				WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				exit();
			}
		});
		
		//Integration du workspace et du desktop dans un splitPane qui fera office du contentPane
		final FenetreWorkspace workspace = new FenetreWorkspace();
		

		desktop = new JDesktopPane();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, workspace, desktop);
		
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(120);

		setContentPane(splitPane);
		desktop.setBackground(new Color(100,100,100));
		defaultExtension = ".xml";

		filtreSequenssos = new FiltreExtension(
				"Filtre Sekence",
		".xml");

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("Fchier");
		menuBar.add(fileMenu);

		JMenuItem nouveauDiag = new JMenuItem("Nouveau diagramme de sÈquence");
		nouveauDiag.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					FenetreEditionDiagramme frame = new FenetreEditionDiagramme(
						new DiagrammeSequence());
						addInternalFrame(frame);
						workspace.setNomDiagramme("New");
				}
				catch (Exception exception)
				{
					exception.printStackTrace();
				}
			}
		});


		fileMenu.add(nouveauDiag);

		JMenuItem fileOpenItem = new JMenuItem("Ouvrir");
		fileOpenItem.addActionListener(new
				ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				openFile();
			}
		});
		fileMenu.add(fileOpenItem);      

		JMenuItem fileSaveItem = new JMenuItem("Sauvegarder"); 
		fileSaveItem.addActionListener(new
				ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				save();
			}
		});
		fileMenu.add(fileSaveItem);
		JMenuItem fileSaveAsItem = new JMenuItem("Sauvegarder sous");
		fileMenu.add(fileSaveAsItem);

		JMenuItem fileExitItem = new JMenuItem("Quitter");
		fileMenu.add(fileExitItem);

		if (fileService == null)
		{
			fileOpenItem.setEnabled(false);
			fileSaveAsItem.setEnabled(false);
			fileExitItem.setEnabled(false);
		}

		if (fileService == null) 
		{
			fileSaveItem.setEnabled(false);
		}

		JMenu editMenu = new JMenu("Edition");
		menuBar.add(editMenu);

		JMenuItem prop = new JMenuItem("Propriétés");
		prop.addActionListener(new
				ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				final FenetreEditionDiagramme frame 
				= (FenetreEditionDiagramme)desktop.getSelectedFrame();
				if (frame == null) return;
				GraphPanel panel = frame.getGraphPanel();
				panel.editSelected();
			}
		});
		editMenu.add(prop);
		JMenuItem suppr = new JMenuItem("Supprimer");
		suppr.addActionListener( new
				ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				FenetreEditionDiagramme frame 
				= (FenetreEditionDiagramme)desktop.getSelectedFrame();
				if (frame == null) return;
				GraphPanel panel = frame.getGraphPanel();
				panel.removeSelected();
			}
		});
		editMenu.add(suppr);

		JMenu viewMenu = new JMenu("Vue");
		menuBar.add(viewMenu);
		JMenuItem dezoom = new JMenuItem("Dezoomer");

		dezoom.addActionListener(new
				ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				FenetreEditionDiagramme frame 
				= (FenetreEditionDiagramme)desktop.getSelectedFrame();
				if (frame == null) return;
				GraphPanel panel = frame.getGraphPanel();
				panel.changeZoom(-1);
			}
		});
		viewMenu.add(dezoom);

		JMenuItem zoom = new JMenuItem("Zoomer");

		zoom.addActionListener(new
				ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				FenetreEditionDiagramme frame 
				= (FenetreEditionDiagramme)desktop.getSelectedFrame();
				if (frame == null) return;
				GraphPanel panel = frame.getGraphPanel();
				panel.changeZoom(1);
			}
		});
		viewMenu.add(zoom);
	} 
	private void changeLookAndFeel(String lafName)
	{
		try
		{
			UIManager.setLookAndFeel(lafName);
			SwingUtilities.updateComponentTreeUI(FenetreEditeur.this);
		}
		catch (ClassNotFoundException ex) {}
		catch (InstantiationException ex) {}
		catch (IllegalAccessException ex) {}
		catch (UnsupportedLookAndFeelException ex) {}
	}

	public void readArgs(String[] args)
	{  
		this.setTitle("Sekencer");
	}

	private void open(String name)
	{
		JInternalFrame[] frames = desktop.getAllFrames();
		for (int i = 0; i < frames.length; i++)
		{
			if (frames[i] instanceof FenetreEditionDiagramme)
			{
				FenetreEditionDiagramme frame = (FenetreEditionDiagramme)frames[i];
				if (frame.getFileName().equals(name)) 
				{
					try
					{
						frame.toFront();
						frame.setSelected(true); 
					}
					catch (PropertyVetoException exception)
					{
					}
					return;
				}
			}
		}      

		try
		{              
			Graph graph = read(new FileInputStream(name));
			FenetreEditionDiagramme frame = new FenetreEditionDiagramme(graph);
			addInternalFrame(frame);
			frame.setFileName(name);              
		}
		catch (IOException exception)
		{
			JOptionPane.showInternalMessageDialog(desktop, 
					exception);
		}      
	}   

	private void addInternalFrame(final JInternalFrame iframe)
	{  
		iframe.setResizable(true);
		iframe.setClosable(true);
		iframe.setMaximizable(true);
		iframe.setIconifiable(true);
		int frameCount = desktop.getAllFrames().length;      
		desktop.add(iframe);
		// position frame
		int emptySpace 
		= FRAME_GAP * Math.max(ESTIMATED_FRAMES, frameCount);
		int width = Math.max(desktop.getWidth() / 2, 
				desktop.getWidth() - emptySpace);            
		int height = Math.max(desktop.getHeight() / 2, 
				desktop.getHeight() - emptySpace);

		iframe.reshape(frameCount * FRAME_GAP, 
				frameCount * FRAME_GAP, width, height);
		iframe.show();
		// select the frame--might be vetoed
		try
		{  
			iframe.setSelected(true);
		}
		catch(PropertyVetoException e)
		{
		}
	}

	public void openFile()
	{  
		try
		{
			FileService.Open open = fileService.open(null, null, filtreSequenssos);
			InputStream in = open.getInputStream();
			if (in != null)
			{      
				Graph graph = read(in);
				FenetreEditionDiagramme frame = new FenetreEditionDiagramme(graph);
				addInternalFrame(frame);
				frame.setFileName(open.getName());
			}               
		}
		catch (IOException exception)      {
			JOptionPane.showInternalMessageDialog(desktop, 
					exception);
		}
	}

	public void save()
	{
		FenetreEditionDiagramme frame 
		= (FenetreEditionDiagramme) desktop.getSelectedFrame();
		if (frame == null) return;
		String fileName = frame.getFileName(); 
		if (fileName == null) { saveAs(); return; }
		try
		{
			saveFile(frame.getGraph(), new FileOutputStream(fileName));
			frame.getGraphPanel().setModified(false);
		}        
		catch (Exception exception)
		{
			JOptionPane.showInternalMessageDialog(desktop, 
					exception);
		}        
	}

	public void saveAs()
	{
		FenetreEditionDiagramme frame = (FenetreEditionDiagramme)desktop.getSelectedFrame();
		if (frame == null) return;
		Graph graph = frame.getGraph();    
		try
		{
			FileService.Save save = fileService.save(null, frame.getFileName(), filtreSequenssos, null, defaultExtension);
			OutputStream out = save.getOutputStream();
			if (out != null)
			{
				try
				{
					saveFile(graph, out);
				}
				finally
				{
					out.close();
				}
				frame.setFileName(save.getName());
				frame.getGraphPanel().setModified(false);
			}
		}
		catch (IOException exception)
		{
			JOptionPane.showInternalMessageDialog(desktop, 
					exception);
		}
	}

	public static Graph read(InputStream in)
	throws IOException
	{
		XMLDecoder reader 
		= new XMLDecoder(in);
		Graph graph = (Graph) reader.readObject();
		in.close();
		return graph;
	}

	private static void saveFile(Graph graph, OutputStream out)
	{
		XMLEncoder encoder = new XMLEncoder(out);

		encoder.setExceptionListener(new 
				ExceptionListener() 
		{
			public void exceptionThrown(Exception ex) 
			{
				ex.printStackTrace();
			}
		});

		encoder.setPersistenceDelegate(Point2D.Double.class, new
				DefaultPersistenceDelegate()
		{
			protected void initialize(Class type, 
					Object oldInstance, Object newInstance, 
					Encoder out) 
			{
				super.initialize(type, oldInstance, 
						newInstance, out);
				Point2D p = (Point2D)oldInstance;
				out.writeStatement(
						new Statement(oldInstance,
								"setLocation", new Object[]{ new Double(p.getX()), new Double(p.getY()) }) );
			}
		});

		encoder.setPersistenceDelegate(BentStyle.class,
				staticFieldDelegate);
		encoder.setPersistenceDelegate(LineStyle.class,
				staticFieldDelegate);
		encoder.setPersistenceDelegate(ArrowHead.class,
				staticFieldDelegate);

		Graph.setPersistenceDelegate(encoder);
		NoeudAbstrait.setPersistenceDelegate(encoder);

		encoder.writeObject(graph);
		encoder.close();
	}

	public void exit()
	{
		int modcount = 0;
		JInternalFrame[] frames = desktop.getAllFrames();
		for (int i = 0; i < frames.length; i++)
		{
			if (frames[i] instanceof FenetreEditionDiagramme)
			{
				FenetreEditionDiagramme frame = (FenetreEditionDiagramme)frames[i];
				if (frame.getGraphPanel().isModified()) modcount++;
			}
		}
		if (modcount > 0)
		{
			int result
			= JOptionPane.showInternalConfirmDialog(
					desktop, 
					"Attention " + modcount +" diagrammes non sauvés, voulez-vous quitter sans sauvegarder?",
					null, 
					JOptionPane.YES_NO_OPTION);

			if (result != JOptionPane.YES_OPTION)
				return;
		}
		System.exit(0);
	}

	private JDesktopPane desktop;
	private FileService fileService;
	private String defaultExtension;

	private FiltreExtension filtreSequenssos;
	
	private static final int FRAME_GAP = 20;
	private static final int ESTIMATED_FRAMES = 5;
	private static final double GROW_SCALE_FACTOR = Math.sqrt(2);

	private static PersistenceDelegate staticFieldDelegate 
	= new 
	DefaultPersistenceDelegate()
	{
		protected Expression instantiate(Object 
				oldInstance, Encoder out)
		{
			try
			{
				Class cl = oldInstance.getClass();
				Field[] fields = cl.getFields();
				for (int i = 0; i < fields.length; i++)
				{
					if (Modifier.isStatic(
							fields[i].getModifiers()) &&
							fields[i].get(null) == oldInstance)
					{
						return new Expression(fields[i], 
								"get",
								new Object[] { null });
					}
				}
			}
			catch (IllegalAccessException ex) 
			{
				ex.printStackTrace();
			}
			return null;
		}

		protected boolean mutatesTo(
				Object oldInstance, Object newInstance)
		{
			return oldInstance == newInstance;
		}
	};


}
