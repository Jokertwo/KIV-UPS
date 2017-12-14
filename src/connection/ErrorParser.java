package connection;

import java.util.logging.Logger;
import constants.Constants;

public class ErrorParser {
    String reslut;
    
    public ErrorParser(String error,Logger log) {
        parseError(error,log);
    }
    
    private void parseError(String error,Logger log){
        if (error.charAt(1) == Constants.ERROR_UNABLE_SEND_PRIVATE){
            reslut = "User is now offline. Your message was not send.";
            log.info("User offline and can't send message to him");
        }
        if(error.charAt(1) == Constants.ERROR_UNABLE_SEND_PUBLIC){
            reslut = "Sorry but I cant send your public message.";
            log.info("Server cant send your public message");
        }
        if (error.charAt(1) == Constants.ERROR_MAX_USERS) {
            reslut = "Max capacity of server has been reached.";
            log.info("Cant connect to server. Maximum capacity of server has been reached.");
        }
        if (error.charAt(1) == Constants.ERROR_USER_EXIST) {
            reslut = "The name is already in use...";
            log.info("Cant connect to server because the name/nick is already is use");
        }
        if (error.equals(Constants.ERROR)){
            reslut = "{MESSAGE WASN'T SEND, TRY TO AGAIN LATER}";
            log.warning("Default error!");
        }
        else{
            log.warning("Cant parse response from server : " + error);   
        }
    }
    
    
    @Override
    public String toString() {
        return reslut;
    }

}
