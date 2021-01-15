package Paint;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;  //sekilleri cizerken tikladigimizda koordinat hesaplamak icin gerekli kutuphane
import java.util.*;
import java.text.DecimalFormat;


@SuppressWarnings("serial")
public class Paint1 extends JFrame {
	JButton fircaButon, cizgiButon, elipsButon, dikdortgenButon, kenarButon, dolguButon;
	JSlider transSlider;
	JLabel transLabel;
	
	DecimalFormat dec = new DecimalFormat("#.##");
	
	Graphics2D grafikAyarlari;
	
	int hareket = 1; //hareketlerimizin kontrolunu saglayacak
	
	float transparantVal = 1.0F;
	
	Color kenarRengi = Color.BLACK, dolguRengi = Color.BLACK;

	public static void main(String[] args) {
		
		new Paint1();

	}
	
	public Paint1() {
		this.setSize(1000,700); //ekran frame'mimizin olculerini verdik
		this.setTitle("Java Paint Uygulamasý");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //carpiya basinca programin kapanmasi icin
		
		JPanel butonPanel = new JPanel(); //butonlar için (yan yana sýrali olmalari icin) ozel alan tanýmladýk
		Box kutu = Box.createHorizontalBox(); //yatay kutular tanimladik
		
		fircaButon = ButonYap("./src/firca.png", 1);
		cizgiButon = ButonYap("./src/cizgi.png", 2);
		elipsButon = ButonYap("./src/elips.png", 3);
		dikdortgenButon = ButonYap("./src/dikdortgen.png", 4);
		
		kenarButon = RenkliButonYap("./src/kenar.png", 5, true);
		dolguButon = RenkliButonYap("./src/dolgu.png", 6, false);
		
		kutu.add(fircaButon);  // butonlari kutuya ekledik
		kutu.add(cizgiButon);
		kutu.add(elipsButon);
		kutu.add(dikdortgenButon);
		kutu.add(kenarButon);
		kutu.add(dolguButon);
		
		transLabel = new JLabel("Transparan: 1");
		transSlider = new JSlider(1, 99, 99);
		ListenForSlider sliderL = new ListenForSlider();
		
		transSlider.addChangeListener(sliderL);
		kutu.add(transLabel);
		kutu.add(transSlider);
		
		butonPanel.add(kutu); //kutuyu panele ekledik
		
		this.add(butonPanel, BorderLayout.SOUTH); // buton panelimizi ekranin altina ekledik
		this.add(new CizimEkrani(), BorderLayout.CENTER);
		this.setVisible(true); // frame'i gorunur yaptik
		
	} //constructor sonu
	
	public JButton ButonYap(String iconDosyasi, final int siraSayisi) {
		
		JButton buton = new JButton();
		Icon butonIcon = new ImageIcon(iconDosyasi);
		buton.setIcon(butonIcon);
		
		buton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				hareket = siraSayisi;
												
			}
			
		});
		return buton;
	}  // ButonYap sonu
	
	public JButton RenkliButonYap(String iconDosyasi, final int siraSayisi, final boolean kenar) {
		
		JButton buton = new JButton();
		Icon butonIcon = new ImageIcon(iconDosyasi);
		buton.setIcon(butonIcon);
		
		buton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(kenar) {
					kenarRengi = JColorChooser.showDialog(null, "Kenar Rengi Seç", Color.BLACK);
					
				}else {
					dolguRengi = JColorChooser.showDialog(null, "Dolgu Rengi Seç", Color.BLACK);
					
				}
												
			}
			
		});
		return buton;
	} //RenkliButonYap sonu
	
	private class CizimEkrani extends JComponent {
		
		ArrayList<Shape> geoSekiller = new ArrayList<Shape>();
		ArrayList<Color> dolguSekli = new ArrayList<Color>();
		ArrayList<Color> kenarSekli = new ArrayList<Color>();
		ArrayList<Float> transYuzdesi = new ArrayList<Float>();
		
		Point ilkCizim, sonCizim;
		
		public CizimEkrani() {
			
			this.addMouseListener(new MouseAdapter() { //mouse ilk aný yakalar
				
				public void mousePressed(MouseEvent e) {
					
					if(hareket != 1) {
						ilkCizim = new Point(e.getX(), e.getY()); //x ve y koordinatlarýný aldýk
						sonCizim = ilkCizim;
						repaint();
					}
					
				}
				public void mouseReleased(MouseEvent e) {
					
					if(hareket != 1) {
						Shape sekil = null;
						
						if(hareket == 2) {
							sekil = cizgiCiz(ilkCizim.x, ilkCizim.y, e.getX(), e.getY());
							
						}else if(hareket == 3) {
							sekil = elipsCiz(ilkCizim.x, ilkCizim.y, e.getX(), e.getY());
							
						}else if(hareket == 4) {
							sekil = dikdortgenCiz(ilkCizim.x, ilkCizim.y, e.getX(), e.getY());
						}
					
					geoSekiller.add(sekil);
					dolguSekli.add(dolguRengi);
					kenarSekli.add(kenarRengi);
					
					transYuzdesi.add(transparantVal);
					
					ilkCizim = null;
					sonCizim = null;
					repaint();
					
				}
				}
			}); //addMouseListener sonu
			
			this.addMouseMotionListener(new MouseMotionAdapter() { //mouse son ani yakalar
				
				public void mouseDragged(MouseEvent e) {
					
					if(hareket ==1) {
					int x = e.getX();
					int y = e.getY();
					
					Shape sekil = null;
					
					kenarRengi = dolguRengi;
					
					sekil = fircaCiz(x, y, 5, 5);
					
					geoSekiller.add(sekil);
					dolguSekli.add(dolguRengi);
					kenarSekli.add(kenarRengi);
					transYuzdesi.add(transparantVal);
					}
					sonCizim = new Point(e.getX(), e.getY());
					repaint();
				}
			}); //addMouseMotionListener sonu
		}
		
		public void paint(Graphics g) {
			Graphics2D grafikAyarlari = (Graphics2D)g;
			grafikAyarlari.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			grafikAyarlari.setStroke(new BasicStroke(4));
			
			Iterator<Color> kenarSay = kenarSekli.iterator();  //birden fazla renk secimini sagladik
			Iterator<Color> dolguSay = dolguSekli.iterator();
			Iterator<Float> transSay = transYuzdesi.iterator();
			
			
			
			for(Shape s: geoSekiller){
				
				grafikAyarlari.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transSay.next()));
				
				grafikAyarlari.setPaint(kenarSay.next());
				grafikAyarlari.draw(s);
				grafikAyarlari.setPaint(dolguSay.next());
				grafikAyarlari.fill(s);
				
			}
			if(ilkCizim != null && sonCizim != null) {
				grafikAyarlari.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.40f));
				grafikAyarlari.setPaint(Color.LIGHT_GRAY);
				
				Shape sekil = null;
				
				if(hareket == 2) {
					sekil = cizgiCiz(ilkCizim.x, ilkCizim.y, sonCizim.x, sonCizim.y);
				}
				else if(hareket == 3) {
					sekil = elipsCiz(ilkCizim.x, ilkCizim.y, sonCizim.x, sonCizim.y);
				}
				else if(hareket == 4){
				
				sekil = dikdortgenCiz(ilkCizim.x, ilkCizim.y, sonCizim.x, sonCizim.y);
				}
				
				grafikAyarlari.draw(sekil);
			}
		}

	
	private Rectangle2D.Float dikdortgenCiz(int x1, int y1, int x2, int y2){
		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		
		int genislik = Math.abs(x1 - x2);
		int yukseklik = Math.abs(y1 - y2);
		
		return new Rectangle2D.Float(x, y, genislik, yukseklik);
	}
	
	private Ellipse2D.Float elipsCiz(int x1, int y1, int x2, int y2){
		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		
		int genislik = Math.abs(x1 - x2);
		int yukseklik = Math.abs(y1 - y2);
		
		return new Ellipse2D.Float(x, y, genislik, yukseklik);
	}
	
	private Line2D.Float cizgiCiz(int x1, int y1, int x2, int y2){
		
		return new Line2D.Float(x1, y1, x2, y2);
		
	}
	
	private Ellipse2D.Float fircaCiz(int x1, int y1, int fircaKenarGenisligi, int fircaKenarYuksekligi){
		
		return new Ellipse2D.Float(x1, y1, fircaKenarGenisligi, fircaKenarYuksekligi);
	}
	
	} //CizimEkrani sonu
	private class ListenForSlider implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent e) {
			
			if(e.getSource() == transSlider) {
				transLabel.setText("Transparan " + dec.format(transSlider.getValue() * .01));
			}
			transparantVal = (float) (transSlider.getValue() * .01);
			
			
		}
		
		
	}
	

}
