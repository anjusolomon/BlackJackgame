package application;
	
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;




public class Blackjack extends Application {
    private final Deck deck = new Deck(1);  
    private final Hand hand = new Hand();  
    private final Hand dealer = new Hand();  
    
    private boolean busted; 
    private boolean playerTurn; 
    
    FlowPane cards = new FlowPane(Orientation.HORIZONTAL);				//direction of player cards - not changed
    FlowPane dealerCards = new FlowPane(Orientation.HORIZONTAL);		//direction of dealer cards - not changed
    Label totalLabel = new Label();
    Label totalLabelDealer = new Label();
    
    Label dealerLbl = new Label("Dealer Hand"); 
    Label playerLbl = new Label("Your Hand"); 
    
    Label status = new Label();
    Image imageback = new Image("file:resources/newtable4.jpg");			//background picture updated
    
    public void drawCard(Hand hand, FlowPane pane, Label l){
        try{
            Card card = deck.dealCard(); 
            ImageView img = new ImageView(card.getCardImage()); 
            pane.getChildren().add(img); 
            hand.addCard(card); 
            
            int handTotal = hand.evaluateHand(); 
            
            StringBuilder total = new StringBuilder(); 
            if(hand.getSoft() > 0){
                total.append(handTotal-10).append(" or ");					// ("/") changed from slash to OR eg ace is 1 or 11
            }
            total.append(handTotal); 
            l.setText(total.toString()); 
        } catch (Exception ex){
            System.out.println(ex.getMessage()); 
        }
    }
    
    public void newDeck(){
        deck.restoreDeck(); 
        deck.shuffle(); 
        System.out.println("We have shuffled the deck"); 
    }
    
    public void newHand(){
        
    	// check if we should shuffle 
    	
        if(deck.getNumberOfCardsRemaining() <= deck.getSizeOfDeck()*0.2){
            newDeck(); 
        }
        
        
        // clear everything 
        
        hand.discardHand(); 
        dealer.discardHand(); 
        cards.getChildren().removeAll(cards.getChildren());  
        dealerCards.getChildren().removeAll(dealerCards.getChildren()); 
        totalLabel.setText(""); 
        totalLabelDealer.setText(""); 
        
        busted = false; 
        playerTurn = true; 
        
        
        // draw cards for the initial hands, player gets 2, dealer gets 1 
        
        drawCard(hand, cards, totalLabel); 
        drawCard(dealer, dealerCards, totalLabelDealer); 
        drawCard(hand, cards, totalLabel); 
        
        status.setText("Your turn"); 
    }
    
    @Override
    public void start(Stage primaryStage) {
        
    	// Update all text colors and fonts
    	
        totalLabel.setFont(new Font("Arial", 40));			// arial size from 24 to 40
        totalLabel.setTextFill(Color.web("#800000"));		// colour of font from white FFF to dark red
        
        
        													//  https://www.rapidtables.com/web/color/RGB_Color.html
        													//  near middle of website Under RGB color table
        
        totalLabelDealer.setFont(new Font("Arial", 40));	// arial size from 24 to 40
        totalLabelDealer.setTextFill(Color.web("#800000")); // colour of font from white FFF to dark red
        
        status.setTextFill(Color.web("#000000")); 			// YOUR TURN (with 2 cards) change white FFF to dark red
        status.setFont(new Font("Arial", 40)); 				// arial size from 24 to 40
        
        dealerLbl.setTextFill(Color.web("#800000")); 		// DEALER HAND (with 1 then 2 cards) change white FFF to dark red	
        dealerLbl.setFont(new Font("Arial", 40)); 			// arial size from 24 to 40
        
        playerLbl.setTextFill(Color.web("#800000")); 		// YOUR HAND (with 1 then 2 cards) change white FFF to dark red
        playerLbl.setFont(new Font("Arial", 40));			// arial size from 24 to 40
        
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(imageback, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        
        Button drawbtn = new Button();
        drawbtn.setText("HIT");
        drawbtn.setStyle("-fx-font: 18 arial; -fx-background-color: 	#FFA07A;");  	// ADDED BUTTON SIZE AND FONT HERE
        																			// DIDNT USE - drawbtn.setStyle("-fx-font-weight: bold;");
        																			// DIDNT USE - drawbtn.setFont(Font.font("arial", FontWeight.BOLD, 70));
        drawbtn.setOnAction((e) -> {
            
        	
        	if(playerTurn == true && busted != true){
                drawCard(hand, cards, totalLabel); 

                if(hand.evaluateHand() > 21){
                    
                	
                	// Updated from You've busted to below
                	
                	// you busted 
                	
                    System.out.println("You have busted"); 
                    busted = true; 
                    playerTurn = false; 
                    status.setText("You have busted"); 
                }
            }
        });
        
        Button standbtn = new Button();
        standbtn.setText("STAND");
        standbtn.setStyle("-fx-font: 18 arial; -fx-background-color: 	#FFA07A;");  	// ADDED BUTTON SIZE AND FONT HERE
        standbtn.setOnAction((e) -> {
            
        	if(playerTurn == true && busted != true){
                playerTurn = false; 
                while(dealer.evaluateHand() < 17){
                    drawCard(dealer, dealerCards, totalLabelDealer);
                } 
                
                int playerTotal = hand.evaluateHand(); 
                int dealerTotal = dealer.evaluateHand(); 
                
                System.out.println("Player Hand: "+hand); 
                System.out.println("Dealer Hand: "+dealer); 

                if(dealerTotal <= 21 && playerTotal == dealerTotal){
                    // tie, push 
                    System.out.println("You've pushed");
                    status.setText("You've pushed"); 
                } else if(dealerTotal <= 21 && playerTotal <= dealerTotal){
                    // you lost 
                    System.out.println("You've lost");     
                    status.setText("You've lost"); 
                } else {
                    // you won 
                    System.out.println("You've won!! :)");      // add smile face
                    status.setText("You've won!! :)");			// add smile face
                    
                    // TRY TO ADD FIREWORKS TO THIS 
                    //Image image = new Image("fireworks.html");
                    //Image image = Toolkit.getDefaultToolkit().getImage("fireworks.html");
                    //Image image=ImageIO.read(new File("fireworks.html"));
                                                  	                                     
                    
                    
                }
            }
        });
        
        Button newbtn = new Button();
        newbtn.setText("NEW HAND");
        newbtn.setStyle("-fx-font: 18 arial; -fx-background-color: 		#FF6347;");  	// ADDED BUTTON SIZE AND FONT HERE
        newbtn.setOnAction((e) -> {
            newHand(); 
        });
        
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(71.5, 12.5, 13.5, 14.5));		//11.5, 12.5, 13.5, 14.5 - chg 1st one brought top down eg dlr section (11.5 to 71.5)
        grid.setHgap(5.5);											// assume Horizontal gap 
        grid.setVgap(5.5); 											// assume Vertical gap
        
        grid.add(dealerCards, 0, 0, 3, 1); 
        grid.add(dealerLbl, 0, 1);
        grid.add(totalLabelDealer, 2, 1, 2, 1); 	// 1,1,2,1 changed total # for dealer from 1 to 2  (2,1,2,1)
        
        // padding
        
        Pane p = new Pane(); 
        p.setPrefSize(0, 30); 
        grid.add(p, 0, 2); 
        
        grid.add(cards, 0, 3, 3, 1); 				// 0,3,3,1
        grid.add(playerLbl, 0, 4);					// 0,4
        grid.add(totalLabel, 2, 4, 2, 1);  			// 1,4,2,1  total # for player
        grid.add(drawbtn, 0,5);						//HIT' BUTTON
        grid.add(standbtn, 2,5);					//STAND' BUTTON change from 1,5 to 10,5 -- chg position of cards/words right to left / chg'd back
        grid.add(newbtn, 3,5); 						//NEW HAND' BUTTON change from 2,5 to 15,5 -- chg position of cards/words right to left / chg'd back
        grid.add(status, 0, 6, 3, 1);				// 0,6,3,1
        grid.setBackground(background);
        
        Scene scene = new Scene(grid, 2000, 1500, Color.BLUEVIOLET);   // 1600, 900 old one, added colour here (top & bottom only)
        
        primaryStage.setTitle("BlackJack");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        newDeck(); 
        newHand(); 
    }
    
    public static void main(String[] args) {
        launch(args);

    }
}

