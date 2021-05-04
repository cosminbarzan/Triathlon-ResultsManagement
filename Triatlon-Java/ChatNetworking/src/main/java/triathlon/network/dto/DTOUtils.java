package triathlon.network.dto;


import triathlon.model.User;
import triathlon.model.Participant;
import triathlon.model.Result;
import triathlon.model.Stage;


public class DTOUtils {
    public static ResultDTO getDTO(Result result){
        Integer participantId = result.getParticipant().getId();
        Integer stageId = result.getStage().getId();
        Double score = result.getScore();

        return new ResultDTO(participantId, stageId, score);
    }

    public static Result getFromDTO(ResultDTO rdto){
        Participant participant = new Participant();
        participant.setId(rdto.getParticipantId());

        Stage stage = new Stage();
        stage.setId(rdto.getStageId());

        Double score = rdto.getScore();

        return new Result(participant, stage, score);
    }

    public static User getFromDTO(UserDTO usdto){
        String firstName = usdto.getFirstName();
        String lastName = usdto.getLastName();
        String username = usdto.getUsername();
        String password = usdto.getPassword();
        Integer idStage = usdto.getIdStage();

        Stage stage = new Stage();
        stage.setId(idStage);

        User user = new User(firstName, lastName, username, password, stage);
        user.setId(usdto.getId());

        return user;
    }
    public static UserDTO getDTO(User user){
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String username = user.getUsername();
        String password = user.getPassword();
        Integer idStage = user.getStage().getId();

        UserDTO userDTO = new UserDTO(user.getId(), firstName, lastName, username, password, idStage);

        return userDTO;
    }

//    public static MessageDTO getDTO(Message message){
//        String senderId=message.getSender().getId();
//        String receiverId=message.getReceiver().getId();
//        String txt=message.getText();
//        return new MessageDTO(senderId, txt, receiverId);
//    }
//
//    public static UserDTO[] getDTO(User[] users){
//        UserDTO[] frDTO=new UserDTO[users.length];
//        for(int i=0;i<users.length;i++)
//            frDTO[i]=getDTO(users[i]);
//        return frDTO;
//    }
//
//    public static User[] getFromDTO(UserDTO[] users){
//        User[] friends=new User[users.length];
//        for(int i=0;i<users.length;i++){
//            friends[i]=getFromDTO(users[i]);
//        }
//        return friends;
//    }
}
