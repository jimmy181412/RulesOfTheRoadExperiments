package experiment_files;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import core_car_sim.Point;
import core_car_sim.*;
import simulated_cars.*;
import simulated_cars.AbstractROTRCar.CarAction;
import simulated_cars.AbstractROTRCar.CarPriority;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
public class RunExperiment{
	public class Simulate implements Runnable{
		int delay;
		int until;
		public boolean finished = false;
		
		public Simulate(int delayTime, int noOfSteps){
			delay = delayTime;
			until = noOfSteps;
		}
		
		@Override
		public void run(){
			int i = 0;
			
			while (!finished){
				simworld.simulate(1);
				try{
					Thread.sleep(delay);
				} 
				catch (InterruptedException e){
					e.printStackTrace();
				}
				updateGUIWorld();
				if (!finished){
					finished = until == 0 ? simworld.allFinished() : until == ++i;
				}
			}
		}
		
	}
	private JFrame frame;
	private JMenuBar menuBar = new JMenuBar();
	private WorldSim simworld;
	
	private JPanel panel = new JPanel();
	private JPanel pnlWorld = new JPanel();
	private JPanel logs = new JPanel();
	
	private JComboBox<String> cbAI = new JComboBox<>();
	
	private JLabel recommendations;
	private JLabel actionsPerformed;
    private JLabel beliefs;
    private JLabel intentions;
	private Executor simulationThread = Executors.newSingleThreadExecutor();
	private CarAddedListener cal;
	private PedestrianAddedListener pal;
	private Simulate currentlyRunning = null;
	
	private boolean raVisible= false;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args){
		EventQueue.invokeLater(() -> {
            try {
                //static
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
                RunExperiment window = new RunExperiment();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
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
			public AbstractCar createCar(String name, Point startingLoca, Point endingLoca, Point referenceLoca,Direction initialDirection){
				if(name.equalsIgnoreCase("slow")){
					return new SmallAICar(startingLoca, endingLoca, referenceLoca, initialDirection,1, Direction.west);
				}
				else{
					if (cbAI.getSelectedItem() == "Reactive"){
						return new ReactiveCar(startingLoca, endingLoca, referenceLoca,initialDirection, 1);
					}
					else if(cbAI.getSelectedItem() == "Must Only"){
						return new RudeCar(startingLoca, endingLoca, referenceLoca, initialDirection, 1);
					}
                    else{
                        return new CleverCar(startingLoca, endingLoca, referenceLoca,initialDirection,1);
                    }
				}
			}
	
			@Override
			public AbstractCar createCar(String name, Point startingLoca, Point endingLoca, Point referenceLoca, Direction initialDirection, String av){
				if(name.equalsIgnoreCase("slow")){
					return new SmallAICar(startingLoca, endingLoca, referenceLoca,initialDirection,1, Direction.north);
				}
				else{
					if (cbAI.getSelectedItem() == "Reactive"){
						return new ReactiveCar(startingLoca, endingLoca, referenceLoca, initialDirection,1);
					}
                    else if(cbAI.getSelectedItem() == "Must Only"){
                        return new RudeCar(startingLoca, endingLoca, referenceLoca, initialDirection,1);
                    }
                    else{
                        return new CleverCar(startingLoca, endingLoca, referenceLoca, initialDirection,1);
                    }
				}
			}
		};
		
		pal = (name, startingLoca, endingLoca, referenceLoca, d) -> {
            if(d == Direction.east) {
                return new Pedestrian(startingLoca, endingLoca, referenceLoca,d,System.getProperty("user.dir") + "/RoTRExperiments/resources/imagesIcon/Pedestrian3.png");
            }
            else if( d == Direction.west) {
                return new Pedestrian(startingLoca, endingLoca, referenceLoca,d,System.getProperty("user.dir") + "/RoTRExperiments/resources/imagesIcon/Pedestrian4.png");
            }
            else if( d == Direction.north) {
                return new Pedestrian(startingLoca, endingLoca, referenceLoca,d,System.getProperty("user.dir") + "/RoTRExperiments/resources/imagesIcon/Pedestrian1.png");
            }
            else if( d == Direction.south) {
                return new Pedestrian(startingLoca, endingLoca, referenceLoca,d,System.getProperty("user.dir") + "/RoTRExperiments/resources/imagesIcon/Pedestrian2.png");
            }
            return null;

        };
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(){
	    // initialize the frame
		frame = new JFrame();
		frame.setBounds(100, 100, 1000, 1000);
		frame.setTitle("Third Year Project");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		
		JMenu scenarios = new JMenu("Scenarios");
		JMenu preferences = new JMenu("Windows");
		JMenu edit = new JMenu("Edit");
		JMenu run = new JMenu("Run");
		menuBar.add(scenarios);
		menuBar.add(edit);
		menuBar.add(preferences);
		menuBar.add(run);

		JMenuItem scenario1 = new JMenuItem("Scenario 1");
		scenario1.addActionListener(e -> {
            try {
                BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/RoTRExperiments/src/simulated_cars/example1.txt"));
                simworld = LoadWorld.loadWorldFromFile(br, cal, pal);
                pnlWorld.setLayout(new GridLayout(simworld.getHeight(), simworld.getWidth(), 1, 1));
                updateGUIWorld();
            }
            catch (IOException e1){
                e1.printStackTrace();
            }
        });
		
		JMenuItem scenario2 = new JMenuItem("Scenario 2");
		scenario2.addActionListener(e -> {
            try{
                BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/RoTRExperiments/src/simulated_cars/example2.txt"));
                simworld = LoadWorld.loadWorldFromFile(br, cal, pal);
                pnlWorld.setLayout(new GridLayout(simworld.getHeight(), simworld.getWidth(), 1, 1));
                updateGUIWorld();

            }
            catch (IOException e1){
                e1.printStackTrace();
            }
        });
		
		JMenuItem scenario3 = new JMenuItem("Scenario 3");
		scenario3.addActionListener(e -> {
            try{
                BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/RoTRExperiments/src/simulated_cars/example3.txt"));
                simworld = LoadWorld.loadWorldFromFile(br, cal,pal);
                pnlWorld.setLayout(new GridLayout(simworld.getHeight(), simworld.getWidth(), 1, 1));
                updateGUIWorld();
            }
            catch(IOException e1){
                e1.printStackTrace();
            }
        });
		
		JMenuItem showVisibleWorld = new JMenuItem("Show visible world");
		showVisibleWorld.addActionListener(e -> {
            int width = simworld.getWidth();
            int height = simworld.getHeight();
            int visibility = simworld.getVisibility();

            for(AbstractCar car: simworld.getCars()) {
                if(car.getClass() == ReactiveCar.class || car.getClass() == RudeCar.class || car.getClass() == CleverCar.class) {
                    Point carPosition = simworld.getCarPosition(car);
                    //get the car visible world
                    ArrayList<Point> visibleCells = new ArrayList<>();
                    for(int i = carPosition.getX() - visibility; i <= carPosition.getX() + visibility;i++) {
                        for(int j = carPosition.getY() - visibility; j <= carPosition.getY() + visibility; j++) {
                            // for the cell out of boundary
                            if( (i < 0) || (i >= width) || (j < 0) || ( j >= height)) {
                               //do nothing
                            }
                            else {
                               visibleCells.add(new Point(i,j));
                            }
                        }
                    }

                    for(int m = 0; m < width; m++) {
                        for(int n = 0; n < height;n++) {
                            if(visibleCells.contains(new Point(m,n))) {
                                simworld.getCell(m, n).setVisible(true);
                            }
                            else {
                               if(simworld.getCell(m, n).getClass() == NonDrivingCell.class) {
                                   NonDrivingCell ndc =  (NonDrivingCell)simworld.getCell(m, n);
                                   ndc.setTransparency((float) 0.5);
                               }
                               else if(simworld.getCell(m, n).getClass() == RoadCell.class) {
                                   RoadCell rc = (RoadCell)simworld.getCell(m, n);
                                   rc.setTransparency((float) 0.5);
                               }
                               else if(simworld.getCell(m, n).getClass() == TrafficLightCell.class) {
                                   TrafficLightCell tlc = (TrafficLightCell)simworld.getCell(m, n);
                                   tlc.setTransparency((float)0.5);
                               }
                               else if(simworld.getCell(m, n).getClass() == PavementCell.class) {
                                   PavementCell pc = (PavementCell)simworld.getCell(m,n);
                                   pc.setTransparency((float)0.5);
                               }
                            }
                        }
                    }
              }
          }
            pnlWorld.repaint();
      });
		

		
		JMenuItem showRecommendationsAndActionPerformed = new JMenuItem("Show recommendations and action performed");
		showRecommendationsAndActionPerformed.addActionListener(e -> {
            raVisible = !raVisible;
            logs.setVisible(raVisible);
        });
		
		JMenuItem removeAll = new JMenuItem("remove all");
		removeAll.addActionListener(e -> {
           pnlWorld.removeAll();
           pnlWorld.repaint();
        });
		
	
		JMenuItem runSimulation1 = new JMenuItem("run 1 step");
		runSimulation1.addActionListener(e -> {
            if (currentlyRunning == null){
                currentlyRunning = new Simulate(50, 1);
                simulationThread.execute(currentlyRunning);
            }
            else{
                currentlyRunning.finished = true;
                currentlyRunning = null;
            }
        });
		JMenuItem runSimulation2 = new JMenuItem("run 3 steps");
	    runSimulation2.addActionListener(e -> {
            if (currentlyRunning == null){
                currentlyRunning = new Simulate(50, 3);
                simulationThread.execute(currentlyRunning);
            }
            else{
                currentlyRunning.finished = true;
                currentlyRunning = null;
            }
        });
		JMenuItem runSimulation3 = new JMenuItem("run 5 steps");
	    runSimulation3.addActionListener(e -> {
            if (currentlyRunning == null){
                currentlyRunning = new Simulate(50, 5);
                simulationThread.execute(currentlyRunning);
            }
            else{
                currentlyRunning.finished = true;
                currentlyRunning = null;
            }
        });
		JMenuItem runSimulation4 = new JMenuItem("run util finished");
		runSimulation4.addActionListener(e -> {
            if (currentlyRunning == null){
                currentlyRunning = new Simulate(50, 0);
                simulationThread.execute(currentlyRunning);
            }
            else{
                currentlyRunning.finished = true;
                currentlyRunning = null;
            }
        });
		
		scenarios.add(scenario1);
		scenarios.add(scenario2);
		scenarios.add(scenario3);
		
		preferences.add(showVisibleWorld);
		preferences.add(showRecommendationsAndActionPerformed);
		
		edit.add(removeAll);
		
		run.add(runSimulation1);
		run.add(runSimulation2);
		run.add(runSimulation3);
        run.add(runSimulation4);
	
		//  initialize the control panel and add the panel to the frame
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setBackground(Color.darkGray.darker());
		
		// initialize the scenario panel and add the panel to the frame
		frame.getContentPane().add(pnlWorld, BorderLayout.CENTER);
        pnlWorld.setLayout(new GridLayout(3, 3, 0, 0));
        pnlWorld.setBackground(new Color(66,66,66).darker().darker());
        
        // initialize the log panel and add the panel to the frame
        frame.getContentPane().add(logs, BorderLayout.EAST);
        logs.setLayout(new FlowLayout());
        logs.setPreferredSize(new Dimension(350, 1000));
        logs.setBackground(Color.darkGray);
        // -----------------------------components in control panel--------------------------------------------
        JLabel label1 = new JLabel("Scenario:");
        JLabel label2 = new JLabel("Vehicle:");
        JLabel label3 = new JLabel("numOfSteps:");
                 
       
		cbAI.setModel(new DefaultComboBoxModel<>(new String[]{"Reactive", "Must Only", "AI"}));
		cbAI.setSelectedIndex(0);
		
		JComboBox<String> cbScenarios = new JComboBox<>();
		cbScenarios.addActionListener(e -> {
            try {
                if(cbScenarios.getSelectedItem() == "Scenario 1") {
                    BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/RoTRExperiments/src/simulated_cars/example1.txt"));
                    simworld = LoadWorld.loadWorldFromFile(br, cal, pal);
                    pnlWorld.setLayout(new GridLayout(simworld.getHeight(), simworld.getWidth(), 0, 0));
                    updateGUIWorld();
                }
                else if(cbScenarios.getSelectedItem() == "Scenario 2") {
                    BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/RoTRExperiments/src/simulated_cars/example2.txt"));
                    simworld = LoadWorld.loadWorldFromFile(br, cal, pal);
                    pnlWorld.setLayout(new GridLayout(simworld.getHeight(), simworld.getWidth(), 0, 0));
                    updateGUIWorld();
                }
                else if(cbScenarios.getSelectedItem() == "Scenario 3") {
                    BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/RoTRExperiments/src/simulated_cars/example3.txt"));
                    simworld = LoadWorld.loadWorldFromFile(br, cal, pal);
                    pnlWorld.setLayout(new GridLayout(simworld.getHeight(), simworld.getWidth(), 0, 0));
                    updateGUIWorld();
                }
            }
            catch (IOException e1){
                e1.printStackTrace();
            }
        });
		cbScenarios.setModel(new DefaultComboBoxModel<>(new String[]{"select scenario", "Scenario 1", "Scenario 2", "Scenario 3"}));
		cbScenarios.setSelectedIndex(0);
		
		
		JComboBox<String> cbStepsSimulate = new JComboBox<>();
		cbStepsSimulate.setModel(new DefaultComboBoxModel<>(new String[]{"select steps", "1 step", "3 steps", "5 steps", "until finished"}));
		cbStepsSimulate.setSelectedIndex(0);
		
		JButton btnNewButton = new JButton("Run");
        btnNewButton.addActionListener(e -> {
            if (currentlyRunning == null){
                if (cbStepsSimulate.getSelectedItem() == "1 step"){
                    currentlyRunning = new Simulate(50, 1);
                }
                else if(cbStepsSimulate.getSelectedItem() == "3 steps") {
                    currentlyRunning = new Simulate(50, 3);
                }
                else if(cbStepsSimulate.getSelectedItem() == "5 steps"){
                    currentlyRunning = new Simulate(50, 5);
                }
                else if(cbStepsSimulate.getSelectedItem() == "until finished") {
                    currentlyRunning = new Simulate(50, 0);
                }

                simulationThread.execute(currentlyRunning);
            }
            else{
                currentlyRunning.finished = true;
                currentlyRunning = null;
            }
        });
	
	    // add components to control panel
		panel.add(label1);
		panel.add(cbScenarios);
		panel.add(label2);
        panel.add(cbAI);
        panel.add(label3);
        panel.add(cbStepsSimulate);
      
        panel.add(btnNewButton);
       
		// ----------------------------------------------------------------------------------------------------
		
		// ---------------components in log panel--------------------------------------------------------------
        JLabel NewLabel1  = new JLabel("Recommendations from RoTRA");
        NewLabel1.setPreferredSize(new Dimension(280,30));
        NewLabel1.setFont(new Font("Cascadia Mono", Font.BOLD, 16));
        NewLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        //the recommendations generated by rotra
        recommendations = new JLabel(" ");
        recommendations.setPreferredSize(new Dimension(280, 200));
        recommendations.setHorizontalAlignment(SwingConstants.CENTER);
        recommendations.setVerticalAlignment(SwingConstants.TOP);
        recommendations.setFont(new Font("Cascadia Mono", Font.PLAIN, 16));
        recommendations.setForeground(Color.white);
        JLabel NewLabel2 = new JLabel("Action performed by the car");
        NewLabel2.setPreferredSize(new Dimension(280, 30));
        NewLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        NewLabel2.setFont(new Font("Cascadia Mono", Font.BOLD, 16));
        //the actions that the self-driving car performed
        actionsPerformed = new JLabel(" ");
        actionsPerformed.setPreferredSize(new Dimension(280, 200));
        actionsPerformed.setHorizontalAlignment(SwingConstants.CENTER);
        actionsPerformed.setVerticalAlignment(SwingConstants.TOP);
        actionsPerformed.setFont(new Font("Cascadia Mono", Font.PLAIN, 16));
        actionsPerformed.setForeground(Color.white);
        JLabel NewLabel3 = new JLabel("Beliefs");
        NewLabel3.setPreferredSize(new Dimension(280,30));
        NewLabel3.setFont(new Font("Cascadia Mono", Font.BOLD, 16));
        NewLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        beliefs = new JLabel("...");
        beliefs.setPreferredSize(new Dimension(280, 200));
        beliefs.setHorizontalAlignment(SwingConstants.CENTER);
        beliefs.setVerticalAlignment(SwingConstants.TOP);
        beliefs.setFont(new Font("Cascadia Mono", Font.PLAIN, 16));
        beliefs.setForeground(Color.white);
        JLabel NewLabel4 = new JLabel("Intentions");
        NewLabel4.setPreferredSize(new Dimension(250, 30));
        NewLabel4.setHorizontalAlignment(SwingConstants.CENTER);
        NewLabel4.setFont(new Font("Cascadia Mono", Font.BOLD, 16));
        intentions = new JLabel("");
        intentions.setPreferredSize(new Dimension(250, 200));
        intentions.setHorizontalAlignment(SwingConstants.CENTER);
        intentions.setVerticalAlignment(SwingConstants.TOP);
        intentions.setFont(new Font("Cascadia Mono", Font.PLAIN, 12));
        intentions.setForeground(Color.white);
        // add components to logs panel
        logs.add(NewLabel1);
        logs.add(recommendations);
        logs.add(NewLabel2);
        logs.add(actionsPerformed);
        logs.add(NewLabel3);
        logs.add(beliefs);
        logs.add(NewLabel4);
        logs.add(intentions);
        logs.setVisible(false);
        // ----------------------------------------------------------------------------------------------------

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
        
        //update pedestrians
        for(Pedestrian p : simworld.getPedestrian()) {
            Point point = simworld.getPedestrianPosition(p);
            ImageIcon iicon2 = p.getPedestrianIcon();
            Image img2 = iicon2.getImage();
            Image newImg2 = img2.getScaledInstance(iconWidth,iconHeight, Image.SCALE_SMOOTH);
            iicon2 = new ImageIcon(newImg2);
            JLabel icon2 = new JLabel(iicon2);
            simworld.getCell(point.getX(), point.getY()).add(icon2);
            }
        
        //update cars
        for (AbstractCar car : simworld.getCars()){
            Point p = simworld.getCarPosition(car);

            //check world cell's direction of the current
            if(simworld.getCell(p.getX(), p.getY()).getClass() == RoadCell.class){
                RoadCell rc = (RoadCell)simworld.getCell(p.getX(), p.getY());
                if(rc.getTravelDirection().size() == 1){
                    Direction d = rc.getTravelDirection().get(0);
                    if(d == Direction.north){
                        car.setCurrentIcon(car.getNorthCarIcon());
                    }
                    else if(d == Direction.south){
                        car.setCurrentIcon(car.getSouthCarIcon());
                    }
                    else if(d == Direction.east){
                        car.setCurrentIcon(car.getEastCarIcon());
                    }
                    else if(d == Direction.west){
                        car.setCurrentIcon(car.getWestCarIcon());
                    }
                }
                //if the car is on a multiple directions cell
                else{
                    Direction d = car.getCMD();
                    if(d == Direction.north){
                        car.setCurrentIcon(car.getNorthCarIcon());
                    }
                    else if(d == Direction.south){
                        car.setCurrentIcon(car.getSouthCarIcon());
                    }
                    else if(d == Direction.east){
                        car.setCurrentIcon(car.getEastCarIcon());
                    }
                    else if(d == Direction.west){
                        car.setCurrentIcon(car.getWestCarIcon());
                    }
                }
            }

            ImageIcon iicon1 = car.getCarIcon();
            Image img1 = iicon1.getImage();
            //adjust size
            Image newImg1 = img1.getScaledInstance(iconWidth,iconHeight, Image.SCALE_SMOOTH);
            iicon1 = new ImageIcon(newImg1);
            JLabel icon1 = new JLabel(iicon1);
            simworld.getCell(p.getX(), p.getY()).add(icon1);
            
            if(car.getClass() == ReactiveCar.class) {
              
                ReactiveCar rCar = (ReactiveCar) car;
                HashMap<CarAction, CarPriority> actionsDone = rCar.getActionsPerformed();
                HashMap<CarAction, CarPriority> actionsRecommended = rCar.getRecommendedActions();
                HashMap<AbstractROTRCar.CarBelief, Boolean> beliefsList = rCar.getBeliefsList();
                HashMap<AbstractROTRCar.CarIntention, Boolean> intentionsList = rCar.getIntentionsList();

                
                StringBuilder sb1 = new StringBuilder();
                int counter1 = 1;
                for(Entry<CarAction, CarPriority> entry1 : actionsDone.entrySet()) {
                    String tmp_action = entry1.getKey().toString();
                   
                    sb1.append("[");
                    sb1.append(counter1);
                    sb1.append("]: ");
                    sb1.append(tmp_action);
                    if(counter1 != actionsDone.size()) {
                        sb1.append("\n");
                    }
                    counter1++;
                }
                String sActionsDone = sb1.toString();
                sActionsDone = sActionsDone.replace("\n", "<br>");

                StringBuilder sb2 = new StringBuilder();
                int counter2 = 1; 
                for(Entry<CarAction, CarPriority> entry2: actionsRecommended.entrySet()) {
                    String tmp_action = entry2.getKey().toString();
                    sb2.append("[");
                    sb2.append(counter2);
                    sb2.append("]: ");
                    sb2.append(tmp_action);
                    if(counter2 != actionsRecommended.size()) {
                        sb2.append("\n");
                    }
                    counter2++;
                }
                String sActionsRecommended = sb2.toString();
                sActionsRecommended = sActionsRecommended.replace("\n", "<br>");
                actionsPerformed.setText("<html>" + sActionsDone + "</html>");
                recommendations.setText("<html>" + sActionsRecommended + "</html>");


                StringBuilder sb3 = new StringBuilder();
                int counter3 = 1;
                for(Entry<AbstractROTRCar.CarBelief, Boolean> entry3 : beliefsList.entrySet()){
                    if(entry3.getValue()){
                        String tmp_belief = entry3.getKey().toString();
                        sb3.append("[");
                        sb3.append(counter3);
                        sb3.append("]: ");
                        sb3.append(tmp_belief);
                        if(counter3 != beliefsList.size()){
                            sb3.append("\n");
                        }
                        counter3++;
                    }
                }

                String sbeliefs = sb3.toString();
                sbeliefs = sbeliefs.replace("\n", "<br>");
                beliefs.setText("<html>" + sbeliefs + "</html>");
                StringBuilder sb4 = new StringBuilder();
                int counter4 = 1;
                for(Entry<AbstractROTRCar.CarIntention, Boolean> entry4 : intentionsList.entrySet()){
                    if(entry4.getValue()){
                        String tmp_intention = entry4.getKey().toString();
                        sb4.append("[");
                        sb4.append(counter4);
                        sb4.append("]: ");
                        sb4.append(tmp_intention);
                        if(counter4 != intentionsList.size()){
                            sb4.append("\n");
                        }
                        counter4++;
                    }
                }

                String sintentions = sb4.toString();
                sintentions.replace("\n", "<br>");
                intentions.setText("<html>" + sintentions + "</html>");
                rCar.clearBeliefs();
                rCar.clearIntentions();
                rCar.resetActions();
                rCar.resetRecommendations();
            }
            else if(car.getClass() == RudeCar.class) {
                RudeCar rCar = (RudeCar) car;
                HashMap<CarAction, CarPriority> actionsDone = rCar.getActionsPerformed();
                HashMap<CarAction, CarPriority> actionsRecommended = rCar.getRecommendedActions();
                HashMap<AbstractROTRCar.CarBelief, Boolean> beliefsList = rCar.getBeliefsList();
                HashMap<AbstractROTRCar.CarIntention, Boolean> intentionsList = rCar.getIntentionsList();

                StringBuilder sb1 = new StringBuilder();
                int counter1 = 1;
                for (Entry<CarAction, CarPriority> entry1 : actionsDone.entrySet()) {
                    String tmp_action = entry1.getKey().toString();

                    sb1.append("[");
                    sb1.append(counter1);
                    sb1.append("]: ");
                    sb1.append(tmp_action);
                    if (counter1 != actionsDone.size()) {
                        sb1.append("\n");
                    }
                    counter1++;
                }
                String sActionsDone = sb1.toString();
                sActionsDone = sActionsDone.replace("\n", "<br>");

                StringBuilder sb2 = new StringBuilder();
                int counter2 = 1;
                for (Entry<CarAction, CarPriority> entry2 : actionsRecommended.entrySet()) {
                    String tmp_action = entry2.getKey().toString();
                    sb2.append("[");
                    sb2.append(counter2);
                    sb2.append("]: ");
                    sb2.append(tmp_action);
                    if (counter2 != actionsRecommended.size()) {
                        sb2.append("\n");
                    }
                    counter2++;
                }
                String sActionsRecommended = sb2.toString();
                sActionsRecommended = sActionsRecommended.replace("\n", "<br>");
                actionsPerformed.setText("<html>" + sActionsDone + "</html>");
                recommendations.setText("<html>" + sActionsRecommended + "</html>");


                StringBuilder sb3 = new StringBuilder();
                int counter3 = 1;
                for (Entry<AbstractROTRCar.CarBelief, Boolean> entry3 : beliefsList.entrySet()) {
                    if (entry3.getValue()) {
                        String tmp_belief = entry3.getKey().toString();
                        sb3.append("[");
                        sb3.append(counter3);
                        sb3.append("]: ");
                        sb3.append(tmp_belief);
                        if (counter3 != beliefsList.size()) {
                            sb3.append("\n");
                        }
                        counter3++;
                    }
                }

                String sbeliefs = sb3.toString();
                sbeliefs = sbeliefs.replace("\n", "<br>");
                beliefs.setText("<html>" + sbeliefs + "</html>");
                StringBuilder sb4 = new StringBuilder();
                int counter4 = 1;
                for (Entry<AbstractROTRCar.CarIntention, Boolean> entry4 : intentionsList.entrySet()) {
                    if (entry4.getValue()) {
                        String tmp_intention = entry4.getKey().toString();
                        sb4.append("[");
                        sb4.append(counter4);
                        sb4.append("]: ");
                        sb4.append(tmp_intention);
                        if (counter4 != intentionsList.size()) {
                            sb4.append("\n");
                        }
                        counter4++;
                    }
                }

                String sintentions = sb4.toString();
                sintentions.replace("\n", "<br>");
                intentions.setText("<html>" + sintentions + "</html>");
                rCar.clearBeliefs();
                rCar.clearIntentions();
                rCar.resetActions();
                rCar.resetRecommendations();
            }
            else if(car.getClass() == CleverCar.class) {

                CleverCar rCar = (CleverCar)car;
                HashMap<CarAction, CarPriority> actionsDone = rCar.getActionsPerformed();
                HashMap<CarAction, CarPriority> actionsRecommended = rCar.getRecommendedActions();
                HashMap<AbstractROTRCar.CarBelief, Boolean> beliefsList = rCar.getBeliefsList();
                HashMap<AbstractROTRCar.CarIntention, Boolean> intentionsList = rCar.getIntentionsList();


                StringBuilder sb1 = new StringBuilder();
                int counter1 = 1;
                for(Entry<CarAction, CarPriority> entry1 : actionsDone.entrySet()) {
                    String tmp_action = entry1.getKey().toString();

                    sb1.append("[");
                    sb1.append(counter1);
                    sb1.append("]: ");
                    sb1.append(tmp_action);
                    if(counter1 != actionsDone.size()) {
                        sb1.append("\n");
                    }
                    counter1++;
                }
                String sActionsDone = sb1.toString();
                sActionsDone = sActionsDone.replace("\n", "<br>");

                StringBuilder sb2 = new StringBuilder();
                int counter2 = 1;
                for(Entry<CarAction, CarPriority> entry2: actionsRecommended.entrySet()) {
                    String tmp_action = entry2.getKey().toString();
                    sb2.append("[");
                    sb2.append(counter2);
                    sb2.append("]: ");
                    sb2.append(tmp_action);
                    if(counter2 != actionsRecommended.size()) {
                        sb2.append("\n");
                    }
                    counter2++;
                }
                String sActionsRecommended = sb2.toString();
                sActionsRecommended = sActionsRecommended.replace("\n", "<br>");
                actionsPerformed.setText("<html>" + sActionsDone + "</html>");
                recommendations.setText("<html>" + sActionsRecommended + "</html>");


                StringBuilder sb3 = new StringBuilder();
                int counter3 = 1;
                for(Entry<AbstractROTRCar.CarBelief, Boolean> entry3 : beliefsList.entrySet()){
                    if(entry3.getValue()){
                        String tmp_belief = entry3.getKey().toString();
                        sb3.append("[");
                        sb3.append(counter3);
                        sb3.append("]: ");
                        sb3.append(tmp_belief);
                        if(counter3 != beliefsList.size()){
                            sb3.append("\n");
                        }
                        counter3++;
                    }
                }

                String sbeliefs = sb3.toString();
                sbeliefs = sbeliefs.replace("\n", "<br>");
                beliefs.setText("<html>" + sbeliefs + "</html>");
                StringBuilder sb4 = new StringBuilder();
                int counter4 = 1;
                for(Entry<AbstractROTRCar.CarIntention, Boolean> entry4 : intentionsList.entrySet()){
                    if(entry4.getValue()){
                        String tmp_intention = entry4.getKey().toString();
                        sb4.append("[");
                        sb4.append(counter4);
                        sb4.append("]: ");
                        sb4.append(tmp_intention);
                        if(counter4 != intentionsList.size()){
                            sb4.append("\n");
                        }
                        counter4++;
                    }
                }

                String sintentions = sb4.toString();
                sintentions.replace("\n", "<br>");
                intentions.setText("<html>" + sintentions + "</html>");
                rCar.clearBeliefs();
                rCar.clearIntentions();
                rCar.resetActions();
                rCar.resetRecommendations();
            }
        }

        pnlWorld.revalidate();
        pnlWorld.repaint();
	}
}
