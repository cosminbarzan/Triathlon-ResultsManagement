package triathlon.network.protobuffprotocol;


import triathlon.model.*;
import triathlon.network.dto.ResultDTO;
import triathlon.network.dto.UserDTO;

public class ProtoUtils {
    public static TriathlonProtobufs.Request createLoginRequest(UserDTO user){
        TriathlonProtobufs.UserDTO userDTO = TriathlonProtobufs.UserDTO.newBuilder()
                .setId(user.getId())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .setIdStage(user.getIdStage()).build();

        TriathlonProtobufs.Request request = TriathlonProtobufs.Request.newBuilder()
                .setType(TriathlonProtobufs.Request.RequestType.LOGIN)
                .setUser(userDTO).build();

        return request;
    }

    public static TriathlonProtobufs.Request createLogoutRequest(UserDTO user){
        TriathlonProtobufs.UserDTO userDTO = TriathlonProtobufs.UserDTO.newBuilder()
                .setId(user.getId()).build();

        TriathlonProtobufs.Request request = TriathlonProtobufs.Request.newBuilder()
                .setType(TriathlonProtobufs.Request.RequestType.LOGOUT)
                .setUser(userDTO).build();

        return request;
    }

    public static TriathlonProtobufs.Request createAddResultRequest(ResultDTO result){
        TriathlonProtobufs.ResultDTO resultDTO = TriathlonProtobufs.ResultDTO.newBuilder()
                .setParticipantId(result.getParticipantId())
                .setStageId(result.getStageId())
                .setScore(result.getScore()).build();

        TriathlonProtobufs.Request request = TriathlonProtobufs.Request.newBuilder()
                .setType(TriathlonProtobufs.Request.RequestType.ADD_RESULT)
                .setResult(resultDTO).build();

        return request;
    }

    public static TriathlonProtobufs.Request createGetParticipantsByScoreRequest(Integer idStage){
        TriathlonProtobufs.Request request = TriathlonProtobufs.Request.newBuilder()
                .setType(TriathlonProtobufs.Request.RequestType.GET_PARTICIPANTS_BY_SCORE)
                .setIdStage(idStage).build();

        return request;
    }

    public static TriathlonProtobufs.Request createGetParticipantsAndScoreRequest(){
        TriathlonProtobufs.Request request = TriathlonProtobufs.Request.newBuilder()
                .setType(TriathlonProtobufs.Request.RequestType.GET_PARTICIPANTS_AND_SCORE).build();

        return request;
    }

    public static TriathlonProtobufs.Response createOkResponse(){
        TriathlonProtobufs.Response response = TriathlonProtobufs.Response.newBuilder()
                .setType(TriathlonProtobufs.Response.ResponseType.OK).build();

        return response;
    }

    public static TriathlonProtobufs.Response createErrorResponse(String text){
        TriathlonProtobufs.Response response = TriathlonProtobufs.Response.newBuilder()
                .setType(TriathlonProtobufs.Response.ResponseType.ERROR)
                .setError(text).build();

        return response;
    }

    public static TriathlonProtobufs.Response createNewResultResponse(ResultDTO result){
        TriathlonProtobufs.ResultDTO resultDTO = TriathlonProtobufs.ResultDTO.newBuilder()
                .setParticipantId(result.getParticipantId())
                .setStageId(result.getStageId())
                .setScore(result.getScore()).build();

        TriathlonProtobufs.Response response = TriathlonProtobufs.Response.newBuilder()
                .setType(TriathlonProtobufs.Response.ResponseType.NEW_RESULT)
                .setResult(resultDTO).build();

        return response;
    }

    public static TriathlonProtobufs.Response createGetParticipantsByScoreResponse(Participant[] participants){
        TriathlonProtobufs.Response.Builder response = TriathlonProtobufs.Response.newBuilder()
                .setType(TriathlonProtobufs.Response.ResponseType.GET_PARTICIPANTS_BY_SCORE);

        for (Participant participant : participants){
            TriathlonProtobufs.Participant participantP = TriathlonProtobufs.Participant.newBuilder()
                    .setFirstName(participant.getFirstName())
                    .setLastName(participant.getLastName()).build();

            response.addParticipants(participantP);
        }

        return response.build();
    }

    public static TriathlonProtobufs.Response createGetParticipantsAndScoreResponse(ParticipantDTO[] participants){
        TriathlonProtobufs.Response.Builder response = TriathlonProtobufs.Response.newBuilder()
                .setType(TriathlonProtobufs.Response.ResponseType.GET_PARTICIPANTS_AND_SCORE);

        for (ParticipantDTO participantDTO : participants){
            TriathlonProtobufs.ParticipantDTO participantDTOP = TriathlonProtobufs.ParticipantDTO.newBuilder()
                    .setId(participantDTO.getId())
                    .setFirstName(participantDTO.getFirstName())
                    .setLastName(participantDTO.getLastName())
                    .setScore(participantDTO.getScore()).build();

            response.addParticipantsDTO(participantDTOP);
        }

        return response.build();
    }

    public static String getError(TriathlonProtobufs.Response response){
        String errorMessage = response.getError();
        return errorMessage;
    }

    public static Result getResult(TriathlonProtobufs.Response response){
        Participant participant = new Participant();
        participant.setId(Math.toIntExact(response.getResult().getParticipantId()));

        Stage stage = new Stage();
        stage.setId(Math.toIntExact(response.getResult().getStageId()));

        Double score = response.getResult().getScore();;

        Result result = new Result(participant, stage, score);
        return result;
    }

    public static Participant[] getParticipantsByScore(TriathlonProtobufs.Response response){
        Participant[] participants = new Participant[response.getParticipantsCount()];
        for (int i = 0; i < response.getParticipantsCount(); i++) {
            TriathlonProtobufs.Participant participantP = response.getParticipants(i);

            Participant participant = new Participant();
            participant.setFirstName(participantP.getFirstName());
            participant.setLastName(participantP.getLastName());

            participants[i] = participant;
        }

        return participants;
    }

    public static ParticipantDTO[] getParticipantsAndScore(TriathlonProtobufs.Response response){
        ParticipantDTO[] participants = new ParticipantDTO[response.getParticipantsDTOCount()];
        for (int i = 0; i < response.getParticipantsDTOCount(); i++) {
            TriathlonProtobufs.ParticipantDTO participantDTOP = response.getParticipantsDTO(i);

            ParticipantDTO participantDTO = new ParticipantDTO(Math.toIntExact(participantDTOP.getId()), participantDTOP.getFirstName(), participantDTOP.getLastName(), participantDTOP.getScore());

            participants[i] = participantDTO;
        }

        return participants;
    }

    public static User getUser(TriathlonProtobufs.Request request){
        Stage stage = new Stage();
        stage.setId(Math.toIntExact(request.getUser().getIdStage()));

        User user = new User(request.getUser().getFirstName(), request.getUser().getLastName(), request.getUser().getUsername(), request.getUser().getPassword(), stage);

        user.setId(Math.toIntExact(request.getUser().getId()));

        return user;
    }

    public static Result getResult(TriathlonProtobufs.Request request){
        Participant participant = new Participant();
        participant.setId(Math.toIntExact(request.getResult().getParticipantId()));

        Stage stage = new Stage();
        stage.setId(Math.toIntExact(request.getResult().getStageId()));

        Result result = new Result(participant, stage, request.getResult().getScore());

        return result;
    }

    public static Integer getIdStage(TriathlonProtobufs.Request request){
        return Math.toIntExact(request.getIdStage());
    }
}

