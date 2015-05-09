import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.imageio.ImageIO;
import java.lang.reflect.*;
import java.net.URL;

public class CMMWRITER extends JFrame implements ComponentListener {
	JTextArea main_text_area;
	JButton save_button, open_button;
	JScrollPane scroll_pane;
	BackgroundPanel background;
	GridBagLayout background_layout;
	
	File opened_file;
	
	public CMMWRITER() {
		super("untitled");
		this.setSize(new Dimension(1000, 600));
		this.setCanWindowFullScreen();
		this.addComponentListener(this);

		main_text_area = new JTextArea(10, 10);
		main_text_area.setTabSize(2);
		main_text_area.setBackground(new Color(221, 226, 228));
		main_text_area.setMargin(new Insets(10, 20, 10, 20));
		main_text_area.setFont(new Font("HiraMinPro-W3", Font.PLAIN, 16));
		
		scroll_pane = new JScrollPane(main_text_area);
		scroll_pane.setPreferredSize(new Dimension(600, 420));
		scroll_pane.setBorder(null);
		
		background = new BackgroundPanel("img/fonds.png");
		background.add(scroll_pane);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 100, 0);
		background_layout = new GridBagLayout();
		background.setLayout(background_layout);
		background_layout.setConstraints(scroll_pane, gbc);
		this.add(background);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem menuSave = new JMenuItem("Save");
		JMenuItem menuOpen = new JMenuItem("Open");
		
		menuSave.setMnemonic(KeyEvent.VK_S);
		menuSave.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				save_file();
			}
		});
		
		menuOpen.setMnemonic(KeyEvent.VK_O);
		menuOpen.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				open_file();
			}
		});
		
		fileMenu.add(menuSave);
		fileMenu.add(menuOpen);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
	}
	
	public void componentResized(ComponentEvent e) {
		scroll_pane.setPreferredSize(new Dimension((int)(getWidth() * 0.6), (int)(getHeight() * 0.7)));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 100, (int)((getWidth() - 1000) / 366.0 * 400));
		background_layout = new GridBagLayout();
		background.setLayout(background_layout);
		background_layout.setConstraints(scroll_pane, gbc);
	}

	public void componentShown(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentHidden(ComponentEvent e) { }
	
	private void setCanWindowFullScreen() {
		try {
			Class<?> util = Class.forName("com.apple.eawt.FullScreenUtilities");
			Class params[] = new Class[]{Window.class, Boolean.TYPE};
			Method method = util.getMethod("setWindowCanFullScreen", params);
			method.invoke(util, this, true);
		} catch(Exception e) { }
	}
	
	private void save_file() {
		if(opened_file == null) {
			FileDialog dialog = new FileDialog(this, "Save", FileDialog.SAVE);
			dialog.setVisible(true);
			opened_file = new File(dialog.getDirectory() + dialog.getFile());
			
			try {
				opened_file.createNewFile();
			} catch(IOException e) {
				System.out.println(e);
			}
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(opened_file));
			writer.write(main_text_area.getText());
			writer.close();
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	private void open_file() {
		try {
			JFileChooser filechooser = new JFileChooser();
			
			if(filechooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				opened_file = filechooser.getSelectedFile();
				
				main_text_area.replaceRange(null, 0, main_text_area.getText().length());
				setTitle(opened_file.getAbsolutePath());
				
				BufferedReader reader = new BufferedReader(new FileReader(opened_file));
				
				for(String line; (line = reader.readLine()) != null; ) {
					main_text_area.append(line + "\n");
				}
				
				reader.close();
			}
		} catch(FileNotFoundException e) {
			System.out.println(e);
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {
		if(System.getProperty("os.name").toLowerCase().startsWith("mac")) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Writer");
    }
    
		CMMWRITER editor_frame = new CMMWRITER();
		editor_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		editor_frame.setVisible(true);
	}
}

class BackgroundPanel extends JPanel
{
	private BufferedImage image;

	public BackgroundPanel(String path) {
		try {
			this.image = ImageIO.read(getClass().getResource(path));
		} catch (IOException ex) {
			ex.printStackTrace();
			this.image = null;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D graphics = (Graphics2D) g;

		double imageWidth = image.getWidth();
		double imageHeight = image.getHeight();
		double panelWidth = this.getWidth();
		double panelHeight = this.getHeight();

		double sx = (panelWidth / imageWidth);
		double sy = (panelHeight / imageHeight);

		AffineTransform af = AffineTransform.getScaleInstance(sx, sy);
		graphics.drawImage(image, af, this);
	}
}
