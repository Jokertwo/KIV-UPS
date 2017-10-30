import java.util.logging.Logger;


public class Parser {

    private static final Logger LOG = Logger.getLogger(Parser.class.getName());


    public void parseMessage(String message){       
        int i = Character.getNumericValue(message.charAt(0));
        
        if(i < 0){
            LOG.warning("Can't parse message : '" + message +"'");
            return;
        }
        switch(i){
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
        }
        
    }

}
