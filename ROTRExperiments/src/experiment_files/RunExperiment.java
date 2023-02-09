package experiment_files;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import core_car_sim.AbstractCar;
import core_car_sim.CarAddedListener;
import core_car_sim.Direction;
import core_car_sim.LoadWorld;
import core_car_sim.Pedestrian;
import core_car_sim.PedestrianAddedListener;
import core_car_sim.Point;
import core_car_sim.WorldSim;
import simulated_cars.BasicAICar;
import simulated_cars.ReactiveCar;
import simulated_cars.RudeCar;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;


public class RunExperiment{
	public class Simulate implements Runnable{
		int delay = 0;
		int until = 0;
		int stepsSimulated = 0;
		public boolean finished = false;
		
		public Simulate(int delayTime, int noOfSteps){
			delay = delayTime;
			until = noOfSteps;
		}
		
		@Override
		public void run(){
			int i = 0;
			
			while (!finished){
			    updateGUIWorld();
				simworld.simulate(1);
				try{
					Thread.sleep(delay);
				} 
				catch (InterruptedException e){
					e.printStackTrace();
				}
				
				lblNewLabel.setText("Steps simulated: " + ++stepsSimulated);
				if (!finished){
					finished = until == 0 ? simworld.allFinished() : until == ++i;
				}
			}
		}
		
	};
	
	private JFrame frame;
	private JLabel lblNewLabel;
	private WorldSim simworld;
	private JPanel pnlWorld = new JPanel();
	private JPanel logs = new JPanel(); 
	private JComboBox<String> cbAI = new JComboBox<String>();
	private Executor simulationThread = Executors.newSingleThreadExecutor();
	private CarAddedListener cal;
	private PedestrianAddedListener pal;
	private Simulate currentlyRunning = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			public void run() {
				try {
					RunExperiment window = new RunExperiment();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RunExperiment() {
		initialize();
		cal = new CarAddedListener() {
			@Override
			public AbstractCar createCar(String name, Point startingLoca, Point endingLoca){
				if(name.equalsIgnoreCase("slow")){
					return new BasicAICar(startingLoca, endingLoca, 2, Direction.west);
				}
				else{
					if (cbAI.getSelectedItem() == "Reactive"){
						return new ReactiveCar(startingLoca, endingLoca,1);
					}
					else{
						return new RudeCar(startingLoca, endingLoca, 1);
					}
				}
			}
	
			@Override
			public AbstractCar createCar(String name, Point startingLoca, Point endingLoca, String av){
				if(name.equalsIgnoreCase("slow")){
					return new BasicAICar(startingLoca, endingLoca, 2, Direction.north);
				}
				else{
					if (cbAI.getSelectedItem() == "Reactive"){
						return new ReactiveCar(startingLoca, endingLoca, 1);
					}
					else{
						return new RudeCar(startingLoca, endingLoca, 1);
					}
				}
			}
		};
		
		pal = new PedestrianAddedListener() {
            @Override
            public Pedestrian createPedestrians(String name,Point startingLoca,Point endingLoca,Point referenceLoca, Direction d) {
                // TODO Auto-generated method stub
                if(d == Direction.east) {
                    return new Pedestrian(startingLoca, endingLoca, referenceLoca,d,System.getProperty("user.dir") + "/resources/pedestrian1.png");
                }
                else if( d == Direction.west) {
                    return new Pedestrian(startingLoca, endingLoca, referenceLoca,d,System.getProperty("user.dir") + "/resources/pedestrian2.png");
                }
                else if( d == Direction.north) {
                    return new Pedestrian(startingLoca, endingLoca, referenceLoca,d,System.getProperty("user.dir") + "/resources/pedestrian2.png");
                }
                else if( d == Direction.south) {
                    return new Pedestrian(startingLoca, endingLoca, referenceLoca,d,System.getProperty("user.dir") + "/resources/pedestrian2.png");
                }
                return null;
                
            }
            
        };
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(){
		frame = new JFrame();
		frame.setBounds(100, 100, 966, 615);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		
		
		// button1
		JButton btnNewButton = new JButton("Load example1");
		btnNewButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/simulated_cars/example1.txt"));
                    simworld = LoadWorld.loadWorldFromFile(br, cal, pal);
                    pnlWorld.setLayout(new GridLayout(simworld.getHeight(), simworld.getWidth(), 1, 1));
                    updateGUIWorld();   
                } 
                catch (IOException e1){
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
		panel.add(btnNewButton);
		
		//button2
		JButton btnNewButton_2 = new JButton("Load example2");
		btnNewButton_2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/simulated_cars/example2.txt"));
					simworld = LoadWorld.loadWorldFromFile(br, cal, pal);
					pnlWorld.setLayout(new GridLayout(simworld.getHeight(), simworld.getWidth(), 1, 1));
					updateGUIWorld();
				
				}
				catch (IOException e1){
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel.add(btnNewButton_2);
		
		// button3
		JButton btnNewButton_3 = new JButton("Load example3");
		btnNewButton_3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/simulated_cars/example3.txt"));
					simworld = LoadWorld.loadWorldFromFile(br, cal,pal);
					pnlWorld.setLayout(new GridLayout(simworld.getHeight(), simworld.getWidth(), 1, 1));
					updateGUIWorld();	
				} 
				catch(IOException e1){
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel.add(btnNewButton_3);
		
		cbAI.setModel(new DefaultComboBoxModel<String>(new String[] {"Reactive", "Must Only"}));
		cbAI.setSelectedIndex(0);
		panel.add(cbAI);
		
		JButton btnNewButton_1 = new JButton("Run Simulation");
		panel.add(btnNewButton_1);
		
		ButtonGroup bg = new ButtonGroup();
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("until finished");
		rdbtnNewRadioButton.setSelected(true);
		panel.add(rdbtnNewRadioButton);
		bg.add(rdbtnNewRadioButton);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("set number");
		panel.add(rdbtnNewRadioButton_1);
		bg.add(rdbtnNewRadioButton_1);
		
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
		panel.add(spinner);
		
		lblNewLabel = new JLabel("New label");
		panel.add(lblNewLabel);
	
		frame.getContentPane().add(pnlWorld, BorderLayout.CENTER);
		pnlWorld.setLayout(new GridLayout(3, 3, 0, 0));
		
	
		frame.getContentPane().add(logs, BorderLayout.EAST);
	
		btnNewButton_1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (currentlyRunning == null){
					if (rdbtnNewRadioButton.isSelected()){
						currentlyRunning = new Simulate(250, 0);	
					}
					else{
						currentlyRunning = new Simulate(250, (Integer)spinner.getValue());
					}
					simulationThread.execute(currentlyRunning);
				}
				else{
					currentlyRunning.finished = true;
					currentlyRunning = null;
				}
			}
		});
	}
	
	private void updateGUIWorld(){
	    pnlWorld.removeAll();
        
        // get pnl's height and weight in pxis
        int pnlWidth = pnlWorld.getWidth();
        int pnlHeight = pnlWorld.getHeight();
        
        //get simulated world's height and width
        int simWidth = simworld.getWidth();
        int simHeight = simworld.getHeight();
        
        // height and width of each cell
        int cWidth = pnlWidth / simWidth;
        int cHeight = pnlHeight / simHeight;
        
        //adjust cell width and cell height for car and pedestrian icons
        int iconWidth = (int) (cWidth / 1.5);
        int iconHeight = (int) (cHeight / 1.5);
        for (int y = 0; y < simworld.getHeight(); y++)
        {
            for (int x = 0; x < simworld.getWidth(); x++)
            {
                simworld.getCell(x, y).removeAll();
                pnlWorld.add(simworld.getCell(x, y));
            }
        }
        //update cars
        for (AbstractCar car : simworld.getCars()){
            Point p = simworld.getCarPosition(car);
            ImageIcon iicon1 = car.getCarIcon();
            Image img1 = iicon1.getImage();
            //adjust size
            Image newimg1 = img1.getScaledInstance(iconWidth,iconHeight,java.awt.Image.SCALE_SMOOTH);
            iicon1 = new ImageIcon(newimg1);
            JLabel icon1 = new JLabel(iicon1);
            simworld.getCell(p.getX(), p.getY()).add(icon1);
            
        }
        //update pedestrians
        for(Pedestrian p : simworld.getPedestrian()) {
            Point point = simworld.getPedestrianPosition(p);
            ImageIcon iicon2 = p.getPedestrainIcon();
            Image img2 = iicon2.getImage();
            Image newimg2 = img2.getScaledInstance(iconWidth,iconHeight,java.awt.Image.SCALE_SMOOTH);
            iicon2 = new ImageIcon(newimg2);
            JLabel icon2 = new JLabel(iicon2);
            simworld.getCell(point.getX(), point.getY()).add(icon2);
            }
        
        pnlWorld.revalidate();
        pnlWorld.repaint();
	}
}
