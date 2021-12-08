//Code written by Christian Neij
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MapApplication extends Application{
	private BorderPane bp = new BorderPane();
	private VBox menuBox = new VBox();
	private VBox sceneNodes = new VBox(menuBox,bp);
	private final FileChooser fileChooser = new FileChooser();
	private Stage theStage;
	private List<Place> selectedPlaces = new LinkedList<>();
	private List<Place> placesToHideOrRemove = new ArrayList<>();
	private Map<Category,LinkedList<Place>> hiddenPlaces = new HashMap<>();
	private List<Place> tempList = new LinkedList<>();
	private List<Place> tempList2 = new ArrayList<>();
	private Map<Category,LinkedList<Place>> placesByCat = new HashMap<>();
	//private Map<String,Category> categories = new HashMap<String,Category>();
	private Map<Position,Place> placesByPosition = new HashMap<Position,Place>();
	private Map<String,LinkedList<Place>> placesByName = new HashMap<String,LinkedList<Place>>();
	private Pane mapAndPlaces = new Pane();
	private ListView<Category> rightListPane = new ListView<>();
	private ImageView img = new ImageView();
	private int radioChoice;
	private Category categoryChoice;
	private boolean cursorIsNormal = true;
	private int x,y;
	private boolean saved = true;
	
	public static void main(String args[]){
		launch();
	}
	private void clearAllPlaces() {
		selectedPlaces.clear();
		placesToHideOrRemove.clear();
		hiddenPlaces.clear();
		tempList.clear();
		tempList2.clear();
		placesByCat.clear();
		placesByPosition.clear();
		placesByName.clear();
	}
	private void savePlaces(){
		try{
			FileChooser fileSaver = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Place files (*.places)", "*.places");
			fileSaver.getExtensionFilters().add(extFilter);
			File file = fileSaver.showSaveDialog(null);
			if(file!=null) {
				FileWriter fileWriter = null;
				fileWriter = new FileWriter(file);
				PrintWriter out = new PrintWriter(fileWriter);
				tempList2.addAll(placesByPosition.values());
				for(Place place : tempList2) {
					String temp = place.toString();
					out.println(temp);
				}
				out.close();
				fileWriter.close();
				tempList2.clear();
			}
			saved = true;
		}catch(IOException e) {
		}
	}
	private void openPlaces(){
		FileChooser placeOpener = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Place files (*.places)", "*.places");
		placeOpener.getExtensionFilters().add(extFilter);
		File file = placeOpener.showOpenDialog(null);
		if (file!=null) {
			try {
				loadPlaces(file);
			} catch (FileNotFoundException e) {}
		}
	}
	private void loadPlaces(File file) throws FileNotFoundException {
		mapAndPlaces.getChildren().removeAll(placesByPosition.values());
		clearAllPlaces();
		FileReader placeFile = new FileReader(file);
		BufferedReader buffFile = new BufferedReader(placeFile);
		try {
			String line;
			while((line = buffFile.readLine())!=null) {
				String[] parts = line.split(",");
				if(parts[0].equals("Named")) {
					Place newPlace = new NamedPlace(parts[4],Category.valueOf(parts[1]),Integer.parseInt(parts[2]),Integer.parseInt(parts[3]));
					addPlaceToDefaultCollections(newPlace);
					newPlace.setOnMouseClicked(e -> eventOnMouseClick(newPlace,e));
					mapAndPlaces.getChildren().add(newPlace);
				}else if(parts[0].equals("Described")) {
					Place newPlace = new DescribedPlace(parts[4],Category.valueOf(parts[1]),Integer.parseInt(parts[2]),Integer.parseInt(parts[3]),parts[5]);
					addPlaceToDefaultCollections(newPlace);
					newPlace.setOnMouseClicked(e -> eventOnMouseClick(newPlace,e));
					mapAndPlaces.getChildren().add(newPlace);
				}
			}
			saved = true;
			placeFile.close();
			buffFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void findPlacesWithName(String name) {
		for(Place p : selectedPlaces) {
			p.changeSelectionStatus(false);
		}
		selectedPlaces.clear();
		if(placesByName.get(name).isEmpty()) {
			createNameError();
			return;
		}
		for(Place p : placesByName.get(name)) {
			p.changeSelectionStatus(true);
			selectedPlaces.add(p);
			try {
				if(hiddenPlaces.get(p.getCategory()).contains(p)) {
					p.setVisible(true);
					hiddenPlaces.get(p.getCategory()).remove(p);
				}
			}catch(NullPointerException e) {
				
			}
		}
	}
	class ClickListHandler implements ChangeListener<Category>{
		@Override
		public void changed(ObservableValue<? extends Category> arg0, Category arg1, Category arg2) {
			unhidePlacesOnCategoryClick(arg2);
		}
	}
	private void completelyRemovePlace(Place place) {
		mapAndPlaces.getChildren().remove(place);
		hiddenPlaces.get(place.getCategory()).remove(place);
		placesByCat.get(place.getCategory()).remove(place);
		placesByPosition.remove(place.getPosition());
		LinkedList<Place>list = placesByName.get(place.getName());
		list.remove(place);
		if(placesByPosition.values().size()==0) {
			saved = true;
		}else {saved = false;}
	}
	private void addPlaceToDefaultCollections(Place place) {
		if(placesByCat.get(place.getCategory())==null) {
			LinkedList<Place> list = new LinkedList<Place>();
			list.add(place);
			placesByCat.put(place.getCategory(), list);
		}else {
			LinkedList<Place> list = placesByCat.get(place.getCategory());
			list.add(place);
		}
		if(placesByName.get(place.getName())==null) {
			LinkedList<Place> list = new LinkedList<Place>();
			list.add(place);
			placesByName.put(place.getName(), list);
		}else {
			LinkedList<Place> list = placesByName.get(place.getName());
			list.add(place);
		}
		placesByPosition.put(place.getPosition(),place);
		saved = false;
	}
	private void lockInRadioChoice(VBox radio) {
		if(((RadioButton) radio.getChildren().get(0)).isSelected()) {
			radioChoice = 0;
			fetchCategory();
		}else if(((RadioButton) radio.getChildren().get(1)).isSelected()) {
			radioChoice = 1;
			fetchCategory();
		}
	}
	private void createErrorStage() {
		Alert errAlert = new Alert(AlertType.ERROR);
		errAlert.setTitle("ERROR");
		errAlert.setHeaderText(null);
		errAlert.setContentText("Unacceptable input!");
		errAlert.showAndWait();
	}
	private void createNameError() {
		Alert errAlert = new Alert(AlertType.ERROR);
		errAlert.setTitle("ERROR");
		errAlert.setHeaderText(null);
		errAlert.setContentText("No place with that name exists!");
		errAlert.showAndWait();
	}
	private void createPosError() {
		Alert errAlert = new Alert(AlertType.ERROR);
		errAlert.setTitle("ERROR");
		errAlert.setHeaderText(null);
		errAlert.setContentText("Position already taken!");
		errAlert.showAndWait();
	}
	private void alertLackOfPlaceAtPos() {
		Alert errAlert = new Alert(AlertType.ERROR);
		errAlert.setTitle("ERROR");
		errAlert.setHeaderText(null);
		errAlert.setContentText("There is no 'Place' at these coordinates!");
		errAlert.showAndWait();
	}
	private boolean createDataLossPreventionDialog() {
		Alert errAlert = new Alert(AlertType.WARNING);
		errAlert.setTitle("Wait!");
		errAlert.setHeaderText(null);
		errAlert.setContentText("Do you really wish to continue without saving first?");
		errAlert.getButtonTypes().clear();
		errAlert.getButtonTypes().add(ButtonType.YES);
		errAlert.getButtonTypes().add(ButtonType.NO);
		Optional<ButtonType> result = errAlert.showAndWait();
		if(result.isPresent() && result.get() == ButtonType.YES) {
			return true;
		}else {
			return false;
		}
	}
	private boolean createDataLossPreventionDialog2() {
		Alert errAlert = new Alert(AlertType.WARNING);
		errAlert.setTitle("Wait!");
		errAlert.setHeaderText(null);
		errAlert.setContentText("Do you really wish to quit without saving first?");
		errAlert.getButtonTypes().clear();
		errAlert.getButtonTypes().add(ButtonType.YES);
		errAlert.getButtonTypes().add(ButtonType.NO);
		Optional<ButtonType> result = errAlert.showAndWait();
		if(result.isPresent() && result.get() == ButtonType.YES) {
			return true;
		}else {
			return false;
		}
	}
	private void openNewNamedDialog() {
		try {
			NamedDialog dialog = new NamedDialog();
			Optional<ButtonType> result = dialog.showAndWait();
			if(result.isPresent() && result.get() == ButtonType.OK) {
				String name = dialog.getName();
				Place newPlace = new NamedPlace(name,categoryChoice,x,y);
				mapAndPlaces.getChildren().add(newPlace);
				newPlace.setOnMouseClicked(e -> eventOnMouseClick(newPlace,e));
				addPlaceToDefaultCollections(newPlace);
				if(name.length()==0) {
					throw new Exception();
				}
			}
		}catch(Exception e) {
			createErrorStage();
		}
	}
	private void openNewDescribedDialog() {
		try {
			DescribedDialog dialog = new DescribedDialog();
			Optional<ButtonType> result = dialog.showAndWait();
			if(result.isPresent() && result.get() == ButtonType.OK) {
				String name = dialog.getName();
				String description = dialog.getDescription();
				Place newPlace = new DescribedPlace(name,categoryChoice,x,y,description);
				mapAndPlaces.getChildren().add(newPlace);
				newPlace.setOnMouseClicked(e -> eventOnMouseClick(newPlace,e));
				addPlaceToDefaultCollections(newPlace);
				if(name.length()==0 || description.length()==0) {
					throw new Exception();
				}
			}
		}catch(Exception e) {
			createErrorStage();
		}
	}
	private void fetchCategory() {
		ObservableList<Integer> list = rightListPane.getSelectionModel().getSelectedIndices();
		if(list.size()==0) {
			categoryChoice = Category.None;
		}else if(list.get(0)==0) {
			categoryChoice = Category.Bus;
		}else if(list.get(0)==1) {
			categoryChoice = Category.Underground;
		}else if(list.get(0)==2) {
			categoryChoice = Category.Train;
		}
		rightListPane.getSelectionModel().select(-1);
	}
	private void changeCursorForMap() {
		img.setCursor(Cursor.CROSSHAIR);
		cursorIsNormal = false;
	}
	private void eventOnCrosshairClick(Event event) {
		if(((MouseEvent) event).getButton() == MouseButton.PRIMARY && !cursorIsNormal) {
			x = (int)((MouseEvent) event).getX();
			y = (int)((MouseEvent) event).getY();
			cursorIsNormal = true;
			img.setCursor(Cursor.DEFAULT);
			Position pos = new Position(x,y);
			try {
				if(placesByPosition.get(pos)!=null) {
					throw new Exception();
				}
			}catch(Exception e) {
				createPosError();
				return;
			}
			if(radioChoice == 0){
				openNewNamedDialog();
			}else if(radioChoice == 1) {
				openNewDescribedDialog();
			}
		}
	}
	private void askForInputCoords() {
		try {
			InputCoordinates dialog = new InputCoordinates();
			Optional<ButtonType> result = dialog.showAndWait();
			if(result.isPresent() && result.get() == ButtonType.OK) {
				int x = dialog.getXCoords();
				int y = dialog.getYCoords();
				Position pos = new Position(x,y);
				Place place = placesByPosition.get(pos);
				if(place==null) {
					alertLackOfPlaceAtPos();
					return;
				}
				for(Place p : selectedPlaces) {
					p.changeSelectionStatus(false);
				}
				selectedPlaces.clear();
				place.changeSelectionStatus(true);
				selectedPlaces.add(place);
				if(hiddenPlaces.get(place.getCategory()).contains(place)) {
					hiddenPlaces.get(place.getCategory()).remove(place);
					place.setVisible(true);
				}
			}
		}catch(Exception e) {
			createErrorStage();
		}
	}
	private void eventOnMouseClick(Place place,Event event) {
		if (((MouseEvent) event).getButton() == MouseButton.SECONDARY && cursorIsNormal) {
			Alert info = new Alert(AlertType.INFORMATION);
			info.setTitle("Info");
			if(place instanceof NamedPlace) {
				info.setContentText("Name: "+place.getName()+" ["+place.getPosition().getX()+", "+place.getPosition().getY()+"]");
			}else if(place instanceof DescribedPlace) {
				info.setContentText("Name: "+place.getName()+" ["+place.getPosition().getX()+", "+place.getPosition().getY()+"]\nDescription: "+((DescribedPlace) place).getDescription());
			}
			info.setHeaderText(null);
			info.showAndWait();
		}else if(((MouseEvent) event).getButton() == MouseButton.PRIMARY && cursorIsNormal) {
			place.changeSelectionStatus(!place.isSelected());
			if(place.isSelected()) {
				selectedPlaces.add(place);
			}else if(!place.isSelected()) {
				selectedPlaces.remove(place);
			}
		}
	}
	private void deselectAllSelectedPlaces() {
		for(Place place : selectedPlaces) {
			place.changeSelectionStatus(false);
		}
		selectedPlaces.clear();
	}
	private void hideEntireCategory() {
		fetchCategory();
		Category cat = categoryChoice;
		if(!selectedPlaces.isEmpty()) {
			deselectAllSelectedPlaces();
		}
		if(cat!=Category.None) {
			selectedPlaces.addAll(placesByCat.get(cat));
			hideMarkedPlaces();
		}
	}
	private void unhidePlacesOnCategoryClick(Category cat) {
		try {
			for(Place place : hiddenPlaces.get(cat)) {
				place.setVisible(true);
			}
			hiddenPlaces.get(cat).clear();
		}catch(NullPointerException e) {
			
		}	
	}
	private void hideMarkedPlaces() {
		for(Place place : selectedPlaces) {
			place.setVisible(false);
			place.changeSelectionStatus(false);
			if(hiddenPlaces.get(place.getCategory())==null) {
				LinkedList<Place> list = new LinkedList<Place>();
				list.add(place);
				hiddenPlaces.put(place.getCategory(), list);
			}else {
				LinkedList<Place> list = hiddenPlaces.get(place.getCategory());
				list.add(place);
			}
		}
		selectedPlaces.clear();
	}
	private void removeMarkedPlaces() {
		for(Place place : selectedPlaces) {
			completelyRemovePlace(place);
		}
		selectedPlaces.clear();
	}
	private void setRightPane() {
		rightListPane.setMaxHeight(100);
		rightListPane.setMaxWidth(100);
		VBox rightSide = new VBox();
		rightSide.setPadding(new Insets(100,0,0,0));
		rightSide.setAlignment(Pos.CENTER);
		Label categories = new Label("Categories");
		Button hideCategory = new Button("Hide Category");
		hideCategory.setOnAction(e -> hideEntireCategory());
		rightSide.getChildren().addAll(categories,rightListPane,hideCategory);
		rightListPane.getItems().add(Category.Bus);
		rightListPane.getItems().add(Category.Underground);
		rightListPane.getItems().add(Category.Train);
		rightListPane.getSelectionModel().selectedItemProperty().addListener((ChangeListener<? super Category>) new ClickListHandler());
		bp.setRight(rightSide);
	}
	private void setTopPane() {
		HBox topPane = new HBox();
		topPane.setPadding(new Insets(10,0,0,0));
		topPane.setSpacing(10);
		topPane.setAlignment(Pos.BASELINE_CENTER);
		Button newButton = new Button("New");
		RadioButton named = new RadioButton("Named");
		RadioButton described = new RadioButton("Described");
		ToggleGroup toggleRadio = new ToggleGroup();
		named.setToggleGroup(toggleRadio);
		described.setToggleGroup(toggleRadio);
		named.setSelected(true);
		VBox radioButtons = new VBox(named,described);
		TextField searchField = new TextField("Search");
		Button searchButton = new Button("Search");
		Button hideButton = new Button("Hide");
		Button removeButton = new Button("Remove");
		Button coordinatesButton = new Button("Coordinates");
		topPane.getChildren().addAll(newButton,radioButtons,searchField,searchButton,hideButton,removeButton,coordinatesButton);
		newButton.setOnAction(e -> {lockInRadioChoice(radioButtons); changeCursorForMap();});
		hideButton.setOnAction(e -> hideMarkedPlaces());
		removeButton.setOnAction(e -> removeMarkedPlaces());
		coordinatesButton.setOnAction(e -> askForInputCoords());
		searchButton.setOnAction(e -> {findPlacesWithName(searchField.getText());});
		bp.setTop(topPane);
	}
	private void createMenu() {
		Menu fileMenu = new Menu("File");
		MenuItem loadMap = new MenuItem("Load Map");
		MenuItem loadPlaces = new MenuItem("Load Places");
		MenuItem save = new MenuItem("Save");
		MenuItem exit = new MenuItem("Exit");
		fileMenu.getItems().addAll(loadMap,loadPlaces,save,exit);
		exit.setOnAction(e -> {if(saved) {
			exit();}else{
				boolean programCanClose = preventLossOfData(1);
				if(programCanClose) {exit();}}});
		loadMap.setOnAction(e -> {if(saved) {
			loadMap();}else {
				boolean newMapCanLoad = preventLossOfData(0);
				if(newMapCanLoad) {loadMap();}}});
		save.setOnAction(e -> savePlaces());
		loadPlaces.setOnAction(e -> {if(saved) {
			openPlaces();}else{
				boolean newPlacesCanLoad = preventLossOfData(0);
				if(newPlacesCanLoad) {openPlaces();}}});
		MenuBar menubar = new MenuBar(fileMenu);
		menuBox.getChildren().add(menubar);
	}
	private void exit() {
		theStage.close();
	}
	private boolean preventLossOfData(int i) {
		if(i==0) {
			boolean ignoreSave = createDataLossPreventionDialog();
			return ignoreSave;
		}else if(i==1) {
			boolean ignoreSave = createDataLossPreventionDialog2();
			return ignoreSave;
		}
		return false;
	}
	private void loadMap() {
		File file = fileChooser.showOpenDialog(null);
		if (file!=null) {
			mapAndPlaces.getChildren().removeAll(placesByPosition.values());
			clearAllPlaces();
			openMap(file);
		}
	}
	private void openMap(File file) {
		Image image = new Image("file:"+file.getAbsolutePath());
		img.setPreserveRatio(true);
		img.setImage(image);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		setRightPane();
		setTopPane();
		createMenu();
		//createCategories();
		mapAndPlaces.getChildren().add(img);
		img.setOnMouseClicked(e -> eventOnCrosshairClick(e));
		theStage = primaryStage;
		bp.setLeft(mapAndPlaces);
		Scene s = new Scene(sceneNodes,800,500);
		primaryStage.setScene(s);
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> {if(saved) {
			exit();}else{
				boolean programCanClose = preventLossOfData(1);if(programCanClose) {}else{e.consume();}}});
	}
}